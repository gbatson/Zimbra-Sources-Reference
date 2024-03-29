/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.cs.offline;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mailbox.MailSender;
import com.zimbra.cs.mailbox.Mailbox;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class LMailSender extends MailSender {
    private Transport transport;
    
    public static LMailSender newInstance(OfflineDataSource ds) throws
        ServiceException {
        if (!ds.isLive())
            throw new IllegalArgumentException("Must be Live data source");
        return new LMailSender(ds);
    }
    
    private LMailSender(OfflineDataSource ds) throws ServiceException {
        Properties props = new Properties();
        Long timeout = LC.javamail_smtp_timeout.longValue() * Constants.MILLIS_PER_SECOND;

        props.setProperty("mail.davmail.from", ds.getEmailAddress());
        props.setProperty("mail.davmail.saveinsent", ds.isSaveToSent() ? "f" : "t");
        if (timeout > 0) {
            props.setProperty("mail.davmail.timeout", timeout.toString());
            props.setProperty("mail.davmail.connectiontimeout", timeout.toString());
        }
        Session ses = Session.getInstance(props);
        try {
            transport = ses.getTransport("davmail_xmit");
            transport.connect(null, ds.getUsername(), ds.getDecryptedPassword());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Live data source");
        }
    }

    @Override
    protected Collection<Address> sendMessage(Mailbox mbox, MimeMessage mm, 
        Collection<RollbackData> rollbacks) throws IOException, SafeMessagingException {
        try {
        	Address[] rcpts = mm.getAllRecipients();
            transport.sendMessage(mm, rcpts);
            return Arrays.asList(rcpts);
        } catch (MessagingException e) {
            for (RollbackData rdata : rollbacks)
                if (rdata != null)
                    rdata.rollback();
            throw new SafeMessagingException(e);
        } catch (Exception e) {
            for (RollbackData rdata : rollbacks)
                if (rdata != null)
                    rdata.rollback();
            throw new IOException(e.toString());
        }
    }
}
