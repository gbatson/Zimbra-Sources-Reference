/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV rrao@zimbra.com
 * Allows ignoring message for a specific period of time
 */


function com_zimbra_ignoremsgs() {
}

com_zimbra_ignoremsgs.prototype = new ZmZimletBase();
com_zimbra_ignoremsgs.prototype.constructor = com_zimbra_ignoremsgs;

com_zimbra_ignoremsgs.MESSAGE_VIEW = "message";
com_zimbra_ignoremsgs.ignoremsgsFolder = 'Ignored Messages';
com_zimbra_ignoremsgs.ignoreTheseMsgsFltr = 'Move These Messages to \'Ignored Messages\' Folder(used by \'Ignore Messages\' Zimlet)';
com_zimbra_ignoremsgs.ignoreMsgsZimlet = "IGNORE_MSGS_ZIMLET";

com_zimbra_ignoremsgs.prototype.init =
function() {
	this.turnOnIgnoreMsgsZimlet = this.getUserProperty("turnOnIgnoreMsgsZimlet") == "true";
	if (!this.turnOnIgnoreMsgsZimlet)
		return;

	this.ignoremsgs_ignoreForDays = parseInt(this.getUserProperty("ignoremsgs_ignoreForDays"));
};

com_zimbra_ignoremsgs.prototype._storeSubjectAndDate =
function(newSubject) {
	var today = new Date();
	var yyyymmdd = today.getFullYear() + "-" + today.getMonth() + "-" + today.getDate();

	//todo: need to check if the subject is already present

	this._subjectAndDate.push({subject:newSubject, date:yyyymmdd});
	this.setUserProperty("ignoreMsgs_storeSubjectWithDate", this.getJSONFormatOfSubjectAndDate(), true);

	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg("This Message Will be ignored for next " + this.ignoremsgs_ignoreForDays + " days", ZmStatusView.LEVEL_INFO, null, transitions);
};

com_zimbra_ignoremsgs.prototype.getJSONFormatOfSubjectAndDate =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "[";
	for (var j = 0; j < this._subjectAndDate.length; j++) {
		var obj = this._subjectAndDate[j];
		html[i++] = "{";
		html[i++] = "subject:";
		html[i++] = "\"" + obj.subject + "\"";
		html[i++] = ",date:";
		html[i++] = "\"" + obj.date + "\"";
		html[i++] = "}";
		if (j != this._subjectAndDate.length - 1) {
			html[i++] = ",";
		}
	}
	html[i++] = "]";
	return html.join("");
};

com_zimbra_ignoremsgs.prototype._createListOfConditions =
function() {
	var sdData = this.getUserProperty("ignoreMsgs_storeSubjectWithDate");
	this._subjectAndDate = "";
	try {
		if (sdData != "") {
			this._subjectAndDate = eval("(" + sdData + ")");
		}
	} catch(e) {
	}
	if (!(this._subjectAndDate instanceof Array)) {
		this._subjectAndDate = new Array();
	}
};

//param should be {message:message, callback:callback}
com_zimbra_ignoremsgs.prototype._updateFilter =
function(param) {
	var rule = null;
	var modifyRule = true;
	rule = this.filterRules.getRuleByName(com_zimbra_ignoremsgs.ignoreTheseMsgsFltr);
	if (rule == undefined) {
		rule = new ZmFilterRule(com_zimbra_ignoremsgs.ignoreTheseMsgsFltr, true);
		modifyRule = false;
	}
	this._updateSubjectAndDate(rule);
	this._updateConditions(rule, param.message.subject);

	if (!modifyRule) {
		rule.addAction(2, com_zimbra_ignoremsgs.ignoremsgsFolder);//file to folder Ignored Messages
		rule.addAction(ZmFilterRule.A_STOP);//stop further action
		this.filterRules._insertRule(rule, 0);
	}
	this.filterRules._saveRules(0, false, param.callback);
};

