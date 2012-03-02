package projects.html.tests.compose;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.cs.account.Provisioning;

import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.html.ui.MailApp;

/**
 * This class file contains all compose bottom toolbar tests
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class ComposeBtmToolBarTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@SuppressWarnings("unused")
	@DataProvider(name = "composeDataProvider")
	private Object[][] createData(Method method) {
		// String test = method.getName();
		return new Object[][] { { "_selfAccountName_", "ccuser@testdomain.com",
				"", getLocalizedData_NoSpecialChar(),
				getLocalizedData_NoSpecialChar(), "" } };
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		// set Compose in html-mode ON
		Map<String, Object> accntAttrs = new HashMap<String, Object>();
		accntAttrs.put(Provisioning.A_zimbraPrefComposeFormat,
				Provisioning.MAIL_FORMAT_HTML);
		zLoginIfRequired(accntAttrs);
		zGoToApplication("Mail");
		isExecutionARetry = false;
	}

	@SuppressWarnings("unused")
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/**
	 * Send an email in html-mode using compose & send button bottom toolbar and
	 * verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailWithBtmToolbar(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailComposeBtmToolBar();
		page.zComposeView.zEnterComposeValues(to, cc, "", subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zSendBtnBtmToolBar);
		MailApp.zClickCheckMailUntilMailShowsUp(subject);

		needReset = false;
	}

	/**
	 * Compose an email in html-mode using compose button, cancel using bottom
	 * toolbar and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cancelMailWithBtmToolbar(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailComposeBtmToolBar();
		obj.zButton.zClick(page.zComposeView.zCancelBtnBtmToolBar);
		obj.zButton.zExists(page.zMailApp.zRefreshBtn);
		obj.zButton.zExists(page.zMailApp.zComposeBtn);

		needReset = false;
	}

	/**
	 * Save draft using bottom toolbar and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void saveDraftWithBtmToolbar(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailComposeBtmToolBar();
		page.zComposeView.zEnterComposeValues(to, cc, "", subject, body,
				attachments);
		Thread.sleep(SMALL_WAIT); // lot of timing issue (also error command time out)
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtnBtmToolBar);
		Thread.sleep(SMALL_WAIT);// lot of timing issue (also error command time out)
		obj.zButton.zClick(page.zComposeView.zCancelBtnBtmToolBar);
		Thread.sleep(SMALL_WAIT);// lot of timing issue (also error command time out)
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		Thread.sleep(SMALL_WAIT);// lot of timing issue (also error command time out)
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	/**
	 * Add attachment using bottom toolbar and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addAttachmentsWithBtmToolbar(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailComposeBtmToolBar();
		page.zComposeView.zEnterComposeValues(to, cc, "", subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zAddAttachmentBtnBtmToolBar);
		obj.zButton.zExists(page.zComposeView.zAddAttachDoneBtn);
		obj.zButton.zClick(page.zComposeView.zAddAttachCancelBtn);
		obj.zButton.zClick(page.zComposeView.zSendBtn);

		needReset = false;
	}

	/**
	 * Add receipients using bottom toolbar and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addRecepientsWithBtmToolbar(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailComposeBtmToolBar();
		page.zComposeView.zEnterComposeValues(to, cc, "", subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zAddReceipientsBtnBtmToolBar);
		obj.zButton.zExists(page.zComposeView.zAddReceipAddSelectedBtn);
		obj.zButton.zExists(page.zComposeView.zAddReceipCancelBtn);
		obj.zButton.zClick(page.zComposeView.zAddReceipDoneBtn);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);

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
