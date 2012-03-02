/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
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
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.html2;

import com.zimbra.qa.selenium.projects.html2.clients.Appointment;
import com.zimbra.qa.selenium.projects.html2.clients.BriefcaseItem;
import com.zimbra.qa.selenium.projects.html2.clients.BrowseFileField;
import com.zimbra.qa.selenium.projects.html2.clients.Button;
import com.zimbra.qa.selenium.projects.html2.clients.ButtonMenu;
import com.zimbra.qa.selenium.projects.html2.clients.CalendarFolder;
import com.zimbra.qa.selenium.projects.html2.clients.CalendarGrid;
import com.zimbra.qa.selenium.projects.html2.clients.CheckBox;
import com.zimbra.qa.selenium.projects.html2.clients.ContactListItem;
import com.zimbra.qa.selenium.projects.html2.clients.Dialog;
import com.zimbra.qa.selenium.projects.html2.clients.DocumentPage;
import com.zimbra.qa.selenium.projects.html2.clients.Editfield;
import com.zimbra.qa.selenium.projects.html2.clients.Editor;
import com.zimbra.qa.selenium.projects.html2.clients.FeatureMenu;
import com.zimbra.qa.selenium.projects.html2.clients.Folder;
import com.zimbra.qa.selenium.projects.html2.clients.HtmlMenu;
import com.zimbra.qa.selenium.projects.html2.clients.ListItem;
import com.zimbra.qa.selenium.projects.html2.clients.MenuItem;
import com.zimbra.qa.selenium.projects.html2.clients.MessageItem;
import com.zimbra.qa.selenium.projects.html2.clients.MiscObject;
import com.zimbra.qa.selenium.projects.html2.clients.PwdField;
import com.zimbra.qa.selenium.projects.html2.clients.RadioBtn;
import com.zimbra.qa.selenium.projects.html2.clients.Tab;
import com.zimbra.qa.selenium.projects.html2.clients.TaskFolder;
import com.zimbra.qa.selenium.projects.html2.clients.TaskItem;
import com.zimbra.qa.selenium.projects.html2.clients.TextArea;
import com.zimbra.qa.selenium.projects.html2.clients.ToastAlertMessage;

public class CoreObjects {
	public static Button zButton = new Button();
	public static FeatureMenu zFeatureMenu = new FeatureMenu();
	public static Appointment zAppointment = new Appointment();
	public static DocumentPage zDocumentPage = new DocumentPage();
	public static Folder zFolder = new Folder();
	public static ButtonMenu zButtonMenu = new ButtonMenu();
	public static MenuItem zMenuItem = new MenuItem();
	public static Dialog zDialog = new Dialog();
	public static MessageItem zMessageItem = new MessageItem();
	public static BriefcaseItem zBriefcaseItem = new BriefcaseItem();
	public static TaskItem zTaskItem = new TaskItem();
	public static TaskFolder zTaskFolder = new TaskFolder();
	public static Tab zTab =  new Tab();
	public static Editfield zEditField = new Editfield();
	public static TextArea zTextAreaField = new TextArea();
	public static Editor zEditor = new Editor();
	public static PwdField zPwdField = new PwdField();
	public static CheckBox zCheckbox = new CheckBox();
	public static RadioBtn zRadioBtn = new RadioBtn();
	public static BrowseFileField zBrowseField = new BrowseFileField();
	public static ContactListItem zContactListItem = new ContactListItem();
	public static ToastAlertMessage zToastAlertMessage = new ToastAlertMessage();
	public static CalendarFolder zCalendarFolder = new CalendarFolder();
	public static ListItem zListItem = new ListItem();
	public static MiscObject zMiscObj = new MiscObject();
	public static CalendarGrid zCalendarGrid = new CalendarGrid();
	public static HtmlMenu zHtmlMenu = new HtmlMenu();
}
