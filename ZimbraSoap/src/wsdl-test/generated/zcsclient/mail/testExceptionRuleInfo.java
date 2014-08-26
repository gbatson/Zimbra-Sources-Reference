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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for exceptionRuleInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="exceptionRuleInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}recurIdInfo">
 *       &lt;sequence>
 *         &lt;element name="add" type="{urn:zimbraMail}recurrenceInfo" minOccurs="0"/>
 *         &lt;element name="exclude" type="{urn:zimbraMail}recurrenceInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exceptionRuleInfo", propOrder = {
    "add",
    "exclude"
})
public class testExceptionRuleInfo
    extends testRecurIdInfo
{

    protected testRecurrenceInfo add;
    protected testRecurrenceInfo exclude;

    /**
     * Gets the value of the add property.
     * 
     * @return
     *     possible object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public testRecurrenceInfo getAdd() {
        return add;
    }

    /**
     * Sets the value of the add property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public void setAdd(testRecurrenceInfo value) {
        this.add = value;
    }

    /**
     * Gets the value of the exclude property.
     * 
     * @return
     *     possible object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public testRecurrenceInfo getExclude() {
        return exclude;
    }

    /**
     * Sets the value of the exclude property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public void setExclude(testRecurrenceInfo value) {
        this.exclude = value;
    }

}
