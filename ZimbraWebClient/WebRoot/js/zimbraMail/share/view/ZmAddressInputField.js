/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010, 2011 VMware, Inc.
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
 * Creates an address input field that shows addresses as bubbles.
 * @constructor
 * @class
 * This class creates and manages a control for entering email addresses and displaying
 * them in bubbles. An address's surrounding bubble can be used to remove it, or, if the
 * address is a distribution list, expand it. They can be dragged to other address fields,
 * or reordered within this one. They can be selected via a "rubber band" selection box.
 * The bubbles support a few shortcuts.
 *
 * It is not a DwtInputField. If you don't want bubbles, use that (or a native INPUT) instead.
 *
 * @author Conrad Damon
 *
 * @param {hash}					params						hash of params:
 * @param {ZmAutocompleteListView}	params.autocompleteListView	associated autocomplete control
 * @param {string}      			params.inputId				an explicit ID to use for the control's INPUT element
 * @param {string}					params.templateId			custom template to use
 * @param {string}					params.type					arbitrary type to uniquely identify this among others
 * @param {boolean}					params.strictMode			if true (default), bubbles must contain valid addresses
 */
ZmAddressInputField = function(params) {

	params.parent = params.parent || appCtxt.getShell();
	params.className = params.className || "addrBubbleContainer";
	DwtComposite.call(this, params);

	this._initialize(params);

	if (params.autocompleteListView) {
		this.setAutocompleteListView(params.autocompleteListView);
	}

	this.type = params.type;
	this._strictMode = (params.strictMode !== false);

    this._bubbleAddedCallback = params.bubbleAddedCallback;
    this._bubbleRemovedCallback = params.bubbleRemovedCallback;
    this._bubbleMenuCreatedCallback = params.bubbleMenuCreatedCallback;
    this._bubbleResetOperationsCallback = params.bubbleMenuResetOperationsCallback;

	this._bubbleClassName = "addrBubble";

	this._bubbleList = new ZmAddressBubbleList();
	this._bubbleList.addSelectionListener(new AjxListener(this, this._selectionListener));
	this._bubbleList.addActionListener(new AjxListener(this, this._actionListener));

	this._listeners = {};
	this._listeners[ZmOperation.DELETE]			= new AjxListener(null, ZmAddressInputField.prototype._deleteListener);
	this._listeners[ZmOperation.EDIT]			= new AjxListener(null, ZmAddressInputField.prototype._editListener);
	this._listeners[ZmOperation.EXPAND]			= new AjxListener(null, ZmAddressInputField.prototype._expandListener);
	this._listeners[ZmOperation.CONTACT]		= new AjxListener(null, ZmAddressInputField.prototype._contactListener);

	if (ZmAddressInputField.AUTO_SELECT_TEXT) {
		this._keyDownListener = new AjxListener(this, this._handleKeyDown);
	}

	this._outsideListener = new AjxListener(null, ZmAddressInputField._outsideMouseDownListener);

	// drag-and-drop of bubbles
	var dropTgt = new DwtDropTarget("ZmAddressBubble");
	dropTgt.markAsMultiple();
	dropTgt.addDropListener(new AjxListener(this, this._dropListener));
	this.setDropTarget(dropTgt);

	// rubber-band selection of bubbles
	this._setEventHdlrs([DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEMOVE, DwtEvent.ONMOUSEUP]);
	var dragBox = new DwtDragBox();
	dragBox.addDragListener(new AjxListener(this, this._dragBoxListener));
	this.setDragBox(dragBox);

	this._reset();
};

ZmAddressInputField.prototype = new DwtComposite;
ZmAddressInputField.prototype.constructor = ZmAddressInputField;

ZmAddressInputField.prototype.TEMPLATE = "share.Widgets#ZmAddressInputField";

ZmAddressInputField.prototype.toString =
function() {
	return "ZmAddressInputField";
};

ZmAddressInputField.AUTO_SELECT_TEXT = true;

ZmAddressInputField.INPUT_EXTRA = 30;		// extra width for the INPUT
ZmAddressInputField.INPUT_EXTRA_SMALL = 10;	// edit mode

// tie a bubble SPAN to a widget that can handle clicks
ZmAddressInputField.BUBBLE_OBJ_ID = {};

// several ZmAddressInputField's can share an action menu, so save context statically
ZmAddressInputField.menuContext = {};

ZmAddressInputField.prototype.setAutocompleteListView =
function(aclv) {
	this._aclv = aclv;
	this._separator = (aclv._separator) || AjxEmailAddress.SEPARATOR;
	aclv.addCallback(ZmAutocompleteListView.CB_KEYDOWN, new AjxCallback(this, this._keyDownCallback), this._inputId);
	aclv.addCallback(ZmAutocompleteListView.CB_KEYUP, new AjxCallback(this, this._keyUpCallback), this._inputId);
	aclv.addCallback(ZmAutocompleteListView.CB_ADDR_FOUND, new AjxCallback(this, this._addrFoundCallback), this._inputId);
};

// Override since we normally want to add bubble before the INPUT, and not at the end. If we're
// leaving edit mode, we want to put the bubble back where it was via the index.
ZmAddressInputField.prototype.addChild =
function(child, index) {

	DwtComposite.prototype.addChild.apply(this, arguments);

	var el = child.getHtmlElement();
	if (this._input.parentNode == this._holder) {
		var refElement;
		if (index != null) {
			var refBubble = this._getBubbleList().getBubble(index);
			refElement = refBubble && refBubble.getHtmlElement();
		}
		this._holder.insertBefore(el, refElement || this._input);
	} else {
		this._holder.appendChild(el);
	}
};

/**
 * Creates a bubble for the given address and adds it into the holding area. If the address
 * is a local group, it is expanded and the members are added individually.
 *
 * @param {hash}				params				hash of params:
 * @param {string}				params.address		address text to go in the bubble
 * @param {ZmAutocompleteMatch}	params.match		match object
 * @param {ZmAddressBubble}		params.bubble		bubble to clone
 * @param {int}					params.index		position (relative to bubbles, not elements) at which to add bubble
 * @param {boolean}				params.skipNotify	if true, don't call bubbleAddedCallback
 */
ZmAddressInputField.prototype.addBubble =
function(params) {

	params = params || {};
	if (!params.address && !params.bubble) { return; }
	
	if (params.bubble) {
		params.address = params.bubble.address;
		params.match = params.bubble.match;
		params.canExpand = params.bubble.canExpand;
	}
	params.parent		= this;
	params.addrInput	= this;
	params.parentId		= this._htmlElId;
	params.className	= this._bubbleClassName;
	params.canRemove	= true;
	params.separator	= this._separator;
	params.type			= this.type;

	if (params.index == null && this._editModeIndex != null) {
		params.index = this._getInsertionIndex(this._holder.childNodes[this._editModeIndex]);
	}
	
	var bubbleAdded = false;
	
	// if it's a local group, expand it and add each address separately
	var match = params.match;
	if (match && match.isGroup && match.type == ZmAutocomplete.AC_TYPE_CONTACT) {
		var addrs = AjxEmailAddress.split(params.address);
		for (var i = 0, len = addrs.length; i < len; i++) {
			params.id = params.addrObj = params.match = params.email = params.canExpand = null;
			params.address = addrs[i];
			params.index = (params.index != null) ? params.index + i : null;
			if (this._hasValidAddress(params)) {
				this._addBubble(new ZmAddressBubble(params), params.index);
				bubbleAdded = true;
			}
		}
	}
	else {
		if (this._hasValidAddress(params)) {
			this._addBubble(new ZmAddressBubble(params), params.index);
			bubbleAdded = true;
		}
		else {
			// if handed a non-address while in strict mode, append it to the INPUT and bail
			var value = this._input.value;
			var sep = value ? this._separator : "";
			this._setInputValue([value, sep, params.address].join(""));
		}
	}

	if (bubbleAdded) {
		this._holder.className = "addrBubbleHolder";
		this._setInputValue("");
		if (this._bubbleAddedCallback && !params.skipNotify) {
			this._bubbleAddedCallback.run();
		}
	}
};

