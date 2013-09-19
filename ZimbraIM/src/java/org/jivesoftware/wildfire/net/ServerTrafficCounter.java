/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.wildfire.stats.Statistic;
import org.jivesoftware.wildfire.stats.StatisticsManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A ServerTrafficCounter counts the number of bytes read and written by the server. This
 * includes client-server, server-server, external components and connection managers traffic.
 * Note that traffic is monitored only for entities that are directly connected to the server.
 * However, traffic generated by file transfers is not considered unless files were sent using
 * the in-band method.
 *
 * @author Gaston Dombiak
 */
public class ServerTrafficCounter {

    /**
     * Outgoing server traffic counter.
     */
    private static final AtomicLong outgoingCounter = new AtomicLong(0);
    /**
     * Incoming server traffic counter.
     */
    private static final AtomicLong incomingCounter = new AtomicLong(0);

    private static final String trafficStatGroup = "server_bytes";
    private static final String incomingStatKey = "server_bytes_in";
    private static final String outgoingStatKey = "server_bytes_out";

    /**
     * Creates and adds statistics to statistic manager.
     */
    public static void initStatistics() {
        addReadBytesStat();
        addWrittenBytesStat();
    }

    /**
     * Wraps the specified input stream to count the number of bytes that were read.
     *
     * @param originalStream the input stream to wrap.
     * @return The wrapped input stream over the original stream.
     */
    public static InputStream wrapInputStream(InputStream originalStream) {
        return new InputStreamWrapper(originalStream);
    }

    /**
     * Wraps the specified output stream to count the number of bytes that were written.
     *
     * @param originalStream the output stream to wrap.
     * @return The wrapped output stream over the original stream.
     */
    public static OutputStream wrapOutputStream(OutputStream originalStream) {
        return new OutputStreamWrapper(originalStream);
    }

    /**
     * Wraps the specified readable channel to count the number of bytes that were read.
     *
     * @param originalChannel the readable byte channel to wrap.
     * @return The wrapped readable channel over the original readable channel .
     */
    public static ReadableByteChannel wrapReadableChannel(ReadableByteChannel originalChannel) {
        return new ReadableByteChannelWrapper(originalChannel);
    }

    /**
     * Wraps the specified writable channel to count the number of bytes that were written.
     *
     * @param originalChannel the writable byte channel to wrap.
     * @return The wrapped writable channel over the original writable channel .
     */
    public static WritableByteChannel wrapWritableChannel(WritableByteChannel originalChannel) {
        return new WritableByteChannelWrapper(originalChannel);
    }

    private static void addReadBytesStat() {
        // Register a statistic.
        Statistic statistic = new Statistic() {
            public String getName() {
                return LocaleUtils.getLocalizedString("server_bytes.stats.incoming.name");
            }

            public Type getStatType() {
                return Type.rate;
            }

            public String getDescription() {
                return LocaleUtils.getLocalizedString("server_bytes.stats.incoming.description");
            }

            public String getUnits() {
                return LocaleUtils.getLocalizedString("server_bytes.stats.incoming.label");
            }

            public double sample() {
                // Divide result by 1024 so that we return the result in Kb.
                return incomingCounter.getAndSet(0)/1024;
            }
        };
        StatisticsManager.getInstance()
                .addMultiStatistic(incomingStatKey, trafficStatGroup, statistic);
    }

    private static void addWrittenBytesStat() {
        // Register a statistic.
        Statistic statistic = new Statistic() {
            public String getName() {
                return LocaleUtils.getLocalizedString("server_bytes.stats.outgoing.name");
            }

            public Type getStatType() {
                return Type.rate;
            }

            public String getDescription() {
                return LocaleUtils.getLocalizedString("server_bytes.stats.outgoing.description");
            }

            public String getUnits() {
                return LocaleUtils.getLocalizedString("server_bytes.stats.outgoing.label");
            }

            public double sample() {
                return outgoingCounter.getAndSet(0)/1024;
            }
        };
        StatisticsManager.getInstance()
                .addMultiStatistic(outgoingStatKey, trafficStatGroup, statistic);
    }

