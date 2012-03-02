package projects.zcs.tests.addressbook.tags;

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
public class TagContactTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagForContactAndVerify")
				|| test.equals("verifyTagFunctionalityFor2ContactAndRemoveTag")
				|| test.equals("applyMutlipleTagToContactAndVerify")
				|| test.equals("editContactAndVerifyAddRemoveTag")
				|| test.equals("editContactGroupAndVerifyAddRemoveTag")
				|| test
						.equals("verifyTagFunctionalityFor2ContactGroupAndRemoveTag")
				|| test.equals("applyTagByDnDTagToContactAndViceVersa")
				|| test.equals("tryToCreateDuplicateTagInAddressBook")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
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
		zGoToApplication("Address Book");
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
	 * Verify create, rename & delete functionality for tag for contacts
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForContactAndVerify(String firstName,
			String lastName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, newTag1;
		page.zABCompose.zCreateBasicContact(lastName, "", firstName);
		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(lastName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		Thread.sleep(1000);

		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);

		zDeleteTag(newTag1);
		obj.zContactListItem.zClick(lastName);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Create 2 tag, apply 1 tag to each contact and verify contact exist / not
	 * exist by clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2ContactAndRemoveTag(String firstName,
			String lastName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String lastName2, tag1, tag2;
		lastName2 = getLocalizedData_NoSpecialChar();
		page.zABCompose.zCreateBasicContact(lastName, "", firstName);
		page.zABCompose.zCreateBasicContact(lastName2, "", firstName);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(lastName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		Thread.sleep(1000);
		obj.zContactListItem.zClick(lastName2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zExists(lastName);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName2),
				"Verify contact2 not exists");
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zContactListItem.zExists(lastName2);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact1 not exists");
		obj.zFolder.zClick(localize(locator.contacts));
		Thread.sleep(1000);
		assertReport("true", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact1 not exists");
		assertReport("true", obj.zContactListItem.zExistsDontWait(lastName2),
				"Verify contact2 not exists");
		obj.zFolder.zClick(localize(locator.emailedContacts));
		Thread.sleep(1000);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact1 not exists");
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName2),
				"Verify contact2 not exists");

		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zClick(lastName2);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Create 2 tag, apply both tag to contact and verify both contact exists
	 * after clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyMutlipleTagToContactAndVerify(String firstName,
			String lastName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, tag2;
		page.zABCompose.zCreateBasicContact(lastName, "", firstName);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		obj.zContactListItem.zClick(lastName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag1);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zExists(lastName);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zContactListItem.zExists(lastName);

		needReset = false;
	}

	/**
	 * Edit contact and verify add, remove tag functionality
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editContactAndVerifyAddRemoveTag(String firstName,
			String lastName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, tag2;
		page.zABCompose.zCreateBasicContact(lastName, "", firstName);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(lastName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		Thread.sleep(1000);

		obj.zButton.zClick(page.zABApp.zEditContactIconBtn);
		obj.zButton.zClick(page.zABApp.zTagContactBtn_EditContact);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactBtn_EditContact);
		obj.zMenuItem.zExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactBtn_EditContact);
		obj.zMenuItem.zExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zButton.zClick(localize(locator.close));
		obj.zFolder.zClick(tag1);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact not exists");
		obj.zFolder.zClick(tag2);
		obj.zContactListItem.zExists(lastName);

		needReset = false;
	}

	/**
	 * Edit contact group and verify add, remove tag functionality
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editContactGroupAndVerifyAddRemoveTag(String firstName,
			String groupName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, tag2;
		page.zABApp.zCreateContactGroup(groupName, "bccuser@testdomain.com");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(groupName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(groupName);
		Thread.sleep(1000);

		obj.zButton.zClick(page.zABApp.zEditContactIconBtn);
		obj.zButton.zClick(page.zABApp.zTagGroupBtn_EditGroup);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagGroupBtn_EditGroup);
		obj.zMenuItem.zExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagGroupBtn_EditGroup);
		obj.zMenuItem.zExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zButton.zClick(localize(locator.close));
		obj.zFolder.zClick(tag1);
		assertReport("false", obj.zContactListItem.zExistsDontWait(groupName),
				"Verify contact group not exists");
		obj.zFolder.zClick(tag2);
		obj.zContactListItem.zExists(groupName);

		needReset = false;
	}

	/**
	 * Create 2 tag, apply 1 tag to each contact group and verify contact group
	 * exist / not exist by clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2ContactGroupAndRemoveTag(
			String firstName, String group1) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String group2, tag1, tag2;
		group2 = getLocalizedData_NoSpecialChar();
		page.zABApp.zCreateContactGroup(group1, "bccuser@testdomain.com");
		page.zABApp.zCreateContactGroup(group2, "bccuser@testdomain.com");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(group1);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(group1);
		Thread.sleep(1000);
		obj.zContactListItem.zClick(group2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(group2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zExists(group1);
		assertReport("false", obj.zContactListItem.zExistsDontWait(group2),
				"Verify contact group2 not exists");
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zContactListItem.zExists(group2);
		assertReport("false", obj.zContactListItem.zExistsDontWait(group1),
				"Verify contact group1 not exists");
		obj.zFolder.zClick(localize(locator.contacts));
		Thread.sleep(1000);
		assertReport("true", obj.zContactListItem.zExistsDontWait(group1),
				"Verify contact group1 not exists");
		assertReport("true", obj.zContactListItem.zExistsDontWait(group2),
				"Verify contact group2 not exists");
		obj.zFolder.zClick(localize(locator.emailedContacts));
		Thread.sleep(1000);
		assertReport("false", obj.zContactListItem.zExistsDontWait(group1),
				"Verify contact group1 not exists");
		assertReport("false", obj.zContactListItem.zExistsDontWait(group2),
				"Verify contact group2 not exists");

		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zClick(group2);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Verify drag n drop functionality for tag and contact. Drag contact to tag
	 * and verify tag applied & same way drag tag to contact and verify tag
	 * applied
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyTagByDnDTagToContactAndViceVersa(String firstName,
			String lastName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String lastName2, tag1, tag2;
		lastName2 = getLocalizedData_NoSpecialChar();
		page.zABCompose.zCreateBasicContact(lastName, "", firstName);
		page.zABCompose.zCreateBasicContact(lastName2, "", firstName);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zCreateTag(tag2);

		zDragAndDrop("//tr[contains(@id, 'zlif__CNS')]//td[contains(text(), '"
				+ lastName + "')]",
				"//td[contains(@id, 'zti__main_Contacts') and contains(text(), '"
						+ tag1 + "')]");
		obj.zContactListItem.zVerifyIsTagged(lastName);
		Thread.sleep(1000);
		obj.zFolder.zClick(localize(locator.trash));
		Thread.sleep(1000);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact1 not exists");
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName2),
				"Verify contact2 not exists");
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zContactListItem.zExists(lastName);

		obj.zFolder.zClick(localize(locator.contacts));
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Contacts') and contains(text(), '"
						+ tag2 + "')]",
				"//tr[contains(@id, 'zlif__CNS')]//td[contains(text(), '"
						+ lastName2 + "')]");
		obj.zContactListItem.zVerifyIsTagged(lastName2);
		Thread.sleep(1000);
		obj.zFolder.zClick(localize(locator.contacts));
		Thread.sleep(1000);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zContactListItem.zExists(lastName2);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact1 not exists");

		needReset = false;
	}

	/**
	 * Try to create duplicate tag and verify its not allowed
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateTagInAddressBook(String firstName,
			String lastName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1;
		tag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zDuplicateTag(tag1);

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