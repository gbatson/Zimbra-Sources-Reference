/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 VMware, Inc.
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
package com.zimbra.cs.offline.util;

import junit.framework.TestCase;

public class TestOfflineUtil extends TestCase {
	public void testFixItemOrder() {
		String[] oldOrder = {"abc", "foo", "bar"};
		String[] newOrder = {"def", "abc", "bar", "xyz", "123"};
		
		OfflineUtil.fixItemOrder(oldOrder, newOrder);
		
		assertEquals(newOrder[0], "abc");
		assertEquals(newOrder[1], "bar");
	}
}
