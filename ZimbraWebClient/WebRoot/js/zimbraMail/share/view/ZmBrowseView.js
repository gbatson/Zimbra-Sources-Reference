/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
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
 * Creates the browse view.
 * @class
 * This class represents the browse view.
 * 
 * @param	{DwtComposite}		parent		the parent
 * @param	{AjxVector}		pickers		the pickers (not used)
 * 
 * @extends		DwtComposite
 */
ZmBrowseView = function(parent, pickers) {

	DwtComposite.call(this, {parent:parent, className:"ZmBrowseView", posStyle:DwtControl.ABSOLUTE_STYLE});

	this.setScrollStyle(DwtControl.SCROLL);
	this.addControlListener(new AjxListener(this, this._controlListener));
	this._pickers = new AjxVector();
}

ZmBrowseView.prototype = new DwtComposite;
ZmBrowseView.prototype.constructor = ZmBrowseView;

ZmBrowseView.prototype.toString = 
function() {
	return "ZmBrowseView";
}

/**
 * Gets the toolbar.
 * 
 * @return	{ZmToolBar}		the tool bar
 */
ZmBrowseView.prototype.getToolBar = 
function() {
	return this._toolbar;
}

/**
 * Adds the picker.
 * 
 * @param	{ZmPicker}		picker		the picker
 * @param	{String}	id		the id (not used)
 * 
 */
ZmBrowseView.prototype.addPicker =
function(picker, id) {
    this._pickers.add(picker);
    this.layout();
}

/**
 * Gets the pickers.
 * 
 * @return	{AxjVector}	 a list of {@link ZmPicker} objects
 */
ZmBrowseView.prototype.getPickers = 
function() {
	return this._pickers;
}

/**
 * Removes the picker.
 * 
 * @param	{ZmPicker}	picker		the picker
 */
ZmBrowseView.prototype.removePicker =
function(picker) {
	var p = this._pickers;
	if (p.size() == 0)
		return;
	if (p.remove(picker)) {
		picker.dispose();
	    this.layout();
    }
}

/**
 * Removes all pickers.
 * 
 */
ZmBrowseView.prototype.removeAllPickers =
function() {
	var p = this._pickers;
	while (p.size() > 0) {
		var picker = p.getLast();
		picker.dispose();
	    p.removeLast();
    }
}

/**
 * Performs a layout of the view.
 * 
 * @return	{ZmBrowseView}	the resulting view
 */
ZmBrowseView.prototype.layout =
function() {
	if (!this.getVisible())
		return;
	var p = this._pickers;
	var i, x;
	var sz = p.size();
	for (i = 0; i < sz; i++) {
		x = (i == 0) ? 0 : p.get(i - 1).getXW();
		var picker = p.get(i);
		picker.setBounds(x, 0, Dwt.DEFAULT, this.getH());
	}
	return this;
}

ZmBrowseView.prototype._controlListener =
function(ev) {
	this.layout();
}
