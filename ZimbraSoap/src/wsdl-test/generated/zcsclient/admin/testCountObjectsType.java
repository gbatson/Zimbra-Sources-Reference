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

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for countObjectsType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="countObjectsType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="userAccount"/>
 *     &lt;enumeration value="account"/>
 *     &lt;enumeration value="alias"/>
 *     &lt;enumeration value="dl"/>
 *     &lt;enumeration value="domain"/>
 *     &lt;enumeration value="cos"/>
 *     &lt;enumeration value="server"/>
 *     &lt;enumeration value="calresource"/>
 *     &lt;enumeration value="accountOnUCService"/>
 *     &lt;enumeration value="cosOnUCService"/>
 *     &lt;enumeration value="domainOnUCService"/>
 *     &lt;enumeration value="internalUserAccount"/>
 *     &lt;enumeration value="internalArchivingAccount"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "countObjectsType")
@XmlEnum
public enum testCountObjectsType {

    @XmlEnumValue("userAccount")
    USER_ACCOUNT("userAccount"),
    @XmlEnumValue("account")
    ACCOUNT("account"),
    @XmlEnumValue("alias")
    ALIAS("alias"),
    @XmlEnumValue("dl")
    DL("dl"),
    @XmlEnumValue("domain")
    DOMAIN("domain"),
    @XmlEnumValue("cos")
    COS("cos"),
    @XmlEnumValue("server")
    SERVER("server"),
    @XmlEnumValue("calresource")
    CALRESOURCE("calresource"),
    @XmlEnumValue("accountOnUCService")
    ACCOUNT_ON_UC_SERVICE("accountOnUCService"),
    @XmlEnumValue("cosOnUCService")
    COS_ON_UC_SERVICE("cosOnUCService"),
    @XmlEnumValue("domainOnUCService")
    DOMAIN_ON_UC_SERVICE("domainOnUCService"),
    @XmlEnumValue("internalUserAccount")
    INTERNAL_USER_ACCOUNT("internalUserAccount"),
    @XmlEnumValue("internalArchivingAccount")
    INTERNAL_ARCHIVING_ACCOUNT("internalArchivingAccount");
    private final String value;

    testCountObjectsType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testCountObjectsType fromValue(String v) {
        for (testCountObjectsType c: testCountObjectsType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
