/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013, 2014 Zimbra, Inc.
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
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.VoiceAdminConstants;
import com.zimbra.soap.type.KeyValuePair;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=VoiceAdminConstants.E_UPDATE_PRESENCE_SESSION_ID_RESPONSE)
@XmlType(propOrder = {})
public class UpdatePresenceSessionIdResponse {

    /**
     * @zm-api-field-description Newly generated Cisco presence session ID.
     */
    @XmlElement(name=AdminConstants.E_A)
    private KeyValuePair sessionId;

    public UpdatePresenceSessionIdResponse() {
    }

    public void setSessionId(KeyValuePair sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        if (sessionId == null) {
            return null;
        }
        return sessionId.getValue();
    }
}
