package projects.zcs.tests.mail.messageactions;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 * 
 *         Class contains 17 methods regarding mail contact right click menu
 *         functionalities. From Search, Advanced Search...New Filter
 * 
 *         Below parameter used to pass values from data provider by injecting
 *         message
 * 
 *         Note - injecting message works fine in english language so hard coded
 *         data provider values in english. Subject shows junk character if use
 *         other locale as of now..
 * 
 * @param from
 *            - necessary here because injecting message through API
 * @param to
 *            - to whom, you want to send message (current logged in user)
 * @param cc
 *            - cc user
 * @param bcc
 *            - bcc user
 * @param subject
 *            - mail subject
 * @param body
 *            - body
 * @param attachments
 *            - attachments
 */
@SuppressWarnings("static-access")
public class MessageRightClickMenuTests extends CommonTest {

	protected static String showOrigText = "";
	protected static String fromUserName = "";
	protected static String commonAccount = "";
	protected static String[] recipients;
	protected int i = 0;
	protected boolean randomAcctCreatedFlag;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@SuppressWarnings("unused")
	@DataProvider(name = "lmtpDataProvider")
	private Object[][] createData(Method method) throws ServiceException {
		if (i == 0) {
			commonAccount = ProvZCS.getRandomAccount();
			i = i + 1;
		}
		String test = method.getName();
		if ((test.equals("verifyAllRightClickMenusExists"))
				|| (test.equals("rtClickToContact_Search"))
				|| (test.equals("rtClickToContact_AdvancedSearch"))
				|| (test.equals("rtClickToContact_NewEmail"))
				|| (test.equals("rtClickToContact_AddEditContact"))
				|| (test.equals("rtClickToContactAndSubject_Reply"))
				|| (test.equals("rtClickToContactAndSubject_ReplytoAll"))
				|| (test.equals("rtClickToContactAndSubject_Forward"))
				|| (test.equals("rtClickToContactAndSubject_EditasNew"))
				|| (test.equals("rtClickToContactAndSubject_TagMessage"))
				|| (test.equals("rtClickToContactAndSubject_Print"))
				|| (test.equals("rtClickToContactAndSubject_ShowOriginal"))
				|| (test.equals("rtClickToContactAndSubject_NewFilter"))
				|| (test.equals("rtClickToContactAndSubject_CreateAppt"))
				|| (test.equals("rtClickToContactAndSubject_CreateTask"))) {

			return new Object[][] { { commonAccount, "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
		} else if (test.equals("rtClickToContactAndSubject_MarkAsReadUnread")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "subjectmarkasreadunread",
					"bodymarkasreadunread", "" } };
		} else if (test.equals("rtClickToContactAndSubject_Delete")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "subjectdelete", "bodydelete", "" } };
		} else if (test.equals("rtClickToContactAndSubject_Move")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "subjectmove", "bodymove", "" } };
		} else if (test.equals("rtClickToContactAndSubject_Junk")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "subjectjunk", "bodyjunk", "" } };
		} else {
			return new Object[][] { { commonAccount, "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zMailApp.zNavigateToMailApp();
		isExecutionARetry = false;
		randomAcctCreatedFlag = false;
		i = 0;
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
	 * This test verifies all right click menu items
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAllRightClickMenusExists(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnContact(fromUserName);
		String[] contactMenuItemArray = { page.zMailApp.zSearchMenuIconBtn,
				page.zMailApp.zAdvancedSearchMenuIconBtn,
				page.zMailApp.zNewEmailMenuIconBtn,
				page.zMailApp.zAddToContactsMenuIconBtn,
				page.zMailApp.zMarkReadMenuIconBtn,
				page.zMailApp.zMarkUnReadMenuIconBtn,
				page.zMailApp.zReplyMenuIconBtn,
				page.zMailApp.zReplyAllMenuIconBtn,
				page.zMailApp.zForwardMenuIconBtn,
				page.zMailApp.zEditAsNewMenuIconBtn,
				page.zMailApp.zTagMenuIconBtn, page.zMailApp.zDeleteIconBtn,
				page.zMailApp.zMoveMenuIconBtn, page.zMailApp.zPrintIconBtn,
				page.zMailApp.zJunkMenuIconBtn,
				page.zMailApp.zShowOriginalMenuIconBtn,
				page.zMailApp.zNewFilterMenuIconBtn,
				page.zMailApp.zCreateApptMenuIconBtn,
				page.zMailApp.zCreateTaskMenuEnaDisaBtn };

		for (int i = 0; i <= 18; i++) {
			obj.zMenuItem.zExists(contactMenuItemArray[i]);
		}

		rightClickOnSubject(subject);
		String[] subjectMenuItemArray = { page.zMailApp.zMarkReadMenuIconBtn,
				page.zMailApp.zMarkUnReadMenuIconBtn,
				page.zMailApp.zReplyMenuIconBtn,
				page.zMailApp.zReplyAllMenuIconBtn,
				page.zMailApp.zForwardMenuIconBtn,
				page.zMailApp.zEditAsNewMenuIconBtn,
				page.zMailApp.zTagMenuIconBtn, page.zMailApp.zDeleteIconBtn,
				page.zMailApp.zMoveMenuIconBtn, page.zMailApp.zPrintIconBtn,
				page.zMailApp.zJunkMenuIconBtn,
				page.zMailApp.zShowOriginalMenuIconBtn,
				page.zMailApp.zNewFilterMenuIconBtn,
				page.zMailApp.zCreateApptMenuIconBtn,
				page.zMailApp.zCreateTaskMenuEnaDisaBtn };
		for (int i = 0; i <= 14; i++) {
			obj.zMenuItem.zExists(subjectMenuItemArray[i]);
		}

		needReset = false;
	}

	/**
	 * This test verifies basic search functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContact_Search(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Search
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);

		String newSubject = "searchsubject";
		String newBody = "searchsubject";
		to = SelNGBase.selfAccountName;
		String[] recipients = { to };

		ProvZCS.injectMessage(commonAccount, recipients, cc, newSubject,
				newBody);

		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		rightClickOnSubject(newSubject);
		obj.zMenuItem.zClick(localize(locator.move));
		obj.zDialog.zExists(localize(locator.moveMessage));
		obj.zFolder.zClickInDlgByName(
				replaceUserNameInStaticId(page.zMailApp.zSentFldrMoveDlg),
				localize(locator.moveMessage));
		Thread.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveMessage));
		Thread.sleep(2000);

		// this method is just to discard dialog
		zPressBtnIfDlgExists(localize(locator.moveMessage),
				localize(locator.ok),
				replaceUserNameInStaticId(page.zMailApp.zSentFldrMoveDlg));

		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zSentFldr));
		obj.zMessageItem.zExists(newSubject);

		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		rightClickOnContact(fromUserName);
		obj.zMenuItem.zClick(page.zMailApp.zSearchMenuIconBtn);
		obj.zMessageItem.zExists(newSubject);
		obj.zMessageItem.zExists(subject);

		// Deleting search mail
		obj.zMessageItem.zRtClick(newSubject);
		obj.zMenuItem.zClick(page.zMailApp.zDeleteIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies advanced search functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContact_AdvancedSearch(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Advanced search
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);

		String newSubject = "advancedsearchsubject";
		String newBody = "advancedsearchbody";
		to = SelNGBase.selfAccountName;
		String[] recipients = { to };

		ProvZCS.injectMessage(commonAccount, recipients, cc, newSubject,
				newBody);

		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		rightClickOnContact(fromUserName);
		obj.zMenuItem.zClick(page.zMailApp.zAdvancedSearchMenuIconBtn);
		Thread.sleep(1000); /*
							 * selenium suddenly changes UI and starts executing
							 * next statements
							 */
		obj.zMessageItem.zExists(subject);
		obj.zMessageItem.zExists(newSubject);
		if (!config.getString("locale").equals("zh_CN")
				&& !config.getString("locale").equals("zh_HK")
				&& !config.getString("locale").equals("fr")
				&& !config.getString("locale").equals("hi")
				&& !config.getString("locale").equals("ja")) {
			String FromValue = obj.zEditField
					.zGetInnerText(localize(locator.fromLabel));
			assertReport(from, FromValue,
					"From user text mismatched in advanced search");
		}

		if (config.getString("locale").equals("ru")
				|| config.getString("locale").equals("pl")
				|| config.getString("locale").equals("sv")
				|| config.getString("locale").equals("ko")) {
			obj.zButton.zClick(localize(locator.close));
		} else {
			obj.zButton.zClick(localize(locator.advanced));
		}

		// Deleting advanced search mail
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		obj.zMessageItem.zRtClick(newSubject);
		obj.zMenuItem.zClick(page.zMailApp.zDeleteIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies new email functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContact_NewEmail(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// New email
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnContact(fromUserName);
		obj.zMenuItem.zClick(page.zMailApp.zNewEmailMenuIconBtn);
		String toValue = obj.zTextAreaField
				.zGetInnerText(localize(locator.toLabel));
		assertReport(
				toValue,
				from,
				"To edit field value mismatched while add contact from contact right click menu");
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies add contact & edit contact functionality from right
	 * click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContact_AddEditContact(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Add contact & edit contact both
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnContact(fromUserName);
		obj.zMenuItem.zClick(page.zMailApp.zAddToContactsMenuIconBtn);
		zWaitTillObjectExist("editfield", page.zABCompose.zFirstEditField);
		String firstNameValue = obj.zEditField
				.zGetInnerText(page.zABCompose.zFirstEditField);
		String emailValue;
		if (config.getString("locale").equals("ja")) {
			emailValue = obj.zEditField
					.zGetInnerText(page.zABCompose.zEmail1EditField + ":");
		} else {
			emailValue = obj.zEditField
					.zGetInnerText(page.zABCompose.zEmail1EditField);
		}
		assertReport(
				fromUserName,
				firstNameValue,
				"First name edit field value mismatched while add contact from contact right click menu");
		assertReport(
				from,
				emailValue,
				"Email edit field value mismatched while add contact from contact right click menu");

		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		zGoToApplication("Address Book");
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		obj.zContactListItem.zExists(fromUserName);
		zGoToApplication("Mail");
		rightClickOnContact(fromUserName);
		obj.zMenuItem.zClick(page.zMailApp.zAddToContactsMenuIconBtn);
		assertReport(
				fromUserName,
				firstNameValue,
				"First name edit field value mismatched edit add contact from contact right click menu");
		assertReport(
				from,
				emailValue,
				"Email edit field value mismatched while edit contact from contact right click menu");
		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		zGoToApplication("Mail");

		needReset = false;
	}

	/**
	 * This test verifies mail mark as read - unread functionality from right
	 * click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_MarkAsReadUnread(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Mark as read
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zIsEnabled(page.zMailApp.zMarkReadMenuIconBtn);
		obj.zMenuItem.zIsDisabled(page.zMailApp.zMarkUnReadMenuEnaDisaBtn);
		obj.zMenuItem.zClick(page.zMailApp.zMarkReadMenuIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		rightClickOnSubject(subject);
		obj.zMenuItem.zIsDisabled(page.zMailApp.zMarkReadMenuEnaDisaBtn);
		obj.zMenuItem.zIsEnabled(page.zMailApp.zMarkUnReadMenuEnaDisaBtn);

		// Mark as unread
		obj.zMenuItem.zClick(page.zMailApp.zMarkReadMenuIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		rightClickOnSubject(subject);
		obj.zMenuItem.zIsDisabled(page.zMailApp.zMarkReadMenuEnaDisaBtn);
		obj.zMenuItem.zIsEnabled(page.zMailApp.zMarkUnReadMenuIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies mail reply functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_Reply(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Reply
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		clickMenuItemIfExists(subject, page.zMailApp.zReplyMenuIconBtn);
		Thread.sleep(2000); /*
							 * Continuously test failing over here because it
							 * tries to verify subject field before mail compose
							 * UI appears
							 */
		String toValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zToField);
		String ccValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zCcField);
		String subjectValue = obj.zEditField
				.zGetInnerText(page.zComposeView.zSubjectField);
		Thread.sleep(2000);
		String bodyValue = obj.zEditor.zGetInnerText(body);
		assertReport(
				toValue,
				from,
				"To user text area field value mismatched while do reply from mail right click menu");
		Assert
				.assertNotSame(
						ccValue,
						"",
						"Cc user text area field value mismatched while do reply from mail right click menu");
		assertReport("Re: " + subject, subjectValue,
				"Subject edit field value mismatched while do reply from mail right click menu");
		verifyCurrentMsgBody(bodyValue, from, to, cc, bcc, subject, body);
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies mail reply all functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_ReplytoAll(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Reply to all
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		clickMenuItemIfExists(subject, page.zMailApp.zReplyAllMenuIconBtn);
		Thread.sleep(2000); /*
							 * Continuously test failing over here because it
							 * tries to verify subject field before mail compose
							 * UI appears
							 */
		String toValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zToField);
		String ccValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zCcField);
		String subjectValue = obj.zEditField
				.zGetInnerText(page.zComposeView.zSubjectField);
		Thread.sleep(2000);
		String bodyValue = obj.zEditor.zGetInnerText(body);
		assertReport(
				toValue,
				from,
				"To user text area field value mismatched while do reply all from mail right click menu");

		assertReport(
				ccValue,
				cc,
				"Cc user text area field value mismatched while do reply all from mail right click menu");
		assertReport(
				"Re: " + subject,
				subjectValue,
				"Subject edit field value mismatched while do reply all from mail right click menu");
		verifyCurrentMsgBody(bodyValue, from, to, cc, bcc, subject, body);
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies mail forward functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_Forward(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Forward
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		clickMenuItemIfExists(subject, page.zMailApp.zForwardMenuIconBtn);
		Thread.sleep(2000); /*
							 * Continuously test failing over here because it
							 * tries to verify subject field before mail compose
							 * UI appears
							 */
		String toValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zToField);
		String ccValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zCcField);
		String subjectValue = obj.zEditField
				.zGetInnerText(page.zComposeView.zSubjectField);
		Thread.sleep(2000);
		String bodyValue = obj.zEditor.zGetInnerText(body);
		Assert
				.assertNotSame(
						toValue,
						"",
						"To user text area field value mismatched while do forward from mail right click menu");
		Assert
				.assertNotSame(
						ccValue,
						"",
						"Cc user text area field value mismatched while do forward from mail right click menu");
		assertReport(
				"Fwd: " + subject,
				subjectValue,
				"Subject edit field value mismatched while do forward from mail right click menu");
		// verifyCurrentMsgBody(bodyValue, from, to, cc, bcc, subject, body);

		assertReport(bodyValue, localize(locator.forwardedMessage),
				"Body value mismatched in mail body");
		assertReport(bodyValue, body, "Body value mismatched in mail body");

		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies edit as new mail functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_EditasNew(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Edit as new
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		clickMenuItemIfExists(subject, page.zMailApp.zEditAsNewMenuIconBtn);
		Thread.sleep(2000); /*
							 * continously test failing over here because it
							 * tries to verify subject field before mail compose
							 * UI appears
							 */
		String toValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zToField);
		String ccValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zCcField);
		String subjectValue = obj.zEditField
				.zGetInnerText(page.zComposeView.zSubjectField);
		Thread.sleep(2000);
		String bodyValue = obj.zEditor.zGetInnerText(body);
		assertReport(
				toValue,
				to,
				"From user text area field value mismatched while do edit as new from mail right click menu");

		assertReport(
				ccValue,
				cc,
				"Cc user text area field value mismatched while do edit as new from mail right click menu");
		assertReport(
				subject,
				subjectValue,
				"Subject edit field value mismatched while do edit as new from mail right click menu");
		assertReport(
				bodyValue,
				body,
				"Body editor field value mismatched while do edit as new from mail right click menu");
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies mail tagging functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_TagMessage(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Tag message
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zMouseOver(page.zMailApp.zTagMenuIconBtn);
		obj.zMenuItem.zClick(page.zMailApp.zNewTagMenuIconBtn);
		obj.zDialog.zExists(localize(locator.createNewTag));
		obj.zEditField.zTypeInDlgByName(localize(locator.tagName),
				"TagFromMail", localize(locator.createNewTag));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewTag));
		rightClickOnContact(fromUserName);
		obj.zMenuItem.zMouseOver(page.zMailApp.zTagMenuIconBtn);
		obj.zMenuItem.zNotExists("TagFromMail");
		obj.zMenuItem.zClick(page.zMailApp.zRemoveTagMenuIconBtn);

		needReset = false;
	}

	/**
	 * This test verifies mail delete functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_Delete(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Delete & permanent delete
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zDeleteMenuIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr)); /*
																				 * this
																				 * is
																				 * not
																				 * required
																				 * but
																				 * selenium
																				 * does
																				 * everything
																				 * so
																				 * fast
																				 * ,
																				 * so
																				 * it
																				 * fails
																				 * over
																				 * here
																				 * .
																				 * To
																				 * make
																				 * it
																				 * more
																				 * reliable
																				 * clicking
																				 * to
																				 * inbox
																				 * before
																				 * verify
																				 * actual
																				 * junk
																				 * mail
																				 */
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		obj.zMessageItem.zExists(subject);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zDeleteMenuIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		obj.zMessageItem.zNotExists(subject);

		needReset = false;
	}

	/**
	 * This test verifies mail move functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_Move_Bug39558(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Move
		String newFolder = getLocalizedData_NoSpecialChar();
		page.zMailApp.zCreateFolder(newFolder);
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zMoveMenuIconBtn);
		obj.zFolder.zClickInDlgByName(newFolder, localize(locator.moveMessage));
		Thread.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveMessage));
		Thread.sleep(2000);
		zPressBtnIfDlgExists(localize(locator.moveMessage),
				localize(locator.ok), newFolder);
		obj.zFolder.zClick(newFolder);
		obj.zMessageItem.zExists(subject);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		Thread.sleep(1500);
		obj.zMessageItem.zNotExists(subject);

		needReset = false;
	}

	/**
	 * This test verifies mail print functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_Print(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Print
		// fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
		// attachments);
		// rightClickOnContact(fromUserName);
		// obj.zMenuItem.zClick(localize(locator.print));

		needReset = false;
	}

	/**
	 * This test verifies mail junk functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_Junk_And_Bug42845(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Junk
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zJunkMenuIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zJunkFldr));
		obj.zMessageItem.zExists(subject);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zJunkMenuIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zJunkFldr));
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));

		// for bug 42845
		obj.zMessageItem.zDblClick(subject);
		obj.zButton.zClick(page.zMailApp.zJunkIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zJunkFldr));
		obj.zMessageItem.zDblClick(subject);
		obj.zButton.zClick(page.zMailApp.zJunkIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zJunkFldr));
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	/**
	 * This test verifies mail show original functionality from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_ShowOriginal(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Show original
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zShowOriginalMenuIconBtn);
		Thread.sleep(4000); // failed because of timing issue
		selenium.selectWindow("_blank");
		showOrigText = selenium.getBodyText();
		Thread.sleep(1000);
		verifyShowOriginalMsgBody(showOrigText, from, to, cc, bcc, subject,
				body);
		if (config.getString("locale").equals("en_US")) {
			assertReport(showOrigText, localize(locator.received),
					"Received: text mismatched in show original body");
		}
		selenium.selectWindow(null);

		needReset = false;
	}

	/**
	 * This test creates filter from right click menu - new filter
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_NewFilter(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// New filter
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zNewFilterMenuIconBtn);
		obj.zDialog.zExists(localize(locator.editFilter));
		if (config.getString("locale").equals("fr")
				&& config.getString("browser").equals("IE")) {
			obj.zEditField.zTypeInDlg(localize(locator.filterName),
					"FilterFromMail");
		} else {
			obj.zEditField.zTypeInDlg(localize(locator.filterNameLabel),
					"FilterFromMail");
		}

		Thread.sleep(4000); /*
							 * selenium immidiatly tries to verify values before
							 * edit filter dialog appears so putting some wait
							 */

		// Verifying default filter values
		SelNGBase.fieldLabelIsAnObject = true;
		String fromValue = obj.zEditField.zGetInnerTextInDlgByName(
				localize(locator.contains), localize(locator.editFilter), "1");
		String ccValue = obj.zEditField.zGetInnerTextInDlgByName(
				localize(locator.contains), localize(locator.editFilter), "2");
		String subjectValue = obj.zEditField
				.zGetInnerTextInDlgByName(localize(locator.exactMatch),
						localize(locator.editFilter), "2");
		SelNGBase.fieldLabelIsAnObject = false;
		Assert
				.assertNotSame(
						fromValue,
						from,
						"From user default value mismatched while create filter from mail right click menu");
		Assert
				.assertNotSame(
						ccValue,
						cc,
						"Cc user default value mismatched while create filter from mail right click menu");
		Assert
				.assertNotSame(
						subjectValue,
						subject,
						"Subject default value mismatched while create filter from mail right click menu");
		if (config.getString("locale").equals("hi")) {
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.editFilter), "2");
		} else {
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.editFilter));
		}
		Thread.sleep(5000); /*
							 * selenium immediately goes to preferences and
							 * verifies created filter but filter doesn't exist
							 * there & it fails - not able to reproduce every
							 * time
							 */
		String filterMessage = obj.zToastAlertMessage.zGetMsg();
		Assert
				.assertNotSame(
						filterMessage,
						localize(locator.filtersSaved),
						"Filter action related toast message not shown properly after creating filter from mail");
		zGoToApplication("Preferences");
		zGoToPreferences("Filters");
		obj.zButton.zClick(localize(locator.filterRemove));
		Thread.sleep(1000);
		filterMessage = obj.zToastAlertMessage.zGetMsg();
		Assert
				.assertNotSame(filterMessage, localize(locator.filtersSaved),
						"Filter action related toast message not shown properly after deleting filter");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		zGoToApplication("Mail");

		needReset = false;
	}

	/**
	 * This test creates appointment from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_CreateAppt_Bug39954(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Create appointment
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zCreateApptMenuIconBtn);
		zWaitTillObjectExist("button", page.zCalCompose.zApptSaveBtn);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		Thread.sleep(2000);
		zGoToApplication("Calendar");
		obj.zAppointment.zExists(subject);
		zGoToApplication("Mail");

		needReset = false;
	}

	/**
	 * This test creates task from right click menu
	 */
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickToContactAndSubject_CreateTask_Bug39954(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Create task
		fromUserName = commonInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		rightClickOnSubject(subject);
		obj.zMenuItem.zClick(page.zMailApp.zCreateTaskMenuEnaDisaBtn);
		zWaitTillObjectExist("button", page.zTaskApp.zTasksSaveBtn);
		obj.zButton.zClick(page.zTaskApp.zTasksSaveBtn);
		zGoToApplication("Tasks");
		obj.zTaskItem.zExists(subject);
		zGoToApplication("Mail");

		needReset = false;
	}

	private String commonInjectMessage(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		to = SelNGBase.selfAccountName;
		String[] recipients = { to };
		if (from.equals(commonAccount) && randomAcctCreatedFlag == false) {
			ProvZCS.injectMessage(from, recipients, cc, subject, body);
			MailApp.ClickCheckMailUntilMailShowsUp(
					replaceUserNameInStaticId(page.zMailApp.zInboxFldr),
					subject);
			randomAcctCreatedFlag = true;
		} else if ((!from.equals(commonAccount))
				&& (randomAcctCreatedFlag == true || randomAcctCreatedFlag == false)) {
			ProvZCS.injectMessage(from, recipients, cc, subject, body);
			MailApp.ClickCheckMailUntilMailShowsUp(
					replaceUserNameInStaticId(page.zMailApp.zInboxFldr),
					subject);
		}
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		return from.split("@")[0];
	}

	private void rightClickOnContact(String fromUserName) throws Exception {
		SelNGBase.actOnLabel = true;
		Thread.sleep(500);
		obj.zMessageItem.zRtClick(fromUserName);
		Thread.sleep(500);
		SelNGBase.actOnLabel = false;
	}

	private void rightClickOnSubject(String subject) throws Exception {
		Thread.sleep(500);
		obj.zMessageItem.zRtClick(subject);
		Thread.sleep(500);
	}

	private void verifyCurrentMsgBody(String bodyValue, String from, String to,
			String cc, String bcc, String subject, String body)
			throws Exception {
		Thread.sleep(2000); // failed because of timing issue

		// due to composing preference change, below check fails
		// if (config.getString("locale").equals("en_US")) {
		// assertReport(bodyValue, localize(locator.fromLabel),
		// "From: text mismatched in mail body");
		// assertReport(bodyValue, localize(locator.toLabel),
		// "To: text mismatched in mail body");
		// assertReport(bodyValue, localize(locator.ccLabel),
		// "Cc: text mismatched in mail body");
		// assertReport(bodyValue, localize(locator.subjectLabel),
		// "Subject: text mismatched in mail body");
		// assertReport(bodyValue, localize(locator.sent),
		// "Sent: text mismatched in mail body");
		// }

		// due to composing preference change, below check fails
		// assertReport(bodyValue, from,
		// "From user value mismatched in mail body");
		// assertReport(bodyValue, to, "To user value mismatched in mail body");
		// assertReport(bodyValue, cc,
		// "Cc user value is mismatched in mail body");
		// assertReport(bodyValue, subject,
		// "Subject value mismatched in mail body");
		assertReport(bodyValue, localize(locator.origMsg),
				"Body value mismatched in mail body");
		assertReport(bodyValue, body, "Body value mismatched in mail body");
	}

	private void clickMenuItemIfExists(String subject, String menuItem)
			throws Exception {
		int i = 0;
		boolean found = false;
		for (i = 0; i <= 10; i++) {
			String rc = obj.zMenuItem.zExistsDontWait(menuItem);
			if (rc.equals("false")) {
				rightClickOnSubject(subject);
				Thread.sleep(2000);
			} else {
				obj.zMenuItem.zClick(menuItem);
				found = true;
				break;
			}
		}
		if (!found)
			Assert.fail("Menu item(" + menuItem
					+ ") not exist while do right click on mail");
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