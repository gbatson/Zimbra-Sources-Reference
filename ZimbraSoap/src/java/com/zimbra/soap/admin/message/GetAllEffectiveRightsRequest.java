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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.admin.type.GranteeSelector;

// soap-right.txt has a fairly complex description with examples but I think it isn't needed with
// the autogenerated XML style description.
/**
 * @zm-api-command-auth-required true
 * @zm-api-command-admin-auth-required true
 * @zm-api-command-description Get all effective <b>Admin</b> rights
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=AdminConstants.E_GET_ALL_EFFECTIVE_RIGHTS_REQUEST)
public class GetAllEffectiveRightsRequest {

    public static final String EXPAND_GET_ATTRS = "getAttrs";
    public static final String EXPAND_SET_ATTRS = "setAttrs";

    private static Splitter COMMA_SPLITTER = Splitter.on(",");
    private static Joiner COMMA_JOINER = Joiner.on(",");

    @XmlTransient
    private Boolean expandGetAttrs;
    @XmlTransient
    private Boolean expandSetAttrs;

    /**
     * @zm-api-field-description Grantee
     */
    @XmlElement(name=AdminConstants.E_GRANTEE, required=false)
    private GranteeSelector grantee;

    public GetAllEffectiveRightsRequest() {
        this((GranteeSelector) null, (Boolean) null, (Boolean) null);
    }

    public GetAllEffectiveRightsRequest(GranteeSelector grantee,
            Boolean expandSetAttrs, Boolean expandGetAttrs) {
        setGrantee(grantee);
        setExpandSetAttrs(expandSetAttrs);
        setExpandGetAttrs(expandGetAttrs);
    }

    /**
     * @zm-api-field-tag expand-all-attrs
     * @zm-api-field-description Flags whether to include all attribute names if the right is meant for all attributes
     */
    @XmlAttribute(name=AdminConstants.A_EXPAND_ALL_ATTRS /* expandAllAttrs */, required=false)
    public String getExpandAllAttrs() {
        List <String> settings = Lists.newArrayList();
        if ((expandSetAttrs != null) && expandSetAttrs)
            settings.add(EXPAND_SET_ATTRS);
        if ((expandGetAttrs != null) && expandGetAttrs)
            settings.add(EXPAND_GET_ATTRS);
        String retVal = COMMA_JOINER.join(settings);
        if (retVal.length() == 0)
            return null;
        else
            return retVal;
    }

    public void setExpandAllAttrs(String types)
    throws ServiceException {
        expandGetAttrs = null;
        expandSetAttrs = null;
        for (String typeString : COMMA_SPLITTER.split(types)) {
            String exp = typeString.trim();
            if (exp.equals(EXPAND_SET_ATTRS))
                expandSetAttrs = true;
            else if (exp.equals(EXPAND_GET_ATTRS))
                expandGetAttrs = true;
            else
                throw ServiceException.INVALID_REQUEST(
                    "invalid " + AdminConstants.A_EXPAND_ALL_ATTRS +
                    " value: " + exp, null);
        }
    }

    public void setExpandGetAttrs(Boolean expandGetAttrs) {
        this.expandGetAttrs = expandGetAttrs;
    }

    public Boolean getExpandGetAttrs() { return expandGetAttrs; }

    public void setExpandSetAttrs(Boolean expandSetAttrs) {
        this.expandSetAttrs = expandSetAttrs;
    }

    public Boolean getExpandSetAttrs() { return expandSetAttrs; }

    public void setGrantee(GranteeSelector grantee) {
        this.grantee = grantee;
    }

    public GranteeSelector getGrantee() { return grantee; }
}
