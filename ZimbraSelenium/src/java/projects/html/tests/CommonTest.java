package projects.html.tests;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.configuration.*;
import org.clapper.util.text.HTMLUtil;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import projects.html.CoreObjects;
import projects.html.Locators;
import projects.html.PageObjects;
import projects.html.clients.ProvZCS;

import framework.core.SelNGBase;
import framework.util.ZimbraSeleniumProperties;
import framework.util.ZimbraUtil;

import com.zimbra.common.service.ServiceException;

/**
 * @author Raja Rao DV
 */
@SuppressWarnings( { "static-access", "deprecation" })
public class CommonTest extends SelNGBase {

	/*
	 * protected static Button button = new Button(); protected static Folder
	 * folder = new Folder(); protected static ButtonMenu buttonMenu = new
	 * ButtonMenu(); protected static MenuItem menuItem = new MenuItem();
	 * protected static Dialog dialog = new Dialog(); protected static
	 * MessageItem messageItem = new MessageItem(); protected static Tab tab =
	 * new Tab(); protected static Editfield editField = new Editfield();
	 * protected static TextArea textAreaField = new TextArea(); protected
	 * static Editor editor = new Editor(); protected static PwdField pwdField =
	 * new PwdField(); protected static CheckBox checkbox = new CheckBox();
	 * protected static RadioBtn radioBtn = new RadioBtn();
	 *
	 * protected static Login login = new Login(); protected static ComposeView
	 * composeView = new ComposeView(); protected static MailApp mailApp = new
	 * MailApp();
	 */
	public static ResourceBundle zmMsg;
	public static ResourceBundle zhMsg;
	public static ResourceBundle zsMsg;
	public static ResourceBundle ajxMsg;
	public static ResourceBundle i18Msg;
	private Configuration conf;
	public static CoreObjects obj;
	public static PageObjects page;
	public static Locators locator;

	protected static Map<String, Object> selfAccountAttrs = new HashMap<String, Object>();
	public static String ZimbraVersion = "";

	public CommonTest() {
		conf = ZimbraSeleniumProperties.getConfigProperties();
		zmMsg = ZimbraSeleniumProperties.getResourceBundleProperty("zmMsg");
		zhMsg = ZimbraSeleniumProperties.getResourceBundleProperty("zhMsg");
		ajxMsg = ZimbraSeleniumProperties.getResourceBundleProperty("ajxMsg");
		i18Msg = ZimbraSeleniumProperties.getResourceBundleProperty("i18Msg");
		zsMsg = ZimbraSeleniumProperties.getResourceBundleProperty("zsMsg");
		obj = new CoreObjects();
		page = new PageObjects();
	}

	public static void zKillBrowsers() throws Exception {
		// reset all the selngbase settings, since they might have been set to
		// true by the failing test
		SelNGBase.labelStartsWith = false;
		SelNGBase.fieldLabelIsAnObject = false;
		SelNGBase.actOnLabel = false;
		CmdExec("taskkill /f /t /im iexplore.exe");
		CmdExec("taskkill /f /t /im firefox.exe");
		CmdExec("taskkill /f /t /im Safari.exe");
		CmdExec("taskkill /f /t /im chrome.exe");

	}

	public static void zLoginIfRequired() throws Exception {
		// set retry to false so that newtests and dependsOn methods would work
		// like fresh-test
		// isExecutionARetry = false;
		Map<String, Object> accntAttrs = new HashMap<String, Object>();
		zLoginIfRequired(accntAttrs);
	}

	public static void zLoginIfRequired(Map<String, Object> accntAttrs)
			throws Exception {
		if (needsReLogin(accntAttrs) || needReset) {
			zKillBrowsers();
			selfAccountAttrs = accntAttrs;
			selfAccountName = page.zLoginpage.zLoginToZimbraHTML(accntAttrs);
		}
	}

	/**
	 * Logs into zimbraAjax and returns zimbra-version
	 *
	 * @return Zimbra Version
	 * @throws ServiceException
	 */
	private String zGetZimbraVersionFromAjax() throws ServiceException {
		try {
			openApplication();
			Thread.sleep(1500);
			obj.zEditField.zType("Username:", "admin");
			obj.zPwdField.zType("Password:", "test123");
			obj.zButton.zClick("class=zLoginButton");
			Thread.sleep(2000);// without this we get permission denied error
			obj.zButton.zExists("id=zb__Search__MENU_left_icon");
			Thread.sleep(2000);// wait another 2 secs after we see the search
			// icon
			return ZimbraUtil.getZimbraVersion().split(" ")[0];
		} catch (Exception e) {
			e.printStackTrace(System.out);

		}
		return "";
	}

