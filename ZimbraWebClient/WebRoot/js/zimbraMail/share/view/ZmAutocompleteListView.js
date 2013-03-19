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
 * 
 */

/**
 * Creates a new autocomplete list. The list isn't populated or displayed until some
 * autocompletion happens. Takes a data class and loader, so that when data is needed (it's
 * loaded lazily), the loader can be called on the data class.
 * @class
 * This class implements autocomplete functionality. It has two main parts: matching data based
 * on keystroke events, and displaying/managing the list of matches. This class is theoretically
 * neutral concerning the data that gets matched (as long as its class has an <code>autocompleteMatch()</code>
 * method), and the field that it's being called from.
 * <p>
 * The data class's <code>autocompleteMatch()</code> method should returns a list of matches, where each match is
 * an object with the following properties:</p>
 * <table border="1" width="50%">
 * <tr><td width="15%">data</td><td>the object being matched</td></tr>
 * <tr><td>text</td><td>the text to display for this object in the list</td></tr>
 * <tr><td>[key1]</td><td>a string that may be used to replace the typed text</td></tr>
 * <tr><td>[keyN]</td><td>a string that may be used to replace the typed text</td></tr>
 * </table>
 * </p><p>
 * The calling client also specifies the key in the match result for the string that will be used
 * to replace the typed text (also called the "completion string"). For example, the completion 
 * string for matching contacts could be a full address, or just the email.
 * </p><p>
 * The client may provide additional key event handlers in the form of callbacks. If the callback
 * explicitly returns true or false, that's what the event handler will return.
 * </p>
 * 
 * @author Conrad Damon
 *
 * @param {Hash}	params			a hash of parameters:
 * @param	{String}		matchValue			the name of field in match result to use for completion
 * @param	{function}		dataClass			the class that has the data loader
 * @param	{function}		dataLoader			a method of dataClass that returns data to match against
 * @param	{DwtComposite}	parent				the control that created this list (defaults to shell)
 * @param	{String}		className			the CSS class
 * @param	{Array}			delims				the list of delimiters (which separate tokens such as addresses)
 * @param	{Array}			delimCodes			the list of delimiter key codes
 * @param	{String}		separator			the separator (gets added to the end of a match)
 * @param	{AjxCallback}	compCallback		the callback into client to notify it that completion happened
 * @param	{AjxCallback}	keyDownCallback		the additional client ONKEYDOWN handler
 * @param	{AjxCallback}	keyPressCallback	the additional client ONKEYPRESS handler
 * @param	{AjxCallback}	keyUpCallback		the additional client ONKEYUP handler
 * @param	{AjxCallback}	enterCallback		the client handler for Enter key
 * @param	{Hash}			options				the additional options for the data class
 * 
 * @extends		DwtComposite
 */
ZmAutocompleteListView = function(params) {

	if (arguments.length == 0) { return; }

	var className = params.className ? params.className : "ZmAutocompleteListView";
	DwtComposite.call(this, params.parent || appCtxt.getShell(), className, DwtControl.ABSOLUTE_STYLE);

	this._dataClass = this._dataAPI = params.dataClass;
	this._dataLoader = params.dataLoader;
	this._dataLoaded = false;
	this._matchValue = params.matchValue;
	this._separator = (params.separator != null) ? params.separator : AjxEmailAddress.SEPARATOR;
    this._options = params.options || {};

	this._callbacks = {};
	for (var i = 0; i < ZmAutocompleteListView.CALLBACKS.length; i++) {
		this._setCallbacks(ZmAutocompleteListView.CALLBACKS[i], params);
	}

	this._isDelim = AjxUtil.arrayAsHash(params.delims || ZmAutocompleteListView.DELIMS);
	this._isDelimCode = AjxUtil.arrayAsHash(params.delimCodes || ZmAutocompleteListView.DELIM_CODES);
	if (!params.delims && !params.delimCodes) {
		this._isDelim[','] = this._isDelimCode[188] = appCtxt.get(ZmSetting.AUTOCOMPLETE_ON_COMMA); 
		var listener = new AjxListener(this, this._settingChangeListener);
		var aoc = appCtxt.getSettings().getSetting(ZmSetting.AUTOCOMPLETE_ON_COMMA);
		if (aoc) {
			aoc.addChangeListener(listener);
		}
	}

    // mouse event handling
	this._setMouseEventHdlrs();
	this.addListener(DwtEvent.ONMOUSEDOWN, new AjxListener(this, this._mouseDownListener));
	this.addListener(DwtEvent.ONMOUSEOVER, new AjxListener(this, this._mouseOverListener));
	this._addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._outsideListener = new AjxListener(null, ZmAutocompleteListView._outsideMouseDownListener);

	// only trigger matching after a sufficient pause
	this._acInterval = appCtxt.get(ZmSetting.AC_TIMER_INTERVAL);
	this._acAction = new AjxTimedAction(null, this._autocompleteAction);
	this._acActionId = -1;

	// for managing focus on Tab in Firefox
	if (AjxEnv.isGeckoBased) {
		this._focusAction = new AjxTimedAction(null, this._autocompleteFocus);
	}

	this._origClass = "acRow";
	this._selClass = "acRow-selected";
	this._showLinkTextClass = "LinkText";
	this._hideLinkTextClass = "LinkText-hide";
	this._hideSelLinkTextClass = "LinkText-hide-selected";

	this._done = {};
	this.setVisible(false);
	this.setScrollStyle(Dwt.SCROLL);
	this.reset();
};

ZmAutocompleteListView.prototype = new DwtComposite;
ZmAutocompleteListView.prototype.constructor = ZmAutocompleteListView;

ZmAutocompleteListView.CB_COMPLETION	= "comp";
ZmAutocompleteListView.CB_KEYDOWN		= "keyDown";
ZmAutocompleteListView.CB_KEYPRESS		= "keyPress";
ZmAutocompleteListView.CB_KEYUP			= "keyUp";
ZmAutocompleteListView.CB_ENTER			= "enter";
ZmAutocompleteListView.CB_ADDR_FOUND	= "addrFound";
ZmAutocompleteListView.CALLBACKS = [
		ZmAutocompleteListView.CB_COMPLETION,
		ZmAutocompleteListView.CB_KEYDOWN,
		ZmAutocompleteListView.CB_KEYPRESS,
		ZmAutocompleteListView.CB_KEYUP,
		ZmAutocompleteListView.CB_ENTER,
		ZmAutocompleteListView.CB_ADDR_FOUND
];

