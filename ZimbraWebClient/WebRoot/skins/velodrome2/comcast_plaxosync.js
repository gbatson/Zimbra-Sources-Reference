/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */

Comcast_plaxosync = function() {
	this.requestmgr = new Comcast_plaxosync_requestmgr();
	window.Comcast_plaxosync_instance = this;
};

Comcast_plaxosync.DISPLAY_IFRAME = true;

Comcast_plaxosync.prototype.toString = function() {
	return "Comcast_plaxosync";
};



Comcast_plaxosync.handleFrameData = function(address, subject, url) {
	if (AjxUtil.isString(address)) {
		if (url) Comcast_plaxosync_instance.setIframeUrl(url);
		var data = {action: ZmOperation.NEW_MESSAGE, toOverride: address, subjOverride: subject || ""};
		AjxDispatcher.run("GetComposeController").doAction(data);
	}
};

Comcast_plaxosync.prototype.init = function() {
	var listener = new AjxListener(this, this.init2)
	ZmZimbraMail.addListener(ZmAppEvent.PRE_STARTUP, listener);
	ZmZimbraMail.addListener(ZmAppEvent.POST_STARTUP, listener);
};


Comcast_plaxosync.prototype.init2 = function() {
	if (!this._init2run) {
		this._init2run = true;
		if (Comcast_plaxosync.DISPLAY_IFRAME)
			this._createIframeView();
		this.requestmgr.init();
	}
};

Comcast_plaxosync.PLAXO_URL_SHOW_ALL = window.PLAXO_URL_BASE+"/ab/contactList/?skin=none";
Comcast_plaxosync.PLAXO_URL_SHOW_CONTACT = window.PLAXO_URL_BASE+"/ab/home/contactView/{0}?skin=none";
// Comcast_plaxosync.PLAXO_URL_SHOW_SEARCH = window.PLAXO_URL_BASE+"/ab/?search={0}&query=Search&src=addressBookSearch&skin=none";
Comcast_plaxosync.PLAXO_URL_SHOW_SEARCH = window.PLAXO_URL_BASE+"/ab/contactList?skin=none&query=Search&src=addressBookSearch#search={0}";
Comcast_plaxosync.PLAXO_URL_NEW_CONTACT = window.PLAXO_URL_BASE+"/ab/home/contactAdd/?skin=none&name={0}&email={1}&phone={2}";
//Comcast_plaxosync.PLAXO_URL_EDIT_CONTACT = window.PLAXO_URL_BASE+"/ab/home/contactView/{0}?editMode=1&skin=none";
Comcast_plaxosync.PLAXO_URL_EDIT_CONTACT = window.PLAXO_URL_BASE+"/xfc/editContact?e={1}&n={0}&skin=none";
//Comcast_plaxosync.PLAXO_URL_NEW_GROUP = window.PLAXO_URL_BASE+"/ab/contactList#createGroup=1&skin=none";
Comcast_plaxosync.PLAXO_URL_NEW_GROUP = window.PLAXO_URL_BASE+"/ab/contactList?skin=none#createGroup=1";

