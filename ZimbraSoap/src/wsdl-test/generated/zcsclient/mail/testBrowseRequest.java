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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for browseRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="browseRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="browseBy" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="regex" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxToReturn" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "browseRequest")
public class testBrowseRequest {

    @XmlAttribute(name = "browseBy", required = true)
    protected String browseBy;
    @XmlAttribute(name = "regex")
    protected String regex;
    @XmlAttribute(name = "maxToReturn")
    protected Integer maxToReturn;

    /**
     * Gets the value of the browseBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowseBy() {
        return browseBy;
    }

    /**
     * Sets the value of the browseBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowseBy(String value) {
        this.browseBy = value;
    }

    /**
     * Gets the value of the regex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Sets the value of the regex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegex(String value) {
        this.regex = value;
    }

    /**
     * Gets the value of the maxToReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxToReturn() {
        return maxToReturn;
    }

    /**
     * Sets the value of the maxToReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxToReturn(Integer value) {
        this.maxToReturn = value;
    }

}
