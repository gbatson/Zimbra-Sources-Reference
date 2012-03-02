/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010 Zimbra, Inc.
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
 * Creates an empty shortcuts page.
 * @constructor
 * @class
 * This class represents a page which allows user to modify the trusted addresses/domain list
 * <p>
 * Only a single pref (the user's shortcuts gathered together in a string)
 * is represented.</p>
 *
 * @author Santosh Sutar
 *
 * @param {DwtControl}	parent			the containing widget
 * @param {object}	section			the page
 * @param {ZmPrefController}	controller		the prefs controller
 *
 * @extends		ZmPreferencesPage
 *
 * @private
 */
ZmTrustedPage = function(parent, section, controller) {
	ZmPreferencesPage.apply(this, arguments);
};

ZmTrustedPage.prototype = new ZmPreferencesPage;
ZmTrustedPage.prototype.constructor = ZmTrustedPage;

ZmTrustedPage.prototype.toString =
function () {
    return "ZmTrustedPage";
};

ZmTrustedPage.prototype.showMe =
function() {
	ZmPreferencesPage.prototype.showMe.call(this);

	if (!this._initialized) {
		this._initialized = true;
	}
};

ZmTrustedPage.prototype._setupCustom =
function(id, setup, value) {
	var el = document.getElementById([this._htmlElId, id].join("_"));
	if (!el) { return; }

	if (id == ZmSetting.TRUSTED_ADDR_LIST) {
		this._trustedListControl = new ZmWhiteBlackList(this, id, "TrustedList");
        var arrTrustedList = [],
            trustedList = appCtxt.get(ZmSetting.TRUSTED_ADDR_LIST);
        if(trustedList && trustedList[0]) {
            arrTrustedList = trustedList[0].split(",");
        }
        this._trustedListControl.loadFromJson(arrTrustedList);
		this._replaceControlElement(el, this._trustedListControl);
	}
};

ZmTrustedPage.prototype.addItem =
function(addr) {
    if(addr && this._trustedListControl) {
        this._trustedListControl.loadFromJson([addr]);
    }
};

ZmTrustedPage.prototype.reset =
function(useDefaults) {
	ZmPreferencesPage.prototype.reset.apply(this, arguments);

	if (this._trustedListControl) {
		this._trustedListControl.reset();
	}
};

ZmTrustedPage.prototype.isDirty =
function() {
	var isDirty = ZmPreferencesPage.prototype.isDirty.call(this);
	return (!isDirty) ? this.isTrustedListDirty() : isDirty;
};

ZmTrustedPage.prototype.isTrustedListDirty =
function() {
	if (this._trustedListControl) {
		return this._trustedListControl.isDirty();
	}
	return false;
};

ZmTrustedPage.prototype.addCommand =
function(batchCmd) {
    if(this._trustedListControl && this._trustedListControl.isDirty()) {
        var value = this._trustedListControl.getValue(),
            soapDoc = AjxSoapDoc.create("ModifyPrefsRequest", "urn:zimbraAccount"),
            node = soapDoc.set("pref", value),
            respCallback = new AjxCallback(this, this._postSaveBatchCmd, [value]);
        node.setAttribute("name", "zimbraPrefMailTrustedSenderList");
        batchCmd.addNewRequestParams(soapDoc, respCallback);
    }
};

ZmTrustedPage.prototype._postSaveBatchCmd =
function(value) {
    appCtxt.set(ZmSetting.TRUSTED_ADDR_LIST, [value]);
    var settings = appCtxt.getSettings();
    var trustedListSetting = settings.getSetting(ZmSetting.TRUSTED_ADDR_LIST);
    trustedListSetting._notify(ZmEvent.E_MODIFY); 
    if(this._trustedListControl) {
        this._trustedListControl.saveLocal();
    }
};