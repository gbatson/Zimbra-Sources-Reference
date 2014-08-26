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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getVoiceInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getVoiceInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="audioType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="storeprincipal" type="{urn:zimbraVoice}storePrincipalSpec"/>
 *         &lt;element name="phone" type="{urn:zimbraVoice}voiceInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getVoiceInfoResponse", propOrder = {
    "audioType",
    "storeprincipal",
    "phone"
})
public class testGetVoiceInfoResponse {

    @XmlElement(required = true)
    protected String audioType;
    @XmlElement(required = true)
    protected testStorePrincipalSpec storeprincipal;
    protected List<testVoiceInfo> phone;

    /**
     * Gets the value of the audioType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAudioType() {
        return audioType;
    }

    /**
     * Sets the value of the audioType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAudioType(String value) {
        this.audioType = value;
    }

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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the phone property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhone().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testVoiceInfo }
     * 
     * 
     */
    public List<testVoiceInfo> getPhone() {
        if (phone == null) {
            phone = new ArrayList<testVoiceInfo>();
        }
        return this.phone;
    }

}
