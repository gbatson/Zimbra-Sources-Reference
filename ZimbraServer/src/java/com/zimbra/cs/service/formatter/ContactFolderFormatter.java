/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.formatter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.service.UserServletException;
import com.zimbra.cs.service.UserServlet.Context;
import com.zimbra.cs.service.util.ItemIdFormatter;

public class ContactFolderFormatter extends Formatter {

	private static final byte FIELD_DELIMITER   = '\u001D';  // group separator
	private static final byte CONTACT_DELIMITER = '\u001E';  // record separator
	private static final String CONTENT_TYPE = "text/x-zimbra-delimitted-fields";

	private enum Delimiter { Field, Contact };
	
	@Override
	public boolean canBeBlocked() {
		return false;
	}

	@Override
	public void formatCallback(Context context) throws UserServletException,
			ServiceException, IOException, ServletException {
		if (!(context.target instanceof Folder))
            throw UserServletException.notImplemented("can only handle Folders");

		Folder f = (Folder)context.target;
		if (f.getDefaultView() != MailItem.TYPE_CONTACT)
            throw UserServletException.notImplemented("can only handle Contact Folders");
		
        String v = context.params.get("t");
        Delimiter d = Delimiter.Field;
        if (v != null && v.equals("2"))
        	d = Delimiter.Contact;
        
        ItemIdFormatter ifmt = new ItemIdFormatter(context.authAccount, context.targetAccount, false);
        context.resp.setContentType(CONTENT_TYPE);
        OutputStream out = new BufferedOutputStream(context.resp.getOutputStream());
		
        Iterator<? extends MailItem> contacts = null;
        try {
            contacts = this.getMailItems(context, 0, 0, 0);
            while (contacts.hasNext()) {
                MailItem item = contacts.next();
                if (!(item instanceof Contact))
                    continue;
                // send metadata of the Contact
                // itemId
                out.write(MailConstants.A_ID.getBytes("UTF-8"));
                out.write(FIELD_DELIMITER);
                out.write(ifmt.formatItemId(item).getBytes("UTF-8"));
                out.write(FIELD_DELIMITER);
                // folderId
                out.write(MailConstants.A_FOLDER.getBytes("UTF-8"));
                out.write(FIELD_DELIMITER);
                out.write(ifmt.formatItemId(item.getFolderId()).getBytes("UTF-8"));
                out.write(FIELD_DELIMITER);
                // date
                out.write(MailConstants.A_DATE.getBytes("UTF-8"));
                out.write(FIELD_DELIMITER);
                out.write(Long.toString(item.getDate()).getBytes("UTF-8"));
                out.write(FIELD_DELIMITER);
                // revision
                out.write(MailConstants.A_REVISION.getBytes("UTF-8"));
                out.write(FIELD_DELIMITER);
                out.write(Integer.toString(item.getSavedSequence()).getBytes("UTF-8"));
                out.write(FIELD_DELIMITER);
                // fileAsStr
                try {
                    String fileAsStr = ((Contact)item).getFileAsString();
                    out.write(MailConstants.A_FILE_AS_STR.getBytes("UTF-8"));
                    out.write(FIELD_DELIMITER);
                    out.write(fileAsStr.getBytes("UTF-8"));
                } catch (ServiceException se) {
                }

                Map<String,String> fields = ((Contact) item).getFields();
                for (String k : fields.keySet()) {
                    out.write(FIELD_DELIMITER);
                    out.write(k.getBytes("UTF-8"));
                    out.write(FIELD_DELIMITER);
                    out.write(fields.get(k).getBytes("UTF-8"));
                }
                switch (d) {
                case Field:
                    out.write(FIELD_DELIMITER);
                    break;
                case Contact:
                    out.write(CONTACT_DELIMITER);
                    break;
                }
            }
            out.flush();
        } finally {
            if (contacts instanceof QueryResultIterator)
                ((QueryResultIterator) contacts).finished();
        }
    }

	@Override
	public String getType() {
		return "cf";
	}

}
