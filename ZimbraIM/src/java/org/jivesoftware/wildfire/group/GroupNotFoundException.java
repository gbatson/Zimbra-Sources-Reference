/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
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
package org.jivesoftware.wildfire.group;

/**
 * Thrown when unable to find or load a group.
 *
 * @author Matt Tucker
 */
public class GroupNotFoundException extends Exception {

    /**
     * Constructs a new exception with null as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to
     * {@link #initCause(Throwable) initCause}.
     */
    public GroupNotFoundException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message. The cause is
     * not initialized, and may subsequently be initialized by a call to
     * {@link #initCause(Throwable) initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *      retrieval by the {@link #getMessage()} method.
     */
    public GroupNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.<p>
     *
     * Note that the detail message associated with cause is not automatically incorporated
     * in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *      {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *      {@link #getCause()} method). (A null value is permitted, and indicates
     *      that the cause is nonexistent or unknown.)
     */
    public GroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and
     * detail message of cause). This constructor is useful for exceptions that are
     * little more than wrappers for other throwables (for example,
     * java.security.PrivilegedActionException).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *      {@link #getCause()} method). (A null value is permitted, and indicates
     *      that the cause is nonexistent or unknown.)
     */
    public GroupNotFoundException(Throwable cause) {
        super(cause);
    }
}