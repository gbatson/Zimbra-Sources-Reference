package projects.zcs.tests.preferences.zimlets;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
public class ZimletPreferenceTests extends CommonTest {

	public static final String zZimletDateChkBox = "id=com_zimbra_date_zimletCheckbox";
	public static final String zZimletURLChkBox = "id=com_zimbra_url_zimletCheckbox";
	public static final String zZimletHighlightPhoneChkBox = "id=com_zimbra_phone_zimletCheckbox";
	public static final String zZimletEmailChkBox = "id=com_zimbra_email_zimletCheckbox";
	public static final String zZimletEmoticonsChkBox = "id=com_zimbra_ymemoticons_zimletCheckbox";
	public static final String zZimletLocalChkBox = "id=com_zimbra_local_zimletCheckbox";

	//--------------------------------------------------------------------------
	// SECTION 1: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Preferences");
		zGoToPreferences("Zimlets");
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
	// SECTION 2: TEST-METHODS
	//--------------------------------------------------------------------------
	@SuppressWarnings("static-access")
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zimletPreferenceTest() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		zWaitTillObjectExist("checkbox", zZimletDateChkBox);
		Thread.sleep(3000);
		obj.zCheckbox.zActivate(zZimletEmailChkBox);
		obj.zCheckbox.zActivate(zZimletURLChkBox);
		obj.zCheckbox.zActivate(zZimletEmoticonsChkBox);
		obj.zCheckbox.zActivate(zZimletDateChkBox);
		obj.zCheckbox.zActivate(zZimletLocalChkBox);
		obj.zCheckbox.zActivate(zZimletHighlightPhoneChkBox);
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(3000);
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		Thread.sleep(5000);

		zGoToApplication("Preferences");
		zGoToPreferences("Zimlets");
		System.out.println(ProvZCS.getAccountPreferenceValue(
				SelNGBase.selfAccountName, "zimbraPrefZimlets"));
		obj.zCheckbox.zVerifyIsNotChecked(zZimletEmailChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletURLChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletEmoticonsChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletDateChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletLocalChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletHighlightPhoneChkBox);
		
		// verify zimlets stuff if possible
		//page.zComposeView.zNavigateToMailCompose();
		//page.zComposeView.zSendMailToSelfAndSelectIt(SelNGBase.selfAccountName,
		//		"", "", "subject", "body", "");
		

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}