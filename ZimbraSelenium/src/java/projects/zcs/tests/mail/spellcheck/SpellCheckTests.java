package projects.zcs.tests.mail.spellcheck;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * @author Amit Jagtap
 */
@SuppressWarnings("static-access")
public class SpellCheckTests extends CommonTest {
	private static String DRAFT_NEW_WINDOW_BUTTON = "zb__COMPOSE1__DETACH_COMPOSE_left_icon";

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		return new Object[][] { { "_selfAccountName_", "ccuser@testdomain.com",
				"bccuser@testdomain.com", getLocalizedData(5),
				getLocalizedData(5), "" } };
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
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

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void mandatorySpellCheck_And_Send_Bug36365(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zCheckbox.zClick(localize(locator.mandatorySpellcheck));
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject,
				"This is test.", attachments);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void spellCheck_For_TextAppt_Bug4345(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsText));
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject, subject,
				selfAccountName, "onee twoo");
		obj.zButton.zClick(localize(locator.spellCheck));

		obj.zToastAlertMessage.zAlertMsgExists("2 Misspellings",
				"Strings did not match.");
		// obj.zToastAlertMessage.zAlertMsgExists("2 "+localize(locator.
		// misspellings), "Strings did not match.");
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void spellCheck_forDraft_inNewWindow_Bug5769_And_Bug39130(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		/**
		 * Extra steps added to automate bug 39130
		 */
		page.zMailApp.zNavigateToComposingPreferences();
		obj.zTab.zClick(page.zMailApp.zPreferencesTabIconBtn);
		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject,
				"onee twoo", attachments);
		obj.zButton.zClick(ComposeView.zSaveDraftsIconBtn);
		obj.zFolder.zClick(localize(locator.drafts));
		obj.zMessageItem.zClick(subject);
		Thread.sleep(500);
		obj.zButton.zClick(localize(locator.edit));
		selenium.mouseOver(DRAFT_NEW_WINDOW_BUTTON);
		selenium.clickAt(DRAFT_NEW_WINDOW_BUTTON, "");
		Thread.sleep(2000);
		selenium.selectWindow("_blank");
		obj.zButton.zClick(localize(locator.spellCheck));
		obj.zToastAlertMessage.zAlertMsgExists("2 Misspellings",
				"Strings did not match.");
		selenium.selectWindow(null);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void objError_inSpellcheck_Bug26037(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		subject = "anoother mostake";
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject,
				"onee twoo", "");
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.reply));
		Thread.sleep(2000);
		selenium.mouseOver(DRAFT_NEW_WINDOW_BUTTON);
		selenium.clickAt(DRAFT_NEW_WINDOW_BUTTON, "");
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("button", localize(locator.spellCheck));
		obj.zButton.zClick(localize(locator.spellCheck));
		Thread.sleep(2000);
		obj.zToastAlertMessage.zAlertMsgExists("6 Misspellings",
				"Strings did not match.");
		selenium.selectWindow(null);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void illegalCharacters_and_replyAfterSpellCheck_Bug29432_and_Bug41760(
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		subject = "anoother mostake";

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		Thread.sleep(500);
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject,
				"onee twoo", "");
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.reply));
		obj.zEditor.zType("onee twoo  threee");
		obj.zButton.zClick(localize(locator.spellCheck));
		Thread.sleep(2000);
		selenium
				.click("xpath=//span[contains(@class,'SpellCheckLink') and contains(text(),'"
						+ localize(locator.checkAgain) + "')]");
		obj.zToastAlertMessage.zAlertMsgExists("3 Misspellings",
				"Strings did not match.");
		obj.zButton.zClick(ComposeView.zSaveDraftsIconBtn);
		obj.zFolder.zClick(localize(locator.drafts));
		obj.zMessageItem.zClick(subject);
		if ((SelNGBase.currentBrowserName.indexOf("MSIE 8") >= 0)) {
			Assert.assertTrue(obj.zMessageItem.zGetCurrentMsgBodyText()
					.contains("onee twoo threee"));
		} else {
			Assert.assertTrue(obj.zMessageItem.zGetCurrentMsgBodyText()
					.contains("onee twoo  threee"));
		}
		selenium.selectWindow(null);

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}