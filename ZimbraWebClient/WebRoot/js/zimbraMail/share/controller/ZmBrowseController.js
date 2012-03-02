/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2009, 2010, 2011 VMware, Inc.
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
 * This file defines a tag tree controller.
 *
 */

/**
 * Creates a browse tree controller.
 * @class
 * This class defines a browse controller.
 * 
 * @param	{DwtComposite}	parent		the parent
 * 
 * @extends		ZmController
 */
ZmBrowseController = function(parent) {

	ZmController.call(this, null);
    var pickers = this._allPickers = this._getPickers();
	this._browseView = new ZmBrowseView(this._shell, pickers);
	this._toolbar = new ZmBrowseToolBar(this._shell, pickers);
	this._browseView._toolbar = this._toolbar;
    var addListener = new AjxListener(this, this._addPickerListener);
	for (var i = 0; i < pickers.length; i++) {
		var id = pickers[i];
		this._toolbar.addSelectionListener(id, addListener);
	}
	this._toolbar.addSelectionListener(ZmPicker.RESET, new AjxListener(this, this._resetListener));
	this._toolbar.addSelectionListener(ZmPicker.CLOSE, new AjxListener(this, this._closeListener));
	this._resetPickers();
	this._browseViewVisible = false;

	var components = {};
	components[ZmAppViewMgr.C_SEARCH_BUILDER_TOOLBAR] = this._toolbar;
	components[ZmAppViewMgr.C_SEARCH_BUILDER] = this._browseView;
	appCtxt.getAppViewMgr().addComponents(components, true);

	this._searchBuilderOpened = false;
};

ZmBrowseController.prototype = new ZmController;
ZmBrowseController.prototype.constructor = ZmBrowseController;

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmBrowseController.prototype.toString =
function() {
	return "ZmBrowseController";
};

/**
 * @private
 */
ZmBrowseController.prototype._getPickers =
function() {
	var list = ZmPicker.DEFAULT_PICKERS;
	if (appCtxt.get(ZmSetting.SAVED_SEARCHES_ENABLED)) {
		list.push(ZmPicker.SEARCH);
	}
    list.push(ZmPicker.SIZE);
	if (appCtxt.zimletsPresent()) {
	    var idxZimlets = appCtxt.getZimletMgr().getIndexedZimlets();
	    if (idxZimlets.length) {
	    	list.push(ZmPicker.ZIMLET);
	    }
	}
	list.push(ZmPicker.FLAG);
	if (appCtxt.get(ZmSetting.TAGGING_ENABLED)) {
		list.push(ZmPicker.TAG);
	}
	list.push(ZmPicker.TIME);

	return list;
};

/**
 * Gets the browse view.
 * 
 * @return	{ZmBrowseView}		the browse view
 */
ZmBrowseController.prototype.getBrowseView =
function() {
	return (appCtxt.get(ZmSetting.BROWSE_ENABLED)) ? this._browseView : null;
};

/**
 * Checks if the browse view is visible.
 * 
 * @return	{Boolean}	<code>true</code> if the browse view is visible; <code>false</code> otherwise
 */
ZmBrowseController.prototype.getBrowseViewVisible =
function() {
	return this._browseViewVisible;
};

/**
 * Sets the browse view visible state.
 * 
 * @param	{Boolean}	visible		if <code>true</code>, the browse view is visible; <code>false</code> otherwise
 */
ZmBrowseController.prototype.setBrowseViewVisible =
function(visible) {
	if (this._browseViewVisible == visible) {return;}

	var tbl, H;
	if (AjxEnv.isGeckoBased && skin.getTopAdContainer()) {
		tbl = skin._getEl("skin_table_outer");
		H = tbl.offsetHeight;
	}

	appCtxt.getAppViewMgr().showSearchBuilder(visible);

	if (AjxEnv.isGeckoBased && skin.getTopAdContainer()) {
		tbl.style.height = H + "px";
		setTimeout(function() {
			tbl.style.height = "100%";
		}, 10);
	}

	// hack to fix bug 10222 - skin doesn't size correctly first time
	if (visible && !this._searchBuilderOpened) {
		appCtxt.getAppViewMgr().showSearchBuilder(!visible);
		appCtxt.getAppViewMgr().showSearchBuilder(visible);
		this._searchBuilderOpened = true;
	}

	var searchCtlr = appCtxt.getSearchController();
	this._browseViewVisible = visible;
	if (visible) {
		if (this._browseView.getPickers().size() == 0) {
			this.addPicker(ZmPicker.DEFAULT_PICKER);
		}
		if (this._query) {
			searchCtlr.setSearchField(this._query);
		}
	}
	var browseButton = searchCtlr.getSearchToolbar().getButton(ZmSearchToolBar.BROWSE_BUTTON);
	browseButton.setToolTipContent(visible ? ZmMsg.closeSearchBuilder : ZmMsg.openSearchBuilder);
	browseButton.setSelected(visible);
};

/**
 * The point of the mess below is to safely construct queries. Some pickers (right now, just
 * the basic picker) may have multiple instances that join their queries with OR instead of
 * AND (represented as a space). In that case, we need parens around each of the picker
 * queries as well as around the query for the type of picker, in order to protect the OR.
 * 
 * @private
 */
