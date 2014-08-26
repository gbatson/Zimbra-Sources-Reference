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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.admin.type.EffectiveRightsTargetSelector;
import com.zimbra.soap.admin.type.GranteeSelector;
import com.zimbra.soap.admin.type.RightModifierInfo;

/**
 * @zm-api-command-auth-required true
 * @zm-api-command-admin-auth-required true
 * @zm-api-command-description Revoke a right from a target that was previously granted to an individual or group
 * grantee.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=AdminConstants.E_REVOKE_RIGHT_REQUEST)
public class RevokeRightRequest {

    /**
     * @zm-api-field-description Target selector
     */
    @XmlElement(name=AdminConstants.E_TARGET, required=true)
    private final EffectiveRightsTargetSelector target;

    /**
     * @zm-api-field-description Grantee selector
     */
    @XmlElement(name=AdminConstants.E_GRANTEE, required=true)
    private final GranteeSelector grantee;

    /**
     * @zm-api-field-description Right
     */
    @XmlElement(name=AdminConstants.E_RIGHT, required=true)
    private final RightModifierInfo right;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private RevokeRightRequest() {
        this((EffectiveRightsTargetSelector) null, (GranteeSelector) null, (RightModifierInfo) null);
    }

    public RevokeRightRequest(EffectiveRightsTargetSelector target, GranteeSelector grantee, RightModifierInfo right) {
        this.target = target;
        this.grantee = grantee;
        this.right = right;
    }

    public EffectiveRightsTargetSelector getTarget() { return target; }
    public GranteeSelector getGrantee() { return grantee; }
    public RightModifierInfo getRight() { return right; }
}
