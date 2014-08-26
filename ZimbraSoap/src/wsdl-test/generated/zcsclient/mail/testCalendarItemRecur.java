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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for calendarItemRecur complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="calendarItemRecur">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="exceptId" type="{urn:zimbraMail}exceptionRecurIdInfo" minOccurs="0"/>
 *         &lt;element name="s" type="{urn:zimbraMail}dtTimeInfo" minOccurs="0"/>
 *         &lt;element name="e" type="{urn:zimbraMail}dtTimeInfo" minOccurs="0"/>
 *         &lt;element name="dur" type="{urn:zimbraMail}durationInfo" minOccurs="0"/>
 *         &lt;element name="recur" type="{urn:zimbraMail}recurrenceInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calendarItemRecur", propOrder = {
    "exceptId",
    "s",
    "e",
    "dur",
    "recur"
})
@XmlSeeAlso({
    testExceptionItemRecur.class,
    testInviteItemRecur.class,
    testCancelItemRecur.class
})
public class testCalendarItemRecur {

    protected testExceptionRecurIdInfo exceptId;
    protected testDtTimeInfo s;
    protected testDtTimeInfo e;
    protected testDurationInfo dur;
    protected testRecurrenceInfo recur;

    /**
     * Gets the value of the exceptId property.
     * 
     * @return
     *     possible object is
     *     {@link testExceptionRecurIdInfo }
     *     
     */
    public testExceptionRecurIdInfo getExceptId() {
        return exceptId;
    }

    /**
     * Sets the value of the exceptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link testExceptionRecurIdInfo }
     *     
     */
    public void setExceptId(testExceptionRecurIdInfo value) {
        this.exceptId = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link testDtTimeInfo }
     *     
     */
    public testDtTimeInfo getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDtTimeInfo }
     *     
     */
    public void setS(testDtTimeInfo value) {
        this.s = value;
    }

    /**
     * Gets the value of the e property.
     * 
     * @return
     *     possible object is
     *     {@link testDtTimeInfo }
     *     
     */
    public testDtTimeInfo getE() {
        return e;
    }

    /**
     * Sets the value of the e property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDtTimeInfo }
     *     
     */
    public void setE(testDtTimeInfo value) {
        this.e = value;
    }

    /**
     * Gets the value of the dur property.
     * 
     * @return
     *     possible object is
     *     {@link testDurationInfo }
     *     
     */
    public testDurationInfo getDur() {
        return dur;
    }

    /**
     * Sets the value of the dur property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDurationInfo }
     *     
     */
    public void setDur(testDurationInfo value) {
        this.dur = value;
    }

    /**
     * Gets the value of the recur property.
     * 
     * @return
     *     possible object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public testRecurrenceInfo getRecur() {
        return recur;
    }

    /**
     * Sets the value of the recur property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public void setRecur(testRecurrenceInfo value) {
        this.recur = value;
    }

}
