/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013, 2014 Zimbra, Inc.
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

/*
 * Created on Jul 30, 2010
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.admin.DeleteMailbox;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineDeleteMailbox extends DeleteMailbox {

    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);

        Element mreq = request.getElement(AdminConstants.E_MAILBOX);
        String accountId = mreq.getAttribute(AdminConstants.A_ACCOUNTID);
        
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        Account account = prov.get(AccountBy.id, accountId, zsc.getAuthToken());
        if (account == null) {
            // Note: isDomainAdminOnly *always* returns false for pure ACL based AccessManager 
            if (isDomainAdminOnly(zsc)) {
                throw ServiceException.PERM_DENIED("account doesn't exist, unable to determine authorization");
            }
            
            // still need to check right, since we don't have an account, the 
            // last resort is checking the global grant.  Do this for now until 
            // there is complain.
            checkRight(zsc, context, null, Admin.R_deleteAccount); 
            
            ZimbraLog.account.warn("DeleteMailbox: account doesn't exist: "+accountId+" (still deleting mailbox)");

        } else {
            checkAccountRight(zsc, account, Admin.R_deleteAccount);   
        }

        //test if the mbox is fetchable; if it is not (e.g. due to corrupt db files) then we want to force-remove it from mgr so next req makes new db files. 
        try {
            MailboxManager.getInstance().getMailboxByAccountId(accountId, false);
            return super.handle(request, context);
        }
        catch (Exception e) {
            ZimbraLog.account.warn("DeleteMailbox: failed to retrieve mailbox due to exception ",e);
            OfflineMailboxManager omgr = (OfflineMailboxManager) MailboxManager.getInstance();
            omgr.purgeBadMailboxByAccountId(accountId);
            if (account instanceof OfflineAccount) {
                ((OfflineAccount) account).setDisabledDueToError(false);
                OfflineSyncManager.getInstance().clearErrorCode(account);
            }
            Map<String,Object> attrs = account.getAttrs();
            attrs.remove(OfflineConstants.A_offlineSyncStatusErrorCode);
            attrs.remove(OfflineConstants.A_offlineSyncStatusErrorMsg);
            attrs.remove(OfflineConstants.A_offlineSyncStatusException);
            account.setAttrs(attrs);
        }
        
        String idString = "<no mailbox for account " + accountId + ">";
        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
            new String[] {"cmd", "DeleteMailbox","id", idString}));
        
        Element response = zsc.createElement(AdminConstants.DELETE_MAILBOX_RESPONSE);
        return response;
    }

}
