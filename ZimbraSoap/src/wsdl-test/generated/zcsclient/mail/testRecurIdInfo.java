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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for recurIdInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="recurIdInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="rangeType" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="recurId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tz" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ridZ" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recurIdInfo")
@XmlSeeAlso({
    testExceptionRuleInfo.class,
    testCalReply.class,
    testCalendarReply.class,
    testCancelRuleInfo.class
})
public class testRecurIdInfo {

    @XmlAttribute(name = "rangeType", required = true)
    protected int rangeType;
    @XmlAttribute(name = "recurId", required = true)
    protected String recurId;
    @XmlAttribute(name = "tz")
    protected String tz;
    @XmlAttribute(name = "ridZ")
    protected String ridZ;

    /**
     * Gets the value of the rangeType property.
     * 
     */
    public int getRangeType() {
        return rangeType;
    }

    /**
     * Sets the value of the rangeType property.
     * 
     */
    public void setRangeType(int value) {
        this.rangeType = value;
    }

    /**
     * Gets the value of the recurId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecurId() {
        return recurId;
    }

    /**
     * Sets the value of the recurId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecurId(String value) {
        this.recurId = value;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTz(String value) {
        this.tz = value;
    }

    /**
     * Gets the value of the ridZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRidZ() {
        return ridZ;
    }

    /**
     * Sets the value of the ridZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRidZ(String value) {
        this.ridZ = value;
    }

}
