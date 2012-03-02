/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
package com.zimbra.cs.imap;

import com.google.common.io.Closeables;
import com.zimbra.common.io.TcpServerInputStream;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.NetUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.stats.ZimbraPerf;
import com.zimbra.cs.util.Config;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

final class TcpImapHandler extends ImapHandler {
    private Socket socket;
    private TcpServerInputStream mInputStream;
    private String remoteIp;
    private TcpImapRequest request;

    TcpImapHandler(ImapServer server) {
        super(server);
    }

    @Override
    String getRemoteIp() {
        return remoteIp;
    }

    @Override
    protected boolean setupConnection(Socket sock) throws IOException {
        socket = sock;
        remoteIp = sock.getInetAddress().getHostAddress();
        INFO("connected");

        mInputStream = new TcpServerInputStream(sock.getInputStream());
        mOutputStream = new BufferedOutputStream(sock.getOutputStream());

        if (!Config.userServicesEnabled()) {
            ZimbraLog.imap.debug("dropping connection because user services are disabled");
            dropConnection();
            return false;
        }

        sendGreeting();

        return true;
    }

    @Override
    protected boolean authenticate() {
        // we auth with the LOGIN command (and more to come)
        return true;
    }

    @Override
    protected void setIdle(boolean idle) {
        super.setIdle(idle);
        ImapSession i4selected = mSelectedFolder;
        if (i4selected != null)
            i4selected.updateAccessTime();
    }

    @Override
    protected boolean processCommand() throws IOException {
        // FIXME: throw an exception instead?
        if (mInputStream == null) {
            clearRequest();
            return STOP_PROCESSING;
        }
        setLoggingContext();

        if (request == null) {
            request = new TcpImapRequest(mInputStream, this);
        }
        boolean complete = true;
        try {
            request.continuation();
            if (request.isMaxRequestSizeExceeded()) {
                setIdle(false);  // FIXME Why for only this error?
                throw new ImapParseException(request.getTag(), "maximum request size exceeded");
            }

            long start = ZimbraPerf.STOPWATCH_IMAP.start();
            // check account status before executing command
            if (!checkAccountStatus()) {
                return STOP_PROCESSING;
            }
            boolean keepGoing;
            if (mAuthenticator != null && !mAuthenticator.isComplete()) {
                keepGoing = continueAuthentication(request);
            } else {
                keepGoing = executeRequest(request);
            }
            // FIXME Shouldn't we do these before executing the request??
            setIdle(false);
            ZimbraPerf.STOPWATCH_IMAP.stop(start);
            if (mLastCommand != null) {
                ZimbraPerf.IMAP_TRACKER.addStat(mLastCommand.toUpperCase(), start);
            }
            mConsecutiveBAD = 0;
            return keepGoing;
        } catch (TcpImapRequest.ImapContinuationException ice) {
            request.rewind();
            complete = false; // skip clearRequest()
            if (ice.sendContinuation) {
                sendContinuation("send literal data");
            }
            return CONTINUE_PROCESSING;
        } catch (TcpImapRequest.ImapTerminatedException e) {
            return STOP_PROCESSING;
        } catch (ImapParseException e) {
            handleParseException(e);
            return mConsecutiveBAD >= MAXIMUM_CONSECUTIVE_BAD ? STOP_PROCESSING : CONTINUE_PROCESSING;
        } catch (ImapException e) { // session closed
            ZimbraLog.imap.debug("stop processing", e);
            return STOP_PROCESSING;
        } catch (IOException e) {
            if (socket.isClosed()) {
                ZimbraLog.imap.debug("stop processing", e);
                return STOP_PROCESSING;
            }
            throw e;
        } finally {
            if (complete) {
                clearRequest();
            }
            ZimbraLog.clearContext();
        }
    }

    /**
     * Only an IMAP handler thread may call. Don't call by other threads including IMAP session sweeper thread,
     * otherwise concurrency issues will arise.
     */
    private void clearRequest() {
        if (request != null) {
            request.cleanup();
            request = null;
        }
    }

