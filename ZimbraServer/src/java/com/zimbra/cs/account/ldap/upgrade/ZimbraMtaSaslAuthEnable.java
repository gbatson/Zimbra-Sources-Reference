/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.ldap.upgrade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeClass;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;

public class ZimbraMtaSaslAuthEnable extends LdapUpgrade {
    
    ZimbraMtaSaslAuthEnable() throws ServiceException {
    }
    
    @Override
    void doUpgrade() throws ServiceException {
        ZimbraLdapContext zlc = new ZimbraLdapContext(true);
        try {
            doGlobalConfig(zlc);
            doAllServers(zlc);
        } finally {
            ZimbraLdapContext.closeContext(zlc);
        }

    }
    
    private void doEntry(ZimbraLdapContext zlc, Entry entry, String entryName) throws ServiceException {
        
        String attrName = Provisioning.A_zimbraMtaSaslAuthEnable;
        
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("Checking " + attrName + " on " + entryName);
        
        String curValue = entry.getAttr(attrName, false);
        if (curValue != null) {
            String newValue =LdapUtil.LDAP_FALSE.equals(curValue) ? "no" : "yes";
            if (!curValue.equals(newValue)) {
                System.out.println("    Changing " + attrName + " on " + entryName + " from " + curValue + " to " + newValue);
                
                Map<String, Object> attr = new HashMap<String, Object>();
                attr.put(attrName, newValue);
                try {
                    LdapUpgrade.modifyAttrs(entry, zlc, attr);
                } catch (NamingException e) {
                    // log the exception and continue
                    System.out.println("Caught NamingException while modifying " + entryName + " attribute " + attr);
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void doGlobalConfig(ZimbraLdapContext zlc) throws ServiceException {
        Config config = mProv.getConfig();
        doEntry(zlc, config, "global config");
    }
    
    private void doAllServers(ZimbraLdapContext zlc) throws ServiceException {
        List<Server> servers = mProv.getAllServers();
        
        for (Server server : servers)
            doEntry(zlc, server, "server " + server.getName());
    }
}
