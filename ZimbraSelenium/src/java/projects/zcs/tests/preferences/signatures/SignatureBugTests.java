package projects.zcs.tests.preferences.signatures;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import framework.util.RetryFailedTests;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;

/**
 * @author Jitesh Sojitra
 * 
 */

@SuppressWarnings("static-access")
public class SignatureBugTests extends CommonTest {
	@DataProvider(name = "SigPrefDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("lossOfSpacesAfterSignatureChange_Bug41092")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("verifyingComposingAndSignaturePref_39282")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };

		} else {
			return new Object[][] { {} };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
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

	// Tests
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void lossOfSpacesAfterSignatureChange_Bug41092(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String defaultSignature = signatureName + "_default";
		String subject = "signature test";
		String body = "This is body with some spaces. We need to verify that spaces are not removed after switching signature.";

		/**
		 * Create Signature 1
		 */
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

		/**
		 * Create Signature 2
		 */
		page.zSignaturePref.zNavigateToPreferenceSignature();
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(defaultSignature, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

		/**
		 * Make Signature 2 default
		 */
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick(defaultSignature);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

		/**
		 * 1. Compose Mail. 2. Switch Signature. 3. Send Mail to self and click
		 * on received mail.
		 */
		page.zMailApp.zNavigateToMailApp();
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		page.zComposeView.zEnterComposeValues("_selfAccountName_", "", "",
				subject, body, "");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		Thread.sleep(1000);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		zGoToApplication("Mail");
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zClick(subject);

		/**
		 * 1. Verify headers and body is correct. 2. Spaces should not be
		 * trimmed.
		 */
		page.zComposeView.zVerifyMsgHeaders("_selfAccountName_", "", "",
				subject, body, "");
		obj.zMessageItem.zVerifyCurrentMsgBodyText(body);

		needReset = false;
	}

	/**
	 * Test Case:-verifyingComposing(Always Compose in New
	 * Window)AndSignaturePref(AddSignature as Plain Text) Go to Preferences ->
	 * Composing Select: Always Compose in New Window. Compose: As Text. Go to
	 * Preference -> Signatures Add Signature as Plain Text. Go to Inbox Click
	 * on 'New' to compose a mail in New Window. From Options change 'Format As
	 * HTML' and send a mail. Now again Go to Preference Tab -> Signature. Click
	 * on 'Save' Observe that focus should not remains thr. Now click on any of
	 * the tabs "Mail" Verify Message Subject.
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyingComposingAndSignaturePref_39282(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zCheckbox.zClick(localize(locator.composeInNewWin));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Composing should be saved");

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		Thread.sleep(1500);
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zSendBtn_newWindow);
		obj.zButton.zClick(ComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(localize(locator.formatAsHtml));
		Thread.sleep(1000);
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		String actualSignature = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actualSignature.contains(signatureBody),
				"Signature not included in mail body");
		String subject = "signature";
		page.zComposeView.zSendMailToSelfAndVerify("_selfAccountName_",
				"ccuser@testdomain.com", "bccuser@testdomain.com", subject,
				getLocalizedData(5), "");

		page.zSignaturePref.zNavigateToPreferenceSignature();
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		zGoToApplication("Mail");
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	private void handleRetry() throws Exception {
		// TODO Auto-generated method stub
		isExecutionARetry = false;// reset this to false
		zLogin();
	}

}