	public static boolean zWaitForElement(String elementId) {
		for (int i = 0; i < 10; i++) {
			if (selenium.isElementPresent(elementId))
				return true;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private static boolean needsReLogin(Map<String, Object> accntAttrs) {
		int currentAccntAttrsSize = selfAccountAttrs.size() - 4;// -4 is to
		// remove default settings
		// none has logged in yet
		if (selfAccountName.equals(""))
			return true;
		// a user has already logged in with default settings
		// and test needs to use default-settings as well.
		if (currentAccntAttrsSize == 0 && accntAttrs.size() == 0)
			return false;
		// we have a default user, but need user with some prefs(s)
		// or, we have user with some pref, but need default user
		if ((currentAccntAttrsSize == 0 && accntAttrs.size() > 0)
				|| (selfAccountAttrs.size() > 0) && (accntAttrs.size() == 0))
			return true;

		if ((currentAccntAttrsSize > 0) && (accntAttrs.size() > 0)) {
			Iterator<String> keys = accntAttrs.keySet().iterator();
			while (keys.hasNext()) {
				String reqkey = keys.next();
				// if the key doesnt exist return true
				if (!selfAccountAttrs.containsKey(reqkey)) {
					return true;
				}
				// if the value doesnt match return true
				String key1 = selfAccountAttrs.get(reqkey).toString();
				String key2 = accntAttrs.get(reqkey).toString();
				if (!key1.equals(key2)) {
					return true;
				}
			}

		}

		return false;
	}

	@BeforeSuite(groups = { "always" })
	public void initTests() throws ServiceException {
		initFramework();
		ProvZCS.setupZCSTestBed();
		startSeleniumServer();
		ProvZCS.createAccount("ccuser@testdomain.com");
		ProvZCS.createAccount("bccuser@testdomain.com");
		if (SelNGBase.suiteName.equals("fullSuite")) {
			ZimbraVersion = zGetZimbraVersionFromAjax();
			SelNGBase.ZimbraVersion = ZimbraVersion;
			CmdExec("taskkill /f /t /im iexplore.exe");
			CmdExec("taskkill /f /t /im firefox.exe");
			CmdExec("taskkill /f /t /im Safari.exe");
			CmdExec("taskkill /f /t /im chrome.exe");
		}

	}

	@AfterSuite(groups = { "always" })
	public void stopSeleniumServer() {
		// selenium.click("link="+localize(locator.switchToAdvancedClient));
		// try {
		// Thread.sleep(10000);
		// } catch (InterruptedException e) {
		// }
		// ZimbraVersion = ZimbraUtil.getZimbraVersion();
		super.stopSeleniumServer();
	}

	public void initFramework() {
		super.initFramework(this.conf);
		zhMsg = ResourceBundle.getBundle("framework.locale.ZhMsg", new Locale(
				conf.getString("locale")));

	}

	public static String localize(String locatorKey) {
		String key = locatorKey.split("::")[0];
		String prependthis = "";
		if (key.indexOf("link=") >= 0) {
			key = key.replace("link=", "");
			prependthis = "link=";
		}
		// dont localize if the locatorKey
		if (key.indexOf("=") > 0)
			return key;

		//some keys have . in them(represented by _dot_ in java).
		if(key.indexOf("_dot_")>0)
			key = key.replace("_dot_", ".");

		// else.. it must be a label, so localize...


		if (key.equals("ok") || key.equals("cancel"))// bug(zhmsg is different
			// from ajxmsg)
			return prependthis + HTMLUtil.stripHTMLTags(ajxMsg.getString(key));
		try {
			return prependthis + HTMLUtil.stripHTMLTags(zhMsg.getString(key));
		} catch (MissingResourceException e) {
			try {
				return prependthis
						+ HTMLUtil.stripHTMLTags(zmMsg.getString(key));
			} catch (MissingResourceException e1) {
				try {
					return prependthis
							+ HTMLUtil.stripHTMLTags(ajxMsg.getString(key));
				} catch (MissingResourceException e2) {
					try {
						return prependthis
								+ HTMLUtil.stripHTMLTags(i18Msg.getString(key));
					} catch (MissingResourceException e3) {
						return prependthis
								+ HTMLUtil.stripHTMLTags(zsMsg.getString(key));
					}
				}
			}
		}

	}

	public static String localize(String key, String zeroValue, String oneValue) {
		String loc = localize(key);
		if (zeroValue != "")
			loc = loc.replace("{0}", zeroValue);
		if (oneValue != "")
			loc.replace("{1}", oneValue);
		return loc;
	}

	//
	/**
	 * Returns localized version of import toast message
	 *
	 * @param key
	 *            localize key
	 * @param itemType
	 *            CONTACTS or CALENDAR(yet to implement)
	 * @param numberOfItemsImported
	 *            number of items value Usage: String str =
	 *            CommonTest.localizeChoiceMsgs("contactsImportedResult",
	 *            "CONTACTS", 10);
	 * @return returns localized version of "10 Contacts imported" string in
	 *         English
	 */
	public static String localizeChoiceMsgs(String key, String itemType,
			int numberOfItemsImported) {
		String loc = localize(key);
		String tmp[] = loc.split("\\{*}");
		int numLoc = 0;
		int typLoc = 0;
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].indexOf("number") > 0)
				numLoc = i;
			else if (tmp[i].indexOf("choice") > 0)
				typLoc = i;
		}

