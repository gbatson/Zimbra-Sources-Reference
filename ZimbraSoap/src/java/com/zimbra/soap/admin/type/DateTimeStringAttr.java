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

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.base.DateTimeStringAttrInterface;

@XmlAccessorType(XmlAccessType.NONE)
public class DateTimeStringAttr
implements DateTimeStringAttrInterface {

    /**
     * @zm-api-field-tag YYYYMMDD[ThhmmssZ]
     * @zm-api-field-description Date in format : YYYYMMDD[ThhmmssZ]
     */
    @XmlAttribute(name=MailConstants.A_CAL_DATETIME, required=true)
    private final String dateTime;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private DateTimeStringAttr() {
        this((String) null);
    }

    public DateTimeStringAttr(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public DateTimeStringAttrInterface create(String dateTime) {
        return new DateTimeStringAttr(dateTime);
    }

    @Override
    public String getDateTime() { return dateTime; }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("dateTime", dateTime)
            .toString();
    }
}
