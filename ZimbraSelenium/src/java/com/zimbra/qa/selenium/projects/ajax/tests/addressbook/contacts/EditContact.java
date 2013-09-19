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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;



public class EditContact extends AjaxCommonTest  {
	public EditContact() {
		logger.info("New "+ EditContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage =  app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	private ContactItem CreateSelectContactItem() throws HarnessException {
		 // Create a contact 
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
 
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn fileAsStr='" + contactItem.lastName + "," + contactItem.firstName + "' >" +
                "<a n='firstName'>" + contactItem.firstName +"</a>" +
                "<a n='lastName'>" + contactItem.lastName +"</a>" +
                "<a n='email'>" + contactItem.email + "</a>" +
                "</cn>" +
                "</CreateContactRequest>");

        app.zGetActiveAccount().soapSelectNode("//mail:CreateContactResponse", 1);

        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);

        // Select the contact
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);

		return contactItem;
	}
	
	private void EditAndVerify(FormContactNew formContactNew, ContactItem contactItem, ContactItem newContact) 
	     throws HarnessException
	   {
		//clear the form, 
		formContactNew.zReset();
		
        // Fill in the form
	    formContactNew.zFill(newContact);
	    
		// Save the contact
        formContactNew.zSubmit();
		
        
        //verify toasted message Contact Saved
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        ZAssert.assertStringContains(toastMsg, "Contact Saved", "Verify toast message 'Contact Saved'");

        //verify new contact item is displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();   
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(newContact.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") existed ");

        
		//verify old contact not displayed
    	isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") deleted");
      
        	    
	}
	
	@Test(	description = "Edit a contact item, click Edit on toolbar",
			groups = { "smoke"})
	public void ClickToolbarEdit() throws HarnessException {
		ContactItem contactItem = CreateSelectContactItem();
				
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	        
        //generate the new contact
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);
						
		EditAndVerify(formContactNew, contactItem, newContact);		
	}

	
	@Test(	description = "Edit a contact item, Right click then click Edit",
			groups = { "smoke" })
	public void ClickContextMenuEdit() throws HarnessException {
		ContactItem contactItem = CreateSelectContactItem();
		
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        
	  	        		
		//generate the new contact
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);
						
		EditAndVerify(formContactNew, contactItem, newContact);
        	             
       }
	
}