		if (numberOfItemsImported != 1 && itemType.equals("CONTACTS")) {
			tmp[typLoc] = localize("contacts");
			tmp[numLoc] = "" + numberOfItemsImported;
		} else if (numberOfItemsImported == 1 && itemType.equals("CONTACTS")) {
			tmp[typLoc] = localize("contact");
			tmp[numLoc] = "" + numberOfItemsImported;
		}
		String val = "";
		for (int i = 0; i < tmp.length; i++) {
			val = val + " " + tmp[i];
		}
		return val;
	}

	public static String getLocalizedData(int numberofkeys) {
		String[] keysArray = { "saveDraft", "saveDraftTooltip", "savedSearch",
				"savedSearches", "saveIn", "savePrefs", "saveSearch",
				"saveSearchTooltip", "saveToSent", "saveToSentNOT", "schedule",
				"search", "searchAll", "searchAppts", "searchBuilder",
				"searchByAttachment", "searchByBasic", "searchByCustom",
				"searchByDate", "searchByDomain", "searchByFlag",
				"searchByFolder", "searchBySavedSearch", "searchBySize",
				"searchByTag", "searchByTime", "searchByZimlet",
				"searchCalendar", "searchContacts", "whenSentToError",
				"whenSentToHint", "whenInFolderError", "whenInFolderHint",
				"whenReplyingToAddress", "whenReplyingToFolder",
				"sendNoMailAboutShare", "sendUpdateTitle", "sendUpdatesNew",
				"sendUpdatesAll", "sendStandardMailAboutShare",
				"sendStandardMailAboutSharePlusNote", "sendPageTT",
				"sendTooltip" };
		Random r = new Random();
		String output = "";
		for (int i = 0; i < numberofkeys; i++) {
			int randint = r.nextInt(keysArray.length);
			output = output + localize(keysArray[randint]).replace("\"", "");

		}
		return output;
	}

	/**
	 * Returns a 5-char length word with random-characters that are localized.
	 * Also, the returned word is special-char or space free.
	 *
	 * @return "tesxe"
	 */
	public static String getLocalizedData_NoSpecialChar() {
		String str = localize("whenReplyingToAddress");
		str = str + localize("editNotebookIndex");
		str = str + localize("invitees");
		str = str + localize("subject");
		str = str + localize("searchCalendar");
		str = str + localize("goToMail");
		str = str + localize("tagItem");
		//str = str + localize("imPrefFlashIcon");
		str = str.replace(" ", "");
		str = str.replace(".", "");
		str = str.replace(":", "");
		Random r = new Random();
		int max = str.length() - 5;
		int randInt = r.nextInt(max);
		return str.substring(randInt, randInt + 5);

	}

	public static String getTodaysDateZimbraFormat() {

		String DATE_FORMAT = "yyyyMMdd";

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		Calendar testcal = Calendar.getInstance();

		String todayDate = sdf.format(testcal.getTime());

		return todayDate;
	}

	public static void assertReport(String expectedFullBody,
			String dataToVerify, String reportSummary) throws Exception {
		if (expectedFullBody.equals("_selfAccountName_"))
			expectedFullBody = selfAccountName;
		if (dataToVerify.equals("_selfAccountName_"))
			dataToVerify = selfAccountName;
		Assert.assertTrue(expectedFullBody.indexOf(dataToVerify) >= 0,
				"Expected value(" + expectedFullBody + "), Actual Value("
						+ dataToVerify + ")");
	}

	public void zPressBtnIfDlgExists(String dlgName, String dlgBtn,
			String folderToBeClicked) throws Exception {
		for (int i = 0; i <= 5; i++) {
			String dlgExists = obj.zDialog.zExistsDontWait(dlgName);
			if (dlgExists.equals("true")) {
				obj.zFolder.zClickInDlgByName(folderToBeClicked, dlgName);
				Thread.sleep(1000);
				obj.zButton.zClickInDlgByName(dlgBtn, dlgName);
			} else {
				Thread.sleep(500);
			}
		}
	}

	/**
	 * @param applicationtab
	 *            Either specify exact (any case either lower OR upper)
	 *            application tab name in english (for e.g. "Mail",
	 *            "Address Book", "Calendar", "Tasks", "Documents", "Briefcase",
	 *            "Preferences") OR pass corresponding localize string to click
	 *            on application tab
	 */
	public static void zGoToApplication(String applicationtab) throws Exception {
		String lCaseapplicationtab = applicationtab.toLowerCase();
		if ((lCaseapplicationtab.equals("mail"))
				|| (applicationtab.equals("id=TAB_MAIL"))) {
			obj.zButton.zClick("id=TAB_MAIL");
			Thread.sleep(2500);
			zWaitTillObjectExist("button", page.zMailApp.zRefreshBtn);
		} else if ((lCaseapplicationtab.equals("address book"))
				|| (applicationtab.equals("id=TAB_ADDRESSBOOK"))) {
			obj.zButton.zClick("id=TAB_ADDRESSBOOK");
		} else if ((lCaseapplicationtab.equals("calendar"))
				|| (applicationtab.equals("id=TAB_CALENDAR"))) {
			obj.zButton.zClick("id=TAB_CALENDAR");
		} else if ((lCaseapplicationtab.equals("tasks"))
				|| (applicationtab.equals("id=TAB_TASKS"))) {
			obj.zButton.zClick("id=TAB_TASKS");
		} else if ((lCaseapplicationtab.equals("preferences"))
				|| (applicationtab.equals("id=TAB_OPTIONS"))) {
			obj.zButton.zClick("id=TAB_OPTIONS");
			Thread.sleep(2500);
			zWaitTillObjectExist("radiobutton", "name=zimbraPrefClientType");
		}
	}

	public static void zWaitTillObjectExist(String objectType, String objectName)
			throws Exception {
		int i = 0;
		boolean found = false;
		for (i = 0; i <= 15; i++) {
			String retVal = null;
			objectType = objectType.toLowerCase();
			if (objectType.equals("button")) {
				retVal = obj.zButton.zExistsDontWait(objectName);
			} else if (objectType.equals("checkbox")) {
				retVal = obj.zCheckbox.zExistsDontWait(objectName);
			} else if (objectType.equals("radiobutton")) {
				retVal = obj.zRadioBtn.zExistsDontWait(objectName);
			} else if (objectType.equals("message")) {
				retVal = obj.zMessageItem.zExistsDontWait(objectName);
			} else if (objectType.equals("menuitem")) {
				retVal = obj.zMenuItem.zExistsDontWait(objectName);
			} else if (objectType.equals("htmlmenu")) {
				retVal = obj.zMenuItem.zExistsDontWait(objectName);
			} else if (objectType.equals("folder")) {
				retVal = obj.zFolder.zExistsDontWait(objectName);
			} else if (objectType.equals("tab")) {
				retVal = obj.zTab.zExistsDontWait(objectName);
			} else if (objectType.equals("editfield")) {
				retVal = obj.zEditField.zExistsDontWait(objectName);
			} else if (objectType.equals("textarea")) {
				retVal = obj.zTextAreaField.zExistsDontWait(objectName);
			} else if (objectType.equals("link")) {
				if (selenium.isElementPresent("link=" + objectName))
					retVal = "true";
				else
					retVal = "false";
			} else if (objectType.equals("text")) {
				if (selenium.isTextPresent(objectName))
					retVal = "true";
				else
					retVal = "false";
			}

			if (retVal.equals("false")) {
				Thread.sleep(2000);
			} else {
				Thread.sleep(1000);
				found = true;
				break;
			}
		}
		if (!found)
			Assert.fail("Object(" + objectName
					+ ") didn't appear even after 60 seconds");
	}

	public static String getNameWithoutSpace(String key) {
		if (config.getString("browser").equals("IE"))
			return key.replace("�:", "");
		else
			return key;
	}

	public static void zReloginToAjax() throws Exception {

		String accountName = selfAccountName;

		zKillBrowsers();
		Thread.sleep(2000);

		SelNGBase.selfAccountName = accountName;
		page.zLoginpage.zLoginToZimbraHTML(accountName);

	}

	public void startSeleniumServer() {
		try {
			super.startSeleniumServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
