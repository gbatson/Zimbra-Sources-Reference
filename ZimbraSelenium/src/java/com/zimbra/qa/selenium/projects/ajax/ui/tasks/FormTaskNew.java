package com.zimbra.qa.selenium.projects.ajax.ui.tasks;

import java.util.List;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;






/**
 * The <code>FormMailNew<code> object defines a compose new message view
 * in the Zimbra Ajax client.
 * <p>
 * This class can be used to compose a new message.
 * <p>
 * 
 * @author Matt Rhoades
 * @see http://wiki.zimbra.com/wiki/Testing:_Selenium:_ZimbraSelenium_Overview#Mail_Page
 */
public class FormTaskNew extends AbsForm {
	
	/**
	 * Defines Selenium locators for various objects in {@link FormTaskNew}
	 */
	public static class Locators {
		
		public static final String zSendIconBtn			= "css=[id^=zb__COMPOSE][id$=__SEND_title]";
		public static final String zCancelIconBtn		= "css=[id^=zb__COMPOSE][id$=__CANCEL_title]";
		public static final String zSaveDraftIconBtn	= "css=[id^=zb__COMPOSE][id$=__SAVE_DRAFT_title]";
		public static final String zSpellCheckIconBtn	= "css=[id^=zb__COMPOSE][id$=__SPELL_CHECK_title]";

		public static final String zToField				= "css=[id^=zv__COMPOSE][id$=_to_control]";
		public static final String zCcField				= "css=[id^=zv__COMPOSE][id$=_cc_control]";
		public static final String zBccField			= "css=[id^=zv__COMPOSE][id$=_bcc_control]";
		public static final String zSubjectField		= "css=div[id^=zv__COMPOSE] input[id$=_subject_control]";
		
		public static final String zBodyFrameHTML		= "//div[contains(id,'zv__COMPOSE')]//iframe";

		
		public static final String zPriorityPulldown	= "css=[id^=zv__COMPOSE][id$=___priority_dropdown]";
		public static final String zPriorityOptionHigh	= "css=[id^=zv__COMPOSE][id$=___priority_dropdown]";
		public static final String zPriorityOptionNormal	= "css=[id^=zv__COMPOSE][id$=___priority_dropdown]";
		public static final String zPriorityOptionLow	= "css=[id^=zv__COMPOSE][id$=___priority_dropdown]";
		
		//added by Girish
		public static final String zFrame = "css=iframe[id*='DWT']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT9_left_icon']";
		public static final String zBodyField = "css=body";
		public static final String zNameField = "css=[id^=DWT4] [input$=]";
		public static final String zEditNameField = "css=[class=DwtInputField] [input$=]";
		public static final String zSaveTask = "zb__TKE1__SAVE_left_icon";
		public static final String zTasksubjField = "//td[contains(@id,'_subject')]/div/input";
		public static final String zCancelTask = "zb__TKE1__CANCEL_left_icon";

		
	}

	public static class Field {
	
		public static final Field To = new Field("To");
		public static final Field Cc = new Field("Cc");
		public static final Field Bcc = new Field("Bcc");
		public static final Field Subject = new Field("Subject");
		public static final Field Body = new Field("Body");
		
		
		private String field;
		private Field(String name) {
			field = name;
		}
		
