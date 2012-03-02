/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/*
 * Created on May 26, 2004
 */
package com.zimbra.cs.service.account;

import org.mortbay.io.EndPoint;
import org.mortbay.io.nio.SelectChannelEndPoint;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.thread.Timeout;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.gal.GalSearchControl;
import com.zimbra.cs.gal.GalSearchParams;
import com.zimbra.soap.ZimbraSoapContext;

import java.util.Map;

/**
 * @author schemers
 */
public class SyncGal extends AccountDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        disableJettyTimeout();
        
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));

        if (!canAccessAccount(zsc, account))
            throw ServiceException.PERM_DENIED("can not access account");
        
        String tokenAttr = request.getAttribute(MailConstants.A_TOKEN, "");
        String galAcctId = request.getAttribute(AccountConstants.A_ID, null);
        boolean idOnly   = request.getAttributeBool(AccountConstants.A_ID_ONLY, false);

        GalSearchParams params = new GalSearchParams(account, zsc);
        params.setType(Provisioning.GAL_SEARCH_TYPE.ALL);
        params.setToken(tokenAttr);
        params.setRequest(request);
        params.setResponseName(AccountConstants.SYNC_GAL_RESPONSE);
        params.setIdOnly(idOnly);
        if (galAcctId != null)
        	params.setGalSyncAccount(Provisioning.getInstance().getAccountById(galAcctId));
        GalSearchControl gal = new GalSearchControl(params);
        gal.sync();
        return params.getResultCallback().getResponse();
    }

    @Override
    public boolean needsAuth(Map<String, Object> context) {
        return true;
    }
    
    
    /**
     * Implemented for bug 56458..
     * 
     * Disable the Jetty timeout for the SelectChannelConnector and the SSLSelectChannelConnector
     * for this request.
     * 
     * By default (and our normal configuration) Jetty has a 30 second idle timeout (10 if the server is busy) for 
     * connection endpoints. There's another task that keeps track of what connections have timeouts and periodically
     * works over a queue and closes endpoints that have been timed out. This plays havoc with downloads to slow connections
     * and whenever we have a long pause while working to create an archive.
     * 
     * This method removes this request from the queue to check timeout via the mechanisms that are normally used
     * after jetty completes a request. Given that we don't send a content-length down to the browser for
     * archive responses, we have to close the socket to tell the browser its done. Since we have to do that.. leaving this 
     * endpoint without a timeout is safe. If the connection was being reused (ie keep-alive) this could have issues, but its not 
     * in this case.
     */
    private void disableJettyTimeout() {
        if (LC.zimbra_gal_sync_disable_timeout.booleanValue()) {
            EndPoint endPoint = HttpConnection.getCurrentConnection().getEndPoint();
            if (endPoint instanceof SelectChannelEndPoint) {
                SelectChannelEndPoint scEndPoint = (SelectChannelEndPoint) endPoint;
                Timeout.Task task = scEndPoint.getTimeoutTask();
                if (task != null) {
                    task.cancel();
                }
            }
        }
    }
}
