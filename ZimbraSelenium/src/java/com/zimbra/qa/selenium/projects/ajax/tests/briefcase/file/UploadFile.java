package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.file;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class UploadFile extends AjaxCommonTest {

	public UploadFile() {
		logger.info("New " + UploadFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Upload file through SOAP - verify through SOAP", groups = { "smoke" })
	public void UploadFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testtextfile.txt";

		String fileName = document.getFileName(filePath);

		// Upload file to server through SOAP
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +

		"<doc l='" + briefcaseFolder.getId() + "'>" +

		"<upload id='" + attachmentId + "'/>" +

		"</doc>" +

		"</SaveDocumentRequest>");

		account.soapSelectNode("//mail:SaveDocumentResponse", 1);

		// Verify file name through SOAP
		// import from soap
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>" + fileName + "</query>"
						+ "</SearchRequest>");

		String name = account.soapSelectValue("//mail:doc", "name");

		ZAssert.assertEquals(name, fileName, "Verify file name through SOAP");
	}

	@Test(description = "Upload file through SOAP - verify through GUI", groups = { "sanity" })
	public void UploadFile_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/structure.jpg";

		String fileName = document.getFileName(filePath);

		// Upload file to server through SOAP
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseFolder.getId() + "'><upload id='"
				+ attachmentId + "'/>" + "</doc>" + "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Verify document is created
		String name = app.zPageBriefcase
				.sGetText("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
						+ fileName + ")");
		ZAssert.assertEquals(name, fileName, "Verify file name through GUI");
	}
}