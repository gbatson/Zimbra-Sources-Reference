/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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
package com.zimbra.cs.service.mail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.service.admin.AdminServiceException;
import com.zimbra.cs.service.util.SyncToken;
import com.zimbra.cs.session.IWaitSet;
import com.zimbra.cs.session.WaitSetAccount;
import com.zimbra.cs.session.WaitSetCallback;
import com.zimbra.cs.session.WaitSetError;
import com.zimbra.cs.session.WaitSetMgr;
import com.zimbra.soap.SoapServlet;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * 
 */
public class WaitSetRequest extends MailDocumentHandler {
    
    private static final long DEFAULT_TIMEOUT;
    private static final long MIN_TIMEOUT;
    private static final long MAX_TIMEOUT;
    private static final long DEFAULT_ADMIN_TIMEOUT;
    private static final long MIN_ADMIN_TIMEOUT;
    private static final long MAX_ADMIN_TIMEOUT;
    private static final long INITIAL_SLEEP_TIME;
    private static final long NODATA_SLEEP_TIME;
    
    static {
        DEFAULT_TIMEOUT = LC.zimbra_waitset_default_request_timeout.longValueWithinRange(0, Constants.SECONDS_PER_DAY) * 1000;
        MIN_TIMEOUT = LC.zimbra_waitset_min_request_timeout.longValueWithinRange(0, Constants.SECONDS_PER_DAY) * 1000;
        MAX_TIMEOUT = LC.zimbra_waitset_max_request_timeout.longValueWithinRange(0, Constants.SECONDS_PER_DAY) * 1000;
        
        DEFAULT_ADMIN_TIMEOUT = LC.zimbra_admin_waitset_default_request_timeout.longValueWithinRange(0, Constants.SECONDS_PER_DAY) * 1000;
        MIN_ADMIN_TIMEOUT = LC.zimbra_admin_waitset_min_request_timeout.longValueWithinRange(0, Constants.SECONDS_PER_DAY) * 1000;
        MAX_ADMIN_TIMEOUT = LC.zimbra_admin_waitset_max_request_timeout.longValueWithinRange(0, Constants.SECONDS_PER_DAY) * 1000;
        
        INITIAL_SLEEP_TIME = LC.zimbra_waitset_initial_sleep_time.longValueWithinRange(0,5*Constants.SECONDS_PER_MINUTE * 1000);
        NODATA_SLEEP_TIME = LC.zimbra_waitset_nodata_sleep_time.longValueWithinRange(0,5*Constants.SECONDS_PER_MINUTE * 1000);
    }
    
    private static long getTimeout(Element request, boolean isAdminRequest) throws ServiceException {
        if (!isAdminRequest) {
            long to = request.getAttributeLong(MailConstants.A_TIMEOUT, DEFAULT_TIMEOUT);
            if (to < MIN_TIMEOUT)
                to = MIN_TIMEOUT;
            if (to > MAX_TIMEOUT)
                to = MAX_TIMEOUT;
            return to;
        } else {
            long to = request.getAttributeLong(MailConstants.A_TIMEOUT, DEFAULT_ADMIN_TIMEOUT);
            if (to < MIN_ADMIN_TIMEOUT)
                to = MIN_ADMIN_TIMEOUT;
            if (to > MAX_ADMIN_TIMEOUT)
                to = MAX_ADMIN_TIMEOUT;
            return to;
        }
    }
    