ZmAddressInputField.prototype._addBubble =
function(bubble, index) {

	if (!bubble) { return; }
	
	DBG.println("aif1", "ADD bubble: " + AjxStringUtil.htmlEncode(bubble.address));
	bubble.setDropTarget(this.getDropTarget());
	this._bubbleList.add(bubble, index);
	this._numBubbles++;

	var bubbleId = bubble._htmlElId;
	this._bubble[bubbleId] = bubble;
	this._addressHash[bubble.email] = true;

	this.focus();
};

ZmAddressInputField.prototype._hasValidAddress =
function(params) {
	var addr = (params.addrObj && params.addrObj.getAddress()) || params.address || (params.match && params.match.email);
	return (!this._strictMode || Boolean(AjxEmailAddress.parse(addr)));
};

/**
 * Removes the bubble with the given ID from the holding area.
 *
 * @param {string}	bubbleId	ID of bubble to remove
 * @param {boolean}	skipNotify	if true, don't call bubbleRemovedCallback
 */
ZmAddressInputField.prototype.removeBubble =
function(bubbleId, skipNotify) {

	var bubble = DwtControl.fromElementId(bubbleId);
	if (!bubble) { return; }
	
	this._bubbleList.remove(bubble);
	this._checkSelectionCount();

	bubble.dispose();

	this._bubble[bubbleId] = null;
	delete this._bubble[bubbleId];
	delete this._addressHash[bubble.email];
	this._numBubbles--;

	if (this._numBubbles == 0) {
		this._holder.className = "addrBubbleHolder-empty";
	}

	this._resizeInput();

	if (this._bubbleRemovedCallback && !skipNotify) {
		this._bubbleRemovedCallback.run();
	}
};

/**
 * Removes all bubbles from the holding area.
 */
ZmAddressInputField.prototype.clear =
function() {
	for (var id in this._bubble) {
		this.removeBubble(id);
	}
	this._reset();
};

/**
 * Returns a string of concatenated bubble addresses.
 */
ZmAddressInputField.prototype.getValue =
function() {
	var list = [].concat(this.getAddresses());
	if (this._input.value) {
		list.push(this._input.value);
	}
	return list.join(this._separator);
};

/**
 * Parses the given text into email addresses, and adds a bubble for each one
 * that we don't already have. Since text is passed in, we don't recognize expandable DLs.
 * A bubble will be added for a string even if it doesn't parse as an email address.
 *
 * @param {string}	text		email addresses
 * @param {boolean}	add			if true, control is not cleared first
 * @param {boolean}	skipNotify	if true, don't call bubbleAddedCallback
 */
ZmAddressInputField.prototype.setValue =
function(text, add, skipNotify) {

	if (!add) {
		this.clear();
	}
	if (!text) { return; }

	var index = null;
	if (this._editModeIndex != null) {
		index = this._getInsertionIndex(this._holder.childNodes[this._editModeIndex]);
	}

	var parsed = AjxEmailAddress.parseEmailString(text);
	var addrs;
	if (this._strictMode) {
		addrs = parsed.good.getArray();
	}
	else {
		addrs = parsed.good.size() ? parsed.good.getArray() : parsed.all.getArray();
	}
	for (var i = 0, len = addrs.length; i < len; i++) {
		var addr = addrs[i].toString();
		var email = addrs[i].getAddress();
		if (!this._addressHash[email]) {
			this.addBubble({address:addr, index:(index != null) ? index + i : null, skipNotify:skipNotify});
		}
	}

	var value = "";
	if (this._strictMode && parsed.bad && parsed.bad.size()) {
		value = parsed.bad.toString(AjxEmailAddress.SEPARATOR);
	}
	this._setInputValue(value);
};

/**
 * Removes the selected bubble. If none are selected, selects the last one.
 *
 * @param {boolean}		checkInput		if true, make sure INPUT is empty
 *
 * @return {boolean}	true if the delete selected or removed a bubble
 */
ZmAddressInputField.prototype.handleDelete =
function(checkInput) {

	if (checkInput && this._input.value.length > 0) {
		return false;
	}

	var sel = this.getSelection();
	if (sel.length) {
		for (var i = 0, len = sel.length; i < len; i++) {
			if (sel[i]) {
				this.removeBubble(sel[i].id);
			}
		}
		this.focus();
		return true;
	}
	else {
		return this._selectBubbleBeforeInput();
	}
};

// Selects the bubble to the left of the (empty) INPUT, if there is one.
ZmAddressInputField.prototype._selectBubbleBeforeInput =
function() {
	
	if (!this._input.value) {
		var index = this._getInputIndex();
		var span = (index > 0) && this._holder.childNodes[index - 1];
		var bubble = DwtControl.fromElement(span);
		if (bubble) {
			this.setSelected(bubble, true);
			this._checkSelection();
			this.blur();
			return true;
		}
	}
	return false;
};

/**
 * Sets selection of the given bubble.
 *
 * @param {Element}	bubble		bubble to select
 * @param {boolean} selected	if true, select the bubble, otherwise deselect it
 */
ZmAddressInputField.prototype.setSelected =
function(bubble, selected) {
	this._bubbleList.setSelected(bubble, selected);
};

/**
 * Returns a list of the currently selected bubbles. If a bubble has been selected via right-click,
 * but is not part of the current left-click selection, only it will be returned.
 *
 * @param {ZmAddressBubble}	bubble	reference bubble
 */
ZmAddressInputField.prototype.getSelection =
function(bubble) {
	return this._bubbleList.getSelection(bubble);
};

ZmAddressInputField.prototype.getSelectionCount =
function(bubble) {
	return this._bubbleList.getSelectionCount(bubble);
};

ZmAddressInputField.prototype.deselectAll =
function() {
	Dwt.deselectText();
	this._bubbleList.deselectAll();
	this._checkSelection();
};

ZmAddressInputField.prototype.preventSelection =
function(targetEl) {
	return !(this._bubble[targetEl.id] || this.__isInputEl(targetEl));
};

/**
 * Makes bubbles out of addresses in pasted text.
 *
 * @param ev
 */
ZmAddressInputField.onPaste =
function(ev) {
	var addrInput = ZmAddressInputField._getAddrInputFromEvent(ev);
	if (addrInput) {
		// give browser time to update input - easier than dealing with clipboard
		// will also resize the INPUT
		AjxTimedAction.scheduleAction(new AjxTimedAction(addrInput, addrInput._checkInput), 100);
	}
};

ZmAddressInputField.onCut =
function(ev) {
	var addrInput = ZmAddressInputField._getAddrInputFromEvent(ev);
	if (addrInput) {
		addrInput._resizeInput();
	}
};

/**
 * Handle arrow up, arrow down for bubble holder
 *
 * @param ev
 */
ZmAddressInputField.onHolderKeyClick =
function(ev) {
    ev = DwtUiEvent.getEvent(ev);
    var key = DwtKeyEvent.getCharCode(ev);
    if (key == 38) {
        if (this.clientHeight >= this.scrollHeight) { return; }
	    this.scrollTop = Math.max(this.scrollTop - this.clientHeight, 0);
        DBG.println("aif", "this.scrollTop  = " + this.scrollTop);
    }
    else if (key == 40) {
         if (this.clientHeight >= this.scrollHeight) { return; }
	     this.scrollTop = Math.min(this.scrollTop + this.clientHeight, this.scrollHeight - this.clientHeight);
         DBG.println("aif", "this.scrollTop  = " + this.scrollTop);
    }
};

// looks for valid addresses in the input, and converts them to bubbles
ZmAddressInputField.prototype._checkInput =
function(text) {
	text = text || this._input.value;
	DBG.println("aif", "CHECK input: " + AjxStringUtil.htmlEncode(text));
	if (text) {
		this.setValue(text, true);
	}
};

