/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.service.im;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * <IMSetIdleRequest isIdle="1|0" idleTime="seconds_idle"/>
 */
public class IMSetIdle extends IMDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context)
    throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        if (!zsc.hasSession()) {
            throw ServiceException.FAILURE("Must be called in the context of a session", null);
        }
        
        IMPersona persona = super.getRequestedPersona(zsc);
        
        boolean isIdle = request.getAttributeBool(IMConstants.A_IS_IDLE);
        long idleTimeSecs = request.getAttributeLong(IMConstants.A_IDLE_TIME);
        long idleStartTime = System.currentTimeMillis() - (1000 * idleTimeSecs);
        if (idleStartTime < 0)
            idleStartTime = 0;
        
        // need way to get session iff already exists
        persona.setIdleState(this.getSession(zsc), isIdle, idleStartTime);
        
        return zsc.createElement(IMConstants.IM_SET_IDLE_RESPONSE);
    }

}
