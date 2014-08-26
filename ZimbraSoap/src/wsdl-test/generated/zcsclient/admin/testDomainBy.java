/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013, 2014 Zimbra, Inc.
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
 * <p>Java class for domainBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="domainBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="id"/>
 *     &lt;enumeration value="name"/>
 *     &lt;enumeration value="virtualHostname"/>
 *     &lt;enumeration value="krb5Realm"/>
 *     &lt;enumeration value="foreignName"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "domainBy")
@XmlEnum
public enum testDomainBy {

    @XmlEnumValue("id")
    ID("id"),
    @XmlEnumValue("name")
    NAME("name"),
    @XmlEnumValue("virtualHostname")
    VIRTUAL_HOSTNAME("virtualHostname"),
    @XmlEnumValue("krb5Realm")
    KRB_5_REALM("krb5Realm"),
    @XmlEnumValue("foreignName")
    FOREIGN_NAME("foreignName");
    private final String value;

    testDomainBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testDomainBy fromValue(String v) {
        for (testDomainBy c: testDomainBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
