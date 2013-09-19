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
package com.zimbra.cs.im.provider;

import org.jivesoftware.wildfire.RoutingTable;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.*;
import org.jivesoftware.wildfire.component.InternalComponentManager;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.wildfire.server.OutgoingSessionPromise;
import org.xmpp.packet.JID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.LogFactory;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.ServerBy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZimbraRoutingTableImpl extends BasicModule implements RoutingTable {
    
    private static com.zimbra.common.util.Log sLog = LogFactory.getLog(ZimbraRoutingTableImpl.class);

    /**
     * We need a three level tree built of hashtables: host -> name -> resource
     */
    private Map routes = new ConcurrentHashMap();

    private InternalComponentManager componentManager;

    public ZimbraRoutingTableImpl() {
        super("Routing table");
        componentManager = InternalComponentManager.getInstance();
    }
    
    public String dumpRoutingTable() {
        StringBuilder toRet = new StringBuilder("LocalRoutes:");
        
        for (Iterator<Map.Entry> iter = routes.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = iter.next();
            toRet.append(entry.getKey().toString()).append(": ").append(entry.getValue().toString()).append("\n");
        }
        toRet.append("\nCloudRoutes: ").append(CloudRouteManager.dumpRoutingTable());
        return toRet.toString();
    }

    public void addRoute(JID node, RoutableChannelHandler destination) {
        
        sLog.debug("Adding route: "+node+" to handler "+destination.toString());

        String nodeJID = node.getNode() == null ? "" : node.getNode();
        String resourceJID = node.getResource() == null ? "" : node.getResource();

        if (destination instanceof ClientSession) {
            Object nameRoutes = routes.get(node.getDomain());
            if (nameRoutes == null) {
                // No route to the requested domain. Create a new entry in the table
                synchronized (node.getDomain().intern()) {
                    // Check again if a route exists now that we have a lock
                    nameRoutes = routes.get(node.getDomain());
                    if (nameRoutes == null) {
                        // Still nothing so create a new entry in the map for domain
                        nameRoutes = new ConcurrentHashMap();
                        routes.put(node.getDomain(), nameRoutes);
                    }
                }
            }
            // Check if there is something associated with the node of the JID
            Object resourceRoutes = ((Map) nameRoutes).get(nodeJID);
            if (resourceRoutes == null) {
                // Nothing was found so create a new entry for this node (a.k.a. user)
                synchronized (nodeJID.intern()) {
                    resourceRoutes = ((Map) nameRoutes).get(nodeJID);
                    if (resourceRoutes == null) {
                        resourceRoutes = new ConcurrentHashMap();
                        ((Map) nameRoutes).put(nodeJID, resourceRoutes);
                    }
                }
            }
            // Add the connected resource to the node's Map
            ((Map) resourceRoutes).put(resourceJID, destination);
        }
        else {
            routes.put(node.getDomain(), destination);
        }
    }

    public ChannelHandler getRoute(JID node) {
        if (node == null) {
            return null;
        }
        return getRoute(node.toString(), node.getNode() == null ? "" : node.getNode(),
                node.getDomain(), node.getResource() == null ? "" : node.getResource());
    }
    
    protected RoutableChannelHandler getCloudRoute(String node, String domain) {
        try {
            String componentServerName = XMPPServer.getInstance().getServerForComponent(domain);
            if (componentServerName != null) {
                Server componentServer = Provisioning.getInstance().get(ServerBy.name, componentServerName);
                Server localServer = Provisioning.getInstance().getLocalServer();
                if (localServer == componentServer)
                    return null;
                CloudRouteSession route = CloudRouteManager.get(componentServer);
                if (route == null)
                    return CloudRouteManager.getInstance();
                else
                    return route;
            } else {
                Account acct = Provisioning.getInstance().get(AccountBy.name, node+"@"+domain);
                if (acct == null)
                    return null;

                if (!Provisioning.onLocalServer(acct)) {
                    Server acctServer = Provisioning.getInstance().getServer(acct);

                    if (acctServer == null)
                        return null;

                    CloudRouteSession route = CloudRouteManager.get(acctServer);
                    if (route == null)
                        return CloudRouteManager.getInstance();
                    else
                        return route;
                }

            }
        } catch (ServiceException ex) {
            return null;
        }
        return null;
    }

    private ChannelHandler getRoute(String jid, String node, String domain, String resource) {
        RoutableChannelHandler route = null;

        // Check if the address belongs to a remote server
        if (!XMPPServer.getInstance().isLocalDomain(domain) 
                        && routes.get(domain) == null
                        && !XMPPServer.getInstance().isCloudComponent(domain)) {
            // Return a promise of a remote session. This object will queue packets pending
            // to be sent to remote servers
            ChannelHandler toRet = OutgoingSessionPromise.getInstance();
            if (sLog.isDebugEnabled())
                sLog.debug("Returning OutgoingSessionPromise route for jid:"+jid+" node:"+node+" domain:"+domain+" resource:"+resource+" result="+toRet);
            return toRet;
        }

        RoutableChannelHandler remoteInThisCloud = getCloudRoute(node, domain);
        if (remoteInThisCloud != null) {
            if (sLog.isDebugEnabled())
                sLog.debug("Returning CloudRoute for jid:"+jid+" node:"+node+" domain:"+domain+" resource:"+resource+" result="+remoteInThisCloud);
            return remoteInThisCloud;
        }
        
        try {
            Object nameRoutes = routes.get(domain);
            if (nameRoutes instanceof ChannelHandler) {
                // probably a Component, e.g. "conference.myhost.com"
                route = (RoutableChannelHandler) nameRoutes;
            } 
            else if (nameRoutes != null) {
                Object resourceRoutes = ((Map) nameRoutes).get(node);
                if (resourceRoutes instanceof ChannelHandler) {
                    route = (RoutableChannelHandler) resourceRoutes;
                }
                else if (resourceRoutes != null) {
                    route = (RoutableChannelHandler) ((Map) resourceRoutes).get(resource);
                }
                else {
                    route = null;
                }
            }
        }
        catch (Exception e) {
            if (Log.isDebugEnabled()) {
                Log.debug("Route not found for JID: " + jid, e);
            }
        }

        if (sLog.isDebugEnabled())
            sLog.debug("Returning local route for jid:"+jid+" node:"+node+" domain:"+domain+" resource:"+resource+" result="+route);
        return route;
    }

    public List<ChannelHandler> getRoutes(JID node) {
        // Check if the address belongs to a remote server
        if (!XMPPServer.getInstance().isLocalDomain(node.getDomain()) 
                        && routes.get(node.getDomain()) == null
                        && !XMPPServer.getInstance().isCloudComponent(node.getDomain())) {
//                        && componentManager.getComponent(node) == null) {
            // Return a promise of a remote session. This object will queue packets pending
            // to be sent to remote servers
            List<ChannelHandler> list = new ArrayList<ChannelHandler>();
            ChannelHandler toRet = OutgoingSessionPromise.getInstance(); 
            if (sLog.isDebugEnabled())
                sLog.debug("Returning OutgoingSessionPromise route for jid:"+node+" result="+toRet);
            list.add(toRet);
            return list;
        }

        RoutableChannelHandler remoteInThisCloud = getCloudRoute(node.getNode(), node.getDomain());
        if (remoteInThisCloud != null) {
            List<ChannelHandler> list = new ArrayList<ChannelHandler>();
            list.add(remoteInThisCloud);
            if (sLog.isDebugEnabled())
                sLog.debug("Returning CloudRoute for jid:"+node+" result="+remoteInThisCloud);
            return list;
        }
        
        LinkedList list = null;
        Object nameRoutes = routes.get(node.getDomain());
        if (nameRoutes != null) {
            if (nameRoutes instanceof ChannelHandler) {
                list = new LinkedList();
                list.add(nameRoutes);
            }
            else if (node.getNode() == null) {
                list = new LinkedList();
                getRoutes(list, (Map) nameRoutes);
            }
            else {
                Object resourceRoutes = ((Map) nameRoutes).get(node.getNode());
                if (resourceRoutes != null) {
                    if (resourceRoutes instanceof ChannelHandler) {
                        list = new LinkedList();
                        list.add(resourceRoutes);
                    }
                    else if (node.getResource() == null || node.getResource().length() == 0) {
                        list = new LinkedList();
                        getRoutes(list, (Map) resourceRoutes);
                    }
                    else {
                        Object entry = ((Map) resourceRoutes).get(node.getResource());
                        if (entry != null) {
                            list = new LinkedList();
                            list.add(entry);
                        }
                    }
                }
            }
        }
        if (list == null) {
            if (sLog.isDebugEnabled())
                sLog.debug("Returning null local route for jid:"+node);
            return Collections.emptyList();
        }
        else {
            if (sLog.isDebugEnabled()) {
                StringBuilder routes = new StringBuilder();
                boolean atFirst = false;
                for (Object o : list) {
                    if (!atFirst)
                        routes.append(", ");
                    routes.append("route=").append(o);
                }
                sLog.debug("Returning local route for "+node+" result="+routes);
            }
            return list;
        }
    }

    /**
     * Recursive method to iterate through the given table (and any embedded map)
     * and stuff non-Map values into the given list.<p>
     *
     * There should be no recursion problems since the routing table is at most 3 levels deep.
     *
     * @param list  The list to stuff entries into
     * @param table The hashtable who's values should be entered into the list
     */
    private void getRoutes(LinkedList list, Map table) {
        Iterator entryIter = table.values().iterator();
        while (entryIter.hasNext()) {
            Object entry = entryIter.next();
            if (entry instanceof ConcurrentHashMap) {
                getRoutes(list, (Map)entry);
            }
            else {
                // Do not include the same entry many times. This could be the case when the same 
                // session is associated with the bareJID and with a given resource
                if (!list.contains(entry)) {
                    list.add(entry);
                }
            }
        }
    }

    public ChannelHandler getBestRoute(JID node) {
        ChannelHandler route = getRoute(node);
        if (route == null) {
            // Try looking for a route based on the bare JID
            String nodeJID = node.getNode() == null ? "" : node.getNode();
            route = getRoute(node.toBareJID(), nodeJID, node.getDomain(), "");
        }
        return route;
    }

    public ChannelHandler removeRoute(JID node) {

        if (sLog.isDebugEnabled()) {
            Object currentRoute = routes.get(node);
            if (currentRoute == null)
                currentRoute = "none";
            sLog.debug("Removing route: "+node+ "(current route: "+currentRoute.toString()+")");
        }
        
        ChannelHandler route = null;
        String nodeJID = node.getNode() == null ? "" : node.getNode();
        String resourceJID = node.getResource() == null ? "" : node.getResource();

        try {
            Object nameRoutes = routes.get(node.getDomain());
            if (nameRoutes instanceof ConcurrentHashMap) {
                Object resourceRoutes = ((Map) nameRoutes).get(nodeJID);
                if (resourceRoutes instanceof ConcurrentHashMap) {
                    // Remove the requested resource for this user
                    route = (ChannelHandler) ((Map) resourceRoutes).remove(resourceJID);
                    if (((Map) resourceRoutes).isEmpty()) {
                        ((Map) nameRoutes).remove(nodeJID);
                        if (((Map) nameRoutes).isEmpty()) {
                            routes.remove(node.getDomain());
                        }
                    }
                }
                else {
                    // Remove the unique route to this node
                    ((Map) nameRoutes).remove(nodeJID);
                }
            }
            else if (nameRoutes != null) {
                // The retrieved route points to a RoutableChannelHandler
                if (("".equals(nodeJID) && "".equals(resourceJID)) ||
                        ((RoutableChannelHandler) nameRoutes).getAddress().equals(node)) {
                    // Remove the route to this domain
                    routes.remove(node.getDomain());
                }
            }
        }
        catch (Exception e) {
            Log.error("Error removing route", e);
        }
        return route;
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);
    }

}
