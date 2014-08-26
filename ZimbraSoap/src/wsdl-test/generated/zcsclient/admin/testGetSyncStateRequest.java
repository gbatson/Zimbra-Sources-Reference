/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2014 Zimbra, Inc.
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
import generated.zcsclient.sync.testDeviceId;
import generated.zcsclient.zm.testAccountSelector;


/**
 * <p>Java class for getSyncStateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSyncStateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="account" type="{urn:zimbra}accountSelector"/>
 *         &lt;element name="device" type="{urn:zimbraSync}deviceId"/>
 *         &lt;element name="folder" type="{urn:zimbraAdmin}folderId" minOccurs="0"/>
 *         &lt;element name="item" type="{urn:zimbraAdmin}itemId" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSyncStateRequest", propOrder = {
    "account",
    "device",
    "folder",
    "item"
})
public class testGetSyncStateRequest {

    @XmlElement(required = true)
    protected testAccountSelector account;
    @XmlElement(required = true)
    protected testDeviceId device;
    protected testFolderId folder;
    protected testItemId item;

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
     * Gets the value of the device property.
     * 
     * @return
     *     possible object is
     *     {@link testDeviceId }
     *     
     */
    public testDeviceId getDevice() {
        return device;
    }

    /**
     * Sets the value of the device property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDeviceId }
     *     
     */
    public void setDevice(testDeviceId value) {
        this.device = value;
    }

    /**
     * Gets the value of the folder property.
     * 
     * @return
     *     possible object is
     *     {@link testFolderId }
     *     
     */
    public testFolderId getFolder() {
        return folder;
    }

    /**
     * Sets the value of the folder property.
     * 
     * @param value
     *     allowed object is
     *     {@link testFolderId }
     *     
     */
    public void setFolder(testFolderId value) {
        this.folder = value;
    }

    /**
     * Gets the value of the item property.
     * 
     * @return
     *     possible object is
     *     {@link testItemId }
     *     
     */
    public testItemId getItem() {
        return item;
    }

    /**
     * Sets the value of the item property.
     * 
     * @param value
     *     allowed object is
     *     {@link testItemId }
     *     
     */
    public void setItem(testItemId value) {
        this.item = value;
    }

}