    /*
<!--*************************************
    WaitMultipleAccounts:  optionally modifies the wait set and checks
    for any notifications.  If block=1 and there are no notificatins, then
    this API will BLOCK until there is data.

    Client should always set 'seq' to be the highest known value it has
    received from the server.  The server will use this information to
    retransmit lost data.

    If the client sends a last known sync token then the notification is
    calculated by comparing the accounts current token with the client's
    last known.

    If the client does not send a last known sync token, then notification
    is based on change since last Wait (or change since <add> if this
    is the first time Wait has been called with the account)
    ************************************* -->
<WaitMultipleAccountsRequest waitSet="setId" seq="highestSeqKnown" [block="1"]>
  [ <add>
      [<a id="ACCTID" [token="lastKnownSyncToken"] [types="a,c..."]/>]+
    </add> ]
  [ <update>
      [<a id="ACCTID" [token="lastKnownSyncToken"] [types=]/>]+
    </update> ]  
  [ <remove>
      [<a id="ACCTID"/>]+
    </remove> ]  
</WaitMultipleAccountsRequest>

<WaitMultipleAccountsResponse waitSet="setId" seq="seqNo" [canceled="1"]>
  [ <n id="ACCTID"/>]*
  [ <error ...something.../>]*
</WaitMultipleAccountsResponse>
     */
    
    
    private static final String VARS_ATTR_NAME = WaitSetRequest.class.getName()+".vars";
    
