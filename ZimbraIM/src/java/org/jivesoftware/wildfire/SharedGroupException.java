/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010, 2011 VMware, Inc.
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
package org.jivesoftware.wildfire;

/**
 * Thrown when a a user is trying to add or remove a contact from his/her roster that belongs to a
 * shared group.
 *
 * @author Gaston Dombiak
 */
public class SharedGroupException extends Exception {

    public SharedGroupException() {
        super();
    }

    public SharedGroupException(String msg) {
        super(msg);
    }
}
