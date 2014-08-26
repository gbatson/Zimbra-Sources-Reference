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
 * <p>Java class for getDistributionListResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDistributionListResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraAdmin}dl" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="more" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDistributionListResponse", propOrder = {
    "dl"
})
public class testGetDistributionListResponse {

    protected testDistributionListInfo dl;
    @XmlAttribute(name = "more")
    protected Boolean more;
    @XmlAttribute(name = "total")
    protected Integer total;

    /**
     * Gets the value of the dl property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListInfo }
     *     
     */
    public testDistributionListInfo getDl() {
        return dl;
    }

    /**
     * Sets the value of the dl property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListInfo }
     *     
     */
    public void setDl(testDistributionListInfo value) {
        this.dl = value;
    }

    /**
     * Gets the value of the more property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMore(Boolean value) {
        this.more = value;
    }

    /**
     * Gets the value of the total property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotal(Integer value) {
        this.total = value;
    }

}