// map of characters that are completion characters
ZmAutocompleteListView.DELIMS		= [',', ';', '\n', '\r', '\t'];	// used when list is not showing
ZmAutocompleteListView.DELIM_CODES	= [188, 59, 186, 3, 13, 9];		// used when list is showing

ZmAutocompleteListView.WAIT_ID = "wait";

// for list selection with up/down arrows
ZmAutocompleteListView.NEXT = -1;
ZmAutocompleteListView.PREV = -2;

// Public static methods

/**
 * Handles the on key down event.
 * 
 * @param	{Event}	event		the event
 */
ZmAutocompleteListView.onKeyDown =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var result = null;
	var key = DwtKeyEvent.getCharCode(ev);
	DBG.println("ac", ev.type + ": " + key);
	if (key == 38 || key == 40) {
		ZmAutocompleteListView.__geckoKeyCode = null;
		result = ZmAutocompleteListView._onKeyUp(ev);
	} else {
		result = ZmAutocompleteListView._onKeyDown(ev);
	}
	var element = DwtUiEvent.getTargetWithProp(ev, "_aclvId");
	var aclv = element && DwtControl.ALL_BY_ID[element._aclvId];
	if (aclv) {
		aclv._inputValue = element.value;
		var cbResult = aclv._runCallbacks(ZmAutocompleteListView.CB_KEYDOWN, element && element.id, [ev, aclv, result, element]);
		result = (cbResult === true || cbResult === false) ? cbResult : result;
	}
    if (AjxEnv.isFirefox){
       ZmAutocompleteListView.clearTimer();
       ZmAutocompleteListView.timer =  new AjxTimedAction(this, ZmAutocompleteListView.onKeyUp, [ev]);
       AjxTimedAction.scheduleAction(ZmAutocompleteListView.timer, 300)
    }
	return result;
};

/**
 * Handles the on key press event.
 * 
 * @param	{Event}	event		the event
 */
ZmAutocompleteListView.onKeyPress =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	DwtKeyEvent.geckoCheck(ev);
	var result = null;
	var key = DwtKeyEvent.getCharCode(ev);
	DBG.println("ac", ev.type + ": " + key);
	if (AjxEnv.isGeckoBased && (key == 38 || key == 40)) {
		if (ZmAutocompleteListView.__geckoKeyCode) {
			result = ZmAutocompleteListView._onKeyUp(ev);
		} else {
			ZmAutocompleteListView.__geckoKeyCode = key;
		}
	}
	var element = DwtUiEvent.getTargetWithProp(ev, "_aclvId");
	var aclv = element && DwtControl.ALL_BY_ID[element._aclvId];

	if (aclv) {
		var cbResult = aclv._runCallbacks(ZmAutocompleteListView.CB_KEYPRESS, element && element.id, [ev, aclv, result, element]);
		result = (cbResult === true || cbResult === false) ? cbResult : result;
	}

	return (result != null) ? result : ZmAutocompleteListView._echoKey(true, ev);
};

/**
 * Handles the on key up event.
 * 
 * @param	{Event}	event		the event
 */
ZmAutocompleteListView.onKeyUp =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var key = DwtKeyEvent.getCharCode(ev);
	DBG.println("ac", ev.type + ": " + key);
	if (key == 38 || key == 40) {
		ZmAutocompleteListView.__geckoKeyCode = null;
		return true;
	}
	var result = ZmAutocompleteListView._onKeyUp(ev);
	var element = DwtUiEvent.getTargetWithProp(ev, "_aclvId");
	var aclv = element && DwtControl.ALL_BY_ID[element._aclvId];
	if (aclv) {
		var cbResult = aclv._runCallbacks(ZmAutocompleteListView.CB_KEYUP, element && element.id, [ev, aclv, result, element]);
		result = (cbResult === true || cbResult === false) ? cbResult : result;
	}
	return result;
};

/**
* "onkeydown" handler for catching Tab and Esc keys. We don't want to let the browser
* handle this event for those (which it will do before we get the keyup event).
*
* @param ev		the key event
* 
* @private
*/
ZmAutocompleteListView._onKeyDown =
function(ev) {
	var key = DwtKeyEvent.getCharCode(ev);
	// don't echo enter key if list view is visible, since in that case it's
	// just a selection mechanism
	if (key == 3 || key == 13) {
		var element = DwtUiEvent.getTargetWithProp(ev, "_aclvId");
		if (!element) {
			return ZmAutocompleteListView._echoKey(true, ev);
		}
		var aclv = DwtControl.ALL_BY_ID[element._aclvId];
		if (aclv && aclv.getVisible()) {
			return ZmAutocompleteListView._echoKey(false, ev);
		}
	}
	if (key == 9 || key == 27) {
		return ZmAutocompleteListView._onKeyUp(ev);
	} else {
		return ZmAutocompleteListView._echoKey(true, ev);
	}
};

