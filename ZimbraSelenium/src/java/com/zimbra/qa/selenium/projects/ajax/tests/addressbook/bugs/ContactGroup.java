/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.bugs;



import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;


public class ContactGroup extends AjaxCommonTest  {

	public ContactGroup() {
		logger.info("New "+ ContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
		
	
	private void verification(ContactGroupItem group) throws HarnessException {
		//verify toasted message 'group created'  
        String expectedMsg ="Group Created";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
        		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
    
	    
        //verify group name is displayed		        
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.fileAs)) {
	            isFileAsEqual = true;	
				break;
			}
		}
	
		ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + group.fileAs + ") existed ");

	    //verify location is System folder "Contacts"
		ZAssert.assertEquals(app.zPageAddressbook.sGetText("css=td.companyFolder"), SystemFolder.Contacts.getName(), "Verify location (folder) is " + SystemFolder.Contacts.getName());
	}
	
	
	
	


	@Test(	description = "Contacts are not populated while creating a new contact group",
			groups = { "functional" })
	public void Bug60652_ContactsGetPopulated() throws HarnessException {			
		//Create random contact group data 
		ContactGroupItem group = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
	
		//open contact group form
		FormContactGroupNew formGroup = (FormContactGroupNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);
        
		//fill in group name
		formGroup.sType(FormContactGroupNew.Locators.zGroupnameField, group.groupName);		
			
		//create contacts
		ContactItem contact = ContactItem.createUsingSOAP(app);
	
		//select contacts option
		formGroup.select(app, FormContactGroupNew.Locators.zSearchDropdown,  FormContactGroupNew.SELECT_OPTION_TEXT_CONTACTS);
			
		ZAssert.assertFalse(formGroup.zIsListGroupEmpty(), "Verify contact's list has some results ");
  
	    //verify contact populated
		ArrayList<ContactItem> ciArray = formGroup.zListGroupRows();
		
		boolean found=false;
		for (ContactItem ci: ciArray) {
			found |= (ci.fileAs.endsWith(contact.fileAs) && ci.email.equals(contact.email));							
		}
        
		ZAssert.assertTrue(found, "Verify contact " + contact.fileAs + " populated");

		//add all to the email list
		formGroup.zClick(FormContactGroupNew.Locators.zAddAllButton);
		
		//TODO: verify email add to the email area		
	   
		//click Save
		formGroup.zSubmit(); 
		
		//verification
		verification(group);

		
	}

	@Test(	description = "Click Delete Toolbar button in Edit Contact Group form",
			groups = { "functionaly" })
	public void Bug62026_ClickDeleteToolbarButtonInEditContactGroupForm() throws HarnessException {

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app,Action.A_LEFTCLICK);
	      
		//Click Edit on Toolbar button	
        app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
    
      
        //Click Delete on Toolbar button	
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
    		 
		//verify toasted message 1 contact group moved to Trash
        String expectedMsg = "1 contact group moved to Trash";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
		        expectedMsg , "Verify toast message '" + expectedMsg + "'");

        //verify deleted contact group not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.groupName)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact group " + group.groupName + " deleted");        

    	FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);


        //verify deleted contact displayed in trash folder
        // refresh Trash folder
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, trash);
   	 
        contacts = app.zPageAddressbook.zListGetContacts(); 
         
		isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.groupName)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact group (" + group.groupName + ") displayed in Trash folder");


   	}

}
