/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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
 * Creates a time picker control.
 * @class
 * This class represents a time picker control.
 * 
 * @param		{DwtControl}	parent		the parent
 * 
 * @extends		ZmPicker
 * @see			ZmPicker.TIME
 */
ZmTimePicker = function(parent) {

	ZmPicker.call(this, parent, ZmPicker.TIME);

    this._checkedItems = new Object();
}

ZmPicker.CTOR[ZmPicker.TIME] = ZmTimePicker;

ZmTimePicker._onClick =
function(ev) {
	var element = DwtUiEvent.getTarget(ev);
	var picker = element._picker;
	if (element.checked)
		picker._checkedItems[element._query] = true;
	else
		delete picker._checkedItems[element._query];
	picker._updateQuery();
}

ZmTimePicker.prototype = new ZmPicker;
ZmTimePicker.prototype.constructor = ZmTimePicker;

ZmTimePicker.prototype.toString = 
function() {
	return "ZmTimePicker";
}

ZmTimePicker.prototype._makeRow =
function(left, leftId, right, rightId) {
    var size = 20;
    var html = new Array(10);
    var i = 0;
    html[i++] = "<tr valign='middle'>";
    html[i++] = "<td align='left' nowrap><input id='" + leftId + "' type='checkbox' value='" + left + "'></input></td>";
    html[i++] = "<td align='left' nowrap>" + left + "</td>";
    html[i++] = "<td align='left' nowrap><input id='" + rightId + "' type='checkbox'></input></td>";
    html[i++] = "<td align='left' nowrap>" + right + "</td>";
    html[i++] = "</tr>";
	return html.join("");
}

ZmTimePicker.prototype._setupPicker =
function(parent) {
    var picker = new DwtComposite(parent);

    var lastHourId = Dwt.getNextId();
    var last4HoursId = Dwt.getNextId();
    var todayId = Dwt.getNextId();
    var yesterdayId = Dwt.getNextId();
    var thisWeekId = Dwt.getNextId();
    var lastWeekId = Dwt.getNextId();
    var thisMonthId = Dwt.getNextId();
    var lastMonthId = Dwt.getNextId();
    var thisYearId = Dwt.getNextId();
    var lastYearId = Dwt.getNextId();

	var html = new Array(10);
	var i = 0;
	html[i++] = "<table cellpadding='2' cellspacing='0' border='0'>";
	html[i++] = this._makeRow(ZmMsg.P_TIME_LAST_HOUR, lastHourId, ZmMsg.P_TIME_LAST_4_HOURS, last4HoursId);
	html[i++] = this._makeRow(ZmMsg.P_TIME_TODAY, todayId, ZmMsg.P_TIME_YESTERDAY, yesterdayId);
	html[i++] = this._makeRow(ZmMsg.P_TIME_THIS_WEEK, thisWeekId, ZmMsg.P_TIME_LAST_WEEK, lastWeekId);
	html[i++] = this._makeRow(ZmMsg.P_TIME_THIS_MONTH, thisMonthId, ZmMsg.P_TIME_LAST_MONTH, lastMonthId);
	html[i++] = this._makeRow(ZmMsg.P_TIME_THIS_YEAR, thisYearId, ZmMsg.P_TIME_LAST_YEAR, lastYearId);
	html[i++] = "</table>";
	picker.getHtmlElement().innerHTML = html.join("");

	this._installOnClick(lastHourId, "date:>-60min");
	this._installOnClick(last4HoursId, "date:>-240min");
	this._installOnClick(todayId, "date:-0day");
	this._installOnClick(yesterdayId, "date:yesterday");
	this._installOnClick(thisWeekId, "date:-0week");
	this._installOnClick(lastWeekId, "date:-1week");
	this._installOnClick(thisMonthId, "date:-0month");
	this._installOnClick(lastMonthId,"date:-1month"); 
	this._installOnClick(thisYearId, "date:-0year"); 
	this._installOnClick(lastYearId, "date:-1year"); 
}

ZmTimePicker.prototype._installOnClick =
function(id, query) {
	var box = document.getElementById(id);
	Dwt.setHandler(box, DwtEvent.ONCLICK, ZmTimePicker._onClick);
	box._query = query;
	box._picker = this;
}

ZmTimePicker.prototype._updateQuery = 
function() {
	var query = new Array();
	for (var q in this._checkedItems)
		query.push(q);
	var str = "";
	if (query.length > 1)
		str += "(";
	str += query.join(" OR ");
	if (query.length > 1)
		str += ")";
	this.setQuery(str);
	this.execute();
}