/**
* "onkeyup" handler for performing autocompletion. The reason it's an "onkeyup" handler is that neither 
* "onkeydown" nor "onkeypress" arrives after the form field has been updated.
*
* @param ev		the key event
* 
* @private
*/
ZmAutocompleteListView._onKeyUp =
function(ev) {

	var element = DwtUiEvent.getTargetWithProp(ev, "_aclvId");
	if (!element) {
		return ZmAutocompleteListView._echoKey(true, ev);
	}

	var aclv = DwtControl.ALL_BY_ID[element._aclvId];
	var key = DwtKeyEvent.getCharCode(ev);
	aclv._hasCompleted = false;

	// Tab/Esc handled in keydown for IE
	if (AjxEnv.isIE && ev.type == "keyup" && (key == 9 || key == 27)) {
		return ZmAutocompleteListView._echoKey(true, ev);
	}

	var value = element.value;
	DBG.println(AjxDebug.DBG3, ev.type + " event, key = " + key + ", value = " + value);
	ev.inputChanged = (value != aclv._inputValue);

	// reset timer on any address field key activity
	if (aclv._acActionId != -1 && !DwtKeyMap.IS_MODIFIER[key]) {
		AjxTimedAction.cancelAction(aclv._acActionId);
		aclv._acActionId = -1;
	}
	
	// Figure out what this handler should return. If it returns true, the browser will
	// handle the key event. That usually means it just echoes the typed character, but
	// it could do something like change focus (eg tab). We let the browser handle input
	// characters, and anything weird that we don't want to deal with. The only keys we
	// don't let the browser handle are ones that control the features of the autocomplete
	// list.

	if (DwtKeyMap.IS_MODIFIER[key] || DwtKeyMapMgr.hasModifier(ev)) {
		return ZmAutocompleteListView._echoKey(true, ev);
	}
	// if the field is empty, clear the list
	if (!value) {
		aclv.reset();
		if (key != 13 && key != 3) { // If the key is ENTER, we want to continue and eventually call the enter-listeners
			return ZmAutocompleteListView._echoKey(true, ev);
		}
	}
	if (key == 37 || key == 39) { // left/right arrow key
		return ZmAutocompleteListView._echoKey(true, ev);
	}
	// Pass tab/esc through if there's no list
	if ((key == 9 || key == 27) && !aclv.size()) {
		return ZmAutocompleteListView._echoKey(true, ev);
	}

	// if the user types a single delimiting character with the list showing, do completion
	var isDelim = (aclv.getVisible() && (!ev.shiftKey && aclv._isDelimCode[key]));

	DBG.println(AjxDebug.DBG3, "key = " + key + ", isDelim: " + isDelim);
	if (isDelim || (key == 27 || (aclv.getVisible() && (key == 38 || key == 40)))) {
		aclv.handleAction(key, isDelim);
		// In Firefox, focus shifts on Tab even if we return false (and stop propagation and prevent default),
		// so make sure the focus stays in this element.
		if (AjxEnv.isGeckoBased && key == 9) {
			aclv._focusAction.args = [ element ];
			AjxTimedAction.scheduleAction(aclv._focusAction, 0);
		}
		if (key == 13 || key == 3) {
			var result = aclv._runCallbacks(ZmAutocompleteListView.CB_ENTER, element && element.id, [ev]);
			return (result != null) ? result : ZmAutocompleteListView._echoKey(true, ev);
		}
		return ZmAutocompleteListView._echoKey(false, ev);
	}

	// skip if it's some weird character
	if (!ev.inputChanged && (key != 3 && key != 13 && key != 9 && key != 8 && key != 46)) {
		return ZmAutocompleteListView._echoKey(true, ev);
	}
    ZmAutocompleteListView.clearTimer();
	if (key == 13 || key == 3) {
		if (aclv._options.addrBubbles && aclv._dataAPI.isComplete && aclv._dataAPI.isComplete(value)) {
				DBG.println(AjxDebug.DBG3, "got a Return, found an addr: " + value);
				aclv._runCallbacks(ZmAutocompleteListView.CB_ADDR_FOUND, element.id, [aclv, value, "\n"]);
		} else {
			// Treat as regular selection
			var selEv = DwtShell.selectionEvent;
			DwtUiEvent.copy(selEv, ev);
			selEv.detail = 0;
			aclv.notifyListeners(DwtEvent.SELECTION, selEv);
		}
		aclv.reset();
		var result = aclv._runCallbacks(ZmAutocompleteListView.CB_ENTER, element && element.id, [ev]);
		return (result != null) ? result : ZmAutocompleteListView._echoKey(true, ev);
	}

	// regular input, schedule autocomplete
	var ev1 = new DwtKeyEvent();
	DwtKeyEvent.copy(ev1, ev);
	ev1.aclv = aclv;
	ev1.element = element;
	aclv._acAction.obj = aclv;
	aclv._acAction.args = [ ev1 ];
	DBG.println(AjxDebug.DBG2, "scheduling autocomplete");
	aclv._autocompleting = true;

	aclv._acActionId = AjxTimedAction.scheduleAction(aclv._acAction, aclv._acInterval);
	
	return ZmAutocompleteListView._echoKey(true, ev);
};


ZmAutocompleteListView.clearTimer =
function(ev){
    if (ZmAutocompleteListView.timer){
        AjxTimedAction.cancelAction(ZmAutocompleteListView.timer)
    }
};

/**
 * Invokes or prevents the browser's default behavior (which is to echo the typed key).
 * 
 * @param {Boolean}	echo	if <code>true</code>, echo the key
 * @param {Event}	ev	the UI event
 * 
 * @private
 */
ZmAutocompleteListView._echoKey =
function(echo, ev) {
	DwtUiEvent.setBehaviour(ev, !echo, echo);
	return echo;
};

/**
 * Hides list if there is a click elsewhere.
 * 
 * @private
 */
ZmAutocompleteListView._outsideMouseDownListener =
function(ev, context) {

	var curList = context && context.obj;
	if (curList) {
		DBG.println("out", "outside listener, cur " + curList.toString() + ": " + curList._htmlElId);
		curList.show(false);
		curList.setWaiting(false);
	}
};

// Public methods

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmAutocompleteListView.prototype.toString = 
function() {
	return "ZmAutocompleteListView";
};

/**
 * Sets the active account.
 * 
 * @param	{ZmAccount}		account		the account
 */
ZmAutocompleteListView.prototype.setActiveAccount =
function(account) {
	this._activeAccount = account;
};

/**
 * Adds autocompletion to the given field by setting key event handlers.
 *
 * @param {Element}	element			an HTML element
 * @param {string}	addrInputId		ID of ZmAddressInputField (for addr bubbles)
 * 
 * @private
 */
