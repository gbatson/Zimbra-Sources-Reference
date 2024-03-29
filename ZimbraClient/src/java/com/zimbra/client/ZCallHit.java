/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.client;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.VoiceConstants;
import com.zimbra.client.event.ZModifyEvent;
import org.json.JSONException;

public class ZCallHit implements ZSearchHit {

    private String mId;
    private String mSortField;
    private long mDate;
    private long mDuration;
    private ZPhone mCaller;
    private ZPhone mRecipient;

    public ZCallHit(Element e) throws ServiceException {
        mId = "ZCallHit";
        mSortField = e.getAttribute(MailConstants.A_SORT_FIELD, null);
        mDate = e.getAttributeLong(MailConstants.A_DATE);
        mDuration = e.getAttributeLong(VoiceConstants.A_VMSG_DURATION) * 1000;
        for (Element el : e.listElements(VoiceConstants.E_CALLPARTY)) {
            String addressType = el.getAttribute(MailConstants.A_ADDRESS_TYPE, null);
            if (ZEmailAddress.EMAIL_TYPE_FROM.equals(addressType)) {
                mCaller = new ZPhone(el.getAttribute(VoiceConstants.A_PHONENUM), el.getAttribute(MailConstants.A_PERSONAL, null));
            } else {
                mRecipient = new ZPhone(el.getAttribute(VoiceConstants.A_PHONENUM), el.getAttribute(MailConstants.A_PERSONAL, null));
            }
        }
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getSortField() {
        return mSortField;
    }

    public ZPhone getCaller() { return mCaller; }

    public ZPhone getRecipient() { return mRecipient; }

    public String getDisplayCaller() { return mCaller.getDisplay(); }

    public String getDisplayRecipient() { return mRecipient.getDisplay(); }

    public long getDate() { return mDate; }

    public long getDuration() { return mDuration; }

    @Override
    public void modifyNotification(ZModifyEvent event) throws ServiceException {
        // No-op.
    }

    @Override
    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject zjo = new ZJSONObject();
        zjo.put("id", mId);
        zjo.put("sortField", mSortField);
        zjo.put("date", mDate);
        zjo.put("duration", mDuration);
        zjo.put("caller", mCaller);
        zjo.put("recipient", mRecipient);
        return zjo;
    }

    @Override
    public String toString() {
        return String.format("[ZCallHit %s]", mId);
    }

    public String dump() {
        return ZJSONObject.toString(this);
    }
}
