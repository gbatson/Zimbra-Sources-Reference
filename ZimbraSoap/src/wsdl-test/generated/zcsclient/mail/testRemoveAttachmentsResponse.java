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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for removeAttachmentsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="removeAttachmentsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="chat" type="{urn:zimbraMail}chatMessageInfo"/>
 *           &lt;element name="m" type="{urn:zimbraMail}messageInfo"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "removeAttachmentsResponse", propOrder = {
    "chat",
    "m"
})
public class testRemoveAttachmentsResponse {

    protected testChatMessageInfo chat;
    protected testMessageInfo m;

    /**
     * Gets the value of the chat property.
     * 
     * @return
     *     possible object is
     *     {@link testChatMessageInfo }
     *     
     */
    public testChatMessageInfo getChat() {
        return chat;
    }

    /**
     * Sets the value of the chat property.
     * 
     * @param value
     *     allowed object is
     *     {@link testChatMessageInfo }
     *     
     */
    public void setChat(testChatMessageInfo value) {
        this.chat = value;
    }

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testMessageInfo }
     *     
     */
    public testMessageInfo getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMessageInfo }
     *     
     */
    public void setM(testMessageInfo value) {
        this.m = value;
    }

}
