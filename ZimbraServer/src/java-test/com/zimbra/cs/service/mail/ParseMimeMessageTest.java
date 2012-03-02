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
package com.zimbra.cs.service.mail;

import java.util.Collections;
import java.util.HashMap;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.mime.ContentType;
import com.zimbra.common.mime.MimeConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.MockProvisioning;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.MockMailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.service.mail.ParseMimeMessage;
import com.zimbra.cs.service.mail.ToXML.EmailType;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * Unit test for {@link ParseMimeMessage}.
 *
 * @author ysasaki
 */
public class ParseMimeMessageTest {

    private static final String ACCOUNT_ID = "11122233-1111-1111-1111-111222333444";

    @BeforeClass
    public static void init() throws Exception {
        MockProvisioning prov = new MockProvisioning();
        HashMap<String,Object> attrs = new HashMap<String,Object>();
        attrs.put(Provisioning.A_zimbraId, ACCOUNT_ID);
        attrs.put(Provisioning.A_zimbraMailHost, "localhost");
        prov.createAccount("test@zimbra.com", "secret", attrs);
        Provisioning.setInstance(prov);
        LC.zimbra_class_mboxmanager.setDefault("com.zimbra.cs.mailbox.MockMailboxManager");
        LC.zimbra_class_store.setDefault("com.zimbra.cs.store.MockStoreManager");
    }

    @Test
    public void parseMimeMsgSoap() throws Exception {
        Element el = new Element.JSONElement(MailConstants.E_MSG);
        el.addAttribute(MailConstants.E_SUBJECT, "dinner appt");
        el.addUniqueElement(MailConstants.E_MIMEPART)
            .addAttribute(MailConstants.A_CONTENT_TYPE, "text/plain")
            .addAttribute(MailConstants.E_CONTENT, "foo bar");
        el.addElement(MailConstants.E_EMAIL)
            .addAttribute(MailConstants.A_ADDRESS_TYPE, EmailType.TO.toString())
            .addAttribute(MailConstants.A_ADDRESS, "rcpt@zimbra.com");

        Account acct = Provisioning.getInstance().get(Provisioning.AccountBy.name, "test@zimbra.com");
        ZimbraSoapContext zsc = new ZimbraSoapContext(new ZimbraSoapContext(null,
                Collections.<String, Object>emptyMap(), SoapProtocol.SoapJS), ACCOUNT_ID, null);
        OperationContext octxt = new OperationContext(acct);

        MimeMessage mm = ParseMimeMessage.parseMimeMsgSoap(zsc, octxt, null, el, null,
                new ParseMimeMessage.MimeMessageData());
        Assert.assertEquals("text/plain; charset=utf-8", mm.getContentType());
        Assert.assertEquals("dinner appt", mm.getSubject());
        Assert.assertEquals("rcpt@zimbra.com", mm.getHeader("To", ","));
        Assert.assertEquals("7bit", mm.getHeader("Content-Transfer-Encoding", ","));
        Assert.assertEquals("foo bar", mm.getContent());
    }

