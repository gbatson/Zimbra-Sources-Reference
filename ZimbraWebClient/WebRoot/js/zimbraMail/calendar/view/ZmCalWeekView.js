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

ZmCalWeekView = function(parent, posStyle, controller, dropTgt) {
	ZmCalColView.call(this, parent, posStyle, controller, dropTgt, ZmId.VIEW_CAL_WEEK, 7, false);
}

ZmCalWeekView.prototype = new ZmCalColView;
ZmCalWeekView.prototype.constructor = ZmCalWeekView;

ZmCalWeekView.prototype.toString = 
function() {
	return "ZmCalWeekView";
}