com_zimbra_ignoremsgs.prototype._updateSubjectAndDate =
function(rule) {
	var newConditions = rule.conditions.headerTest;
	if (newConditions == undefined)
		return;

	var newSubjectAndDate = [];
	for (var i = 0; i < newConditions.length; i++) {
		var condition = newConditions[i];
		for (var j = 0; j < this._subjectAndDate.length; j++) {
			if (this._subjectAndDate[j].subject == condition.value) {
				newSubjectAndDate.push(this._subjectAndDate[j]);
				break;
			}
		}
	}
	this._subjectAndDate = newSubjectAndDate;
}

com_zimbra_ignoremsgs.prototype._updateConditions =
function(rule, newSubject) {
	var prevConditions = rule.conditions.headerTest;
	if (prevConditions == undefined) {
		rule.addCondition("headerTest", 3, newSubject, "subject");
		return;
	}
	var newConditions = [];
	for (var i = 0; i < prevConditions.length; i++) {
		var condition = prevConditions[i];
		for (var j = 0; j < this._subjectAndDate.length; j++) {
			if (this._subjectAndDate[j].subject != condition.value)
				continue;

			var dateStr = this._subjectAndDate[j].date.split("-");
			var date = new Date(dateStr[0], dateStr[1], dateStr[2]);
			var tmp = new Date();
			var today = new Date(tmp.getFullYear(), tmp.getMonth(), tmp.getDate());
			var diffDate = (today - date) / (3600 * 24 * 1000);
			if (diffDate <= this.ignoremsgs_ignoreForDays) {
				newConditions.push(condition);
				break;
			}
		}
	}
	rule.conditions.headerTest = newConditions;//set the new set of conditions
	rule.addCondition("headerTest", 3, newSubject, "subject");
};

com_zimbra_ignoremsgs.prototype.setIgnoreMsgsFldrId =
function(callback) {
	if (this.ignoreMsgsFldrId) {
		if (callback) {
			callback.run(this);
		}
		return;
	}
	var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
	var folderNode = soapDoc.set("folder");
	folderNode.setAttribute("l", appCtxt.getFolderTree().root.id);

	var command = new ZmCsfeCommand();
	var top = command.invoke({soapDoc: soapDoc}).Body.GetFolderResponse.folder[0];

	var folders = top.folder;
	if (folders) {
		for (var i = 0; i < folders.length; i++) {
			var f = folders[i];
			if (f && f.name == com_zimbra_ignoremsgs.ignoremsgsFolder && f.view == com_zimbra_ignoremsgs.view) {
				this.ignoreMsgsFldrId = f.id;
				break;
			}
		}
	}
	if (this.ignoreMsgsFldrId) {
		if (callback)
			callback.run(this);
	} else {
		this.createFolder(callback);	//there is no such folder, so create one.
	}
};

com_zimbra_ignoremsgs.prototype.initializeToolbar =
function(app, toolbar, controller, view) {
	if (!this.turnOnIgnoreMsgsZimlet)
		return;

	if (view == ZmId.VIEW_CONVLIST ||
		view == ZmId.VIEW_CONV ||
		view == ZmId.VIEW_TRAD)
	{
		var buttonIndex = -1;
		for (var i = 0, count = toolbar.opList.length; i < count; i++) {
			if (toolbar.opList[i] == ZmOperation.PRINT) {
				buttonIndex = i + 1;
				break;
			}
		}
		ZmMsg.ignoreMsgsBtnLabel = "Ignore";
		var buttonArgs = {
			text	: ZmMsg.ignoreMsgsBtnLabel,
			tooltip: "Sets the Filter to ignore this message and Moves it to 'Ignored Messages Folder'",
			index: buttonIndex,
			image: "ignoremsgs-panelIcon"
		};
		var button = toolbar.createOp(com_zimbra_ignoremsgs.ignoreMsgsZimlet, buttonArgs);
		button.addSelectionListener(new AjxListener(this, this._buttonListener, [controller]));
	}
};