ZmAutocompleteListView.prototype.handle =
function(element, addrInputId) {
	element._aclvId = this._htmlElId;
	if (addrInputId) {
		element._aifId = addrInputId;
	}
	Dwt.setHandler(element, DwtEvent.ONKEYDOWN, ZmAutocompleteListView.onKeyDown);
	Dwt.setHandler(element, DwtEvent.ONKEYPRESS, ZmAutocompleteListView.onKeyPress);
	Dwt.setHandler(element, DwtEvent.ONKEYUP, ZmAutocompleteListView.onKeyUp);
    if (AjxEnv.isFirefox){
        Dwt.setHandler(element, DwtEvent.ONBLUR, ZmAutocompleteListView.clearTimer);
    }
};

/**
 * Autocomplete typed text. Should be called by a handler for a keyboard event.
 *
 * @param {Element}	element	the element (some sort of text field) doing autocomplete
 * @param {Object}	loc		where to popup the list, if appropriate
 * 
 * @private
 */
ZmAutocompleteListView.prototype.autocomplete =
function(info) {
	DBG.println(AjxDebug.DBG3, "ZmAutocompleteListView: autocomplete");
	if (!info || (info.text == "undefined")) { return; }
	if (this._dataLoader && !this._dataLoaded) {
		this._data = this._dataLoader.call(this._dataClass);
		this._dataAPI = this._data;
		this._dataLoaded = true;
	}

	// The callback into ourself emulates a while loop, and is here to support
	// async calls into _autocomplete(), which may result in a server request
	// (for example, when matching against the GAL)
	var callback = new AjxCallback(this, this.autocomplete);
	if (info.start < info.text.length) {
		var chunk = this._nextChunk(info.text, info.start);
		this._autocomplete(chunk, callback);
	} else if (info.change) {
		// quick completion was done
		this._autocompleting = false;
		DBG.println(AjxDebug.DBG2, "autocomplete, new text: " + info.text + "; element: " + this._element.value);
		this._updateField(info.text, info.match);
	}
};

/**
 * Resets the state of the autocomplete list.
 */
ZmAutocompleteListView.prototype.reset =
function() {

	this._matches = null;
	this._selected = null;

	this._matchHash			= {};
	this._forgetLink		= {};
	this._expandLink		= {};

	this.show(false);
	if (this._memberListView) {
		this._memberListView.show(false);
	}
	this.setWaiting(false);
};

/**
* Checks the given key to see if it's used to control the autocomplete list in some way.
* If it does, the action is taken and the key won't be echoed into the input area.
*
* The following keys are action keys:
*	38 40		up/down arrows (list selection)
*	27			escape (hide list)
*
* The following keys are delimiters (trigger completion):
*	3 13		return
*	9			tab
*	59 186		semicolon
*	188			comma
*
* @param key		a numeric key code
* @param isDelim	true if a single delimiter key was typed
* 
* @private
*/
ZmAutocompleteListView.prototype.handleAction =
function(key, isDelim) {

	DBG.println(AjxDebug.DBG2, "autocomplete handleAction for key " + key + " / " + isDelim);

	if (isDelim) {
		this._update(true);
	} else if (key == 38 || key == 40) {
		// handle up and down arrow keys
		if (this.size() <= 1) { return; }
		if (key == 40) {
			this._setSelected(ZmAutocompleteListView.NEXT);
		} else if (key == 38) {
			this._setSelected(ZmAutocompleteListView.PREV);
		}
	} else if (key == 27) {
		this.reset(); // ESC hides the list
	}
};

/**
 * Sets the waiting status.
 * 
 * @param	{Boolean}	on		if <code>true</code>, turn waiting "on"
 * 
 */
ZmAutocompleteListView.prototype.setWaiting =
function(on) {

	if (!on && !this._waitingDiv) { return; }

	var div = this._waitingDiv;
	if (!div) {
		div = this._waitingDiv = document.createElement("div");
		div.className = "acWaiting";
		var html = [], idx = 0;
		html[idx++] = "<table cellpadding=0 cellspacing=0 border=0>";
		html[idx++] = "<tr>";
		html[idx++] = "<td><div class='ImgSpinner'></div></td>";
		html[idx++] = "<td>" + ZmMsg.autocompleteWaiting + "</td>";
		html[idx++] = "</tr>";
		html[idx++] = "</table>";
		div.innerHTML = html.join("");
		Dwt.setPosition(div, Dwt.ABSOLUTE_STYLE);
		appCtxt.getShell().getHtmlElement().appendChild(div);
	}

	if (on) {
		var loc = this._getDefaultLoc();
		Dwt.setLocation(div, loc.x, loc.y);
	}

	Dwt.setZIndex(div, on ? Dwt.Z_DIALOG_MENU : Dwt.Z_HIDDEN);
	Dwt.setVisible(div, on);
};

// Private methods

/**
 * Called as a timed action, after a sufficient pause in typing within an address field.
 * 
 * @private
 */
ZmAutocompleteListView.prototype._autocompleteAction =
function(ev) {
	DBG.println(AjxDebug.DBG2, "performing autocomplete");
	var element = ev.element;
	var aclv = ev.aclv;
	aclv._acActionId = -1; // so we don't try to cancel

	aclv.reset();				// start fresh
	aclv._element = element;	// for updating element later
	
	var info = {text: element.value, start: 0};
	
	aclv.autocomplete(info);
};

/**
 * Displays the current matches in a popup list, selecting the first.
 *
 * @param {Boolean}	show	if <code>true</code>, display the list
 * @param {String}	loc		where to display the list
 * 
 */
ZmAutocompleteListView.prototype.show =
function(show, loc) {
	DBG.println(AjxDebug.DBG3, "autocomplete show: " + show);
	if (show) {
		this.setWaiting(false);
		this._popup(loc);
	} else {
		this._hasCompleted = false;
		this._popdown();
	}
};

// Private methods

/*
 * Finds the next chunk of text in a string that we should try to autocomplete, by reading
 * until it hits some sort of address delimiter (or runs out of text). If the chunk is a
 * valid email address, it's skipped since there's no point in trying to autocomplete it.
 * 
 * @param text	[string]	text to scan
 * @param start	[int]		index to start at
 */
