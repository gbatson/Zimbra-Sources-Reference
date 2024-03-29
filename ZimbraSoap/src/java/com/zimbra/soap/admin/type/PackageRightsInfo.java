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

package com.zimbra.soap.admin.type;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.admin.type.CmdRightsInfo;

@XmlAccessorType(XmlAccessType.NONE)
public class PackageRightsInfo {

    /**
     * @zm-api-field-tag name
     * @zm-api-field-description Name
     */
    @XmlAttribute(name=AdminConstants.A_NAME /* name */, required=false)
    private final String name;

    /**
     * @zm-api-field-description Command rights information
     */
    @XmlElement(name=AdminConstants.E_CMD /* cmd */, required=false)
    private List <CmdRightsInfo> cmds = Lists.newArrayList();

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private PackageRightsInfo() { this(null); }

    public PackageRightsInfo(String name) { this.name = name; }

    public PackageRightsInfo setCmds(Collection <CmdRightsInfo> cmds) {
        this.cmds.clear();
        if (cmds != null) {
            this.cmds.addAll(cmds);
        }
        return this;
    }

    public PackageRightsInfo addCmd(CmdRightsInfo cmd) {
        cmds.add(cmd);
        return this;
    }

    public List<CmdRightsInfo> getCmds() {
        return Collections.unmodifiableList(cmds);
    }

    public String getName() { return name; }
}