Comcast_plaxosync.prototype._createIframeView = function() {
	if (Comcast_plaxosync.DISPLAY_IFRAME) {
		this.viewName = "PlaxoFrame";
		this._iframeView = new ZmUpsellView({parent:appCtxt.getShell(), posStyle:Dwt.ABSOLUTE_STYLE, className: 'ZmUpsellView'});
		var el = this._iframeView.getHtmlElement();
		var htmlArr = [];
		var idx = 0;
		htmlArr[idx++] = "<iframe id='iframe_";
		htmlArr[idx++] = this._iframeView.getHTMLElId();
		htmlArr[idx++] = "' ";
		if (this._iframeSrc) {
			htmlArr[idx++] = "src='";
		//	htmlArr[idx++] = this._iframeSrc;
			htmlArr[idx++] = "'";
		}
		htmlArr[idx++] = " width='100%' height='100%' frameborder='0'>";
		el.innerHTML = htmlArr.join("");

		// ZmOperation.registerOp("SHOW_ALL_CONTACTS", {image:"SharedContactsFolder"});
		// this._toolbar = new ZmButtonToolBar({parent: appCtxt.getShell(), buttons:[ZmOperation.SHOW_ALL_CONTACTS]});
		// var op = this._toolbar.getOp(ZmOperation.SHOW_ALL_CONTACTS);
		// op.setText("Show all contacts");
		// op.addSelectionListener(new AjxListener(this, this.setIframeUrl, [Comcast_plaxosync.PLAXO_URL_SHOW_ALL, true]));

		var elements = {};
		elements[ZmAppViewMgr.C_APP_CONTENT_FULL] = this._iframeView;
		// elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		var callbacks = {}
		callbacks[ZmAppViewMgr.CB_POST_SHOW] = new AjxCallback(this, this.displayIframeView);
		appCtxt.getAppViewMgr().createView({viewId:this.viewName, isAppView:"Contacts", elements:elements, isTransient:false, callbacks:callbacks});

		var iframe = this.getIframe();
		iframe.onload = null;
	}
};

Comcast_plaxosync.prototype.displayIframeView =
function(appName) {
	if (Comcast_plaxosync.DISPLAY_IFRAME) {
		var title = [ZmMsg.zimbraTitle, "Address Book"].join(": ");
		Dwt.setTitle(title);
	}
};

Comcast_plaxosync.prototype.getIframe = function() {
	if (!this._iframe)
		this._iframe = document.getElementById("iframe_" + this._iframeView.getHTMLElId());
	return this._iframe;
};

Comcast_plaxosync.prototype.getIframeView = function() {
	return this._iframeView;
};

/*
 * new method with IE specific support
 *
 */
Comcast_plaxosync.prototype.setIframeUrl = function(url, display) {
     if (Comcast_plaxosync.DISPLAY_IFRAME) { 
        if (url) this._iframeSrc = url;
        if (this._iframeView) { 
            var iframe = this.getIframe(); 
            if (iframe) { 
                var avm = appCtxt.getAppViewMgr();
                 if (display && avm.getCurrentViewId() != this.viewName) {
                     appCtxt.getAppChooser().setSelected("Contacts");
                     if (url) {
                        var handler = AjxCallback.simpleClosure(this.displayIframe, this);
                        if (AjxEnv.isIE) {
                            iframe.attachEvent('onload', handler);
                        } else { 
                            Dwt.setHandler(iframe, "onload", handler);
                        } 
                        this._displayLoading();
                     } 
                } else { 
                    Dwt.clearHandler(iframe, "onload"); 
                } 
                if (url) {
                    iframe.src = url;
                } 
            } 
        }
     }
};

/*
 * old method without IE specific support
 *
Comcast_plaxosync.prototype.setIframeUrl = function(url, display) {
	if (Comcast_plaxosync.DISPLAY_IFRAME) {
		if (url) this._iframeSrc = url;
		if (this._iframeView) {
			var iframe = this.getIframe();
			if (iframe) {
				var avm = appCtxt.getAppViewMgr();
				if (display && avm.getCurrentViewId() != this.viewName) {
					appCtxt.getAppChooser().setSelected("Contacts");
					if (url) {
						Dwt.setHandler(iframe, "onload", AjxCallback.simpleClosure(this.displayIframe, this));
						this._displayLoading();
					}
				} else {
					Dwt.clearHandler(iframe, "onload");
				}
				if (url)
					iframe.src = url;
			}
		}
	}
};
 *
 */

Comcast_plaxosync.prototype.displayIframe = function() {
	var avm = appCtxt.getAppViewMgr();
	avm.popView(ZmId.VIEW_LOADING, true);
	avm.pushView(this.viewName);
	appCtxt.getAppChooser().setSelected("Contacts");
};
Comcast_plaxosync.prototype._displayLoading = function() {
	if (!appCtxt.inStartup)
		appCtxt.getAppViewMgr().pushView(ZmId.VIEW_LOADING, true);
};