// focus input when holder div is clicked
ZmAddressInputField.onHolderClick =
function(ev) {
	DBG.println("aif", "ZmAddressInputField.onHolderClick");
	var addrInput = ZmAddressInputField._getAddrInputFromEvent(ev);
	if (addrInput) {
		addrInput.blur();	// focus in Chrome won't work without this if any bubbles are selected, no idea why
		addrInput.focus();
	}
};

/**
 * Removes the bubble with the given ID from the holding area.
 *
 * @param {string}	bubbleId	ID of bubble to remove
 * @param {boolean}	skipNotify	if true, don't call bubbleRemovedCallback
 *
 */
ZmAddressInputField.removeBubble =
function(bubbleId, skipNotify) {

	var bubble = document.getElementById(bubbleId);
	DBG.println("aif", "REMOVE bubble: " + AjxStringUtil.htmlEncode(bubble.address));
	var parentId = bubble._aifId || ZmAddressInputField.BUBBLE_OBJ_ID[bubbleId];
	var addrInput = bubble && DwtControl.ALL_BY_ID[parentId];
	if (addrInput && addrInput.getEnabled()) {
		addrInput.removeBubble(bubbleId, skipNotify);
		addrInput.focus();
	}
};

/**
 * Expands the distribution list address of the bubble with the given ID.
 *
 * @param {string}	bubbleId	ID of bubble
 * @param {string}	email		address to expand
 */
ZmAddressInputField.prototype.expandBubble =
function(bubbleId, email) {

	var bubble = DwtControl.fromElementId(bubbleId);
	if (bubble) {
		var loc = bubble.getLocation();
		loc.y += bubble.getSize().y + 2;
		this._aclv.expandDL(email, bubble._htmlElId, null, null, loc, this._input);
	}
};

/**
 * Expands the distribution list address of the bubble with the given ID.
 *
 * @param {string}	bubbleId	ID of bubble
 * @param {string}	email		address to expand
 */
ZmAddressInputField.expandBubble =
function(bubbleId, email) {

	var bubble = document.getElementById(bubbleId);
	var parentId = bubble._aifId || ZmAddressInputField.BUBBLE_OBJ_ID[bubbleId];
	var addrInput = bubble && DwtControl.ALL_BY_ID[parentId];
	if (addrInput && addrInput.getEnabled()) {
		addrInput.expandBubble(bubbleId, email);
	}
};

ZmAddressInputField.prototype.getInputElement =
function() {
	return this._input;
};

ZmAddressInputField.prototype.setEnabled =
function(enabled) {
	DwtControl.prototype.setEnabled.call(this, enabled);
	this._input.disabled = !enabled;
};

ZmAddressInputField.prototype._initialize =
function(params) {

	this._holderId = Dwt.getNextId();
	this._inputId = params.inputId || Dwt.getNextId();
	this._dragInsertionBarId = Dwt.getNextId();
	var data = {
		holderId:			this._holderId,
		inputId:			this._inputId,
		dragInsertionBarId:	this._dragInsertionBarId
	};
	this._createHtmlFromTemplate(params.templateId || this.TEMPLATE, data);

	this._holder = document.getElementById(this._holderId);
	this._holder._aifId = this._htmlElId;
	this._input = document.getElementById(this._inputId);
	this._dragInsertionBar = document.getElementById(this._dragInsertionBarId);

	Dwt.setHandler(this._holder, DwtEvent.ONCLICK, ZmAddressInputField.onHolderClick);
	Dwt.setHandler(this._input, DwtEvent.ONCUT, ZmAddressInputField.onCut);
	Dwt.setHandler(this._input, DwtEvent.ONPASTE, ZmAddressInputField.onPaste);
    Dwt.setHandler(this._holder, DwtEvent.ONKEYDOWN, ZmAddressInputField.onHolderKeyClick);

    var args = {container:this._holder, threshold:10, amount:15, interval:5, id:this._holderId};
    this._dndScrollCallback = new AjxCallback(null, DwtControl._dndScrollCallback, [args]);
    this._dndScrollId = this._holderId;

};

ZmAddressInputField.prototype._reset =
function() {

	this._bubble		= {};	// bubbles by bubble ID
	this._addressHash	= {};	// used addresses, so we can check for dupes

	this._numBubbles	= 0;

	this._bubbleList.reset();

	this._editMode = false;
	this._editModeIndex = this._editModeBubble = null;

	this._dragInsertionBarIndex = null;	// node index vertical bar indicating insertion point

	this._holder.className = "addrBubbleHolder-empty";
	this._setInputValue("");
};

/**
 * Focuses on this control.
 */
ZmAddressInputField.prototype.focus =
function() {
	if (this.getEnabled()) {
		this._input.focus();
	}
};

/**
 * Blurs this control.
 */
ZmAddressInputField.prototype.blur =
function() {
	this._input.blur();
};

ZmAddressInputField.prototype._setInputValue =
function(value) {
	DBG.println("aif", "SET input value to: " + AjxStringUtil.htmlEncode(value));
	this._input.value = value;
	this._resizeInput();
};

// Handles key events that occur in the INPUT.
ZmAddressInputField.prototype._keyDownCallback =
function(ev, aclv) {

	ev = DwtUiEvent.getEvent(ev);
	var key = DwtKeyEvent.getCharCode(ev);
	var propagate = true;
	var clearInput = false;
	
	if (DwtKeyMapMgr.hasModifier(ev) || ev.shiftKey) {
		return propagate;
	}

	// Esc in edit mode restores the original address to the bubble
	if (key == 27 && this._editMode) {
		DBG.println("aif", "_keyDownCallback found ESC key in edit mode");
		this._leaveEditMode(true);
		propagate = false;	// eat the event - eg don't let compose view catch Esc and pop the view
		clearInput = true;
	}
	// Tab checks to see if current input is an address
	else if (key == 9) {
		this._checkInput();
	}
	// Del removes selected bubbles, or selects last bubble if there is no input
	else if (key == 8) {
		DBG.println("aif", "_keyDownCallback found DEL key");
		propagate = !this.handleDelete(true);
	}
	// Left arrow selects last bubble if there is no input
	else if (key == 37) {
		this._selectBubbleBeforeInput();
	}
	// Handle case where user is leaving edit while we're not in strict mode
	// (in strict mode, aclv will call addrFoundCallback if it gets a Return)
	else if (!this._strictMode && (key == 3 || key == 13)) {
		var bubble = this._editMode && this._editModeBubble;
		if (bubble && !bubble.addrObj) {
			this._leaveEditMode();
			propagate = false;
			clearInput = true;
		}
	}

	if (clearInput && AjxEnv.isGeckoBased) {
		AjxTimedAction.scheduleAction(new AjxTimedAction(this, this._setInputValue, [""]), 20);
	}
	
	DwtUiEvent.setBehaviour(ev, !propagate, propagate);
	return propagate;
};

// need to do this on keyup, after character has appeared in the INPUT
ZmAddressInputField.prototype._keyUpCallback =
function(ev, aclv) {
	this._resizeInput();
};

ZmAddressInputField.prototype._addrFoundCallback =
function(aclv, addr, delim) {

	var index = null;
	if (this._editModeIndex != null) {
		index = this._getInsertionIndex(this._holder.childNodes[this._editModeIndex]);
	}

	this.addBubble({address:addr, index:index, skipNotify:true});
	this._leaveEditMode();
	return true;
};

ZmAddressInputField.prototype._selectionListener =
function(ev) {

	var bubble = ev.item;
	if (ev.detail == DwtEvent.ONDBLCLICK) {
		// Double-clicking a bubble moves it into edit mode. It is replaced by the
		// INPUT, which is moved to the bubble's position. The bubble's address fills
		// the input and is selected.
		this._checkInput();
		this._enterEditMode(bubble);
	}
	else {
		this._checkSelection(bubble);
		this._resetOperations();
	}
};

