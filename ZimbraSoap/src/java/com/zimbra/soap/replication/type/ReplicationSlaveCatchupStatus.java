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

package com.zimbra.soap.replication.type;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.zimbra.common.soap.ReplicationConstants;

@XmlAccessorType(XmlAccessType.NONE)
public class ReplicationSlaveCatchupStatus {

    /**
     * @zm-api-field-tag num-pending-redo-operations
     * @zm-api-field-description Number of pending redo operations
     */
    @XmlAttribute(name=ReplicationConstants.A_REMAINING_OPS /* remainingOps */, required=true)
    private final int remaingingOps;

    /**
     * @zm-api-field-tag num-remaining-files
     * @zm-api-field-description Number of remaining files
     */
    @XmlAttribute(name=ReplicationConstants.A_REMAINING_FILES /* remainingFiles */, required=true)
    private final int remaingingFiles;

    /**
     * @zm-api-field-tag num-remaining-bytes
     * @zm-api-field-description Number of remaining bytes
     */
    @XmlAttribute(name=ReplicationConstants.A_REMAINING_BYTES /* remainingBytes */, required=true)
    private final long remaingingBytes;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private ReplicationSlaveCatchupStatus() {
        this(-1, -1, -1L);
    }

    public ReplicationSlaveCatchupStatus(int remaingingOps, int remaingingFiles, long remaingingBytes) {
        this.remaingingOps = remaingingOps;
        this.remaingingFiles = remaingingFiles;
        this.remaingingBytes = remaingingBytes;
    }

    public int getRemaingingOps() { return remaingingOps; }
    public int getRemaingingFiles() { return remaingingFiles; }
    public long getRemaingingBytes() { return remaingingBytes; }

    public Objects.ToStringHelper addToStringInfo(Objects.ToStringHelper helper) {
        return helper
            .add("remaingingOps", remaingingOps)
            .add("remaingingFiles", remaingingFiles)
            .add("remaingingBytes", remaingingBytes);
    }

    @Override
    public String toString() {
        return addToStringInfo(Objects.toStringHelper(this)).toString();
    }
}
