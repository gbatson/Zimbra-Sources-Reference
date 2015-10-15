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

import com.zimbra.common.soap.AdminConstants;

/**
 * @zm-api-command-auth-required true
 * @zm-api-command-admin-auth-required true
 * @zm-api-command-description Get all Zimlets
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=AdminConstants.E_GET_ALL_ZIMLETS_REQUEST)
public class GetAllZimletsRequest {

    /**
     * @zm-api-field-tag exclude
     * @zm-api-field-description {exclude} can be "none|extension|mail"
     * <table>
     * <tr> <td> <b>extension</b> </td> <td> return only mail Zimlets </td> </tr>
     * <tr> <td> <b>mail</b> </td> <td> return only admin extensions </td> </tr>
     * <tr> <td> <b>none [default]</b> </td> <td> return both mail and admin zimlets </td> </tr>
     * </table>
     */
    @XmlAttribute(name=AdminConstants.A_EXCLUDE, required=false)
    private final String exclude;

    public GetAllZimletsRequest() {
        this((String) null);
    }

    public GetAllZimletsRequest(String exclude) {
        this.exclude = exclude;
    }

    public String getExclude() { return exclude; }
}