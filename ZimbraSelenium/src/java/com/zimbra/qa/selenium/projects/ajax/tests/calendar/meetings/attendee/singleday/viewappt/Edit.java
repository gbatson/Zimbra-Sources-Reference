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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.attendee.singleday.viewappt;

import java.util.*;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;

public class Edit extends CalendarWorkWeekTest {

	public Edit() {
		logger.info("New "+ Edit.class.getCanonicalName());
		super.startingPage =  app.zPageCalendar;
		super.startingAccountPreferences = null;
	}
	
	@Test(description = "View meeting invite by opening it, Edit the invitation and locally save it", 
			groups = { "functional" })
			
	public void EditMeeting_01() throws HarnessException {

		// ------------------------ Test data ------------------------------------
		
		String organizer;
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		String newSubject = ZimbraSeleniumProperties.getUniqueString();
		String newContent = ZimbraSeleniumProperties.getUniqueString();
		String organizerEmailAddress = app.zGetActiveAccount().EmailAddress;
		String attendee1EmailAddress = ZimbraAccount.AccountA().EmailAddress;
		String attendee2EmailAddress = ZimbraAccount.AccountB().EmailAddress;
		
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 8, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 9, 0, 0);

		// --------------- Creating invitation (organizer) ----------------------------

		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ organizerEmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>" + apptContent + "</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");        
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// --------------- Login to attendee & open the invitation ----------------------------------------------------
		DialogWarning dialog = (DialogWarning)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, Button.O_EDIT, apptSubject);
		dialog.zClickButton(Button.B_OK);
		
		FormApptNew form = new FormApptNew(app);
        form.zFillField(Field.Subject, newSubject);
        form.zFillField(Field.Body, newContent);
        form.zFillField(Field.Attendees, attendee2EmailAddress);
        form.zSubmit();
        SleepUtil.sleepMedium();
		
		// Verify that new invitation doesn't appear in attendee2's inbox because it is locally saved appointment
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>" + "subject:(" + newSubject + ")" + " " + "content:(" + newContent +")" + "</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		ZAssert.assertNull(id, "Verify the new invitation doesn't appear in attendee2's inbox");
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>subject:("+ newSubject +")" + " " + "content:(" + newContent +")</query>"
			+	"</SearchRequest>");
		id = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");
		ZAssert.assertNotNull(id, "Verify saved meeting invite present");
		
		// Verify organizer for the locally saved appointment
		organizer = app.zGetActiveAccount().soapSelectValue("//mail:appt/mail:or", "a");
		ZAssert.assertEquals(organizer, attendee1EmailAddress, "Verify organizer for the saved appointment");
		
	}
	
}