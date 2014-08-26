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
 * <p>Java class for createXMPPComponentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createXMPPComponentResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="xmppcomponent" type="{urn:zimbraAdmin}xmppComponentInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createXMPPComponentResponse", propOrder = {
    "xmppcomponent"
})
public class testCreateXMPPComponentResponse {

    @XmlElement(required = true)
    protected testXmppComponentInfo xmppcomponent;

    /**
     * Gets the value of the xmppcomponent property.
     * 
     * @return
     *     possible object is
     *     {@link testXmppComponentInfo }
     *     
     */
    public testXmppComponentInfo getXmppcomponent() {
        return xmppcomponent;
    }

    /**
     * Sets the value of the xmppcomponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link testXmppComponentInfo }
     *     
     */
    public void setXmppcomponent(testXmppComponentInfo value) {
        this.xmppcomponent = value;
    }

}
