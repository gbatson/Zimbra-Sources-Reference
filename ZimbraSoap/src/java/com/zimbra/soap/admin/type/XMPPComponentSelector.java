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

package com.zimbra.soap.admin.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.zimbra.common.soap.AdminConstants;

@XmlAccessorType(XmlAccessType.NONE)
public class XMPPComponentSelector {

    /**
     * @zm-api-field-tag xmpp-comp-selector-by
     * @zm-api-field-description Select the meaning of <b>{xmpp-comp-selector-key}</b>
     */
    @XmlAttribute(name=AdminConstants.A_BY, required=true)
    private final XMPPComponentBy by;

    /**
     * @zm-api-field-tag xmpp-comp-selector-key
     * @zm-api-field-description The key used to identify the XMPP component.
     * Meaning determined by <b>{xmpp-comp-selector-by}</b>
     */
    @XmlValue
    private final String value;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private XMPPComponentSelector() {
        this((XMPPComponentBy) null, (String) null);
    }

    public XMPPComponentSelector(XMPPComponentBy by, String value) {
        this.by = by;
        this.value = value;
    }

    public XMPPComponentBy getBy() { return by; }
    public String getValue() { return value; }
}