ZmAddressInputField.prototype._actionListener =
function(ev) {

	var bubble = ev.item;
	var menu = this.getActionMenu();
	ZmAddressInputField.menuContext.addrInput = this;
	ZmAddressInputField.menuContext.event = ev;
	ZmAddressInputField.menuContext.bubble = bubble;

	DBG.println("aif", "right sel bubble: " + bubble.id);
	this._resetOperations();

	var email = bubble.email;
	var contactsApp = appCtxt.getApp(ZmApp.CONTACTS);
	if (email && contactsApp) {
		// first check if contact is cached, and no server call is needed
		var contact = contactsApp.getContactByEmail(email);
		if (contact) {
			this._handleResponseGetContact(ev, contact);
		} else {
			menu.getOp(ZmOperation.CONTACT).setText(ZmMsg.loading);
			var respCallback = new AjxCallback(this, this._handleResponseGetContact, [ev]);
			contactsApp.getContactByEmail(email, respCallback);
		}
	}
	else {
		this._setContactText(false);
		menu.popup(0, ev.docX, ev.docY);
	}

	// if we are listening for outside mouse clicks, add the action menu to the elements
	// defined as "inside" so that clicking a menu item doesn't call our outside listener
	// and deselectAll before the menu listener does its thing
	if (this._selectionMode) {
		var omem = appCtxt.getOutsideMouseEventMgr();
		var omemParams = {
			id:					"ZmAddressInputField",
			obj:				menu,
			outsideListener:	this._outsideListener
		}
		DBG.println("aif", "ADD menu to outside listening " + this._input.id);
		omem.startListening(omemParams);
	}
};

ZmAddressInputField.prototype.getActionMenu =
function() {
	var menu = this._actionMenu || this.parent._bubbleActionMenu;
	if (!menu) {
		menu = this._actionMenu = this.parent._bubbleActionMenu = this._createActionMenu();
	}
	return menu;
};

ZmAddressInputField.prototype._createActionMenu =
function() {

	DBG.println("aif", "create action menu for " + this._input.id);
	var menuItems = this._getActionMenuOps();
	var menu = new ZmActionMenu({parent:this.shell, menuItems:menuItems});
	for (var i = 0; i < menuItems.length; i++) {
		var menuItem = menuItems[i];
		if (this._listeners[menuItem]) {
			menu.addSelectionListener(menuItem, this._listeners[menuItem]);
		}
	}
	menu.addPopdownListener(new AjxListener(this, this._menuPopdownListener));

	if (this._bubbleMenuCreatedCallback) {
		this._bubbleMenuCreatedCallback.run(this, menu);
	}

	return menu;
};

ZmAddressInputField.prototype._resetOperations =
function() {

	var menu = this.getActionMenu();
	if (menu) {
		var sel = this.getSelection();
		var bubble = (sel.length == 1) ? sel[0] : null;
		menu.enable(ZmOperation.DELETE, sel.length > 0);
		menu.enable(ZmOperation.EDIT, Boolean(bubble));
		var email = bubble && bubble.email;
		var ac = window.parentAppCtxt || window.appCtxt;
		menu.enable(ZmOperation.EXPAND, ac.isExpandableDL(email));
		menu.enable(ZmOperation.CONTACT, Boolean(bubble));
	}

	if (this._bubbleResetOperationsCallback) {
		this._bubbleResetOperationsCallback.run(this, menu);
	}
};

ZmAddressInputField.prototype._getActionMenuOps =
function() {
	return [
		ZmOperation.DELETE,
		ZmOperation.EDIT,
		ZmOperation.EXPAND,
		ZmOperation.CONTACT
	];
};

ZmAddressInputField.prototype._handleResponseGetContact =
function(ev, contact) {
	ZmAddressInputField.menuContext.contact = contact;
	this._setContactText(contact != null);
	this.getActionMenu().popup(0, ev.docX, ev.docY);
};

ZmAddressInputField.prototype._setContactText =
function(isContact) {
	var newOp = isContact ? ZmOperation.EDIT_CONTACT : ZmOperation.NEW_CONTACT;
	var newText = isContact ? null : ZmMsg.AB_ADD_CONTACT;
	ZmOperation.setOperation(this.getActionMenu(), ZmOperation.CONTACT, newOp, newText);
};

ZmAddressInputField.prototype._deleteListener =
function() {
	var addrInput = ZmAddressInputField.menuContext.addrInput;
	var sel = addrInput && addrInput.getSelection();
	if (sel && sel.length) {
		for (var i = 0; i < sel.length; i++) {
			addrInput.removeBubble(sel[i].id);
		}
	}
};

ZmAddressInputField.prototype._editListener =
function() {
	var addrInput = ZmAddressInputField.menuContext.addrInput;
	var bubble = ZmAddressInputField.menuContext.bubble;
	if (addrInput && bubble) {
		addrInput._enterEditMode(bubble);
	}
};

ZmAddressInputField.prototype._expandListener =
function() {
	var addrInput = ZmAddressInputField.menuContext.addrInput;
	var bubble = ZmAddressInputField.menuContext.bubble;
	if (addrInput && bubble) {
		addrInput.expandBubble(bubble.id, bubble.email);
	}
};

/**
 * If there's a contact for the participant, edit it, otherwise add it.
 *
 * @private
 */
ZmAddressInputField.prototype._contactListener =
function(ev) {
	var addrInput = ZmAddressInputField.menuContext.addrInput;
	if (addrInput) {
		var loadCallback = new AjxCallback(addrInput, addrInput._handleLoadContactListener);
		AjxDispatcher.require(["ContactsCore", "Contacts"], false, loadCallback, null, true);
	}
};

/**
 * @private
 */
ZmAddressInputField.prototype._handleLoadContactListener =
function() {

	var ctlr = window.parentAppCtxt ? window.parentAppCtxt.getApp(ZmApp.CONTACTS).getContactController() :
									  AjxDispatcher.run("GetContactController");
	var contact = ZmAddressInputField.menuContext.contact;
	if (contact) {
		if (contact.isLoaded) {
			ctlr.show(contact, true);
		} else {
			var callback = new AjxCallback(this, this._loadContactCallback);
			contact.load(callback);
		}
	} else {
		var contact = new ZmContact(null);
		var bubble = ZmAddressInputField.menuContext.bubble;
		var email = bubble && bubble.email;
		if (email) {
			contact.initFromEmail(email);
			ctlr.show(contact, true);
		}
	}
};

ZmAddressInputField.prototype._loadContactCallback =
function(resp, contact) {
	var ctlr = window.parentAppCtxt ? window.parentAppCtxt.getApp(ZmApp.CONTACTS).getContactController() :
									  AjxDispatcher.run("GetContactController");
	ctlr.show(contact);
};

ZmAddressInputField.prototype._menuPopdownListener =
function() {

	var bubble = ZmAddressInputField.menuContext.bubble;
	if (bubble) {
		bubble.setClassName(this._bubbleClassName);
	}

	if (this._selectionMode) {
		DBG.println("aif", "REMOVE menu from outside listening " + this._input.id);
		var omem = appCtxt.getOutsideMouseEventMgr();
		omem.stopListening({id:"ZmAddressInputField", obj:this.getActionMenu()});
	}

	// use a timer since popdown happens before listeners are called; alternatively, we could put the
	// code below at the end of every menu action listener
	AjxTimedAction.scheduleAction(new AjxTimedAction(this,
		function() {
			DBG.println("aif", "_menuPopdownListener");
			ZmAddressInputField.menuContext = {};
			this._bubbleList.clearRightSelection();
		}), 10);
};

ZmAddressInputField.prototype._enterEditMode =
function(bubble) {

	DBG.println("aif", "ENTER edit mode");
	if (this._editMode) {
		// user double-clicked a bubble while another bubble was being edited
		this._leaveEditMode();
	}

	this._editMode = true;
	this._editModeIndex = this._getBubbleIndex(bubble);
	DBG.println("aif", "MOVE input");
	this._holder.insertBefore(this._input, bubble.getHtmlElement());
	this.removeBubble(bubble.id, true);

	this._editModeBubble = bubble;
	this._setInputValue(bubble.address);

	// Chrome triggers BLUR after DBLCLICK, so use a timer to make sure select works
	AjxTimedAction.scheduleAction(new AjxTimedAction(this,
		function() {
			this.focus();
			this._input.select();
		}), 20);
};

