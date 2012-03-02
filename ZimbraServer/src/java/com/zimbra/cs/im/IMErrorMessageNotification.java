/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 VMware, Inc.
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
package com.zimbra.cs.im;

import org.xmpp.packet.PacketError;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMMessage.TextPart;

/**
 * 
 */
public class IMErrorMessageNotification extends IMBaseMessageNotification {
    
    private PacketError.Condition mErrorCondition = null;
    private IMMessage mErrorText;

    public IMErrorMessageNotification(String fromAddr, String threadId, boolean typing, long timestamp, String errorText, PacketError.Condition errorCondition) {
        super(fromAddr, threadId, typing, timestamp);
        mErrorCondition = errorCondition;
        mErrorText = new IMMessage(null, new TextPart("ERROR: "+errorText), false);
        mErrorText.setFrom(new IMAddr(fromAddr));
    }

    public Element toXml(Element parent) throws ServiceException {
        Element e = super.toXml(parent);
        
//        switch(mErrorCondition) {
//            case recipient_unavailable:
//                e.addAttribute(IMConstants.A_ERROR, PacketError.Condition.recipient_unavailable.name());
//                break;
//                
//        }
        if (mErrorCondition != null)
            e.addAttribute(IMConstants.A_ERROR, mErrorCondition.name());
        mErrorText.toXml(e);
        return e;
    }
}
