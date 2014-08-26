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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testDistributionListSelector;


/**
 * <p>Java class for distributionListActionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="distributionListActionRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAccount}attrsImpl">
 *       &lt;sequence>
 *         &lt;element name="dl" type="{urn:zimbra}distributionListSelector"/>
 *         &lt;element name="action" type="{urn:zimbraAccount}distributionListAction"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionListActionRequest", propOrder = {
    "dl",
    "action"
})
public class testDistributionListActionRequest
    extends testAttrsImpl
{

    @XmlElement(required = true)
    protected testDistributionListSelector dl;
    @XmlElement(required = true)
    protected testDistributionListAction action;

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
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListAction }
     *     
     */
    public testDistributionListAction getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListAction }
     *     
     */
    public void setAction(testDistributionListAction value) {
        this.action = value;
    }

}
