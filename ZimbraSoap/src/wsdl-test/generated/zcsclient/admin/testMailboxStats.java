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

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailboxStats complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailboxStats">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *       &lt;/all>
 *       &lt;attribute name="numMboxes" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="totalSize" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailboxStats", propOrder = {

})
public class testMailboxStats {

    @XmlAttribute(name = "numMboxes", required = true)
    protected long numMboxes;
    @XmlAttribute(name = "totalSize", required = true)
    protected long totalSize;

    /**
     * Gets the value of the numMboxes property.
     * 
     */
    public long getNumMboxes() {
        return numMboxes;
    }

    /**
     * Sets the value of the numMboxes property.
     * 
     */
    public void setNumMboxes(long value) {
        this.numMboxes = value;
    }

    /**
     * Gets the value of the totalSize property.
     * 
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Sets the value of the totalSize property.
     * 
     */
    public void setTotalSize(long value) {
        this.totalSize = value;
    }

}