    /* (non-Javadoc)
     * @see com.zimbra.soap.DocumentHandler#handle(com.zimbra.common.soap.Element, java.util.Map)
     */
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        boolean adminAllowed = zsc.getAuthToken().isAdmin(); 
        Element response = zsc.createElement(MailConstants.WAIT_SET_RESPONSE);
        return staticHandle(request, context, response, adminAllowed);
    }
    
    public static Element staticHandle(Element request, Map<String, Object> context, Element response, boolean adminAllowed) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        HttpServletRequest servletRequest = (HttpServletRequest) context.get(SoapServlet.SERVLET_REQUEST);
        Continuation continuation;

        String waitSetId = request.getAttribute(MailConstants.A_WAITSET_ID);
        String lastKnownSeqNo = request.getAttribute(MailConstants.A_SEQ);
        boolean block = request.getAttributeBool(MailConstants.A_BLOCK, false);
        
        Callback cb;
        
        if (context.containsKey(SoapServlet.IS_RESUMED_REQUEST)) {
            cb  = (Callback)servletRequest.getAttribute(VARS_ATTR_NAME);
            // load variables here
            continuation = ContinuationSupport.getContinuation(servletRequest, cb);
        } else {
            cb = new Callback();
            continuation = ContinuationSupport.getContinuation(servletRequest, cb);
            cb.continuation = continuation;
            servletRequest.setAttribute(VARS_ATTR_NAME, cb);
            
            String defInterestStr = null;
            if (waitSetId.startsWith(WaitSetMgr.ALL_ACCOUNTS_ID_PREFIX)) {
            	WaitSetMgr.checkRightForAllAccounts(zsc);
            	
                // default interest types required for "All" waitsets
                defInterestStr = request.getAttribute(MailConstants.A_DEFTYPES);
                int defaultInterests = WaitSetRequest.parseInterestStr(defInterestStr, 0);
                cb.ws = WaitSetMgr.lookupOrCreateForAllAccts(zsc.getRequestedAccountId(), waitSetId, defaultInterests, lastKnownSeqNo);
            } else {
                cb.ws = WaitSetMgr.lookup(waitSetId);
            }
            
            if (cb.ws == null)
                throw AdminServiceException.NO_SUCH_WAITSET(waitSetId);
            
            WaitSetMgr.checkRightForOwnerAccount(cb.ws, zsc.getRequestedAccountId());
            
            List<WaitSetAccount> add = parseAddUpdateAccounts(zsc,
                request.getOptionalElement(MailConstants.E_WAITSET_ADD), cb.ws.getDefaultInterest());
            List<WaitSetAccount> update = parseAddUpdateAccounts(zsc,
                request.getOptionalElement(MailConstants.E_WAITSET_UPDATE), cb.ws.getDefaultInterest());
            List<String> remove = parseRemoveAccounts(zsc, request.getOptionalElement(MailConstants.E_WAITSET_REMOVE));

            ///////////////////
            // workaround for 27480: load the mailboxes NOW, before we grab the waitset lock
            List<Mailbox> referencedMailboxes = new ArrayList<Mailbox>();
            for (WaitSetAccount acct : add) {
                try {
                    Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(acct.getAccountId(), MailboxManager.FetchMode.AUTOCREATE);
                    referencedMailboxes.add(mbox);
                } catch (ServiceException e) {
                    ZimbraLog.session.debug("Caught exception preloading mailbox for waitset", e);
                }
            }
            for (WaitSetAccount acct : update) {
                try {
                    Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(acct.getAccountId(), MailboxManager.FetchMode.AUTOCREATE);
                    referencedMailboxes.add(mbox);
                } catch (ServiceException e) {
                    ZimbraLog.session.debug("Caught exception preloading mailbox for waitset", e);
                }
            }
            // END workaround for 27480
            ///////////////////
            
            
            // Force the client to wait briefly before processing -- this will stop 'bad' clients from polling 
            // the server in a very fast loop (they should be using the 'block' mode)
            try { Thread.sleep(INITIAL_SLEEP_TIME); } catch (InterruptedException ex) {}

            cb.errors.addAll(cb.ws.removeAccounts(remove));
            synchronized(cb.ws) { // bug 28190: always grab the WS lock before the CB lock.
                synchronized(cb) {
                    cb.errors.addAll(cb.ws.doWait(cb, lastKnownSeqNo, add, update));
                    // after this point, the ws has a pointer to the cb and so we *MUST NOT* lock
                    // the ws until we release the cb lock!
                    if (cb.completed)
                        block = false;
                }
            }
            
            if (block) {
                // No data after initial check...wait a few extra seconds
                // before going into the notification wait...basically we're just 
                // trying to let the server coalesce notification data a little 
                // bit.
                try { Thread.sleep(NODATA_SLEEP_TIME); } catch (InterruptedException ex) {}
                
                synchronized (cb) {
                    if (!cb.completed) { // don't wait if it completed right away
                        continuation.suspend(getTimeout(request, adminAllowed));
                    }
                }
            }
        }
        
        // if we got here, then we did *not* execute a jetty RetryContinuation,
        // soooo, we'll fall through and finish up at the bottom
        
        // clear the 
        cb.ws.doneWaiting();
        
        response.addAttribute(MailConstants.A_WAITSET_ID, waitSetId);
        if (cb.canceled) {
            response.addAttribute(MailConstants.A_CANCELED, true);
        } else if (cb.completed) {
            response.addAttribute(MailConstants.A_SEQ, cb.seqNo);
            
            for (String s : cb.signalledAccounts) {
                Element saElt = response.addElement(MailConstants.E_A);
                saElt.addAttribute(MailConstants.A_ID, s);
            }
        } else {
            // timed out....they should try again
            response.addAttribute(MailConstants.A_SEQ, lastKnownSeqNo);
        }
        
        encodeErrors(response, cb.errors);
        
        return response;
    }
    
    /**
     * @param elt
     * @param defaultInterest
     * @param allowedAccountIds NULL means "all allowed" (admin)
     * @return
     * @throws ServiceException
     */
    static List<WaitSetAccount> parseAddUpdateAccounts(ZimbraSoapContext zsc, Element elt, int defaultInterest) 
    throws ServiceException {
        List<WaitSetAccount> toRet = new ArrayList<WaitSetAccount>();
        if (elt != null) {
            for (Iterator<Element> iter = elt.elementIterator(MailConstants.E_A); iter.hasNext();) {
                Element a = iter.next();
                String id;
                String name = a.getAttribute(MailConstants.A_NAME, null);
                if (name != null) {
                    Account acct = Provisioning.getInstance().get(AccountBy.name, name);
                    if (acct != null)
                        id = acct.getId();
                    else {
                        WaitSetError err = new WaitSetError(name, WaitSetError.Type.NO_SUCH_ACCOUNT);
                        continue;
                    }
                } else {
                    id = a.getAttribute(MailConstants.A_ID);
                }
                
                WaitSetMgr.checkRightForAdditionalAccount(id, zsc);
                
                String tokenStr = a.getAttribute(MailConstants.A_TOKEN, null);
                SyncToken token = tokenStr != null ? new SyncToken(tokenStr) : null;
                int interests = parseInterestStr(a.getAttribute(MailConstants.A_TYPES, null), defaultInterest);
                toRet.add(new WaitSetAccount(id, token, interests));
            }
        }
        return toRet;
    }
    
    static List<String> parseRemoveAccounts(ZimbraSoapContext zsc, Element elt) throws ServiceException {
        List<String> remove = new ArrayList<String>();
        if (elt != null) {
            for (Iterator<Element> iter = elt.elementIterator(MailConstants.E_A); iter.hasNext();) {
                Element a = iter.next();
                String id = a.getAttribute(MailConstants.A_ID);
                WaitSetMgr.checkRightForAdditionalAccount(id, zsc);

                remove.add(id);
            }
        }
        return remove;
    }
    
    public static class Callback implements WaitSetCallback {
        public void dataReady(IWaitSet ws, String seqNo, boolean canceled, List<WaitSetError> inErrors, String[] signalledAccounts) {
            synchronized(this) {
                ZimbraLog.session.debug("WaitSet: Called WaitSetCallback.dataReady()!");
                if (inErrors != null && inErrors.size() > 0)  
                    errors.addAll(inErrors);
                this.waitSet = ws;
                this.canceled = canceled;
                this.signalledAccounts = signalledAccounts;
                this.seqNo = seqNo;
                this.completed = true;
                if (continuation != null)
                    continuation.resume();
            }
        }

        public boolean completed = false;
        public boolean canceled;
        public String[] signalledAccounts;
        public IWaitSet waitSet;
        public String seqNo;
        public IWaitSet ws;
        public List<WaitSetError> errors = new ArrayList<WaitSetError>();
        public Continuation continuation;
    }
    
    public static enum TypeEnum {
        m(MailItem.typeToBitmask(MailItem.TYPE_MESSAGE)),
        c(MailItem.typeToBitmask(MailItem.TYPE_CONTACT)),
        a(MailItem.typeToBitmask(MailItem.TYPE_APPOINTMENT)),
        all(0xffffff),
        ;
        
        TypeEnum(int mask) { mMask = mask; }
        public int getMask() { return mMask; }
        
        private final int mMask;
    }
    
    public static final String expandInterestStr(int interest) {
        if (interest == TypeEnum.all.getMask())
            return TypeEnum.all.name();
        
        StringBuilder toRet = new StringBuilder();
        for (TypeEnum type : TypeEnum.values()) {
            if ((interest & type.getMask()) != 0) 
                toRet.append(type.name());
        }
        return toRet.toString();
    }
    
    public static final void encodeErrors(Element parent, List<WaitSetError> errors) {
        for (WaitSetError error : errors) {
            Element errorElt = parent.addElement(MailConstants.E_ERROR);
            errorElt.addAttribute(MailConstants.A_ID, error.accountId); 
            errorElt.addAttribute(MailConstants.A_TYPE, error.error.name()); 
        }
    }
    
    public static final int parseInterestStr(String typesList, int defaultInterest) {
        if (typesList == null)
            return defaultInterest;
        
        int toRet = 0;
        
        for (String s : typesList.split(",")) {
            s = s.trim();
            TypeEnum te = TypeEnum.valueOf(s);
            toRet |= te.getMask();
        }
        return toRet;
    }
    
    public static final String interestToStr(int interests) {
        StringBuilder toRet = new StringBuilder();
        
        boolean atFirst = true;
        for (TypeEnum t : TypeEnum.values()) {
            if (t != TypeEnum.all && (interests & t.getMask())!=0) {
                if (!atFirst) {
                    toRet.append(',');
                }
                atFirst = false;
                toRet.append(t.name());
            }
        }
        return toRet.toString();
    }

}
