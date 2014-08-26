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
 * <p>Java class for dataSourceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="dataSourceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="pop3"/>
 *     &lt;enumeration value="imap"/>
 *     &lt;enumeration value="caldav"/>
 *     &lt;enumeration value="contacts"/>
 *     &lt;enumeration value="yab"/>
 *     &lt;enumeration value="rss"/>
 *     &lt;enumeration value="cal"/>
 *     &lt;enumeration value="gal"/>
 *     &lt;enumeration value="xsync"/>
 *     &lt;enumeration value="tagmap"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "dataSourceType")
@XmlEnum
public enum testDataSourceType {

    @XmlEnumValue("pop3")
    POP_3("pop3"),
    @XmlEnumValue("imap")
    IMAP("imap"),
    @XmlEnumValue("caldav")
    CALDAV("caldav"),
    @XmlEnumValue("contacts")
    CONTACTS("contacts"),
    @XmlEnumValue("yab")
    YAB("yab"),
    @XmlEnumValue("rss")
    RSS("rss"),
    @XmlEnumValue("cal")
    CAL("cal"),
    @XmlEnumValue("gal")
    GAL("gal"),
    @XmlEnumValue("xsync")
    XSYNC("xsync"),
    @XmlEnumValue("tagmap")
    TAGMAP("tagmap");
    private final String value;

    testDataSourceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testDataSourceType fromValue(String v) {
        for (testDataSourceType c: testDataSourceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
