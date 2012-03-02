/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011 VMware, Inc.
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

package com.zimbra.cs.mailbox;

import java.util.HashMap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;

public class MockMailboxManager extends MailboxManager {

    public MockMailboxManager() {
        super(true);
        mailboxes = new HashMap<String,Mailbox>();
    }

    @Override
    public Mailbox getMailboxByAccountId(String accountId)
        throws ServiceException {

        Mailbox mbox = mailboxes.get(accountId);
        if (mbox != null)
            return mbox;
        Account account = Provisioning.getInstance().getAccount(accountId);
        mbox = new MockMailbox(account);
        mailboxes.put(accountId, mbox);
        return mbox;
    }
    
    private HashMap<String,Mailbox> mailboxes;
}
