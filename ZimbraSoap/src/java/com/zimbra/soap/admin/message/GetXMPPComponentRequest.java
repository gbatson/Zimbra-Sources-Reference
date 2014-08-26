/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013, 2014 Zimbra, Inc.
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

package com.zimbra.soap.admin.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.type.AttributeSelectorImpl;
import com.zimbra.soap.admin.type.XMPPComponentSelector;

/**
 * @zm-api-command-auth-required true
 * @zm-api-command-admin-auth-required true
 * @zm-api-command-description Get XMPP Component
 * <br />
 * XMPP stands for Extensible Messaging and Presence Protocol
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=AdminConstants.E_GET_XMPPCOMPONENT_REQUEST)
public class GetXMPPComponentRequest extends AttributeSelectorImpl {

    /**
     * @zm-api-field-description XMPP Component selector
     */
    @XmlElement(name=AccountConstants.E_XMPP_COMPONENT, required=true)
    private final XMPPComponentSelector component;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private GetXMPPComponentRequest() {
        this((XMPPComponentSelector) null);
    }

    public GetXMPPComponentRequest(XMPPComponentSelector component) {
        this.component = component;
    }

    public XMPPComponentSelector getComponent() { return component; }
}
