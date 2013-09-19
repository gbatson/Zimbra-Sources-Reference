/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
package org.jivesoftware.wildfire.audit;

import org.jivesoftware.wildfire.Session;
import java.util.Date;

/**
 * Defines the known event types used with audits on arbitrary
 * data/events.
 *
 * @author Iain Shigeoka
 */
public class AuditEvent {

    /**
     * All user generated codes must be equal to or greater than this constant
     * to avoid clashing with Wildfire event codes.
     */
    public static final int USER_CODES = 100;

    private Session session;
    private Date time;
    private int code;
    private int reason;
    private String data;

    /**
     * Create a new audit event.
     *
     * @param eventSession the session that triggered the event or null
     *      if no session is associated with this event.
     * @param timestamp the date/time the event occured.
     * @param eventCode a code indicating the type of event that occured.
     * @param eventReason a second code indicating more details about the event type.
     * @param eventData arbitrary string data associated with the event or null.
     */
    public AuditEvent(Session eventSession, Date timestamp, int eventCode, int eventReason,
            String eventData)
    {
        this.session = eventSession;
        this.time = timestamp;
        this.code = eventCode;
        this.reason = eventReason;
        this.data = eventData;
    }

    /**
     * Obtain the primary type of event.
     *
     * @return the code indicating the event's type.
     */
    public int getCode() {
        return code;
    }

    /**
     * Set the primary type of event.
     *
     * @param code the code indicating the event's type.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Obtain the data associated with the event.
     *
     * @return the data associated with the event
     */
    public String getData() {
        return data;
    }

    /**
     * Set the data associated with the event.
     *
     * @param data the data associated with the event.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Obtain the subtype of event.
     *
     * @return the code indicating the event's subtype
     */
    public int getReason() {
        return reason;
    }

    /**
     * Set the subtype of event.
     *
     * @param reason the code indicating the event's subtype.
     */
    public void setReason(int reason) {
        this.reason = reason;
    }

    /**
     * Obtain the session associated with the event.
     *
     * @return the session associated with the event.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Set the session associated with the event.
     *
     * @param session the session associated with the event.
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Obtain the timestamp of when the event occured.
     *
     * @return the time the event occured.
     */
    public Date getTimestamp() {
        return time;
    }

    /**
     * Set the timestamp of when the event occured.
     *
     * @param time the time the event occured.
     */
    public void setTimestamp(Date time) {
        this.time = time;
    }
}