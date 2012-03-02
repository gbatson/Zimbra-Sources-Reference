/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.util.Log.Level;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * Creates a custom logger for the given account.
 * 
 * @author bburtin
 */
public class AddAccountLogger extends AdminDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context)
    throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        Server localServer = Provisioning.getInstance().getLocalServer();
        checkRight(zsc, context, localServer, Admin.R_manageAccountLogger);
        
        // Look up account
        Account account = getAccountFromLoggerRequest(request);
        
        // Add logger
        Element eLogger = request.getElement(AdminConstants.E_LOGGER);
        String category = eLogger.getAttribute(AdminConstants.A_CATEGORY);
        String sLevel = eLogger.getAttribute(AdminConstants.A_LEVEL);
        Level level = null;
        try {
            level = Level.valueOf(sLevel.toLowerCase());
        } catch (IllegalArgumentException e) {
            String error = String.format("Invalid level: %s.  Valid values are %s.",
                sLevel, StringUtil.join(",", Level.values()));
            throw ServiceException.INVALID_REQUEST(error, null);
        }
        if (!LogFactory.logExists(category)) {
            throw ServiceException.INVALID_REQUEST("Log category " + category + " does not exist.", null);
        }
        ZimbraLog.misc.info("Adding custom logger: account=%s, category=%s, level=%s",
            account.getName(), category, level);
        Log log = LogFactory.getLog(category);
        log.addAccountLogger(account.getName(), level);

        // Send response
        Element response = zsc.createElement(AdminConstants.ADD_ACCOUNT_LOGGER_RESPONSE);
        return response;
    }
    
    /**
     * Returns the <tt>Account</tt> object based on the &lt;id&gt; or &lt;account&gt;
     * element owned by the given request element. 
     */
    static Account getAccountFromLoggerRequest(Element request)
    throws ServiceException {
        Account account = null;
        Provisioning prov = Provisioning.getInstance();
        Element idElement = request.getOptionalElement(AdminConstants.E_ID);
        
        if (idElement != null) {
            // Handle deprecated <id> element.
            ZimbraLog.soap.info("The <%s> element is deprecated for <%s>.  Use <%s> instead.",
                AdminConstants.E_ID, request.getName(), AdminConstants.E_ACCOUNT);
            String id = idElement.getText();
            account = prov.get(AccountBy.id, id);
            if (account == null) {
                throw AccountServiceException.NO_SUCH_ACCOUNT(idElement.getText());
            }
        } else {
            // Handle <account> element.
            Element accountElement = request.getElement(AdminConstants.E_ACCOUNT);
            AccountBy by = AccountBy.fromString(accountElement.getAttribute(AdminConstants.A_BY));
            account = prov.get(by, accountElement.getText());
            if (account == null) {
                throw AccountServiceException.NO_SUCH_ACCOUNT(accountElement.getText());
            }
        }
        return account;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_manageAccountLogger);
    }
}