Comcast_plaxosync.prototype.add = function(object, funcname, newfunc) {
    newfunc = newfunc || this[funcname];
    if (newfunc) {
        var oldfunc = object[funcname];
        object[funcname] = function() {
			var args = arguments;
            args.push(oldfunc.apply(this, arguments));
            return newfunc.apply(this, args);
        }
        object[funcname].func = oldfunc;
    }
};

//-----------------------------------------------------------------------------

Comcast_plaxosync.parseQuery =
function(query) {
	locFound = /.*:.*/.test(query);
	match = locFound && /(.*)\(.*:(.*)\)$/.exec(query);
	return {
		location : (match && AjxStringUtil.trim(match[2])) || (locFound && query) || "",
		searchfor : (match && AjxStringUtil.trim(match[1])) || (!locFound && query) || ""
	};
};

Comcast_plaxosync.getFirstAttr = 
function(attrs, accepted, def) {
	for (var i=0; i<accepted.length; i++) {
		var key = accepted[i];
		if (attrs[key])
			return attrs[key];
	}
	return def;
}

//-----------------------------------------------------------------------------

// Overrides
// ZmRequestMgr.prototype.sendRequest =
VelodromeSkin.prototype.sendRequest =
function(params) {
	var obj = params.jsonObj;
	var searchTaken;
	if (obj) {
		var requestMgr = window.plaxosync.requestmgr;
		var callback = new AjxCallback(this, this._handleDataReturn, params);

		if (obj.CreateContactRequest) {
			requestMgr.createContactRequest(obj.CreateContactRequest, callback);
			delete obj.CreateContactRequest;
		}
		if (obj.ModifyContactRequest) {
			requestMgr.modifyContactRequest(obj.ModifyContactRequest, callback);
			delete obj.ModifyContactRequest;
		}
		if (obj.ContactActionRequest) {
			requestMgr.contactActionRequest(obj.ContactActionRequest, callback);
			delete obj.ContactActionRequest;
		}
		/*if (obj.SearchRequest) {
			if (obj.SearchRequest.types) {
				var types = obj.SearchRequest.types.split(",");
				if (AjxUtil.indexOf(types, "contact", true)!=-1) {
					AjxUtil.arrayRemove(types, "contact");
					obj.SearchRequest.types = types.join(",");
					searchTaken = true;
				}
			}
			if (obj.SearchRequest.query=="in:contacts") {
				obj.SearchRequest.query = "";
				searchTaken = true;
			}
			if (searchTaken) {
				requestMgr.searchRequest(obj.SearchRequest, callback);
			}
		}*/
		/*if (obj.AutoCompleteRequest) {
			requestMgr.autoCompleteRequest(obj.AutoCompleteRequest, callback);
			delete obj.AutoCompleteRequest;
		}*/

		for (var s in obj) {
			if (obj[s] && (s!="SearchRequest" || !searchTaken)) { // If any remain, call overridden function
				return this.sendRequest.func.call(this, params);
			}
		}
	} else {
		return this.sendRequest.func.call(this, params);
	}
};

// Doesn't override anything, but should be placed in ZmRequestMgr anyway
VelodromeSkin.prototype._handleDataReturn = function(params, response) {
	var obj = params.jsonObj;
	if (obj) {
		if (obj.SearchRequest) {
			if (obj.SearchRequest.types) { // We searched for something in addition to contacts
				params.callback = new AjxCallback(this, function(callback, result) {
					result._data.SearchResponse.cn = response.Body.SearchResponse.cn; // Put in the contacts we found in the response
					callback.run(result);
				}, params.callback);
				this.sendRequest.func.call(this, params); // Perform the regular search
				return;
			}
		}
		params.response = response; // Put in the canned response
		this.sendRequest.func.call(this, params); // and let the original function do its business
	}
};



