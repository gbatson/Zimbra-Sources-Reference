/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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

import java.io.IOException;

import javax.servlet.ServletException;

import com.zimbra.cs.index.MailboxIndex;
import com.zimbra.cs.service.UserServletContext;
import com.zimbra.cs.service.UserServletException;
import com.zimbra.cs.service.formatter.FormatterFactory.FormatType;
import com.zimbra.common.service.ServiceException;

public class FreeBusyFormatter extends Formatter {

    private static final String ATTR_FREEBUSY = "zimbra_freebusy";

    public FormatType getType() {
        return FormatType.FREE_BUSY;
    }

    public boolean requiresAuth() {
        return true;
    }
    
    public String getDefaultSearchTypes() {
        return MailboxIndex.SEARCH_FOR_APPOINTMENTS;
    }

    public void formatCallback(UserServletContext context)
    throws IOException, ServiceException, UserServletException, ServletException {
        context.req.setAttribute(ATTR_FREEBUSY, "true");
        HtmlFormatter.dispatchJspRest(context.getServlet(), context);
    }

}
