/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013, 2014 Zimbra, Inc.
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

package com.zimbra.cs.offline;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.account.Key;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.client.ZMailbox;


public class OfflineServlet extends HttpServlet {

    private static final String LOCALHOST_URL_PREFIX = "http://127.0.0.1:";

    private static String LOCALHOST_SOAP_URL;
    private static String LOCALHOST_ADMIN_URL;
    private static String LOCALHOST_MAIL_URL;

    private ZMailbox.Options getMailboxOptions(String username, String password) {
        ZMailbox.Options options = new ZMailbox.Options(username, Key.AccountBy.name, password, LOCALHOST_SOAP_URL);
        options.setNoSession(false);
        return options;
    }

    private void setAuthCookie(String username, String password, HttpServletResponse response) throws ServiceException {
        String auth = ZMailbox.getMailbox(getMailboxOptions(username, password)).getAuthToken().getValue();
        Cookie cookie = new Cookie("ZM_AUTH_TOKEN", auth);
        cookie.setPath("/");
        cookie.setMaxAge(31536000);
        response.addCookie(cookie);

        Cookie zmapps = new Cookie("ZM_APPS", "mcaoinbtx");
        zmapps.setPath("/");
        zmapps.setMaxAge(31536000);
        response.addCookie(zmapps);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        try {
            SoapProvisioning prov = new SoapProvisioning();
            prov.soapSetURI(LOCALHOST_ADMIN_URL);
            prov.soapZimbraAdminAuthenticate();

            setAuthCookie("local_account@host.local", "test123", resp);
            resp.sendRedirect(LOCALHOST_MAIL_URL);
        } catch (ServiceException x) {
            throw new ServletException(x);
        }
    }

    private static final long serialVersionUID = 901093939836074611L;

    @Override
    public void init() {
        try {
            ZimbraLog.addContextFilters(OfflineLC.zdesktop_log_context_filter.value());
            String port = LC.zimbra_admin_service_port.value();

            //setting static variables
            LOCALHOST_SOAP_URL = LOCALHOST_URL_PREFIX + port + AccountConstants.USER_SERVICE_URI;
            LOCALHOST_ADMIN_URL = LOCALHOST_URL_PREFIX + port + AdminConstants.ADMIN_SERVICE_URI;
            LOCALHOST_MAIL_URL = LOCALHOST_URL_PREFIX + port + "/zimbra/mail";

            OfflineProvisioning.getOfflineInstance().getLocalAccount();
            OfflineSyncManager.getInstance().init();
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
}
