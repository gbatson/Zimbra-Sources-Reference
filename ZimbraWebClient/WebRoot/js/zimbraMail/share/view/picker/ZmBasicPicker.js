/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * @overview
 */

/**
 * Creates a basic picker control.
 * @class
 * This class represents a basic picker control.
 * 
 * @param		{DwtControl}	parent		the parent
 * 
 * @extends		ZmPicker
 * @see			ZmPicker.BASIC
 */
ZmBasicPicker = function(parent) {

	ZmPicker.call(this, parent, ZmPicker.BASIC);
	
	var settings = appCtxt.getSettings();
	var listener = new AjxListener(this, this._settingChangeListener);
	settings.getSetting(ZmSetting.SEARCH_INCLUDES_SPAM).addChangeListener(listener);
	settings.getSetting(ZmSetting.SEARCH_INCLUDES_TRASH).addChangeListener(listener);
};

ZmBasicPicker.prototype = new ZmPicker;
ZmBasicPicker.prototype.constructor = ZmBasicPicker;

ZmPicker.CTOR[ZmPicker.BASIC] = ZmBasicPicker;

ZmBasicPicker.prototype.toString = 
function() {
	return "ZmBasicPicker";
};

ZmBasicPicker.prototype._makeRow =
function(text, id) {
    var size = 17;
    var html = [];
    var i = 0;
    html[i++] = "<tr valign='middle'>";
    html[i++] = "<td align='right' nowrap>";
	html[i++] = AjxMessageFormat.format(ZmMsg.makeLabel, text);
	html[i++] = "</td>";
    html[i++] = "<td align='left' nowrap><input type='text' autocomplete='off' nowrap size='";
	html[i++] = size;
	html[i++] = "' id='";
	html[i++] = id;
	html[i++] = "'/></td>";
    html[i++] = "</tr>";

	return html.join("");
};

// TODO: if we really wanted, we could add a prefs listener to update the "also search" checkboxes
ZmBasicPicker.prototype._setupPicker =
function(parent) {
    var picker = new DwtComposite(parent);

    var fromId = Dwt.getNextId();
    var toId = Dwt.getNextId();
    var subjectId = Dwt.getNextId();
    var contentId = Dwt.getNextId();
    var inTrashId = Dwt.getNextId();
    var inSpamId, checked;
    
	var html = [];
	var i = 0;
	html[i++] = "<table cellpadding='5' cellspacing='0' border='0' width='100%'>";
	html[i++] = this._makeRow(ZmMsg.from, fromId);
	html[i++] = this._makeRow(ZmMsg.toCc, toId);
	html[i++] = this._makeRow(ZmMsg.subject, subjectId);
	html[i++] = this._makeRow(ZmMsg.content, contentId);
	html[i++] = "</table>";
    html[i++] = "<div style='overflow:hidden; padding-left: 20px;'>";
	if (appCtxt.get(ZmSetting.SPAM_ENABLED)) {
		inSpamId = Dwt.getNextId();
		checked = appCtxt.get(ZmSetting.SEARCH_INCLUDES_SPAM) ? " checked" : "";
		/*html[i++] = "<tr valign='middle'>";*/
		html[i++] = "<span><input type='checkbox'";
		html[i++] = checked;
		html[i++] = " id='";
		html[i++] = inSpamId;
		html[i++] = "' /></span>";
		html[i++] = "<span>";
		html[i++] = ZmMsg.includeJunk;
		html[i++] = "</span>";
		html[i++] = "<br>";

    }
	checked = appCtxt.get(ZmSetting.SEARCH_INCLUDES_TRASH) ? " checked" : "";
	html[i++] = "<span><input type='checkbox'";
	html[i++] = checked;
	html[i++] = " id='";
	html[i++] = inTrashId;
	html[i++] = "' /></span>";
	html[i++] = "<span>";
	html[i++] = ZmMsg.includeTrash;
	html[i++] = "</span>";
	html[i++] = "</div>";
	picker.getHtmlElement().innerHTML = html.join("");

	this._from = this._setupField(fromId);
	this._to = this._setupField(toId);
	this._subject= this._setupField(subjectId);
	this._content = this._setupField(contentId);
	if (appCtxt.get(ZmSetting.SPAM_ENABLED)) {
		this._inSpam = this._setupCheckbox(inSpamId);
	}
	this._inTrash = this._setupCheckbox(inTrashId);
};

/**
 * Sets the "from" value of the query.
 * 
 * @param	{String}	from		the from value
 */
