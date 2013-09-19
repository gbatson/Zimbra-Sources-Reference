/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.formatter;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.FileBufferedWriter;
import com.zimbra.common.util.HttpUtil;
import com.zimbra.common.util.HttpUtil.Browser;
import com.zimbra.common.mime.MimeConstants;
import com.zimbra.cs.index.MailboxIndex;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.CalendarItem.Instance;
import com.zimbra.cs.mailbox.calendar.IcsImportParseHandler;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.calendar.IcsImportParseHandler.ImportInviteVisitor;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ZCalendarBuilder;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ZICalendarParseHandler;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ZVCalendar;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.service.UserServletContext;
import com.zimbra.cs.service.UserServletException;
import com.zimbra.cs.service.formatter.FormatterFactory.FormatType;

import javax.mail.Part;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class IcsFormatter extends Formatter {

    @Override
    public FormatType getType() {
        return FormatType.ICS;
    }

    public String[] getDefaultMimeTypes() {
        return new String[] { MimeConstants.CT_TEXT_CALENDAR, "text/x-vcalendar" };
    }

    public String getDefaultSearchTypes() {
        return MailboxIndex.SEARCH_FOR_APPOINTMENTS;
    }

    public void formatCallback(UserServletContext context) throws IOException, ServiceException {
        Iterator<? extends MailItem> iterator = null;
        List<CalendarItem> calItems = new ArrayList<CalendarItem>();
        //ZimbraLog.mailbox.info("start = "+new Date(context.getStartTime()));
        //ZimbraLog.mailbox.info("end = "+new Date(context.getEndTime()));
        try {
        	long start = context.getStartTime();
        	long end = context.getEndTime();
            boolean hasTimeRange = start != TIME_UNSPECIFIED && end != TIME_UNSPECIFIED;
            iterator = getMailItems(context, start, end, Integer.MAX_VALUE);

            // this is lame
            while (iterator.hasNext()) {
                MailItem item = iterator.next();
                if (item instanceof CalendarItem) {
                	CalendarItem calItem = (CalendarItem) item;
                	if (hasTimeRange) {
                    	Collection<Instance> instances = calItem.expandInstances(start, end, false);
                    	if (!instances.isEmpty())
                    		calItems.add(calItem);
                	} else {
                	    calItems.add(calItem);
                	}
                }
            }
        } finally {
            if (iterator instanceof QueryResultIterator)
                ((QueryResultIterator) iterator).finished();
        }

        // todo: get from folder name
        String filename = context.itemPath;

        if (mayAttach(context)) {
            if (filename == null || filename.length() == 0)
                filename = "contacts";
			
            String requestFilename = context.req.getParameter("filename"); // Let the client specify the filename to save as
            if (requestFilename != null)
                filename = requestFilename;
            else
                filename = filename.replaceAll("^\\W",""); // Trim off leading non-word characters (e.g. forward slash)

            String cd = Part.ATTACHMENT + "; filename=" + HttpUtil.encodeFilename(context.req, filename + ".ics");
            context.resp.addHeader("Content-Disposition", cd);
        }
        Browser browser = HttpUtil.guessBrowser(context.req);
        boolean useOutlookCompatMode = Browser.IE.equals(browser);
        boolean needAppleICalHacks = Browser.APPLE_ICAL.equals(browser);  // bug 15549
        boolean htmlFormat = !mayAttach(context) && Browser.IE.equals(browser); // Use only htmlFormat when the file isn't supposed to be downloaded (ie. it's supposed to be shown in the browser). Mangles the code so it can be displayed correctly, especially by IE

        context.resp.setCharacterEncoding(MimeConstants.P_CHARSET_UTF8);
        context.resp.setContentType(htmlFormat ? MimeConstants.CT_TEXT_HTML : getContentType(context, MimeConstants.CT_TEXT_CALENDAR));
        
        OperationContext octxt = new OperationContext(context.getAuthAccount(), context.isUsingAdminPrivileges());
        FileBufferedWriter fileBufferedWriter = new FileBufferedWriter(
                context.resp.getWriter(),
                LC.calendar_ics_export_buffer_size.intValueWithinRange(0, FileBufferedWriter.MAX_BUFFER_SIZE));
        try {
            if (htmlFormat)
                fileBufferedWriter.write("<html><body><pre>");
            context.targetMailbox.writeICalendarForCalendarItems(
                    fileBufferedWriter, octxt, calItems, (context.target != null && context.target instanceof Folder) ? (Folder)context.target : null,
                    useOutlookCompatMode, true, needAppleICalHacks, true, htmlFormat);
            if (htmlFormat)
                fileBufferedWriter.write("</pre></body></html>");
        } finally {
            fileBufferedWriter.finish();
        }
    }

    public boolean supportsSave() {
        return true;
    }

    public void saveCallback(UserServletContext context, String contentType, Folder folder, String filename)
    throws UserServletException, ServiceException, IOException, ServletException {
        boolean continueOnError = context.ignoreAndContinueOnError();
        boolean preserveExistingAlarms = context.preserveAlarms();
        InputStream is = context.getRequestInputStream(-1);
        String charset = MimeConstants.P_CHARSET_UTF8;
        String ctStr = context.req.getContentType();
        if (ctStr != null) {
            String cs = Mime.getCharset(ctStr);
            if (cs != null)
                charset = cs;
        }

        try {
            if (context.req.getContentLength() <= LC.calendar_ics_import_full_parse_max_size.intValue()) {
                // Build a list of ZVCalendar objects by fully parsing the ics file, then iterate them
                // and add them one by one.  Memory hungry if there are very many events/tasks, but it allows
                // TZID reference before VTIMEZONE of that timezone appears in the ics file.
                List<ZVCalendar> icals = ZCalendarBuilder.buildMulti(is, charset);
                ImportInviteVisitor visitor = new ImportInviteVisitor(context.opContext, folder, preserveExistingAlarms);
                Invite.createFromCalendar(context.targetAccount, null, icals, true, continueOnError, visitor);
            } else {
                // Events/tasks are added in callbacks during parse.  This is more memory efficient than the
                // other method, but it doesn't allow forward referencing TZIDs.  ics files generated by
                // clients that put VTIMEZONEs at the end will not parse.  Evolution client does this.
                ZICalendarParseHandler handler =
                    new IcsImportParseHandler(context.opContext, context.targetAccount, folder,
                                              continueOnError, preserveExistingAlarms);
                ZCalendarBuilder.parse(is, charset, handler);
            }
        } finally {
            is.close();
        }
    }
    
}