com_zimbra_ignoremsgs.prototype._buttonListener =
function(controller) {
	var message = controller.getMsg();
	this._createListOfConditions();

	//create callbacks in reverse order(of work flow)
	var callback_updateServerAboutNewConditions = new AjxCallback(this, this._storeSubjectAndDate, message.subject);
	var callback_moveMsgAfterAddingToFilter = new AjxCallback(this, this._moveMsg, {message:message, callback:callback_updateServerAboutNewConditions});
	//first makesure we have the folder, then update the filter
	var callback_AfterLoadingRulesCallback = new AjxCallback(this, this._updateFilter, {message:message, callback:callback_moveMsgAfterAddingToFilter});
	this.setIgnoreMsgsFldrId(new AjxCallback(this, this._loadAllFilterRules, callback_AfterLoadingRulesCallback));
};

com_zimbra_ignoremsgs.prototype._moveMsg =
function(param) {
	param.message.move(this.ignoreMsgsFldrId);
	if (param.callback) {
		param.callback.run(this);
	}
};

com_zimbra_ignoremsgs.prototype._loadAllFilterRules =
function(callback) {
	if (!this.filterRules) {
		this.filterRules = AjxDispatcher.run("GetFilterRules");
	}
	this.filterRules.loadRules(false, callback);
};

com_zimbra_ignoremsgs.prototype._deleteFilter =
function() {
	var rule = this.filterRules.getRuleByName(com_zimbra_ignoremsgs.ignoreTheseMsgsFltr);
	if (rule != undefined) {
		this.filterRules.removeRule(rule);
	}
};

com_zimbra_ignoremsgs.prototype.createFolder =
function(postCallback) {
	var params = {color:null, name:com_zimbra_ignoremsgs.ignoremsgsFolder, url:null, view:com_zimbra_ignoremsgs.view, l:"1", postCallback:postCallback};
	this._createFolder(params);
};

com_zimbra_ignoremsgs.prototype.doubleClicked =
function() {
	this.singleClicked();
};
com_zimbra_ignoremsgs.prototype.singleClicked =
function() {
	this.ignoremsgs_ignoreForDays = parseInt(this.getUserProperty("ignoremsgs_ignoreForDays"));
	this._showPreferenceDlg();
};

com_zimbra_ignoremsgs.prototype._showPreferenceDlg = function() {
	//if zimlet dialog already exists...
	if (this._preferenceDialog) {
		this._preferenceDialog.popup();
		return;
	}
	this._preferenceView = new DwtComposite(this.getShell());
	this._preferenceView.getHtmlElement().style.overflow = "auto";
	this._preferenceView.getHtmlElement().innerHTML = this._createPrefView();
	this._preferenceDialog = this._createDialog({title:"Ignore Messages Preferences", view:this._preferenceView, standardButtons:[DwtDialog.OK_BUTTON]});
	this._preferenceDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okPreferenceBtnListener));
	if (this.getUserProperty("turnOnIgnoreMsgsZimlet") == "true") {
		this.turnOnIgnoreMsgsZimlet = true;
		document.getElementById("turnOnIgnoreMsgsZimlet").checked = true;
	}
	this._preferenceDialog.popup();
};

com_zimbra_ignoremsgs.prototype._okPreferenceBtnListener =
function() {
	this._loadAllFilterRules(new AjxCallback(this, this._storePreferences));
};

com_zimbra_ignoremsgs.prototype._storePreferences =
function() {
	this._preferenceDialog.popdown();
	this._reloadRequired = false;
	if (document.getElementById("turnOnIgnoreMsgsZimlet").checked) {
		if (!this.turnOnIgnoreMsgsZimlet) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnOnIgnoreMsgsZimlet", "true");

	} else {
		//delete filter
		this._deleteFilter();
		this.setUserProperty("turnOnIgnoreMsgsZimlet", "false");
		if (this.turnOnIgnoreMsgsZimlet) {
			this._reloadRequired = true;
		}
			this.setUserProperty("ignoreMsgs_storeSubjectWithDate", "");

	}

	if (this._getignoreSelectedValue() != this.ignoremsgs_ignoreForDays) {
		this.setUserProperty("ignoremsgs_ignoreForDays", this._getignoreSelectedValue());
		this._reloadRequired = true;
	}

	if (this._reloadRequired) {
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg("Please wait. Browser will be refreshed for changes to take effect..", ZmStatusView.LEVEL_INFO, null, transitions);
		this.saveUserProperties(new AjxCallback(this, this._reloadBrowser));
	}
};

