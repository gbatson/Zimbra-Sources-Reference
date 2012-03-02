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
package com.zimbra.cs.mailclient.util;

import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream filter for tracing mail client output to server.
 */
public class TraceOutputStream extends OutputStream {
    private final OutputStream out;
    private final PrintStream traceOut;
    private String prefix = PREFIX;
    private boolean enabled = true;
    private boolean eol = true;
    private boolean closed;

    private static final String PREFIX = "C: ";
    
    public TraceOutputStream(OutputStream out, PrintStream traceOut) {
        this.out = out;
        this.traceOut = traceOut != null ? traceOut : System.out;
    }

    public TraceOutputStream(OutputStream out) {
        this(out, System.out);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean suspendTrace(String msg) {
        if (closed || !enabled) return false;
        if (msg != null) {
            if (eol) traceOut.print(prefix);
            traceOut.print(msg);
            eol = msg.endsWith("\n");
        }
        enabled = false;
        return true;
    }

    public void resumeTrace() {
        enabled = true;
    }
    
    @Override
    public void write(int b) throws IOException {
        checkClosed();
        out.write(b);
        if (enabled) {
            if (eol) traceOut.print(prefix);
            printByte((byte) b);
            eol = (b == '\n');
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        checkClosed();
        if (enabled) {
            while (--len >= 0) write(buf[off++]);
        } else {
            out.write(buf, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        checkClosed();
        out.flush();
        if (enabled) {
            traceOut.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            traceOut.flush();
            out.close();
            closed = true;
        }
    }
    
    private void printByte(byte b) {
        switch (b) {
        case '\r':
            break;
        case '\n':
            traceOut.println();
            break;
        default:
            traceOut.print(Ascii.pp(b));
        }
    }

    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
    }
}
