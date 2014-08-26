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

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cacheEntryType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="cacheEntryType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="acl"/>
 *     &lt;enumeration value="locale"/>
 *     &lt;enumeration value="skin"/>
 *     &lt;enumeration value="uistrings"/>
 *     &lt;enumeration value="license"/>
 *     &lt;enumeration value="all"/>
 *     &lt;enumeration value="account"/>
 *     &lt;enumeration value="config"/>
 *     &lt;enumeration value="globalgrant"/>
 *     &lt;enumeration value="cos"/>
 *     &lt;enumeration value="domain"/>
 *     &lt;enumeration value="galgroup"/>
 *     &lt;enumeration value="group"/>
 *     &lt;enumeration value="mime"/>
 *     &lt;enumeration value="server"/>
 *     &lt;enumeration value="alwaysOnCluster"/>
 *     &lt;enumeration value="zimlet"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "cacheEntryType")
@XmlEnum
public enum testCacheEntryType {

    @XmlEnumValue("acl")
    ACL("acl"),
    @XmlEnumValue("locale")
    LOCALE("locale"),
    @XmlEnumValue("skin")
    SKIN("skin"),
    @XmlEnumValue("uistrings")
    UISTRINGS("uistrings"),
    @XmlEnumValue("license")
    LICENSE("license"),
    @XmlEnumValue("all")
    ALL("all"),
    @XmlEnumValue("account")
    ACCOUNT("account"),
    @XmlEnumValue("config")
    CONFIG("config"),
    @XmlEnumValue("globalgrant")
    GLOBALGRANT("globalgrant"),
    @XmlEnumValue("cos")
    COS("cos"),
    @XmlEnumValue("domain")
    DOMAIN("domain"),
    @XmlEnumValue("galgroup")
    GALGROUP("galgroup"),
    @XmlEnumValue("group")
    GROUP("group"),
    @XmlEnumValue("mime")
    MIME("mime"),
    @XmlEnumValue("server")
    SERVER("server"),
    @XmlEnumValue("alwaysOnCluster")
    ALWAYS_ON_CLUSTER("alwaysOnCluster"),
    @XmlEnumValue("zimlet")
    ZIMLET("zimlet");
    private final String value;

    testCacheEntryType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testCacheEntryType fromValue(String v) {
        for (testCacheEntryType c: testCacheEntryType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
