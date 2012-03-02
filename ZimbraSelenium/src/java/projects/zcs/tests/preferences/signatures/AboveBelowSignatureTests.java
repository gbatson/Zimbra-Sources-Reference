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
public class AboveBelowSignatureTests extends CommonTest {
	@DataProvider(name = "SigPrefDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("verifyTextSignatureAboveIncludedMsgInReply_Bug45880")
				|| test
						.equals("verifyTextSignatureBelowIncludedMsgInReply_Bug45880")
				|| test
						.equals("verifyHtmlSignatureAboveIncludedMsgInReply_Bug45880")
				|| test
						.equals("verifyHtmlSignatureBelowIncludedMsgInReply_Bug45880")
				|| test
						.equals("verifyTextSignatureAboveIncludedMsgInFwd_Bug45880")
				|| test
						.equals("verifyHtmlSignatureAboveIncludedMsgInFwd_Bug45880")
				|| test
						.equals("verifyTextSignatureBelowIncludedMsgInFwd_Bug45880")
				|| test
						.equals("verifyHtmlSignatureBelowIncludedMsgInFwd_Bug45880")
				|| test
						.equals("verifyTextSignatureAboveIncludedMsgInReplyAll_Bug45880")
				|| test
						.equals("verifyTextSignatureBelowIncludedMsgInReplyAll_Bug45880")
				|| test
						.equals("verifyHtmlSignatureAboveIncludedMsgInReplyAll_Bug45880")
				|| test
						.equals("verifyHtmlSignatureBelowIncludedMsgInReplyAll_Bug45880")
				|| test.equals("twoSignaturesOnSwitching_Bug41404")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), selfAccountName,
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "" } };
		} else
			return new Object[][] { { "" } };
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

	/**
	 * Test Case:Reply-Text:-verifyTextSignatureAboveIncludedMsgInReply 1.Login
	 * to ZCS 2.Go to Preference 3.Create Signature in Text mode 4.select or
	 * check "Above included messages" check box 5.Save 6.Compose a mail to self
	 * and verify it 7.Click on Reply and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name 9.Verify
	 * whether signature name place above or below included message 10.It should
	 * place above included message
	 * 
	 * @author Girish
	 */

	// Reply:-Text: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureAboveIncludedMsgInReply_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"text");
		Thread.sleep(1000);
		page.zSignaturePref.zUsingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInText(
				localize(locator.aboveQuotedText), signatureBody, "Reply");

		needReset = false;
	}

	/**
	 * Test Case:Reply-Html Mode:verifyHtmlSignatureAboveIncludedMsgInReply
	 * 1.Login to ZCS 2.Go to Preference 3.Create Signature in Html mode
	 * 4.select or check "Above included messages" check box 5.Save 6.Compose a
	 * mail in html mode and send it to self and verify it 7.Click on Reply and
	 * verify subject field with RE: key word 8.click on Signature Icon button
	 * and select signature name 9.Verify whether signature name place above or
	 * below included message 10.It should place above included message
	 * 
	 * @author Girish
	 */
	// Reply:-html: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureAboveIncludedMsgInReply_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"html");
		Thread.sleep(1000);
		page.zSignaturePref.zUsingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInHTML(
				localize(locator.aboveQuotedText), signatureBody, "Reply");

		needReset = false;
	}

	/**
	 * Test Case:Reply-Text:-verifyTextSignatureBelowIncludedMsgInReply 1.Login
	 * to ZCS 2.Go to Preference 3.Create Signature in Text mode 4.select or
	 * check "Below included messages" check box 5.Save 6.Compose a mail to self
	 * and verify it 7.Click on Reply and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name 9.Verify
	 * whether signature name place above or below included message 10.It should
	 * place Below included message
	 * 
	 * @author Girish
	 */
	// Reply:-Text: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureBelowIncludedMsgInReply_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"text");
		Thread.sleep(1000);
		page.zSignaturePref
				.zUsingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInText(
				localize(locator.atBottomOfMessage), signatureBody, "Reply");

		needReset = false;
	}

	/**
	 * Test Case:Reply-Html
	 * Mode:BelowIncludedMsg:verifyHtmlSignatureBelowIncludedMsgInReply 1.Login
	 * to ZCS 2.Go to Preference 3.Create Signature in Html mode 4.select or
	 * check "below included messages" check box 5.Save 6.Compose a mail in html
	 * mode and send it to self and verify it 7.Click on Reply and verify
	 * subject field with RE: key word 8.click on Signature Icon button and
	 * select signature name 9.Verify whether signature name place above or
	 * below included message 10.It should place below included message
	 * 
	 * @author Girish
	 */
	// Reply:-html: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureBelowIncludedMsgInReply_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"html");
		Thread.sleep(1000);
		page.zSignaturePref
				.zUsingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInHTML(
				localize(locator.atBottomOfMessage), signatureBody, "Reply");

		needReset = false;
	}

	/**
	 * Test Case:Forward-Text:-verifyTextSignatureAboveIncludedMsgInFwd 1.Login
	 * to ZCS 2.Go to Preference 3.Create Signature in Text mode 4.select or
	 * check "Above included messages" check box 5.Save 6.Compose a mail to self
	 * and verify it 7.Click on Forward and verify subject field with Fwd: key
	 * word 8.click on Signature Icon button and select signature name 9.Verify
	 * whether signature name place above or below included message 10.It should
	 * place above included message
	 * 
	 * @author Girish
	 */

	// FWD:-Text: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureAboveIncludedMsgInFwd_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"text");
		Thread.sleep(1000);
		page.zSignaturePref.zUsingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Fwd: " + subject),
				"Subject doesnot Contains Fwd:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInText(
				localize(locator.aboveQuotedText), signatureBody, "Forward");

		needReset = false;
	}

	/**
	 * Test Case:Forward-Html Mode:verifyHtmlSignatureAboveIncludedMsgInFwd
	 * 1.Login to ZCS 2.Go to Preference 3.Create Signature in Html mode
	 * 4.select or check "Above included messages" check box 5.Save 6.Compose a
	 * mail in html mode and send it to self and verify it 7.Click on Forward
	 * and verify subject field with Fwd: key word 8.click on Signature Icon
	 * button and select signature name 9.Verify whether signature name place
	 * above or below included message 10.It should place above included message
	 * 
	 * @author Girish
	 */

	// Fwd:-html: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureAboveIncludedMsgInFwd_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"html");
		Thread.sleep(1000);
		page.zSignaturePref.zUsingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Fwd: " + subject),
				"Subject doesnot Contains Fwd:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInHTML(
				localize(locator.aboveQuotedText), signatureBody, "Forward");

		needReset = false;
	}

	/**
	 * Test Case:Forward-Text:-verifyTextSignatureBelowIncludedMsgInFwd 1.Login
	 * to ZCS 2.Go to Preference 3.Create Signature in Text mode 4.select or
	 * check "Below included messages" check box 5.Save 6.Compose a mail to self
	 * and verify it 7.Click on Forward and verify subject field with Fwd: key
	 * word 8.click on Signature Icon button and select signature name 9.Verify
	 * whether signature name place above or below included message 10.It should
	 * place Below included message
	 * 
	 * @author Girish
	 */
	// Fwd:-Text: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureBelowIncludedMsgInFwd_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"text");
		Thread.sleep(1000);
		page.zSignaturePref
				.zUsingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Fwd: " + subject),
				"Subject doesnot Contains Fwd:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInText(
				localize(locator.atBottomOfMessage), signatureBody, "Forward");

		needReset = false;
	}

	/**
	 * Test Case:Forward-Html
	 * Mode:BelowIncludedMsg:verifyHtmlSignatureBelowIncludedMsgInFwd 1.Login to
	 * ZCS 2.Go to Preference 3.Create Signature in Html mode 4.select or check
	 * "Below included messages" check box 5.Save 6.Compose a mail in html mode
	 * and send it to self and verify it 7.Click on Forward and verify subject
	 * field with Fwd: key word 8.click on Signature Icon button and select
	 * signature name 9.Verify whether signature name place above or below
	 * included message 10.It should place below included message
	 * 
	 * @author Girish
	 */
	// Fwd:-html: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureBelowIncludedMsgInFwd_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"html");
		Thread.sleep(1000);
		page.zSignaturePref
				.zUsingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Fwd: " + subject),
				"Subject doesnot Contains FWD:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInHTML(
				localize(locator.atBottomOfMessage), signatureBody, "Forward");

		needReset = false;
	}

	/**
	 * Test Case:ReplyAll-Text:-verifyTextSignatureAboveIncludedMsgInReplyAll
	 * 1.Login to ZCS 2.Go to Preference 3.Create Signature in Text mode
	 * 4.select or check "Above included messages" check box 5.Save 6.Compose a
	 * mail to self and verify it 7.Click on ReplyAll and verify subject field
	 * with RE: key word 8.click on Signature Icon button and select signature
	 * name 9.Verify whether signature name place above or below included
	 * message 10.It should place above included message
	 * 
	 * @author Girish
	 */
	// ReplyAll:-Text: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureAboveIncludedMsgInReplyAll_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"text");
		Thread.sleep(1000);
		page.zSignaturePref.zUsingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInText(
				localize(locator.aboveQuotedText), signatureBody, "ReplyAll");

		needReset = false;
	}

	/**
	 * Test Case:ReplyAll-Html
	 * Mode:verifyHtmlSignatureAboveIncludedMsgInReplyAll 1.Login to ZCS 2.Go to
	 * Preference 3.Create Signature in Html mode 4.select or check
	 * "Above included messages" check box 5.Save 6.Compose a mail in html mode
	 * and send it to self and verify it 7.Click on ReplyAll and verify subject
	 * field with RE: key word 8.click on Signature Icon button and select
	 * signature name 9.Verify whether signature name place above or below
	 * included message 10.It should place above included message
	 * 
	 * @author Girish
	 */
	// ReplyAll:-html: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureAboveIncludedMsgInReplyAll_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"html");
		Thread.sleep(1000);
		page.zSignaturePref.zUsingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInHTML(
				localize(locator.aboveQuotedText), signatureBody, "ReplyAll");

		needReset = false;
	}

	/**
	 * Test Case:ReplyAll-Text:-verifyTextSignatureBelowIncludedMsgInReplyAll
	 * 1.Login to ZCS 2.Go to Preference 3.Create Signature in Text mode
	 * 4.select or check "Below included messages" check box 5.Save 6.Compose a
	 * mail to self and verify it 7.Click on ReplyAll and verify subject field
	 * with RE: key word 8.click on Signature Icon button and select signature
	 * name 9.Verify whether signature name place above or below included
	 * message 10.It should place Below included message
	 * 
	 * @author Girish
	 */

	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureBelowIncludedMsgInReplyAll_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"text");
		Thread.sleep(1000);
		page.zSignaturePref
				.zUsingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInText(
				localize(locator.atBottomOfMessage), signatureBody, "ReplyAll");

		needReset = false;
	}

	/**
	 * Test Case:ReplyAll-Html
	 * Mode:BelowIncludedMsg:verifyHtmlSignatureBelowIncludedMsgInReplyAll
	 * 1.Login to ZCS 2.Go to Preference 3.Create Signature in Html mode
	 * 4.select or check "below included messages" check box 5.Save 6.Compose a
	 * mail in html mode and send it to self and verify it 7.Click on ReplyAll
	 * and verify subject field with RE: key word 8.click on Signature Icon
	 * button and select signature name 9.Verify whether signature name place
	 * above or below included message 10.It should place below included message
	 * 
	 * @author Girish
	 */
	// ReplyAll:-html: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureBelowIncludedMsgInReplyAll_Bug45880(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"html");
		Thread.sleep(1000);
		page.zSignaturePref
				.zUsingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zSignaturePref.zVerifySignaturePlaceInHTML(
				localize(locator.atBottomOfMessage), signatureBody, "ReplyAll");

		needReset = false;
	}

	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void twoSignaturesOnSwitching_Bug41404(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String defaultSignature = signatureName + "_default";
		String defaultSignatureBody = signatureBody + "_default";
		String nondefaultSignatureBody = signatureBody + "_non_default";

		String subject = "signature test";
		String body = "This is test body.";

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		/**
		 * Following code is added to test bug 43244
		 */
		page.zSignaturePref.zNavigateToPreferenceSignature();
		obj.zButton.zClick(localize(locator.formatAsText));
		obj.zMenuItem.zClick(localize(locator.formatAsHtml));
		Thread.sleep(1000);
		selenium.clickAt("//*[contains(@class,'ImgImageDoc')]", "");
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.addImg));
		selenium.clickAt("//*[contains(@class,'ImgInsertImage')]", "");
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.uploadImage));
		/**
		 * Code for bug 43244 ends here.
		 */

		/**
		 * Create Signature 1
		 */
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(defaultSignature,
				defaultSignatureBody, "TEXT");

		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

		/**
		 * Create Signature 2
		 */
		page.zSignaturePref.zNavigateToPreferenceSignature();
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName,
				nondefaultSignatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

		/**
		 * Make Signature 1 default
		 */
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick(defaultSignature);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

		/**
		 * 1. Compose Mail. 2. Switch Signature to non-default. 3. Switch back
		 * to default signature. 4. Send Mail to self and click on received
		 * mail.
		 */
		page.zMailApp.zNavigateToMailApp();
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		page.zComposeView.zEnterComposeValues("_selfAccountName_", "", "",
				subject, body, "");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(defaultSignature);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		Thread.sleep(1000);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		zGoToApplication("Mail");
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zClick(subject);

		/**
		 * 1. Verify Body contains default signature. 2. Verify body does not
		 * contain non-default signature.
		 * 
		 */
		obj.zMessageItem.zVerifyCurrentMsgBodyText(defaultSignatureBody);
		obj.zMessageItem
				.zVerifyCurrentMsgBodyDoesNotHaveText(nondefaultSignatureBody);

		needReset = false;
	}

	// //end
	private void waitForIE() throws Exception {
		String browser = config.getString("browser");
		if (browser.equals("IE"))
			Thread.sleep(1000);
	}

	private void handleRetry() throws Exception {
		// TODO Auto-generated method stub
		isExecutionARetry = false;// reset this to false
		zLogin();
	}
}