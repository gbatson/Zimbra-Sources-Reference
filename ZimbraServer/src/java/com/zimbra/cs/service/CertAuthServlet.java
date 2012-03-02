/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AccountServiceException.AuthFailedServiceException;
import com.zimbra.cs.account.ZAttrProvisioning.MailSSLClientCertMode;
import com.zimbra.cs.account.auth.AuthContext;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.service.authenticator.SSOAuthenticator;
import com.zimbra.cs.service.authenticator.SSOAuthenticator.SSOAuthenticatorServiceException;
import com.zimbra.cs.service.authenticator.SSOAuthenticator.ZimbraPrincipal;
import com.zimbra.cs.service.authenticator.ClientCertAuthenticator;

public class CertAuthServlet extends SSOServlet {
    
    // The clientCertPortRule in jetty.xml ensure only the certauth URLs can land on the 
    // SSL client cert port.  
    // The regex here is to ensure that this servlet is only serving the URLs it recognizes,
    private static final Pattern allowedUrl = Pattern.compile("^(/service/certauth)(/|/(admin)(/)?)?$");
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ZimbraLog.clearContext();
        addRemoteIpToLoggingContext(req);
        addUAToLoggingContext(req);
        
        try {
            String url = req.getRequestURI();
            Matcher matcher = allowedUrl.matcher(url);
            boolean isAdminRequest = false;
            if (!matcher.matches()) {
                throw ServiceException.INVALID_REQUEST("resource not allowed for the certauth servlet: " + url, null);
            } else {
                if (matcher.groupCount() > 3 && "admin".equals(matcher.group(3))) {
                    isAdminRequest = true;
                }
            }
            
            SSOAuthenticator authenticator = new ClientCertAuthenticator(req, resp);
            ZimbraPrincipal principal = null;
            try {
                principal = authenticator.authenticate();
            } catch (SSOAuthenticatorServiceException e) {
                if (SSOAuthenticatorServiceException.NO_CLIENT_CERTIFICATE.equals(e.getCode())) {
                    if (missingClientCertOK()) {
                        redirectToErrorPage(req, resp, isAdminRequest, null);
                        return;
                    } else {
                        throw e;
                    }
                }
            }
            
            AuthToken authToken = authorize(req, AuthContext.Protocol.client_certificate, principal, isAdminRequest);
            setAuthTokenCookieAndRedirect(req, resp, principal.getAccount(), authToken);
            
        } catch (ServiceException e) {
            if (e instanceof AuthFailedServiceException) {
                AuthFailedServiceException afe = (AuthFailedServiceException)e;
                ZimbraLog.account.debug("client certificate auth failed: " + afe.getMessage() + afe.getReason(", %s"), e);
            } else {
                ZimbraLog.account.warn("client certificate auth failed: " + e.getMessage(), e);
            }
            ZimbraLog.account.debug("client certificate auth failed", e);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
    
    private boolean missingClientCertOK() {
        try {
            // should not have to checked the zimbraMailSSLClientCertMode.
            // If it is NeedClientAuth and there is not client cert, the 
            // requested should have failed during ssl handshake.
            // This is just a sanity check, just in case.
            Server server = Provisioning.getInstance().getLocalServer();
            Provisioning.MailSSLClientCertMode mode = server.getMailSSLClientCertMode();
            if (mode == MailSSLClientCertMode.WantClientAuth) {
                return true;
            }
        } catch (ServiceException e) {
            ZimbraLog.account.debug("unable to get local server", e);
        }
        return false;
    }

}