ZmAutocompleteListView.prototype._nextChunk =
function(text, start) {

	while (text.charAt(start) == ' ') {	// ignore leading space
		start++;
	}
	var insideQuotes = false;
	for (var i = start; i < text.length; i++) {
		var c = text.charAt(i);
		if (c == '"') {
			// skip text and delimiters that are quoted
			c = text.charAt(++i);
			while (i < text.length && c != '"') {
				c = text.charAt(++i);
			}
		}
		var isDelim = this._isDelim[c];
		if (isDelim || (this._options.addrBubbles && c == ' ')) {
			var chunk = text.substring(start, i);
			if (this._dataAPI.isComplete && this._dataAPI.isComplete(chunk)) {
				DBG.println(AjxDebug.DBG3, "skipping completed chunk: " + chunk);
				if (this._runCallbacks(ZmAutocompleteListView.CB_ADDR_FOUND, this._element && this._element.id, [this, chunk, c])) {
					return null;
				}
				if (isDelim) {
					start = i + 1;
				}
				while (text.charAt(start) == ' ') {	// ignore leading space
					start++;
				}
			} else if (isDelim) {
				return {text: text, str: chunk, start: start, end: i, delim: isDelim};
			}
		}
	}
	var chunk = text.substring(start, i);

	return {text: text, str: chunk, start: start, end: i, delim: false};
};

// Looks for matches for a string and either displays them in a list, or does the completion
// immediately (if the string was followed by a delimiter). The chunk object that we get has
// information that allows us to do the replacement if we are performing completion.
ZmAutocompleteListView.prototype._autocomplete =
function(chunk, callback) {
	DBG.println(AjxDebug.DBG3, "ZmAutocompleteListView: _autocomplete");

	if (!chunk || !(this._dataAPI && this._dataAPI.autocompleteMatch)) { return; }
	var str = AjxStringUtil.trim(chunk.str);

	// if string is empty or already a delimited address, no reason to look for matches
	if (!(str && str.length) || (this._done[str])) {
		if (callback) {
			callback.run({text: chunk.text, start: chunk.end + 1});
			return;
		} else {
			return {text: chunk.text, start: chunk.end + 1};
		}
	}

	//start and end of the chunk we are autocompleting, i.e. the range we might replace.
	this._start = chunk.start;
	this._end = chunk.end;

	var text = chunk.text;
	var start = chunk.end; // move beyond the current chunk

	this._removeAll();

	var respCallback = new AjxCallback(this, this._handleResponseAutocomplete, [str, chunk, text, start, callback]);
	this._dataAPI.autocompleteMatch(str, respCallback, this, this._options, this._activeAccount);
};

ZmAutocompleteListView.prototype._handleResponseAutocomplete =
function(str, chunk, text, start, callback, list) {

	var retValue;
	var change = false;
	this._autocompleting = false;

	if (!retValue) {
		if (list && list.length) {
			DBG.println(AjxDebug.DBG2, "matches found: " + list.length);
			// done now in case of quick complete
			this._set(list); // populate the list view
		} else {
			this._showNoResults();
		}

		// if text ends in a delimiter, complete immediately without showing the list
		var match;
		if (chunk && chunk.delim && (chunk.end == chunk.text.length - 1)) {
			DBG.println(AjxDebug.DBG2, "performing quick completion");
			var result = this._complete(text, str, true);
			if (result) {
				text = result.text;
				start = result.start;
				match = result.match;
				change = true;
			}
		} else if (list && list.length) {
			// show the list, unless this is not the first time we're displaying results for
			// this autocomplete string (eg GAL), and the user selected a result from the
			// first time we showed the list (bug 28886)
			if (!this._hasCompleted) {
				this.show(true);
			}
		}
	
		retValue = {text: text, start: start, match: match, change: change};
	}
	
	if (callback) {
		callback.run(retValue);
	}
};

/**
 * Replaces the current chunk of text with the selected match (even if the list
 * is not visible), or with replacement text returned by the data class. If the
 * given text ends with a delimiter, "quick complete" is triggered, which does
 * the replacement without showing the list.
 * 
 * @param text		[string]		current input text
 * @param str		[string]*		current chunk
 * @param hasDelim	[boolean]*		current chunk ends with a delimiter
 * @param match		[object]*		forced match object
 */
ZmAutocompleteListView.prototype._complete =
function(text, str, hasDelim, match) {

	DBG.println(AjxDebug.DBG3, "ZmAutocompleteListView: _complete: selected is " + this._selected);
	match = match || this._matchHash[this._selected];
	if (!match)	{ return; }

	var start = this._start;
	var end = hasDelim ? this._end + 1 : this._end;
	DBG.println(AjxDebug.DBG2, "update replace range: " + start + " - " + end);
	var value = this._getCompletionValue(match);
	var newText = this._options.addrBubbles ? value :
				  [text.substring(0, start), value, this._separator, text.substring(end, text.length)].join("");
	this._start = this._end = newText.length; //update in case this is called multiple times from ZmDLAutocompleteListView for selecitng all items from a DL
	if (value) {
		this._done[value] = true;
	}
	DBG.display(AjxDebug.DBG2, newText);
	return {text: newText, start: start + value.length + this._separator.length, match: match};
};

ZmAutocompleteListView.prototype._getCompletionValue =
function(match) {
	var value = "";
	if (this._matchValue instanceof Array) {
		for (var i = 0, len = this._matchValue.length; i < len; i++) {
			if (match[this._matchValue[i]]) {
				value = match[this._matchValue[i]];
				break;
			}
		}
	} else {
		value = match[this._matchValue] || "";
	}
	return value;
};

// Resets the value of an element to the given text.
ZmAutocompleteListView.prototype._updateField =
function(text, match, ev) {

	var el = this._element;
	if (this._options.addrBubbles) {
		var addrInput = el && el._aifId && DwtControl.ALL_BY_ID[el._aifId];
		if (addrInput) {
			if (match && match.multipleAddresses) {
				addrInput.setValue(text);
			}
			else {
				addrInput.addBubble({address:text, match:match});
			}
			el = addrInput._input;
			if (AjxEnv.isIE) // Input field loses focus along the way. Restore it when the stack is finished
				AjxTimedAction.scheduleAction(new AjxTimedAction(addrInput, addrInput.focus), 0);
		}
	}
	else if (el) {
		el.value = text;
		el.focus();
		Dwt.setSelectionRange(el, text.length, text.length);
	}

	this.reset();
	this._hasCompleted = true;
	this._runCallbacks(ZmAutocompleteListView.CB_COMPLETION, el && el.id, [text, el, match, ev]);
};

