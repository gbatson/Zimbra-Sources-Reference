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

package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for granteeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="granteeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="usr"/>
 *     &lt;enumeration value="grp"/>
 *     &lt;enumeration value="egp"/>
 *     &lt;enumeration value="all"/>
 *     &lt;enumeration value="dom"/>
 *     &lt;enumeration value="edom"/>
 *     &lt;enumeration value="gst"/>
 *     &lt;enumeration value="key"/>
 *     &lt;enumeration value="pub"/>
 *     &lt;enumeration value="email"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "granteeType")
@XmlEnum
public enum testGranteeType {

    @XmlEnumValue("usr")
    USR("usr"),
    @XmlEnumValue("grp")
    GRP("grp"),
    @XmlEnumValue("egp")
    EGP("egp"),
    @XmlEnumValue("all")
    ALL("all"),
    @XmlEnumValue("dom")
    DOM("dom"),
    @XmlEnumValue("edom")
    EDOM("edom"),
    @XmlEnumValue("gst")
    GST("gst"),
    @XmlEnumValue("key")
    KEY("key"),
    @XmlEnumValue("pub")
    PUB("pub"),
    @XmlEnumValue("email")
    EMAIL("email");
    private final String value;

    testGranteeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testGranteeType fromValue(String v) {
        for (testGranteeType c: testGranteeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
