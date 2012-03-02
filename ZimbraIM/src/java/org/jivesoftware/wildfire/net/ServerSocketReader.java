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
package org.jivesoftware.wildfire.net;

import org.apache.mina.common.IoSession;
import org.dom4j.Element;
import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.PacketRouter;
import org.jivesoftware.wildfire.RoutingTable;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.jivesoftware.wildfire.interceptor.PacketRejectedException;
import org.jivesoftware.wildfire.server.DialbackCreatorSession;
import org.jivesoftware.wildfire.server.IncomingServerSession;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A SocketReader specialized for server connections. This reader will be used when the open
 * stream contains a jabber:server namespace. Server-to-server communication requires two
 * TCP connections between the servers where one is used for sending packets whilst the other
 * connection is used for receiving packets. The connection used for receiving packets will use
 * a ServerSocketReader since the other connection will not receive packets.<p>
 *
 * The received packets will be routed using another thread to ensure that many received packets
 * could be routed at the same time. To avoid creating new threads every time a packet is received
 * each <tt>ServerSocketReader</tt> instance uses a {@link ThreadPoolExecutor}. By default the
 * maximum number of threads that the executor may have is 50. However, this value may be modified
 * by changing the property <b>XMPP_SERVER_PROCESSING_MAX_THREADS</b>.
 *
 * @author Gaston Dombiak
 */
public class ServerSocketReader extends SocketReader {

    /**
     * Pool of threads that are available for processing the requests.
     */
    private ThreadPoolExecutor threadPool;

    public ServerSocketReader(PacketRouter router, RoutingTable routingTable, 
                Socket socket, SocketConnection connection)
    {
        super(router, routingTable, socket, connection);
        // Create a pool of threads that will process received packets. If more threads are
        // required then the command will be executed on the SocketReader process
        int coreThreads = IMConfig.XMPP_SERVER_PROCESSING_CORE_THREADS.getInt();
        int maxThreads = IMConfig.XMPP_SERVER_PROCESSING_MAX_THREADS.getInt();
        int queueSize = IMConfig.XMPP_SERVER_PROCESSING_QUEUE.getInt();
        threadPool =
            new ThreadPoolExecutor(coreThreads, maxThreads, 60, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(queueSize),
                        new ThreadPoolExecutor.CallerRunsPolicy());
    }
    
    public ServerSocketReader(PacketRouter router, RoutingTable routingTable, 
                IoSession nioSocket, SocketConnection connection) {
        super(router, routingTable, nioSocket, connection);
        // Create a pool of threads that will process received packets. If more threads are
        // required then the command will be executed on the SocketReader process
        int coreThreads = IMConfig.XMPP_SERVER_PROCESSING_CORE_THREADS.getInt();
        int maxThreads = IMConfig.XMPP_SERVER_PROCESSING_MAX_THREADS.getInt();
        int queueSize = IMConfig.XMPP_SERVER_PROCESSING_QUEUE.getInt();
        threadPool =
            new ThreadPoolExecutor(coreThreads, maxThreads, 60, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(queueSize),
                        new ThreadPoolExecutor.CallerRunsPolicy());
    }
    
    protected void process(Element doc) throws Exception {
        if (doc != null && session != null && session instanceof DialbackCreatorSession) {
            DialbackCreatorSession dbcs = (DialbackCreatorSession)session;
            session = dbcs.processSecondElement(doc);
        } else {
            super.process(doc);
        }
    }
    

