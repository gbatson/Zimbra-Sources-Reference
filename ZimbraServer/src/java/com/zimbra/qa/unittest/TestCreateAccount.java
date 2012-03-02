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

package com.zimbra.qa.unittest;

import java.util.HashMap;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.CliUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.LdapProvisioning;
import com.zimbra.cs.account.soap.SoapProvisioning;

public class TestCreateAccount extends TestCase {
    private String TEST_ID = TestProvisioningUtil.genTestId();;
    private static String TEST_NAME = "test-accounts";
    private static String PASSWORD = "test123";
    private static int NUM_THREADS = 1; // 20;
    private static int NUM_ACCTS_PER_THREAD = 100; // 500;

    private String DOMAIN_NAME = null;
   
    /*
     * for testCreateAccount
     * 
     * call either setUpDomain or setUpAccounts
     */
    private void setUpDomain() throws Exception {
        
        Provisioning prov = Provisioning.getInstance();
        assertTrue(prov instanceof LdapProvisioning);
            
        DOMAIN_NAME = TestProvisioningUtil.baseDomainName(TEST_NAME, TEST_ID);
        Domain domain = prov.createDomain(DOMAIN_NAME, new HashMap<String, Object>());
        assertNotNull(domain);
    }
    
    /*
     * for testGetAccount, create only one and sue for subsequent tests
     * 
     * call either setUpDomain or setUpAccounts
     */
    private void setUpAccounts() throws Exception {
        
        Provisioning prov = Provisioning.getInstance();
        assertTrue(prov instanceof LdapProvisioning);
            
        DOMAIN_NAME = TestProvisioningUtil.baseDomainName(TEST_NAME, null);
        Domain domain = prov.get(Provisioning.DomainBy.name, DOMAIN_NAME);
        if (domain != null)
            return;
        
        domain = prov.createDomain(DOMAIN_NAME, new HashMap<String, Object>());
        assertNotNull(domain);
        
        // simulate accounts created by the concurrent create account test
        for (int i=0; i<NUM_THREADS; i++) {
            createAccount(prov, i);
        }
    }
    
    private SoapProvisioning getSoapProv() throws Exception {
        SoapProvisioning sp = new SoapProvisioning();    
        String server = LC.zimbra_zmprov_default_soap_server.value();
        int port = LC.zimbra_admin_service_port.intValue();
        
        sp.soapSetURI(LC.zimbra_admin_service_scheme.value()+server+":"+port+AdminConstants.ADMIN_SERVICE_URI);
        sp.soapZimbraAdminAuthenticate();
        return sp;            
    }
    
    private String makeAcctName(int threadIdx, int idx) {
        return "a-" + idx + "-thread-"+ threadIdx + "@" + DOMAIN_NAME;
    }
    
    private void createAccount(Provisioning prov, int threadIdx) throws Exception {
        for (int i=0; i<NUM_ACCTS_PER_THREAD; i++) {
            String acctName = makeAcctName(threadIdx, i);
            try {
                Account acct = prov.createAccount(acctName, PASSWORD, new HashMap<String, Object>());
                assertNotNull(acct);
                // if ((i+1)%100 == 0)
                    System.out.println("createAccount: " + threadIdx + ", " + i);
            } catch (Exception e) {
                System.out.println("createAccount caught exception: " + threadIdx + ", " + i);
                throw e;
            }
        }
    }
    
    private void getAccount(int threadIdx) throws Exception {
        SoapProvisioning prov = getSoapProv();
        for (int i=0; i<NUM_ACCTS_PER_THREAD; i++) {
            System.out.println("getAccount: " + threadIdx + ", " + i);
            String acctName = makeAcctName(threadIdx, i);
            Account acct = prov.get(Provisioning.AccountBy.name, acctName);
            assertNotNull(acct);
        }
    }
    
    abstract class TestThread extends Thread {
        
        protected int mThreadIdx;
        
        TestThread(int threadIdx) {
            mThreadIdx = threadIdx;
        }
        
        abstract void doRun() throws Exception ; 
        
        public void run() {
            try {
                System.out.println("thread " + mThreadIdx + " started");
                doRun();
            } catch (Exception e) {
                System.out.println("thread " + mThreadIdx + " caught exception");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    
    class TestCreateAccountThread extends TestThread {
        
        TestCreateAccountThread(int threadIdx) {
            super(threadIdx);
        }
        
        public void doRun() throws Exception {
            SoapProvisioning soapProv = getSoapProv();
            createAccount(soapProv, mThreadIdx);
        }
    }

    class TestGetAccountThread extends TestThread {
        
        TestGetAccountThread(int threadIdx) {
            super(threadIdx);
        }
        
        public void doRun() throws Exception {
            getAccount(mThreadIdx);
        }
    }
    
    /*
     * bug 22933
     */
    public void testCreateAccount() throws Exception {
        
        setUpDomain();
        CliUtil.toolSetup(); // setup cert
        
        for (int i=0; i<NUM_THREADS; i++) {
            TestThread t = new TestCreateAccountThread(i);
            t.start();
        }
    }
    
    public void xxxtestGetAccount() throws Exception {
        
        setUpAccounts();
        CliUtil.toolSetup(); // setup cert
        
        for (int i=0; i<NUM_THREADS; i++) {
            TestThread t = new TestGetAccountThread(i);
            t.start();
        }
    }
    
    public void xxxtestCreateDomain() throws Exception {
        Provisioning prov = Provisioning.getInstance();
        assertTrue(prov instanceof LdapProvisioning);
         
        String baseDomainName = TestProvisioningUtil.baseDomainName("domaintest", TEST_ID);
        
        int NUM_DOMAINS = 100;
        for (int i=0; i<NUM_DOMAINS; i++) {
            String domainName = "d-" + (i+1) + "." + baseDomainName;
            Domain domain = prov.createDomain(domainName, new HashMap<String, Object>());
            assertNotNull(domain);
        }
    }
 
    public static void main(String[] args) throws Exception {
        CliUtil.toolSetup();
        try {
            TestUtil.runTest(TestCreateAccount.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

