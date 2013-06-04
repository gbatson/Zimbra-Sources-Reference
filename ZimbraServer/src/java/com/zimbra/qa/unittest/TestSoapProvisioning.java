/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
package com.zimbra.qa.unittest;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

import com.zimbra.common.util.CliUtil;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.httpclient.URLUtil;

public class TestSoapProvisioning {
    
    @BeforeClass
    public static void init() {
        CliUtil.toolSetup();
        // ZimbraLog.ldap.setLevel(Log.Level.debug);
    }

    @Test
    public void isExpired() throws Exception {
        long lifeTimeSecs = 10;  // 10 seconds
        
        Provisioning prov = Provisioning.getInstance();
        
        String acctName = TestUtil.getAddress("isExpired");
        String password = "test123";
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraIsAdminAccount, Provisioning.TRUE);
        attrs.put(Provisioning.A_zimbraAdminAuthTokenLifetime, String.valueOf(lifeTimeSecs) + "s");
        Account acct = prov.createAccount(acctName, password, attrs);
        
        SoapProvisioning soapProv = new SoapProvisioning();
        
        Server server = prov.getLocalServer();
        soapProv.soapSetURI(URLUtil.getAdminURL(server));
        
        assertTrue(soapProv.isExpired());
        
        soapProv.soapAdminAuthenticate(acctName, password);
        
        assertFalse(soapProv.isExpired());
        
        System.out.println("Waiting for " + lifeTimeSecs + " seconds");
        Thread.sleep((lifeTimeSecs+1)*1000);
        
        assertTrue(soapProv.isExpired());
        
        prov.deleteAccount(acct.getId());
    }
}