// Updates the element with the currently selected match.
ZmAutocompleteListView.prototype._update =
function(hasDelim, match, ev) {
	if (!this._autocompleting) { // Trying to update while autocomplete is busy usually results in undesired results
		var value = this._element ? this._element.value : "";
		var result = this._complete(value, null, hasDelim, match);
		if (result) {
			this._updateField(result.text, result.match, ev);
		}
	}
	this._popdown();
};

// Listeners

// MOUSE_DOWN selects a match and performs an update. Note that we don't wait for
// a corresponding MOUSE_UP event.
ZmAutocompleteListView.prototype._mouseDownListener = 
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var row = DwtUiEvent.getTargetWithProp(ev, "id");
	if (!row || !row.id || row.id.indexOf("Row") == -1) { return; }
	if (ev.button == DwtMouseEvent.LEFT) {
		this._setSelected(row.id);
		if (this.isListenerRegistered(DwtEvent.SELECTION)) {
	    	var selEv = DwtShell.selectionEvent;
	    	DwtUiEvent.copy(selEv, ev);
	    	selEv.detail = 0;
	    	this.notifyListeners(DwtEvent.SELECTION, selEv);
	    	return true;
	    }		
	}
};

// Mouse over selects a match
ZmAutocompleteListView.prototype._mouseOverListener = 
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var row = Dwt.findAncestor(DwtUiEvent.getTarget(ev), "id");
	if (row) {
		this._setSelected(row.id);
	}
	return true;
};

// Seems like DwtComposite should define this method
ZmAutocompleteListView.prototype._addSelectionListener = 
function(listener) {
	this._eventMgr.addListener(DwtEvent.SELECTION, listener);
};

ZmAutocompleteListView.prototype._listSelectionListener = 
function(ev) {
	this._update(true, null, ev);
};

// Layout

// Lazily create main table, since we may need it to show "Waiting..." row before
// a call to _set() is made.
ZmAutocompleteListView.prototype._getTable =
function() {

	var table = this._tableId && document.getElementById(this._tableId);
	if (!table) {
		var html = [], idx = 0;
		this._tableId = Dwt.getNextId();
		html[idx++] = "<table id='" + this._tableId + "' cellpadding=0 cellspacing=0 border=0>";
		html[idx++] = "</table>";
		this.getHtmlElement().innerHTML = html.join("");
		table = document.getElementById(this._tableId);
	}
	return table;
};

// Creates the list and its member elements based on the matches we have. Each match becomes a
// row. The first match is automatically selected.
ZmAutocompleteListView.prototype._set =
function(list) {

	var table = this._getTable();
	this._matches = list;
	var forgetEnabled = (this._options.supportForget !== false);
	var expandEnabled = (this._options.supportExpand !== false);
	var len = this._matches.length;
	for (var i = 0; i < len; i++) {
		var match = this._matches[i];
		if (match && (match.text || match.icon)) {
			var rowId = match.id = this._getId("Row", i);
			this._matchHash[rowId] = match;
			var row = table.insertRow(-1);
			row.className = this._origClass;
			row.id = rowId;
			var html = [], idx = 0;
			var cell = row.insertCell(-1);
			cell.className = "Icon";
			if (match.icon) {
				cell.innerHTML = (match.icon.indexOf('Dwt') != -1) ? ["<div class='", match.icon, "'></div>"].join("") :
								 									 AjxImg.getImageHtml(match.icon);
			} else {
				cell.innerHTML = "&nbsp;";
			}
			cell = row.insertCell(-1);
			cell.innerHTML = match.text || "&nbsp;";
			if (forgetEnabled) {
				this._insertLinkCell(this._forgetLink, row, rowId, this._getId("Forget", i), (match.score > 0));
			}
			if (expandEnabled) {
				this._insertLinkCell(this._expandLink, row, rowId, this._getId("Expand", i), match.canExpand);
			}
		}
	}
	if (forgetEnabled) {
		this._forgetText = {};
		this._addLinks(this._forgetText, "Forget", ZmMsg.forget, ZmMsg.forgetTooltip, this._handleForgetLink);
	}
	if (expandEnabled) {
		this._expandText = {};
		this._addLinks(this._expandText, "Expand", ZmMsg.expand, ZmMsg.expandTooltip, this.expandDL);
	}

	AjxTimedAction.scheduleAction(new AjxTimedAction(this,
		function() {
			this._setSelected(this._getId("Row", 0));
		}), 100);
};

ZmAutocompleteListView.prototype._showNoResults =
function() {
	// do nothing. Overload to show something.
};

ZmAutocompleteListView.prototype._insertLinkCell =
function(hash, row, rowId, linkId, addLink) {
	hash[rowId] = addLink ? linkId : null;
	var cell = row.insertCell(-1);
	cell.className = "Link";
	cell.innerHTML = addLink ? "<a id='" + linkId + "'></a>" : "";
};

ZmAutocompleteListView.prototype._getId =
function(type, num) {
	return [this._htmlElId, "ac" + type, num].join("_");
};

// Add a DwtText to the link so it can have a tooltip.
ZmAutocompleteListView.prototype._addLinks =
function(textHash, idLabel, label, tooltip, handler) {

	var len = this._matches.length;
	for (var i = 0; i < len; i++) {
		var match = this._matches[i];
		var rowId = match.id = this._getId("Row", i);
		var linkId = this._getId(idLabel, i);
		var link = document.getElementById(linkId);
		if (link) {
			var textId = this._getId(idLabel + "Text", i);
			var text = new DwtText({parent:this, className:this._hideLinkTextClass, id:textId});
			textHash[rowId] = text;
			text.isLinkText = true;
			text.setText(label);
			text.setToolTipContent(tooltip);
			var listener = new AjxListener(this, handler, [match.email, textId, rowId]);
			text.addListener(DwtEvent.ONMOUSEDOWN, listener);
			text.reparentHtmlElement(link);
		}
	}
};

