/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010, 2011 VMware, Inc.
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

ZmNotebookItem = function(type, id, list) {
	if (arguments.length == 0) { return; }
	ZmItem.call(this, type, id, list);
	this.folderId = ZmNotebookItem.DEFAULT_FOLDER;
}
ZmNotebookItem.prototype = new ZmItem;
ZmNotebookItem.prototype.constructor = ZmNotebookItem;

ZmNotebookItem.prototype.toString = function() {
	return "ZmNotebookItem";
};

// Constants

ZmNotebookItem.DEFAULT_FOLDER = ZmOrganizer.ID_NOTEBOOK;

// Data

ZmNotebookItem.prototype.name;
ZmNotebookItem.prototype.creator;
ZmNotebookItem.prototype.createDate;
ZmNotebookItem.prototype.modifier;
ZmNotebookItem.prototype.modifyDate;
ZmNotebookItem.prototype.size;
ZmNotebookItem.prototype.version = 0;

// Static functions

ZmNotebookItem.createFromDom = function(node, args) {
	var item = new ZmNotebookItem(args.type || -1, node.id, args.list);
	item.set(node);
	return item;
};

// Public methods

ZmNotebookItem.prototype.getPath = function(dontIncludeThisName) {
	var notebook = appCtxt.getById(this.folderId);
	var name = !dontIncludeThisName ? this.name : "";
	return [ notebook.getPath(), "/", name ].join("");
};

ZmNotebookItem.prototype.getRestUrl = function(dontIncludeThisName) {
	var url = ZmItem.prototype.getRestUrl.call(this);

	if (dontIncludeThisName) {
		url = url ? url.replace(new RegExp(("/"+this.name+"(/)?$")),"") : null;
	}
	return url;
};

ZmNotebookItem.prototype.set = function(data) {
	// ZmItem fields
	this.id = data.id;
	this.restUrl = data.rest != null ? data.rest : this.restUrl;
	this.folderId = data.l != null ? data.l : this.folderId;
	this._parseTags(data.t);

	// ZmNotebookItem fields
	this.name = data.name != null ? data.name : this.name;
	this.creator = data.cr != null ? data.cr : this.creator;
	this.createDate = data.d != null ? new Date(Number(data.d)) : this.createDate;
	this.modifier = data.leb != null ? data.leb : this.modifier;
	this.modifyDate = data.md != null ? new Date(Number(data.md)) : this.modifyDate;
	this.size = data.s != null ? Number(data.s) : this.size;
	this.version = data.ver != null ? Number(data.ver) : this.version;
};
