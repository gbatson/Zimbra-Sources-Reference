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

import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

import org.dom4j.Element;
import org.jivesoftware.util.IMConfig;
import org.jivesoftware.wildfire.ChannelHandler;
import org.jivesoftware.wildfire.Connection;
import org.jivesoftware.wildfire.ConnectionCloseListener;
import org.jivesoftware.wildfire.PacketDeliverer;
import org.jivesoftware.wildfire.PacketException;
import org.jivesoftware.wildfire.RoutingTable;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.wildfire.SessionManager;
import org.jivesoftware.wildfire.StreamID;
import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.jivesoftware.wildfire.net.CloudRoutingSocketReader;
import org.jivesoftware.wildfire.net.SSLConfig;
import org.jivesoftware.wildfire.net.SocketConnection;
import org.jivesoftware.wildfire.net.StdSocketConnection;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.PacketExtension;
import org.xmpp.packet.Presence;
import org.xmpp.packet.StreamError;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.ZimbraAuthToken;
import com.zimbra.cs.service.AuthProvider;

/**
 * This class represents a route in the *LOCAL* cloud, ie between trusted servers
 * in the same backend.
 */
public class CloudRouteSession extends Session {

    static public CloudRouteSession create(String hostname, CloudRoutingSocketReader reader, SocketConnection connection, Element streamElt) {
        String encodedAuthToken = streamElt.attributeValue("authtoken");
        try {
            AuthToken token = AuthToken.getAuthToken(encodedAuthToken);
            
            if (token.isAdmin()) {
                CloudRouteSession toRet = new CloudRouteSession(connection, XMPPServer.getInstance().getSessionManager().nextStreamID());
                connection.init(toRet);
                ZimbraLog.im.info("Accepted CloudRouting connection from host "+connection.toString());
                synchronized(sCloseListener) {
                    connection.registerCloseListener(sCloseListener, toRet);
                    SessionManager.getInstance().registerCloudRoutingSession(toRet);
                }
                return toRet;
            } else {
                ZimbraLog.im.warn("Rejecting CloudRouting connection -- Got non-admin auth token on cloud routing connection from "+connection.toString()+" token = "+token);
           }
        } catch (Exception e) {
            ZimbraLog.im.warn("Rejecting CloudRouting connection -- caught exception attempting to create CloudRoutingSession from "+connection.toString(), e);
        }
        
        StreamError error = new StreamError(StreamError.Condition.not_authorized);
        connection.deliverRawText(error.toXML());
        connection.close();
        return null;
    }

    static CloudRouteSession connect(Server targetServer) throws Exception {
        String hostname = targetServer.getAttr(Provisioning.A_zimbraServiceHostname);
        int port = IMConfig.XMPP_CLOUDROUTING_PORT.getInt();
        
        AuthToken adminAuthToken = AuthProvider.getAdminAuthToken();
        
        Socket socket = SSLConfig.createSSLSocket();
        try {
            
            ZimbraLog.im.info("LocalCloudRoute: Trying to connect (3)  " + hostname + ":" + port);
            
            // Establish a TCP connection to the Receiving Server
            socket.connect(new InetSocketAddress(hostname, port), 30000);
            ZimbraLog.im.debug("LocalCloudRoute: Plain connection to " + hostname + ":" + port + " successful");
            
            SocketConnection connection = new StdSocketConnection(new ErrorPacketDeliverer(), socket, false); 
            
            // Send the stream header
            StringBuilder openingStream = new StringBuilder();
            openingStream.append("<stream:stream");
            openingStream.append(" xmlns:stream=\"http://etherx.jabber.org/streams\"");
            openingStream.append(" xmlns=\"jabber:cloudrouting\"");
            openingStream.append(" to=\"").append(hostname).append("\"");
            openingStream.append(" authtoken=\"").append(adminAuthToken.getEncoded()).append("\"");
            openingStream.append(" version=\"1.0\">\n");
            ZimbraLog.im.debug("LocalCloudRoute - Sending stream header: "+openingStream.toString()); 
            connection.deliverRawText(openingStream.toString());
            
            CloudRoutingSocketReader ssReader = new CloudRoutingSocketReader(XMPPServer.getInstance().getPacketRouter(), 
                XMPPServer.getInstance().getRoutingTable(), socket, connection);
            
            Thread readerThread = new Thread(ssReader, "CloudRoutingReaderThread-"+connection.toString());
            readerThread.setDaemon(true);
            readerThread.start();
             
            CloudRouteSession toRet = new CloudRouteSession(targetServer, connection, XMPPServer.getInstance().getSessionManager().nextStreamID());
            connection.init(toRet);
            ssReader.setSession(toRet);
            synchronized(sCloseListener) {
                connection.registerCloseListener(sCloseListener, toRet);
                SessionManager.getInstance().registerCloudRoutingSession(toRet);
            }
            return toRet;
        }
        catch (Exception e) {
            if (ZimbraLog.im.isDebugEnabled()) {
                ZimbraLog.im.debug("Error trying to connect to remote server: " + hostname + ":" + port, e);
            } else {
                if (ZimbraLog.im.isInfoEnabled()) {
                    ZimbraLog.im.info("Error trying to connect to remote server: " + hostname + ":" + port);
                }
            }
            throw e;
        }
    }
    