ZmBrowseController.prototype._updateQuery =
function(doSearch) {
	var queries = {};
	var numPickers = 0;
	for (var id in this._pickers) {
		var a = this._pickers[id].getArray();
		for (var i = 0; i < a.length; i++) {
			var pq = a[i]._query;
			if (pq && pq.length) {
				if (!queries[id]) {
					queries[id] = [];
					numPickers++;
				}
				queries[id].push(pq);
			}
		}
	}
	var queryStr = [];
	for (var id in queries) {
		var a = queries[id];
		if (a && a.length) {
			if (a.length > 1) {
				var q = [];
				var j = ZmPicker.MULTI_JOIN[id];
				var needParen = (j != " ");
				for (var i = 0; i < a.length; i++)
					q.push((needParen && a.length > 1) ? "(" + a[i] + ")" : a[i]);
				var query = q.join(j);
				if (needParen && numPickers > 1)
					query = "(" + query + ")";
				queryStr.push(query);
			} else {
				queryStr.push(a[0]);
			}
		}
	}

	if (appCtxt.get(ZmSetting.SAVED_SEARCHES_ENABLED)) {
		// so we can select search folder in overview
		var a = this._pickers[ZmPicker.SEARCH].getArray();
		this._searchId = null;
		if (a && (a.length == 1) && (queryStr.length == 1)) {
			this._searchId = a[0]._searchId;
		}
	}

	var newQuery = queryStr.join(" ");
	if (newQuery != this._query) {
		this._query = newQuery;
		DBG.println(AjxDebug.DBG3, "Browse query: " + this._query);
		if (doSearch) {
			appCtxt.getSearchController().search({query: this._query, searchId: this._searchId});
		} else {
			appCtxt.getSearchController().setSearchField(this._query);
		}
	}
}

/**
 * @private
 */
ZmBrowseController.prototype._executeQuery =
function() {
	appCtxt.getSearchController().search({query: this._query, searchId: this._searchId});
};

/**
 * @private
 */
ZmBrowseController.prototype._resetListener =
function(ev) {
	this.removeAllPickers();
};

/**
 * @private
 */
ZmBrowseController.prototype._closeListener =
function(ev) {
	this.setBrowseViewVisible(false);
};

/**
 * @private
 */
ZmBrowseController.prototype.removeAllPickers =
function(ev) {
	this._browseView.removeAllPickers();
	this._clearQuery();
	this._resetPickers();
};

/**
 * Adds the picker.
 * 
 * @param	{String}	id		the picker id
 * @return	{ZmPicker}	the picker or <code>null</code> for none
 */
ZmBrowseController.prototype.addPicker =
function(id) {
	if (ZmPicker.LIMIT[id] != -1 && this._pickers[id].size() >= ZmPicker.LIMIT[id]) {return;}

	var ctor = ZmPicker.CTOR[id];
	var picker = new ctor(this._browseView);
	this._browseView.addPicker(picker, id);
	this._pickers[id].add(picker);
	this._numPickers++;
	if (id == ZmPicker.DATE && this._pickers[id].size() == ZmPicker.LIMIT[id]) {
		picker.secondDate();
	}
    var cb = picker.getCloseButton();
    cb.setData(ZmPicker.KEY_ID, id);
    cb.setData(ZmPicker.KEY_PICKER, picker);
    cb.addSelectionListener(new AjxListener(this, this._pickerCloseListener));
    picker.addPickerListener(new AjxListener(this, this._pickerListener));
	this._updateQuery(true);

	// disable picker button if max instances of this picker has been reached
	if (ZmPicker.LIMIT[id] != -1 && this._pickers[id].size() == ZmPicker.LIMIT[id]) {
		this._toolbar.enable(id, false);
	}

	return picker;
};

/**
 * @private
 */
ZmBrowseController.prototype._addPickerListener =
function(ev) {
	var id = ev.item.getData(ZmPicker.KEY_ID);
	this.addPicker(id);
};

/**
 * @private
 */
ZmBrowseController.prototype._pickerCloseListener =
function(ev) {
    var b = ev.item;
	var picker = b.getData(ZmPicker.KEY_PICKER);
	this._browseView.removePicker(picker);
	var id = b.getData(ZmPicker.KEY_ID);
	this._pickers[id].remove(picker);
	this._numPickers--;
	if (this._numPickers == 0) {
		this._clearQuery();
	} else {
		this._updateQuery(true);
	}
	// enable picker button if max instances of this picker has not been reached
	if (ZmPicker.LIMIT[id] != -1 && this._pickers[id].size() < ZmPicker.LIMIT[id]) {
		this._toolbar.enable(id, true);
	}
};

/**
 * @private
 */
ZmBrowseController.prototype._clearQuery =
function() {
	this._query = "";
	appCtxt.getSearchController().setSearchField("");
};

/**
 * @private
 */
ZmBrowseController.prototype._pickerListener =
function(ev) {
	if (ev.type != ZmEvent.S_PICKER) {return;}
	if (ev.event == ZmEvent.E_MODIFY) {
		this._updateQuery(false);
	} else if (ev.event == ZmEvent.E_LOAD) {
		this._executeQuery();
	}
};

/**
 * @private
 */
ZmBrowseController.prototype._resetPickers =
function() {
	this._pickers = {};
	for (var i = 0; i < this._allPickers.length; i++) {
		var id = this._allPickers[i];
		this._pickers[id] = new AjxVector();
		this._toolbar.enable(id, true);
	}
	this._numPickers = 0;
};
