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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.week.singleday;

import java.util.Calendar;
import java.util.HashMap;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class CreateAppointment extends CalendarWorkWeekTest {

	public CreateAppointment() {
		logger.info("New "+ CreateAppointment.class.getCanonicalName());
		
		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with week view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "week");
		}};

	}

	
	@Test(	description = "Create a basic appointment without an attendee in week view",
			groups = { "smoke" }
	)
	public void CreateAppointment_01() throws HarnessException {
		
		// Create appointment
		AppointmentItem appt = new AppointmentItem();
		Calendar now = this.calendarWeekDayUTC;
		appt.setSubject("appointment" + ZimbraSeleniumProperties.getUniqueString());
		appt.setContent("content" + ZimbraSeleniumProperties.getUniqueString());
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
	
		// Open the new mail form
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(apptForm, "Verify the new form opened");

		// Fill out the form with the data
		apptForm.zFill(appt);

		// Send the message
		apptForm.zSubmit();
			
		// Verify the new appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
	}
	
	@Test(	description = "Create appointment with all the fields and verify it",
			groups = { "functional" }
	)
	public void CreateAppointment_02() throws HarnessException {
		
		// Create appointment data
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		ZimbraResource equipment = new ZimbraResource(ZimbraResource.Type.EQUIPMENT);
		
		AppointmentItem appt = new AppointmentItem();
		
		String apptSubject, apptAttendee1, apptOptional1, apptLocation1, apptEquipment1, apptContent;
		Calendar now = this.calendarWeekDayUTC;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		apptOptional1 = ZimbraAccount.AccountB().EmailAddress;
		apptLocation1 = location.EmailAddress;
		apptEquipment1 = equipment.EmailAddress;
		apptContent = ZimbraSeleniumProperties.getUniqueString();
		
		appt.setSubject(apptSubject);
		appt.setAttendees(apptAttendee1);
		appt.setOptional(apptOptional1);
		appt.setLocation(apptLocation1);
		appt.setEquipment(apptEquipment1);
		appt.setStartTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0));
		appt.setEndTime(new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0));
		appt.setContent(apptContent);
	
		// Compose appointment and send it to invitee
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		apptForm.zFill(appt);
		apptForm.zSubmit();
			
		// Verify appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getAttendees(), appt.getAttendees(), "Attendees: Verify the appointment data");
		ZAssert.assertEquals(actual.getOptional(), appt.getOptional(), "Optional: Verify the appointment data");
		ZAssert.assertEquals(actual.getLocation(), appt.getLocation(), "Location: Verify the appointment data");
		ZAssert.assertEquals(actual.getEquipment(), appt.getEquipment(), "Equipment: Verify the appointment data");
		ZAssert.assertEquals(actual.getContent(), appt.getContent(), "Content: Verify the appointment data");
	}
	
	@Test(	description = "Create private appointment in week view",
			groups = { "functional" }
	)
	public void CreatePrivateAppointment_03() throws HarnessException {
		
		// Create appointment
		String apptSubject;
		apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem appt = new AppointmentItem();
		
		appt.setSubject(apptSubject);
		appt.setContent("content" + ZimbraSeleniumProperties.getUniqueString());
		appt.setAttendees(ZimbraAccount.AccountA().EmailAddress);
		appt.setIsPrivate(true);
	
		// Open the new mail form
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(apptForm, "Verify the new form opened");

		// Fill the data and submit it
		apptForm.zFill(appt);
		apptForm.zSubmit();
			
		// Verify private appointment exists on the server
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")");
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");
		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");
		ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetAppointmentResponse//mail:comp", "class", "PRI"), true, "");
	}
	
}
