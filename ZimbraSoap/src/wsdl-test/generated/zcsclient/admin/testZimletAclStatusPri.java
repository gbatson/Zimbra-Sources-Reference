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

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for zimletAclStatusPri complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="zimletAclStatusPri">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acl" type="{urn:zimbraAdmin}zimletAcl" minOccurs="0"/>
 *         &lt;element name="status" type="{urn:zimbraAdmin}valueAttrib" minOccurs="0"/>
 *         &lt;element name="priority" type="{urn:zimbraAdmin}integerValueAttrib" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "zimletAclStatusPri", propOrder = {
    "acl",
    "status",
    "priority"
})
public class testZimletAclStatusPri {

    protected testZimletAcl acl;
    protected testValueAttrib status;
    protected testIntegerValueAttrib priority;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the acl property.
     * 
     * @return
     *     possible object is
     *     {@link testZimletAcl }
     *     
     */
    public testZimletAcl getAcl() {
        return acl;
    }

    /**
     * Sets the value of the acl property.
     * 
     * @param value
     *     allowed object is
     *     {@link testZimletAcl }
     *     
     */
    public void setAcl(testZimletAcl value) {
        this.acl = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link testValueAttrib }
     *     
     */
    public testValueAttrib getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link testValueAttrib }
     *     
     */
    public void setStatus(testValueAttrib value) {
        this.status = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link testIntegerValueAttrib }
     *     
     */
    public testIntegerValueAttrib getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link testIntegerValueAttrib }
     *     
     */
    public void setPriority(testIntegerValueAttrib value) {
        this.priority = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