		@Override
		public String toString() {
			return (field);
		}

	}
	
	
	/**
	 * Protected constuctor for this object.  Only classes within
	 * this package should create DisplayMail objects.
	 * 
	 * @param application
	 */
	protected FormTaskNew(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormTaskNew.class.getCanonicalName());

	}

	public boolean zIsVisible() {
		String locator = "//div[contains(@id,'ztb__COMPOSE')]";
		
		if ( !this.sIsElementPresent(locator) )
			return (false);
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) )
			return (false);
		
		return (true);
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	

	@Override
	public void zSubmit() throws HarnessException {
		if (!this.sIsElementPresent(Locators.zSaveTask))
			throw new HarnessException("Save button is not present");
		zClick(Locators.zSaveTask);

		this.zWaitForBusyOverlay();

	}

	/**
	 * Press the toolbar button
	 * @param button
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Fallthrough objects
		AbsPage page = null;
		String locator = null;
		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Fallthrough objects

		if (button == Button.B_CANCEL) {

			locator = Locators.zCancelTask;

			this.zClick(locator);
			page = new DialogWarning(
					DialogWarning.DialogWarningID.SaveTaskChangeMessage,
					this.MyApplication);

			this.zWaitForBusyOverlay();

			return (page);
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}
	/*	if ( button == Button.B_SEND ) {
			
			locator = Locators.zSendIconBtn;
			
			// Click on send
			this.zClick(locator);
			
			this.zWaitForBusyOverlay();
			
			// Wait for the message to be delivered
			try {
			
				// Check the message queue
				Stafpostqueue sp = new Stafpostqueue();
				sp.waitForPostqueue();
			
			} catch (Exception e) {
				throw new HarnessException("Unable to wait for message queue", e);
			}
			
			return (page);
		
		} else if ( button == Button.B_CANCEL ) {

			locator = Locators.zCancelIconBtn;
			page = new DialogWarning(DialogWarning.DialogWarningID.SaveCurrentMessageAsDraft, this.MyApplication);
			
			// If the compose view is not dirty (i.e. no pending changes)
			// then the dialog will not appear.  So, click the button
			// and return the page, without waiting for it to be active
						
			this.zClick(locator);

			this.zWaitForBusyOverlay();

			// Return the page, if specified
			return (page);
			
		} else if ( button == Button.B_SAVE_DRAFT ) {

			locator = Locators.zSaveDraftIconBtn;
			page = this;
			
			// FALL THROUGH
			
		} else if ( button == Button.B_ADD_ATTACHMENT ) {

			throw new HarnessException("implement me (?)");
			
			// FALL THROUGH
			
		} else if ( button == Button.B_SPELL_CHECK ) {

			locator = Locators.zSpellCheckIconBtn;
			page = this;
			
			// FALL THROUGH
			
		} else if ( button == Button.B_SIGNATURE ) {

			throw new HarnessException("use zToolbarPressPulldown to attach signature");
			
		} else if ( button == Button.B_OPTIONS ) {

			throw new HarnessException("use zToolbarPressPulldown to attach signature");
			
		} else if ( button == Button.B_SHOWBCC) {

			page = this;
			locator = "xpath=//div[contains(@id,'zv__COMPOSE')]//a[contains(@id,'_toggle_bcc')]";

			if ( zBccIsActive() )
				return (this);
			
			////
			// For some reason, zClick doesn't work for "Show BCC", but sClick does
			////
			
			// Click it
			this.sClick(locator);
			
			this.zWaitForBusyOverlay();

			return (page);
		}
		else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		// Make sure a locator was set
		if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		
		// Default behavior, process the locator by clicking on it
		//
		
		// Click it
		this.zClick(locator);

		// if the app is busy, wait for it to become active again
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			
			// Make sure the page becomes active
			page.zWaitForActive();
			
		}
		*/
		// Return the page, if specified
	//	return (page);

	}
	
	/**
	 * Press the toolbar pulldown and the menu option
	 * @param pulldown
	 * @param option
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown("+ pulldown +", "+ option +")");
		
		if ( pulldown == null )
			throw new HarnessException("Pulldown cannot be null!");
		
		if ( option == null )
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( pulldown == Button.B_PRIORITY ) {
			
			if ( option == Button.O_PRIORITY_HIGH ) {
				
				// TODO
				pulldownLocator = "css=[id^='zv__COMPOSE'][id$='___priority_left_icon']";
				optionLocator = "TODO";
				page = this;

			} else if ( option == Button.O_PRIORITY_NORMAL ) {
				
				// TODO
				pulldownLocator = "css=[id^='zv__COMPOSE'][id$='___priority_left_icon']";
				optionLocator = "TODO";
				page = this;

			} else if ( option == Button.O_PRIORITY_LOW ) {
				
				// TODO
				pulldownLocator = "css=[id^='zv__COMPOSE'][id$='___priority_left_icon']";
				optionLocator = "TODO";
				page = this;

			} else {
				throw new HarnessException("unsupported priority option "+ option);
			}
				
		} else {
			throw new HarnessException("no logic defined for pulldown "+ pulldown);
		}

		// Default behavior
		if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !this.sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
			}
			
			this.zClick(pulldownLocator);

			this.zWaitForBusyOverlay();
			
			if ( optionLocator != null ) {

				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}
				
				this.zClick(optionLocator);

				this.zWaitForBusyOverlay();

			}
			
			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if ( page != null ) {
				page.zWaitForActive();
			}
			
		}
		
		// Return the specified page, or null if not set
		return (page);
	}
	
	/**
	 * Fill in the form field with the specified text
	 * @param field
	 * @param value
	 * @throws HarnessException
	 */
	public void zFillField(Field field, String value) throws HarnessException {
	
		String locator = null;
		

		if (field == Field.Subject) {

			locator = Locators.zTasksubjField;

		} else if (field == Field.Body) {
			locator = Locators.zBodyField;
			try {
				sSelectFrame(Locators.zFrame);
				sType(locator, value);
			} finally {
				sSelectWindow(null);
			}
			return;

		} else {

			throw new HarnessException("not implemented for field " + field);
		}
		
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		// Enter text
		this.sType(locator, value);
		
		this.zWaitForBusyOverlay();

	}
	
	
	private boolean zBccIsActive() throws HarnessException {
		logger.info(myPageName() + ".zBccIsActive()");

		// <tr id='zv__COMPOSEX_bcc_row' style='display: table_row' x-display='table-row' ...
		// <tr id='zv__COMPOSEX_bcc_row' style='display: none'  x-display='table-row' ...
		
		String xpath = "//div[contains(@id,'zv__COMPOSE')]//tr[contains(@id,'_bcc_row')]";
		if ( !sIsElementPresent(xpath) )
			throw new HarnessException("Unable to locate the BCC field "+ xpath);
		
		String locator = "xpath=("+ xpath +")@style";
		String style = this.sGetAttribute(locator);
		
		logger.info(myPageName() + ".zBccIsActive() ... style="+ style);
		return (!style.contains("none"));
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info(myPageName() + ".zFill(ZimbraItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a MailItem
		if ( !(item instanceof MailItem) ) {
			throw new HarnessException("Invalid item type - must be MailItem");
		}
		
		// Convert object to MailItem
		MailItem mail = (MailItem) item;
		
		// Fill out the form
		//
		
		// Handle the subject
		if ( mail.dSubject != null ) {
			
			zFillField(Field.Subject, mail.dSubject);

		}
		
		if ( mail.dBodyText != null ) {
			
			zFillField(Field.Body, mail.dBodyText);
			
		}
		
		// TODO: how to handle HTML body?
		
		// Handle the Recipient list, which can be a combination
		// of To, Cc, Bcc, and From
		StringBuilder to = null;
		StringBuilder cc = null;
		StringBuilder bcc = null;
		StringBuilder from = null;
		
		// Convert the list of recipients to a semicolon separated string
		List<RecipientItem> recipients = mail.dAllRecipients();
		if ( recipients != null ) {
			if ( !recipients.isEmpty() ) {
				
				for (RecipientItem r : recipients) {
					if ( r.dType == RecipientType.To ) {
						if ( to == null ) {
							to = new StringBuilder();
							to.append(r.dEmailAddress);
						} else {
							to.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.Cc ) {
						if ( cc == null ) {
							cc = new StringBuilder();
							cc.append(r.dEmailAddress);
						} else {
							cc.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.Bcc ) {
						if ( bcc == null ) {
							bcc = new StringBuilder();
							bcc.append(r.dEmailAddress);
						} else {
							bcc.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.From ) {
						if ( from == null ) {
							from = new StringBuilder();
							from.append(r.dEmailAddress);
						} else {
							from.append(";").append(r.dEmailAddress);
						}
					}
				}
				
			}
		}
		
		// Fill out the To field
		if ( to != null ) {
			this.zFillField(Field.To, to.toString());
		}
		
		if ( cc != null ) {
			this.zFillField(Field.Cc, cc.toString());
		}
		
		if ( bcc != null ) {
			this.zFillField(Field.Bcc, bcc.toString());
		}

		
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
		
		// Look for the div
		String locator = "css=div[id^='ztb__COMPOSE']";
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false);	
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 150, 80) ) {
			return (false);
		}
		
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}

}