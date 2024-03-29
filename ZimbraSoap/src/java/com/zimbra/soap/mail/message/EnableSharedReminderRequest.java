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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.mail.type.SharedReminderMount;
import com.zimbra.soap.json.jackson.annotate.ZimbraUniqueElement;

/**
 * @zm-api-command-auth-required true
 * @zm-api-command-admin-auth-required false
 * @zm-api-command-description Enable/disable reminders for shared appointments/tasks on a mountpoint
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=MailConstants.E_ENABLE_SHARED_REMINDER_REQUEST)
public class EnableSharedReminderRequest {

    /**
     * @zm-api-field-description Specification for mountpoint
     */
    @ZimbraUniqueElement
    @XmlElement(name=MailConstants.E_MOUNT /* link */, required=true)
    private final SharedReminderMount mount;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private EnableSharedReminderRequest() {
        this((SharedReminderMount) null);
    }

    public EnableSharedReminderRequest(SharedReminderMount mount) {
        this.mount = mount;
    }

    public SharedReminderMount getMount() { return mount; }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("mount", mount)
            .toString();
    }
}
