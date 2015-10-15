/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011, 2012, 2013, 2014 Zimbra, Inc.
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
 * All portions of the code are Copyright (C) 2011, 2012, 2013, 2014 Zimbra, Inc. All Rights Reserved. 
 * ***** END LICENSE BLOCK *****
 */

UT.module("GetOriginalContent", ["Mail"]);

UtGetOriginalContent = function() {};

UtGetOriginalContent.test = function() {
    ZmUnitTestUtil.log("starting conversation view test");

	UT.expect(UtGetOriginalContent_data.length);
    for (var i = 0, count = UtGetOriginalContent_data.length; i < count; i++) {
        var obj = UtGetOriginalContent_data[i];
        var output = AjxStringUtil.getOriginalContent(obj.input, obj.isHtml);
		var referenceOutput = (obj.output == UtZWCUtils.SAME) ? obj.input : obj.output;
        UT.equals(output, referenceOutput);
    }
};

UT.test("GetOriginalContent Tests", UtGetOriginalContent.test);