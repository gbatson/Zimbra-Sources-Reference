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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createSystemRetentionPolicyRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createSystemRetentionPolicyRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cos" type="{urn:zimbraAdmin}cosSelector" minOccurs="0"/>
 *         &lt;element name="keep" type="{urn:zimbraAdmin}policyHolder" minOccurs="0"/>
 *         &lt;element name="purge" type="{urn:zimbraAdmin}policyHolder" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createSystemRetentionPolicyRequest", propOrder = {
    "cos",
    "keep",
    "purge"
})
public class testCreateSystemRetentionPolicyRequest {

    protected testCosSelector cos;
    protected testPolicyHolder keep;
    protected testPolicyHolder purge;

    /**
     * Gets the value of the cos property.
     * 
     * @return
     *     possible object is
     *     {@link testCosSelector }
     *     
     */
    public testCosSelector getCos() {
        return cos;
    }

    /**
     * Sets the value of the cos property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCosSelector }
     *     
     */
    public void setCos(testCosSelector value) {
        this.cos = value;
    }

    /**
     * Gets the value of the keep property.
     * 
     * @return
     *     possible object is
     *     {@link testPolicyHolder }
     *     
     */
    public testPolicyHolder getKeep() {
        return keep;
    }

    /**
     * Sets the value of the keep property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPolicyHolder }
     *     
     */
    public void setKeep(testPolicyHolder value) {
        this.keep = value;
    }

    /**
     * Gets the value of the purge property.
     * 
     * @return
     *     possible object is
     *     {@link testPolicyHolder }
     *     
     */
    public testPolicyHolder getPurge() {
        return purge;
    }

    /**
     * Sets the value of the purge property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPolicyHolder }
     *     
     */
    public void setPurge(testPolicyHolder value) {
        this.purge = value;
    }

}
