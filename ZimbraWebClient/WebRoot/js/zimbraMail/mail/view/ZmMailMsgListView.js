/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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

ZmMailMsgListView = function(params) {
	this._mode = params.mode;
	this.view = params.view = (params.view || params.mode);
	params.type = ZmItem.MSG;
	params.headerList = this._getHeaderList(params.parent, params.controller);
	ZmMailListView.call(this, params);
};

ZmMailMsgListView.prototype = new ZmMailListView;
ZmMailMsgListView.prototype.constructor = ZmMailMsgListView;


// Consts

ZmMailMsgListView.INDENT		= "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

ZmMailMsgListView.SINGLE_COLUMN_SORT_CV = [
	{field:ZmItem.F_FROM,	msg:"from"		},
	{field:ZmItem.F_SIZE,	msg:"size"		},
	{field:ZmItem.F_DATE,	msg:"date"		}
];

// Public methods

ZmMailMsgListView.prototype.toString = 
function() {
	return "ZmMailMsgListView";
};

ZmMailMsgListView.prototype.markUIAsRead = 
function(msg) {
	ZmMailListView.prototype.markUIAsRead.apply(this, arguments);
	this._setImage(msg, ZmItem.F_STATUS, msg.getStatusIcon());
};

// Private / protected methods

// following _createItemHtml support methods are also used for creating msg
// rows in ZmConvListView

// support for showing which msgs in a conv matched the search
ZmMailMsgListView.prototype._addParams =
function(msg, params) {
	if (this._mode == ZmId.VIEW_TRAD) {
		return ZmMailListView.prototype._addParams.apply(this, arguments);
	} else {
		var conv = appCtxt.getById(msg.cid);
		var s = this._controller._activeSearch && this._controller._activeSearch.search;
		params.isMatched = (s && s.hasTextTerm && msg.inHitList);
	}
};

ZmMailMsgListView.prototype._getDivClass =
function(base, item, params) {
	if (params.isMatched && !params.isDragProxy) {
		return base + " " + [base, DwtCssStyle.MATCHED].join("-");			// Row Row-matched
	} else {
		return ZmMailListView.prototype._getDivClass.apply(this, arguments);
	}
};

ZmMailMsgListView.prototype._getRowClass =
function(msg) {
	var classes = [];
	if (this._mode != ZmId.VIEW_TRAD) {
		var folder = appCtxt.getById(msg.folderId);
		if (folder && folder.isInTrash()) {
			classes.push("Trash");
		}
	}
	if (msg.isUnread)	{	classes.push("Unread"); }
	if (msg.isSent)		{	classes.push("Sent"); }

	return classes.length ? classes.join(" ") : null;
};

ZmMailMsgListView.prototype._getCellId =
function(item, field) {
	if (field == ZmItem.F_SUBJECT && (this._mode == ZmId.VIEW_CONV ||
									  this._mode == ZmId.VIEW_CONVLIST)) {
		return this._getFieldId(item, field);
	} else {
		return ZmMailListView.prototype._getCellId.apply(this, arguments);
	}
};

