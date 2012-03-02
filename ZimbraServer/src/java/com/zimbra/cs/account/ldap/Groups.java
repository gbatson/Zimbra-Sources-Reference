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
package com.zimbra.cs.account.ldap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.Attributes;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Provisioning;

public class Groups {
    
    private LdapProvisioning mProv;
    private Set<String> mAllDLs = null; // email addresses of all distribution lists on the system
    
    private static class GetAllDLsVisitor implements LdapUtil.SearchLdapVisitor {
        Set<String> allDLs = new HashSet<String>();
        
        @Override
        public void visit(String dn, Map<String, Object> attrs, Attributes ldapAttrs) {
            Object addrs = attrs.get(Provisioning.A_mail);
            if (addrs instanceof String)
                allDLs.add(((String)addrs).toLowerCase());
            else if (addrs instanceof String[]) {
                for (String addr : (String[])addrs)
                    allDLs.add(addr.toLowerCase());
            }
        }
        
        private Set<String> getResult() {
            return allDLs;
        }
    }
    
    Groups(LdapProvisioning prov) {
        mProv = prov;
    }
    
    private synchronized Set<String> getAllDLs() throws ServiceException {
        if (mAllDLs == null) {
            try {
                GetAllDLsVisitor visitor = new GetAllDLsVisitor();
                LdapUtil.searchLdapOnReplica(mProv.getDIT().mailBranchBaseDN(), LdapFilter.allDistributionLists(),
                        new String[] {Provisioning.A_mail}, visitor);
                
                // all is well, swap in the result Set and cache it
                mAllDLs = Collections.synchronizedSet(visitor.getResult());
            } catch (ServiceException e) {
                ZimbraLog.account.error("unable to get all DLs", e);
            }
        }
        return mAllDLs;
    }
    
    void addGroup(DistributionList dl) {
        try {
            Set<String> allGroups = getAllDLs();
            for (String email : dl.getMultiAttrSet(Provisioning.A_mail))
                allGroups.add(email.toLowerCase());
        } catch (ServiceException e) {
            // ignore
        }
    }
    
    void removeGroup(Set<String> addrs) {
        try {
            Set<String> allGroups = getAllDLs();
            for (String email : addrs)
                allGroups.remove(email.toLowerCase());
        } catch (ServiceException e) {
            // ignore
        }
    }
    
    /**
     * returns if addr is a group (distribution list)
     * @param addr
     * @return
     */
    boolean isGroup(String addr) {
        boolean isGroup = false;
        try {
            isGroup = getAllDLs().contains(addr.toLowerCase());
        } catch (ServiceException e) {
            // log and ignore
            ZimbraLog.account.warn("unable to determine if address " + addr + " is a DL", e);
        }
        ZimbraLog.account.debug("address " + addr + " isGroup=" + isGroup);
        return isGroup;
    }
}