    @Override boolean doSTARTTLS(String tag) throws IOException {
        if (!checkState(tag, State.NOT_AUTHENTICATED)) {
            return CONTINUE_PROCESSING;
        } else if (mStartedTLS) {
            sendNO(tag, "TLS already started");
            return CONTINUE_PROCESSING;
        }
        sendOK(tag, "Begin TLS negotiation now");

        SSLSocketFactory fac = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket tlsconn = (SSLSocket) fac.createSocket(mConnection, mConnection.getInetAddress().getHostName(), mConnection.getPort(), true);
        NetUtil.setSSLEnabledCipherSuites(tlsconn, mConfig.getSslExcludedCiphers());
        tlsconn.setUseClientMode(false);
        startHandshake(tlsconn);
        ZimbraLog.imap.debug("suite: " + tlsconn.getSession().getCipherSuite());
        mInputStream = new TcpServerInputStream(tlsconn.getInputStream());
        mOutputStream = new BufferedOutputStream(tlsconn.getOutputStream());
        mStartedTLS = true;

        return CONTINUE_PROCESSING;
    }

    @Override
    protected void dropConnection(boolean sendBanner) {
        try {
            unsetSelectedFolder(false);
        } catch (Exception e) {
        }

        //TODO use thread pool
        // wait at most 10 seconds for the untagged BYE to be sent, then force the stream closed
        new Thread() {
            @Override
            public void run() {
                if (mOutputStream == null) {
                    return;
                }
                try {
                    sleep(10 * Constants.MILLIS_PER_SECOND);
                } catch (InterruptedException e) {
                }
                Closeables.closeQuietly(mOutputStream);
            }
        }.start();

        if (mCredentials != null && !mGoodbyeSent) {
            ZimbraLog.imap.info("dropping connection for user %s (server-initiated)", mCredentials.getUsername());
        }
        ZimbraLog.addIpToContext(remoteIp);
        try {
            OutputStream os = mOutputStream;
            if (os != null) {
                if (sendBanner && !mGoodbyeSent) {
                    sendBYE();
                }
                os.close();
                mOutputStream = null;
            }
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mAuthenticator != null) {
                mAuthenticator.dispose();
                mAuthenticator = null;
            }
        } catch (IOException e) {
            if (ZimbraLog.imap.isDebugEnabled()) {
                ZimbraLog.imap.debug("I/O error while closing connection", e);
            } else {
                ZimbraLog.imap.debug("I/O error while closing connection: " + e);
            }
        } finally {
            ZimbraLog.clearContext();
        }
    }

    /**
     * Close the IMAP connection immediately without sending an untagged BYE.
     *
     * This is necessarily violating RFC 3501 3.4:
     * <pre>
     *   A server MUST NOT unilaterally close the connection without
     *   sending an untagged BYE response that contains the reason for
     *   having done so.
     * </pre>
     * because there is no easy way to interrupt a blocking read from the socket ({@link Socket#shutdownInput()} is
     * not supported by {@link SSLSocket}) and trying to send an untagged BYE not from this IMAP handler has
     * concurrency issues.
     */
    @Override
    void close() {
        try {
            socket.close(); // blocking read from this socket will throw throw SocketException
        } catch (IOException e) {
            ZimbraLog.imap.debug("Failed to close socket", e);
        }
    }

    @Override protected void notifyIdleConnection() {
        // we can, and do, drop idle connections after the timeout

        // TODO in the TcpServer case, is this duplicated effort with
        // session timeout code that also drops connections?
        ZimbraLog.imap.debug("dropping connection for inactivity");
        dropConnection();
    }

    @Override protected void completeAuthentication() throws IOException {
        mAuthenticator.sendSuccess();
        if (mAuthenticator.isEncryptionEnabled()) {
            // switch to encrypted streams
            mInputStream = new TcpServerInputStream(mAuthenticator.unwrap(mConnection.getInputStream()));
            mOutputStream = mAuthenticator.wrap(mConnection.getOutputStream());
        }
    }

    @Override protected void enableInactivityTimer() throws SocketException {
        mConnection.setSoTimeout(mConfig.getAuthenticatedMaxIdleSeconds() * 1000);
    }

    @Override protected void flushOutput() throws IOException {
        mOutputStream.flush();
    }

    @Override void sendLine(String line, boolean flush) throws IOException {
        OutputStream os = mOutputStream;
        if (os == null)
            return;
        os.write(line.getBytes());
        os.write(LINE_SEPARATOR_BYTES);
        if (flush)
            os.flush();
    }

    void INFO(String message, Throwable e) {
        if (ZimbraLog.imap.isInfoEnabled())
            ZimbraLog.imap.info(withClientInfo(message), e);
    }

    void INFO(String message) {
        if (ZimbraLog.imap.isInfoEnabled())
            ZimbraLog.imap.info(withClientInfo(message));
    }

    private StringBuilder withClientInfo(String message) {
        int length = 64;
        if (message != null)
            length += message.length();
        return new StringBuilder(length).append("[").append(remoteIp).append("] ").append(message);
    }
}
