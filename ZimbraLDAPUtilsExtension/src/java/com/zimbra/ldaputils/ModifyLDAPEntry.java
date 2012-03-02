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
import javax.naming.directory.DirContext;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
/**
 * @author Greg Solovyev
 */
public class ModifyLDAPEntry extends AdminDocumentHandler {

	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {

		ZimbraSoapContext lc = getZimbraSoapContext(context);
		ZimbraLdapContext zlc = null;
		zlc = new ZimbraLdapContext(true);

		String dn = request.getAttribute(ZimbraLDAPUtilsService.E_DN);
		if(dn==null)
			throw ServiceException.INVALID_REQUEST("Missing request parameter: "+ZimbraLDAPUtilsService.E_DN, null);
		
		Map<String, Object> attrs = AdminService.getAttrs(request);

		try {
			LDAPUtilEntry ne = GetLDAPEntries.getObjectByDN(dn, zlc);
			if(ne==null)
				throw ServiceException.FAILURE("Cannot find an object for DN "+dn, null);
			
			LdapUtil.modifyAttrs(zlc, ne.getDN(), attrs, ne);

			ZimbraLog.security.info(ZimbraLog.encodeAttrs(new String[] { "cmd",
					"SaveLDAPEntry", "dn", dn }, attrs));
			
			LDAPUtilEntry newNe = GetLDAPEntries.getObjectByDN(dn, zlc);
			Element response = lc.createElement(ZimbraLDAPUtilsService.MODIFY_LDAP_ENTRY_RESPONSE);
			ZimbraLDAPUtilsService.encodeLDAPEntry(response, newNe);

			return response;

		} catch (ServiceException e) {
            throw ServiceException.FAILURE("unable to modify attrs: "
                    + e.getMessage(), e);
		} catch (NamingException e) {
            throw ServiceException.FAILURE("unable to modify attrs: "
                    + e.getMessage(), e);
        } finally {
            ZimbraLdapContext.closeContext(zlc);
        }

	}
}
