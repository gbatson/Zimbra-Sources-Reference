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

package com.zimbra.cs.util;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.util.EmailUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.mime.MimeConstants;
import com.zimbra.common.mime.shim.JavaMailInternetAddress;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.Provisioning.DomainBy;

public class AccountUtil {

    public static InternetAddress getFriendlyEmailAddress(Account acct) {
        // check "displayName" for personal part, and fall back to "cn" if not present
        String personalPart = acct.getAttr(Provisioning.A_displayName);
        if (personalPart == null)
            personalPart = acct.getAttr(Provisioning.A_cn);
        // catch the case where no real name was present and so cn was defaulted to the username
        if (personalPart == null || personalPart.trim().equals("") || personalPart.equals(acct.getAttr("uid")))
            personalPart = null;

        String address;
        try {
            address = getCanonicalAddress(acct);
        } catch (ServiceException se) {
            ZimbraLog.misc.warn("unexpected exception canonicalizing address, will use account name", se);
            address = acct.getName();
        }

        try {
            return new JavaMailInternetAddress(address, personalPart, MimeConstants.P_CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) { }

        // UTF-8 should *always* be supported (i.e. this is actually unreachable)
        try {
            // fall back to using the system's default charset (also pretty much guaranteed not to be "unsupported")
            return new JavaMailInternetAddress(address, personalPart);
        } catch (UnsupportedEncodingException e) { }

        // if we ever reached this point (which we won't), just return an address with no personal part
        InternetAddress ia = new JavaMailInternetAddress();
        ia.setAddress(address);
        return ia;
    }
    
    /**
     * Returns the <tt>From</tt> address used for an outgoing message from the given account.
     * Takes all account attributes into consideration, including user preferences.  
     */
    public static InternetAddress getFromAddress(Account acct) {
        if (acct == null) {
            return null;
        }
        String address = SystemUtil.coalesce(acct.getPrefFromAddress(), acct.getName());
        String personal = SystemUtil.coalesce(acct.getPrefFromDisplay(), acct.getDisplayName(), acct.getCn());
        try {
            return new JavaMailInternetAddress(address, personal, MimeConstants.P_CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            ZimbraLog.system.error("Unable to encode address %s <%s>", personal, address);
            InternetAddress ia = new JavaMailInternetAddress();
            ia.setAddress(address);
            return ia;
        }
    }
    
    /**
     * Returns the <tt>Reply-To</tt> address used for an outgoing message from the given
     * account, based on user preferences, or <tt>null</tt> if <tt>zimbraPrefReplyToEnabled</tt>
     * is <tt>FALSE</tt>.
     */
    public static InternetAddress getReplyToAddress(Account acct) {
        if (acct == null) {
            return null;
        }
        if (!acct.isPrefReplyToEnabled()) {
            return null;
        }
        String address = acct.getPrefReplyToAddress();
        if (address == null) {
            return null;
        }
        String personal = acct.getPrefReplyToDisplay();
        try {
            return new JavaMailInternetAddress(address, personal, MimeConstants.P_CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            ZimbraLog.system.error("Unable to encode address %s <%s>", personal, address);
            InternetAddress ia = new JavaMailInternetAddress();
            ia.setAddress(address);
            return ia;
        }
    }
    
    public static boolean isDirectRecipient(Account acct, MimeMessage mm) throws ServiceException, MessagingException {
        return isDirectRecipient(acct, null, mm, -1);
    }
    
    public static boolean isDirectRecipient(Account acct, String[] otherAccountAddrs, MimeMessage mm, int maxToCheck) throws ServiceException, MessagingException {
        Address[] recipients = mm.getAllRecipients();
        if (recipients == null) {
            return false;
        }

        AccountAddressMatcher acctMatcher = new AccountAddressMatcher(acct);
        int numRecipientsToCheck = (maxToCheck <= 0 ? recipients.length : Math.min(recipients.length, maxToCheck));
        for (int i = 0; i < numRecipientsToCheck; i++) {
            String msgAddress = ((InternetAddress) recipients[i]).getAddress();
            if (acctMatcher.matches(msgAddress))
                return true;
            
            if (otherAccountAddrs != null) {
                for (String otherAddr: otherAccountAddrs) {
                    if (otherAddr.equalsIgnoreCase(msgAddress)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /* We do a more lightweight canonicalization that postfix, because
     * we except to set the LDAP attributes only in certain ways.  For
     * instance we do not canonicalize the local part by itself.
     */
    public static String getCanonicalAddress(Account account) throws ServiceException {
        // If account has a canonical address, let's use that.
        String ca = account.getAttr(Provisioning.A_zimbraMailCanonicalAddress);
        
        // But we still have to canonicalize domain names, so do that with account address
        if (ca == null)
            ca = account.getName();

        String[] parts = EmailUtil.getLocalPartAndDomain(ca);
        if (parts == null)
            return ca;

        Domain domain = Provisioning.getInstance().getDomain(DomainBy.name, parts[1], true);
        if (domain == null)
            return ca;

        String domainCatchAll = domain.getAttr(Provisioning.A_zimbraMailCatchAllCanonicalAddress);
        if (domainCatchAll != null)
            return parts[0] + domainCatchAll;

        return ca;
    }

    /**
     * Check if given account is allowed to set given from header.
     */
    public static boolean allowFromAddress(Account acct, String fromAddr) throws ServiceException {
        if (fromAddr == null)
            return false;
        return addressMatchesAccountOrSendAs(acct, fromAddr)
            || acct.getBooleanAttr(Provisioning.A_zimbraAllowAnyFromAddress, false);
    }

    /**
     * True if this address matches some address for this account (aliases, domain re-writes, etc) or address of
     * another account that this account may send as.
     */
    public static boolean addressMatchesAccountOrSendAs(Account acct, String givenAddress) throws ServiceException {
        return (new AccountAddressMatcher(acct, true)).matches(givenAddress);
    }

    /**
     * True if this address matches some address for this account (aliases, domain re-writes, etc).  Send-as addresses
     * are not considered a match.  A send-as address is an allow-from address that corresponds to an account different
     * from this account.
     */
    public static boolean addressMatchesAccount(Account acct, String givenAddress) throws ServiceException {
        return (new AccountAddressMatcher(acct)).matches(givenAddress);
    }
    
    /**
     * Returns all account email addresses in lower case in a hash set.
     * @param acct user's account
     * @return Set containing all account email addresses in lower case or, empty if no email address is found
     * @throws ServiceException
     */
    public static Set<String> getEmailAddresses(Account acct) throws ServiceException {
        Set<String> addrs = new HashSet<String> ();
        
        addrs.add(acct.getName().toLowerCase());
        addrs.add(AccountUtil.getCanonicalAddress(acct).toLowerCase());

        String[] accountAliases = acct.getMailAlias();
        for (String addr : accountAliases)
            addrs.add(addr.toLowerCase());
        
        String[] allowedFromAddrs = acct.getMultiAttr(Provisioning.A_zimbraAllowFromAddress);
        for (String addr : allowedFromAddrs)
            addrs.add(addr.toLowerCase());
        
        return addrs;
    }

    public static class AccountAddressMatcher {
        private Set<String> addresses;

        public AccountAddressMatcher(Account account) throws ServiceException {
            this(account, false);
        }

        public AccountAddressMatcher(Account account, boolean matchSendAs) throws ServiceException {
            addresses = new HashSet<String>();
            String mainAddr = account.getName();
            if (!StringUtil.isNullOrEmpty(mainAddr)) {
                addresses.add(mainAddr.toLowerCase());
            }
            String canonAddr = getCanonicalAddress(account);
            if (!StringUtil.isNullOrEmpty(canonAddr)) {
                addresses.add(canonAddr.toLowerCase());
            }
            String[] aliases = account.getMailAlias();
            if (aliases != null) {
                for (String alias : aliases) {
                    if (!StringUtil.isNullOrEmpty(alias)) {
                        addresses.add(alias.toLowerCase());
                    }
                }
            }
            String[] addrs = account.getMultiAttr(Provisioning.A_zimbraAllowFromAddress);
            if (addrs != null) {
                for (String addr : addrs) {
                    if (StringUtil.isNullOrEmpty(addr)) {
                        continue;
                    }
                    if (!matchSendAs) {
                        // Find addresses that point to a different account.  We want to distinguish between sending
                        // as another user and sending as an external address controlled/owned by this user.
                        // This check can be removed when we stop adding sendAs addresses in zimbraAllowFromAddress.
                        try {
                            // Don't lookup account if email domain is not internal.  This will avoid unnecessary ldap searches
                            // that will have returned no match anyway.
                            String domain = EmailUtil.getValidDomainPart(addr);
                            if (domain != null) {
                                Domain internalDomain = Provisioning.getInstance().getDomain(DomainBy.name, domain, true);
                                if (internalDomain != null) {
                                    Account allowFromAccount;
                                    if (Provisioning.getInstance().isDistributionList(addr)) {
                                        // Avoid ldap search of DL address as an account.  This will have returned no match anyway.
                                        allowFromAccount = null;
                                    } else {
                                        allowFromAccount = Provisioning.getInstance().get(AccountBy.name, addr);
                                    }
                                    allowFromAccount = Provisioning.getInstance().get(AccountBy.name, addr);
                                    if (allowFromAccount != null && !account.getId().equalsIgnoreCase(allowFromAccount.getId())) {
                                        // The allow-from address refers to another account, and therefore it is not a match
                                        // for this account.
                                        continue;
                                    }
                                }
                            }
                        } catch (ServiceException e) {}                
                    }
                    addresses.add(addr.toLowerCase());
                }
            }
        }

        public boolean matches(String address) throws ServiceException {
            return matches(address, true);
        }

        private boolean matches(String address, boolean checkDomainAlias) throws ServiceException {
            if (StringUtil.isNullOrEmpty(address)) {
                return false;
            }
            if (addresses.contains(address.toLowerCase())) {
                return true;
            }
            if (checkDomainAlias) {
                try {
                    String addrByDomainAlias = Provisioning.getInstance().getEmailAddrByDomainAlias(address);
                    if (addrByDomainAlias != null) {
                        return matches(addrByDomainAlias, false);  // Assume domain aliases are never chained.
                    }
                } catch (ServiceException e) {
                    ZimbraLog.account.warn("unable to get addr by alias domain" + e);
                }
            }
            return false;
        }
    }

    public static String getSoapUri(Account account) {
        String base = getBaseUri(account);
        return (base == null ? null : base + AccountConstants.USER_SERVICE_URI);
    }

    public static String getBaseUri(Account account) {
        if (account == null)
            return null;

        try {
            Server server = Provisioning.getInstance().getServer(account);
            if (server == null) {
                ZimbraLog.account.warn("no server associated with acccount " + account.getName());
                return null;
            }
            return getBaseUri(server);
        } catch (ServiceException e) {
            ZimbraLog.account.warn("error fetching SOAP URI for account " + account.getName(), e);
            return null;
        }
    }

    public static String getBaseUri(Server server) {
        if (server == null)
            return null;

        String host = server.getAttr(Provisioning.A_zimbraServiceHostname);
        String mode = server.getAttr(Provisioning.A_zimbraMailMode, "http");
        int port = server.getIntAttr(Provisioning.A_zimbraMailPort, 0);
        if (port > 0 && !mode.equalsIgnoreCase("https") && !mode.equalsIgnoreCase("redirect")) {
            return "http://" + host + ':' + port;
        } else if (!mode.equalsIgnoreCase("http")) {
            port = server.getIntAttr(Provisioning.A_zimbraMailSSLPort, 0);
            if (port > 0)
                return "https://" + host + ':' + port;
        }
        ZimbraLog.account.warn("no service port available on host " + host);
        return null;
    }

//    /**
//     * True if this mime message has at least one recipient that is NOT the same as the specified account
//     * 
//     * @param acct
//     * @param mm
//     * @return
//     * @throws ServiceException
//     */
//    public static boolean hasExternalRecipient(Account acct, MimeMessage mm) throws ServiceException, MessagingException
//    {
//        int maxToCheck = -1;
//        String accountAddress = acct.getName();
//        String canonicalAddress = getCanonicalAddress(acct);
//        String[] accountAliases = acct.getMailAlias();
//        Address[] recipients = mm.getAllRecipients();
//        
//        if (recipients != null) {
//            int numRecipientsToCheck = (maxToCheck <= 0 ? recipients.length : Math.min(recipients.length, maxToCheck));
//            for (int i = 0; i < numRecipientsToCheck; i++) {
//                String msgAddress = ((InternetAddress) recipients[i]).getAddress();
//                if (!addressMatchesAccount(accountAddress, canonicalAddress, accountAliases, msgAddress)) 
//                    return true;
//            }
//        }
//        return false;
//    }

    /**
     *
     * @param id account id to lookup
     * @param nameKey name key to add to context if account lookup is ok
     * @param idOnlyKey id key to add to context if account lookup fails
     */
    public static void addAccountToLogContext(Provisioning prov, String id, String nameKey, String idOnlyKey, AuthToken authToken) {
        Account acct = null;
        try {
            acct = prov.get(Provisioning.AccountBy.id, id, authToken);
        } catch (ServiceException se) {
            ZimbraLog.misc.warn("unable to lookup account for log, id: " + id, se);
        }
        if (acct == null) {
            ZimbraLog.addToContext(idOnlyKey, id);
        } else {
            ZimbraLog.addToContext(nameKey, acct.getName());
        }
    }

    /**
     * True if accountId is the "local@host.local" special account of ZDesktop.
     * @param accountId
     * @return
     */
    public static boolean isZDesktopLocalAccount(String accountId) {
        String zdLocalAcctId = LC.zdesktop_local_account_id.value();
        return zdLocalAcctId != null && zdLocalAcctId.equalsIgnoreCase(accountId);
    }
}
