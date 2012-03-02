package projects.zcs.tests.preferences.composing;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.testng.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class ComposePreferencesTestsUI extends CommonTest {

	//--------------------------------------------------------------------------
	@DataProvider(name = "composePreferencesDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		return new Object[][] { { "localize(locator.GAL)" } };

	}

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();

		String accountName = selfAccountName;

		ProvZCS.modifyAccount(accountName, "zimbraPrefComposeFormat", "text");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefHtmlEditorDefaultFontFamily", "Times New Roman");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefHtmlEditorDefaultFontSize", "12pt");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefForwardReplyInOriginalFormat", "FALSE");

		ProvZCS.modifyAccount(accountName, "zimbraPrefComposeInNewWindow",
				"FALSE");

		ProvZCS.modifyAccount(accountName, "zimbraPrefAutoSaveDraftInterval",
				"30s");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefReplyIncludeOriginalText", "includeBody");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefForwardIncludeOriginalText", "includeBody");

		ProvZCS.modifyAccount(accountName, "zimbraPrefForwardReplyPrefixChar",
				">");

		ProvZCS.modifyAccount(accountName, "zimbraPrefSaveToSent", "TRUE");

		// selenium.refresh();
		zReloginToAjax();

		Thread.sleep(5000);
		isExecutionARetry = false;
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingComposeFormat() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zRadioBtn.zClick(localize(locator.composeAsHTML), "1");

		waitForIE();

		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(3000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefComposeFormat");

		Assert.assertEquals(actualVal, "html",
				"Compose format set is not set in db. Actual value is "
						+ actualVal);

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zRadioBtn.zClick(localize(locator.composeAsText));

		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefComposeFormat");

		Assert.assertEquals(actualVal, "text",
				"Compose format set is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingFontSetting() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (config.getString("locale").equals("en_US")) {
			String actualVal;
			String accountName = selfAccountName;
			String fontSize;

			page.zMailApp.zNavigateToComposingPreferences();

			obj.zFeatureMenu.zClick(localize(locator.fFamilyLabel));
			obj.zMenuItem.zClick("Arial");

			String currentFont = "10";
			if (config.getString("locale").equals("fr")
					|| config.getString("locale").equals("nl")
					|| config.getString("locale").equals("de")
					|| config.getString("locale").equals("hi")
					|| config.getString("locale").equals("sv")
					|| config.getString("locale").equals("da")) {
				fontSize = currentFont + " pt";
			} else if (config.getString("locale").equals("es")) {
				fontSize = currentFont + " p";
			} else if (config.getString("locale").equals("it")) {
				fontSize = currentFont + " punti";
			} else {
				fontSize = currentFont + "pt";
			}

			obj.zFeatureMenu.zClick(localize(locator.fFamilyLabel), "2");
			obj.zMenuItem.zClick(fontSize);

			waitForIE();

			obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

			Thread.sleep(2000);

			actualVal = ProvZCS.getAccountPreferenceValue(accountName,
					"zimbraPrefHtmlEditorDefaultFontFamily");

			Assert.assertEquals(actualVal, "Arial",
					"Font face set is not set in db. Actual value is "
							+ actualVal);

			actualVal = ProvZCS.getAccountPreferenceValue(accountName,
					"zimbraPrefHtmlEditorDefaultFontSize");

			Assert.assertTrue(actualVal.indexOf(currentFont) >= 0,
					"Font size set is not set in db. Actual value is "
							+ actualVal);

			page.zMailApp.zNavigateToComposingPreferences();

			currentFont = "12";
			if (config.getString("locale").equals("fr")
					|| config.getString("locale").equals("nl")
					|| config.getString("locale").equals("de")
					|| config.getString("locale").equals("hi")
					|| config.getString("locale").equals("sv")
					|| config.getString("locale").equals("da")) {
				fontSize = currentFont + " pt";
			} else if (config.getString("locale").equals("es")) {
				fontSize = currentFont + " p";
			} else if (config.getString("locale").equals("it")) {
				fontSize = currentFont + " punti";
			} else {
				fontSize = currentFont + "pt";
			}

			obj.zFeatureMenu.zClick(localize(locator.fFamilyLabel));
			obj.zMenuItem.zClick("Times New Roman");

			obj.zFeatureMenu.zClick(localize(locator.fFamilyLabel), "2");
			obj.zMenuItem.zClick(fontSize);

			waitForIE();

			obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

			Thread.sleep(2000);

			actualVal = ProvZCS.getAccountPreferenceValue(accountName,
					"zimbraPrefHtmlEditorDefaultFontFamily");

			Assert.assertEquals(actualVal, "Times New Roman",
					"Font face set is not set in db. Actual value is "
							+ actualVal);

			actualVal = ProvZCS.getAccountPreferenceValue(accountName,
					"zimbraPrefHtmlEditorDefaultFontSize");

			Assert.assertTrue(actualVal.indexOf(currentFont) >= 0,
					"Font size set is not set in db. Actual value is "
							+ actualVal);
		}

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingReplyFwdUsingOriginalFormat() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zCheckbox.zClick(localize(locator.replyForwardInSameFormat));

		waitForIE();

		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefForwardReplyInOriginalFormat");

		Assert.assertEquals(actualVal, "TRUE",
				"Reply/fwd format set is not set in db. Actual value is "
						+ actualVal);

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zCheckbox.zClick(localize(locator.replyForwardInSameFormat));

		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefForwardReplyInOriginalFormat");

		Assert.assertEquals(actualVal, "FALSE",
				"Reply/fwd format set is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingComposeInNewWindow() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zCheckbox.zClick(localize(locator.composeInNewWin));

		waitForIE();

		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefComposeInNewWindow");

		Assert.assertEquals(actualVal, "TRUE",
				"Compose in new window set is not set in db. Actual value is "
						+ actualVal);

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zCheckbox.zClick(localize(locator.composeInNewWin));

		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefComposeInNewWindow");

		Assert.assertEquals(actualVal, "FALSE",
				"Compose in new window set is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingAutomaticallySaveDrafts() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zCheckbox.zClick(localize(locator.autoSaveDrafts));

		waitForIE();

		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefAutoSaveDraftInterval");

		Assert.assertEquals(actualVal, "0",
				"Autosave drafts value set is not set in db. Actual value is "
						+ actualVal);

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zCheckbox.zClick(localize(locator.autoSaveDrafts));

		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefAutoSaveDraftInterval");

		Assert.assertEquals(actualVal, "30",
				"Autosave draft value set is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingReplyingToEmail() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zFeatureMenu.zClick(localize(locator.composeReplyEmail), "2");
		obj.zMenuItem.zClick(localize(locator.dontInclude));

		waitForIE();

		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefReplyIncludeOriginalText");

		Assert.assertEquals(actualVal, "includeNone",
				"Reply/Reply All option set is not set in db. Actual value is "
						+ actualVal);

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zFeatureMenu.zClick(localize(locator.composeReplyEmail), "2");
		obj.zMenuItem.zClick(localize(locator.includeInBody));

		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefReplyIncludeOriginalText");

		Assert.assertEquals(actualVal, "includeBodyAndHeaders",
				"Reply/Reply All option set is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingForwardEmail() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zFeatureMenu.zClick(localize(locator.forwardingEmail), "3");
		obj.zMenuItem.zClick(localize(locator.includeOriginalAsAttach));

		waitForIE();

		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefForwardIncludeOriginalText");

		Assert.assertEquals(actualVal, "includeAsAttachment",
				"Forward option set is not set in db. Actual value is "
						+ actualVal);

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zFeatureMenu.zClick(localize(locator.forwardingEmail), "3");
		obj.zMenuItem.zClick(localize(locator.includeInBody));

		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefForwardIncludeOriginalText");

		Assert.assertEquals(actualVal, "includeBodyAndHeaders",
				"Forward option set is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingPrefixSetting() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zRadioBtn.zClick("|");

		waitForIE();

		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefForwardReplyPrefixChar");

		Assert.assertEquals(actualVal, "|",
				"Prefix option set is not set in db. Actual value is "
						+ actualVal);

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zRadioBtn.zClick(">");

		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefForwardReplyPrefixChar");

		Assert.assertEquals(actualVal, ">",
				"Prefix option set is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zComposingSaveToSent() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		obj.zRadioBtn.zClick(localize(locator.saveToSentNOT));

		waitForIE();

		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefSaveToSent");

		Assert.assertEquals(actualVal, "FALSE",
				"'Save to Sent' option set is not set in db. Actual value is "
						+ actualVal);

		page.zMailApp.zNavigateToComposingPreferences();

		if (config.getString("locale").equals("de")) {
			obj.zRadioBtn.zClick("Kopie im Ordner");
		} else {
			obj.zRadioBtn.zClick(localize(locator.saveToSent));
		}

		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefSaveToSent");

		Assert.assertEquals(actualVal, "TRUE",
				"'Save to Sent' option set is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	private void waitForIE() throws Exception {
		String browser = config.getString("browser");

		if (browser.equals("IE"))
			Thread.sleep(1000);

	}

	/**
	 * Test case:ZWC doesn't honor html font size from Preferences Steps 1.Login
	 * to Web client 2.Go to Preferences 3.Clikc on Compose 4. Select Radio
	 * Button "As Html" and select font size=12 pt 5.Click on Save. 6.Compose
	 * Mail to self and verify 7.Verify text in the Message body 8.verify Its
	 * Font Size it should be 12Pt
	 * 
	 * @throws Exception
	 * @author Girish
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void checkFontSizeInMsgBody_36919() throws Exception {

		if (isExecutionARetry)
			handleRetry();
		String fontSize;
		String currentFont;
		String actualVal;
		String accountName = selfAccountName;

		page.zMailApp.zNavigateToComposingPreferences();

		selenium.clickAt(
				"xpath=//label[contains(@id,'_text_right') and contains(text(),'"
						+ localize(locator.composeAsHTML) + "')]", "");

		currentFont = "12";
		if (config.getString("locale").equals("fr")
				|| config.getString("locale").equals("nl")
				|| config.getString("locale").equals("de")
				|| config.getString("locale").equals("hi")
				|| config.getString("locale").equals("sv")
				|| config.getString("locale").equals("da")) {
			fontSize = currentFont + " pt";
		} else if (config.getString("locale").equals("es")) {
			fontSize = currentFont + " p";
		} else if (config.getString("locale").equals("it")) {
			fontSize = currentFont + " punti";
		} else {
			fontSize = currentFont + "pt";
		}

		obj.zFeatureMenu.zClick(localize(locator.fFamilyLabel));
		obj.zMenuItem.zClick("Times New Roman");

		obj.zFeatureMenu.zClick(localize(locator.fFamilyLabel), "2");
		obj.zMenuItem.zClick(fontSize);
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(accountName,
				"ccuser@testdomain.com", "bccuser@testdomain.com", "sub",
				"hello", "");

		selenium.selectFrame("css=iframe[id*='zv__CLV__MSG_body__iframe']");
		String messageBodyText = selenium.getText("xpath=/html/body/div");
		Assert.assertTrue(messageBodyText.contains("hello"));
		Assert
				.assertTrue(selenium
						.isElementPresent("xpath=/html/body/div[contains(@style,'font-size: "
								+ fontSize + "')]"));
		selenium.selectFrame("relative=top");

		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
