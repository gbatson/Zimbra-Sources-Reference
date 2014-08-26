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

package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addMsgRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addMsgRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="m" type="{urn:zimbraMail}addMsgSpec"/>
 *       &lt;/sequence>
 *       &lt;attribute name="filterSent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addMsgRequest", propOrder = {
    "m"
})
public class testAddMsgRequest {

    @XmlElement(required = true)
    protected testAddMsgSpec m;
    @XmlAttribute(name = "filterSent")
    protected Boolean filterSent;

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testAddMsgSpec }
     *     
     */
    public testAddMsgSpec getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAddMsgSpec }
     *     
     */
    public void setM(testAddMsgSpec value) {
        this.m = value;
    }

    /**
     * Gets the value of the filterSent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFilterSent() {
        return filterSent;
    }

    /**
     * Sets the value of the filterSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFilterSent(Boolean value) {
        this.filterSent = value;
    }

}
