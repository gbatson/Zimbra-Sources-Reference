/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.ldaputils;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.ldap.LdapUtil;
/**
 * @author Greg Solovyev
 */
public class PosixAccount extends LDAPUtilEntry {
	private static final String A_uidNumber = "uidNumber";
	public PosixAccount(String dn, Attributes attrs,
			Map<String, Object> defaults) throws NamingException {
		super(dn, attrs, defaults);
        mId = LdapUtil.getAttrString(attrs, A_uidNumber);
	}

    public String getId() {
        return getAttr(A_uidNumber);
    }
}
