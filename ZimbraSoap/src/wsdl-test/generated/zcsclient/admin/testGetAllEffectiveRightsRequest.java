/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAllEffectiveRightsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAllEffectiveRightsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="expandAllAttrs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllEffectiveRightsRequest", propOrder = {
    "grantee"
})
public class testGetAllEffectiveRightsRequest {

    protected testGranteeSelector grantee;
    @XmlAttribute(name = "expandAllAttrs")
    protected String expandAllAttrs;

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeSelector }
     *     
     */
    public testGranteeSelector getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeSelector }
     *     
     */
    public void setGrantee(testGranteeSelector value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the expandAllAttrs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpandAllAttrs() {
        return expandAllAttrs;
    }

    /**
     * Sets the value of the expandAllAttrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpandAllAttrs(String value) {
        this.expandAllAttrs = value;
    }

}
