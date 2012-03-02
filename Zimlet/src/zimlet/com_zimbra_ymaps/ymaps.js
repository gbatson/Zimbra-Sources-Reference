/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

//////////////////////////////////////////////////////////////
//  Zimlet to handle integration with a Yahoo! Maps         //
//  @author Kevin Henrikson                                 //
//////////////////////////////////////////////////////////////

function Com_Zimbra_YMaps() {
}

Com_Zimbra_YMaps.prototype = new ZmZimletBase();
Com_Zimbra_YMaps.prototype.constructor = Com_Zimbra_YMaps;

Com_Zimbra_YMaps.prototype.init =
function() {
	if (ZmAssistant && ZmAssistant.register) ZmAssistant.register(new Com_Zimbra_YMaps_Asst());
};

// Y! Maps Webservice URL
Com_Zimbra_YMaps.URL = "http://api.local.yahoo.com/MapsService/V1/mapImage?appid=ZimbraMail&zoom=4&image_height=245&image_width=345&location=";

// Map image URI cache
Com_Zimbra_YMaps.CACHE = [];

// Panel Zimlet Methods
// Called by the Zimbra framework when the Ymaps panel item was double clicked
Com_Zimbra_YMaps.prototype.doubleClicked =
function() {
	this.singleClicked();
};

// Called when clicked on matched text
Com_Zimbra_YMaps.prototype.clicked =
function(spanElem, contentObj, matchContext, canvas) {
	var url = "http://maps.yahoo.com/maps_result?addr=";
	var addr = contentObj.replace("\n"," ").replace("\r"," ");
	canvas = window.open(url+escape(addr));
};

// Called by the Zimbra framework when the Ymaps panel item was clicked
Com_Zimbra_YMaps.prototype.singleClicked =
function() {
	var editorProps = [{
		label: "Address",
		name: "address",
		type: "string",
		minLength: 2,
		maxLength: 200
	}];
	if (!this._dlg_propertyEditor) {
		var view = new DwtComposite(this.getShell());
		var pe = this._propertyEditor = new DwtPropertyEditor(view, true);
		pe.initProperties(editorProps);
		var dialog_args = {
			title: "Yahoo Maps: Enter Address",
			view: view
		};
		this._dlg_propertyEditor = this._createDialog(dialog_args);
		var dlg = this._dlg_propertyEditor;
		pe.setFixedLabelWidth();
		pe.setFixedFieldWidth();
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
				new AjxListener(this, function() {
					if (!pe.validateData()) { return; }
					this._getDisplayCustomMap();
				}));
	}
	this._dlg_propertyEditor.popup();
};

Com_Zimbra_YMaps.prototype._getDisplayCustomMap =
function() {
	this._dlg_propertyEditor.popdown();
	this._displayDialogMap(this._propertyEditor.getProperties().address);
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;
};

Com_Zimbra_YMaps.prototype._displayDialogMap = 
function(address) {
	var view = new DwtComposite(this.getShell());

	var dialog_args = {
		view: view,
		title: "Yahoo Map",
		standardButtons: [DwtDialog.OK_BUTTON]
	};

	var dlg = this._createDialog(dialog_args);
	dlg.popup();
	dlg.setButtonListener(DwtDialog.OK_BUTTON,
			new AjxListener(this, function() {
				dlg.popdown();
				dlg.dispose();
			}));

	var div = document.createElement("div");
	view.getHtmlElement().appendChild(div);

	this.toolTipPoppedUp(null, address, null, div);
};

// Content Object Methods

Com_Zimbra_YMaps.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	canvas.innerHTML = [
		'<center><img width="345" height="245" id="',
		ZmZimletBase.encodeId(obj),
		'" src="',
		this.getResource('blank_pixel.gif'),
		'"/></center>'
	].join("");

	if (Com_Zimbra_YMaps.CACHE[obj+"img"]) {
		Com_Zimbra_YMaps._displayImage(Com_Zimbra_YMaps.CACHE[obj+"img"], obj);
	} else {
		var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(Com_Zimbra_YMaps.URL + AjxStringUtil.urlComponentEncode(obj));
		DBG.println(AjxDebug.DBG2, "Com_Zimbra_YMaps URL: " + url);
		AjxRpc.invoke(null, url, null, new AjxCallback(this, Com_Zimbra_YMaps._callback, obj), true);
	}
};

// Private Methods

Com_Zimbra_YMaps._displayImage = 
function(img_src, obj) {
	var imgEl = document.getElementById(ZmZimletBase.encodeId(obj));
	imgEl.style.backgroundImage = "url("+img_src+")";

	if (!Com_Zimbra_YMaps.CACHE[obj+"img"]) {
		Com_Zimbra_YMaps.CACHE[obj+"img"] = img_src;
	}
};

Com_Zimbra_YMaps._callback = 
function(obj, result) {
	var r = result.text;
	var url = r.substring(r.indexOf("http://gws"),r.indexOf("</Result>"));
	url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	Com_Zimbra_YMaps._displayImage(url, obj);
};


//////////////////////////////////////////////////////////////////////////
// Zimlet assistant class
// - used by the Assistant dialog to run games via "command-line"
//////////////////////////////////////////////////////////////////////////
function Com_Zimbra_YMaps_Asst() {
	// XXX: localize later (does NOT belong in ZmMsg.properties)
	ZmAssistant.call(this, "Yahoo Maps", "map", "Map an address using Yahoo Maps");
};

Com_Zimbra_YMaps_Asst.prototype = new ZmAssistant();
Com_Zimbra_YMaps_Asst.prototype.constructor = Com_Zimbra_YMaps_Asst;

Com_Zimbra_YMaps_Asst.prototype.okHandler =
function(dialog) {
	// get reference to the ymaps zimlet
	var zm = appCtxt.getZimletMgr();
	var zimlet = zm ? zm._ZIMLETS_BY_ID["com_zimbra_ymaps"] : null;
	if (zimlet && this._address) {
		zimlet.handlerObject.toolTipPoppedUp(null, this._address, null, dialog.getAssistantDiv());
	}
	return false;
};

Com_Zimbra_YMaps_Asst.prototype.handle =
function(dialog, verb, args) {
	this._address = args;
	var valid = args.length > 0;
	dialog._setOkButton("Map", true, valid);
};
