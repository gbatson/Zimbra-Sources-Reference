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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.type.ZmBoolean;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=AdminConstants.E_CHECK_HEALTH_RESPONSE)
@XmlType(propOrder = {})
public class CheckHealthResponse {

    /**
     * @zm-api-field-description Flags whether healthy or not
     */
    @XmlAttribute(name=AdminConstants.A_HEALTHY, required=true)
    private ZmBoolean healthy;

    public CheckHealthResponse() { }

    public CheckHealthResponse(boolean healthy) { this.healthy = ZmBoolean.fromBool(healthy); }

    public void setHealthy(boolean healthy) { this.healthy = ZmBoolean.fromBool(healthy); }

    public boolean isHealthy() { return ZmBoolean.toBool(healthy); }
}