    private static void remoteDeliveryFailed(Packet packet) {
        JID from = packet.getFrom();
        JID to = packet.getTo();
        
        ZimbraLog.im.debug("CloudRouteSession.remoteDeliveryFailed for packet "+packet);
        
        // TODO Send correct error condition: timeout or not_found depending on the real error
        try {
            if (packet instanceof IQ) {
                IQ reply = new IQ();
                reply.setID(((IQ) packet).getID());
                reply.setTo(from);
                reply.setFrom(to);
                reply.setChildElement(((IQ) packet).getChildElement().createCopy());
                reply.setError(PacketError.Condition.remote_server_not_found);
                ChannelHandler route = sRoutingTable.getRoute(reply.getTo());
                if (route != null) {
                    route.process(reply);
                }
            }
            else if (packet instanceof Presence) {
                Presence reply = new Presence();
                reply.setID(packet.getID());
                reply.setTo(from);
                reply.setFrom(to);
                reply.setError(PacketError.Condition.remote_server_not_found);
                ChannelHandler route = sRoutingTable.getRoute(reply.getTo());
                if (route != null) {
                    route.process(reply);
                }
            }
            else if (packet instanceof Message) {
                Message reply = new Message();
                reply.setID(packet.getID());
                reply.setTo(from);
                reply.setFrom(to);
                reply.setType(((Message)packet).getType());
                reply.setThread(((Message)packet).getThread());
                reply.setError(PacketError.Condition.remote_server_not_found);
                ChannelHandler route = sRoutingTable.getRoute(reply.getTo());
                if (route != null) {
                    route.process(reply);
                }
            }
        }
        catch (UnauthorizedException e) {
        }
        catch (Exception e) {
            ZimbraLog.im.warn("Error returning error to sender. Original packet: " + packet, e);
        }
    } 

    private CloudRouteSession(Connection conn, StreamID streamID) {
        super("cloudroute.fake.domain.com", conn, streamID);
        mServer = null;
    }
    /**
     * 
     */
    private CloudRouteSession(Server targetServer, Connection conn, StreamID streamID) {
        super(targetServer.getName(), conn, streamID);
        mServer = targetServer;
    }
    
    @Override
    public JID getAddress() {
        if (mServer != null)
            return new JID(mServer.getName());
        else
            return null;
    }
    
    @Override
    public String getAvailableStreamFeatures() {
        return null;
    }
    
    public void process(Packet packet) throws UnauthorizedException, PacketException {
//        PacketExtension pe = new PacketExtension("hopcount", "zimbra:cloudrouting");
        String curHopCount = packet.getElement().attributeValue("x-zimbra-hopcount", "0");
        int hopCount = Integer.parseInt(curHopCount);
        packet.getElement().addAttribute("x-zimbra-hopcount", Integer.toString(++hopCount));
        if (hopCount<5 && conn != null&& !conn.isClosed()) {
            ZimbraLog.im.debug("CloudRouteSession delivering packet: "+packet);
            conn.deliver(packet);
        } else {
            remoteDeliveryFailed(packet);
        }
    }

    Server getServer() { return mServer; }
    
    private static RoutingTable sRoutingTable = XMPPServer.getInstance().getRoutingTable();
    
    private Server mServer;
    
    private static class CloseListener implements ConnectionCloseListener {
        public synchronized void onConnectionClose(Object handback) {
            SessionManager.getInstance().unregisterCloudRoutingSession((Session)handback);
        }
    }
    private static CloseListener sCloseListener = new CloseListener(); 
    
    private static class ErrorPacketDeliverer implements PacketDeliverer {
        public void deliver(Packet packet) throws UnauthorizedException, PacketException {
            remoteDeliveryFailed(packet);
        }
    }
    
}