    @Test
    public void customMimeHeader() throws Exception {
        Element el = new Element.JSONElement(MailConstants.E_MSG);
        el.addAttribute(MailConstants.E_SUBJECT, "subject");
        el.addUniqueElement(MailConstants.E_MIMEPART)
            .addAttribute(MailConstants.A_CONTENT_TYPE, "text/plain")
            .addAttribute(MailConstants.E_CONTENT, "body");
        el.addElement(MailConstants.E_EMAIL)
            .addAttribute(MailConstants.A_ADDRESS_TYPE, EmailType.TO.toString())
            .addAttribute(MailConstants.A_ADDRESS, "rcpt@zimbra.com");
        el.addElement(MailConstants.E_HEADER)
            .addAttribute(MailConstants.A_NAME, "X-Zimbra-Test")
            .setText("custom");
        el.addElement(MailConstants.E_HEADER)
            .addAttribute(MailConstants.A_NAME, "X-Zimbra-Test")
            .setText("\u30ab\u30b9\u30bf\u30e0");

        Account acct = Provisioning.getInstance().get(Provisioning.AccountBy.name, "test@zimbra.com");
        ZimbraSoapContext zsc = new ZimbraSoapContext(new ZimbraSoapContext(null,
                Collections.<String, Object>emptyMap(), SoapProtocol.SoapJS), ACCOUNT_ID, null);
        OperationContext octxt = new OperationContext(acct);
        MimeMessage mm;
        try {
            mm = ParseMimeMessage.parseMimeMsgSoap(zsc, octxt, null, el, null, new ParseMimeMessage.MimeMessageData());
            Assert.fail();
        } catch (ServiceException expected) {
            Assert.assertEquals("invalid request: header 'X-Zimbra-Test' not allowed", expected.getMessage());
        }

        Provisioning.getInstance().getConfig().setCustomMimeHeaderNameAllowed(new String[] {"X-Zimbra-Test"});
        mm = ParseMimeMessage.parseMimeMsgSoap(zsc, octxt, null, el, null, new ParseMimeMessage.MimeMessageData());
        Assert.assertEquals("custom, =?utf-8?B?44Kr44K544K/44Og?=", mm.getHeader("X-Zimbra-Test", ", "));
    }

    @Test
    public void attachedMessage() throws Exception {
        Element el = new Element.JSONElement(MailConstants.E_MSG);
        el.addAttribute(MailConstants.E_SUBJECT, "attach message");
        el.addElement(MailConstants.E_EMAIL)
            .addAttribute(MailConstants.A_ADDRESS_TYPE, EmailType.TO.toString())
            .addAttribute(MailConstants.A_ADDRESS, "rcpt@zimbra.com");
        Element mp = el.addUniqueElement(MailConstants.E_MIMEPART)
            .addAttribute(MailConstants.A_CONTENT_TYPE, "multipart/mixed;");
        mp.addElement(MailConstants.E_MIMEPART)
            .addAttribute(MailConstants.A_CONTENT_TYPE, "text/plain")
            .addAttribute(MailConstants.E_CONTENT, "This is the outer message.");
        mp.addElement(MailConstants.E_MIMEPART)
            .addAttribute(MailConstants.A_CONTENT_TYPE, "message/rfc822")
            .addAttribute(MailConstants.E_CONTENT,
                    "From: inner-sender@zimbra.com\r\n" +
                    "To: inner-rcpt@zimbra.com\r\n" +
                    "Subject: inner-message\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Transfer-Encoding: 7bit\r\n" +
                    "MIME-Version: 1.0\r\n\r\n" +
                    "This is the inner message.");

        Account acct = Provisioning.getInstance().get(Provisioning.AccountBy.name, "test@zimbra.com");
        ZimbraSoapContext zsc = new ZimbraSoapContext(new ZimbraSoapContext(null,
                Collections.<String, Object>emptyMap(), SoapProtocol.SoapJS), ACCOUNT_ID, null);
        OperationContext octxt = new OperationContext(acct);

        MimeMessage mm = ParseMimeMessage.parseMimeMsgSoap(zsc, octxt, null, el, null,
                new ParseMimeMessage.MimeMessageData());
        Assert.assertTrue(mm.getContentType().startsWith("multipart/mixed;"));
        Assert.assertEquals("attach message", mm.getSubject());
        Assert.assertEquals("rcpt@zimbra.com", mm.getHeader("To", ","));
        MimeMultipart mmp = (MimeMultipart) mm.getContent();
        Assert.assertEquals(2, mmp.getCount());
        Assert.assertTrue(mmp.getContentType().startsWith("multipart/mixed;"));

        MimeBodyPart part = (MimeBodyPart) mmp.getBodyPart(0);
        Assert.assertEquals("text/plain; charset=utf-8", part.getContentType());
        Assert.assertEquals("7bit", part.getHeader("Content-Transfer-Encoding", ","));
        Assert.assertEquals("This is the outer message.", part.getContent());

        part = (MimeBodyPart) mmp.getBodyPart(1);
        Assert.assertEquals("message/rfc822; charset=utf-8", part.getContentType());
        MimeMessage msg = (MimeMessage) part.getContent();
        Assert.assertEquals("text/plain", msg.getContentType());
        Assert.assertEquals("inner-message", msg.getSubject());
        Assert.assertEquals("This is the inner message.", msg.getContent());
    }

