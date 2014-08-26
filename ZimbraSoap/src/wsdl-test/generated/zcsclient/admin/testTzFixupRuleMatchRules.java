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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tzFixupRuleMatchRules complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzFixupRuleMatchRules">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="standard" type="{urn:zimbraAdmin}tzFixupRuleMatchRule"/>
 *         &lt;element name="daylight" type="{urn:zimbraAdmin}tzFixupRuleMatchRule"/>
 *       &lt;/all>
 *       &lt;attribute name="stdoff" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="dayoff" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tzFixupRuleMatchRules", propOrder = {

})
public class testTzFixupRuleMatchRules {

    @XmlElement(required = true)
    protected testTzFixupRuleMatchRule standard;
    @XmlElement(required = true)
    protected testTzFixupRuleMatchRule daylight;
    @XmlAttribute(name = "stdoff", required = true)
    protected long stdoff;
    @XmlAttribute(name = "dayoff", required = true)
    protected long dayoff;

    /**
     * Gets the value of the standard property.
     * 
     * @return
     *     possible object is
     *     {@link testTzFixupRuleMatchRule }
     *     
     */
    public testTzFixupRuleMatchRule getStandard() {
        return standard;
    }

    /**
     * Sets the value of the standard property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTzFixupRuleMatchRule }
     *     
     */
    public void setStandard(testTzFixupRuleMatchRule value) {
        this.standard = value;
    }

    /**
     * Gets the value of the daylight property.
     * 
     * @return
     *     possible object is
     *     {@link testTzFixupRuleMatchRule }
     *     
     */
    public testTzFixupRuleMatchRule getDaylight() {
        return daylight;
    }

    /**
     * Sets the value of the daylight property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTzFixupRuleMatchRule }
     *     
     */
    public void setDaylight(testTzFixupRuleMatchRule value) {
        this.daylight = value;
    }

    /**
     * Gets the value of the stdoff property.
     * 
     */
    public long getStdoff() {
        return stdoff;
    }

    /**
     * Sets the value of the stdoff property.
     * 
     */
    public void setStdoff(long value) {
        this.stdoff = value;
    }

    /**
     * Gets the value of the dayoff property.
     * 
     */
    public long getDayoff() {
        return dayoff;
    }

    /**
     * Sets the value of the dayoff property.
     * 
     */
    public void setDayoff(long value) {
        this.dayoff = value;
    }

}
