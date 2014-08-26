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


/**
 * <p>Java class for adminZimletContext complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminZimletContext">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="baseUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="priority" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="presence" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminZimletContext")
public class testAdminZimletContext {

    @XmlAttribute(name = "baseUrl", required = true)
    protected String baseUrl;
    @XmlAttribute(name = "priority")
    protected Integer priority;
    @XmlAttribute(name = "presence", required = true)
    protected String presence;

    /**
     * Gets the value of the baseUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the value of the baseUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseUrl(String value) {
        this.baseUrl = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPriority(Integer value) {
        this.priority = value;
    }

    /**
     * Gets the value of the presence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPresence() {
        return presence;
    }

    /**
     * Sets the value of the presence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPresence(String value) {
        this.presence = value;
    }

}
