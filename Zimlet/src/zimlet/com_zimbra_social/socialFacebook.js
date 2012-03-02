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
 */

//Author: Raja Rao DV (rrao@zimbra.com)

function com_zimbra_socialFacebook(zimlet) {
	this.zimlet = zimlet;
	this.waitingForApproval = false;
	this.itemsLimit = 50;
	this._extendedPerms = "read_stream,publish_stream,offline_access,friends_activities,user_activities,friends_likes,user_likes";
	this.isZD = false;
	this._tableIdAndFBProfilesCache = new Array();
	this.apiKey = this.zimlet.getConfig("social_facebook_api_key");
	try{
		var version = appCtxt.getActiveAccount().settings.getInfoResponse.version;
		if(version.toLowerCase().indexOf("desktop") > 0) {
			this.isZD = true;
			this.apiKey = this.zimlet.getConfig("social_facebook_api_key_zd");
		}
	}catch(e) {
		//ignore
	}
}

com_zimbra_socialFacebook.prototype._addFBComment = function(params) {
	var url = "https://api.facebook.com/restserver.php";
	var account = this.zimlet.tableIdAndAccountMap[params.tableId];
	var paramsArray = [
		["method", "Stream.addComment"],
		["session_key", account.session_key],
		["comment", document.getElementById(params.commentFieldId).value],
		["uid", account.uid],
		["post_id", params.postId]
	];
	var urlParams = this._getFBParams(paramsArray, account.secret);
	params["account"] = account;
	this._doPOST(url, urlParams, new AjxCallback(this, this._addFBCommentCallback, params));
};

com_zimbra_socialFacebook.prototype._addFBCommentCallback =
function (params, response) {
	if (response.success) {
		var field = document.getElementById(params.commentFieldId);
		field.value = this.zimlet.getMessage("writeAComment");
		field.style.color = "gray";
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("commentAdded"), ZmStatusView.LEVEL_INFO);
		document.getElementById(params.commentBoxId).style.display = "none";
		//resetar timer
		var timer = setInterval(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 400000);
		this.zimlet.tableIdAndTimerMap[params.tableId] = timer;
		setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 3000);//refresh table after 3 secs
	} else {
		var msgDialog = appCtxt.getMsgDialog();
		msgDialog.setMessage(this.zimlet.getMessage("couldNotAddCommentTofb")+"<br/>" + response.text, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
};

com_zimbra_socialFacebook.prototype._publishToFacebook =
function (params) {
	var url = "https://api.facebook.com/restserver.php";
	var account = params.account;
	var paramsArray = [
		["method", "Stream.publish"],
		["session_key", account.session_key],
		["message",params.message],
		["uid", account.uid]
	];
	if (params.targetUser != undefined) {
		paramsArray.push(["target_id", params.targetUser]);
	}
	var urlParams = this._getFBParams(paramsArray, account.secret);
	this._doPOST(url, urlParams, new AjxCallback(this, this._publishToFacebookCallback, params));
};

com_zimbra_socialFacebook.prototype._publishToFacebookCallback =
function (params, response) {
	if (response.success) {
		if (this.zimlet.updateField) {
			this.zimlet.updateField.value = "";
			this.zimlet.showNumberOfLetters();
		}

		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("updatesSent"), ZmStatusView.LEVEL_INFO);
		var tableId = this.zimlet._getTableIdFromAccount(params.account);
		if (tableId) {
			setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, tableId, params.account), 3000);//refresh table after 3 secs
		}
	} else {
		var msgDialog = appCtxt.getMsgDialog();
		var msg = jsonObj.error;
		msgDialog.setMessage(this.zimlet.getMessage("couldNotPostToFacebook"), DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
};

com_zimbra_socialFacebook.prototype._getExtendedPermissionInfo =
function (params) {
	var url = "https://api.facebook.com/restserver.php";
	var account = params.account;
	var paramsArray = [
		["method", "Users.hasAppPermission"],
		["session_key", account.session_key],
		["ext_perm", params.permission],
		["uid", account.uid],
		["call_id", (new Date()).getTime()]
	];
	var urlParams = this._getFBParams(paramsArray, account.secret);
	this._doPOST(url, urlParams, new AjxCallback(this, this._getExtendedPermissionCallback, params));
};

com_zimbra_socialFacebook.prototype._getExtendedPermissionCallback =
function (params, response) {
	var permission = params.permission;
	var account = params.account;
	if (response.text == "0")
		account[permission] = "NO";
	else if (response.text == "1")
		account[permission] = "YES";

	if (params.callback)
		params.callback.run(this);
};

com_zimbra_socialFacebook.prototype.authorizeExtendedPermission =
function(params) {
	var permission = params.permission;
	var account = params.account;
	var url = "https://www.facebook.com/authorize.php?";
	var params = "version=1.0&ext_perm=" + permission + "&api_key=" + this.apiKey;
	this.zimlet.openCenteredWindow(AjxStringUtil.urlComponentEncode(url + params));

};

com_zimbra_socialFacebook.prototype.getExtendedPermForRead =
function () {
	var url = "https://www.facebook.com/authorize.php?";
	var params = "version=1.0&ext_perm=read_stream&api_key=" + this.apiKey;
	this.zimlet.openCenteredWindow(url + params);
};

com_zimbra_socialFacebook.prototype._fbGetStream =
function (tableId, account) {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Stream.get"],
		["session_key", account.session_key],
		["limit", this.itemsLimit],
		["filter_key", "nf,pp"]	
	];

	var sinceOrUntilParams = this._getSinceOrUntilParams(tableId);
	if(sinceOrUntilParams != "") {
		paramsArray.push(sinceOrUntilParams);
	}
	var params = this._getFBParams(paramsArray, account.secret);
	if (!tableId) {
		var tableId = this.zimlet._showCard({headerName:"facebook", type:"FACEBOOK", autScroll:true});
	}
	this._doPOST(url, params, new AjxCallback(this, this._getStreamCallback, tableId));
};