ZmMailMsgListView.prototype._getCellContents =
function(htmlArr, idx, msg, field, colIdx, params) {

	if (field == ZmItem.F_STATUS) {
		idx = this._getImageHtml(htmlArr, idx, msg.getStatusIcon(), this._getFieldId(msg, field));
	} else if (field == ZmItem.F_FROM || field == ZmItem.F_PARTICIPANT) {
		// setup participants list for Sent/Drafts/Outbox folders
		if (this._isOutboundFolder()) {
			var addrs = msg.getAddresses(AjxEmailAddress.TO).getArray();

			// default to FROM addresses if no TO: found
			if (!addrs || addrs.length == 0) {
				addrs = msg.getAddresses(AjxEmailAddress.FROM).getArray();
			}

			if (addrs && addrs.length) {
				var fieldId = this._getFieldId(msg, ZmItem.F_PARTICIPANT);
				var origLen = addrs.length;
				var headerCol = this._headerHash[field];
				var partColWidth = headerCol ? headerCol._width : ZmMsg.COLUMN_WIDTH_FROM_CLV;
				var parts = this._fitParticipants(addrs, msg, partColWidth);
				for (var j = 0; j < parts.length; j++) {
					if (j == 0 && (parts.length < origLen)) {
						htmlArr[idx++] = AjxStringUtil.ELLIPSIS;
					} else if (parts.length > 1 && j > 0) {
						htmlArr[idx++] = AjxStringUtil.LIST_SEP;
					}
					htmlArr[idx++] = "<span style='white-space: nowrap' id='";
					htmlArr[idx++] = [fieldId, parts[j].index].join(DwtId.SEP);
					htmlArr[idx++] = "'>";
					htmlArr[idx++] = AjxStringUtil.htmlEncode(parts[j].name);
					htmlArr[idx++] = "</span>";
				}
			} else {
				htmlArr[idx++] = "&nbsp;"
			}
		} else {
			var fromAddr = msg.getAddress(AjxEmailAddress.FROM);
			if (fromAddr) {
				if (this._mode == ZmId.VIEW_CONVLIST && this._isMultiColumn) {
					htmlArr[idx++] = ZmMailMsgListView.INDENT;
				}
				htmlArr[idx++] = "<span style='white-space:nowrap' id='";
				htmlArr[idx++] = this._getFieldId(msg, ZmItem.F_FROM);
				htmlArr[idx++] = "'>";
				var name = fromAddr.getName() || fromAddr.getDispName() || fromAddr.getAddress();
				htmlArr[idx++] = AjxStringUtil.htmlEncode(name);
				htmlArr[idx++] = "</span>";
			}
		}

	} else if (field == ZmItem.F_SUBJECT) {
		if (this._mode == ZmId.VIEW_CONV || this._mode == ZmId.VIEW_CONVLIST) {
			// msg within a conv shows just the fragment
			if (this._mode == ZmId.VIEW_CONVLIST && this._isMultiColumn) {
				htmlArr[idx++] = ZmMailMsgListView.INDENT;
			}
			if (!this._isMultiColumn) {
				htmlArr[idx++] = "<span class='ZmConvListFragment'>";
			}
			htmlArr[idx++] = AjxStringUtil.htmlEncode(msg.fragment, true);
			if (!this._isMultiColumn) {
				htmlArr[idx++] = "</span>";
			}
		} else {
			// msg on its own (TV) shows subject and fragment
			var subj = msg.subject || ZmMsg.noSubject;
			htmlArr[idx++] = AjxStringUtil.htmlEncode(subj);
			if (appCtxt.get(ZmSetting.SHOW_FRAGMENTS) && msg.fragment) {
				htmlArr[idx++] = this._getFragmentSpan(msg);
			}
		}

	} else if (field == ZmItem.F_FOLDER) {
		htmlArr[idx++] = "<nobr id='";
		htmlArr[idx++] = this._getFieldId(msg, field);
		htmlArr[idx++] = "'>"; // required for IE bug
		var folder = appCtxt.getById(msg.folderId);
		if (folder) {
			htmlArr[idx++] = folder.getName();
		}
		htmlArr[idx++] = "</nobr>";

	} else if (field == ZmItem.F_SIZE) {
		htmlArr[idx++] = "<nobr>";
		htmlArr[idx++] = AjxUtil.formatSize(msg.size);
		htmlArr[idx++] = "</nobr>";
	} else if (field == ZmItem.F_SORTED_BY) {
		htmlArr[idx++] = this._getAbridgedContent(msg, colIdx);
	} else {
		idx = ZmMailListView.prototype._getCellContents.apply(this, arguments);
	}
	
	return idx;
};

ZmMailMsgListView.prototype._getAbridgedContent =
function(item, colIdx) {
	var htmlArr = [];
	var idx = 0;
	var width = (AjxEnv.isIE || AjxEnv.isSafari) ? "22" : "16";

	// first row
	htmlArr[idx++] = "<table border=0 cellspacing=0 cellpadding=0 width=100%>";
	htmlArr[idx++] = (item.isUnread) ? "<tr class='Unread' " : "<tr ";
	htmlArr[idx++] = "id='";
	htmlArr[idx++] = DwtId.getListViewItemId(DwtId.WIDGET_ITEM_FIELD, this._view, item.id, ZmItem.F_ITEM_ROW_3PANE);
	htmlArr[idx++] = "'>";

	// for multi-account, show the account icon for cross mbox search results
	if (appCtxt.multiAccounts && appCtxt.getSearchController().searchAllAccounts) {
		idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_ACCOUNT, colIdx, "16", "align=right");
	}

	if (item.isHighPriority || item.isLowPriority) {
		idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_PRIORITY, colIdx, "10", "align=right");
	}
	idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_SUBJECT, colIdx);
	if (item.hasAttach) {
		idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_ATTACHMENT, colIdx, width);
	}
	if (appCtxt.get("FLAGGING_ENABLED"))
		idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_FLAG, colIdx, width);
	htmlArr[idx++] = "</tr></table>";

	// second row
	htmlArr[idx++] = "<table border=0 cellspacing=0 cellpadding=0 width=100%><tr>";
	htmlArr[idx++] = "<td width=16></td>";
	idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_STATUS, colIdx, width, "style='padding-left:0px'");

	idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_FROM, colIdx);
	idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_DATE, colIdx, ZmMsg.COLUMN_WIDTH_DATE, "align=right");
	idx = this._getAbridgedCell(htmlArr, idx, item, ZmItem.F_TAG, colIdx, width);
	htmlArr[idx++] = "</tr></table>";

	return htmlArr.join("");

};

