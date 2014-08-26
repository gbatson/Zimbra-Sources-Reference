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

package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cursorInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cursorInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sortVal" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="endSortVal" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="includeOffset" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cursorInfo")
public class testCursorInfo {

    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "sortVal")
    protected String sortVal;
    @XmlAttribute(name = "endSortVal")
    protected String endSortVal;
    @XmlAttribute(name = "includeOffset")
    protected Boolean includeOffset;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the sortVal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortVal() {
        return sortVal;
    }

    /**
     * Sets the value of the sortVal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortVal(String value) {
        this.sortVal = value;
    }

    /**
     * Gets the value of the endSortVal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndSortVal() {
        return endSortVal;
    }

    /**
     * Sets the value of the endSortVal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndSortVal(String value) {
        this.endSortVal = value;
    }

    /**
     * Gets the value of the includeOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeOffset() {
        return includeOffset;
    }

    /**
     * Sets the value of the includeOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeOffset(Boolean value) {
        this.includeOffset = value;
    }

}
