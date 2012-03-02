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
package com.zimbra.qa.unittest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import junit.framework.TestCase;

import org.junit.Assert;

import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.Contact.Attachment;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbResults;
import com.zimbra.cs.db.DbUtil;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.zclient.ZContact;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.ContactSortBy;
import com.zimbra.cs.zclient.ZMailbox.ZAttachmentInfo;
import com.zimbra.cs.zclient.ZMailbox.ZImportContactsResult;


public class TestContacts
extends TestCase {

    private static final String NAME_PREFIX = TestContacts.class.getSimpleName();
    private static final String USER_NAME = "user1";
    String mOriginalMaxContacts;

    @Override public void setUp()
    throws Exception {
        cleanUp();
        mOriginalMaxContacts = TestUtil.getAccountAttr(USER_NAME, Provisioning.A_zimbraContactMaxNumEntries);
    }
    
    /**
     * Confirms that {@link Provisioning#A_zimbraContactMaxNumEntries} is enforced (bug 29627).
     */
    public void testMaxContacts()
    throws Exception {
        ZMailbox mbox = TestUtil.getZMailbox(USER_NAME);
        List<ZContact> contacts = mbox.getAllContacts(null, ContactSortBy.nameAsc, false, null);
        int max = contacts.size() + 2;
        TestUtil.setAccountAttr(USER_NAME, Provisioning.A_zimbraContactMaxNumEntries, Integer.toString(max));
        Map<String, String> attrs = new HashMap<String, String>();
        int i;
        for (i = 1; i <= 10; i++) {
            attrs.put("fullName", NAME_PREFIX + " testMaxContacts" + i);
            try {
                mbox.createContact(Integer.toString(Mailbox.ID_FOLDER_CONTACTS), null, attrs);
            } catch (SoapFaultException e) {
                assertEquals(MailServiceException.TOO_MANY_CONTACTS, e.getCode());
                break;
            }
        }
        assertEquals("Unexpected contact number", 3, i);
    }
    
    /**
     * Tests the server-side {@link Attachment} class.
     */
    public void testServerAttachment()
    throws Exception {
        // Specify the attachment size.
        byte[] data = "test".getBytes();
        ByteArrayDataSource ds = new ByteArrayDataSource(data, "text/plain");
        ds.setName("attachment.txt");
        DataHandler dh = new DataHandler(ds);
        Attachment attach = new Attachment(dh,  "attachment", data.length);
        
        // Don't specify the attachment size.
        attach = new Attachment(dh,  "attachment");
        checkServerAttachment(data, attach);
        
        // Create attachment from byte[].
        attach = new Attachment(data, "text/plain", "attachment", "attachment.txt");
        checkServerAttachment(data, attach);
    }
    
    private void checkServerAttachment(byte[] expected, Attachment attach)
    throws Exception {
        TestUtil.assertEquals(expected, attach.getContent());
        TestUtil.assertEquals(expected, ByteUtil.getContent(attach.getInputStream(), 4));
        assertEquals(expected.length, attach.getSize());
        assertEquals("text/plain", attach.getContentType());
        assertEquals("attachment", attach.getName());
        assertEquals("attachment.txt", attach.getFilename());
    }

    public void testContactAttachments()
    throws Exception {
        ZMailbox mbox = TestUtil.getZMailbox(USER_NAME);
        
        // Create a contact with an attachment.
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("fullName", NAME_PREFIX + " testAttachments");
        
        String attachment1Text = "attachment 1";
        int timeout = (int) Constants.MILLIS_PER_MINUTE;
        String folderId = Integer.toString(Mailbox.ID_FOLDER_CONTACTS);
        
        String attachment1Id = mbox.uploadAttachment("attachment.txt", attachment1Text.getBytes(), "text/plain", timeout);
        Map<String, ZAttachmentInfo> attachments = new HashMap<String, ZAttachmentInfo>();
        ZAttachmentInfo info = new ZAttachmentInfo().setAttachmentId(attachment1Id);
        attachments.put("attachment1", info);
        ZContact contact = mbox.createContact(folderId, null, attrs, attachments);
        
        // Validate the attachment data.
        assertTrue(contact.getAttachmentNames().contains("attachment1"));
        byte[] data = getAttachmentData(contact, "attachment1");
        assertEquals(attachment1Text, new String(data));
        
        // Add a second attachment.
        String attachment2Text = "attachment 2";
        String attachment2Id = mbox.uploadAttachment("attachment.txt", attachment2Text.getBytes(), "text/plain", timeout);
        attachments.clear();
        info.setAttachmentId(attachment2Id);
        attachments.put("attachment2", info);
        contact = mbox.modifyContact(contact.getId(), false, null, attachments);
        
        // Validate second attachment data.
        data = getAttachmentData(contact, "attachment2");
        assertEquals(attachment2Text, new String(data));
        
        // Replace second attachment.
        String newAttachment2Text = "new attachment 2";
        String newAttachment2Id = mbox.uploadAttachment("attachment.txt", newAttachment2Text.getBytes(), "text/plain", timeout);
        info.setAttachmentId(newAttachment2Id);
        contact = mbox.modifyContact(contact.getId(), false, null, attachments);

        // Confirm that the attachment data was updated.
        data = getAttachmentData(contact, "attachment2");
        assertEquals(newAttachment2Text, new String(data));
        
        // Create third attachment with data from the second attachment.
        info.setAttachmentId(null);
        info.setPartName(contact.getAttachmentPartName("attachment2"));
        attachments.clear();
        attachments.put("attachment3", info);
        contact = mbox.modifyContact(contact.getId(), false, null, attachments);
        
        // Verify the attachment data.
        data = getAttachmentData(contact, "attachment2");
        assertEquals(newAttachment2Text, new String(data));
        data = getAttachmentData(contact, "attachment3");
        assertEquals(newAttachment2Text, new String(data));
        
        // Replace attachments.
        String attachment4Text = "attachment 4";
        String attachment4Id = mbox.uploadAttachment("attachment.txt", attachment4Text.getBytes(), "text/plain", timeout);
        info.setAttachmentId(attachment4Id);
        info.setPartName(null);
        attachments.clear();
        attachments.put("attachment4", info);
        contact = mbox.modifyContact(contact.getId(), true, attrs, attachments);
        
        // Verify the attachment data.
        Set<String> names = contact.getAttachmentNames();
        assertEquals(1, names.size());
        assertTrue(names.contains("attachment4"));
        data = getAttachmentData(contact, "attachment4");
        assertEquals(attachment4Text, new String(data));
        
        // Remove all attachments.
        info.setAttachmentId(null);
        contact = mbox.modifyContact(contact.getId(), false, attrs, attachments);
        assertEquals(0, contact.getAttachmentNames().size());
        
        // Add an attachment to a contact that didn't previously have one.
        attachment4Id = mbox.uploadAttachment("attachment.txt", attachment4Text.getBytes(), "text/plain", timeout);
        info.setAttachmentId(attachment4Id);
        info.setPartName(null);
        attachments.clear();
        attachments.put("attachment4", info);
        contact = mbox.modifyContact(contact.getId(), false, attrs, attachments);
        
        // Verify the attachment data.
        names = contact.getAttachmentNames();
        assertEquals(1, names.size());
        assertTrue(names.contains("attachment4"));
        data = getAttachmentData(contact, "attachment4");
        assertEquals(attachment4Text, new String(data));
    }

    private byte[] getAttachmentData(ZContact contact, String attachmentName)
    throws Exception {
        InputStream in = contact.getAttachmentData(attachmentName);
        return ByteUtil.getContent(in, 0);
    }

    /**
     * test zclient contact import
     */
    public void testImportContacts()
    throws Exception {
        int timeout = (int) Constants.MILLIS_PER_MINUTE;
        long contactNum = 1;
        String folderId = Integer.toString(Mailbox.ID_FOLDER_CONTACTS);
        ZMailbox mbox = TestUtil.getZMailbox(USER_NAME);
        String csvText = "\"email\",\"fullName\"\n\"testImportContacts@example.org\",\"" + NAME_PREFIX + " testImportContacts\"";
        String attachmentId = mbox.uploadAttachment("ImportContacts.csv", csvText.getBytes(), "text/plain", timeout);

        ZImportContactsResult res = mbox.importContacts(folderId, ZMailbox.CONTACT_IMPORT_TYPE_CSV, attachmentId);
        Assert.assertEquals("Number of contacts imported", contactNum, res.getCount());
    }

    @Override public void tearDown()
    throws Exception {
        TestUtil.setAccountAttr(USER_NAME, Provisioning.A_zimbraContactMaxNumEntries, mOriginalMaxContacts);
        cleanUp();
    }
    
    private void cleanUp()
    throws Exception {
        TestUtil.deleteTestData(USER_NAME, NAME_PREFIX);
    }
    
    public static void main(String[] args)
    throws Exception {
        TestUtil.cliSetup();
        TestUtil.runTest(TestContacts.class);
    }
}
