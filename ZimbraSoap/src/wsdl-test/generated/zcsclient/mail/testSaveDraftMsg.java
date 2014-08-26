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
 * <p>Java class for saveDraftMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="saveDraftMsg">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}msg">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="forAcct" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="t" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tn" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rgb" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="color" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *       &lt;attribute name="autoSendTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "saveDraftMsg")
public class testSaveDraftMsg
    extends testMsg
{

    @XmlAttribute(name = "id")
    protected Integer id;
    @XmlAttribute(name = "forAcct")
    protected String forAcct;
    @XmlAttribute(name = "t")
    protected String t;
    @XmlAttribute(name = "tn")
    protected String tn;
    @XmlAttribute(name = "rgb")
    protected String rgb;
    @XmlAttribute(name = "color")
    protected Byte color;
    @XmlAttribute(name = "autoSendTime")
    protected Long autoSendTime;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the forAcct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForAcct() {
        return forAcct;
    }

    /**
     * Sets the value of the forAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForAcct(String value) {
        this.forAcct = value;
    }

    /**
     * Gets the value of the t property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getT() {
        return t;
    }

    /**
     * Sets the value of the t property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setT(String value) {
        this.t = value;
    }

    /**
     * Gets the value of the tn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTn() {
        return tn;
    }

    /**
     * Sets the value of the tn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTn(String value) {
        this.tn = value;
    }

    /**
     * Gets the value of the rgb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRgb() {
        return rgb;
    }

    /**
     * Sets the value of the rgb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRgb(String value) {
        this.rgb = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setColor(Byte value) {
        this.color = value;
    }

    /**
     * Gets the value of the autoSendTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAutoSendTime() {
        return autoSendTime;
    }

    /**
     * Sets the value of the autoSendTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAutoSendTime(Long value) {
        this.autoSendTime = value;
    }

}
