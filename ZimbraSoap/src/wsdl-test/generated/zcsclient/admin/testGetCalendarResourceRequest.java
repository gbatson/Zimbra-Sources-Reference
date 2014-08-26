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
 * <p>Java class for getCalendarResourceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCalendarResourceRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="calresource" type="{urn:zimbraAdmin}calendarResourceSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="applyCos" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCalendarResourceRequest", propOrder = {
    "calresource"
})
public class testGetCalendarResourceRequest
    extends testAttributeSelectorImpl
{

    protected testCalendarResourceSelector calresource;
    @XmlAttribute(name = "applyCos")
    protected Boolean applyCos;

    /**
     * Gets the value of the calresource property.
     * 
     * @return
     *     possible object is
     *     {@link testCalendarResourceSelector }
     *     
     */
    public testCalendarResourceSelector getCalresource() {
        return calresource;
    }

    /**
     * Sets the value of the calresource property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalendarResourceSelector }
     *     
     */
    public void setCalresource(testCalendarResourceSelector value) {
        this.calresource = value;
    }

    /**
     * Gets the value of the applyCos property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isApplyCos() {
        return applyCos;
    }

    /**
     * Sets the value of the applyCos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setApplyCos(Boolean value) {
        this.applyCos = value;
    }

}