ZmAddressInputField.prototype._leaveEditMode =
function(restore) {

	DBG.println("aif", "LEAVE edit mode");
	if (!this._editMode) { return; }

	if (this._holder.lastChild != this._input) {
		this._holder.appendChild(this._input);
	}
	var bubble = restore && this._editModeBubble;
	this._checkInput(bubble && bubble.address);
	this.focus();

	this._editMode = false;
	this._editModeIndex = this._editModeBubble = null;
	DBG.println("aif", "input value: " + AjxStringUtil.htmlEncode(this._input.value));
};

/**
 * This function should be called after selection of one or more bubbles has been done. It does two things:
 *
 * 1. If we are going from no selected bubbles to one or more, add global keydown and outside mouse event
 * listeners. If vice-versa, remove the listeners.
 *
 * 2. Select the text within selected bubbles. That operation is very focus-sensitive. Since it's done on
 * a timer, we have to be careful with what happens before.
 *
 * @param bubble
 * @private
 */
ZmAddressInputField.prototype._checkSelection =
function(bubble) {

	DBG.println("aif", "_checkSelection");
	this._checkSelectionCount();
	if (ZmAddressInputField.AUTO_SELECT_TEXT) {
		this._bubbleList.selectText(bubble, this);
	}
};

/**
 * There are two things we want to have in place when at least one bubble is selected: a global key
 * event handler (to trap Delete so it doesn't cause a browser back action), and an outside mouse
 * event listener so that we can deselect if the user clicks outside the address field.
 *
 * @private
 */
ZmAddressInputField.prototype._checkSelectionCount =
function() {

	var numSelected = this._bubbleList.getSelectionCount();
	DBG.println("aif", "selection count: " + numSelected);
	if (!this._selectionMode && numSelected > 0) {
		if (this._keyDownListener) {
			appCtxt.getKeyboardMgr().addListener(DwtEvent.ONKEYDOWN, this._keyDownListener);
		}
		var omem = appCtxt.getOutsideMouseEventMgr();
		var omemParams = {
			id:					"ZmAddressInputField",
			obj:				this,
			outsideListener:	this._outsideListener,
			noWindowBlur:		appCtxt.get(ZmSetting.IS_DEV_SERVER)
		}
		DBG.println("aif", "START outside listening " + this._input.id);
		omem.startListening(omemParams);
		this._selectionMode = true;
	}
	else if (this._selectionMode && numSelected == 0) {
		if (this._keyDownListener) {
			appCtxt.getKeyboardMgr().removeListener(DwtEvent.ONKEYDOWN, this._keyDownListener);
		}
		var omem = appCtxt.getOutsideMouseEventMgr();
		DBG.println("aif", "STOP outside listening " + this._input.id);
		omem.stopListening("ZmAddressInputField");
		this._selectionMode = false;
	}
};

// size the input to a bit more than its current content
ZmAddressInputField.prototype._resizeInput =
function() {

	var val = AjxStringUtil.htmlEncode(this._input.value);
	var holderWidth = Dwt.getSize(this._holder).x;
	var strW = AjxStringUtil.getWidth(val);
	if (AjxEnv.isWindows && (AjxEnv.isFirefox || AjxEnv.isSafari || AjxEnv.isChrome) ){
		// FF/Win: fudge factor since string is longer in INPUT than when measured in SPAN
		strW = strW * 1.2;
	}
	var pad = this._editMode ? ZmAddressInputField.INPUT_EXTRA_SMALL : ZmAddressInputField.INPUT_EXTRA;
	var inputWidth = Math.min(strW, holderWidth) + pad;
	if (this._editMode) {
		inputWidth = Math.max(inputWidth, ZmAddressInputField.INPUT_EXTRA);
	}
	Dwt.setSize(this._input, inputWidth, Dwt.DEFAULT);

	if (AjxEnv.isIE) {
		// TODO: make the INPUT line up with the SPANs vertically
	}
};

/**
 * Global key event handler which we use to point shortcut handling at us without actually
 * setting focus (which breaks auto-selection of text).
 *
 * Note: It's important (at least in FF) that the event for Delete does not propagate after
 * text has been auto-selected, since at that point the BODY has focus and the Delete gets
 * interpreted as a browser Back action. Normal shortcut handling will not propagate the
 * event.
 *
 * @private
 */
ZmAddressInputField.prototype._handleKeyDown =
function(ev) {

	if (appCtxt.getCurrentView() != this.parent) {
		appCtxt.getKeyboardMgr().removeListener(DwtEvent.ONKEYDOWN, this._keyDownListener);
		DBG.println("aif", "REMOVE keydown listener - view is not current");
	}
	else {
		ev = DwtUiEvent.getEvent(ev);
		ev.focusObj = this;
		DBG.println("aif", "SET focus obj in _handleKeyDown");
	}
};

ZmAddressInputField.prototype.hasFocus =
function(ev) {
	return true;
};

ZmAddressInputField.prototype.getKeyMapName =
function() {
	return "ZmAddressBubble";
};

ZmAddressInputField.prototype.handleKeyAction =
function(actionCode, ev) {

	var selCount = this.getSelectionCount();
	if (selCount == 0) {
		return true;
	}

	switch (actionCode) {

		case DwtKeyMap.DELETE:
			this.handleDelete();
			break;

		case DwtKeyMap.SELECT_NEXT:
			if (selCount == 1) {
				this.selectBubble(true);
			}
			break;

		case DwtKeyMap.SELECT_PREV:
			if (selCount == 1) {
				this.selectBubble(false);
			}
			break;

		default:
			return false;
	}

	return true;
};

// Returns an ordered list of bubbles
ZmAddressInputField.prototype._getBubbleList =
function() {

	var list = [];
	var children = this._holder.childNodes;
	for (var i = 0; i < children.length; i++) {
		var id = children[i].id;
		if (id && this._bubble[id]) {
			var bubble = DwtControl.fromElementId(id);
			if (bubble) {
				list.push(bubble);
			}
		}
	}
	
	this._bubbleList.set(list);
	return this._bubbleList;
};

// returns the index of the given bubble among all the holder's elements (not just bubbles)
ZmAddressInputField.prototype._getBubbleIndex =
function(bubble) {
	return AjxUtil.indexOf(this._holder.childNodes, bubble.getHtmlElement());
};

// returns the index of the INPUT among all the holder's elements
ZmAddressInputField.prototype._getInputIndex =
function() {
	return AjxUtil.indexOf(this._holder.childNodes, this._input);
};

/**
 * Selects the next or previous bubble relative to the selected one.
 *
 * @param {boolean}			next		if true, select next bubble; otherwise select previous bubble
 */
ZmAddressInputField.prototype.selectBubble =
function(next) {

	var sel = this.getSelection();
	var bubble = sel && sel.length && sel[0];
	if (!bubble) { return; }

	var index = this._getBubbleIndex(bubble);
	index = next ? index + 1 : index - 1;
	var children = this._holder.childNodes;
	var el = (index >= 0 && index < children.length) && children[index];
	if (el == this._dragInsertionBar) {
		index = next ? index + 1 : index - 1;
		el = (index >= 0 && index < children.length) && children[index];
	}
	if (el) {
		if (el == this._input) {
			this.setSelected(bubble, false);
			this.focus();
		}
		else {
			var newBubble = DwtControl.fromElement(el);
			if (newBubble) {
				this.setSelected(bubble, false);
				this.setSelected(newBubble, true);
				this._checkSelection();
			}
		}
	}
};

/**
 * Returns an ordered list of bubble addresses.
 *
 * @param {boolean}	asObjects	if true, return list of AjxEmailAddress
 */
