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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testTrueOrFalse;


/**
 * <p>Java class for callFeatureInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="callFeatureInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="s" use="required" type="{urn:zimbra}trueOrFalse" />
 *       &lt;attribute name="a" use="required" type="{urn:zimbra}trueOrFalse" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "callFeatureInfo")
@XmlSeeAlso({
    testAnonCallRejectionFeature.class,
    testCallerIdBlockingFeature.class,
    testFeatureWithCallerList.class,
    testVoiceMailPrefsFeature.class,
    testCallForwardBusyLineFeature.class,
    testCallWaitingFeature.class,
    testCallForwardNoAnswerFeature.class,
    testCallForwardFeature.class
})
public abstract class testCallFeatureInfo {

    @XmlAttribute(name = "s", required = true)
    protected testTrueOrFalse s;
    @XmlAttribute(name = "a", required = true)
    protected testTrueOrFalse a;

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link testTrueOrFalse }
     *     
     */
    public testTrueOrFalse getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTrueOrFalse }
     *     
     */
    public void setS(testTrueOrFalse value) {
        this.s = value;
    }

    /**
     * Gets the value of the a property.
     * 
     * @return
     *     possible object is
     *     {@link testTrueOrFalse }
     *     
     */
    public testTrueOrFalse getA() {
        return a;
    }

    /**
     * Sets the value of the a property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTrueOrFalse }
     *     
     */
    public void setA(testTrueOrFalse value) {
        this.a = value;
    }

}
