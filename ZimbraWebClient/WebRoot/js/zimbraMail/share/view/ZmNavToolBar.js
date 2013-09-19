/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2012, 2013 Zimbra Software, LLC.
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
 * Creates a navigation tool bar.
 * @class
 * Navigation toolbar for the client. This toolbar is affected by every 
 * push/pop of a view and must be context sensitive since it can custom apply 
 * to any view. A new class was created since nav toolbar may be expanded in 
 * the future (i.e. to incl. a text input indicating current page, etc)
 *
 * @param {Hash}	params			a hash of parameters
 * @param {DwtComposite}	params.parent			the containing widget
 * @param {constant}	params.posStyle			the positioning style
 * @param {String}	params.className			the CSS class name
 * @param {Boolean}	params.hasText			if <code>true</code>, this toolbar includes text in the middle
 * @param {constant}	params.context			the view ID (used to generate button IDs)
 * 
 * @extends	ZmButtonToolBar
 */
ZmNavToolBar = function(params) {

	params.className = params.className || "ZmNavToolBar";
	var hasText = (params.hasText !== false);
	params.buttons = this._getButtons(hasText);
	params.toolbarType = ZmId.TB_NAV;
	params.posStyle = params.posStyle || DwtControl.STATIC_STYLE;
	ZmButtonToolBar.call(this, params);
	if (hasText) {
		this._textButton = this.getButton(ZmOperation.TEXT);
	}
};

ZmNavToolBar.prototype = new ZmButtonToolBar;
ZmNavToolBar.prototype.constructor = ZmNavToolBar;

ZmNavToolBar.prototype.toString = 
function() {
	return "ZmNavToolBar";
};

/**
 * Enables/disables buttons.
 *
 * @param {Array}	ids		a list of button IDs
 * @param {Boolean}	enabled	if <code>true</code>, enable the buttons
 * 
 */
ZmNavToolBar.prototype.enable =
function(ids, enabled) {
	ZmButtonToolBar.prototype.enable.call(this, ids, enabled);

	// 	also kill the tooltips if buttons are disabled
	if (!enabled) {
		if (!(ids instanceof Array))
			ids = [ids];
		for (var i = 0; i < ids.length; i++) {
			var button = this.getButton(ids[i]);
			if (button)
				button.setToolTipContent(null);
		}
	}
};

/**
 * Sets the tool tip for the button.
 * 
 * @param	{String}	buttonId		the button id
 * @param	{String}	tooltip			the tool tip
 */
ZmNavToolBar.prototype.setToolTip = 
function(buttonId, tooltip) {
	var button = this.getButton(buttonId);
	if (button)
		button.setToolTipContent(tooltip);
};

/**
 * Sets the text.
 * 
 * @param	{String}	text		the text
 */
ZmNavToolBar.prototype.setText =
function(text) {
	if (!this._textButton) return;
	this._textButton.setText(text);
};

ZmNavToolBar.prototype._getButtons = 
function(hasText) {

	var buttons = [];
	buttons.push(ZmOperation.PAGE_BACK);
	if (hasText) {
		buttons.push(ZmOperation.TEXT);
	}
	buttons.push(ZmOperation.PAGE_FORWARD);

	return buttons;
};

ZmNavToolBar.prototype.createOp =
function(id, params) {
	params.textClassName = "ZWidgetTitle ZmNavToolBarTitle";
	return ZmButtonToolBar.prototype.createOp.apply(this, arguments);
};
