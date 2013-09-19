/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.milter;

import static com.zimbra.cs.account.ZAttrProvisioning.A_zimbraMilterNumThreads;
import static com.zimbra.cs.account.ZAttrProvisioning.A_zimbraMilterBindAddress;
import static com.zimbra.cs.account.ZAttrProvisioning.A_zimbraMilterBindPort;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.server.ServerConfig;
import com.zimbra.cs.util.Config;

public class MilterConfig extends ServerConfig {
    private static final String PROTOCOL = "MILTER";
    
    public MilterConfig() {
        super(PROTOCOL, false);
    }
    
    @Override public int getBindPort() {
        int port = LC.milter_bind_port.intValue();
        return port != 0 ? port : getIntAttr(A_zimbraMilterBindPort, Config.D_MILTER_BIND_PORT);
    }

    @Override public String getBindAddress() {
        String addr = LC.milter_bind_address.value();
        return addr != null ? addr : getAttr(A_zimbraMilterBindAddress, "127.0.0.1");
    }
    
    @Override
    public int getNumThreads() {
        return getIntAttr(A_zimbraMilterNumThreads, super.getNumThreads());
    }
    
    @Override public Log getLog() {
        return ZimbraLog.milter;
    }

    // for now nio tweaks are in LC
    
    @Override public int getNioMaxSessions() {
        return LC.milter_max_sessions.intValue();
    }
    
    @Override public int getMaxIdleSeconds() {
        return LC.milter_max_idle_seconds.intValue();
    }
    
    @Override
    public int getNioMaxScheduledWriteBytes() {
        return LC.milter_max_scheduled_write_bytes.intValue();
    }

    @Override
    public int getNioWriteTimeout() {
        return LC.milter_write_timeout.intValue();
    }

    @Override
    public int getNioWriteChunkSize() {
        return LC.milter_write_chunk_size.intValue();
    }

    @Override
    public int getNioMinThreads() {
        return LC.milter_min_threads.intValue();
    }

    @Override
    public int getNioThreadKeepAliveTime() {
        return LC.milter_thread_keep_alive_time.intValue();
    }
}
