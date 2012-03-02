package projects.zcs.tests.calendar.tags;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class TagAppointmentTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagForApptAndVerify_ListView")
				|| test
						.equals("createRenameDeleteTagForApptAndVerifyInAll6View")
				|| test
						.equals("verifyTagFunctionalityFor2ApptAndRemoveTag_ListView")
				|| test.equals("applyMutlipleTagToApptAndVerify_ListView")
				|| test.equals("applyTagByDnDTagToApptAndViceVersa_ListView")
				|| test.equals("tryToCreateDuplicateTagInCalendar")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Calendar");
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

	/**
	 * Verify create, rename & delete functionality for tag for appointments
	 * (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForApptAndVerify_ListView(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, newTag1;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		page.zCalCompose.zCreateSimpleAppt(subject);
		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zListItem.zClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);
		Thread.sleep(1000);

		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);

		zDeleteTag(newTag1);
		obj.zListItem.zClick(subject);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Verify create, rename & delete functionality for tag for appointments
	 * (all 6 view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForApptAndVerifyInAll6View(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, newTag1;
		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewMonth));
		page.zCalCompose.zCreateSimpleAppt(subject);

		// Verify tagged appointment in all 6 view
		zCreateTag(tag1);
		zClickApptInMonthView(subject);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		zRtClickApptInDiffView(subject, "month");
		obj.zMenuItem.zIsEnabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(tag1);
		// right now there is no way to verify tagged appt. in month view - bug
		// 30645

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewDay));
		Thread.sleep(1000);
		zRtClickApptInDiffView(subject, "day");
		obj.zMenuItem.zIsEnabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(tag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewWorkWeek));
		Thread.sleep(1000);
		zRtClickApptInDiffView(subject, "workweek");
		obj.zMenuItem.zIsEnabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(tag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewWeek));
		Thread.sleep(1000);
		zRtClickApptInDiffView(subject, "week");
		obj.zMenuItem.zIsEnabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(tag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewSchedule));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);

		// Verify renamed tagged appointment in all 6 view
		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		Thread.sleep(1000);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewDay));
		Thread.sleep(1000);
		zRtClickApptInDiffView(subject, "day");
		obj.zMenuItem.zIsEnabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(newTag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewWorkWeek));
		Thread.sleep(1000);
		zRtClickApptInDiffView(subject, "workweek");
		obj.zMenuItem.zIsEnabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(newTag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewWeek));
		Thread.sleep(1000);
		zRtClickApptInDiffView(subject, "week");
		obj.zMenuItem.zIsEnabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(newTag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewSchedule));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);

		// Verify tag is deleted from appointment in all 6 view
		zDeleteTag(newTag1);
		Thread.sleep(1000);

		selenium
				.clickAt(
						"//div[contains(@id,'zli__CLS')]//td[contains(@class,'_name') and contains(text(), '"
								+ subject + "')]", "");
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		obj.zListItem.zVerifyIsNotTagged(subject);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewDay));
		zClickApptInDiffView(subject, "day");
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		zRtClickApptInDiffView(subject, "day");
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(newTag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewWorkWeek));
		zClickApptInDiffView(subject, "workweek");
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		zRtClickApptInDiffView(subject, "workweek");
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(newTag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewWeek));
		zClickApptInDiffView(subject, "week");
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		zRtClickApptInDiffView(subject, "week");
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(newTag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.viewMonth));
		zClickApptInDiffView(subject, "month");
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		zRtClickApptInDiffView(subject, "month");
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		obj.zMenuItem.zNotExists(newTag1);

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		Thread.sleep(1000);
		selenium.clickAt(
				"//td[contains(@id,'zlif__CLL') and contains(text(), '"
						+ subject + "')]", "");
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));
		obj.zListItem.zVerifyIsNotTagged(subject);

		needReset = false;
	}

	/**
	 * Create 2 tag, apply 1 tag to each appointment and verify appointment
	 * exist / not exist by clicking to tag (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2ApptAndRemoveTag_ListView(
			String subject) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject2, tag1, tag2;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		subject2 = getLocalizedData_NoSpecialChar();
		page.zCalCompose.zCreateSimpleAppt(subject);
		page.zCalCompose.zCreateSimpleAppt(subject2);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zListItem.zClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);
		Thread.sleep(1000);
		obj.zListItem.zClick(subject2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject);
		assertReport("false", obj.zListItem.zExistsDontWait(subject2),
				"Verify contact2 not exists");
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject2);
		assertReport("false", obj.zListItem.zExistsDontWait(subject),
				"Verify contact1 not exists");
		obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
		Thread.sleep(1000);
		assertReport("true", obj.zListItem.zExistsDontWait(subject),
				"Verify appointment1 not exists");
		assertReport("true", obj.zListItem.zExistsDontWait(subject2),
				"Verify appointment2 not exists");
		obj.zListItem.zClick(subject2);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		Thread.sleep(1000);
		obj.zListItem.zClick(subject2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Create 2 tag, apply both tag to appointment and verify both appointment
	 * after clicking to tag exists (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyMutlipleTagToApptAndVerify_ListView(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, tag2;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		page.zCalCompose.zCreateSimpleAppt(subject);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		obj.zListItem.zClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag1);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);
		obj.zListItem.zClick(subject);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject);

		needReset = false;
	}

	/**
	 * Verify drag n drop functionality for tag and appointment. Drag
	 * appointment to tag and verify tag applied & same way drag tag to
	 * appointment and verify tag applied (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyTagByDnDTagToApptAndViceVersa_ListView(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject2, tag1, tag2;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		subject2 = getLocalizedData_NoSpecialChar();
		page.zCalCompose.zCreateSimpleAppt(subject);
		page.zCalCompose.zCreateSimpleAppt(subject2);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zCreateTag(tag2);
		zDragAndDrop("//div[contains(@id, 'zli__CLL')]//td[contains(text(), '"
				+ subject + "')]",
				"//td[contains(@id, 'zti__main_Calendar') and contains(text(), '"
						+ tag1 + "')]");
		obj.zListItem.zVerifyIsTagged(subject);

		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject);
		obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Calendar') and contains(text(), '"
						+ tag2 + "')]",
				"//div[contains(@id, 'zli__CLL')]//td[contains(text(), '"
						+ subject2 + "')]");
		obj.zListItem.zVerifyIsTagged(subject2);
		Thread.sleep(1000);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject2);
		assertReport("false", obj.zListItem.zExistsDontWait(subject),
				"Verify appointment1 not exists");

		needReset = false;
	}

	/**
	 * Try to create duplicate tag and verify its not allowed (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateTagInCalendar(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1;
		tag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zDuplicateTag(tag1);

		needReset = false;
	}

	public static void zClickApptInMonthView(String subject) throws Exception {
		Thread.sleep(1000);
		selenium
				.clickAt(
						"xpath=//td[contains(@class, 'calendar_month_day_item')]//div[contains(text(), "
								+ subject + ")]", "");
	}

	public static void zRtClickApptInDiffView(String subject, String view)
			throws Exception {
		Thread.sleep(1000);
		if (view.toLowerCase().equals("day")) {
			selenium
					.mouseDownRight("xpath=//div[contains(@id, 'zli__CLD')]//td[contains(@class, '_name') and contains(text(), "
							+ subject + ")]");
		} else if (view.toLowerCase().equals("workweek")) {
			selenium
					.mouseDownRight("xpath=//div[contains(@id, 'zli__CLWW')]//td[contains(@class, '_name') and contains(text(), "
							+ subject + ")]");
		} else if (view.toLowerCase().equals("week")) {
			selenium
					.mouseDownRight("xpath=//div[contains(@id, 'zli__CLW')]//td[contains(@class, '_name') and contains(text(), "
							+ subject + ")]");
		} else if (view.toLowerCase().equals("month")) {
			selenium
					.mouseDownRight("xpath=//td[contains(@class, 'calendar_month_day_item')]//div[contains(text(), "
							+ subject + ")]");
		}
		obj.zMenuItem.zMouseOver(localize(locator.tagAppt));
		Thread.sleep(1000);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
	}

	public static void zClickApptInDiffView(String subject, String view)
			throws Exception {
		Thread.sleep(1000);
		if (view.toLowerCase().equals("day")) {
			selenium
					.clickAt(
							"xpath=//div[contains(@id, 'zli__CLD')]//td[contains(@class, '_name') and contains(text(), "
									+ subject + ")]", "");
		} else if (view.toLowerCase().equals("workweek")) {
			selenium
					.clickAt(
							"xpath=//div[contains(@id, 'zli__CLWW')]//td[contains(@class, '_name') and contains(text(), "
									+ subject + ")]", "");
		} else if (view.toLowerCase().equals("week")) {
			selenium
					.clickAt(
							"xpath=//div[contains(@id, 'zli__CLW')]//td[contains(@class, '_name') and contains(text(), "
									+ subject + ")]", "");
		} else if (view.toLowerCase().equals("month")) {
			selenium
					.clickAt(
							"xpath=//td[contains(@class, 'calendar_month_day_item')]//div[contains(text(), "
									+ subject + ")]", "");
		}
		Thread.sleep(1000);
	}

	public static void zDblClickApptInMonthView(String subject)
			throws Exception {
		Thread.sleep(1000);
		selenium
				.doubleClickAt(
						"xpath=//td[contains(@class, 'calendar_month_day_item')]//div[contains(text(), "
								+ subject + ")]", "");
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