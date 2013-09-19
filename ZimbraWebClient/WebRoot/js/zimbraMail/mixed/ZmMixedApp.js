/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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

ZmMixedApp = function(container) {

	ZmApp.call(this, ZmApp.MIXED, container);
};

// Organizer and item-related constants
ZmItem.MIXED	= "MIXED"; // special type for heterogeneous list

// App-related constants
ZmApp.MIXED						= ZmId.APP_MIXED;
ZmApp.CLASS[ZmApp.MIXED]		= "ZmMixedApp";
ZmApp.SETTING[ZmApp.MIXED]		= ZmSetting.MIXED_VIEW_ENABLED;
ZmApp.LOAD_SORT[ZmApp.MIXED]	= 50;

ZmMixedApp.prototype = new ZmApp;
ZmMixedApp.prototype.constructor = ZmMixedApp;

ZmMixedApp.prototype.toString = 
function() {
	return "ZmMixedApp";
};

// Construction

ZmMixedApp.prototype._defineAPI =
function() {
	AjxDispatcher.registerMethod("GetMixedController", "Mixed", new AjxCallback(this, this.getMixedController));
};

ZmMixedApp.prototype._registerItems =
function() {
	ZmItem.registerItem(ZmItem.MIXED,
						{app:			ZmApp.MIXED});
};

ZmMixedApp.prototype._registerApp =
function() {
    var trees = [], types = [];
    if (appCtxt.get(ZmSetting.MAIL_ENABLED)) {
        trees.push(ZmOrganizer.FOLDER);
        types.push(ZmItem.MSG, ZmItem.CONV);
    }
    if (appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
        trees.push(ZmOrganizer.ADDRBOOK);
    }
    if (appCtxt.get(ZmSetting.CALENDAR_ENABLED)) {
        trees.push(ZmOrganizer.CALENDAR);
    }
    if (appCtxt.get(ZmSetting.TASKS_ENABLED)) {
        trees.push(ZmOrganizer.TASKS);
    }
    if (appCtxt.get(ZmSetting.BRIEFCASE_ENABLED)) {
        trees.push(ZmOrganizer.BRIEFCASE);
    }
    trees.push(ZmOrganizer.SEARCH, ZmOrganizer.TAG);

	ZmApp.registerApp(ZmApp.MIXED,
							 {mainPkg:			"Mixed",
							  nameKey:			"zimbraTitle",
							  icon:				"Globe",
							  overviewTrees:	trees,
							  searchTypes:		types
							  });
};

ZmMixedApp.prototype.launch =
function() {
};

// Don't show folders when viewing mixed app coming from contacts app,
// and don't show addrbooks when viewing mixed app coming from mail app
ZmMixedApp.prototype._getOverviewTrees =
function() {
	var list = ZmApp.OVERVIEW_TREES[this._name];
	var trees = [];
	var prevApp = appCtxt.getAppController().getPreviousApp();
	for (var i = 0; i < list.length; i++) {
		var id = list[i];
		if ((prevApp == ZmApp.CONTACTS && id == ZmOrganizer.FOLDER) ||
			(prevApp != ZmApp.CONTACTS && id == ZmOrganizer.ADDRBOOK)) {
			continue;
		}
		trees.push(id);
	}
	return trees;
};

ZmMixedApp.prototype.showSearchResults =
function(results, callback) {
	var loadCallback = new AjxCallback(this, this._handleLoadShowSearchResults, [results, callback]);
	AjxDispatcher.require(["MailCore", "Mail", "Mixed"], false, loadCallback, null, true);
};

ZmMixedApp.prototype._handleLoadShowSearchResults =
function(results, callback) {
	this.getMixedController().show(results);
	if (callback) {
		callback.run();
	}
};

ZmMixedApp.prototype.getMixedController =
function() {
	if (!this._mixedController)
		this._mixedController = new ZmMixedController(this._container, this);
	return this._mixedController;
};
