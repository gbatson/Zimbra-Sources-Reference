/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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

package com.zimbra.cs.account.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException.AuthFailedServiceException;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.krb5.Krb5Login;
import com.zimbra.cs.account.krb5.Krb5Principal;
import com.zimbra.cs.account.ldap.LdapEntry;
import com.zimbra.cs.account.ldap.LdapProvisioning;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;
import com.zimbra.cs.account.auth.AuthContext;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;

public abstract class AuthMechanism {
    
    protected String mAuthMech;  // value of the zimbraAuthMech attribute
    
    protected AuthMechanism(String authMech) {
        mAuthMech = authMech;
    }
    
    public static AuthMechanism makeInstance(Account acct) throws ServiceException {
        String authMech = Provisioning.AM_ZIMBRA;
        
        Provisioning prov = Provisioning.getInstance();
        Domain domain = prov.getDomain(acct);
        
        // see if it specifies an alternate auth
        if (domain != null) {
            String am = domain.getAttr(Provisioning.A_zimbraAuthMech);
            if (am != null)
                authMech = am;
        }
        
        if (authMech.equals(Provisioning.AM_ZIMBRA))
            return new ZimbraAuth(authMech);
        else if (authMech.equals(Provisioning.AM_LDAP) || authMech.equals(Provisioning.AM_AD)) 
            return new LdapAuth(authMech);
        else if (authMech.equals(Provisioning.AM_KERBEROS5))
            return new Kerberos5Auth(authMech);
        else if (authMech.startsWith(Provisioning.AM_CUSTOM))
            return new CustomAuth(authMech);
        else {
            // we didn't have a valid auth mech, fallback to zimbra default
            ZimbraLog.account.warn("unknown value for "+Provisioning.A_zimbraAuthMech+": "+authMech+", falling back to default mech");
            return new ZimbraAuth(authMech);
        }
    }
    
    public static void doZimbraAuth(LdapProvisioning prov, Domain domain, Account acct, String password, Map<String, Object> authCtxt) throws ServiceException {
        ZimbraAuth zimbraAuth = new ZimbraAuth(Provisioning.AM_ZIMBRA);
        zimbraAuth.doAuth(prov, domain, acct, password, authCtxt);
    }

    public boolean isZimbraAuth() {
        return false;
    }
    
    public abstract boolean checkPasswordAging() throws ServiceException;
    
    public abstract void doAuth(LdapProvisioning prov, Domain domain, Account acct, String password, Map<String, Object> authCtxt) throws ServiceException;

    public String getMechanism() {
        return mAuthMech;
    }
    
    public static String namePassedIn(Map<String, Object> authCtxt) {
        String npi;
        if (authCtxt != null) {
            npi = (String)authCtxt.get(AuthContext.AC_ACCOUNT_NAME_PASSEDIN);
            if (npi==null)
                npi = "";
        } else
            npi = "";
        return npi;    
    }
    
    /*
     * ZimbraAuth 
     */
    public static class ZimbraAuth extends AuthMechanism {
        ZimbraAuth(String authMech) {
            super(authMech);
        }
        
        public boolean isZimbraAuth() {
            return true;
        }
        
        public void doAuth(LdapProvisioning prov, Domain domain, Account acct, String password, Map<String, Object> authCtxt) throws ServiceException {
            
            String encodedPassword = acct.getAttr(Provisioning.A_userPassword);

            if (encodedPassword == null)
                throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt), "missing "+Provisioning.A_userPassword);

