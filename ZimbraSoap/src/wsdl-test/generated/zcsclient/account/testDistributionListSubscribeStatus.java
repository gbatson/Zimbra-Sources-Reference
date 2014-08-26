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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for distributionListSubscribeStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="distributionListSubscribeStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="subscribed"/>
 *     &lt;enumeration value="unsubscribed"/>
 *     &lt;enumeration value="awaiting_approval"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "distributionListSubscribeStatus")
@XmlEnum
public enum testDistributionListSubscribeStatus {

    @XmlEnumValue("subscribed")
    SUBSCRIBED("subscribed"),
    @XmlEnumValue("unsubscribed")
    UNSUBSCRIBED("unsubscribed"),
    @XmlEnumValue("awaiting_approval")
    AWAITING___APPROVAL("awaiting_approval");
    private final String value;

    testDistributionListSubscribeStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testDistributionListSubscribeStatus fromValue(String v) {
        for (testDistributionListSubscribeStatus c: testDistributionListSubscribeStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
