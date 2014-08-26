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
 * <p>Java class for getSessionsSortBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="getSessionsSortBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="nameAsc"/>
 *     &lt;enumeration value="nameDesc"/>
 *     &lt;enumeration value="createdAsc"/>
 *     &lt;enumeration value="createdDesc"/>
 *     &lt;enumeration value="accessedAsc"/>
 *     &lt;enumeration value="accessedDesc"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "getSessionsSortBy")
@XmlEnum
public enum testGetSessionsSortBy {

    @XmlEnumValue("nameAsc")
    NAME_ASC("nameAsc"),
    @XmlEnumValue("nameDesc")
    NAME_DESC("nameDesc"),
    @XmlEnumValue("createdAsc")
    CREATED_ASC("createdAsc"),
    @XmlEnumValue("createdDesc")
    CREATED_DESC("createdDesc"),
    @XmlEnumValue("accessedAsc")
    ACCESSED_ASC("accessedAsc"),
    @XmlEnumValue("accessedDesc")
    ACCESSED_DESC("accessedDesc");
    private final String value;

    testGetSessionsSortBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testGetSessionsSortBy fromValue(String v) {
        for (testGetSessionsSortBy c: testGetSessionsSortBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