com_zimbra_socialFacebook.prototype._getSinceOrUntilParams =
function (tableId) {
	var refreshType = this.zimlet.tableIdAndRefreshType[tableId];
	var id;
	var idName;
	if(refreshType == "OLDER") {
		id = this.zimlet._tableIdAndBottomPostIdMap[tableId];
		if(!id) {//when the first page has no results..
			id = this.zimlet.tableIdAndMarkAsReadId[tableId];
		}
		idName = "end_time";
	} else if(refreshType == "NEWER") {
		id = this.zimlet.tableIdAndTopPostIdMap[tableId];
		idName = "start_time";
	} else  {
		return "";
	}
	if(id) {		
		return [idName, id];
	} else {
		return "";
	}
};

com_zimbra_socialFacebook.prototype.postLike =
function (obj) {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Stream.addLike"],
		["session_key", obj.account.session_key],
		["post_id", obj.postId]
	];
	var params = this._getFBParams(paramsArray, obj.account.secret);
	this._doPOST(url, params, new AjxCallback(this, this._postLikeCallback, obj));
};

com_zimbra_socialFacebook.prototype._postLikeCallback =
function (params, response) {
	if (response.success) {
		setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 3000);//refresh table after 3 secs
	} else {
		var msgDialog = appCtxt.getMsgDialog();
		msgDialog.setMessage(this.zimlet.getMessage("couldNotAddLikeToFb") +" <br/>" + response.text, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
};

com_zimbra_socialFacebook.prototype.insertMoreComments =
function (obj) {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Stream.getComments"],
		["session_key", obj.account.session_key],
		["post_id", obj.postId]
	];
	var params = this._getFBParams(paramsArray, obj.account.secret);
	this._doPOST(url, params, new AjxCallback(this, this._getMoreCommentsCallback, obj));
};

com_zimbra_socialFacebook.prototype._getMoreCommentsCallback =
function (obj, response) {
	var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("couldNotGetComments"), response);
	if(jsonObj.error) {
		if(appCtxt.getCurrentAppName().indexOf("social") > 0) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("couldNotGetComments") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
	obj["moreComments"] = jsonObj;
	this._getUserInfo(obj);
};

