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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAttributeSelectorImpl;


/**
 * <p>Java class for getServerRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getServerRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="server" type="{urn:zimbraAdmin}serverSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="applyConfig" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getServerRequest", propOrder = {
    "server"
})
public class testGetServerRequest
    extends testAttributeSelectorImpl
{

    protected testServerSelector server;
    @XmlAttribute(name = "applyConfig")
    protected Boolean applyConfig;

    /**
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link testServerSelector }
     *     
     */
    public testServerSelector getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link testServerSelector }
     *     
     */
    public void setServer(testServerSelector value) {
        this.server = value;
    }

    /**
     * Gets the value of the applyConfig property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isApplyConfig() {
        return applyConfig;
    }

    /**
     * Sets the value of the applyConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setApplyConfig(Boolean value) {
        this.applyConfig = value;
    }

}