ZmAddressInputField.prototype.getAddresses =
function(asObjects) {

	var addrs = [];
	var bubbles = this._getBubbleList().getArray();
	var ac = window.parentAppCtxt || window.appCtxt;
	for (var i = 0; i < bubbles.length; i++) {
		var bubble = bubbles[i];
		var addr = bubble.address;
		if (asObjects) {
			var addrObj = AjxEmailAddress.parse(addr) || new AjxEmailAddress("", null, addr);
			if (ac.isExpandableDL(bubble.email) || (bubble.match && bubble.match.isDL)) {
				addrObj.isGroup = true;
				addrObj.canExpand = true;
			}
			addrs.push(addrObj);
		}
		else {
			addrs.push(addr);
		}
	}
	return addrs;
};

ZmAddressInputField._outsideMouseDownListener =
function(ev, context) {
	var aif = context && context.obj;
	if (aif) {
		DBG.println("aif", "_outsideMouseDownListener: " + aif._input.id);
		aif.deselectAll();
	}
};

ZmAddressInputField._getAddrInputFromEvent =
function(ev) {
	var target = DwtUiEvent.getTarget(ev);
	return target && DwtControl.ALL_BY_ID[target._aifId];
};

/**
 * Since both the input and each of its bubbles has a drop listener, the target object may be
 * either of those object types. Dropping is okay if we're over a different type of input, or if
 * we're reordering bubbles within the same input.
 */
ZmAddressInputField.prototype._dropListener =
function(dragEv) {

	var sel = dragEv.srcData && dragEv.srcData.selection;
	if (!(sel && sel.length)) { return; }

	DBG.println("aif", "target obj: " + dragEv.uiEvent.dwtObj.toString());
	if (dragEv.action == DwtDropEvent.DRAG_ENTER) {
		DBG.println("aif", "DRAG_ENTER");
		var targetObj = dragEv.uiEvent.dwtObj;
		var targetInput = targetObj.isAddressBubble ? targetObj.addrInput : targetObj;
		var dragBubble = sel[0];
		if (dragBubble.type != this.type) {
			dragEv.doIt = true;
		}
		else if (targetInput._numBubbles <= 1) {
			dragEv.doIt = false;
		}
		if (dragEv.doIt && targetInput._numBubbles >= 1) {
			var idx = targetInput._getIndexFromEvent(dragEv.uiEvent);
			var bubbleIdx = targetInput._getBubbleIndex(dragBubble);
			DBG.println("aif", "idx: " + idx + ", bubbleIdx: " + bubbleIdx);
			if ((dragBubble.type == this.type) && (idx == bubbleIdx || idx == bubbleIdx + 1)) {
				dragEv.doIt = false;
			}
			else {
				this._setInsertionBar(idx);
			}
		}
		if (!dragEv.doIt) {
			this._setInsertionBar(null);
		}
	}
	else if (dragEv.action == DwtDropEvent.DRAG_LEAVE) {
		DBG.println("aif", "DRAG_LEAVE");
		this._setInsertionBar(null);
	}
	else if (dragEv.action == DwtDropEvent.DRAG_DROP) {
		DBG.println("aif", "DRAG_DROP");
		var sourceInput = dragEv.srcData.addrInput;
		var index = this._getInsertionIndex(this._dragInsertionBar);
		for (var i = 0; i < sel.length; i++) {
			var bubble = sel[i];
			var id = bubble.id;
			this.addBubble({bubble:bubble, index:index + i});
			sourceInput.removeBubble(id);
		}
		this._setInsertionBar(null);
	}
};

ZmAddressInputField.prototype._dragBoxListener =
function(ev) {
    // Check if user is using scroll bar rather than trying to drag.
    if (ev && ev.srcControl && this._holder) {
        var scrollWidth = this._holder.scrollWidth;  //returns width w/out scrollbar
        var scrollPos = scrollWidth + Dwt.getLocation(this._holder).x;
        var dBox = ev.srcControl.getDragBox();
        if (dBox) {
            DBG.println("aif", "DRAG_DROP x =" + dBox.getStartX() + " scrollWidth = " + scrollWidth);
            if (dBox.getStartX() > scrollPos) {
                DBG.println("aif", "DRAG_DROP x =" + dBox.getStartX() + " scrollPos = " + scrollPos);
                return false;
            }
        }
    }

	if (ev.action == DwtDragEvent.DRAG_INIT) {
		// okay to draw drag box if we have at least one bubble, and user isn't clicking in
		// the non-empty INPUT (might be trying to select text)
		return (this._numBubbles > 0 && (ev.target != this._input || this._input.value == ""));
	}
	else if (ev.action == DwtDragEvent.DRAG_START) {
		DBG.println("aif", "ZmAddressInputField DRAG_START");
		this.deselectAll();
	}
	else if (ev.action == DwtDragEvent.DRAG_MOVE) {
//		DBG.println("aif", "ZmAddressInputField DRAG_MOVE");
		var box = this._dragSelectionBox;
		for (var id in this._bubble) {
			var bubble = this._bubble[id];
			var span = bubble.getHtmlElement();
			var sel = Dwt.doOverlap(box, span);
			if (sel != this._bubbleList.isSelected(bubble)) {
				this.setSelected(bubble, sel);
			}
		}
	}
	else if (ev.action == DwtDragEvent.DRAG_END) {
		DBG.println("aif", "ZmAddressInputField DRAG_END");
		this._checkSelection();
		if (AjxEnv.isWindows && (this.getSelectionCount() == 0)) {
			this.blur();
			this.focus();
		}
	}
};

// Returns insertion index (among all elements) based on event coordinates
ZmAddressInputField.prototype._getIndexFromEvent =
function(ev) {

	var bubble, w, bx, idx;
	var bubble = (ev.dwtObj && ev.dwtObj.isAddressBubble) ? ev.dwtObj : null;
	if (bubble) {
		w = bubble.getSize().x;
		bx = ev.docX - bubble.getLocation().x;
		idx = this._getBubbleIndex(bubble);	// TODO: cache?
		return (bx > (w / 2)) ? idx + 1 : idx;
	}
	else {
		idx = 0;
		var children = this._holder.childNodes;
		for (var i = 0; i < children.length; i++) {
			var id = children[i].id;
			bubble = id && this._bubble[id];
			if (bubble) {
				w = bubble.getSize().x;
				bx = ev.docX - bubble.getLocation().x;
				if (bx < (w / 2)) {
					return idx;
				}
				else {
					idx++;
				}
			}
			else if (i < (children.length - 1)) {
				idx++;
			}
		}
		return idx;
	}
};

ZmAddressInputField.prototype._setInsertionBar =
function(index) {

	if (index == this._dragInsertionBarIndex) { return; }

	var bar = this._dragInsertionBar;
	if (index != null) {
		bar.style.display = "inline";
		var refElement = this._holder.childNodes[index];
		if (refElement) {
			this._holder.insertBefore(bar, refElement);
			this._dragInsertionBarIndex = index;
		}
	}
	else {
		bar.style.display = "none";
		this._dragInsertionBarIndex = null;
	}
};

ZmAddressInputField.prototype._getInsertionIndex =
function(element) {

	var bubbleIndex = 0;
	var children = this._holder.childNodes;
	for (var i = 0; i < children.length; i++) {
		var el = children[i];
		if (el == element) {
			break;
		}
		else if (el && this._bubble[el.id]) {
			bubbleIndex++;
		}
	}
	return bubbleIndex;
};




/**
 * Creates a bubble that contains an email address.
 * @constructor
 * @class
 * This class represents an object that allows various operations to be performed on an
 * email address within a compose or display context.
 *
 * @param {hash}				params		the hash of parameters:
 * @param {ZmAddressInputField}	parent		parent control
 * @param {string}				id			element ID for the bubble
 * @param {string}				className	CSS class for the bubble
 * @param {string}				address		email address to display in the bubble
 * @param {AjxEmailAddress}		addrObj		email address (alternative form)
 * @param {boolean}				canRemove	if true, an x will be provided to remove the address bubble
 * @param {boolean}				canExpand	if true, a + will be provided to expand the DL address
 * @param {boolean}				returnSpan	if true, return SPAN element rather than HTML
 * @param {string}				separator	address separator - hidden, present for copy of text (optional)
 *
 * @extends DwtControl
 */
