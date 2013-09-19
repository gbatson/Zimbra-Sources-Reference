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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for recalculateMailboxCountsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="recalculateMailboxCountsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraAdmin}mbox"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recalculateMailboxCountsResponse", propOrder = {
    "mbox"
})
public class testRecalculateMailboxCountsResponse {

    @XmlElement(required = true)
    protected testMailboxQuotaInfo mbox;

    /**
     * Gets the value of the mbox property.
     * 
     * @return
     *     possible object is
     *     {@link testMailboxQuotaInfo }
     *     
     */
    public testMailboxQuotaInfo getMbox() {
        return mbox;
    }

    /**
     * Sets the value of the mbox property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailboxQuotaInfo }
     *     
     */
    public void setMbox(testMailboxQuotaInfo value) {
        this.mbox = value;
    }

}
