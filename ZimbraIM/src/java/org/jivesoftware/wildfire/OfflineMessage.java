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

import org.xmpp.packet.Message;
import org.dom4j.Element;

import java.util.Date;

/**
 * Subclass of Message that keeps the date when the offline message was stored in the database.
 * The creation date and the user may be used as a unique identifier of the offline message.
 *
 * @author Gaston Dombiak
 */
public class OfflineMessage extends Message {

    private Date creationDate;

    public OfflineMessage(Date creationDate, Element element) {
        super(element);
        this.creationDate = creationDate;
    }

    /**
     * Returns the date when the offline message was stored in the database.
     *
     * @return the date the offline message was stored.
     */
    public Date getCreationDate() {
        return creationDate;
    }
}
