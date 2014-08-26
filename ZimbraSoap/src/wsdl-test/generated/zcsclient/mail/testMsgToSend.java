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

package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for msgToSend complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="msgToSend">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}msg">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="did" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sfd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "msgToSend")
public class testMsgToSend
    extends testMsg
{

    @XmlAttribute(name = "did")
    protected String did;
    @XmlAttribute(name = "sfd")
    protected Boolean sfd;

    /**
     * Gets the value of the did property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDid() {
        return did;
    }

    /**
     * Sets the value of the did property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDid(String value) {
        this.did = value;
    }

    /**
     * Gets the value of the sfd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSfd() {
        return sfd;
    }

    /**
     * Sets the value of the sfd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSfd(Boolean value) {
        this.sfd = value;
    }

}