//ZmContactList.prototype.load =
VelodromeSkin.prototype.load =
function(callback, errorCallback, accountName) {
	this.isCanonical = true;
	var respCallback = new AjxCallback(this, this._handleResponseLoad, [callback]);
	window.plaxosync.requestmgr.getContacts(new AjxCallback(this, this._handleResponseLoad, [callback]));
};

//ZmContactList.prototype._handleResponseLoad =
VelodromeSkin.prototype._handleResponseLoad =
function(callback, contacts) {
	for (var id in contacts) {
		var attrs = contacts[id];
		var _contact = {}, _attrs = {};
		for (var key in attrs) {
			var value = attrs[key];
			if (ZmContactList.IS_CONTACT_FIELD[key]) {
				_contact[key] = value;
			} else {
				_attrs[key] = value;
			}
		}
		_contact._attrs = _attrs;
		_contact.id = id;
		this._addContact(_contact);
	}
	this._finishLoading();
	if (callback) {
		callback.run();
	}
};

//ZmContactController.prototype.show =
VelodromeSkin.prototype._show = 
function(contact, isDirty) {
	if (Comcast_plaxosync.DISPLAY_IFRAME) {
		if (contact) {
			if (contact.type == ZmItem.GROUP) {
				var url = Comcast_plaxosync.PLAXO_URL_NEW_GROUP;
			} else {
				var name = [contact.attr.firstName||"", contact.attr.middleName||"", contact.attr.lastName||""].join(" ").replace(/\s+/g," ");
				name = name.replace(/^\s+|\s+$/g,"");
				var emailFields = ["email", "workEmail1"];;
				var email = Comcast_plaxosync.getFirstAttr(contact.attr, emailFields, "");
				//var email = Comcast_plaxosync.getFirstAttr(contact.attr, ZmContact.EMAIL_FIELDS, "");
				var phone = Comcast_plaxosync.getFirstAttr(contact.attr, ZmContact.PHONE_FIELDS, "");
				//var url = (contact.id) ? AjxMessageFormat.format(Comcast_plaxosync.PLAXO_URL_EDIT_CONTACT,[contact.id]) : AjxMessageFormat.format(Comcast_plaxosync.PLAXO_URL_NEW_CONTACT, [name, email, phone]);
				var url = (contact.id) ? AjxMessageFormat.format(Comcast_plaxosync.PLAXO_URL_EDIT_CONTACT,[name, email]) : AjxMessageFormat.format(Comcast_plaxosync.PLAXO_URL_NEW_CONTACT, [name, email, phone]);
			}
			window.plaxosync.setIframeUrl(url, true);
		}
	} else {
		arguments.callee.func.apply(this, arguments);
	}
};

//ZmContactController.prototype.show =
VelodromeSkin.prototype._Portalshow =
function(contact, isDirty) {
	window.location = "http://xfinity-qa.mail.comcast.net/connect/";
}

//ZmSearchController.prototype._doSearch =
VelodromeSkin.prototype._doSearch = 
function(params, noRender, callback, errorCallback) {
	var types = params.types || this.getTypes(params);
	if (Comcast_plaxosync.DISPLAY_IFRAME && (
		(AjxUtil.isArray(types) && types.length==1 && types[0] == ZmItem.CONTACT) || 
		(types instanceof AjxVector && types.size() == 1 && types.get(0) == ZmItem.CONTACT)
	)) {
		var query = params.query;
		var p = query && Comcast_plaxosync.parseQuery(query);
		var url;	
		if (p && p.searchfor) {
			window.plaxosync.setIframeUrl(AjxMessageFormat.format(Comcast_plaxosync.PLAXO_URL_SHOW_SEARCH,[p.searchfor]), true);
		} else {
			if (!window.plaxosync._iframeSrc) {
				window.plaxosync.setIframeUrl(Comcast_plaxosync.PLAXO_URL_SHOW_ALL, true);
			} else {
				window.plaxosync.displayIframe();
			}
		}
	}
	arguments.callee.func.apply(this, arguments);
};

