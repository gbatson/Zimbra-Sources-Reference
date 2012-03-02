/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.im;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.util.LdapConfig;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.interceptor.InterceptorManager;
import org.jivesoftware.wildfire.net.CloudRoutingSocketReader;
import org.jivesoftware.wildfire.net.SocketConnection;
import org.jivesoftware.wildfire.net.CloudRoutingSocketReader.CloudRoutingSessionFactory;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.im.interop.Interop;
import com.zimbra.cs.im.provider.CloudRouteSession;
import com.zimbra.cs.im.provider.IMServerConfig;
import com.zimbra.cs.im.provider.InteropRegistrationProviderImpl;
import com.zimbra.cs.im.provider.ZimbraLocationManager;

public class ZimbraIM {
    
    private static boolean sRunning = false;
    
    public synchronized static void startup() throws ServiceException {
        try {
            LdapConfig.setProvider(new IMServerConfig());
            
            final ArrayList<String> domainStrs = new ArrayList<String>();
            
            String defaultDomain = Provisioning.getInstance().getConfig().getAttr(Provisioning.A_zimbraDefaultDomainName, null);
            if (defaultDomain != null) {
                ZimbraLog.im.info("Setting default XMPP domain to: "+defaultDomain);
                domainStrs.add(defaultDomain);
            } 
            
            NamedEntry.Visitor visitor = new NamedEntry.Visitor() {
                public void visit(NamedEntry entry) {
                    domainStrs.add(entry.getName());
                }
            };
            Provisioning.getInstance().getAllDomains(visitor, new String[]{Provisioning.A_zimbraDomainName});
            
            // set the special msgs ClassLoader -- so that WF looks in our conf/msgs directory
            // for its localization .properties bundles.
            org.jivesoftware.util.LocaleUtils.sMsgsClassLoader = com.zimbra.common.util.L10nUtil.getMsgClassLoader();
            
            CloudRoutingSocketReader.setSessionFactory(new CloudRoutingSessionFactory() { 
                public Session createSession(String hostname, CloudRoutingSocketReader reader, SocketConnection connection, Element streamElt) {
                    return CloudRouteSession.create(hostname, reader, connection, streamElt);
                }
            });

            ZimbraLocationManager locMgr = ZimbraLocationManager.getInstance();
            
            String[] excludedCiphers = Provisioning.getInstance().getConfig().getMultiAttr(Provisioning.A_zimbraSSLExcludeCipherSuites);
            XMPPServer srv = new XMPPServer(excludedCiphers, locMgr, domainStrs);
            InterceptorManager.getInstance().addInterceptor(new com.zimbra.cs.im.PacketInterceptor());
            
            Interop.getInstance().start(XMPPServer.getInstance(), XMPPServer.getInstance().getInternalComponentManager());
            Interop.setDataProvider(new InteropRegistrationProviderImpl());
            
            sRunning = true;
        } catch (Exception e) { 
            ZimbraLog.system.warn("Could not start XMPP server: " + e.toString());
            e.printStackTrace();
        }
    }
    
    public synchronized static void shutdown() {
        XMPPServer instance = XMPPServer.getInstance();
        if (instance != null)
            instance.stop();
        if (sRunning) {
            sRunning = false;
        }
    }
}
