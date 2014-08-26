/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.soap.account;
import java.util.List;

import com.sun.xml.ws.developer.WSBindingProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.soap.Utility;
import generated.zcsclient.account.testGetPrefsRequest;
import generated.zcsclient.account.testGetPrefsResponse;
import generated.zcsclient.account.testPref;
import generated.zcsclient.ws.service.ZcsPortType;

/**
 * Current assumption : user1 exists with password test123
 */
public class WSDLGetPrefsTest {

    private static ZcsPortType acctSvcEIF;

    @BeforeClass
    public static void init() throws Exception {
        acctSvcEIF = Utility.getZcsSvcEIF();
    }

    @Test
    public void simple() throws Exception {
       testGetPrefsRequest req = new testGetPrefsRequest();
       Utility.addSoapAcctAuthHeader((WSBindingProvider)acctSvcEIF);
       testGetPrefsResponse response = acctSvcEIF.getPrefsRequest(req);
       Assert.assertNotNull(response);
       List <testPref> respPrefs = response.getPref();
       Assert.assertTrue("Number of preferences in response=" + respPrefs.size() + " which is less than expected", respPrefs.size() > 100);
    }

    @Test
    public void getTwo() throws Exception {
       testGetPrefsRequest req = new testGetPrefsRequest();
       Utility.addSoapAcctAuthHeader((WSBindingProvider)acctSvcEIF);
       testPref calUserQAddPref = new testPref();
       calUserQAddPref.setName("zimbraPrefCalendarUseQuickAdd");
       testPref zimbraPrefShowSearchString = new testPref();
       zimbraPrefShowSearchString.setName("zimbraPrefShowSearchString");

       req.getPref().add(calUserQAddPref);
       req.getPref().add(zimbraPrefShowSearchString);
       testGetPrefsResponse response = acctSvcEIF.getPrefsRequest(req);
       Assert.assertNotNull(response);
       List <testPref> respPrefs = response.getPref();
       Assert.assertEquals("Number of <pref> in response wrong.", 2, respPrefs.size());
    }
}
