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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkLicenseStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="checkLicenseStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ok"/>
 *     &lt;enumeration value="no"/>
 *     &lt;enumeration value="inGracePeriod"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "checkLicenseStatus")
@XmlEnum
public enum testCheckLicenseStatus {

    @XmlEnumValue("ok")
    OK("ok"),
    @XmlEnumValue("no")
    NO("no"),
    @XmlEnumValue("inGracePeriod")
    IN_GRACE_PERIOD("inGracePeriod");
    private final String value;

    testCheckLicenseStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testCheckLicenseStatus fromValue(String v) {
        for (testCheckLicenseStatus c: testCheckLicenseStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