            if (PasswordUtil.SSHA.isSSHA(encodedPassword)) {
                if (PasswordUtil.SSHA.verifySSHA(encodedPassword, password))
                    return; // good password, RETURN
                else {
                    throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt), "invalid password"); 
                }
            } else if (acct instanceof LdapEntry) {
                // not SSHA, authenticate to Zimbra LDAP
                try {
                    ZimbraLdapContext.ldapAuthenticate(((LdapEntry)acct).getDN(), password);
                    return; // good password, RETURN                
                } catch (AuthenticationException e) {
                    throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt), e.getMessage(), e);
                } catch (AuthenticationNotSupportedException e) {
                    throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt), e.getMessage(), e);
                } catch (NamingException e) {
                    throw ServiceException.FAILURE(e.getMessage(), e);
                } catch (IOException e) {
                    throw ServiceException.FAILURE(e.getMessage(), e);
                }
            }
            throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt));       
        }
        
        public boolean checkPasswordAging() throws ServiceException {
            return true;
        }
    }
    
    /*
     * LdapAuth
     */
    static class LdapAuth extends AuthMechanism {
        LdapAuth(String authMech) {
            super(authMech);
        }
        
        public void doAuth(LdapProvisioning prov, Domain domain, Account acct, String password, Map<String, Object> authCtxt) throws ServiceException {
            prov.externalLdapAuth(domain, mAuthMech, acct, password, authCtxt);
        }
        
        public boolean checkPasswordAging() throws ServiceException {
            return false;
        }
    }
    
    /*
     * Kerberos5Auth
     */
    static class Kerberos5Auth extends AuthMechanism {
        Kerberos5Auth(String authMech) {
            super(authMech);
        }
        
        public void doAuth(LdapProvisioning prov, Domain domain, Account acct, String password, Map<String, Object> authCtxt) throws ServiceException {
            String principal = Krb5Principal.getKrb5Principal(domain, acct);
            
            if (principal == null)
                throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt), "cannot obtain principal for " + mAuthMech + " auth");
            
            if (principal != null) {
                try {
                    Krb5Login.verifyPassword(principal, password);
                } catch (LoginException e) {
                    throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt) + "(kerberos5 principal: " + principal + ")", e.getMessage(), e);
                }
            }
        }
        
        public boolean checkPasswordAging() throws ServiceException {
            return false;
        }
    }
    
    /*
     * CustomAuth
     */
    static class CustomAuth extends AuthMechanism {
        private String mHandlerName = ""; 
        private ZimbraCustomAuth mHandler;
        List<String> mArgs;
        
        CustomAuth(String authMech) {
            super(authMech);
         
            /*
             * value is in the format of custom:{handler} [arg1 arg2 ...]
             * args can be quoted.  whitespace and empty string in quoted args are preserved
             * 
             * e.g. http://blah.com:123    green " ocean blue   "  "" yelllow "" 
             * will be parsed and passed to the custom handler as:
             * [http://blah.com:123]
             * [green]
             * [ ocean blue   ]
             * []
             * [yelllow]
             * []
             * 
             */
            int handlerNameStart = mAuthMech.indexOf(':');
            if (handlerNameStart != -1) {
                int handlerNameEnd = mAuthMech.indexOf(' ');
                if (handlerNameEnd != -1) {
                    mHandlerName = mAuthMech.substring(handlerNameStart+1, handlerNameEnd);
                    QuotedStringParser parser = new QuotedStringParser(mAuthMech.substring(handlerNameEnd+1));
                    mArgs = parser.parse();
                    if (mArgs.size() == 0)
                        mArgs = null;
                } else {    
                    mHandlerName = mAuthMech.substring(handlerNameStart+1);
                }
                
                if (!StringUtil.isNullOrEmpty(mHandlerName))
                    mHandler = ZimbraCustomAuth.getHandler(mHandlerName);
            }
            
            if (ZimbraLog.account.isDebugEnabled()) {
                StringBuffer sb = null;
                if (mArgs != null) {
                    sb = new StringBuffer();
                    for (String s : mArgs)
                        sb.append("[" + s + "] ");
                }
                ZimbraLog.account.debug("CustomAuth: handlerName=" + mHandlerName + ", args=" + sb);
            }
        }
        
        public void doAuth(LdapProvisioning prov, Domain domain, Account acct, String password, Map<String, Object> authCtxt) throws ServiceException {
            
            if (mHandler == null)
                throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt), "handler " + mHandlerName + " for custom auth for domain " + domain.getName() + " not found");
            
            try {
                mHandler.authenticate(acct, password, authCtxt, mArgs);
                return;
            } catch (Exception e) {
                if (e instanceof ServiceException) {
                    throw (ServiceException)e;
                } else {   
                    String msg = e.getMessage();
                    if (StringUtil.isNullOrEmpty(msg))
                        msg = "";
                    else
                        msg = " (" + msg + ")";
                    /*
                     * include msg in the response, in addition to logs.  This is because custom 
                     * auth handlers might want to pass the reason back to the client.
                     */ 
                    throw AuthFailedServiceException.AUTH_FAILED(acct.getName(), namePassedIn(authCtxt)+msg, msg, e);
                }
            }
            
        }
        
        public boolean checkPasswordAging() throws ServiceException {
            if (mHandler == null)
                throw ServiceException.FAILURE("custom auth handler " + mHandlerName + " not found", null);
            return mHandler.checkPasswordAging();
        }
    }
    
    static class QuotedStringParser {
        private String mInput;
          
        //the parser flips between these two sets of delimiters
        private static final String DELIM_WHITESPACE_AND_QUOTES = " \t\r\n\"";
        private static final String DELIM_QUOTES_ONLY ="\"";

        public QuotedStringParser(String input) {
            if (input == null)
                throw new IllegalArgumentException("Search Text cannot be null.");
            mInput = input;
        }

        public List<String> parse() {
            List<String> result = new ArrayList<String>();

            boolean returnTokens = true;
            String currentDelims = DELIM_WHITESPACE_AND_QUOTES;
            StringTokenizer parser = new StringTokenizer(mInput, currentDelims, returnTokens);

            boolean openDoubleQuote = false;
            boolean gotContent = false;
            String token = null;
            while (parser.hasMoreTokens()) {
                token = parser.nextToken(currentDelims);
                if (!isDoubleQuote(token)) {
                    if (!currentDelims.contains(token)) {
                        result.add(token);
                        gotContent = true;
                    }
                } else {
                    currentDelims = flipDelimiters(currentDelims);
                    // allow empty string in double quotes
                    if (openDoubleQuote && !gotContent)
                        result.add("");
                    openDoubleQuote = !openDoubleQuote;
                    gotContent = false;
                }
            }
            return result;
        }

        private boolean isDoubleQuote(String token) {
            return token.equals("\"");
        }

        private String flipDelimiters(String curDelims) {
            if (curDelims.equals(DELIM_WHITESPACE_AND_QUOTES))
                return DELIM_QUOTES_ONLY;
            else
                return DELIM_WHITESPACE_AND_QUOTES;
        }
    }
    
    public static void main(String[] args) {
        QuotedStringParser parser = new QuotedStringParser( "http://blah.com:123    green \" ocean blue   \"  \"\" yelllow \"\"");
        List<String> tokens = parser.parse();
        int i = 0;
        for (String s : tokens)
            System.out.format("%d [%s]\n", ++i, s);
        
        CustomAuth ca = new CustomAuth("custom:sample http://blah.com:123    green \" ocean blue   \"  \"\" yelllow \"\"");
         
    }
}

