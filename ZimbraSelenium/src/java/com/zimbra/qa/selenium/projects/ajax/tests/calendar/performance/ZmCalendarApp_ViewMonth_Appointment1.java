/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.performance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class ZmCalendarApp_ViewMonth_Appointment1 extends AjaxCommonTest {

	
	public ZmCalendarApp_ViewMonth_Appointment1() throws HarnessException {
		logger.info("New "+ ZmCalendarApp_ViewMonth_Appointment1.class.getCanonicalName());
		
		super.startingPage = app.zPageMail;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 3038458962443347843L;
			{
				put("zimbraPrefCalendarInitialView", "month");
			}};


	}
	
	
	@Test(	description = "Measure the time to load the Calendar, month view, initial load",
			groups = { "performance" })
	public void ZmCalendarApp_01() throws HarnessException {
		
		// Create an appointment
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem.createAppointmentSingleDay(
				app.zGetActiveAccount(),
				Calendar.getInstance(),
				60,
				null,
				subject,
				"content" + ZimbraSeleniumProperties.getUniqueString(),
				"location" + ZimbraSeleniumProperties.getUniqueString(),
				null);


		// Start the perf token
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarApp, "Load the calendar app, month view, initial load");

		// Go to calendar
		//app.zPageCalendar.zNavigateTo();
		app.zPageCalendar.zClickAt("css=td[id='zb__App__Calendar_title']","");

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();
		

	}


	@Test(	description = "Measure the time to load the Calendar, month view, 1 appointment",
			groups = { "performance" })
	public void ZmCalendarApp_02() throws HarnessException {
		
		// Create an appointment
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem.createAppointmentSingleDay(
				app.zGetActiveAccount(),
				Calendar.getInstance(),
				60,
				null,
				subject,
				"content" + ZimbraSeleniumProperties.getUniqueString(),
				"location" + ZimbraSeleniumProperties.getUniqueString(),
				null);


		// Start the perf token
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarApp, "Load the calendar app, month view, 1 appointment");

		// Go to calendar
		//app.zPageCalendar.zNavigateTo();
		app.zPageCalendar.zClickAt("css=td[id='zb__App__Calendar_title']","");

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();
		

	}

	@Test(	description = "Measure the time to load the Calendar, month view, 100 appointment",
			groups = { "performance" })
	public void ZmCalendarApp_03() throws HarnessException {
		
		// What is today?
		Calendar today = Calendar.getInstance();

		// Import 100 appointments using Calendar.ics and REST
		String filename = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/ics/calendar05/Calendar.ics";
		File file = null;

		// Modify the ICS in two ways:
		// 1. Make the current account the organizer
		// 2. Make the dates equal to this month
		file = RestUtil.FileUtils.replaceInFile("user@domain.com", app.zGetActiveAccount().EmailAddress, new File(filename));
		file = RestUtil.FileUtils.replaceInFile("201111", (new SimpleDateFormat("yyyyMM")).format(today.getTime()), file);

		RestUtil rest = new RestUtil();
		rest.setAuthentication(app.zGetActiveAccount());
		rest.setPath("/service/home/~/Calendar");
		rest.setQueryParameter("fmt", "ics");
		rest.setUploadFile(file);
		rest.doPost();


		// Start the perf token
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarApp, "Load the calendar app, month view, 100 appointment");

		// Go to calendar
		//app.zPageCalendar.zNavigateTo();
		app.zPageCalendar.zClickAt("css=td[id='zb__App__Calendar_title']","");

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();
		

	}


}