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

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.admin.type.CalendarResourceInfo;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=AdminConstants.E_RENAME_CALENDAR_RESOURCE_RESPONSE)
public class RenameCalendarResourceResponse {

    /**
     * @zm-api-field-description Information about calendar resource
     */
    @XmlElement(name=AdminConstants.E_CALENDAR_RESOURCE, required=true)
    private final CalendarResourceInfo calResource;

    /**
     * no-argument constructor wanted by JAXB
     */
     @SuppressWarnings("unused")
    private RenameCalendarResourceResponse() {
        this((CalendarResourceInfo)null);
    }

    public RenameCalendarResourceResponse(CalendarResourceInfo calResource) {
        this.calResource = calResource;
    }

    public CalendarResourceInfo getCalResource() { return calResource; }
}
