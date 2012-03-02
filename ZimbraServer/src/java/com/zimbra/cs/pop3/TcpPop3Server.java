/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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

package com.zimbra.cs.pop3;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.stats.RealtimeStatsCallback;
import com.zimbra.cs.stats.ZimbraPerf;
import com.zimbra.cs.tcpserver.ProtocolHandler;
import com.zimbra.cs.tcpserver.TcpServer;

import java.util.HashMap;
import java.util.Map;

public class TcpPop3Server extends TcpServer implements Pop3Server, RealtimeStatsCallback {
    public TcpPop3Server(Pop3Config config) throws ServiceException {
        super(config.isSslEnabled() ? "Pop3SSLServer" : "Pop3Server", config);
        ZimbraPerf.addStatsCallback(this);
    }

    @Override
    protected ProtocolHandler newProtocolHandler() {
        return new TcpPop3Handler(this);
    }

    @Override
    public Pop3Config getConfig() {
        return (Pop3Config) super.getConfig();
    }

    /**
     * Implementation of {@link RealtimeStatsCallback} that returns the number
     * of active handlers and number of threads for this server.
     */
    public Map<String, Object> getStatData() {
        Map<String, Object> data = new HashMap<String, Object>();
        if (getConfig().isSslEnabled()) {
            data.put(ZimbraPerf.RTS_POP_SSL_CONN, numActiveHandlers());
            data.put(ZimbraPerf.RTS_POP_SSL_THREADS, numThreads());
        } else {
            data.put(ZimbraPerf.RTS_POP_CONN, numActiveHandlers());
            data.put(ZimbraPerf.RTS_POP_THREADS, numThreads());
        }
        return data;
    }
}
