/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.service.im;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * 
 */
public class IMListConferenceRooms extends IMDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        Element response = zsc.createElement(IMConstants.IM_LIST_CONFERENCE_ROOMS_RESPONSE);
        IMPersona persona = super.getRequestedPersona(zsc);
        
        String svc = request.getAttribute("svc");
        List<Pair<String/*name*/, String/*JID*/>> rooms = persona.listRooms(svc);
        for (Pair<String/*name*/, String/*JID*/> pair : rooms) {
            Element elt = response.addElement("room");
            elt.addAttribute("name", pair.getFirst());
            elt.addAttribute("addr", pair.getSecond());
        }
        return response;
    }
}