    @Test
    public void attachDocument1() throws Exception {
        Element el = new Element.JSONElement(MailConstants.E_MSG);
        el.addAttribute(MailConstants.E_SUBJECT, "attach message");
        el.addElement(MailConstants.E_EMAIL)
            .addAttribute(MailConstants.A_ADDRESS_TYPE, EmailType.TO.toString())
            .addAttribute(MailConstants.A_ADDRESS, "rcpt@zimbra.com");
        el.addElement(MailConstants.E_MIMEPART)
            .addAttribute(MailConstants.A_CONTENT_TYPE, "text/plain")
            .addAttribute(MailConstants.E_CONTENT, "This is the content.");
        el.addElement(MailConstants.E_ATTACH)
            .addElement(MailConstants.E_DOC)
            .addAttribute(MailConstants.A_ID, "100");

        Account acct = Provisioning.getInstance().get(Provisioning.AccountBy.name, "test@zimbra.com");
        MockMailbox mbox = (MockMailbox)MailboxManager.getInstance().getMailboxByAccountId(ACCOUNT_ID);
        mbox.addDocument(100, "testdoc", MimeConstants.CT_APPLICATION_PDF, "test123");
        ZimbraSoapContext zsc = new ZimbraSoapContext(new ZimbraSoapContext(null,
                Collections.<String, Object>emptyMap(), SoapProtocol.SoapJS), ACCOUNT_ID, null);
        OperationContext octxt = new OperationContext(acct);

        MimeMessage mm = ParseMimeMessage.parseMimeMsgSoap(zsc, octxt, null, el, null,
                new ParseMimeMessage.MimeMessageData());
        MimeMultipart mmp = (MimeMultipart) mm.getContent();
        MimeBodyPart part = (MimeBodyPart) mmp.getBodyPart(1);
        Assert.assertEquals(MimeConstants.CT_APPLICATION_PDF, new ContentType(part.getContentType()).getContentType());
    }

    @Test
    public void attachDocument2() throws Exception {
        Element el = new Element.JSONElement(MailConstants.E_MSG);
        el.addAttribute(MailConstants.E_SUBJECT, "attach message");
        el.addElement(MailConstants.E_EMAIL)
            .addAttribute(MailConstants.A_ADDRESS_TYPE, EmailType.TO.toString())
            .addAttribute(MailConstants.A_ADDRESS, "rcpt@zimbra.com");
        el.addElement(MailConstants.E_MIMEPART)
            .addAttribute(MailConstants.A_CONTENT_TYPE, "text/plain")
            .addAttribute(MailConstants.E_CONTENT, "This is the content.");
        el.addElement(MailConstants.E_ATTACH)
            .addElement(MailConstants.E_DOC)
            .addAttribute(MailConstants.A_ID, "101");

        Account acct = Provisioning.getInstance().get(Provisioning.AccountBy.name, "test@zimbra.com");
        MockMailbox mbox = (MockMailbox)MailboxManager.getInstance().getMailboxByAccountId(ACCOUNT_ID);
        mbox.addDocument(101, "testdoc", MimeConstants.CT_APPLICATION_ZIMBRA_DOC, "test123");
        ZimbraSoapContext zsc = new ZimbraSoapContext(new ZimbraSoapContext(null,
                Collections.<String, Object>emptyMap(), SoapProtocol.SoapJS), ACCOUNT_ID, null);
        OperationContext octxt = new OperationContext(acct);

        MimeMessage mm = ParseMimeMessage.parseMimeMsgSoap(zsc, octxt, null, el, null,
                new ParseMimeMessage.MimeMessageData());
        MimeMultipart mmp = (MimeMultipart) mm.getContent();
        MimeBodyPart part = (MimeBodyPart) mmp.getBodyPart(1);
        Assert.assertEquals(MimeConstants.CT_TEXT_HTML, new ContentType(part.getContentType()).getContentType());
    }
}
