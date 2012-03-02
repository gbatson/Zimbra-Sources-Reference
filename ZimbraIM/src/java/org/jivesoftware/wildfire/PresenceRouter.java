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
package org.jivesoftware.wildfire;

import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.wildfire.handler.PresenceSubscribeHandler;
import org.jivesoftware.wildfire.handler.PresenceUpdateHandler;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

/**
 * <p>Route presence packets throughout the server.</p>
 * <p>Routing is based on the recipient and sender addresses. The typical
 * packet will often be routed twice, once from the sender to some internal
 * server component for handling or processing, and then back to the router
 * to be delivered to it's final destination.</p>
 *
 * @author Iain Shigeoka
 */
public class PresenceRouter extends BasicModule {

    private RoutingTable routingTable;
    private PresenceUpdateHandler updateHandler;
    private PresenceSubscribeHandler subscribeHandler;
    private PresenceManager presenceManager;
    private SessionManager sessionManager;
    private MulticastRouter multicastRouter;
    
    /**
     * Constructs a presence router.
     */
    public PresenceRouter() {
        super("XMPP Presence Router");
    }

    /**
     * Routes presence packets.
     *
     * @param packet the packet to route.
     * @throws NullPointerException if the packet is null.
     */
    public void route(Presence packet) {
        if (packet == null) {
            throw new NullPointerException();
        }
        ClientSession session = sessionManager.getSession(packet.getFrom());
        if (session == null || session.getStatus() == Session.STATUS_AUTHENTICATED) {
            handle(packet);
        }
        else {
            packet.setTo(session.getAddress());
            packet.setFrom((JID)null);
            packet.setError(PacketError.Condition.not_authorized);
            session.process(packet);
        }
    }

    private void handle(Presence packet) {
        JID recipientJID = packet.getTo();
        
        boolean recipJIDThisServer = false;
        if (recipientJID != null && XMPPServer.getInstance().getLocalDomains().contains(recipientJID.getDomain()))
            recipJIDThisServer = true;
        
        // Check if the packet was sent to the server hostname
        if (recipientJID != null && recipientJID.toBareJID() == null
                    && recipientJID.getResource() == null 
                    && recipJIDThisServer) {
            if (packet.getElement().element("addresses") != null) {
                // Presence includes multicast processing instructions. Ask the multicastRouter
                // to route this packet
                multicastRouter.route(packet);
                return;
            }
        }
        try {
            Presence.Type type = packet.getType();
            // Presence updates (null is 'available')
            if (type == null || Presence.Type.unavailable == type) {
                // check for local server target
                if (recipientJID == null || recipientJID.getDomain() == null ||
                        "".equals(recipientJID.getDomain()) || (recipientJID.toBareJID() == null &&
                        recipientJID.getResource() == null) &&
                        recipJIDThisServer) {
                    updateHandler.process(packet);
                }
                else {
                    // Check that sender session is still active
                    Session session = sessionManager.getSession(packet.getFrom());
                    if (session != null && session.getStatus() == Session.STATUS_CLOSED) {
                        Log.warn("Rejected available presence: " + packet + " - " + session);
                        return;
                    }

                    // The user sent a directed presence to an entity
                    // Broadcast it to all connected resources
                    for (ChannelHandler route : routingTable.getRoutes(recipientJID)) {
                        // Register the sent directed presence
                        updateHandler.directedPresenceSent(packet, route, recipientJID.toString());
                        // Route the packet
                        route.process(packet);
                    }
                }

            }
            else if (Presence.Type.subscribe == type // presence subscriptions
                    || Presence.Type.unsubscribe == type
                    || Presence.Type.subscribed == type
                    || Presence.Type.unsubscribed == type)
            {
                subscribeHandler.process(packet);
            }
            else if (Presence.Type.probe == type) {
                // Handle a presence probe sent by a remote server
                presenceManager.handleProbe(packet);
            }
            else {
                // It's an unknown or ERROR type, just deliver it because there's nothing
                // else to do with it
                ChannelHandler route = routingTable.getRoute(recipientJID);
                if (route != null) {
                    route.process(packet);
                }
            }

        }
        catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error.routing"), e);
            Session session = sessionManager.getSession(packet.getFrom());
            if (session != null) {
                Connection conn = session.getConnection();
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);
        routingTable = server.getRoutingTable();
        updateHandler = server.getPresenceUpdateHandler();
        subscribeHandler = server.getPresenceSubscribeHandler();
        presenceManager = server.getPresenceManager();
        multicastRouter = server.getMulticastRouter();
        sessionManager = server.getSessionManager();
    }
}
