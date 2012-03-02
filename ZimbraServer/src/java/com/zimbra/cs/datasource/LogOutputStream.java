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
package com.zimbra.cs.datasource;

import com.zimbra.common.util.Log;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class LogOutputStream extends OutputStream {
    private final Log log;
    private final ByteArrayOutputStream baos;
    private boolean closed;

    public LogOutputStream(Log log) {
        this.log = log;
        baos = new ByteArrayOutputStream(256);
    }

    @Override
    public void write(int b) throws IOException {
        if (closed) throw new IOException("stream is closed");
        if (b == '\n') {
            flushLine();
        } else {
            baos.write(b);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            // Make sure any remaining bytes are flushed to log file
            if (baos.size() > 0) {
                flushLine();
            }
            closed = true;
        }
    }
    
    private void flushLine() {
        String line = baos.toString();
        if (line.endsWith("\r")) {
            line = line.substring(0, line.length() - 1);
        }
        log.debug(line);
        baos.reset();
    }
}
