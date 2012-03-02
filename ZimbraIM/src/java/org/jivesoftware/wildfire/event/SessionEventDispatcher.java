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
package org.jivesoftware.wildfire.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.wildfire.Session;
import org.jivesoftware.util.Log;

/**
 * Dispatches session events. Each event has a {@link EventType type}
 *
 * @author Matt Tucker
 */
public class SessionEventDispatcher {

    private static List<SessionEventListener> listeners =
            new CopyOnWriteArrayList<SessionEventListener>();

    private SessionEventDispatcher() {
        // Not instantiable.
    }

    /**
     * Registers a listener to receive events.
     *
     * @param listener the listener.
     */
    public static void addListener(SessionEventListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        listeners.add(listener);
    }

    /**
     * Unregisters a listener to receive events.
     *
     * @param listener the listener.
     */
    public static void removeListener(SessionEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Dispatches an event to all listeners.
     *
     * @param session the session.
     * @param eventType the event type.
     */
    public static void dispatchEvent(Session session, EventType eventType) {
        for (SessionEventListener listener : listeners) {
            try {
                switch (eventType) {
                    case session_created: {
                        listener.sessionCreated(session);
                        break;
                    }
                    case session_destroyed: {
                        listener.sessionDestroyed(session);
                        break;
                    }
                    case anonymous_session_created: {
                      listener.anonymousSessionCreated(session);
                      break;
                    }
                    case anonymous_session_destroyed: {
                      listener.anonymousSessionDestroyed(session);
                      break;
                    }
                   
                    default:
                        break;
                }
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
    }

    /**
     * Represents valid event types.
     */
    public enum EventType {

        /**
         * A session was created.
         */
        session_created,

        /**
         * A session was destroyed
         */
        session_destroyed,
        
        /**
         * An anonymous session was created.
         */
        anonymous_session_created,

        /**
         * A anonymous session was destroyed
         */
        anonymous_session_destroyed,
                
    }
}