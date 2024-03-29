/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.ui;

import java.awt.event.KeyEvent;

import org.apache.log4j.*;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;


public class PagePrint extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsTab.class);
	



	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public PagePrint(AbsApplication application) {
		super(application);
		
		logger.info("new " + PagePrint.class.getCanonicalName());
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
	    return true;
		//throw new HarnessException("Implement me");
	} 
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	
	public void cancelPrintDialog() throws HarnessException {
		//wait for Print Dialog displayed
		SleepUtil.sleepMedium();
		
		// close Print dialog 
		zKeyboard.zTypeKeyEvent(KeyEvent.VK_ESCAPE);
	
		SleepUtil.sleepSmall();
		
		//switch to Print View
		String title ="title=Zimbra";
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...switch to Print View");
			switchTo(title);
		}else{
			ClientSessionFactory.session().selenium().selectWindow(title);
		}
	}
	
	
	public boolean isContained(String locator, String message) throws HarnessException {		
		return this.sGetText(locator).contains(message);
	}
}
