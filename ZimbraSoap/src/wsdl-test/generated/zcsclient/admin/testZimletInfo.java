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
 * <p>Java class for zimletInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="zimletInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdmin}adminObjectInfo">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="hasKeyword" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "zimletInfo")
public class testZimletInfo
    extends testAdminObjectInfo
{

    @XmlAttribute(name = "hasKeyword")
    protected String hasKeyword;

    /**
     * Gets the value of the hasKeyword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHasKeyword() {
        return hasKeyword;
    }

    /**
     * Sets the value of the hasKeyword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHasKeyword(String value) {
        this.hasKeyword = value;
    }

}
