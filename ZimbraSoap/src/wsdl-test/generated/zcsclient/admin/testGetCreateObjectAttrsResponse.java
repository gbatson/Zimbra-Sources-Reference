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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getCreateObjectAttrsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCreateObjectAttrsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setAttrs" type="{urn:zimbraAdmin}effectiveAttrsInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCreateObjectAttrsResponse", propOrder = {
    "setAttrs"
})
public class testGetCreateObjectAttrsResponse {

    @XmlElement(required = true)
    protected testEffectiveAttrsInfo setAttrs;

    /**
     * Gets the value of the setAttrs property.
     * 
     * @return
     *     possible object is
     *     {@link testEffectiveAttrsInfo }
     *     
     */
    public testEffectiveAttrsInfo getSetAttrs() {
        return setAttrs;
    }

    /**
     * Sets the value of the setAttrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEffectiveAttrsInfo }
     *     
     */
    public void setSetAttrs(testEffectiveAttrsInfo value) {
        this.setAttrs = value;
    }

}