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

package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for loggingLevel.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="loggingLevel">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="error"/>
 *     &lt;enumeration value="warn"/>
 *     &lt;enumeration value="info"/>
 *     &lt;enumeration value="debug"/>
 *     &lt;enumeration value="trace"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "loggingLevel")
@XmlEnum
public enum testLoggingLevel {

    @XmlEnumValue("error")
    ERROR("error"),
    @XmlEnumValue("warn")
    WARN("warn"),
    @XmlEnumValue("info")
    INFO("info"),
    @XmlEnumValue("debug")
    DEBUG("debug"),
    @XmlEnumValue("trace")
    TRACE("trace");
    private final String value;

    testLoggingLevel(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testLoggingLevel fromValue(String v) {
        for (testLoggingLevel c: testLoggingLevel.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
