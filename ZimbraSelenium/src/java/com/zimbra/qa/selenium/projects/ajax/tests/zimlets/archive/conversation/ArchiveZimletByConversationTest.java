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
package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.archive.conversation;

import java.lang.reflect.*;

import org.testng.*;
import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;

public class ArchiveZimletByConversationTest extends PrefGroupMailByConversationTest {


	protected FolderItem MyArchiveFolder = null;
	
	public ArchiveZimletByConversationTest() {
		super();
		
		logger.info("New "+ this.getClass().getCanonicalName());


	}

	/**
	 * Get the Archive folder
	 * @return 
	 * @throws HarnessException
	 */
	public void initializeArchiveFolder() throws HarnessException {
		
		String foldername = "archive" + ZimbraSeleniumProperties.getUniqueString();
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername +"' l='"+ root.getId() +"'/>" +
                "</CreateFolderRequest>");
		MyArchiveFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		app.zGetActiveAccount().soapSend(
				"<SetMailboxMetadataRequest xmlns='urn:zimbraMail'>" +
						"<meta section='zwc:archiveZimlet'>" +
							"<a n='hideDeleteButton'>false</a>" +
							"<a n='showSendAndArchive'>false</a>" +
							"<a n='archivedFolder'>"+ MyArchiveFolder.getId() + "</a>" +
						"</meta>" +
				"</SetMailboxMetadataRequest>");
		
	}
	
	

	@BeforeMethod( groups = { "always" } )
	public void archiveZimletTestBeforeMethod(Method method, ITestContext testContext) throws HarnessException {
		logger.info("archiveZimletTestBeforeMethod: start");
		
//		// Call the superclass methods
//		// Is this required?
//		this.commonTestBeforeMethod(method, testContext);
//		
		// Set the Archive settings
		// If this has already been called for this account, don't logout/login again
		//
		if ( MyArchiveFolder == null ) {

			// Create and set the archive folder
			initializeArchiveFolder();

			
			// To refresh the zimlet settings: Logout/Login
			app.zPageLogin.zNavigateTo();
			app.zPageMail.zNavigateTo();

		}
		
		
		logger.info("archiveZimletTestBeforeMethod: finish");
	}
}
