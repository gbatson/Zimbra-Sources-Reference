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
package com.zimbra.qa.selenium.framework.util.performance;


/**
 * A list of perf metrics that can be measured.
 * See http://bugzilla.zimbra.com/show_bug.cgi?id=61972
 * See http://bugzilla.zimbra.com/attachment.cgi?id=33941
 * @author Matt Rhoades
 *
 */
public class PerfKey {

	public static final PerfKey ZmMailApp 			= new PerfKey("ZmMailApp", "ZmMailApp_launched", "ZmMailApp_loaded");
	public static final PerfKey ZmMailItem 			= new PerfKey("ZmMailItem", "ZmMailItem_loading", "ZmMailItem_loaded");
	public static final PerfKey ZmMailAppOverviewPanel 			= new PerfKey("ZmMailApp-overviewPanel", "ZmMailApp-overviewPanel_loading", "ZmMailApp-overviewPanel_loaded");
	public static final PerfKey ZmConv	 			= new PerfKey("ZmConv", "ZmConv_loading", "ZmConv_loaded");
	
	public static final PerfKey ZmMailAppCompose	= new PerfKey("ZmMailAppCompose", "ZmMailApp-compose_loading", "ZmMailApp-compose_loaded");
	
	
	
	public static final PerfKey ZmContactsApp 		= new PerfKey("ZmContactsApp", "ZmContactsApp_launched", "ZmContactsApp_loaded");
	public static final PerfKey ZmContactsItem 		= new PerfKey("ZmContactsItem", "ZmContactItem_loading", "ZmContactItem_loaded");
	public static final PerfKey ZmContactsAppOverviewPanel 			= new PerfKey("ZmContactsApp-overviewPanel", "ZmContactsApp-overviewPanel_loading", "ZmContactsApp-overviewPanel_loaded");

	public static final PerfKey ZmCalendarApp 		= new PerfKey("ZmCalendarApp", "ZmCalendarApp_launched", "ZmCalendarApp_loaded");
	public static final PerfKey ZmCalWorkWeekView 	= new PerfKey("ZmCalWorkWeekView", "ZmCalWorkWeekView_loading", "ZmCalWorkWeekView_loaded");
	public static final PerfKey ZmCalItemView 		= new PerfKey("ZmCalItemView", "ZmCalItemView_loading", "ZmCalItemView_loaded");
	public static final PerfKey ZmCalViewItem 		= new PerfKey("ZmCalViewItem", "ZmCalViewItem_loading", "ZmCalViewItem_loaded");
	public static final PerfKey ZmCalendarAppOverviewPanel 			= new PerfKey("ZmCalendarApp-overviewPanel", "ZmCalendarApp-overviewPanel_loading", "ZmCalendarApp-overviewPanel_loaded");

	public static final PerfKey ZmTasksApp 			= new PerfKey("ZmTasksApp", "ZmTasksApp_launched", "ZmTasksApp_loaded");
	public static final PerfKey ZmTaskItem 			= new PerfKey("ZmTaskItem", "ZmTaskItem_loading", "ZmTaskItem_loaded");
	public static final PerfKey ZmTasksAppOverviewPanel 			= new PerfKey("ZmTasksApp-overviewPanel", "ZmTasksApp-overviewPanel_loading", "ZmTasksApp-overviewPanel_loaded");

	public static final PerfKey ZmBriefcaseApp 		= new PerfKey("ZmBriefcaseApp", "ZmBriefcaseApp_launched", "ZmBriefcaseApp_loaded");
	public static final PerfKey ZmBriefcaseItem 	= new PerfKey("ZmBriefcaseItem", "ZmBriefcaseItem_loading", "ZmBriefcaseItem_loaded");
	public static final PerfKey ZmBriefcaseAppOverviewPanel 		= new PerfKey("ZmBriefcaseApp-overviewPanel", "ZmBriefcaseApp-overviewPanel_loading", "ZmBriefcaseApp-overviewPanel_loaded");

	public static final PerfKey ZmPreferencesAppOverviewPanel 		= new PerfKey("ZmContactsApp-overviewPanel", "ZmPreferencesApp-overviewPanel_loading", "ZmPreferencesApp-overviewPanel_loaded");

	protected String Key;
	protected String LaunchKey;
	protected String FinishKey;
	
	public PerfKey(String key, String launch, String finish) {
		Key = key;
		LaunchKey = launch;
		FinishKey = finish; 
	}
	
	public String toString() {
		return (Key);
	}

}
