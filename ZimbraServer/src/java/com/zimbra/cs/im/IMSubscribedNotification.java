/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
package com.zimbra.cs.im;

import java.util.Collection;
import java.util.List;

import org.xmpp.packet.Roster;

import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.soap.Element;

class IMSubscribedNotification extends IMNotification {
    IMAddr mAddr;
    String mName;
    String[] mGroups;
    boolean mSubscribed; // isTo (i'm subscribed TO that remote entity)
    boolean mSubscribedFrom; 
    Roster.Ask mAsk;

    static IMSubscribedNotification create(IMAddr address, String name, List<IMGroup> groups, 
                                           boolean subscribedTo, boolean subscribedFrom, Roster.Ask ask) {
        String[] str = new String[groups.size()];
        int i = 0;
        for (IMGroup grp : groups) 
            str[i++] = grp.getName();

        return new IMSubscribedNotification(address, name, str, subscribedTo, subscribedFrom, ask);
    }
    
    static IMSubscribedNotification create(IMAddr address, String name, Collection<String> groups, 
                                           boolean subscribedTo, boolean subscribedFrom, Roster.Ask ask) {
        String[] str = new String[groups.size()];
        int i = 0;
        for (String s : groups) 
            str[i++] = s;

        return new IMSubscribedNotification(address, name, str, subscribedTo, subscribedFrom, ask);
    }
    
    static IMSubscribedNotification create(IMAddr address, String name, String[] groups, 
                                           boolean subscribedTo, boolean subscribedFrom, Roster.Ask ask) {
        return new IMSubscribedNotification(address, name, groups, subscribedTo, subscribedFrom, ask);
    }
    
    static IMSubscribedNotification create(IMAddr address, String name, 
                                           boolean subscribedTo, boolean subscribedFrom, Roster.Ask ask) {
        return new IMSubscribedNotification(address, name, null, subscribedTo, subscribedFrom, ask);
    }
    
    private IMSubscribedNotification(IMAddr address, String name, String[] groups, 
                                     boolean subscribedTo, boolean subscribedFrom, Roster.Ask ask) {
        mAddr = address;
        mName = name;
        mGroups = groups;
        mSubscribed = subscribedTo;
        mSubscribedFrom = subscribedFrom;
        mAsk = ask;
    }
    
    public boolean isSubscribedTo() { return mSubscribed; }
    public boolean isSubscribedFrom() { return mSubscribedFrom; }
    
    public Element toXml(Element parent) {
        ZimbraLog.im.info("IMSubscribedNotification " + mAddr + " " + mName + " Subscribed=" +mSubscribed
                    + (mAsk != null ? " Ask="+mAsk.toString() : ""));
        
        Element e;
        if (mSubscribed) { 
            e = create(parent, IMConstants.E_SUBSCRIBED);
        } else {
            e = create(parent, IMConstants.E_UNSUBSCRIBED);
        }
        e.addAttribute(IMConstants.A_NAME, mName);

        // don't send ask="unsubscribe"...looks like the client gets confused by this,
        // and frankly it shouldn't care if the other user has responded to our
        // unsub request or not...
        if (mAsk != null && mAsk!=Roster.Ask.unsubscribe) {
            e.addAttribute(IMConstants.A_ASK, mAsk.name());
        }
        
        if (mGroups != null) {
            e.addAttribute(IMConstants.A_GROUPS, StringUtil.join(",", mGroups));
        }
        
        e.addAttribute(IMConstants.A_TO, mAddr.getAddr());

        return e;
    }
}