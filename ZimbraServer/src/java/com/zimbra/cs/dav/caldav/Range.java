/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.dav.caldav;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.dom4j.Element;

import com.zimbra.common.account.Key;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.dav.DavElements;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;

/**
 * draft-dusseault-caldav section 9.9.
 *
 */
public abstract class Range {
    protected long mStart;
    protected long mEnd;

    public Range(String name) {
        try {
            Account acct = Provisioning.getInstance().get(Key.AccountBy.name, name);
            if (acct != null) {
                mStart = acct.getTimeInterval(Provisioning.A_zimbraCalendarCalDavSyncStart, 0);
                mEnd = acct.getTimeInterval(Provisioning.A_zimbraCalendarCalDavSyncEnd, 0);
            }
        } catch (ServiceException se) {
        }
        long now = System.currentTimeMillis();
        if (mStart == 0)
            mStart = Long.MIN_VALUE;
        else
            mStart = now - mStart;
        if (mEnd == 0)
            mEnd = Long.MAX_VALUE;
        else
            mEnd = now + mEnd;
    }

    public Range(Element elem) {
        mStart = mEnd = 0;

        String s = elem.attributeValue(DavElements.P_START);
        if (s != null)
            mStart = parseDateWithUTCTime(s);

        s = elem.attributeValue(DavElements.P_END);
        if (s != null)
            mEnd = parseDateWithUTCTime(s);

        if (mStart == 0)
            mStart = Long.MIN_VALUE;
        if (mEnd == 0)
            mEnd = Long.MAX_VALUE;
    }

    private static long parseDateWithUTCTime(String time) {
        if (time.length() != 8 && time.length() != 16)
            return 0;
        if (!time.endsWith("Z"))
            return 0;
        TimeZone tz = TimeZone.getTimeZone("GMT");
        int year, month, date, hour, min, sec;
        int index = 0;
        year = Integer.parseInt(time.substring(index, index+4)); index+=4;
        month = Integer.parseInt(time.substring(index, index+2))-1; index+=2;
        date = Integer.parseInt(time.substring(index, index+2)); index+=2;
        hour = min = sec = 0;
        if (time.length() == 16) {
            if (time.charAt(index) == 'T') index++;
            hour = Integer.parseInt(time.substring(index, index+2)); index+=2;
            min = Integer.parseInt(time.substring(index, index+2)); index+=2;
            sec = Integer.parseInt(time.substring(index, index+2)); index+=2;
        }
        GregorianCalendar calendar = new GregorianCalendar(tz);
        calendar.clear();
        calendar.set(year, month, date, hour, min, sec);
        return calendar.getTimeInMillis();
    }

    public void intersection(Range another) {
        if (mStart < another.mStart)
            mStart = another.mStart;
        if (mEnd > another.mEnd)
            mEnd = another.mEnd;
    }

    public long getStart() {
        return mStart;
    }

    public long getEnd() {
        return mEnd;
    }

    public static class TimeRange extends Range {
        
        public TimeRange(String name) {
            super(name);
        }

        public TimeRange(Element elem) {
            super(elem);
        }
        
        public boolean matches(int mboxId, int itemId, long apptRangeStart, long apptRangeEnd) {
            // it matches if the range of the appointment is completely contained in the requested range.
            if (apptRangeStart >= mStart && apptRangeEnd <= mEnd)
                return true;
            if (mboxId == 0 || itemId == 0)
                return true;
            try {
                // check each instances and see if at least one of them overlaps with the requested range.
                Mailbox mbox = MailboxManager.getInstance().getMailboxById(mboxId);
                CalendarItem item = mbox.getCalendarItemById(new OperationContext(mbox), itemId);
                for (CalendarItem.Instance instance : item.expandInstances(mStart, mEnd, false)) {
                    if (instance.getStart() < mEnd && instance.getEnd() > mStart) {
                        return true;
                    }
                }
            } catch (ServiceException se) {
                ZimbraLog.dav.warn("error getting calendar item "+itemId+" from mailbox "+mboxId, se);
            }
            return false;
        }
    }
    
    public static class ExpandRange extends Range {

        public ExpandRange(Element elem) {
            super(elem);
        }
        
    }
}
