package projects.zcs.tests.features;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.CalApp;

/**
 * Class contains client URL tests (applicationTab, skin, composeMail &
 * composeAppt, mail compose (with and without login))
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class ClientURLTests extends CommonTest {
	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
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

	// Tests
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void customLoginToCalendarApp() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zKillBrowsers();
		page.zLoginpage.zCustomLoginToZimbraAjax("?app=calendar");
		obj.zFolder.zExists(localize(locator.calendar));
		obj.zButton.zNotExists(localize(locator.newFolder));
		obj.zButton.zNotExists(page.zMailApp.zGetMailIconBtn);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void appTabURL() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Address book
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=contacts");
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=contacts");
		obj.zFolder.zExists(page.zABCompose.zContactsFolder);
		obj.zButton.zExists(page.zABCompose.zNewABOverviewPaneIcon);
		obj.zButton.zExists(page.zABCompose.zNewContactMenuIconBtn);

		// Calendar
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=calendar");
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=calendar");
		obj.zFolder.zExists(page.zCalApp.zCalendarFolder);
		obj.zButton.zExists(page.zCalApp.zNewCalOverviewPaneIcon);
		obj.zButton.zExists(page.zCalApp.zCalNewApptBtn);

		// Tasks
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=tasks");
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=tasks");
		obj.zFolder.zExists(page.zTaskApp.zTasksFolder);
		obj.zButton.zExists(page.zTaskApp.zNewTasksOverviewPaneIcon);
		obj.zButton.zExists(page.zTaskApp.zTasksNewBtn);

		// Documents
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=documents");
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=documents");
		obj.zFolder.zExists(page.zDocumentApp.zNotebookFolder);
		obj.zButton.zExists(page.zDocumentApp.zNewNotebookOverviewPaneIcon);
		obj.zButton.zExists(page.zDocumentCompose.zNewPageIconBtn);

		// Briefcase
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=briefcase");
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=briefcase");
		obj.zFolder.zExists(page.zBriefcaseApp.zBriefcaseFolder);
		obj.zButton.zExists("id=ztih__main_Briefcase__BRIEFCASE_textCell");
		obj.zButton.zExists(page.zBriefcaseApp.zNewMenuIconBtn);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composeURL() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String toField = ProvZCS.getRandomAccount();
		String subjectField;
		String bodyField;
		if (config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("ko")
				|| config.getString("locale").equals("zh_HK")
				|| config.getString("locale").equals("ja")
				|| config.getString("locale").equals("ru")) {
			subjectField = "testSubject";
			bodyField = "testBody";
		} else {
			subjectField = getLocalizedData_NoSpecialChar();
			bodyField = getLocalizedData_NoSpecialChar();
		}
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?view=compose" + "&to="
				+ toField + "&subject=" + subjectField + "&body=" + bodyField);
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server") + "/?view=compose" + "&to="
				+ toField + "&subject=" + subjectField + "&body=" + bodyField);
		obj.zButton.zExists(page.zComposeView.zSendIconBtn);
		String toValue = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zToField);
		String subjectValue = obj.zEditField
				.zGetInnerText(page.zComposeView.zSubjectField);
		String bodyValue = obj.zEditor.zGetInnerText(bodyField).trim();
		assertReport(
				toField,
				toValue,
				"To text area field value mismatched while directly type client URL to compose a mail");
		assertReport(
				subjectField,
				subjectValue,
				"Subject edit field value mismatched while directly type client URL to compose a mail");
		assertReport(bodyField, bodyValue,
				"Body editor value mismatched while directly type client URL to compose a mail");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composeURLWithSpecialCharacter_Bug44332() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zKillBrowsers();
		page.zLoginpage
				.zCustomLoginToZimbraAjax("mail?view=compose&to=foo@example.com&subject=���&body=body");
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server")
				+ "/mail?view=compose&to=foo@example.com&subject=���&body=body");
		obj.zButton.zExists(page.zComposeView.zSendIconBtn);
		assertReport(
				"foo@example.com",
				obj.zTextAreaField.zGetInnerText(page.zComposeView.zToField),
				"To text area field value mismatched while directly type client URL to compose a mail");
		assertReport(
				"?",
				obj.zEditField.zGetInnerText(page.zComposeView.zSubjectField),
				"Subject edit field value mismatched while directly type client URL to compose a mail");
		assertReport("body", obj.zEditor.zGetInnerText("").trim(),
				"Body editor value mismatched while directly type client URL to compose a mail");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void calendarViewURL() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// day|workWeek|week|month
		String startDate = "20090310";
		String expectedDate = null;
		if (config.getString("locale").equals("zh_CN")) {
			expectedDate = "2009/3/10";
		} else if (config.getString("locale").equals("zh_HK")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("es")
				|| config.getString("locale").equals("pt_BR")
				|| config.getString("locale").equals("it")
				|| config.getString("locale").equals("hi")) {
			expectedDate = "10/3/2009";
		} else if (config.getString("locale").equals("ja")) {
			expectedDate = "2009/3/10";
		} else if (config.getString("locale").equals("nl")) {
			expectedDate = "10-3-2009";
		} else if (config.getString("locale").equals("fr")) {
			expectedDate = "10/3/2009";
		} else if (config.getString("locale").equals("ru")
				|| config.getString("locale").equals("de")) {
			expectedDate = "10.3.2009";
		} else if (config.getString("locale").equals("pl")) {
			expectedDate = "2009-3-10";
		} else if (config.getString("locale").equals("da")) {
			expectedDate = "10/3/2009";
		} else if (config.getString("locale").equals("ar")) {
			expectedDate = "200/3/10";
		} else if (config.getString("locale").equals("en_US")) {
			expectedDate = "3/10/2009";
		} else if (config.getString("locale").equals("sv")) {
			expectedDate = "2009-3-10";
		}
		String calView = "workWeek";
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=calendar&view=" + calView
				+ "&date=" + startDate);
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=calendar&view=" + calView
				+ "&date=" + startDate);
		obj.zFolder.zExists(page.zCalApp.zCalendarFolder);
		obj.zButton.zExists(CalApp.zViewBtn);
		page.zCalApp.zNavigateToApptCompose();
		String actualDate;
		actualDate = obj.zEditField.zGetInnerText("id=startDateField*");
		if (!config.getString("locale").equals("ar")) {
			assertReport(
					expectedDate,
					actualDate,
					"Start date edit field value mismatched while directly type client URL to create appointment");
		}

		obj.zButton.zClick(page.zCalCompose.zApptCancelBtn);
		boolean found = false;
		for (int i = 1; i <= 10; i++) {
			String retVal = null;
			retVal = obj.zDialog.zExistsDontWait(localize(locator.warningMsg));
			System.out.println(retVal);
			if (retVal.equals("false")) {
				Thread.sleep(1000);
			} else {
				found = true;
				break;
			}
		}
		if (found) {
			obj.zButton.zClickInDlgByName(localize(locator.no),
					localize(locator.warningMsg));
		}

		needReset = false;
	}

	public static String getTimeMenuLocation(String val) {
		String str = localize(locator.formatDateShort);
		if (str.indexOf("H") >= 0)
			return getLocation24(val);
		else
			return getLocation12(val);
	}

	private static String getLocation12(String val) {
		String str = localize(locator.formatDateShort);
		String[] str1 = str.split(" ");
		String one = "";
		String two = "";
		String three = "";
		if (str1[0].indexOf(":") > 0) {
			String[] str2 = str1[0].split(":");
			one = str2[0];
			two = str2[1];
			three = str1[1];
		} else if (str1[1].indexOf(":") > 0) {
			String[] str2 = str1[1].split(":");
			two = str2[0];
			three = str2[1];
			one = str1[0];
		}
		if (one.toLowerCase().indexOf(val) >= 0)
			return "1";
		else if (two.toLowerCase().indexOf(val) >= 0)
			return "2";
		else if (three.toLowerCase().indexOf(val) >= 0)
			return "3";

		// something has gone wrong
		return "-1";
	}

	private static String getLocation24(String val) {
		String str = localize(locator.formatTimeShort);
		String[] str1 = str.split(":");
		String one = str1[0];
		String two = str1[1];
		if (one.toLowerCase().indexOf(val) >= 0)
			return "1";
		else if (two.toLowerCase().indexOf(val) >= 0)
			return "2";

		// something has gone wrong
		return "-1";
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
