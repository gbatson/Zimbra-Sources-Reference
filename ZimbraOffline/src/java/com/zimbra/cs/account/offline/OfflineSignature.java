/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.cs.account.offline;

import java.util.Map;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Signature;

class OfflineSignature extends Signature {
    OfflineSignature(Account acct, String name, Map<String,Object> attrs, Provisioning prov) {
        super(acct, name, (String)attrs.get(Provisioning.A_zimbraSignatureId), attrs, prov);
    }
    
    OfflineSignature(Account acct, Map<String,Object> attrs, Provisioning prov) {
        super(acct, (String)attrs.get(Provisioning.A_zimbraSignatureName), (String)attrs.get(Provisioning.A_zimbraSignatureId), attrs, prov);
    }

    void setName(String name) {
        mName = name;
    }
}
