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

package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modifyVoiceMailPinRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modifyVoiceMailPinRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="storeprincipal" type="{urn:zimbraVoice}storePrincipalSpec" minOccurs="0"/>
 *         &lt;element name="phone" type="{urn:zimbraVoice}modifyVoiceMailPinSpec" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modifyVoiceMailPinRequest", propOrder = {
    "storeprincipal",
    "phone"
})
public class testModifyVoiceMailPinRequest {

    protected testStorePrincipalSpec storeprincipal;
    protected testModifyVoiceMailPinSpec phone;

    /**
     * Gets the value of the storeprincipal property.
     * 
     * @return
     *     possible object is
     *     {@link testStorePrincipalSpec }
     *     
     */
    public testStorePrincipalSpec getStoreprincipal() {
        return storeprincipal;
    }

    /**
     * Sets the value of the storeprincipal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testStorePrincipalSpec }
     *     
     */
    public void setStoreprincipal(testStorePrincipalSpec value) {
        this.storeprincipal = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link testModifyVoiceMailPinSpec }
     *     
     */
    public testModifyVoiceMailPinSpec getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link testModifyVoiceMailPinSpec }
     *     
     */
    public void setPhone(testModifyVoiceMailPinSpec value) {
        this.phone = value;
    }

}