//ZmContactListController.prototype._defaultView =
VelodromeSkin.prototype._defaultView =
function() {
	if (Comcast_plaxosync.DISPLAY_IFRAME) {
		return window.plaxosync.viewName;
	} else {
		return (appCtxt.get(ZmSetting.CONTACTS_VIEW) == "cards")
			? ZmId.VIEW_CONTACT_CARDS
			: ZmId.VIEW_CONTACT_SIMPLE;
	}
};

VelodromeSkin.prototype.switchView =
function(view, force, initialized, stageView) {
	if (Comcast_plaxosync.DISPLAY_IFRAME) {
		view = window.plaxosync.viewName;
		initialized = true;
		if (!this._appViews[view]) {
			this._appViews[view] = window.plaxosync.getIframeView();
		}
	}
	return arguments.callee.func.call(this, view, force, initialized, stageView);
};

VelodromeSkin.prototype.listShow =
function() {
	this.switchView(window.plaxosync.viewName);
};

VelodromeSkin.prototype._createNewView =
function(view) {
	if (view == window.plaxosync.viewName) {
		return window.plaxosync.getIframeView();
	}
	return arguments.callee.func.apply(this, arguments);
};

VelodromeSkin.prototype._initializeListView =
function(view) {
	if (view == window.plaxosync.viewName) {
		this._listView[view] = this._createNewView(view);
	} else {
		arguments.callee.func.apply(this, arguments);
	}
};

//--------------------------------

VelodromeSkin.prototype._handleContactsCoreLoad = function() {
	this.overrideAPI(ZmContactList.prototype, "load", this.load);
	this.overrideAPI(ZmContactList.prototype, "_handleResponseLoad", this._handleResponseLoad);
};

VelodromeSkin.prototype._handleContactsLoad = function() {
	this.overrideAPI(ZmContactController.prototype, "show", this._show);
	this.overrideAPI(ZmContactListController.prototype, "_defaultView", this._defaultView);
	this.overrideAPI(ZmContactListController.prototype, "show", this.listShow);
	this.overrideAPI(ZmContactListController.prototype, "_createNewView", this._createNewView);
	this.overrideAPI(ZmContactListController.prototype, "_initializeListView", this._initializeListView);
	this.overrideAPI(ZmContactListController.prototype, "switchView", this.switchView);
	this.overrideAPI(ZmContactListController.prototype, "_setViewContents", AjxCallback.returnFalse);
	this.overrideAPI(ZmContactListController.prototype, "_resetOperations", AjxCallback.returnFalse);
};

VelodromeSkin.prototype._handlePortalLoad = function() {
	this.overrideAPI(ZmPortalController.prototype, "show", this._Portalshow);
};

VelodromeSkin.prototype._handleStartupLoad = function() {
	window.plaxosync = new Comcast_plaxosync();
	this.overrideAPI(ZmRequestMgr.prototype, "sendRequest", this.sendRequest);
	this.overrideAPI(ZmRequestMgr.prototype, "_handleDataReturn", this._handleDataReturn);
	this.overrideAPI(ZmSearchController.prototype, "_doSearch", this._doSearch);
	window.plaxosync.init();
};

//AjxDispatcher.addPackageLoadFunction("ContactsCore", new AjxCallback(skin, skin._handleContactsCoreLoad));
AjxDispatcher.addPackageLoadFunction("Contacts", new AjxCallback(skin, skin._handleContactsLoad));
AjxDispatcher.addPackageLoadFunction("Portal", new AjxCallback(skin, skin._handlePortalLoad));
AjxDispatcher.addPackageLoadFunction("Startup1_2", new AjxCallback(skin, skin._handleStartupLoad));

