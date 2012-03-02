package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.search;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.desktop.core.DesktopCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.PageMain;


public class PeopleSearch extends DesktopCommonTest{

   public PeopleSearch() throws HarnessException {
      logger.info("New "+ PeopleSearch.class.getCanonicalName());
      
      // All tests start at the login page
      super.startingPage = app.zPageMain;
   }

   @Test(   description = "Verifying People Search property",
         groups = { "sanity" })
   public void BasicPeopleSearch01() throws HarnessException {
      GeneralUtility.waitForElementPresent(app.zPageMain, PageMain.Locators.zPeopleSearchField);
      String searchResult =
         app.zPageMain.sGetText(PageMain.Locators.zPeopleSearchField);
      logger.debug("Search result: " + searchResult);
      ZAssert.assertEquals(searchResult, "", "Verifying the initial text" +
      		" on People Search Textfield.");

      ZAssert.assertTrue(app.zPageMain.sIsVisible(
            PageMain.Locators.zPeopleSearchImage),
            "Search icon (the magnifying glass is visible.");
   }
}