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
package com.zimbra.cs.service.offline;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.BufferStreamRequestEntity;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraHttpConnectionManager;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ZimbraAuthTokenEncoded;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineDocumentHandlers {
    static Pattern pattern = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}:\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}");

    static Account getTargetAccount(ZimbraSoapContext zsc)
        throws ServiceException {
        String acctId = zsc.getRequestedAccountId();
        Provisioning prov = Provisioning.getInstance();
        return prov.get(Provisioning.AccountBy.id, acctId);
    }

    static class DiffDocument extends com.zimbra.cs.service.wiki.DiffDocument {
        protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
            return getTargetAccount(zsc).getName();
        }
    }

    static class ListDocumentRevisions extends
        com.zimbra.cs.service.wiki.ListDocumentRevisions {
        protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
            return getTargetAccount(zsc).getName();
        }
    }

    static class GetWiki extends com.zimbra.cs.service.wiki.GetWiki {
        protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
            return getTargetAccount(zsc).getName();
        }
    }

    static class SaveDocument extends com.zimbra.cs.service.wiki.SaveDocument {
        protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
            return getTargetAccount(zsc).getName();
        }
    }

    static class SaveWiki extends com.zimbra.cs.service.wiki.SaveWiki {
        protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
            return getTargetAccount(zsc).getName();
        }
    }

    static class WikiAction extends com.zimbra.cs.service.wiki.WikiAction {
        protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
            return getTargetAccount(zsc).getName();
        }
    }
	
    public static String uploadOfflineDocument(String id, String acctId) throws ServiceException {
        HttpClient client = ZimbraHttpConnectionManager.getInternalHttpConnMgr().getDefaultHttpClient();
        boolean ka = ZimbraHttpConnectionManager.getInternalHttpConnMgr().getKeepAlive();
        FileUploadServlet.Upload upload = FileUploadServlet.fetchUpload(
            OfflineConstants.LOCAL_ACCOUNT_ID, id, null);
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        Account account = prov.get(Provisioning.AccountBy.id, acctId);

        if (prov.isMountpointAccount(account))
            account = prov.get(Provisioning.AccountBy.id, account.getAttr(
                OfflineProvisioning.A_offlineMountpointProxyAccountId));
        String url = account.getAttr(OfflineConstants.A_offlineRemoteServerUri);
        if (url == null)
            throw ServiceException.FAILURE("not a zimbra account: " + account.getName(), null);
        url += "/service/upload";
        
        String authToken = prov.getProxyAuthToken(acctId);
        BufferStreamRequestEntity bsre = null;
        String newId;
        PostMethod post = new PostMethod(url);
        HttpState state = new HttpState();
        
        try {
            bsre = new BufferStreamRequestEntity(upload.getInputStream(),
                upload.getContentType() + "; name=\"" + upload.getName() + "\"",
                upload.getSize(), 512 * 1024);
            post.setRequestEntity(bsre);
            (new ZimbraAuthTokenEncoded(authToken)).encode(state, false, post.getURI().getHost());
            client.setState(state);
            post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(LC.httpclient_soaphttptransport_retry_count.intValue() - 1, true));
            post.getParams().setVersion(HttpVersion.HTTP_1_1);
            post.getParams().setSoTimeout(LC.httpclient_soaphttptransport_so_timeout.intValue());
            post.setRequestHeader("Connection", ka ? "keep-alive" : "close");
            HttpClientUtil.executeMethod(client, post);
            if (post.getStatusCode() != HttpStatus.SC_OK)
                throw ServiceException.FAILURE("http error. failed to upload to " + url, null);
            
            InputStream resp = post.getResponseBodyAsStream();
            String body = new String(ByteUtil.readInput(resp, 0, 2048));
            Matcher matcher = pattern.matcher(body);
            
            if (!matcher.find())
                throw ServiceException.FAILURE("missing upload id. failed to upload to " + url, null);
            newId = body.substring(matcher.start(), matcher.end());
        } catch (IOException e) {
            throw ServiceException.FAILURE("io error reading upload file: " + e.getMessage(), e);            
        } finally {
            post.releaseConnection();
            if (bsre != null)
                bsre.close();
        }        
        return newId;
    }
}
