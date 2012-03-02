package projects.zcs.tests.features;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.html.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

public class FamilyMailboxTests extends CommonTest {
	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test to create Parent/Child configuration and UI verification
	 */
	@SuppressWarnings("static-access")
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void familyMailbox_UIverification() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		/**
		 * Add child Account
		 */
		String parentAccount=SelNGBase.selfAccountName.toLowerCase();
		String childAccount=ProvZCS.getRandomAccount().toLowerCase();
		String childUserAccountId=ProvZCS.getAccount(childAccount).getId();

		ProvZCS.modifyAccount(parentAccount, "zimbraChildAccount", childUserAccountId);
		ProvZCS.modifyAccount(parentAccount, "zimbraPrefChildVisibleAccount", childUserAccountId);

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("class", "ZmOverviewZimletHeader");

		/**
		 * UI Verification starts here
		 * Check Mail Tab
		 */
		obj.zButton.zClick(localize(locator.mail));
		zWaitTillObjectExist("xpath", createXpath(parentAccount, localize(locator.inbox)));

		clickAt(parentAccount, localize(locator.inbox));
		clickAt(parentAccount, localize(locator.sent));
		clickAt(parentAccount, localize(locator.drafts));
		clickAt(parentAccount, localize(locator.junk));
		clickAt(parentAccount, localize(locator.trash));
		clickAt(childAccount, localize(locator.inbox));
		clickAt(childAccount, localize(locator.sent));
		clickAt(childAccount, localize(locator.drafts));
		clickAt(childAccount, localize(locator.junk));
		clickAt(childAccount, localize(locator.trash));

		obj.zButton.zExists(page.zMailApp.zNewMenuIconBtn);
		obj.zButton.zExists(page.zMailApp.zViewIconBtn);

		/**
		 * Check Address Book Tab
		 */
		obj.zButton.zClick(localize(locator.addressBook));
		zWaitTillObjectExist("xpath", createXpath(parentAccount, localize(locator.addressBook)));

		clickAt(parentAccount, localize(locator.contacts));
		clickAt(parentAccount, localize(locator.emailedContacts));
		clickAt(parentAccount, localize(locator.trash));
		clickAt(childAccount, localize(locator.contacts));
		clickAt(childAccount, localize(locator.emailedContacts));
		clickAt(childAccount, localize(locator.trash));

		obj.zButton.zExists(page.zABApp.zNewContactMenuIconBtn);

		/**
		 * Check Calendar Tab
		 */
		obj.zButton.zClick(localize(locator.calendar));
		zWaitTillObjectExist("xpath", createXpath(parentAccount, localize(locator.calendar)));

		clickAt(parentAccount, localize(locator.calendar));
		clickAt(childAccount, localize(locator.calendar));

		obj.zButton.zExists(page.zCalApp.zCalNewApptBtn);
		obj.zButton.zExists(page.zCalApp.zCalTodayBtn);

		/**
		 * Check Tasks Tab
		 */
		obj.zButton.zClick(localize(locator.tasks));
		zWaitTillObjectExist("xpath", createXpath(parentAccount, localize(locator.tasks)));

		clickAt(parentAccount, localize(locator.tasks));
		clickAt(childAccount, localize(locator.tasks));

		obj.zButton.zExists(page.zTaskApp.zTasksNewBtn);
		obj.zButton.zExists(page.zTaskApp.zTasksViewBtn);

		/**
		 * Check Documents Tab
		 */
		obj.zButton.zClick(localize(locator.documents));
		zWaitTillObjectExist("xpath", createXpath(parentAccount, localize(locator.notebook)));

		clickAt(parentAccount, localize(locator.notebook));
		clickAt(childAccount, localize(locator.notebook));

		obj.zButton.zExists(page.zDocumentCompose.zNewPageIconBtn);

		/**
		 * Check Briefcase Tab
		 */
		obj.zButton.zClick(localize(locator.briefcase));
		zWaitTillObjectExist("xpath", createXpath(parentAccount, localize(locator.briefcase)));

		clickAt(parentAccount, localize(locator.briefcase));
		clickAt(parentAccount, localize(locator.trash));
		clickAt(childAccount, localize(locator.briefcase));
		clickAt(childAccount, localize(locator.trash));

		obj.zButton.zExists(page.zBriefcaseApp.zNewMenuIconBtn);
		obj.zButton.zExists(page.zBriefcaseApp.zViewIconBtn);

		/**
		 * Check Preferences Tab
		 * Parent Preferences
		 */
		obj.zButton.zClick(localize(locator.preferences));
		zWaitTillObjectExist("xpath", createXpath(parentAccount, localize(locator.preferences)));
		obj.zButton.zExists(localize(locator.changePassword));
		checkHeaders(localize(locator.loginOptions));
		checkHeaders(localize(locator.searches));
		checkHeaders(localize(locator.other));

