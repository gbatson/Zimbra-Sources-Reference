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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testDistributionListSelector;


/**
 * <p>Java class for subscribeDistributionListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subscribeDistributionListRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dl" type="{urn:zimbra}distributionListSelector"/>
 *       &lt;/sequence>
 *       &lt;attribute name="op" use="required" type="{urn:zimbraAccount}distributionListSubscribeOp" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscribeDistributionListRequest", propOrder = {
    "dl"
})
public class testSubscribeDistributionListRequest {

    @XmlElement(required = true)
    protected testDistributionListSelector dl;
    @XmlAttribute(name = "op", required = true)
    protected testDistributionListSubscribeOp op;

    /**
     * Gets the value of the dl property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListSelector }
     *     
     */
    public testDistributionListSelector getDl() {
        return dl;
    }

    /**
     * Sets the value of the dl property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListSelector }
     *     
     */
    public void setDl(testDistributionListSelector value) {
        this.dl = value;
    }

    /**
     * Gets the value of the op property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListSubscribeOp }
     *     
     */
    public testDistributionListSubscribeOp getOp() {
        return op;
    }

    /**
     * Sets the value of the op property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListSubscribeOp }
     *     
     */
    public void setOp(testDistributionListSubscribeOp value) {
        this.op = value;
    }

}