// Listeners

ZmMailMsgListView.prototype._changeListener =
function(ev) {

	var msg = this._getItemFromEvent(ev);
	if (!msg || ev.handled || !this._handleEventType[msg.type]) { return; }

	if ((ev.event == ZmEvent.E_DELETE || ev.event == ZmEvent.E_MOVE) && this._mode == ZmId.VIEW_CONV) {
		if (!this._controller.handleDelete()) {
			if (ev.event == ZmEvent.E_DELETE) {
				ZmMailListView.prototype._changeListener.call(this, ev);
			} else {
				// if spam, remove it from listview
				if (msg.folderId == ZmFolder.ID_SPAM) {
					this._controller._list.remove(msg, true);
					ZmMailListView.prototype._changeListener.call(this, ev);
				} else {
					this._changeFolderName(msg, ev.getDetail("oldFolderId"));
					this._checkReplenishOnTimer();
				}
			}
		}
	} else if (this._mode == ZmId.VIEW_CONV && ev.event == ZmEvent.E_CREATE) {
		var conv = AjxDispatcher.run("GetConvController").getConv();
		if (conv && (msg.cid == conv.id)) {
			ZmMailListView.prototype._changeListener.call(this, ev);
		}
	} else if (ev.event == ZmEvent.E_FLAGS) { // handle "replied" and "forwarded" flags
		var flags = ev.getDetail("flags");
		for (var j = 0; j < flags.length; j++) {
			var flag = flags[j];
			var on = msg[ZmItem.FLAG_PROP[flag]];
			if (flag == ZmItem.FLAG_REPLIED && on) {
				this._setImage(msg, ZmItem.F_STATUS, "MsgStatusReply");
			} else if (flag == ZmItem.FLAG_FORWARDED && on) {
				this._setImage(msg, ZmItem.F_STATUS, "MsgStatusForward");
			}
		}
		ZmMailListView.prototype._changeListener.call(this, ev); // handle other flags
	} else {
		ZmMailListView.prototype._changeListener.call(this, ev);
		if (ev.event == ZmEvent.E_CREATE || ev.event == ZmEvent.E_DELETE || ev.event == ZmEvent.E_MOVE)	{
			this._resetColWidth();
		}
	}
};

ZmMailMsgListView.prototype._changeFolderName = 
function(msg, oldFolderId) {
	var folder = appCtxt.getById(msg.folderId);

	if (!this._controller.isReadingPaneOn() || 
		!this._controller.isReadingPaneOnRight())
	{
		var folderCell = folder ? this._getElement(msg, ZmItem.F_FOLDER) : null;
		if (folderCell) {
			folderCell.innerHTML = folder.getName();
		}
	}

	if (folder && (folder.nId == ZmFolder.ID_TRASH || oldFolderId == ZmFolder.ID_TRASH)) {
		this._changeTrashStatus(msg);
	}
};

ZmMailMsgListView.prototype._changeTrashStatus = 
function(msg) {
	var row = this._getElement(msg, ZmItem.F_ITEM_ROW);
	if (row) {
		if (msg.isUnread) {
			Dwt.addClass(row, "Unread");
		}

		var folder = appCtxt.getById(msg.folderId);
		if (folder && folder.isInTrash()) {
			Dwt.addClass(row, "Trash");
		} else {
			Dwt.delClass(row, "Trash");
		}

		if (msg.isSent) {
			Dwt.addClass(row, "Sent");
		}
	}
};

