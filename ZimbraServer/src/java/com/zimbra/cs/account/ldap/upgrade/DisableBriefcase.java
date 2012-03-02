/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.ldap.upgrade;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.LdapFilter;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;
import com.zimbra.cs.account.ldap.LdapUtil.SearchLdapVisitor;

public class DisableBriefcase extends LdapUpgrade {

    private static String ATTR_SPREADSHEET = Provisioning.A_zimbraFeatureBriefcaseSpreadsheetEnabled;
    private static String ATTR_SLIDES = Provisioning.A_zimbraFeatureBriefcaseSlidesEnabled;
    private static String ATTR_NOTEBOOK = Provisioning.A_zimbraFeatureNotebookEnabled;
    
    DisableBriefcase() throws ServiceException {
    }
    
    @Override
    void doUpgrade() throws ServiceException {
        ZimbraLdapContext zlc = new ZimbraLdapContext(true);
        try {
            doCos(zlc);
            doAccount(zlc);
        } finally {
            ZimbraLdapContext.closeContext(zlc);
        }

    }
    
    private static class DisableBriefcaseVisitor implements SearchLdapVisitor {
        private ZimbraLdapContext mModZlc;
        
        DisableBriefcaseVisitor(ZimbraLdapContext modZlc) {
            mModZlc = modZlc;
        }
        
        public void visit(String dn, Map<String, Object> attrs, Attributes ldapAttrs) {
            Attributes modAttrs = new BasicAttributes(true);
            
            try {
                if (LdapUtil.getAttrString(ldapAttrs, ATTR_SPREADSHEET) != null)
                    modAttrs.put(ATTR_SPREADSHEET, LdapUtil.LDAP_FALSE);
                
                if (LdapUtil.getAttrString(ldapAttrs, ATTR_SLIDES) != null)
                    modAttrs.put(ATTR_SLIDES, LdapUtil.LDAP_FALSE);
                
                if (LdapUtil.getAttrString(ldapAttrs, ATTR_NOTEBOOK) != null)
                    modAttrs.put(ATTR_NOTEBOOK, LdapUtil.LDAP_FALSE);
                
                if (modAttrs.size() > 0) {
                    System.out.println("Modifying " + dn);
                    mModZlc.replaceAttributes(dn, modAttrs);
                }
            } catch (NamingException e) {
                // log and continue
                System.out.println("Caught NamingException while modifying " + dn);
                e.printStackTrace();
            }
        }
    }
    
    private void upgrade(ZimbraLdapContext modZlc, String bases[], String query) {
        SearchLdapVisitor visitor = new DisableBriefcaseVisitor(modZlc);

        String attrs[] = new String[] {ATTR_SPREADSHEET, ATTR_SLIDES, ATTR_NOTEBOOK};
        
        for (String base : bases) {
            try {
                LdapUtil.searchLdapOnMaster(base, query, attrs, visitor);
            } catch (ServiceException e) {
                // log and continue
                System.out.println("Caught ServiceException while searching " + query + " under base " + base);
                e.printStackTrace();
            }
        }
    }
    
    private String query() {
        return "(|(" + ATTR_SPREADSHEET + "=" + LdapUtil.LDAP_TRUE + ")" + 
                 "(" + ATTR_SLIDES + "=" + LdapUtil.LDAP_TRUE + ")" + 
                 "(" + ATTR_NOTEBOOK + "=" + LdapUtil.LDAP_TRUE + ")" +
               ")";
    }
    
    private void doCos(ZimbraLdapContext modZlc) {
        String bases[] = mProv.getSearchBases(Provisioning.SD_COS_FLAG);
        String query = "(&" + LdapFilter.allCoses() + query() + ")";
        upgrade(modZlc, bases, query);
    }
    
    private void doAccount(ZimbraLdapContext modZlc) {
        String bases[] = mProv.getSearchBases(Provisioning.SA_ACCOUNT_FLAG);
        String query = "(&" + LdapFilter.allAccounts() + query() + ")";
        upgrade(modZlc, bases, query);
    }
}