com_zimbra_socialFacebook.prototype._getUserInfo =
function (obj) {
	var moreComments = obj.moreComments;
	var uids = "";
	for (var i = 0; i < moreComments.length; i++) {
		if (uids == "") {
			uids = moreComments[i].fromid;
		} else {
			uids = uids + "," + moreComments[i].fromid;
		}
	}
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Users.getInfo"],
		["uids", uids],
		["fields", "name,pic_square,profile_url"],
		["call_id", (new Date()).getTime()],
		["session_key", obj.account.session_key]

	];
	var params = this._getFBParams(paramsArray, obj.account.secret);
	this._doPOST(url, params, new AjxCallback(this, this._getUsersInfoCallback, obj));
};

com_zimbra_socialFacebook.prototype._getUsersInfoCallback =
function (obj, response) {
	var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("tryRefreshingFBCard"), response);
	if(jsonObj.error) {
		if(appCtxt.getCurrentAppName().indexOf("social") > 0) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("tryRefreshingFBCard") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
	var moreprofiles = jsonObj;
	var fbProfiles = this._getFBProfiles(obj.tableId);
	if(fbProfiles && fbProfiles instanceof Array) {
		fbProfiles = fbProfiles.concat(moreprofiles);
		this._cacheFBProfiles(obj.tableId, fbProfiles);
	}
	var html = this.zimlet._getCommentsHtml(obj.moreComments, obj.moreComments.length, obj.postId, obj.divId, obj.account, obj.tableId);
	try {
		document.getElementById(obj.divId).innerHTML = html;
	} catch(e) {
	}
};

com_zimbra_socialFacebook.prototype._getStreamCallback =
function (tableId, response) {
	var jsonObj = this.zimlet._extractJSONResponse(tableId, this.zimlet.getMessage("errorTryRefreshing"), response);
	var posts = jsonObj.posts;
	if(jsonObj.profiles && posts) {
		this._cacheFBProfiles(tableId, jsonObj.profiles);
		if(posts && !(posts instanceof Array)) {
			posts = [];
		}
		this.zimlet.createCardView({tableId:tableId, items:posts, type:"FACEBOOK"});
	} else if(jsonObj.error){
		this.zimlet.createCardView({tableId:tableId, items:jsonObj, type:"FACEBOOK"});
	}else if(jsonObj.error_code && jsonObj.error_code != ""){
		jsonObj.error = jsonObj.error_code;
		this.zimlet.createCardView({tableId:tableId, items:jsonObj,  type:"FACEBOOK"});
	}
};

com_zimbra_socialFacebook.prototype._cacheFBProfiles =
function (tableId, profiles) {
	var pageNumber = this.zimlet.tableIdAndPageNumberMap[tableId];
	if(!pageNumber) {
		pageNumber = 1;
	}
	if(!this._tableIdAndFBProfilesCache[tableId]) {
		this._tableIdAndFBProfilesCache[tableId] = [];
	}
	this._tableIdAndFBProfilesCache[tableId][pageNumber] = profiles;
};

com_zimbra_socialFacebook.prototype._getFBProfiles =
function (tableId) {
	var pageNumber = this.zimlet.tableIdAndPageNumberMap[tableId];
	if(!pageNumber) {
		pageNumber = 1;
	}
	return this._tableIdAndFBProfilesCache[tableId][pageNumber];
};

com_zimbra_socialFacebook.prototype._getSignatureFromJSP =
function (args) {
	var params = new Array;
	for (var i = 0; i < args.length; i++) {
		var item = args[i];
		params.push(item[0] + "=" + item[1]);
	}
	params.push("isZD="+this.isZD);
	var url = this.zimlet.getResource("md5.jsp") + "?" + params.join("&");
	var response = AjxRpc.invoke(null, url, null, null, true);
	var obj = eval("(" + response.text + ")");
	return obj.signature;
};

com_zimbra_socialFacebook.prototype.fbCreateToken =
function () {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Auth.createToken"]
	];
	var signature = this._getSignatureFromJSP(paramsArray);
	setTimeout(AjxCallback.simpleClosure(this._doFbCreateToken, this, url, paramsArray, signature), 500);//delay calling by .5 secs(otherwise, sometimes breaks in ff)
};