ZmAddressBubble = function(params) {

	params = params || {};
	params.id = this.id = params.id || Dwt.getNextId();
	params.className = params.className || "addrBubble";
	DwtControl.call(this, params);

	this.type = params.type;
	this.isAddressBubble = true;

	var addrInput = this.addrInput = params.addrInput;
	var match = this.match = params.match;
	var addrObj = this.addrObj = params.addrObj || AjxEmailAddress.parse(params.address || (match && match.email));
	this.address = params.address || (addrObj && addrObj.toString());
	this.email = params.email = params.email || (addrObj && addrObj.getAddress()) || "";
	var ac = window.parentAppCtxt || window.appCtxt;
	this.canExpand = params.canExpand = params.canExpand || ac.isExpandableDL(this.email);
	
	this._createHtml(params);

	this._setEventHdlrs([DwtEvent.ONCLICK, DwtEvent.ONDBLCLICK,
						 DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT,
						 DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEMOVE, DwtEvent.ONMOUSEUP]);
	this.addListener(DwtEvent.ONCLICK, new AjxListener(this, this._clickListener));
	this.addListener(DwtEvent.ONDBLCLICK, new AjxListener(this, this._dblClickListener));
	this.addListener(DwtEvent.ONMOUSEUP, new AjxListener(this, this._mouseUpListener));

	if (addrInput) {
		var dragSrc = new DwtDragSource(Dwt.DND_DROP_MOVE);
		dragSrc.addDragListener(new AjxListener(this, this._dragListener));
		this.setDragSource(dragSrc);
	}
};

ZmAddressBubble.prototype = new DwtControl;
ZmAddressBubble.prototype.constructor = ZmAddressBubble;

ZmAddressBubble.prototype.toString =
function() {
	return "ZmAddressBubble";
};

ZmAddressBubble.prototype._createElement =
function() {
	return document.createElement("SPAN")
};

ZmAddressBubble.prototype._createHtml =
function(params) {

	var el = this.getHtmlElement();
	el.innerHTML = ZmAddressBubble.getContent(params);
	if (params.parentId) {
		el._aifId = params.parentId;
	}
};

/**
 * Returns HTML for the content of a bubble.
 *
 * @param {hash}				params		the hash of parameters:
 * @param {ZmAddressInputField}	parent		parent control
 * @param {string}				id			element ID for the bubble
 * @param {string}				className	CSS class for the bubble
 * @param {string}				address		email address to display in the bubble
 * @param {AjxEmailAddress}		addrObj		email address (alternative form)
 * @param {boolean}				canRemove	if true, an x will be provided to remove the address bubble
 * @param {boolean}				canExpand	if true, a + will be provided to expand the DL address
 * @param {boolean}				returnSpan	if true, return SPAN element rather than HTML
 * @param {string}				separator	address separator - hidden, present for copy of text (optional)
 */
ZmAddressBubble.getContent =
function(params) {

	var id = params.id;
	var addrObj = params.addrObj || AjxEmailAddress.parse(params.address) || params.address || ZmMsg.unknown;
	var fullAddress = AjxStringUtil.htmlEncode(addrObj.toString());
	var text = AjxStringUtil.htmlEncode(addrObj.toString(appCtxt.get(ZmSetting.SHORT_ADDRESS)));
	var selectId = id + "_select";
	var sep = params.separator ? AjxStringUtil.trim(params.separator) : "";
	
	var html = [], idx = 0;
	html[idx++] = "<span>";
	html[idx++] = "<span>" + text + " </span>";
	//span is not selectable in this area for IE (so Dwt.selectText would not work, since the range.select() call would not work. See bug 56731)
	//textarea is not so good for FF since the copy keeps the border. So keeping span for non IE.
	var selectElement = AjxEnv.isIE ? "textarea" : "span";
	html[idx++] = "<" + selectElement + " class='addrBubbleHide' id='" + selectId + "'>" + fullAddress + sep + " </" + selectElement + ">";
	html[idx++] = "</span>";
	var addrText = html.join("");

	var expandLinkText = "", removeLinkText = "";
	var style = "display:inline-block;cursor:pointer;";
	if (AjxEnv.isIE) {
		// hack - IE won't display block elements inline via inline-block
		style = style + "*display:inline;zoom:1;";
	}

	if (params.canExpand) {
		var addr = params.email || params.address;
		var expandLinkId = id + "_expand";
		var expandLink = 'ZmAddressInputField.expandBubble("' + id + '","' + addr + '");';
		var expStyle = style + "margin-right:3px;";
		var expandLinkText = AjxImg.getImageHtml("BubbleExpand", expStyle, "id='" + expandLinkId + "' onclick='" + expandLink + "'");
	}

	if (params.canRemove) {
		var removeLinkId = id + "_remove";
		var removeLink = 'ZmAddressInputField.removeBubble("' + id + '");';
		var removeLinkText = AjxImg.getImageHtml("BubbleDelete", style, "id='" + removeLinkId + "' onclick='" + removeLink + "'");
	}

	return expandLinkText + addrText + removeLinkText;
};

ZmAddressBubble.prototype._clickListener =
function(ev) {
	if (!this.list) { return; }
	if (this._dragging == DwtControl._NO_DRAG) {
		this.list._itemClicked(ev, this);
	}
};

ZmAddressBubble.prototype._dblClickListener =
function(ev) {
	if (!this.list) { return; }
	this.list._itemDoubleClicked(ev, this);
};

ZmAddressBubble.prototype._mouseUpListener =
function(ev) {
	if (!this.list) { return; }
	if (ev.button == DwtMouseEvent.RIGHT) {
		this.list._itemActioned(ev, this);
	}
};

ZmAddressBubble.prototype._getDragProxy =
function(dragOp) {

	var icon = document.createElement("div");
	icon.className = this._className;
	Dwt.setPosition(icon, Dwt.ABSOLUTE_STYLE);
	var count = this.addrInput.getSelectionCount(this);
	var content;
	if (count == 1) {
		var addrObj = AjxEmailAddress.parse(this.address) || this.address || ZmMsg.unknown;
		content = AjxStringUtil.htmlEncode(addrObj.toString(appCtxt.get(ZmSetting.SHORT_ADDRESS)));
	}
	else {
		content = AjxMessageFormat.format(ZmMsg.numAddresses, count);
	}
	icon.innerHTML = content;
	this.shell.getHtmlElement().appendChild(icon);
	Dwt.setZIndex(icon, Dwt.Z_DND);
	return icon;
};

ZmAddressBubble.prototype._dragListener =
function(ev) {
	if (ev.action == DwtDragEvent.SET_DATA) {
		ev.srcData = {selection: this.addrInput.getSelection(this),
					  addrInput: this.addrInput};
	}
};

ZmAddressBubble.prototype._dragOver =
function(ev) {
	this.addrInput._dragOver(ev);
};

/**
 * Gets the tool tip content.
 * 
 * @param	{Object}	ev		the hover event
 * @return	{String}	the tool tip content
 */
ZmAddressBubble.prototype.getToolTipContent =
function(ev) {

	var ttParams = {address:this.addrObj, ev:ev};
	var ttCallback = new AjxCallback(this,
		function(callback) {
			appCtxt.getToolTipMgr().getToolTip(ZmToolTipMgr.PERSON, ttParams, callback);
		});
	return {callback:ttCallback};
};




/**
 * Creates an empty bubble list.
 * @constructor
 * @class
 * This class manages selection events (click, double-click, and right-click) of a collection of bubbles, since
 * those events are typically meaningful within a group of bubbles. It maintains the visual state of the bubble
 * and notifies any listeners of the selection events. 
 * 
 * @param {string}	normalClass			class for an unselected bubble
 * @param {string}	selClass			class for a selected bubble
 * @param {string}	rightSelClass		class for a right-clicked bubble
 */
