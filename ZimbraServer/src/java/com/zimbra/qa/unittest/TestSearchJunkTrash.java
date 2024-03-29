/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2014 Zimbra, Inc.
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
package com.zimbra.qa.unittest;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zimbra.client.ZFolder;
import com.zimbra.client.ZGrant;
import com.zimbra.client.ZMailbox;
import com.zimbra.client.ZMountpoint;
import com.zimbra.client.ZSearchParams;
import com.zimbra.client.ZSearchResult;
import com.zimbra.common.service.ServiceException;

public class TestSearchJunkTrash extends TestCase {
    private static final String USER_NAME = "junktrashuser1";
    private static final String REMOTE_USER_NAME = "junktrashuser2";
    private static ZMailbox mbox;
    private static ZMailbox remote_mbox;
    private static ZFolder folder;
    private static ZMountpoint mPoint;

    @Override
    @Before
    public void setUp() throws Exception {
        TestUtil.createAccount(USER_NAME);
        TestUtil.createAccount(REMOTE_USER_NAME);
        mbox = TestUtil.getZMailbox(USER_NAME);
        remote_mbox = TestUtil.getZMailbox(REMOTE_USER_NAME);
        folder = remote_mbox.createFolder("1", "my shared folder", ZFolder.View.message, null, null, null);
        remote_mbox.modifyFolderGrant(folder.getId(),ZGrant.GranteeType.usr, USER_NAME, "r", null);
        mPoint = mbox.createMountpoint("1", folder.getName(), ZFolder.View.message, ZFolder.Color.BLUE, null,
                ZMailbox.OwnerBy.BY_NAME, REMOTE_USER_NAME, ZMailbox.SharedItemBy.BY_ID, folder.getId(), false);
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("zimbraPrefIncludeSharedItemsInSearch", "TRUE");
        mbox.modifyPrefs(prefs);
        TestUtil.addMessage(mbox, "junk message 1", ZFolder.ID_SPAM);
        TestUtil.addMessage(mbox, "junk message 2", ZFolder.ID_SPAM);
        TestUtil.addMessage(mbox, "a trash message", ZFolder.ID_TRASH);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        TestUtil.deleteAccount(USER_NAME);
        TestUtil.deleteAccount(REMOTE_USER_NAME);
    }

    @Test
    public void testJunk() throws ServiceException {
        ZSearchParams params = new ZSearchParams(String.format("(in:junk) (inid:%s OR is:local)",folder.getId()));
        ZSearchResult results = mbox.search(params);
      assertEquals(2, results.getHits().size());
    }

    @Test
    public void testTrash() throws ServiceException {
        ZSearchParams params = new ZSearchParams(String.format("(in:trash) (inid:%s OR is:local)",folder.getId()));
        ZSearchResult results = mbox.search(params);
      assertEquals(1, results.getHits().size());
    }

    @Test
    public void testTrashOrJunk() throws ServiceException {
        ZSearchParams params = new ZSearchParams(String.format("(in:trash OR in:junk) (inid:%s OR is:local)",folder.getId()));
        ZSearchResult results = mbox.search(params);
      assertEquals(3, results.getHits().size());
    }

    @Test
    public void testTrashOrNotJunk() throws ServiceException {
        //resolves to trash only
        ZSearchParams params = new ZSearchParams(String.format("(in:trash OR NOT in:junk) (inid:%s OR is:local)",folder.getId()));
        ZSearchResult results = mbox.search(params);
      assertEquals(1, results.getHits().size());
    }

    @Test
    public void testNotNotTrashOrJunk() throws ServiceException {
        //resolves to junk only
        ZSearchParams params = new ZSearchParams(String.format("(NOT(in:trash OR NOT in:junk)) (inid:%s OR is:local)",folder.getId()));
        ZSearchResult results = mbox.search(params);
      assertEquals(2, results.getHits().size());
    }
}
