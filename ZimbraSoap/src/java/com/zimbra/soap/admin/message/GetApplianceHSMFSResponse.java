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

package com.zimbra.soap.admin.message;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.HsmConstants;
import com.zimbra.soap.admin.type.HsmFileSystemInfo;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=HsmConstants.E_GET_APPLIANCE_HSM_FS_RESPONSE)
@XmlType(propOrder = {})
public class GetApplianceHSMFSResponse {

    /**
     * @zm-api-field-description HSM filesystem information
     */
    @XmlElement(name=HsmConstants.E_FS /* fs */, required=false)
    private List<HsmFileSystemInfo> fileSystems = Lists.newArrayList();

    public GetApplianceHSMFSResponse() {
    }

    public void setFileSystems(Iterable <HsmFileSystemInfo> fileSystems) {
        this.fileSystems.clear();
        if (fileSystems != null) {
            Iterables.addAll(this.fileSystems,fileSystems);
        }
    }

    public void addFileSystem(HsmFileSystemInfo fileSystem) {
        this.fileSystems.add(fileSystem);
    }

    public List<HsmFileSystemInfo> getFileSystems() {
        return Collections.unmodifiableList(fileSystems);
    }

    public Objects.ToStringHelper addToStringInfo(
                Objects.ToStringHelper helper) {
        return helper
            .add("fileSystems", fileSystems);
    }

    @Override
    public String toString() {
        return addToStringInfo(Objects.toStringHelper(this))
                .toString();
    }
}
