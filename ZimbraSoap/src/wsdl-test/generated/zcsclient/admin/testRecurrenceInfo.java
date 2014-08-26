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

package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for recurrenceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="recurrenceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="add" type="{urn:zimbraAdmin}addRecurrenceInfo"/>
 *           &lt;element name="exclude" type="{urn:zimbraAdmin}excludeRecurrenceInfo"/>
 *           &lt;element name="except" type="{urn:zimbraAdmin}exceptionRuleInfo"/>
 *           &lt;element name="cancel" type="{urn:zimbraAdmin}cancelRuleInfo"/>
 *           &lt;element name="dates" type="{urn:zimbraAdmin}singleDates"/>
 *           &lt;element name="rule" type="{urn:zimbraAdmin}simpleRepeatingRule"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recurrenceInfo", propOrder = {
    "addOrExcludeOrExcept"
})
@XmlSeeAlso({
    testAddRecurrenceInfo.class,
    testExcludeRecurrenceInfo.class
})
public class testRecurrenceInfo {

    @XmlElements({
        @XmlElement(name = "dates", type = testSingleDates.class),
        @XmlElement(name = "except", type = testExceptionRuleInfo.class),
        @XmlElement(name = "add", type = testAddRecurrenceInfo.class),
        @XmlElement(name = "exclude", type = testExcludeRecurrenceInfo.class),
        @XmlElement(name = "rule", type = testSimpleRepeatingRule.class),
        @XmlElement(name = "cancel", type = testCancelRuleInfo.class)
    })
    protected List<Object> addOrExcludeOrExcept;

    /**
     * Gets the value of the addOrExcludeOrExcept property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addOrExcludeOrExcept property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddOrExcludeOrExcept().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testSingleDates }
     * {@link testExceptionRuleInfo }
     * {@link testAddRecurrenceInfo }
     * {@link testExcludeRecurrenceInfo }
     * {@link testSimpleRepeatingRule }
     * {@link testCancelRuleInfo }
     * 
     * 
     */
    public List<Object> getAddOrExcludeOrExcept() {
        if (addOrExcludeOrExcept == null) {
            addOrExcludeOrExcept = new ArrayList<Object>();
        }
        return this.addOrExcludeOrExcept;
    }

}