com_zimbra_socialFacebook.prototype._doFbCreateToken =
function (url, paramsArray, signature) {
	var params = this._getFBParams(paramsArray, null, signature);
	this._doPOST(url, params, new AjxCallback(this, this._fbCreateTokenCallback));
	this.zimlet.preferences.showAddFBInfoDlg();
};

com_zimbra_socialFacebook.prototype._fbCreateTokenCallback =
function (response) {
	var text = response.text;
	this.fb_auth_token = eval("(" + text + ")");
	this.loginToFB(this.fb_auth_token);
};

com_zimbra_socialFacebook.prototype.loginToFB =
function (authToken) {
	var url = "https://www.facebook.com/login.php?";
	var params = new Array();
	params["api_key"] = AjxStringUtil.urlComponentEncode(this.apiKey);
	params["fbconnect"] = AjxStringUtil.urlComponentEncode("true");
	params["v"] = AjxStringUtil.urlComponentEncode("1.0");
	params["connect_display"] = AjxStringUtil.urlComponentEncode("popup");
	params["next"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_success.html");
	params["cancel_url"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_failure.html");
	if (authToken) {
		params["auth_token"] = AjxStringUtil.urlComponentEncode(this.fb_auth_token);
	}
	if (!authToken) {
		params["req_perms"] = AjxStringUtil.urlComponentEncode(this._extendedPerms);
	}
	var tmp = [];
	for (var name in params) {
		tmp.push(name + "=" + params[name]);
	}
	var p = tmp.join("&");
	url = url + p;
	this.zimlet.openCenteredWindow(url);
};

com_zimbra_socialFacebook.prototype._getSessionId =
function () {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "auth.getSession"],
		["auth_token", this.fb_auth_token]
	];
	var signature = this._getSignatureFromJSP(paramsArray);
	setTimeout(AjxCallback.simpleClosure(this._doGetSessionId, this, url, paramsArray, signature), 500);//delay calling by .5 secs(otherwise, sometimes breaks in ff)


};
com_zimbra_socialFacebook.prototype._doGetSessionId =
function (url, paramsArray, signature) {
	var params = this._getFBParams(paramsArray, null, signature);
	this._doPOST(url, params, new AjxCallback(this, this._sessionIdCallback));
};

com_zimbra_socialFacebook.prototype._sessionIdCallback =
function (response) {
	var text = response.text;
	if (text.indexOf("session_key") >= 0 && text.indexOf("secret") >= 0) {
		var fbStr = this._convertFB_JsonStrToUrlEncodedStr(text);
		this.manageFacebookAccounts(fbStr);
		var authStr = this.zimlet.getMessage("fbSignInLine3");
		this.zimlet.preferences._setAccountPrefDlgAuthMessage(authStr, "blue");
		this.zimlet.preferences._updateAccountsTable({message:authStr, color:"blue"});
		this.zimlet.preferences._updateAllFBPermissions({message:authStr, color:"blue",askForPermissions:false});
	}
	this.zimlet.preferences._getFbInfoDialog.popdown();
};

