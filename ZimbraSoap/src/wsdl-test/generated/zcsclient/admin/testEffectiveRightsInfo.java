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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for effectiveRightsInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="effectiveRightsInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="right" type="{urn:zimbraAdmin}rightWithName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="setAttrs" type="{urn:zimbraAdmin}effectiveAttrsInfo"/>
 *         &lt;element name="getAttrs" type="{urn:zimbraAdmin}effectiveAttrsInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "effectiveRightsInfo", propOrder = {
    "right",
    "setAttrs",
    "getAttrs"
})
@XmlSeeAlso({
    testEffectiveRightsTargetInfo.class
})
public class testEffectiveRightsInfo {

    protected List<testRightWithName> right;
    @XmlElement(required = true)
    protected testEffectiveAttrsInfo setAttrs;
    @XmlElement(required = true)
    protected testEffectiveAttrsInfo getAttrs;

    /**
     * Gets the value of the right property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the right property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testRightWithName }
     * 
     * 
     */
    public List<testRightWithName> getRight() {
        if (right == null) {
            right = new ArrayList<testRightWithName>();
        }
        return this.right;
    }

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

    /**
     * Gets the value of the getAttrs property.
     * 
     * @return
     *     possible object is
     *     {@link testEffectiveAttrsInfo }
     *     
     */
    public testEffectiveAttrsInfo getGetAttrs() {
        return getAttrs;
    }

    /**
     * Sets the value of the getAttrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEffectiveAttrsInfo }
     *     
     */
    public void setGetAttrs(testEffectiveAttrsInfo value) {
        this.getAttrs = value;
    }

}
