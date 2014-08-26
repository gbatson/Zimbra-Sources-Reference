/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2014 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at: http://www.zimbra.com/license
 * The License is based on the Mozilla Public License Version 1.1 but Sections 14 and 15 
 * have been added to cover use of software over a computer network and provide for limited attribution 
 * for the Original Developer. In addition, Exhibit A has been modified to be consistent with Exhibit B. 
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. 
 * See the License for the specific language governing rights and limitations under the License. 
 * The Original Code is Zimbra Open Source Web Client. 
 * The Initial Developer of the Original Code is Zimbra, Inc. 
 * All portions of the code are Copyright (C) 2009, 2014 Zimbra, Inc. All Rights Reserved. 
 * ***** END LICENSE BLOCK *****
 */
var ZMTB_Actions = function(zmtb)
{
	if(!zmtb)
		return;
	ZMTB_TBItem.call(this, zmtb);
	this._rqManager = zmtb.getRequestManager();
	this._folderMan = zmtb.getFolderManager();
	this._rqIds = [];
	var This=this;
	document.getElementById("ZimTB-ActionsBar").addEventListener("keypress", function(e){if(e.keyCode == e.DOM_VK_ESCAPE)This.hideActions()}, false);
}

ZMTB_Actions.prototype = new ZMTB_TBItem();
ZMTB_Actions.prototype.constructor = ZMTB_Actions;

ZMTB_Actions.prototype.addRqId = function(id)
{
	this._rqIds.push(id);
}

ZMTB_Actions.prototype.clearRqId = function(id)
{
	for (var i = this._rqIds.length - 1; i >= 0; i--)
		if(this._rqIds[i] == id)
			this._rqIds.splice(i, 1);
}

ZMTB_Actions.prototype.hasRequestId = function(id)
{
	for (var i = this._rqIds.length - 1; i >= 0; i--)
		if(this._rqIds[i] == id)
			return true;
	return false;
}

ZMTB_Actions.prototype.openActions = function(elementId)
{
	for (var i=0; i < document.getElementById("ZimTB-ActionsBar").childNodes.length; i++)
		document.getElementById("ZimTB-ActionsBar").childNodes[i].hidden = true;
	document.getElementById(elementId).hidden = false;
	document.getElementById("ZimTB-ActionsBar").hidden=false;
}

ZMTB_Actions.prototype.hideActions = function()
{
	for (var i=0; i < document.getElementById("ZimTB-ActionsBar").childNodes.length; i++)
		document.getElementById("ZimTB-ActionsBar").childNodes[i].hidden = true;
	document.getElementById("ZimTB-ActionsBar").hidden=true;
	this._zmtb.clearNotifications();
}

ZMTB_Actions.prototype.newLinked = function(name, view, parentId, owner, path)
{
	var sd = ZMTB_AjxSoapDoc.create("CreateMountpointRequest", ZMTB_RequestManager.NS_MAIL);
	sd.set("link", {"name":name, "view":view, "l":parentId, "owner":owner, "path":path});
	this._rqManager.sendRequest(sd);
}

ZMTB_Actions.prototype.newFolder = function(name, view, parentId, url)
{
	var sd = ZMTB_AjxSoapDoc.create("CreateFolderRequest", ZMTB_RequestManager.NS_MAIL);
	var folder =  {"name":name, "view":view, "l":parentId};
	if(url)
		folder.url = url;
	sd.set("folder", folder);
	var rid = this._rqManager.getNewRqId();
	sd.set("requestId", rid);
	this._rqManager.sendRequest(sd)
	this.addRqObj(rid, null);
}

ZMTB_Actions.prototype._populateList = function(list, folders, root)
{
	for (var i=0; i < folders.length; i++)
		if(this._listIndexOf(folders[i].id, list)<0)
			list.appendItem(this._folderMan.getFullPath(folders[i].id, root), folders[i].id);
}

ZMTB_Actions.prototype._listIndexOf = function(folderId, list)
{
	for (var i=0; i < list.itemCount; i++)
		if(list.getItemAtIndex(i).value == folderId)
			return i;
	return -1;
}