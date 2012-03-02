/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmpp.packet.IQ;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.Element;

import com.zimbra.cs.im.IMAddr;
import com.zimbra.cs.im.IMChat;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.cs.im.IMServiceException;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.soap.ZimbraSoapContext;

public class IMModifyChat extends IMDocumentHandler {

    static enum Op {
        CLOSE, ADDUSER, CONFIGURE
        ;
    }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException, SoapFaultException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        OperationContext octxt = getOperationContext(zsc, context);

        Element response = zsc.createElement(IMConstants.IM_MODIFY_CHAT_RESPONSE);

        String threadId = request.getAttribute(IMConstants.A_THREAD_ID);
        response.addAttribute(IMConstants.A_THREAD_ID, threadId);

        IMPersona persona = super.getRequestedPersona(zsc);
        IMChat chat = null;
        
        if (persona != null)
            chat = persona.getChat(threadId);
        
        if (chat == null) {
            response.addAttribute(IMConstants.A_ERROR, "not_found");
        } else {
            String opStr = request.getAttribute(IMConstants.A_OPERATION);
            Op op = Op.valueOf(opStr.toUpperCase());
            switch (op) {
                case CLOSE:
                    synchronized(persona.getLock()) {
                        persona.closeChat(octxt, chat);
                    }
                    break;
                case ADDUSER: 
                    synchronized(persona.getLock()) {
                        String newUser = request.getAttribute(IMConstants.A_ADDRESS);
                        String inviteMessage = request.getText();
                        if (inviteMessage == null || inviteMessage.length() == 0)
                            inviteMessage = "Please join my chat";
                        persona.addUserToChat(octxt, chat, new IMAddr(newUser), inviteMessage);
                    }
                    break;
                case CONFIGURE:
                    handleConfigure(persona, chat, request, response);
                    break;
            }
        }
        return response;        
    }

    private void handleConfigure(IMPersona persona, IMChat chat, Element request, Element response) throws ServiceException {
        if (!chat.isMUC())
            throw IMServiceException.NOT_A_MUC_CHAT(chat.getThreadId());
        
        Map<String, Object> data = new HashMap<String, Object>();
        for (Iterator<Element> iter = request.elementIterator("var"); iter.hasNext();) {
            Element var = iter.next();
            String name = var.getAttribute("name");
            boolean isMulti = var.getAttributeBool("multi", false);
            if (!isMulti) {
                String s = var.getText().trim();
                if (s.length() > 0) 
                    data.put(name, s);
            } else {
                List<String> values = new ArrayList<String>();
                for (Iterator<Element> valueIter = var.elementIterator("value"); valueIter.hasNext();) {
                    Element valueElt = valueIter.next();
                    String s = valueElt.getText().trim();
                    if (s.length()>0)
                        values.add(s);
                }
                data.put(name, values);
            }
        }
        IQ result = persona.configureChat(chat, data);
        if (result == null) {
            throw IMServiceException.NO_RESPONSE_FROM_REMOTE("Attempting to configure chat "+chat.toString(), chat.getThreadId());
        }
    }
}
