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

package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for accountBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="accountBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="adminName"/>
 *     &lt;enumeration value="appAdminName"/>
 *     &lt;enumeration value="id"/>
 *     &lt;enumeration value="foreignPrincipal"/>
 *     &lt;enumeration value="name"/>
 *     &lt;enumeration value="krb5Principal"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "accountBy")
@XmlEnum
public enum testAccountBy {

    @XmlEnumValue("adminName")
    ADMIN_NAME("adminName"),
    @XmlEnumValue("appAdminName")
    APP_ADMIN_NAME("appAdminName"),
    @XmlEnumValue("id")
    ID("id"),
    @XmlEnumValue("foreignPrincipal")
    FOREIGN_PRINCIPAL("foreignPrincipal"),
    @XmlEnumValue("name")
    NAME("name"),
    @XmlEnumValue("krb5Principal")
    KRB_5_PRINCIPAL("krb5Principal");
    private final String value;

    testAccountBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testAccountBy fromValue(String v) {
        for (testAccountBy c: testAccountBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
