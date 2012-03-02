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
package com.zimbra.cs.dav.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Element;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.dav.DavContext;
import com.zimbra.cs.dav.DavElements;
import com.zimbra.cs.dav.DavException;
import com.zimbra.cs.dav.DavProtocol;
import com.zimbra.cs.dav.property.CardDavProperty;
import com.zimbra.cs.dav.property.ResourceProperty;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.service.formatter.VCard;

public class AddressObject extends MailItemResource {

    public static final String VCARD_EXTENSION = ".vcf";
    
    public AddressObject(DavContext ctxt, Contact item) throws ServiceException {
        super(ctxt, item);
        setProperty(DavElements.P_GETCONTENTTYPE, DavProtocol.VCARD_CONTENT_TYPE);
        setProperty(DavElements.P_GETCONTENTLENGTH, Integer.toString(mId));
    }
    
    public boolean isCollection() {
        return false;
    }
    
    public ResourceProperty getProperty(Element prop) {
        if (prop.getQName().equals(DavElements.CardDav.E_ADDRESS_DATA)) {
            return CardDavProperty.getAddressbookData(prop, this);
        }
        return super.getProperty(prop);
    }
    
    protected InputStream getRawContent(DavContext ctxt) throws DavException, IOException {
        try {
            return new ByteArrayInputStream(toVCard(ctxt).getBytes("UTF-8"));
        } catch (ServiceException e) {
            ZimbraLog.dav.warn("can't get content for Contact %d", mId, e);
        }
        return null;
    }
    
    public String toVCard(DavContext ctxt) throws ServiceException, DavException {
        Contact contact = (Contact)getMailItem(ctxt);
        return VCard.formatContact(contact, null, true).formatted;
    }
    public String toVCard(DavContext ctxt, java.util.Collection<String> attrs) throws ServiceException, DavException {
        if (attrs == null || attrs.isEmpty())
            return toVCard(ctxt);
        Contact contact = (Contact)getMailItem(ctxt);
        return VCard.formatContact(contact, attrs, true).formatted;
    }
}
