/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2009, 2010, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.client.soap;

import org.dom4j.Element;

import com.zimbra.common.soap.DomUtil;
import com.zimbra.common.soap.MailConstants;

public class LmcSearchConvRequest extends LmcSearchRequest {

    private String mConvID;

    public void setConvID(String c) { mConvID = c; }

    public String getConvID() { return mConvID; }

    protected Element getRequestXML() {
        // the request XML is the same as for search, with a conversation ID added
        Element response = createQuery(MailConstants.SEARCH_CONV_REQUEST);
        DomUtil.addAttr(response, MailConstants.A_CONV_ID, mConvID);
        return response;
    }


}
