package com.zimbra.qa.selenium.projects.ajax.tests.mail.bugs;

import java.io.IOException;

import org.testng.annotations.Test;

import com.zimbra.cs.lmtpserver.LmtpProtocolException;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class Bug39246 extends AjaxCommonTest {

	public Bug39246() {
		logger.info("New "+ Bug39246.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Verify bug 39246",
			groups = { "functional" })
	public void Bug39246_01() throws IOException, LmtpProtocolException, HarnessException  {
		
		final String subject = "Bug39246";
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/viewEntireMessage_Bug39246.txt";
		
		
		// Inject the example message
		LmtpUtil.injectFile(app.zGetActiveAccount().EmailAddress, mime);

		// Get Mail
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the message
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// In the preview pane, click "View Entire Message"
		DisplayMail pane = app.zPageMail.zGetReadingPane();
		pane.zClickViewEntireMessage();

		throw new HarnessException("Convert remaining code from LmtpInject.viewEntireMessage_Bug39246()");

		
	}

}