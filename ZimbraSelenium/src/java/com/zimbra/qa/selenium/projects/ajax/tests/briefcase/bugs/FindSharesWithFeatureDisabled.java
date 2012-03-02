package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.bugs;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.LinkItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogFindShares;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.PageMail;

public class FindSharesWithFeatureDisabled extends AjaxCommonTest {
String url;
	@SuppressWarnings("serial")
	public FindSharesWithFeatureDisabled() {
		logger.info("New "
				+ FindSharesWithFeatureDisabled.class.getCanonicalName());

		// test starts in the Briefcase tab
		super.startingPage = app.zPageBriefcase;

		// use an account with some of the Features disabled
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraFeatureCalendarEnabled", "FALSE");
				// put("zimbraFeatureTasksEnabled", "FALSE");
			}
		};
	}

	@Bugs(ids = "60854")
	@Test(description = "Click on Find Shares link when some of the Features are disabled - Verify Find Shares dialog is displayed", groups = { "functional" })
	public void FindSharesWithFeatureDisabled_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		LinkItem link = new LinkItem();

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Click on Find shares link
		DialogFindShares dialog = (DialogFindShares) app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, link);

		// Verify Find Shares dialog is opened
		ZAssert.assertTrue(dialog.zIsActive(),
		"Verify Find Shares dialog is opened");
		
		// Dismiss the dialog
		dialog.zClickButton(Button.B_CANCEL);	
	}	
}