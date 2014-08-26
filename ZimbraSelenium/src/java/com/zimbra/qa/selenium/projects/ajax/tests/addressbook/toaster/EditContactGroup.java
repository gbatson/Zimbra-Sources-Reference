/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.toaster;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.ContactGroupItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew.Field;


public class EditContactGroup extends AjaxCommonTest  {
	public EditContactGroup() {
		logger.info("New "+ EditContactGroup.class.getCanonicalName());

		// All tests start at the Address page
		super.startingPage =  app.zPageAddressbook;
		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		

	}
	@Test(description = "Edit a contact group by click Edit on Toolbar button and verify Toast msg", groups = { "functional" })
	public void EditContactGroupToastMsg_01() throws HarnessException {

		// A new group name
		String newname = "edit" + ZimbraSeleniumProperties.getUniqueString();
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		//-Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		//Click Edit on Toolbar button	
		FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);

		// CHange the group name
		formContactGroupNew.zFillField(Field.GroupName, newname);
		formContactGroupNew.zSubmit();

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Group Saved","Verify toast message: Group Saved");
	}

	@Test(description = "Edit a contact group by click Edit Group on Context Menu and verify toast msg", groups = { "functional" })
	public void EditContactGroupToastMsg_02() throws HarnessException {

		// A new group name
		String newname = "edit" + ZimbraSeleniumProperties.getUniqueString();
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Right click -> Edit	
		FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, group.getName());        

		// CHange the group name
		formContactGroupNew.zFillField(Field.GroupName, newname);
		formContactGroupNew.zSubmit();

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Group Saved","Verify toast message: Group Saved");

	}


	@Test(description = "Edit a contact group by double click on the contact group and verify toast msg  ", groups = { "functional" })
	public void EditContactGroupToastMsg_03() throws HarnessException {

		// A new group name
		String newname = "edit" + ZimbraSeleniumProperties.getUniqueString();

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Double click	
		FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, group.getName());        

		// CHange the group name
		formContactGroupNew.zFillField(Field.GroupName, newname);
		formContactGroupNew.zSubmit();

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Group Saved","Verify toast message: Group Saved");
	}

} 

