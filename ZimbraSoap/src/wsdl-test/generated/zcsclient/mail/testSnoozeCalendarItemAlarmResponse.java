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

package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for snoozeCalendarItemAlarmResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="snoozeCalendarItemAlarmResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="appt" type="{urn:zimbraMail}updatedAppointmentAlarmInfo"/>
 *           &lt;element name="task" type="{urn:zimbraMail}updatedTaskAlarmInfo"/>
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
@XmlType(name = "snoozeCalendarItemAlarmResponse", propOrder = {
    "apptOrTask"
})
public class testSnoozeCalendarItemAlarmResponse {

    @XmlElements({
        @XmlElement(name = "task", type = testUpdatedTaskAlarmInfo.class),
        @XmlElement(name = "appt", type = testUpdatedAppointmentAlarmInfo.class)
    })
    protected List<testUpdatedAlarmInfo> apptOrTask;

    /**
     * Gets the value of the apptOrTask property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the apptOrTask property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApptOrTask().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testUpdatedTaskAlarmInfo }
     * {@link testUpdatedAppointmentAlarmInfo }
     * 
     * 
     */
    public List<testUpdatedAlarmInfo> getApptOrTask() {
        if (apptOrTask == null) {
            apptOrTask = new ArrayList<testUpdatedAlarmInfo>();
        }
        return this.apptOrTask;
    }

}
