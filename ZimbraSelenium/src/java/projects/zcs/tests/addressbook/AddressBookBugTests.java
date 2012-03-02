package projects.zcs.tests.addressbook;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import framework.util.RetryFailedTests;

/**
 * This covers some high priority test cases related to address book
 * 
 * @written by Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class AddressBookBugTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "ABDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("checkAutoFillForConatctWorkAddr_41144")) {
			return new Object[][] { {
					"lastName  " + getLocalizedData_NoSpecialChar(),
					"middleName " + getLocalizedData_NoSpecialChar(), "",
					"touser@testdomain.com" } };
		} else if (test
				.equals("CreateContactWhileSrchFldrAndTagSelected_Bug40517")) {
			return new Object[][] { { "LN" + getLocalizedData_NoSpecialChar(),
					"MN" + getLocalizedData_NoSpecialChar(),
					"FN" + getLocalizedData_NoSpecialChar(),
					"NLN" + getLocalizedData_NoSpecialChar(),
					"NMN" + getLocalizedData_NoSpecialChar(),
					"NFN" + getLocalizedData_NoSpecialChar(),
					"NNMN" + getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zABCompose.zNavigateToContact();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test Case:-Contact added as Work Address is not autofilled Login and go
	 * to Address book tab. Create a new contact and add the email address as
	 * "Work Address". Save the contact and compose a new mail. When a letter is
	 * placed in a "To" or "Cc' field it is suppose to auto fill Expected
	 * result:-The contact must get auto filled.
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param email
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void checkAutoFillForConatctWorkAddr_41144(String cnLastName,
			String cnMiddleName, String cnFirstName, String email)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (config.getString("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		page.zABCompose.zEnterBasicABData("", cnLastName, cnMiddleName,
				cnFirstName);
		Thread.sleep(1000);
		selenium.clickAt("id=editcontactform_EMAIL_0_add", "");
		Thread.sleep(2000);
		obj.zEditField.zActivateAndType(page.zABCompose.zWorkEmail1EditField,
				email);
		obj.zButton.zClick(localize(locator.save), "2");
		Thread.sleep(1500);
		obj.zContactListItem.zExists(cnLastName);
		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		System.out.println(email);
		if (email.contains("@")) {
			email = email.substring(0, email.indexOf('@'));
		}
		selenium.typeKeys("id=zv__COMPOSE1_to_control", email);
		selenium.keyDown("id=zv__COMPOSE1_to_control", "\\13");
		selenium.keyUp("id=zv__COMPOSE1_to_control", "\\13");
		Thread.sleep(1000);
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'DWT') and contains(@style,'display: block') and @class='ZmAutocompleteListView']/div"),
						"Auto complete not showing");

		needReset = false;
	}

	/**
	 * Scenario 1: Login to Web Client. Create a new contact and create Saved
	 * Search with a Contact. Go to Searches -> Click on New Contact. Enter
	 * Details and try to save it. Expected :Should able to add new contact
	 * 
	 *Scenario 2: Assign Tag to any contact. Go to Tags -> Click on New
	 * Contact. Enter details and try to save it.
	 * 
	 *Expected :Should able to add new contact
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void CreateContactWhileSrchFldrAndTagSelected_Bug40517(
			String cnLastName, String cnMiddleName, String cnFirstName,
			String newcnLastName, String newMiddleName, String newcnLastName1,
			String newMiddleName1) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateContactInAddressBook("", cnLastName,
				cnMiddleName, cnFirstName);
		obj.zFolder.zClick(page.zABCompose.zContactsFolder);
		obj.zContactListItem.zExists(cnLastName);
		selenium.type("xpath=//input[@class='search_input']", cnLastName);
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zContactListItem.zExists(cnLastName);
		obj.zButton.zClick(localize(locator.save));
		obj.zDialog.zExists(localize(locator.saveSearch));
		selenium.type("xpath=//td/input[contains(@id,'_nameField')]",
				"savecontact");
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(2000);
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'savecontact')]"),
						"savecontact folder does not present");
		selenium
				.clickAt(
						"xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'savecontact')]",
						"");
		page.zABCompose.zCreateContactInAddressBook("", newcnLastName,
				newMiddleName, "");
		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zRtClick(newcnLastName);
		selenium.mouseOver("id=zmi__Contacts__TAG_MENU_title");
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), "tagName");
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(2000);
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'tagName')]"),
						"tagName folder does not present");
		selenium
				.clickAt(
						"xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'tagName')]",
						"");
		obj.zContactListItem.zExists(newcnLastName);
		page.zABCompose.zCreateContactInAddressBook("", newcnLastName1,
				newMiddleName1, "");
		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zExists(newcnLastName1);

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}

}