com_zimbra_socialFacebook.prototype.askForPermissions =
function (fromAuthorizeBtn) {
	var params = new Array();
	if (!fromAuthorizeBtn) {
		var url = "https://www.facebook.com/connect/prompt_permissions.php?";
		params["api_key"] = AjxStringUtil.urlComponentEncode(this.apiKey);
		params["fbconnect"] = AjxStringUtil.urlComponentEncode("true");
		params["v"] = AjxStringUtil.urlComponentEncode("1.0");
		params["display"] = AjxStringUtil.urlComponentEncode("popup");
		params["extern"] = AjxStringUtil.urlComponentEncode("1");
		params["next"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_success.html");
		params["cancel_url"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_failure.html");

		params["ext_perm"] = AjxStringUtil.urlComponentEncode(this._extendedPerms);
	} else {
		var url = "https://www.facebook.com/login.php?";
		params["api_key"] = AjxStringUtil.urlComponentEncode(this.apiKey);
		params["fbconnect"] = AjxStringUtil.urlComponentEncode("true");
		params["v"] = AjxStringUtil.urlComponentEncode("1.0");
		params["connect_display"] = AjxStringUtil.urlComponentEncode("popup");
		params["cancel_url"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_failure.html");
		params["req_perms"] = AjxStringUtil.urlComponentEncode(this._extendedPerms);
	}
	var tmp = [];
	for (var name in params) {
		tmp.push(name + "=" + params[name]);
	}
	var p = tmp.join("&");
	url = url + p;
	this.zimlet.openCenteredWindow(url);
};

com_zimbra_socialFacebook.prototype._convertFB_JsonStrToUrlEncodedStr = function(text) {
	var jsonObj = eval("(" + text + ")");
	var fb_raw = "";
	for (var el in jsonObj) {
		if (fb_raw == "")
			fb_raw = el + "=" + jsonObj[el];
		else
			fb_raw = fb_raw + "&" + el + "=" + jsonObj[el];
	}
	return fb_raw;
};

com_zimbra_socialFacebook.prototype._doPOST =
function (url, params, callback) {
	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Content-length"] = params.length;
	hdrs["Connection"] = "close";
	var entireurl = ZmZimletBase.PROXY + url;
	AjxRpc.invoke(params, entireurl, hdrs, callback, false);
}

com_zimbra_socialFacebook.prototype._getFBParams =
function(otherParamsArray, secret, signatureFromJSP) {
	var paramsArray = [
		["api_key",this.apiKey],
		["v","1.0"],
		["format", "json"]
	].concat(otherParamsArray);
	paramsArray = paramsArray.sort();
	var sig = "";
	if (!signatureFromJSP) {
		if (paramsArray.length > 0) {
			for (var i = 0; i < paramsArray.length; i++) {
				sig = sig + paramsArray[i][0] + "=" + paramsArray[i][1];
			}
			AjxPackage.require("Crypt");
			//AjxPackage.require("ajax.util.AjxMD5");
			sig =  AjxMD5.hex_md5(sig + secret);
		}
	} else {
		sig = signatureFromJSP;
	}

	var arry = new Array();
	for (var i = 0; i < paramsArray.length; i++) {
		arry.push(AjxStringUtil.urlComponentEncode(paramsArray[i][0]) + "=" + AjxStringUtil.urlComponentEncode(paramsArray[i][1]));
	}
	arry.push(AjxStringUtil.urlComponentEncode("sig") + "=" + AjxStringUtil.urlComponentEncode(sig));
	return arry.join("&");
};

com_zimbra_socialFacebook.prototype.manageFacebookAccounts = function(text) {
	var nv = text.split("&");
	var tObj = {};
	for (var i = 0; i < nv.length; i++) {
		var tmp = nv[i].split("=");
		tObj[tmp[0]] = tmp[1];
	}
	if (tObj["__type"] == undefined) {
		tObj["__type"] = "facebook";
	}
	if (tObj["__on"] == undefined) {
		tObj["__on"] = "true";
	}
	if (tObj["__pos"] == undefined) {
		tObj["pos"] = "";
	}
	if (tObj["__s"] == undefined) { //__s means shown & 1 means true
		tObj["__s"] = "1";
	}
	//to normalize names with fb
	tObj.raw = text;
	tObj.name = "facebook";
	tObj.type = tObj["__type"];
	this.zimlet.allAccounts[tObj.name + tObj.uid] = tObj;
};

com_zimbra_socialFacebook.prototype._updateFacebookStream =
function(tableId, account) {
	this._fbGetStream(tableId, account);
};

com_zimbra_socialFacebook.prototype.getFacebookProfile =
function(id, tableId) {
	var fbProfiles = this._getFBProfiles(tableId);
	for (var i = 0; i < fbProfiles.length; i++) {
		var reqId = fbProfiles[i].id == undefined ? fbProfiles[i].uid : fbProfiles[i].id;
		if (id == reqId) {
			return fbProfiles[i];
		}
	}
};