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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAccountSelector;


/**
 * <p>Java class for changePasswordRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="changePasswordRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="account" type="{urn:zimbra}accountSelector"/>
 *         &lt;element name="oldPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="virtualHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "changePasswordRequest", propOrder = {

})
public class testChangePasswordRequest {

    @XmlElement(required = true)
    protected testAccountSelector account;
    @XmlElement(required = true)
    protected String oldPassword;
    @XmlElement(required = true)
    protected String password;
    protected String virtualHost;

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link testAccountSelector }
     *     
     */
    public testAccountSelector getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAccountSelector }
     *     
     */
    public void setAccount(testAccountSelector value) {
        this.account = value;
    }

    /**
     * Gets the value of the oldPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Sets the value of the oldPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldPassword(String value) {
        this.oldPassword = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the virtualHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVirtualHost() {
        return virtualHost;
    }

    /**
     * Sets the value of the virtualHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVirtualHost(String value) {
        this.virtualHost = value;
    }

}
