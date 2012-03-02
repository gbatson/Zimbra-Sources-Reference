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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccessManager;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.auth.AuthContext;
import com.zimbra.cs.httpclient.URLUtil;
import com.zimbra.cs.service.authenticator.SSOAuthenticator.ZimbraPrincipal;
import com.zimbra.cs.servlet.ZimbraServlet;

public abstract class SSOServlet extends ZimbraServlet {
    private final static String IGNORE_LOGIN_URL = "/?ignoreLoginURL=1";
    
    
    public void init() throws ServletException {
        String name = getServletName();
        ZimbraLog.account.info("Servlet " + name + " starting up");
        super.init();
    }

    public void destroy() {
        String name = getServletName();
        ZimbraLog.account.info("Servlet " + name + " shutting down");
        super.destroy();
    }
    
    protected AuthToken authorize(HttpServletRequest req, AuthContext.Protocol proto, 
            ZimbraPrincipal principal, boolean isAdminRequest) 
    throws ServiceException {
        
        Map<String, Object> authCtxt = new HashMap<String, Object>();
        authCtxt.put(AuthContext.AC_ORIGINATING_CLIENT_IP, ZimbraServlet.getOrigIp(req));
        authCtxt.put(AuthContext.AC_ACCOUNT_NAME_PASSEDIN, principal.getName());
        authCtxt.put(AuthContext.AC_USER_AGENT, req.getHeader("User-Agent"));
        
        Provisioning prov = Provisioning.getInstance();
        Account acct = principal.getAccount();
        
        prov.ssoAuthAccount(acct, proto, authCtxt); 
        
        if (isAdminRequest) {
            if (!AccessManager.getInstance().isAdequateAdminAccount(acct)) {
                throw ServiceException.PERM_DENIED("not an admin account");
            }
        }
        
        AuthToken authToken = AuthProvider.getAuthToken(acct, isAdminRequest);
        
        /*
        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
                new String[] {"cmd", authType, "account", acct.getName(), "admin", isAdminRequest+""}));
        */
        
        return authToken;
    }
    
    protected boolean isOnAdminPort(HttpServletRequest req) throws ServiceException {
        int adminPort = Provisioning.getInstance().getLocalServer().getAdminPort();
        return req.getLocalPort() == adminPort;
    }
    
    protected boolean isFromZCO(HttpServletRequest req) throws ServiceException {
        final String UA_ZCO = "Zimbra-ZCO";
        String userAgent = req.getHeader("User-Agent");
        return userAgent.contains(UA_ZCO);
    }
    
    protected void setAuthTokenCookieAndReturn(HttpServletRequest req, HttpServletResponse resp, 
            AuthToken authToken) 
    throws IOException, ServiceException {
        
        boolean isAdmin = AuthToken.isAnyAdmin(authToken);
        boolean secureCookie = isProtocolSecure(req.getScheme());
        authToken.encode(resp, isAdmin, secureCookie);
        resp.setContentLength(0);
    }
    
    protected void setAuthTokenCookieAndRedirect(HttpServletRequest req, HttpServletResponse resp, 
            Account acct, AuthToken authToken) 
    throws IOException, ServiceException {
        
        boolean isAdmin = AuthToken.isAnyAdmin(authToken);
        boolean secureCookie = isProtocolSecure(req.getScheme());
        authToken.encode(resp, isAdmin, secureCookie);

        Provisioning prov = Provisioning.getInstance();
        Server server = prov.getServer(acct);
        
        String redirectUrl;
        if (isAdmin) {
            redirectUrl = getAdminUrl(server);
        } else {
            redirectUrl = getMailUrl(server);
        }
        
        // always append the ignore loginURL query so we do not get into a redirect loop.
        redirectUrl = redirectUrl + IGNORE_LOGIN_URL;  // not yet supported for admin console
        
        URL url = new URL(redirectUrl);
        boolean isRedirectProtocolSecure = isProtocolSecure(url.getProtocol());
        
        if (secureCookie && !isRedirectProtocolSecure) {
            throw ServiceException.INVALID_REQUEST("cannot redirect to non-secure protocol: " + redirectUrl, null);
        }
        
        resp.sendRedirect(redirectUrl);
    }
    
    // Redirect to the specified error page without Zimbra auth token cookie.
    // The default error page is the webapp's regular entry page where user can
    // enter his username/password.
    protected void redirectToErrorPage(HttpServletRequest req, HttpServletResponse resp, 
            boolean isAdminRequest, String errorUrl) throws IOException, ServiceException {
        String redirectUrl;
        
        if (errorUrl == null) {
            
            Server server = Provisioning.getInstance().getLocalServer();
            
            if (isAdminRequest) {
                redirectUrl = getAdminUrl(server); 
            } else {
                redirectUrl = getMailUrl(server);
            }
            
            // always append the ignore loginURL query so we do not get into a redirect loop.
            redirectUrl = redirectUrl + IGNORE_LOGIN_URL;  // not yet supported for admin console
            
        } else {
            redirectUrl = errorUrl;
        }
        
        resp.sendRedirect(redirectUrl);
    }
    
    
    private boolean isProtocolSecure(String protocol) {
        return URLUtil.PROTO_HTTPS.equalsIgnoreCase(protocol);
    }
    
    protected String getMailUrl(Server server) throws ServiceException {
        final String DEFAULT_MAIL_URL = "/zimbra";
    
        String serviceUrl = server.getAttr(Provisioning.A_zimbraMailURL, DEFAULT_MAIL_URL);
        return URLUtil.getServiceURL(server, serviceUrl, true);
    }
    
    protected String getAdminUrl(Server server) throws ServiceException {
        final String DEFAULT_ADMIN_URL = "/zimbraAdmin";
        
        String serviceUrl = server.getAttr(Provisioning.A_zimbraAdminURL, DEFAULT_ADMIN_URL);
        return URLUtil.getAdminURL(server, serviceUrl, true);
    }
}