    /**
     * Processes the packet in another thread if the packet has not been rejected.
     *
     * @param packet the received packet.
     */
    protected void processIQ(final IQ packet) throws UnauthorizedException {
        try {
            packetReceived(packet);
            // Process the packet in another thread
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        ServerSocketReader.super.processIQ(packet);
                    }
                    catch (UnauthorizedException e) {
                        Log.error("Error processing packet", e);
                    }
                }
            });
        }
        catch (PacketRejectedException e) {
            Log.debug("IQ rejected: " + packet.toXML(), e);
        }
    }

    /**
     * Processes the packet in another thread if the packet has not been rejected.
     *
     * @param packet the received packet.
     */
    protected void processPresence(final Presence packet) throws UnauthorizedException {
        try {
            packetReceived(packet);
            // Process the packet in another thread
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        ServerSocketReader.super.processPresence(packet);
                    }
                    catch (UnauthorizedException e) {
                        Log.error("Error processing packet", e);
                    }
                }
            });
        }
        catch (PacketRejectedException e) {
            Log.debug("Presence rejected: " + packet.toXML(), e);
        }
    }

    /**
     * Processes the packet in another thread if the packet has not been rejected.
     *
     * @param packet the received packet.
     */
    protected void processMessage(final Message packet) throws UnauthorizedException {
        try {
            packetReceived(packet);
            // Process the packet in another thread
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        ServerSocketReader.super.processMessage(packet);
                    }
                    catch (UnauthorizedException e) {
                        Log.error("Error processing packet", e);
                    }
                }
            });
        }
        catch (PacketRejectedException e) {
            Log.debug("Message rejected: " + packet.toXML(), e);
        }
    }

    /**
     * Remote servers may send subsequent db:result packets so we need to process them in order
     * to validate new domains.
     *
     * @param doc the unknown DOM element that was received
     * @return true if the packet is a db:result packet otherwise false.
     */
    protected boolean processUnknowPacket(Element doc) {
        // Handle subsequent db:result packets
        if ("db".equals(doc.getNamespacePrefix()) && "result".equals(doc.getName())) {
            if (!((IncomingServerSession) session).validateSubsequentDomain(doc)) {
                open = false;
            }
            return true;
        }
        else if ("db".equals(doc.getNamespacePrefix()) && "verify".equals(doc.getName())) {
            // The Receiving Server is reusing an existing connection for sending the
            // Authoritative Server a request for verification of a key
            ((IncomingServerSession) session).verifyReceivedKey(doc);
            return true;
        }
        return false;
    }

    /**
     * Make sure that the received packet has a TO and FROM values defined and that it was sent
     * from a previously validated domain. If the packet does not matches any of the above
     * conditions then a PacketRejectedException will be thrown.
     *
     * @param packet the received packet.
     * @throws PacketRejectedException if the packet does not include a TO or FROM or if the packet
     *                                 was sent from a domain that was not previously validated.
     */
    private void packetReceived(Packet packet) throws PacketRejectedException {
        if (packet.getTo() == null || packet.getFrom() == null) {
            Log.debug("Closing IncomingServerSession due to packet with no TO or FROM: " +
                    packet.toXML());
            // Send a stream error saying that the packet includes no TO or FROM
            StreamError error = new StreamError(StreamError.Condition.improper_addressing);
            connection.deliverRawText(error.toXML());
            // Close the underlying connection
            connection.close();
            open = false;
            throw new PacketRejectedException("Packet with no TO or FROM attributes");
        }
        else if (!((IncomingServerSession) session).isValidDomain(packet.getFrom().getDomain())) {
            Log.debug("Closing IncomingServerSession due to packet with invalid domain: " +
                    packet.toXML());
            // Send a stream error saying that the packet includes an invalid FROM
            StreamError error = new StreamError(StreamError.Condition.invalid_from);
            connection.deliverRawText(error.toXML());
            // Close the underlying connection
            connection.close();
            open = false;
            throw new PacketRejectedException("Packet with invalid Domain");
        }
    }

    protected void shutdown() {
        super.shutdown();
        // Shutdown the pool of threads that are processing packets sent by
        // the remote server
        threadPool.shutdown();
    }

    boolean createSession(String namespace, String host, Element streamElt) throws UnauthorizedException, XmlPullParserException,
            IOException {
        if (getNamespace().equals(namespace)) {
            // The connected client is a server so create an IncomingServerSession
            session = IncomingServerSession.createSession(host, connection, streamElt);
            return true;
        }
        return false;
    }

    String getNamespace() {
        return "jabber:server";
    }

    String getName() {
        return "Server SR - " + hashCode();
    }

    boolean validateHost() {
        return true;
    }
}