com_zimbra_ignoremsgs.prototype._reloadBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};

com_zimbra_ignoremsgs.prototype._createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "Ignore Messages For:";
	html[i++] = this._getignoreSelectHTML();
	html[i++] = "</DIV>";
	html[i++] = "<BR/>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='turnOnIgnoreMsgsZimlet'  type='checkbox'/>Enable 'Ignore Messages'-Zimlet";
	html[i++] = "</DIV>";
	return html.join("");
};

com_zimbra_ignoremsgs.prototype._getignoreSelectHTML =
function() {
	var html = new Array();
	var i = 0;
	var d = [5, 10, 15, 20];
	html[i++] = "<select id=\"ignoremsgs_ignoreForDays\">";
	for (var j = 0; j < d.length; j++) {
		var dayNum = d[j];
		if (this.ignoremsgs_ignoreForDays != dayNum) {
			html[i++] = "<option value=\"" + dayNum + "\">" + dayNum + " days</option>";
		} else {
			html[i++] = "<option value=\"" + dayNum + "\" selected>" + dayNum + " days</option>";
		}
	}
	html[i++] = "</select>";
	return html.join("");
};

com_zimbra_ignoremsgs.prototype._getignoreSelectedValue =
function() {
	return document.getElementById("ignoremsgs_ignoreForDays").value;
};

com_zimbra_ignoremsgs.prototype._createFolder =
function(params) {
	var jsonObj = {CreateFolderRequest:{_jsns:"urn:zimbraMail"}};
	var folder = jsonObj.CreateFolderRequest.folder = {};
	for (var i in params) {
		if (i == "callback" || i == "errorCallback" || i == "postCallback") {
			continue;
		}

		var value = params[i];
		if (value) {
			folder[i] = value;
		}
	}
	var _createFldrCallback = new AjxCallback(this, this._createFldrCallback, params);
	var _createFldrErrCallback = new AjxCallback(this, this._createFldrErrCallback, params);
	return appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode:true, errorCallback:_createFldrErrCallback, callback:_createFldrCallback});
};

com_zimbra_ignoremsgs.prototype._createFldrCallback =
function(params, response) {
	if (params.name == com_zimbra_ignoremsgs.ignoremsgsFolder) {
		this.ignoreMsgsFldrId = response.getResponse().CreateFolderResponse.folder[0].id;
		if (params.postCallback) {
			params.postCallback.run(this);
		}
	} else {
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg("'Ignored Messages' folder Created", ZmStatusView.LEVEL_INFO, null, transitions);
	}
};

com_zimbra_ignoremsgs.prototype._createFldrErrCallback =
function(params, ex) {
	if (!params.url && !params.name) {
		return false;
	}
	var msg;
	if (params.name && (ex.code == ZmCsfeException.MAIL_ALREADY_EXISTS)) {
		msg = AjxMessageFormat.format(ZmMsg.errorAlreadyExists, [params.name]);
	} else if (params.url) {
		var errorMsg = (ex.code == ZmCsfeException.SVC_RESOURCE_UNREACHABLE) ? ZmMsg.feedUnreachable : ZmMsg.feedInvalid;
		msg = AjxMessageFormat.format(errorMsg, params.url);
	}
	var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
	appCtxt.getAppController().setStatusMsg("Could Not create 'Ignored Messages' Folder", ZmStatusView.LEVEL_WARNING, null, transitions);
	if (msg) {
		this._showErrorMsg(msg);
		return true;
	}
	return false;
};

com_zimbra_ignoremsgs.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};