ZmBasicPicker.prototype.setFrom =
function(from) {
	this._from.value = from;
	this._updateQuery();
};

/**
 * Sets the "to" value of the query.
 * 
 * @param	{String}	from		the to value
 */
ZmBasicPicker.prototype.setTo =
function(to) {
	this._to.value = to;
	this._updateQuery();
};

/**
 * Sets the "subject" value of the query.
 * 
 * @param	{String}	from		the subject value
 */
ZmBasicPicker.prototype.setSubject =
function(subject) {
	this._subject.value = subject;
	this._updateQuery();
};

/**
 * Sets the "content" value of the query.
 * 
 * @param	{String}	from		the content value
 */
ZmBasicPicker.prototype.setContent =
function(content) {
	this._content.value = content;
	this._updateQuery();
};

ZmBasicPicker.prototype._setupField = 
function(id) {
	var f = document.getElementById(id);
	Dwt.setHandler(f, DwtEvent.ONKEYUP, ZmBasicPicker._onChange);
	Dwt.associateElementWithObject(f, this);
	return f;
};

ZmBasicPicker.prototype._setupCheckbox = 
function(id) {
	var f = document.getElementById(id);
	Dwt.setHandler(f, DwtEvent.ONCLICK, ZmBasicPicker._onChange);
	Dwt.associateElementWithObject(f, this);
	return f;
};

ZmBasicPicker._onChange =
function(ev) {
	var element = DwtUiEvent.getTarget(ev);
	var picker = Dwt.getObjectFromElement(element);

	var charCode = DwtKeyEvent.getCharCode(ev);
	if (charCode == 13 || charCode == 3 || charCode == 9) {
		picker.execute();
	    return false;
	} else {
		picker._updateQuery();
		return true;
	}
};

ZmBasicPicker.prototype._updateQuery = 
function() {
	var query1 = [];
	var from = AjxStringUtil.trim(this._from.value, true);
	if (from.length)
		query1.push("from:(" + from + ")");
	var to = AjxStringUtil.trim(this._to.value, true);
	if (to.length)
		query1.push("(to:(" + to + ")" + " OR cc:(" + to + "))");
	var subject = AjxStringUtil.trim(this._subject.value, true);
	if (subject.length)
		query1.push("subject:(" + subject + ")");
	var content = AjxStringUtil.trim(this._content.value, true);
	if (content.length)
		query1.push("content:(" + content + ")");
	
	// Sort out "Check Trash/Spam" pref vs checkbox
	var query2 = [];
	var checkSpamPref = appCtxt.get(ZmSetting.SEARCH_INCLUDES_SPAM);
	var checkTrashPref = appCtxt.get(ZmSetting.SEARCH_INCLUDES_TRASH);
	var checkSpamCheckbox = (this._inSpam && this._inSpam.checked);
	var checkTrashCheckbox = (this._inTrash && this._inTrash.checked);
	
	if ((!checkSpamPref && checkSpamCheckbox) && (!checkTrashPref && checkTrashCheckbox)) {
		query2.push("is:anywhere");
	} else {
		if (!checkSpamPref && checkSpamCheckbox)
			query2.push("is:anywhere not in:trash");
		else if (checkTrashPref && !checkTrashCheckbox)
			query2.push("not in:trash");
		if (!checkTrashPref && checkTrashCheckbox)
			query2.push("is:anywhere not in:junk");
		else if (checkSpamPref && !checkSpamCheckbox)
			query2.push("not in:junk");
	}
	
	var cbQuery = query2.length ? query2.join(" ") : null;

	// if the "search Trash/Spam" checkboxes changed, run the query
	var cbChange = (query1.length && ((this._cbQuery || cbQuery) && (this._cbQuery != cbQuery)));
	var query = query1.concat(query2);
	this.setQuery(query.length ? query.join(" ") : "");
	if (cbChange) {
		this.execute();
	}
	this._cbQuery = cbQuery;
};

ZmBasicPicker.prototype._settingChangeListener =
function(ev) {
	if (ev.type != ZmEvent.S_SETTING) return;
	
	var setting = ev.source;
	if (setting.id == ZmSetting.SEARCH_INCLUDES_SPAM) {
		this._inSpam.checked = appCtxt.get(ZmSetting.SEARCH_INCLUDES_SPAM);
		this._updateQuery();
	} else	if (setting.id == ZmSetting.SEARCH_INCLUDES_TRASH) {
		this._inTrash.checked = appCtxt.get(ZmSetting.SEARCH_INCLUDES_TRASH);
		this._updateQuery();
	}
};