ZmMailMsgListView.prototype._getHeaderList =
function(parent, controller) {
	var headers;
	if (this.isMultiColumn(controller)) {
		headers = [];
		headers.push(ZmItem.F_SELECTION);
		if (appCtxt.get("FLAGGING_ENABLED"))
			headers.push(ZmItem.F_FLAG);
		headers.push(
			ZmItem.F_PRIORITY,
			ZmItem.F_TAG,
			ZmItem.F_STATUS,
			ZmItem.F_FROM,
			ZmItem.F_ATTACHMENT,
			ZmItem.F_SUBJECT,
			ZmItem.F_FOLDER,
			ZmItem.F_SIZE
		);
		if (appCtxt.accountList.size() > 2) {
			headers.push(ZmItem.F_ACCOUNT);
		}
		headers.push(ZmItem.F_DATE);
	}
	else {
		headers = [
			ZmItem.F_SELECTION,
			ZmItem.F_SORTED_BY
		];
	}

	return this._getHeaders(this.view, headers);
};

ZmMailMsgListView.prototype._initHeaders =
function() {

	ZmMailListView.prototype._initHeaders.apply(this, arguments);
	if (this._mode == ZmId.VIEW_CONV) {
		this._headerInit[ZmItem.F_SUBJECT] = {text:ZmMsg.fragment, noRemove:true, resizeable:true};
	}
};

ZmMailMsgListView.prototype._getHeaderToolTip =
function(field, itemIdx) {
	if (field == ZmItem.F_SUBJECT && this._mode == ZmId.VIEW_CONV) {
		return ZmMsg.fragment;
	}
	else {
		return ZmMailListView.prototype._getHeaderToolTip.apply(this, arguments);
	}
};

ZmMailMsgListView.prototype._getSingleColumnSortFields =
function() {
	return (this._mode == ZmId.VIEW_CONV) ? ZmMailMsgListView.SINGLE_COLUMN_SORT_CV : ZmMailListView.SINGLE_COLUMN_SORT;
};

ZmMailMsgListView.prototype._sortColumn = 
function(columnItem, bSortAsc, callback) {

	// call base class to save new sorting pref
	ZmMailListView.prototype._sortColumn.call(this, columnItem, bSortAsc);

	var query;
	var controller = AjxDispatcher.run((this._mode == ZmId.VIEW_CONV) ? "GetConvController" : "GetTradController");
	if (this._columnHasCustomQuery(columnItem)) 
	{
		query = this._getSearchForSort(columnItem._sortable, controller);
	}
	else if (this.getList().size() > 1 && this._sortByString) {
		query = controller.getSearchString();
	}

	var queryHint = this._controller.getSearchStringHint();

	if (query || queryHint) {
		if (this._mode == ZmId.VIEW_CONV) {
			var conv = controller.getConv();
			if (conv) {
				var respCallback = new AjxCallback(this, this._handleResponseSortColumn, [conv, columnItem, controller, callback]);
				var params = {
					query: query,
					queryHint: queryHint,
					sortBy: this._sortByString,
					getFirstMsg: controller.isReadingPaneOn()
				};
				conv.load(params, respCallback);
			}
		} else {
			var params = {
				query: query,
				queryHint: queryHint,
				types: [ZmItem.MSG],
				sortBy: this._sortByString,
				limit: this.getLimit(),
				callback: callback
			};
			appCtxt.getSearchController().search(params);
		}
	}
};

ZmMailMsgListView.prototype._columnHasCustomQuery =
function(columnItem) {
	return (columnItem._sortable == ZmItem.F_FLAG || columnItem._sortable == ZmItem.F_ATTACHMENT);
};

ZmMailMsgListView.prototype._handleResponseSortColumn =
function(conv, columnItem, controller, callback, result) {
	var searchResult = result.getResponse();
	var list = searchResult.getResults(ZmItem.MSG);
	controller.setList(list); // set the new list returned
	controller._activeSearch = searchResult;
	this.offset = 0;
	this.set(conv.msgs, columnItem);
	this.setSelection(conv.getFirstHotMsg({offset:this.offset, limit:this.getLimit(this.offset)}));
	if (callback instanceof AjxCallback)
		callback.run();
};

ZmMailMsgListView.prototype._getParentForColResize = 
function() {
	return this.parent;
};
