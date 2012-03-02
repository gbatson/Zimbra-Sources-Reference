/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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

import java.util.Arrays;
import java.util.Collections;

import com.zimbra.common.lmtp.SmtpToLmtp;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.DistributionListBy;

/**
 * Validates recipients and expands distribution lists for the dev
 * SMTP server.
 */
public class SmtpRecipientValidator
implements SmtpToLmtp.RecipientValidator {
    
    private static final Log log = LogFactory.getLog(SmtpRecipientValidator.class);
    
    @Override
    public Iterable<String> validate(String recipient) {
        try {
            Provisioning prov = Provisioning.getInstance();
            Account account = prov.get(AccountBy.name, recipient);
            if (account != null) {
                return Arrays.asList(account.getName());
            } else {
                DistributionList dl = prov.get(DistributionListBy.name, recipient);
                if (dl != null) {
                    return dl.getAllMembersSet();
                }
            }
        } catch (ServiceException e) {
            log.error("Unable to validate recipient %s", recipient, e);
        }
        return Collections.emptyList();
    }
}
