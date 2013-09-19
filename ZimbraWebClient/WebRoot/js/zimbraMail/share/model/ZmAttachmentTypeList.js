/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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

/**
 * @overview
 * 
 * This file defines a list of attachment types.
 *
 */

/**
 * Creates an attachment type list
 * @class
 * This class represents attachment types.
 * 
 * @extends	ZmModel
 */
ZmAttachmentTypeList = function() {
	ZmModel.call(this, ZmEvent.S_ATT);
};

ZmAttachmentTypeList.prototype = new ZmModel;
ZmAttachmentTypeList.prototype.constructor = ZmAttachmentTypeList;

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmAttachmentTypeList.prototype.toString = 
function() {
	return "ZmAttachmentTypeList";
};

/**
 * Gets the attachments.
 * 
 * @return	{Array}	an array of attachments
 */
ZmAttachmentTypeList.prototype.getAttachments =
function() {
	return this._attachments;
};

/**
 * Compares attachment type lists by description.
 * 
 * @param	{ZmAttachmentTypeList}	a			the first entry
 * @param	{ZmAttachmentTypeList}	b			the first entry
 * @return	{int}	0 if the entries match; 1 if "a" is before "b"; -1 if "b" is before "a"
 */
ZmAttachmentTypeList.compareEntry = 
function(a,b) {
	if (a.desc.toLowerCase() < b.desc.toLowerCase())
		return -1;
	if (a.desc.toLowerCase() > b.desc.toLowerCase())
		return 1;
	else
		return 0;
};

/**
 * Loads the attachments.
 * 
 * @param	{AjxCallback}	callback		the callback to call after load
 */
ZmAttachmentTypeList.prototype.load =
function(callback) {
	this._attachments = new Array();

	var soapDoc = AjxSoapDoc.create("BrowseRequest", "urn:zimbraMail");
	soapDoc.getMethod().setAttribute("browseBy", "attachments");

	var respCallback = new AjxCallback(this, this._handleResponseLoad, callback);
	appCtxt.getAppController().sendRequest({soapDoc: soapDoc, asyncMode: true, callback: respCallback});
};

/**
 * @private
 */
ZmAttachmentTypeList.prototype._handleResponseLoad =
function(callback, result) {
	var att = result.getResponse().BrowseResponse.bd;
	if (att) {
		for (var i = 0; i < att.length; i++) {
			var type = att[i]._content;
			if (!ZmMimeTable.isIgnored(type) && (type.indexOf("/") != -1 || type == "image"))
				this._attachments.push(ZmMimeTable.getInfo(type, true));
		}
		this._attachments.sort(ZmAttachmentTypeList.compareEntry);
	}
	
	if (callback) callback.run(result);
};
