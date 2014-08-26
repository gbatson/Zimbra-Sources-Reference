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

package generated.zcsclient.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for replicationMasterCatchupStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationMasterCatchupStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="remainingFiles" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="remainingBytes" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicationMasterCatchupStatus")
public class testReplicationMasterCatchupStatus {

    @XmlAttribute(name = "remainingFiles", required = true)
    protected int remainingFiles;
    @XmlAttribute(name = "remainingBytes", required = true)
    protected long remainingBytes;

    /**
     * Gets the value of the remainingFiles property.
     * 
     */
    public int getRemainingFiles() {
        return remainingFiles;
    }

    /**
     * Sets the value of the remainingFiles property.
     * 
     */
    public void setRemainingFiles(int value) {
        this.remainingFiles = value;
    }

    /**
     * Gets the value of the remainingBytes property.
     * 
     */
    public long getRemainingBytes() {
        return remainingBytes;
    }

    /**
     * Sets the value of the remainingBytes property.
     * 
     */
    public void setRemainingBytes(long value) {
        this.remainingBytes = value;
    }

}
