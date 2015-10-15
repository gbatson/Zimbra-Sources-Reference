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
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlValue;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;

@XmlAccessorType(XmlAccessType.NONE)
public class CosSelector {

    @XmlEnum
    public static enum CosBy {
        // case must match protocol
        id, name;

        public static CosBy fromString(String s) throws ServiceException {
            try {
                return CosBy.valueOf(s);
            } catch (IllegalArgumentException e) {
                throw ServiceException.INVALID_REQUEST("unknown key: "+s, e);
            }
        }
    }

    /**
     * @zm-api-field-tag cos-selector-by
     * @zm-api-field-description Select the meaning of <b>{cos-selector-key}</b>
     */
    @XmlAttribute(name=AdminConstants.A_BY) private final CosBy cosBy;

    /**
     * @zm-api-field-tag cos-selector-key
     * @zm-api-field-description The key used to identify the COS. Meaning determined by <b>{cos-selector-by}</b>
     */
    @XmlValue private final String key;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private CosSelector() {
        this.cosBy = null;
        this.key = null;
    }

    public CosSelector(CosBy by, String key) {
        this.cosBy = by;
        this.key = key;
    }

    public String getKey() { return key; }

    public CosBy getBy() { return cosBy; }
}