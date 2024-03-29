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

package com.zimbra.soap.mail.message;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.mail.type.ContactSpec;
import com.zimbra.soap.type.ZmBoolean;
import com.zimbra.soap.json.jackson.annotate.ZimbraUniqueElement;

/**
 * @zm-api-command-auth-required true
 * @zm-api-command-admin-auth-required false
 * @zm-api-command-description Create a contact
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=MailConstants.E_CREATE_CONTACT_REQUEST)
public class CreateContactRequest {

    /**
     * @zm-api-field-tag verbose
     * @zm-api-field-description If set (defaults to unset) The returned <b>&lt;cn></b> is just a placeholder
     * containing the new contact ID (i.e. <b>&lt;cn id="{id}"/></b>)
     */
    @XmlAttribute(name=MailConstants.A_VERBOSE /* verbose */, required=false)
    private ZmBoolean verbose;

    /**
     * @zm-api-field-description Contact specification
     */
    @ZimbraUniqueElement
    @XmlElement(name=MailConstants.E_CONTACT /* cn */, required=true)
    private final ContactSpec contact;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private CreateContactRequest() {
        this((ContactSpec) null);
    }

    public CreateContactRequest(ContactSpec contact) {
        this.contact = contact;
    }

    public void setVerbose(Boolean verbose) { this.verbose = ZmBoolean.fromBool(verbose); }
    public Boolean getVerbose() { return ZmBoolean.toBool(verbose); }
    public ContactSpec getContact() { return contact; }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("verbose", verbose)
            .add("contact", contact)
            .toString();
    }
}
