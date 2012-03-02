/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

ZmFileListView = function(params) {

	// call super constructor
	var headerList = params.headerList = this._getHeaderList(parent);
	params.view = ZmId.VIEW_NOTEBOOK_FILE;
	params.type = ZmItem.PAGE;
	ZmListView.call(this, params);

	// create a action menu for the header list
	this._colHeaderActionMenu = new ZmPopupMenu(this);
	var actionListener = new AjxListener(this, this._colHeaderActionListener);
	for (var i = 0; i < headerList.length; i++) {
		var hCol = headerList[i];
		// lets not allow columns w/ relative width to be removed (for now) - it messes stuff up
		if (hCol._width) {
			var mi = this._colHeaderActionMenu.createMenuItem(hCol._id, {text:hCol._name, style:DwtMenuItem.CHECK_STYLE});
			mi.setData(ZmFileListView.KEY_ID, hCol._id);
			mi.setChecked(true, true);
			this._colHeaderActionMenu.addSelectionListener(hCol._id, actionListener);
		}
	}
};

ZmFileListView.prototype = new ZmListView;
ZmFileListView.prototype.constructor = ZmFileListView;

ZmFileListView.prototype.toString =
function() {
	return "ZmFileListView";
};

// Constants

ZmFileListView.KEY_ID = "_keyId";

ZmFileListView.COLWIDTH_ICON 			= 20;
ZmFileListView.COLWIDTH_TYPE			= ZmMsg.COLUMN_WIDTH_TYPE_DLV;
ZmFileListView.COLWIDTH_SIZE 			= ZmMsg.COLUMN_WIDTH_SIZE_DLV;
ZmFileListView.COLWIDTH_DATE 			= ZmMsg.COLUMN_WIDTH_DATE_DLV;
ZmFileListView.COLWIDTH_OWNER			= ZmMsg.COLUMN_WIDTH_OWNER_DLV;
ZmFileListView.COLWIDTH_FOLDER			= ZmMsg.COLUMN_WIDTH_FOLDER_FLV;

// Protected methods

ZmFileListView.prototype._getHeaderList =
function(parent) {
	// Columns: tag, name, type, size, date, owner, folder
	var headers = [];
	if (appCtxt.get(ZmSetting.TAGGING_ENABLED)) {
		headers.push(new DwtListHeaderItem({field:ZmItem.F_TAG, icon:"Tag", width:ZmFileListView.COLWIDTH_ICON, name:ZmMsg.tag}));
	}
	headers.push(
		new DwtListHeaderItem({field:ZmItem.F_TYPE, icon:"Globe", width:ZmFileListView.COLWIDTH_ICON}),
		new DwtListHeaderItem({field:ZmItem.F_SUBJECT, text:ZmMsg._name, resizeable:true}),
		new DwtListHeaderItem({field:ZmItem.F_FILE_TYPE, text:ZmMsg.type, width:ZmFileListView.COLWIDTH_TYPE}),
		new DwtListHeaderItem({field:ZmItem.F_SIZE, text:ZmMsg.size, width:ZmFileListView.COLWIDTH_SIZE}),
		new DwtListHeaderItem({field:ZmItem.F_DATE, text:ZmMsg.date, width:ZmFileListView.COLWIDTH_DATE}),
		new DwtListHeaderItem({field:ZmItem.F_FROM, text:ZmMsg.owner, width:ZmFileListView.COLWIDTH_OWNER}),
		new DwtListHeaderItem({field:ZmItem.F_FOLDER, text:ZmMsg.folder, width:ZmFileListView.COLWIDTH_FOLDER})
	);
	return headers;
};

