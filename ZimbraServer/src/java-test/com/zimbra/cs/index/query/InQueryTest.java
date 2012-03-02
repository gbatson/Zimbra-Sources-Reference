/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011 Zimbra, Inc.
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
package com.zimbra.cs.index.query;

import java.util.Collections;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.cs.account.MockProvisioning;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MockMailboxManager;
import com.zimbra.cs.service.util.ItemId;

/**
 * Unit test for {@link InQuery}.
 *
 * @author ysasaki
 */
public class InQueryTest {

    @BeforeClass
    public static void init() throws Exception {
        MockProvisioning prov = new MockProvisioning();
        prov.createAccount("zero@zimbra.com", "secret",
                Collections.<String, Object>singletonMap(Provisioning.A_zimbraId, "0-0-0"));
        Provisioning.setInstance(prov);
    }

    @Test
    public void inAnyFolder() throws Exception {
        Mailbox mbox = new MockMailboxManager().getMailboxByAccountId("0-0-0");

        Query query = InQuery.create(mbox, new ItemId("0-0-0", 1), null, true);
        Assert.assertEquals("Q(UNDER,ANY_FOLDER)", query.toString());

        query = InQuery.create(mbox, new ItemId("0-0-0", 1), null, false);
        Assert.assertEquals("Q(IN,1)", query.toString());

        query = InQuery.create(mbox, new ItemId("1-1-1", 1), null, true);
        Assert.assertEquals("Q(UNDER,1-1-1:1)", query.toString());

        query = InQuery.create(mbox, new ItemId("1-1-1", 1), null, false);
        Assert.assertEquals("Q(IN,1-1-1:1)", query.toString());
    }

}
