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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rightViaInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rightViaInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="target" type="{urn:zimbraAdmin}targetWithType"/>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeWithType"/>
 *         &lt;element name="right" type="{urn:zimbraAdmin}checkedRight"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rightViaInfo", propOrder = {

})
public class testRightViaInfo {

    @XmlElement(required = true)
    protected testTargetWithType target;
    @XmlElement(required = true)
    protected testGranteeWithType grantee;
    @XmlElement(required = true)
    protected String right;

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link testTargetWithType }
     *     
     */
    public testTargetWithType getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTargetWithType }
     *     
     */
    public void setTarget(testTargetWithType value) {
        this.target = value;
    }

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeWithType }
     *     
     */
    public testGranteeWithType getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeWithType }
     *     
     */
    public void setGrantee(testGranteeWithType value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the right property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRight(String value) {
        this.right = value;
    }

}
