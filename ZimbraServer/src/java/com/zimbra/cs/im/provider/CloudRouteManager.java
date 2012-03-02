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
package com.zimbra.cs.im.provider;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.wildfire.ConnectionCloseListener;
import org.jivesoftware.wildfire.server.OutgoingSessionPromise;
import org.xmpp.packet.Packet;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning.AccountBy;

/**
 * Singleton class which manages cloud-routing connections.  This class is a ChannelHandler
 * itself because it can receive and buffer outgoing packets during the connection process
 */
public class CloudRouteManager extends OutgoingSessionPromise {
    
    public static CloudRouteSession get(Server server) {
        return localServers.get(server);
    }
    
    public static CloudRouteManager getInstance() {
        return sInstance;
    }
    
    public static String dumpRoutingTable() {
        StringBuilder toRet = new StringBuilder();
        
        for (Iterator<Map.Entry<Server,CloudRouteSession>> iter = localServers.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<Server,CloudRouteSession> entry = iter.next();
            toRet.append(entry.getKey().getName().toString()).append(": ").append(entry.getValue().toString()).append("\n");
        }
        
        return toRet.toString();
    }
    
    static void remove(Server server, CloudRouteSession route) {
        localServers.remove(server, route);
    }
    
    private CloudRouteManager() {
        super();
    }
    
    @Override
    protected void createSessionAndSendPacket(Packet packet) throws Exception {
        
        String node = packet.getTo().getNode();
        String domain = packet.getTo().getDomain();
        Account acct = Provisioning.getInstance().get(AccountBy.name, node+"@"+domain);
        if (!Provisioning.onLocalServer(acct)) {
            Server acctServer = Provisioning.getInstance().getServer(acct);
            
            CloudRouteSession route = localServers.get(acctServer);
            if (route == null) {
                synchronized(this) {
                    route = localServers.get(acctServer);
                    if (route == null) {
                        route = CloudRouteSession.connect(acctServer);
                        localServers.put(acctServer, route);
                        route.getConnection().registerCloseListener(sCloseListener, route);
                    }
                }
            }
            
            if (route != null) {
                route.process(packet);
            } else {
                ZimbraLog.im.info("Failed to connect to cloud server: "+acctServer.getName() +" for account "+acct.getName());
                throw new Exception("Failed to connect to cloud server: "+acctServer.getName());
            }
        }
    }
    
    private static ConcurrentHashMap<Server, CloudRouteSession> localServers = new ConcurrentHashMap<Server, CloudRouteSession>();
    
    private static final CloudConnectionCloseListener sCloseListener = new CloudConnectionCloseListener();
    
    private static CloudRouteManager sInstance = new CloudRouteManager();
    
    private static final class CloudConnectionCloseListener implements ConnectionCloseListener {
        public void onConnectionClose(Object handback) {
            CloudRouteSession route = (CloudRouteSession)handback;
            Server server = route.getServer();
            if (server != null)
                CloudRouteManager.remove(server, route);
        }
    }
}
