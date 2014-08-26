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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alarmTriggerInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alarmTriggerInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="abs" type="{urn:zimbraMail}dateAttr" minOccurs="0"/>
 *         &lt;element name="rel" type="{urn:zimbraMail}durationInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alarmTriggerInfo", propOrder = {
    "abs",
    "rel"
})
public class testAlarmTriggerInfo {

    protected testDateAttr abs;
    protected testDurationInfo rel;

    /**
     * Gets the value of the abs property.
     * 
     * @return
     *     possible object is
     *     {@link testDateAttr }
     *     
     */
    public testDateAttr getAbs() {
        return abs;
    }

    /**
     * Sets the value of the abs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDateAttr }
     *     
     */
    public void setAbs(testDateAttr value) {
        this.abs = value;
    }

    /**
     * Gets the value of the rel property.
     * 
     * @return
     *     possible object is
     *     {@link testDurationInfo }
     *     
     */
    public testDurationInfo getRel() {
        return rel;
    }

    /**
     * Sets the value of the rel property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDurationInfo }
     *     
     */
    public void setRel(testDurationInfo value) {
        this.rel = value;
    }

}
