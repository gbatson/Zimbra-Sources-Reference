/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013, 2014 Zimbra, Inc.
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

package generated.zcsclient.voice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resetPhoneVoiceFeaturesSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resetPhoneVoiceFeaturesSpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="anoncallrejection" type="{urn:zimbraVoice}anonCallRejectionReq"/>
 *           &lt;element name="calleridblocking" type="{urn:zimbraVoice}callerIdBlockingReq"/>
 *           &lt;element name="callforward" type="{urn:zimbraVoice}callForwardReq"/>
 *           &lt;element name="callforwardbusyline" type="{urn:zimbraVoice}callForwardBusyLineReq"/>
 *           &lt;element name="callforwardnoanswer" type="{urn:zimbraVoice}callForwardNoAnswerReq"/>
 *           &lt;element name="callwaiting" type="{urn:zimbraVoice}callWaitingReq"/>
 *           &lt;element name="selectivecallforward" type="{urn:zimbraVoice}selectiveCallForwardReq"/>
 *           &lt;element name="selectivecallacceptance" type="{urn:zimbraVoice}selectiveCallAcceptanceReq"/>
 *           &lt;element name="selectivecallrejection" type="{urn:zimbraVoice}selectiveCallRejectionReq"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resetPhoneVoiceFeaturesSpec", propOrder = {
    "anoncallrejectionOrCalleridblockingOrCallforward"
})
public class testResetPhoneVoiceFeaturesSpec {

    @XmlElements({
        @XmlElement(name = "selectivecallacceptance", type = testSelectiveCallAcceptanceReq.class),
        @XmlElement(name = "selectivecallforward", type = testSelectiveCallForwardReq.class),
        @XmlElement(name = "selectivecallrejection", type = testSelectiveCallRejectionReq.class),
        @XmlElement(name = "callforwardbusyline", type = testCallForwardBusyLineReq.class),
        @XmlElement(name = "anoncallrejection", type = testAnonCallRejectionReq.class),
        @XmlElement(name = "callforwardnoanswer", type = testCallForwardNoAnswerReq.class),
        @XmlElement(name = "calleridblocking", type = testCallerIdBlockingReq.class),
        @XmlElement(name = "callforward", type = testCallForwardReq.class),
        @XmlElement(name = "callwaiting", type = testCallWaitingReq.class)
    })
    protected List<Object> anoncallrejectionOrCalleridblockingOrCallforward;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the anoncallrejectionOrCalleridblockingOrCallforward property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the anoncallrejectionOrCalleridblockingOrCallforward property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnoncallrejectionOrCalleridblockingOrCallforward().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testSelectiveCallAcceptanceReq }
     * {@link testSelectiveCallForwardReq }
     * {@link testSelectiveCallRejectionReq }
     * {@link testCallForwardBusyLineReq }
     * {@link testAnonCallRejectionReq }
     * {@link testCallForwardNoAnswerReq }
     * {@link testCallerIdBlockingReq }
     * {@link testCallForwardReq }
     * {@link testCallWaitingReq }
     * 
     * 
     */
    public List<Object> getAnoncallrejectionOrCalleridblockingOrCallforward() {
        if (anoncallrejectionOrCalleridblockingOrCallforward == null) {
            anoncallrejectionOrCalleridblockingOrCallforward = new ArrayList<Object>();
        }
        return this.anoncallrejectionOrCalleridblockingOrCallforward;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
