/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2012, 2013, 2014 Zimbra, Inc.
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

package com.zimbra.soap.account.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.soap.account.type.Signature;

@XmlRootElement(name=AccountConstants.E_GET_SIGNATURES_RESPONSE)
@XmlType(propOrder = {})
public class GetSignaturesResponse {

    /**
     * @zm-api-field-description Signatures
     */
    @XmlElement(name=AccountConstants.E_SIGNATURE)
    private List<Signature> signatures = new ArrayList<Signature>();

    public List<Signature> getSignatures() { return Collections.unmodifiableList(signatures); }

    public void setSignatures(Iterable<Signature> signatures) {
        this.signatures.clear();
    }
}