ZmAutocompleteListView.prototype._showLink =
function(hash, textHash, rowId, show) {
	var text = textHash && textHash[rowId];
	if (text) {
		text.setClassName(!show ? this._hideLinkTextClass :
			hash[rowId] ? this._showLinkTextClass : this._hideSelLinkTextClass);
	}
};

// Displays the list
ZmAutocompleteListView.prototype._popup =
function(loc) {

	if (this.getVisible()) { return; }

	loc = loc || this._getDefaultLoc();
	var x = loc.x;
	var y = loc.y;

	var windowSize = this.shell.getSize();
	var availHeight = windowSize.y - y;
	var fullHeight = this.size() * this._getRowHeight();
	this.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	this.setVisible(true);
	var curSize = this.getSize();
	if (availHeight < fullHeight) {
	  //we are short add text to alert user to keep typing
      this._showMoreResultsText(availHeight);
      // if we don't fit, resize so we are scrollable
      this.setSize(Dwt.DEFAULT, availHeight - (AjxEnv.isIE ? 30 : 10));
	   // see if we need to account for width of vertical scrollbar
	  var div = this.getHtmlElement();
	  if (div.clientWidth != div.scrollWidth) {
			this.setSize(curSize.x + Dwt.SCROLLBAR_WIDTH, Dwt.DEFAULT);
	  }
      
	} else if (curSize.y < fullHeight) {
		this.setSize(Dwt.CLEAR, fullHeight);
	} else {
		this.setSize(Dwt.CLEAR, Dwt.CLEAR);	// set back to auto-sizing
	}

	var newX = (x + curSize.x >= windowSize.x) ? windowSize.x - curSize.x : x;

	DBG.println(AjxDebug.DBG1, this.toString() + " popup at: " + newX + "," + y);
    this.setLocation(newX, y);
	this.setVisible(true);
	this.setZIndex(Dwt.Z_DIALOG_MENU);

	var omem = appCtxt.getOutsideMouseEventMgr();
	var omemParams = {
		id:					"ZmAutocompleteListView",
		obj:				this,
		outsideListener:	this._outsideListener
	}
	omem.startListening(omemParams);
};

// returns a point with a location just below the input field
ZmAutocompleteListView.prototype._getDefaultLoc = 
function() {

	var elLoc = Dwt.getLocation(this._element);
	var elSize = Dwt.getSize(this._element);
	var x = elLoc.x;
	var y = elLoc.y + elSize.y;
	if (this._options.addrBubbles) {
		y += 3;
	}
	DwtPoint.tmp.set(x, y);
	return DwtPoint.tmp;
};

// Hides the list
ZmAutocompleteListView.prototype._popdown = 
function() {

	if (!this.getVisible()) { return; }
	DBG.println("out", "popdown " + this.toString() + ": " + this._htmlElId);

	if (this._memberListView) {
		this._memberListView._popdown();
	}
	
	this.setZIndex(Dwt.Z_HIDDEN);
	this.setVisible(false);
	this._removeAll();

	var omem = appCtxt.getOutsideMouseEventMgr();
	omem.stopListening({id:"ZmAutocompleteListView", obj:this});
};

/*
    Display message to user that more results are available than fit in the current display
    @param {int}    availHeight available height of display
 */
ZmAutocompleteListView.prototype._showMoreResultsText =
function (availHeight){
    //over load for implementation
};

/**
 * Selects a match by changing its CSS class.
 *
 * @param	{string}	id		ID of row to select, or NEXT / PREV
 */
ZmAutocompleteListView.prototype._setSelected =
function(id) {

	if (id == this._selected) { return; }

	DBG.println(AjxDebug.DBG3, "setting selected id to " + id);
	var table = document.getElementById(this._tableId);
	var rows = table && table.rows;
	if (!(rows && rows.length)) { return; }

	var len = rows.length;

	if (id == ZmAutocompleteListView.NEXT || id == ZmAutocompleteListView.PREV) {
		id = this._getRowId(rows, id, len);
		if (!id) { return; }
	}

	for (var i = 0; i < len; i++) {
		var row = rows[i];
		var curStyle = row.className;
		if (row.id == id) {
			row.className = this._selClass;
		} else if (curStyle != this._origClass) {
			row.className = this._origClass;
		}
	}

	this._showLink(this._forgetLink, this._forgetText, this._selected, false);
	this._showLink(this._forgetLink, this._forgetText, id, true);

	this._showLink(this._expandLink, this._expandText, this._selected, false);
	this._showLink(this._expandLink, this._expandText, id, true);

	this._selected = id;
};

ZmAutocompleteListView.prototype._getRowId =
function(rows, id, len) {
	if (len <= 1) { return; }

	var idx = -1;
	for (var i = 0; i < len; i++) {
		if (rows[i].id == this._selected) {
			idx = i;
			break;
		}
	}
	var newIdx = (id == ZmAutocompleteListView.PREV) ? idx - 1 : idx + 1;

	if (!(newIdx < 0 || newIdx >= len)) {
		DwtControl._scrollIntoView(rows[newIdx], this.getHtmlElement());
		return rows[newIdx].id;
	}
	return null;
};

ZmAutocompleteListView.prototype._getRowHeight =
function() {
	if (!this._rowHeight) {
		if (!this.getVisible()) {
			this.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
			this.setVisible(true);
		}
		var row = this._getTable().rows[0];
		this._rowHeight = row && Dwt.getSize(row).y;
	}
	return this._rowHeight || 18;
};



// Miscellaneous

// Clears the internal list of matches
ZmAutocompleteListView.prototype._removeAll =
function() {
	this._matches = null;
	var table = this._getTable();
	for (var i = table.rows.length - 1; i >= 0; i--) {
		var row = table.rows[i];
		if (row != this._waitingRow) {
			table.deleteRow(i);
		}
	}
	this._removeLinks(this._forgetText);
	this._removeLinks(this._expandText);
};

