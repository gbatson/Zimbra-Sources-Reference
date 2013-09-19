/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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

package com.zimbra.soap.admin.message;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.BackupConstants;
import com.zimbra.soap.admin.type.Name;

/**
 * @zm-api-command-network-edition
 * @zm-api-command-auth-required true
 * @zm-api-command-admin-auth-required true
 * @zm-api-command-description Schedule backups
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=BackupConstants.E_SCHEDULE_BACKUPS_REQUEST)
public class ScheduleBackupsRequest {

    /**
     * @zm-api-field-description Server specification
     */
    @XmlElement(name=AdminConstants.E_SERVER /* server */, required=true)
    private final Name server;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private ScheduleBackupsRequest() {
        this((Name) null);
    }

    public ScheduleBackupsRequest(Name server) {
        this.server = server;
    }

    public Name getServer() { return server; }

    public Objects.ToStringHelper addToStringInfo(
                Objects.ToStringHelper helper) {
        return helper
            .add("server", server);
    }

    @Override
    public String toString() {
        return addToStringInfo(Objects.toStringHelper(this))
                .toString();
    }
}