ZmFileListView.prototype._getCellContents =
function(htmlArr, idx, item, field, colIdx, params) {
	if (field == ZmItem.F_SUBJECT) {
		htmlArr[idx++] = "<div id='"+this._getFieldId(item,ZmItem.F_SUBJECT)+"'>"+AjxStringUtil.htmlEncode(item.name)+"</div>";
	} else if (field == ZmItem.F_SIZE) {
		htmlArr[idx++] = AjxUtil.formatSize(item.size);
	} else if (field == ZmItem.F_FILE_TYPE) {
		var desc = (item instanceof ZmPage) ? ZmMsg.page : null;
		if (!desc) {
			var mimeInfo = item.ct ? ZmMimeTable.getInfo(item.ct) : null;
			desc = mimeInfo ? mimeInfo.desc : "&nbsp;";
		}
		htmlArr[idx++] = desc;
	} else if (field == ZmItem.F_TYPE) {
		var icon = (item instanceof ZmPage) ? "Page" : null;
		if (!icon) {
			var contentType = item.contentType;
			var mimeInfo = contentType ? ZmMimeTable.getInfo(contentType) : null;
			icon = mimeInfo ? mimeInfo.image : "UnknownDoc";
		}
		htmlArr[idx++] = "<div class='Img" + icon + "'></div>";
	} else if (field == ZmItem.F_FROM) {
		var creator = item.creator && item.creator.split("@");
		var cname = creator && creator[0];
		var uname = appCtxt.get(ZmSetting.USERNAME);
		if (uname) {
			var user = uname.split("@");
			if (creator && (creator[1] != user[1])) {
				cname = creator.join("@");
			}
		}
		htmlArr[idx++] = "<span style='white-space: nowrap'>";
		htmlArr[idx++] = cname;
		htmlArr[idx++] = "</span>";
	} else if (field == ZmItem.F_FOLDER) {
		var notebook = appCtxt.getById(item.folderId);
		htmlArr[idx++] = notebook ? notebook.getPath() : item.folderId;
	} else {
		if (field == ZmItem.F_DATE) {
			item = AjxUtil.createProxy(item);
			item.date = item.modifyDate;
		}
		idx = ZmListView.prototype._getCellContents.apply(this, arguments);
	}
	
	return idx;
};

// listeners

ZmFileListView.prototype._colHeaderActionListener =
function(event) {
  	// TODO
};

//
// Private functions
//

ZmFileListView.__typify =
function(array, type) {
	for (var i = 0; i < array.length; i++) {
		array[i]._type = type;
	}
};

ZmFileListView.prototype._changeListener =
function(ev) {
	var item = ev.item || ev.getDetail("items");
	var items = (item instanceof Array) ? item : [item];
	for(var i in items) {
		this._handleChangeItem(ev,items[i]);
	}
};

ZmFileListView.prototype._handleChangeItem =
function(ev,item) {

	if (ev.handled || !this._handleEventType[item.type] && (this.type != ZmItem.MIXED)) { return; }
	
	if (ev.event == ZmEvent.E_TAGS || ev.event == ZmEvent.E_REMOVE_ALL) {
		DBG.println(AjxDebug.DBG2, "ZmListView: TAG");
		this._setImage(item, ZmItem.F_TAG, item.getTagImageInfo());
	}
	
	if (ev.event == ZmEvent.E_FLAGS) { // handle "flagged" and "has attachment" flags
		DBG.println(AjxDebug.DBG2, "ZmListView: FLAGS");
		var flags = ev.getDetail("flags");
		for (var j = 0; j < flags.length; j++) {
			var flag = flags[j];
			var on = item[ZmItem.FLAG_PROP[flag]];
			if (flag == ZmItem.FLAG_FLAGGED) {
				this._setImage(item, ZmItem.F_FLAG, on ? "FlagRed" : null);
			} else if (flag == ZmItem.FLAG_ATTACH) {
				this._setImage(item, ZmItem.F_ATTACHMENT, on ? "Attachment" : null);
			}
		}
	}
	
	if (ev.event == ZmEvent.E_DELETE || ev.event == ZmEvent.E_MOVE) {
		DBG.println(AjxDebug.DBG2, "ZmListView: DELETE or MOVE");
        this.removeItem(item, true);
        this._controller._app._checkReplenishListView = this;
		this._controller._resetToolbarOperations();		
	}
};

ZmFileListView.prototype._getToolTip =
function(params) {
	if (!params.item) { return; }
	return this._controller.getItemTooltip(params.item, this);
};