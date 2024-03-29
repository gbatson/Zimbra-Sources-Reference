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
 * <p>Java class for grantRightRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="grantRightRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="target" type="{urn:zimbraAdmin}effectiveRightsTargetSelector"/>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeSelector"/>
 *         &lt;element name="right" type="{urn:zimbraAdmin}rightModifierInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grantRightRequest", propOrder = {
    "target",
    "grantee",
    "right"
})
public class testGrantRightRequest {

    @XmlElement(required = true)
    protected testEffectiveRightsTargetSelector target;
    @XmlElement(required = true)
    protected testGranteeSelector grantee;
    @XmlElement(required = true)
    protected testRightModifierInfo right;

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link testEffectiveRightsTargetSelector }
     *     
     */
    public testEffectiveRightsTargetSelector getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEffectiveRightsTargetSelector }
     *     
     */
    public void setTarget(testEffectiveRightsTargetSelector value) {
        this.target = value;
    }

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeSelector }
     *     
     */
    public testGranteeSelector getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeSelector }
     *     
     */
    public void setGrantee(testGranteeSelector value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the right property.
     * 
     * @return
     *     possible object is
     *     {@link testRightModifierInfo }
     *     
     */
    public testRightModifierInfo getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRightModifierInfo }
     *     
     */
    public void setRight(testRightModifierInfo value) {
        this.right = value;
    }

}
