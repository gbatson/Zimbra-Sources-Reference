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
 * <p>Java class for noOpRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="noOpRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="wait" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="delegate" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="limitToOneBlocked" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="timeout" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "noOpRequest")
public class testNoOpRequest {

    @XmlAttribute(name = "wait")
    protected Boolean wait;
    @XmlAttribute(name = "delegate")
    protected Boolean delegate;
    @XmlAttribute(name = "limitToOneBlocked")
    protected Boolean limitToOneBlocked;
    @XmlAttribute(name = "timeout")
    protected Long timeout;

    /**
     * Gets the value of the wait property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWait() {
        return wait;
    }

    /**
     * Sets the value of the wait property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWait(Boolean value) {
        this.wait = value;
    }

    /**
     * Gets the value of the delegate property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDelegate() {
        return delegate;
    }

    /**
     * Sets the value of the delegate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDelegate(Boolean value) {
        this.delegate = value;
    }

    /**
     * Gets the value of the limitToOneBlocked property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLimitToOneBlocked() {
        return limitToOneBlocked;
    }

    /**
     * Sets the value of the limitToOneBlocked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLimitToOneBlocked(Boolean value) {
        this.limitToOneBlocked = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTimeout(Long value) {
        this.timeout = value;
    }

}
