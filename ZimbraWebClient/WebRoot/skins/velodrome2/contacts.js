/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

VelodromeSkin.prototype._getContactListToolBarOps =
function() {
	return [ZmOperation.NEW_MENU,
			ZmOperation.SEP,
			ZmOperation.EDIT,
			ZmOperation.SEP,
			ZmOperation.DELETE, ZmOperation.PRINT,
			ZmOperation.SEP,
			ZmOperation.TAG_MENU];
			//ZmOperation.SEP,
			//ZmOperation.VIEW_MENU];
};

VelodromeSkin.prototype._getContactListActionMenuOps =
function() {
	var list = this._participantOps();
	list.push(ZmOperation.SEP,
				ZmOperation.TAG_MENU,
				ZmOperation.DELETE,
				ZmOperation.PRINT_CONTACT);
	return list;
};

VelodromeSkin.prototype.addRow =
function(itemDef, index) {
	arguments.callee.func.apply(this, arguments); // Call overridden function

	if (this._rowCount >= this._maxRows) return;
	if (index == null) index = this._rowCount - 1;

	itemDef = itemDef || (this._rowDef && AjxUtil.createProxy(this._rowDef));
	if (!itemDef) return;

	itemDef.id = itemDef.id || this._items[index].id; // Hack: get the id of the first child in the row

	var helpDef = {};
	helpDef.id = itemDef.id+"_helptext";
	helpDef.visible = "true";
	var helpLabel = this._registerControl(helpDef,null,null,null,null,"DwtLabel");

	var item = this._items[itemDef.id];
	item._helpId = helpDef.id;
	this._setControlIds(item.id, index);
	
	if (this._itemDef.id == "EMAIL")
		this._setFirstHelpText(ZmMsg.contactEditAddRemoveHelp);
}

VelodromeSkin.prototype.removeRow =
function(indexOrId) {
	arguments.callee.func.apply(this, arguments); // Call overridden function
	if (this._itemDef.id == "EMAIL")
		this._setFirstHelpText(ZmMsg.contactEditAddRemoveHelp);
}

VelodromeSkin.prototype._setFirstHelpText =
function(text) {
	for (var i=0; i<this._rowCount; i++) {
		var helpId = this._items[i]._helpId;
		if (helpId) {
			var helpLabel = this._items[helpId].control;
			if (helpLabel)
				helpLabel.setText((i==0) ? text : "");
		}
	}
}

VelodromeSkin.prototype._setControlIds = function(rowId, index) {
	arguments.callee.func.apply(this, arguments);
	
	var id = [this.getHTMLElId(), index].join("_");
	var item = this._items[rowId];

	var helpLabel = this._items[item._helpId];
	this._setControlId(helpLabel && helpLabel.control, id+"_helptext");
};

VelodromeSkin.prototype._handleContactsLoad = function() {

	// override/add API to ZmContactListController
	var proto = window.ZmContactListController && ZmContactListController.prototype
	if (proto) {
		this.overrideAPI(proto, "_getToolBarOps", this._getContactListToolBarOps);
		this.overrideAPI(proto, "_getActionMenuOps", this._getContactListActionMenuOps);
	}

	var proto = window.ZmEditContactViewInputSelectRows && ZmEditContactViewInputSelectRows.prototype;
	if (proto) {
		this.overrideAPI(proto, "addRow", this.addRow);
		this.overrideAPI(proto, "removeRow", this.removeRow);
		this.overrideAPI(proto, "_setControlIds", this._setControlIds);
		this.overrideAPI(proto, "_setFirstHelpText", this._setFirstHelpText);
	}

	ZmEditContactViewRows.prototype.ROW_TEMPLATE = "abook.Contacts#ZmEditContactViewRow";
};

VelodromeSkin.prototype._handleContactsCoreLoad = function() {
	ZmOperation.NEW_ITEM_KEY[ZmOperation.NEW_CONTACT] = "newContact";
	ZmOperation.NEW_ITEM_KEY[ZmOperation.NEW_GROUP] = "newGroup";
};

AjxDispatcher.addPackageLoadFunction("Contacts", new AjxCallback(skin, skin._handleContactsLoad));
AjxDispatcher.addPackageLoadFunction("ContactsCore", new AjxCallback(skin, skin._handleContactsCoreLoad));
