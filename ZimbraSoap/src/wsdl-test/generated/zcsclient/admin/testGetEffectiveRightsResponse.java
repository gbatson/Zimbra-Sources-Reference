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
 * <p>Java class for getEffectiveRightsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getEffectiveRightsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeInfo"/>
 *         &lt;element name="target" type="{urn:zimbraAdmin}effectiveRightsTargetInfo"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getEffectiveRightsResponse", propOrder = {

})
public class testGetEffectiveRightsResponse {

    @XmlElement(required = true)
    protected testGranteeInfo grantee;
    @XmlElement(required = true)
    protected testEffectiveRightsTargetInfo target;

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeInfo }
     *     
     */
    public testGranteeInfo getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeInfo }
     *     
     */
    public void setGrantee(testGranteeInfo value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link testEffectiveRightsTargetInfo }
     *     
     */
    public testEffectiveRightsTargetInfo getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEffectiveRightsTargetInfo }
     *     
     */
    public void setTarget(testEffectiveRightsTargetInfo value) {
        this.target = value;
    }

}
