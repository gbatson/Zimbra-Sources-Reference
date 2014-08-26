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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testId;


/**
 * <p>Java class for tzReplaceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzReplaceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="wellKnownTz" type="{urn:zimbra}id" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraAdmin}calTZInfo" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tzReplaceInfo", propOrder = {

})
public class testTzReplaceInfo {

    protected testId wellKnownTz;
    protected testCalTZInfo tz;

    /**
     * Gets the value of the wellKnownTz property.
     * 
     * @return
     *     possible object is
     *     {@link testId }
     *     
     */
    public testId getWellKnownTz() {
        return wellKnownTz;
    }

    /**
     * Sets the value of the wellKnownTz property.
     * 
     * @param value
     *     allowed object is
     *     {@link testId }
     *     
     */
    public void setWellKnownTz(testId value) {
        this.wellKnownTz = value;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link testCalTZInfo }
     *     
     */
    public testCalTZInfo getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalTZInfo }
     *     
     */
    public void setTz(testCalTZInfo value) {
        this.tz = value;
    }

}
