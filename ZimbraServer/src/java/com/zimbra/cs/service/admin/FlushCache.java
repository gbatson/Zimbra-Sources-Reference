/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014 Zimbra, Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.admin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.zimbra.common.account.Key;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.SoapHttpTransport;
import com.zimbra.common.util.FileUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.CacheExtension;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.CacheEntry;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.PermissionCache;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.gal.GalGroup;
import com.zimbra.cs.httpclient.URLUtil;
import com.zimbra.cs.util.SkinUtil;
import com.zimbra.cs.util.WebClientL10nUtil;
import com.zimbra.cs.util.WebClientServiceUtil;
import com.zimbra.cs.zimlet.ZimletUtil;
import com.zimbra.soap.SoapServlet;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.soap.admin.type.CacheEntryType;

public class FlushCache extends AdminDocumentHandler {

    public static final String FLUSH_CACHE = "flushCache";
    public static final String RES_AJXMSG_JS = "/res/AjxMsg.js";
    public static final String JS_SKIN_JS = "/js/skin.js";

    /**
     * must be careful and only allow deletes domain admin has access to
     */
    @Override
    public boolean domainAuthSufficient(Map<String, Object> context) {
        return true;
    }

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);

        Server localServer = Provisioning.getInstance().getLocalServer();
        checkRight(zsc, context, localServer, Admin.R_flushCache);

        Element eCache = request.getElement(AdminConstants.E_CACHE);
        String typeStr = eCache.getAttribute(AdminConstants.A_TYPE);
        boolean allServers = eCache.getAttributeBool(AdminConstants.A_ALLSERVERS, false);

        String[] types = typeStr.split(",");

        for (String type : types) {
            CacheEntryType cacheType = null;

            try {
                cacheType = CacheEntryType.fromString(type);
                doFlush(context, cacheType, eCache);
            } catch (ServiceException e) {

                if (cacheType == null) {
                    // see if it is a registered extension
                    CacheExtension ce = CacheExtension.getHandler(type);
                    if (ce != null) {
                        ce.flushCache();
                    } else {
                        throw e;
                    }
                } else {
                    throw e;
                }
            }
        }

        if (allServers) {
            flushCacheOnAllServers(zsc, request);
        }

        Element response = zsc.createElement(AdminConstants.FLUSH_CACHE_RESPONSE);
        return response;
    }

    private void doFlush(Map<String, Object> context, CacheEntryType cacheType, Element eCache) throws ServiceException {

		String mailURL = Provisioning.getInstance().getLocalServer().getMailURL();
		switch (cacheType) {
			case acl:
				PermissionCache.invalidateCache();
				break;
			case all:
				flushLdapCache(cacheType, eCache);
				Provisioning.getInstance().refreshValidators(); // refresh other bits of cached license data
				break;
			case galgroup:
				GalGroup.flushCache(getCacheEntries(eCache));
				break;
			case uistrings:
                if (WebClientServiceUtil.isServerInSplitMode()) {
                    WebClientServiceUtil.flushUistringsCache();
                } else {
                    FlushCache.sendFlushRequest(context, mailURL, RES_AJXMSG_JS);
                    FlushCache.sendFlushRequest(context, "/zimbraAdmin", RES_AJXMSG_JS);
                }
				break;
			case skin:
                SkinUtil.flushCache();
                if (!WebClientServiceUtil.isServerInSplitMode()) {
                    FlushCache.sendFlushRequest(context, mailURL, JS_SKIN_JS);
                }
				break;
			case locale:
                WebClientL10nUtil.flushCache();
				break;
			case license:
				flushLdapCache(CacheEntryType.config, eCache); // refresh global config for parsed license
				Provisioning.getInstance().refreshValidators(); // refresh other bits of cached license data
				break;
			case zimlet:
			    ZimletUtil.flushDiskCache(context);
                if (!WebClientServiceUtil.isServerInSplitMode()) {
                    flushAllZimlets(context);
                }
				// fall through to also flush ldap entries
			default:
				flushLdapCache(cacheType, eCache);
		}
	}

    public static void flushAllZimlets(Map<String, Object> context) {
        FlushCache.sendFlushRequest(context, "/service", "/zimlet/res/all.js");
    }

    private CacheEntry[] getCacheEntries(Element eCache) throws ServiceException {
        List<Element> eEntries = eCache.listElements(AdminConstants.E_ENTRY);
        CacheEntry[] entries = null;
        if (eEntries.size() > 0) {
            entries = new CacheEntry[eEntries.size()];
            int i = 0;
            for (Element eEntry : eEntries) {
                entries[i++] = new CacheEntry(Key.CacheEntryBy.valueOf(eEntry.getAttribute(AdminConstants.A_BY)),
                        eEntry.getText());
            }
        }

        return entries;
    }

    private void flushLdapCache(CacheEntryType cacheType, Element eCache) throws ServiceException {
        CacheEntry[] entries = getCacheEntries(eCache);
        Provisioning.getInstance().flushCache(cacheType, entries);
    }

    public static void sendFlushRequest(Map<String,Object> context, String appContext, String resourceUri) {
        ServletContext containerContext = (ServletContext)context.get(SoapServlet.SERVLET_CONTEXT);
        if (containerContext == null) {
            if (ZimbraLog.misc.isDebugEnabled()) {
                ZimbraLog.misc.debug("flushCache: no container context");
            }
            return;
        }
        ServletContext webappContext = containerContext.getContext(appContext);
        RequestDispatcher dispatcher = webappContext.getRequestDispatcher(resourceUri);
        if (dispatcher == null) {
            if (ZimbraLog.misc.isDebugEnabled()) {
                ZimbraLog.misc.debug("flushCache: no dispatcher for "+resourceUri);
            }
            return;
        }

        try {
            if (ZimbraLog.misc.isDebugEnabled()) {
                ZimbraLog.misc.debug("flushCache: sending flush request");
            }
            ServletRequest request = (ServletRequest)context.get(SoapServlet.SERVLET_REQUEST);
            request.setAttribute(FLUSH_CACHE, Boolean.TRUE);
            ServletResponse response = (ServletResponse)context.get(SoapServlet.SERVLET_RESPONSE);
            dispatcher.include(request, response);
        }
        catch (Throwable t) {
            // ignore error
            if (ZimbraLog.misc.isDebugEnabled()) {
                ZimbraLog.misc.debug("flushCache: "+t.getMessage());
            }
        }
    }

    public static void flushChacheOnServer(Server server,ZimbraSoapContext zsc,String cacheType) throws ServiceException, IOException {
        String adminUrl = URLUtil.getAdminURL(server, AdminConstants.ADMIN_SERVICE_URI);
        SoapHttpTransport mTransport = new SoapHttpTransport(adminUrl);
        mTransport.setAuthToken(zsc.getRawAuthToken());
        XMLElement req = new XMLElement(AdminConstants.FLUSH_CACHE_REQUEST);
        req.addElement(AdminConstants.E_CACHE).addAttribute(AdminConstants.A_TYPE, cacheType);
        mTransport.invoke(req);
    }

    private void flushCacheOnAllServers(ZimbraSoapContext zsc, Element origReq) throws ServiceException {

        Provisioning prov = Provisioning.getInstance();
        String localServerId = prov.getLocalServer().getId();

        for (Server server : prov.getAllMailClientServers()) {

            if (localServerId.equals(server.getId())) {
                continue;
            }

            ZimbraLog.misc.debug("Flushing cache on server: " + server.getName());

            Element req = origReq.clone();
            Element eCache = req.getElement(AdminConstants.E_CACHE);
            eCache.addAttribute(AdminConstants.A_ALLSERVERS, false);

            String adminUrl = URLUtil.getAdminURL(server, AdminConstants.ADMIN_SERVICE_URI);
            SoapHttpTransport mTransport = new SoapHttpTransport(adminUrl);
            mTransport.setAuthToken(zsc.getRawAuthToken());

            try {
                mTransport.invoke(req);
            } catch (ServiceException e) {
                // log and continue
                ZimbraLog.misc.warn("Encountered exception while FlushCache on server: " + server.getName() +
                        ", skip and continue with the next server", e);
            } catch (IOException e) {
                // log and continue
                ZimbraLog.misc.warn("Encountered exception while FlushCache on server: " + server.getName() +
                        ", skip and continue with the next server", e);
            }
        }
    }


    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_flushCache);
    }
}
