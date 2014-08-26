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
import com.sun.xml.ws.developer.WSBindingProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.soap.Utility;
import generated.zcsclient.account.testEndSessionRequest;
import generated.zcsclient.account.testEndSessionResponse;
import generated.zcsclient.ws.service.ZcsPortType;

/**
 * Current assumption : user1 exists with password test123
 */
public class WSDLEndSessionTest {

    private static ZcsPortType acctSvcEIF;

    @BeforeClass
    public static void init() throws Exception {
        acctSvcEIF = Utility.getZcsSvcEIF();
    }

    @Test
    public void simple() throws Exception {
       testEndSessionRequest req = new testEndSessionRequest();
       Utility.addSoapAcctAuthHeader((WSBindingProvider)acctSvcEIF);
       testEndSessionResponse response = acctSvcEIF.endSessionRequest(req);
       Assert.assertNotNull(response);
    }
}
