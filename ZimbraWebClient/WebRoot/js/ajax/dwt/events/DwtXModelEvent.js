/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
 * 
 * 
 * @private
 */
DwtXModelEvent = function(instance, modelItem, refPath, details) {
	if (arguments.length == 0) return;
	this.instance = instance;
	this.modelItem = modelItem;
	this.refPath = refPath;
	this.details = details;
}

DwtEvent.prototype.toString = function() {
	return "DwtXModelEvent";
}