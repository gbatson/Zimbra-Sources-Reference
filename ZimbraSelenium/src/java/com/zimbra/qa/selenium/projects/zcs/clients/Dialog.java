/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.zcs.clients;

import org.testng.Assert;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;


public class Dialog extends SelNGBase{
	//note: this doesnt extend from ZObject since click,rtclick, indlg etc doesnt make sense
	private String coreName;
	public Dialog () {
		this.coreName = "dialogCore";
	}
	public String zExistsDontWait(String dialogNameOrId)  throws HarnessException  {
		//this method doesnt wait and also doesnt fail if object doesnt exist
		return  dialogCore(dialogNameOrId, "exists", false);
	}
	public void zNotExists(String dialogNameOrId) throws HarnessException   {
		String actual = dialogCore(dialogNameOrId, "notexists");
		Assert.assertEquals("true", actual, "Dialog(" + dialogNameOrId
				+ ") Found, which should not be present.");
	}
	public String zGetMessage(String dialogNameOrId)  throws HarnessException  {
		return dialogCore(dialogNameOrId, "getmessage");
	}
	public String zIsNotPresent(String dialogNameOrId)  throws HarnessException  {
		return  dialogCore(dialogNameOrId, "notexists");

	}	
	public void zWait(String dialogNameOrId)  throws HarnessException  {
		this.zWait( dialogNameOrId, "", "");
	}
	public void zWait(String dialogNameOrId, String panel, String param1)  throws HarnessException  {
		//don't call core(since it could go one of the core might be calling this(chicken and egg)
		ClientSessionFactory.session().selenium().call(coreName,  dialogNameOrId, "wait", true, panel, param1);
	}	
	public void zExists(String dialogNameOrId)  throws HarnessException  {
		String actual = dialogCore(dialogNameOrId, "exists");
		Assert.assertEquals("true", actual, "Dialog(" + dialogNameOrId
				+ ") Not Found.");
	}

	public void zVerifyAlertMessage(String dialogNameOrId, String expectedMessage) throws HarnessException   {
		String actual = dialogCore(dialogNameOrId, "getmessage");
		if(actual.indexOf("\r\n")>=0)
			actual = actual.replace("\r\n", "");

		expectedMessage = expectedMessage.replace((char)160, (char)32);
		Assert.assertEquals(actual.trim(), expectedMessage.trim(), printUnMatchedTextWithIndex(actual.trim(),expectedMessage.trim()));
	}
	public void zVerifyContainsText(String dialogNameOrId, String expectedText)  throws HarnessException  {
		String actual = dialogCore(dialogNameOrId, "getalltxt");
		if(actual.indexOf(expectedText)== -1)
			Assert.fail("Expected text("+expectedText+") not found in actual("+actual+")");
	}	
	protected String dialogCore(String dialogNameOrId, String action) throws HarnessException   {
		return dialogCore(dialogNameOrId, action, true);
	}
	protected String dialogCore(String dialogNameOrId, String action, Boolean retryOnFalse)  throws HarnessException  {
		return dialogCore(dialogNameOrId, action, retryOnFalse, "", "");
	}
	protected String dialogCore(String dialogNameOrId, String action, Boolean retryOnFalse,
			String panel, String param1) throws HarnessException   {
		return ClientSessionFactory.session().selenium().call(coreName, dialogNameOrId, action, retryOnFalse, panel, param1);

	}
	
	protected String printUnMatchedTextWithIndex(String actual,
			String expected) {
		if (actual.length() != expected.length()) {
			return "actual(" + actual + ") expected(" + expected + ")";
		}

		char[] arr = new char[actual.length()];
		actual.getChars(0, actual.length(), arr, 0);
		char[] arr2 = new char[expected.length()];
		expected.getChars(0, expected.length(), arr2, 0);
		String retval = "";
		for (int i = 0; i < arr.length; i++) {
			int t = (int) arr[i];
			int t2 = (int) arr2[i];
			if (t != t2) {
				retval = retval + " index=" + i + " ('" + arr[i] + "' != '"
						+ arr2[i] + "'    '" + t + "' != '" + t2 + "')\n";
			}
		}
		return retval;
	}

}
