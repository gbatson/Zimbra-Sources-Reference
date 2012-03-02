/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.mailclient.smtp;

import com.zimbra.cs.mailclient.MailConfig;

/**
 * SMTP client configuration.
 */
public class SmtpConfig extends MailConfig {
    public static final String PROTOCOL = "smtp";
    public static final int DEFAULT_PORT = 25;

    private String domain;
    private boolean allowPartialSend;
    private String password;
    
    public SmtpConfig(String host, int port, String user, String password) {
        super(host);
        setPort(port);
        setAuthenticationId(user);
        setPassword(password);
    }
    
    public SmtpConfig(String host) {
        super(host);
        setPort(DEFAULT_PORT);
    }
    
    public SmtpConfig() {
    }
    
    public String getProtocol() {
        return PROTOCOL;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setAllowPartialSend(boolean allow) {
        this.allowPartialSend = allow;
    }
    
    public boolean isPartialSendAllowed() {
        return allowPartialSend;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean needAuth() {
        return getAuthenticationId() != null;
    }
}
