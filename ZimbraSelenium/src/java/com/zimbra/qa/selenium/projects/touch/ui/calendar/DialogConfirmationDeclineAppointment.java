/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013, 2014 Zimbra, Inc.
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
package com.zimbra.qa.selenium.projects.touch.ui.calendar;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class DialogConfirmationDeclineAppointment extends DialogWarning {

	public static class Locators {

		public static final String LocatorDivID = "CONFIRM_DELETE_APPT_DIALOG";
		public static final String LocatorDivCSS = "css=div#CONFIRM_DELETE_APPT_DIALOG";

		public static final String DontNotifyOrganizerRadioButton = "css=div[id='CONFIRM_DELETE_APPT_DIALOG_content'] td label:contains('Don't notify organizer')";
		public static final String NotifyOrganizerRadioButton = "css=div[id='CONFIRM_DELETE_APPT_DIALOG_content'] td label:contains('Notify organizer')";

	}

	public DialogConfirmationDeclineAppointment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(Locators.LocatorDivID), application, page);

		logger.info("new " + DialogConfirmationDeclineAppointment.class.getCanonicalName());
	}

	
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");
		
		
		AbsPage page = null;
		String locator = null;
		
		if (button == Button.B_DONT_NOTIFY_ORGANIZER) {

			locator = Locators.DontNotifyOrganizerRadioButton;

			sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (page);

		} else if (button == Button.B_NOTIFY_ORGANIZER) {

			locator = Locators.NotifyOrganizerRadioButton;

			sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (page);
		
		}
		
		return ( super.zClickButton(button) );
		
	}

}