ZmAddressBubbleList = function(normalClass, selClass, rightSelClass) {
	
	this._normalClass = normalClass || "addrBubble";
	this._selClass = selClass || this._normalClass + "-" + DwtCssStyle.SELECTED;
	this._actionClass = rightSelClass || this._normalClass + "-" + DwtCssStyle.ACTIONED;

	this._evtMgr = new AjxEventMgr();
	this._selEv = new DwtSelectionEvent(true);
	this._actionEv = new DwtListViewActionEvent(true);

	this.reset();
};

ZmAddressBubbleList.prototype.toString =
function() {
	return "ZmAddressBubbleList";
};

ZmAddressBubbleList.prototype.set =
function(list) {
	
	this._bubbleList = [];
	var selected = {};
	this._numSelected = 0;
	for (var i = 0; i < list.length; i++) {
		var bubble = list[i];
		this._bubbleList.push(bubble);
		if (this._selected[bubble.id]) {
			selected[bubble.id] = true;
			this._numSelected++;
		}
	}
	this._selected = selected; 
};

ZmAddressBubbleList.prototype.getArray =
function(list) {
	return this._bubbleList;
};

ZmAddressBubbleList.prototype.add =
function(bubble, index) {
	AjxUtil.arrayAdd(this._bubbleList, bubble, index);
	bubble.list = this;
};

ZmAddressBubbleList.prototype.remove =
function(bubble) {
	AjxUtil.arrayRemove(this._bubbleList, bubble);
	bubble.list = null;
	if (bubble == this._rightSelBubble) {
		this._rightSelBubble = null;
	}
};

ZmAddressBubbleList.prototype.getBubble =
function(index) {
	index = index || 0;
	return this._bubbleList[index];
};

/**
 * Adds a selection listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
ZmAddressBubbleList.prototype.addSelectionListener =
function(listener) {
	this._evtMgr.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes a selection listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
ZmAddressBubbleList.prototype.removeSelectionListener =
function(listener) {
	this._evtMgr.removeListener(DwtEvent.SELECTION, listener);
};

/**
 * Adds an action listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
ZmAddressBubbleList.prototype.addActionListener =
function(listener) {
	this._evtMgr.addListener(DwtEvent.ACTION, listener);
};

/**
 * Removes an action listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
ZmAddressBubbleList.prototype.removeActionListener =
function(listener) {
	this._evtMgr.removeListener(DwtEvent.ACTION, listener);
};

ZmAddressBubbleList.prototype._itemClicked =
function(ev, bubble) {

	if (ev.shiftKey) {
		if (this._lastSelectedId) {
			var select = false;
			for (var i = 0, len = this._bubbleList.length; i < len; i++) {
				var b = this._bubbleList[i];
				if (b == bubble || b.id == this._lastSelectedId) {
					if (select) {
						this.setSelected(b, true);
						select = false;
						continue;
					}
					select = !select;
				}
				this.setSelected(b, select);
			}
		}
	}
	else if (ev.ctrlKey) {
		this.setSelected(bubble, !this._selected[bubble.id]);
		if (this._selected[bubble.id]) {
			this._lastSelectedId = bubble.id;
		}
	}
	else {
		var wasOnlyOneSelected = ((this.getSelectionCount() == 1) && this._selected[bubble.id]);
		this.deselectAll();
		this.setSelected(bubble, !wasOnlyOneSelected);
		this._lastSelectedId = wasOnlyOneSelected ? null : bubble.id;
	}

	if (this._evtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
		DwtUiEvent.copy(this._selEv, ev);
		this._selEv.item = bubble;
		this._selEv.detail = DwtEvent.ONCLICK;
		this._evtMgr.notifyListeners(DwtEvent.SELECTION, this._selEv);
	}
};

ZmAddressBubbleList.prototype._itemDoubleClicked =
function(ev, bubble) {

	if (this._evtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
		DwtUiEvent.copy(this._selEv, ev);
		this._selEv.item = bubble;
		this._selEv.detail = DwtEvent.ONDBLCLICK;
		this._evtMgr.notifyListeners(DwtEvent.SELECTION, this._selEv);
	}
};

ZmAddressBubbleList.prototype._itemActioned =
function(ev, bubble) {

	this._rightSelBubble = bubble;
	bubble.setClassName(this._actionClass);
	if (this._evtMgr.isListenerRegistered(DwtEvent.ACTION)) {
		DwtUiEvent.copy(this._actionEv, ev);
		this._actionEv.item = bubble;
		this._evtMgr.notifyListeners(DwtEvent.ACTION, this._actionEv);
	}
};

/**
 * Sets selection of the given bubble.
 *
 * @param {ZmAddressBubble}	bubble		bubble to select
 * @param {boolean} 		selected	if true, select the bubble, otherwise deselect it
 */
ZmAddressBubbleList.prototype.setSelected =
function(bubble, selected) {

	if (!bubble) { return; }
	if (selected == Boolean(this._selected[bubble.id])) { return; }

	this._selected[bubble.id] = selected;
	bubble.setClassName(selected ? this._selClass : this._normalClass);

	this._numSelected = selected ? this._numSelected + 1 : this._numSelected - 1;
	DBG.println("aif", "**** selected: " + selected + ", num = " + this._numSelected);
};

ZmAddressBubbleList.prototype.isSelected =
function(bubble) {
	return Boolean(bubble && this._selected[bubble.id]);
};

/**
 * Returns a list of the currently selected bubbles. If a bubble has been selected via right-click,
 * but is not part of the current left-click selection, only it will be returned.
 *
 * @param {ZmAddressBubble}	bubble	reference bubble
 */
ZmAddressBubbleList.prototype.getSelection =
function(bubble) {

	var ref = bubble || this._rightSelBubble;
	var refIncluded = false;
	var sel = [];
	for (var i = 0; i < this._bubbleList.length; i++) {
		var bubble = this._bubbleList[i];
		if (this._selected[bubble.id]) {
			sel.push(bubble);
			if (bubble == ref) {
				refIncluded = true;
			}
		}
	}
	sel = (ref && !refIncluded) ? [ref] : sel;
	DBG.println("aif", "getSelection, sel length: " + sel.length);

	return sel;
};

ZmAddressBubbleList.prototype.getSelectionCount =
function(bubble) {
	return bubble ? this.getSelection(bubble).length : this._numSelected;
};

ZmAddressBubbleList.prototype.deselectAll =
function() {
	DBG.println("aif", "deselectAll");
	var sel = this.getSelection();
	for (var i = 0, len = sel.length; i < len; i++) {
		this.setSelected(sel[i], false);
	}
	this._selected = {};
	this._numSelected = 0;
};

ZmAddressBubbleList.prototype.clearRightSelection =
function() {
	this._rightSelBubble = null;
};

ZmAddressBubbleList.prototype.reset =
function(list) {
	this._bubbleList = [];
	this._selected = {};
	this._numSelected = 0;
};

ZmAddressBubbleList.prototype.selectText =
function(bubble, focusObj) {

	// programmatic text selection works consistently only if the BODY is the active element,
	// so blur whatever currently has focus
	if (document.activeElement && document.activeElement.blur && document.activeElement != document.body) {
			document.activeElement.blur();
	}
	AjxTimedAction.scheduleAction(new AjxTimedAction(this,
		function() {
			DBG.println("aif", "select text");
			// only FF supports multiple selected ranges; if not FF, select the bubble that was clicked
			var sel = [];
			if (AjxEnv.isGeckoBased) {
				sel = this.getSelection();
			}
			else if (this.isSelected(bubble)) {
				sel = [bubble];
			}
			if (sel && sel.length) {
				Dwt.deselectText();
				for (var i = 0, len = sel.length; i < len; i++) {
					var selectId = sel[i] && (sel[i]._htmlElId + "_select");
					var node = selectId && document.getElementById(selectId);
					if (node) {
						Dwt.selectText(node);
					}
				}
			}
		}), 10);
};