		clickAt(parentAccount, localize(locator.mail));
		checkHeaders(localize(locator.displayMessages));
		checkHeaders(localize(locator.messagesReceiving));		
		obj.zRadioBtn.zExists(localize(locator.displayAsHTML));
		obj.zEditField.zExists(localize(locator.forwardCopyTo));
		selenium.isElementPresent("//*[contains(@class, 'DwtListView ZmWhiteBlackList')]");

		clickAt(parentAccount, localize(locator.composing));
		checkHeaders(localize(locator.composingMessages));
		checkLabels("Compose:");
		checkLabels("Prefix:");
		selenium.isElementPresent("link=Accounts Page");

		clickAt(parentAccount, localize(locator.signatures));
		checkHeaders(localize(locator.signatures));
		checkHeaders(localize(locator.signaturesUsing));
		obj.zButton.zExists(localize(locator.addSignature));
		selenium.isElementPresent("link=Accounts Page");

		clickAt(parentAccount, localize(locator.accounts));
		checkHeaders(localize(locator.accounts));
		checkHeaders(localize(locator.accountHeaderPrimary));
		obj.zButton.zExists(localize(locator.addExternalAccount));
		obj.zButton.zExists(localize(locator.signatureDoNotAttach));		

		clickAt(parentAccount, localize(locator.filterRules));
		checkHeaders(localize(locator.filterRules));
		obj.zButton.zExists(localize(locator.newFilter));

		clickAt(parentAccount, localize(locator.addressBook));
		checkHeaders(localize(locator.options));
		selenium.isElementPresent("//*[contains(@id, 'AUTOCOMPLETE_NO_GROUP_MATCH')]");
		selenium.isElementPresent("//*[contains(@id, 'AUTO_ADD_ADDRESS')]");

		clickAt(parentAccount, localize(locator.calendar));
		checkHeaders(localize(locator.general));
		checkHeaders(localize(locator.apptCreating));

		clickAt(parentAccount, localize(locator.sharing));
		obj.zButton.zExists(localize(locator.share));

		clickAt(parentAccount, localize(locator.importExport));
		checkHeaders(localize(locator.importLabel));
		checkHeaders(localize(locator.importLabel));		

		clickAt(parentAccount, localize(locator.shortcuts));
		clickAt(parentAccount, localize(locator.zimlets));
		checkHeaders(localize(locator.zimlets));

		/**
		 * Check Preferences Tab
		 * Child Preferences
		 */
		clickAt(childAccount, localize(locator.mail));
		checkHeaders(localize(locator.messagesReceiving));		
		selenium.isElementPresent("//*[contains(@class, 'DwtListView ZmWhiteBlackList')]");

		clickAt(childAccount, localize(locator.signature));
		checkHeaders(localize(locator.signatures));
		checkHeaders(localize(locator.signaturesUsing));
		obj.zButton.zExists(localize(locator.addSignature));
		selenium.isElementPresent("link=Accounts Page");

		clickAt(childAccount, localize(locator.accounts));
		checkHeaders(localize(locator.accounts));
		checkHeaders(localize(locator.accountHeaderPrimary));
		obj.zButton.zExists(localize(locator.addExternalAccount));
		obj.zButton.zExists(localize(locator.signatureDoNotAttach));		

		clickAt(childAccount, localize(locator.filterRules));
		checkHeaders(localize(locator.filterRules));
		obj.zButton.zExists(localize(locator.newFilter));

		clickAt(childAccount, localize(locator.addressBook));
		checkHeaders(localize(locator.options));
		selenium.isElementPresent("//*[contains(@id, 'AUTOCOMPLETE_NO_GROUP_MATCH')]");
		selenium.isElementPresent("//*[contains(@id, 'AUTO_ADD_ADDRESS')]");

		clickAt(childAccount, localize(locator.calendar));
		checkHeaders(localize(locator.general));
		checkHeaders(localize(locator.permissions));

		clickAt(childAccount, localize(locator.sharing));
		obj.zButton.zExists(localize(locator.share));

		clickAt(childAccount, localize(locator.importExport));
		checkHeaders(localize(locator.importLabel));
		checkHeaders(localize(locator.importLabel));		

		needReset = false;
	}


	public void clickAt(String accountName, String tabName) throws Exception{
		selenium.clickAt("//*[contains(@id,'"+accountName+"') and contains(text(),'"+tabName+"')]","");
	}

	public String createXpath(String accountName, String tabName) throws Exception{
		return "//*[contains(@id,'"+accountName+"') and contains(text(),'"+tabName+"')]";
	}

	public void checkHeaders(String headerText) throws Exception {
		selenium.isElementPresent("//*[contains(@class, 'ZOptionsHeader ImgPrefsHeader') and contains(text(), '"+headerText+"')]");
	}

	public void checkLabels(String labelText) throws Exception {
		selenium.isElementPresent("//*[contains(@class, 'ZOptionsLabel') and contains(text(), '"+labelText+"')]");
	}


	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}

}
