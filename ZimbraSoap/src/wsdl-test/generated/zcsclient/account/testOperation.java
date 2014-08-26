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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for operation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="operation">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="delete"/>
 *     &lt;enumeration value="modify"/>
 *     &lt;enumeration value="rename"/>
 *     &lt;enumeration value="addOwners"/>
 *     &lt;enumeration value="removeOwners"/>
 *     &lt;enumeration value="setOwners"/>
 *     &lt;enumeration value="grantRights"/>
 *     &lt;enumeration value="revokeRights"/>
 *     &lt;enumeration value="setRights"/>
 *     &lt;enumeration value="addMembers"/>
 *     &lt;enumeration value="removeMembers"/>
 *     &lt;enumeration value="acceptSubsReq"/>
 *     &lt;enumeration value="rejectSubsReq"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "operation")
@XmlEnum
public enum testOperation {

    @XmlEnumValue("delete")
    DELETE("delete"),
    @XmlEnumValue("modify")
    MODIFY("modify"),
    @XmlEnumValue("rename")
    RENAME("rename"),
    @XmlEnumValue("addOwners")
    ADD_OWNERS("addOwners"),
    @XmlEnumValue("removeOwners")
    REMOVE_OWNERS("removeOwners"),
    @XmlEnumValue("setOwners")
    SET_OWNERS("setOwners"),
    @XmlEnumValue("grantRights")
    GRANT_RIGHTS("grantRights"),
    @XmlEnumValue("revokeRights")
    REVOKE_RIGHTS("revokeRights"),
    @XmlEnumValue("setRights")
    SET_RIGHTS("setRights"),
    @XmlEnumValue("addMembers")
    ADD_MEMBERS("addMembers"),
    @XmlEnumValue("removeMembers")
    REMOVE_MEMBERS("removeMembers"),
    @XmlEnumValue("acceptSubsReq")
    ACCEPT_SUBS_REQ("acceptSubsReq"),
    @XmlEnumValue("rejectSubsReq")
    REJECT_SUBS_REQ("rejectSubsReq");
    private final String value;

    testOperation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testOperation fromValue(String v) {
        for (testOperation c: testOperation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
