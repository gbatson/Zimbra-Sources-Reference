/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
 * Creates a custom picker control.
 * @class
 * This class represents a custom picker control.
 * 
 * @param		{DwtControl}	parent		the parent
 * 
 * @extends		ZmPicker
 * @see			ZmPicker.CUSTOM
 */
ZmCustomPicker = function(parent) {

	ZmPicker.call(this, parent);

    this.setTitle(ZmMsg.search);
    this.setImage("Search");
}

ZmCustomPicker.prototype = new ZmPicker;
ZmCustomPicker.prototype.constructor = ZmCustomPicker;

ZmPicker.CTOR[ZmPicker.CUSTOM] = ZmCustomPicker;

ZmCustomPicker.prototype.toString = 
function() {
	return "ZmCustomPicker";
}

ZmCustomPicker.prototype._setupPicker =
function(parent) {
	var picker = new DwtComposite(parent);
	var size = 24;
	var fieldId = Dwt.getNextId();
	var html = new Array(20);
	var i = 0;
	html[i++] = "<p>" + ZmMsg.addSearch + "</p>";
	html[i++] = "<center><table cellpadding='2' cellspacing='0' border='0'>";
	html[i++] = "<tr valign='middle'>";
	html[i++] = "<td align='right' nowrap>" + ZmMsg.searchLabel + "</td>";
	html[i++] = "<td align='left' nowrap><input type='text' autocomplete='off' nowrap size='" + size + "' id='" + fieldId + "'/></td>";
	html[i++] = "</tr>";
	html[i++] = "</table></center>";
	picker.getHtmlElement().innerHTML = html.join("");

	var field = this._field = document.getElementById(fieldId);
	Dwt.setHandler(field, DwtEvent.ONCHANGE, ZmCustomPicker._onChange);
	field._picker = this;
}

ZmCustomPicker._onChange = 
function(ev) {
	var element = DwtUiEvent.getTarget(ev);
	var picker = element._picker;
	var charCode = DwtKeyEvent.getCharCode(ev);
	if (charCode == 13 || charCode == 3) {
		picker.execute();
	    return false;
	} else {
		picker._updateQuery();
		return true;
	}	
}

ZmCustomPicker.prototype._updateQuery = 
function() {
	var val = this._field.value;
	if (val)
		this.setQuery("(" + this._field.value + ")");
	else
		this.setQuery("");
}