    /**
     * Wrapper on an input stream to intercept and count number of read bytes.
     */
    private static class InputStreamWrapper extends InputStream {
        /**
         * Original input stream being wrapped to count incmoing traffic.
         */
        private InputStream originalStream;

        public InputStreamWrapper(InputStream originalStream) {
            this.originalStream = originalStream;
        }

        public int read() throws IOException {
            int readByte = originalStream.read();
            if (readByte > -1) {
                incomingCounter.incrementAndGet();
            }
            return readByte;
        }

        public int read(byte b[]) throws IOException {
            int bytes = originalStream.read(b);
            if (bytes > -1) {
                incomingCounter.getAndAdd(bytes);
            }
            return bytes;
        }

        public int read(byte b[], int off, int len) throws IOException {
            int bytes = originalStream.read(b, off, len);
            if (bytes > -1) {
                incomingCounter.getAndAdd(bytes);
            }
            return bytes;
        }

        public int available() throws IOException {
            return originalStream.available();
        }

        public void close() throws IOException {
            originalStream.close();
        }

        public synchronized void mark(int readlimit) {
            originalStream.mark(readlimit);
        }

        public boolean markSupported() {
            return originalStream.markSupported();
        }

        public synchronized void reset() throws IOException {
            originalStream.reset();
        }

        public long skip(long n) throws IOException {
            return originalStream.skip(n);
        }
    }

    /**
     * Wrapper on an output stream to intercept and count number of written bytes.
     */
    private static class OutputStreamWrapper extends OutputStream {
        /**
         * Original output stream being wrapped to count outgoing traffic.
         */
        private OutputStream originalStream;

        public OutputStreamWrapper(OutputStream originalStream) {
            this.originalStream = originalStream;
        }

        public void write(int b) throws IOException {
            // forward request to wrapped stream
            originalStream.write(b);
            // update outgoingCounter
            outgoingCounter.incrementAndGet();
        }

        public void write(byte b[]) throws IOException {
            // forward request to wrapped stream
            originalStream.write(b);
            // update outgoingCounter
            outgoingCounter.getAndAdd(b.length);
        }

        public void write(byte b[], int off, int len) throws IOException {
            // forward request to wrapped stream
            originalStream.write(b, off, len);
            // update outgoingCounter
            outgoingCounter.getAndAdd(b.length);
        }

        public void close() throws IOException {
            originalStream.close();
        }

        public void flush() throws IOException {
            originalStream.flush();
        }
    }

    /**
     * Wrapper on a ReadableByteChannel to intercept and count number of read bytes.
     */
    private static class ReadableByteChannelWrapper implements ReadableByteChannel {
        private ReadableByteChannel originalChannel;

        public ReadableByteChannelWrapper(ReadableByteChannel originalChannel) {
            this.originalChannel = originalChannel;
        }

        public int read(ByteBuffer dst) throws IOException {
            int bytes = originalChannel.read(dst);
            if (bytes > -1) {
                incomingCounter.getAndAdd(bytes);
            }
            return bytes;
        }

        public void close() throws IOException {
            originalChannel.close();
        }

        public boolean isOpen() {
            return originalChannel.isOpen();
        }
    }

    /**
     * Wrapper on a WritableByteChannel to intercept and count number of written bytes.
     */
    private static class WritableByteChannelWrapper implements WritableByteChannel {
        private WritableByteChannel originalChannel;

        public WritableByteChannelWrapper(WritableByteChannel originalChannel) {
            this.originalChannel = originalChannel;
        }

        public void close() throws IOException {
            originalChannel.close();
        }

        public boolean isOpen() {
            return originalChannel.isOpen();
        }

        public int write(ByteBuffer src) throws IOException {
            int bytes = originalChannel.write(src);
            outgoingCounter.getAndAdd(bytes);
            return bytes;
        }
    }
}
