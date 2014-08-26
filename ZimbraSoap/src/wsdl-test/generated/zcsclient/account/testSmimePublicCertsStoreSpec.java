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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import generated.zcsclient.zm.testSourceLookupOpt;
import generated.zcsclient.zm.testStoreLookupOpt;


/**
 * <p>Java class for smimePublicCertsStoreSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="smimePublicCertsStoreSpec">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="storeLookupOpt" type="{urn:zimbra}storeLookupOpt" />
 *       &lt;attribute name="sourceLookupOpt" type="{urn:zimbra}sourceLookupOpt" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "smimePublicCertsStoreSpec", propOrder = {
    "value"
})
public class testSmimePublicCertsStoreSpec {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "storeLookupOpt")
    protected testStoreLookupOpt storeLookupOpt;
    @XmlAttribute(name = "sourceLookupOpt")
    protected testSourceLookupOpt sourceLookupOpt;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the storeLookupOpt property.
     * 
     * @return
     *     possible object is
     *     {@link testStoreLookupOpt }
     *     
     */
    public testStoreLookupOpt getStoreLookupOpt() {
        return storeLookupOpt;
    }

    /**
     * Sets the value of the storeLookupOpt property.
     * 
     * @param value
     *     allowed object is
     *     {@link testStoreLookupOpt }
     *     
     */
    public void setStoreLookupOpt(testStoreLookupOpt value) {
        this.storeLookupOpt = value;
    }

    /**
     * Gets the value of the sourceLookupOpt property.
     * 
     * @return
     *     possible object is
     *     {@link testSourceLookupOpt }
     *     
     */
    public testSourceLookupOpt getSourceLookupOpt() {
        return sourceLookupOpt;
    }

    /**
     * Sets the value of the sourceLookupOpt property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSourceLookupOpt }
     *     
     */
    public void setSourceLookupOpt(testSourceLookupOpt value) {
        this.sourceLookupOpt = value;
    }

}
