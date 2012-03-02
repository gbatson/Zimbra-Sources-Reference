/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
 */

/**
 * Creates the version assistant.
 * @class
 * This class represents a version assistant.
 * 
 * @extends		ZmAssistant
 */
ZmVersionAssistant = function() {
	ZmAssistant.call(this, "Client Version Information", ".version");
};

ZmVersionAssistant.prototype = new ZmAssistant();
ZmVersionAssistant.prototype.constructor = ZmVersionAssistant;

ZmVersionAssistant.prototype.handle =
function(dialog, verb, args) {
	dialog._setOkButton(AjxMsg.ok, true, true);
	this._setField("Version", appCtxt.get(ZmSetting.CLIENT_VERSION), false, true);
	this._setField("Release", appCtxt.get(ZmSetting.CLIENT_RELEASE), false, true);
	this._setField("Build Date", appCtxt.get(ZmSetting.CLIENT_DATETIME), false, true);	
};