ZmAutocompleteListView.prototype._removeLinks =
function(textHash) {
	if (!textHash) { return; }
	for (var id in textHash) {
		var textCtrl = textHash[id];
		if (textCtrl) {
			textCtrl.dispose();
		}
	}
};

// Returns the number of matches
ZmAutocompleteListView.prototype.size =
function() {
	return this._getTable().rows.length;
};

// Force focus to the input element (handle Tab in Firefox)
ZmAutocompleteListView.prototype._autocompleteFocus =
function(htmlEl) {
	htmlEl.focus();
};

ZmAutocompleteListView.prototype._getAcListLoc =
function(ev) {
	var element = ev.element;
	var loc = Dwt.getLocation(element);
	var height = Dwt.getSize(element).y;
	return (new DwtPoint(loc.x, loc.y + height));
};

ZmAutocompleteListView.prototype._settingChangeListener =
function(ev) {
	if (ev.type != ZmEvent.S_SETTING) { return; }
	if (ev.source.id == ZmSetting.AUTOCOMPLETE_ON_COMMA) {
		this._isDelim[','] = this._isDelimCode[188] = appCtxt.get(ZmSetting.AUTOCOMPLETE_ON_COMMA);
	}
};

ZmAutocompleteListView.prototype._handleForgetLink =
function(email, textId, rowId) {
	if (this._dataAPI.forget) {
		this._dataAPI.forget(email, new AjxCallback(this, this._handleResponseForget, [email, rowId]));
	}
};

ZmAutocompleteListView.prototype._handleResponseForget =
function(email, rowId) {
	var row = document.getElementById(rowId);
	if (row) {
		row.parentNode.removeChild(row);
		var msg = AjxMessageFormat.format(ZmMsg.forgetSummary, [email]);
		appCtxt.setStatusMsg(msg);
	}
	appCtxt.clearAutocompleteCache(ZmAutocomplete.AC_TYPE_CONTACT);
};

/**
 * Displays a second popup list with the members of the given distribution list.
 *
 * @param {string}			email		address of a distribution list
 * @param {string}			textId		ID of link text
 * @param {string}			rowId		ID or list view row
 * @param {DwtMouseEvent}	ev			mouse event
 * @param {DwtPoint}		loc			location to popup at; default is right of parent ACLV
 * @param {Element}			element		input element
 */
ZmAutocompleteListView.prototype.expandDL =
function(email, textId, rowId, ev, loc, element) {

	if (!this._dataAPI.expandDL) { return; }

	var mlv = this._memberListView;
	if (mlv && mlv.getVisible() && textId && this._curExpanded == textId) {
		// User has clicked "Collapse" link
		mlv.show(false);
		this._curExpanded = null;
		this._setExpandText(textId, false);
	} else {
		// User has clicked "Expand" link
		if (mlv && mlv.getVisible()) {
			// expanding a DL while another one is showing
			this._setExpandText(this._curExpanded, false);
			mlv.show(false);
		}
		var contactsApp = appCtxt.getApp(ZmApp.CONTACTS);
		var contact = contactsApp.getContactByEmail(email);
		if (!contact) {
			contact = new ZmContact(null);
			contact.initFromEmail(email);	// don't cache, since it's not a real contact (no ID)
		}
		contact.isDL = true;
		if (textId && rowId) {
			this._curExpanded = textId;
			this._setExpandText(textId, true);
		}
		this._dataAPI.expandDL(contact, 0, new AjxCallback(this, this._handleResponseExpandDL, [contact, loc, textId]));
	}
	this._element = element || this._element;
	if (this._element) {
		this._element.focus();
	}
};

ZmAutocompleteListView.prototype._handleResponseExpandDL =
function(contact, loc, textId, matches) {

	var mlv = this._memberListView;
	if (!mlv) {
		mlv = this._memberListView = new ZmDLAutocompleteListView({parent:appCtxt.getShell(), parentAclv:this});
	}
	mlv._element = this._element;
	mlv._dlContact = contact;
	mlv._dlBubbleId = textId;
	mlv._removeAll();
	mlv._set(matches, contact);

	// default position is just to right of parent ac list
	if (this.getVisible()) {
		loc = this.getLocation();
		loc.x += this.getSize().x;
	}

	mlv.show(true, loc);
	if (!mlv._rowHeight) {
		var table = document.getElementById(mlv._tableId);
		if (table) {
			mlv._rowHeight = Dwt.getSize(table.rows[0]).y;
		}
	}
};

ZmAutocompleteListView.prototype._setExpandText =
function(textId, expanded) {
	var textCtrl = DwtControl.fromElementId(textId);
	if (textCtrl) {
		textCtrl.setText(expanded ? ZmMsg.collapse : ZmMsg.expand);
	}
};

ZmAutocompleteListView.prototype._setCallbacks =
function(type, params) {

	var cbKey = type + "Callback";
	var list = this._callbacks[type] = [];
	if (params[cbKey]) {
		list.push({callback:params[cbKey]});
	}
};

/**
 * Adds a callback of the given type. In an input ID is provided, then the callback
 * will only be run if the event happened in that input.
 *
 * @param {constant}	type		autocomplete callback type (ZmAutocompleteListView.CB_*)
 * @param {AjxCallback}	callback	callback to add
 * @param {string}		inputId		DOM ID of an input element (optional)
 */
ZmAutocompleteListView.prototype.addCallback =
function(type, callback, inputId) {
	this._callbacks[type].push({callback:callback, inputId:inputId});
};

ZmAutocompleteListView.prototype._runCallbacks =
function(type, inputId, args) {

	var result = null;
	var list = this._callbacks[type];
	if (list && list.length) {
		for (var i = 0; i < list.length; i++) {
			var cbObj = list[i];
			if (inputId && cbObj.inputId && (inputId != cbObj.inputId)) { continue; }
			var r = AjxCallback.prototype.run.apply(cbObj.callback, args);
			if (r === true || r === false) {
				result = (result == null) ? r : result && r;
			}
		}
	}
	return result;
};
