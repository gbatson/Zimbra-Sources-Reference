/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

package com.zimbra.cs.service.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import com.zimbra.common.util.ZimbraLog;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.MailSender;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.calendar.CalendarMailSender;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.calendar.RecurId;
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ICalTok;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ZVCalendar;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.common.util.L10nUtil;
import com.zimbra.common.util.L10nUtil.MsgKey;
import com.zimbra.soap.ZimbraSoapContext;

public class CancelCalendarItem extends CalendarRequest {

    private static final String[] TARGET_ITEM_PATH = new String[] { MailConstants.A_ID };
    protected String[] getProxiedIdPath(Element request)     { return TARGET_ITEM_PATH; }
    protected boolean checkMountpointProxy(Element request)  { return false; }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account acct = getRequestedAccount(zsc);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        
        ItemId iid = new ItemId(request.getAttribute(MailConstants.A_ID), zsc);
        if (!iid.hasSubpart())
            throw ServiceException.INVALID_REQUEST("missing invId subpart: id should be specified as \"item-inv\"", null);
        int compNum = (int) request.getAttributeLong(MailConstants.E_INVITE_COMPONENT);

        //synchronized (mbox) {
            CalendarItem calItem = mbox.getCalendarItemById(octxt, iid.getId()); 
            if (calItem == null)
                throw MailServiceException.NO_SUCH_CALITEM(iid.getId(), " for CancelCalendarItemRequest(" + iid + "," + compNum + ")");
            if (calItem.inTrash())
                throw ServiceException.INVALID_REQUEST("cannot cancel a calendar item under trash", null);

            if (false) {  // We probably don't want to bother with conflict check for a cancel request...
                // Conflict detection.  Do it only if requested by client.  (for backward compat)
                int modSeq = (int) request.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE, 0);
                int revision = (int) request.getAttributeLong(MailConstants.A_REVISION, 0);
                if (modSeq != 0 && revision != 0 &&
                    (modSeq < calItem.getModifiedSequence() || revision < calItem.getSavedSequence()))
                    throw MailServiceException.INVITE_OUT_OF_DATE(iid.toString());
            }

            Invite inv = calItem.getInvite(iid.getSubpartId(), compNum);
            if (inv == null)
                throw MailServiceException.INVITE_OUT_OF_DATE(iid.toString());

            Element recurElt = request.getOptionalElement(MailConstants.E_INSTANCE);
            if (recurElt != null) {
                TimeZoneMap tzmap = inv.getTimeZoneMap();
                Element tzElem = request.getOptionalElement(MailConstants.E_CAL_TZ);
                ICalTimeZone tz = null;
                if (tzElem != null) {
                    tz = CalendarUtils.parseTzElement(tzElem);
                    tzmap.add(tz);
                }
                RecurId recurId = CalendarUtils.parseRecurId(recurElt, tzmap);

                // trace logging
                ZimbraLog.calendar.info("<CancelCalendarItem> id=%d, folderId=%d, subject=\"%s\", UID=%s, recurId=%s",
                        calItem.getId(), calItem.getFolderId(), inv.isPublic() ? inv.getName() : "(private)",
                        calItem.getUid(), recurId.getDtZ());

                Element msgElem = request.getOptionalElement(MailConstants.E_MSG);
                cancelInstance(zsc, octxt, msgElem, acct, mbox, calItem, inv, recurId, inv.getAttendees());
            } else {
                // if recur is not set, then we're canceling the entire calendar item...

                // trace logging
                ZimbraLog.calendar.info("<CancelCalendarItem> id=%d, folderId=%d, subject=\"%s\", UID=%s",
                        calItem.getId(), calItem.getFolderId(), inv.isPublic() ? inv.getName() : "(private)",
                        calItem.getUid());

                Invite seriesInv = calItem.getDefaultInviteOrNull();
                if (seriesInv != null) {
                    if (seriesInv.getMethod().equals(ICalTok.REQUEST.toString()) ||
                        seriesInv.getMethod().equals(ICalTok.PUBLISH.toString())) {

                        if (seriesInv.isOrganizer()) {
                            // Send cancel notice to attendees who were invited to exception instances only.
                            // These attendees need to be notified separately because they aren't included in the series
                            // cancel notice.
                            List<ZAttendee> atsSeries = seriesInv.getAttendees();
                            Invite[] invs = calItem.getInvites();
                            for (Invite exceptInv : invs) {
                                if (exceptInv != seriesInv) {
                                    String mthd = exceptInv.getMethod();
                                    if (mthd.equals(ICalTok.REQUEST.toString()) || mthd.equals(ICalTok.PUBLISH.toString())) {
                                        List<ZAttendee> atsExcept = exceptInv.getAttendees();
                                        // Find exception instance attendees who aren't series attendees.
                                        List<ZAttendee> ats = CalendarUtils.getRemovedAttendees(atsExcept, atsSeries, false, acct);
                                        if (!ats.isEmpty()) {
                                            // notify ats
                                            cancelInstance(zsc, octxt, null, acct, mbox, calItem, exceptInv, exceptInv.getRecurId(), ats);
                                        }
                                    }
                                }
                            }
                        }

                        // Finally, cancel the series.
                        Element msgElem = request.getOptionalElement(MailConstants.E_MSG);
                        cancelInvite(zsc, octxt, msgElem, acct, mbox, calItem, seriesInv);
                    }
                    // disable change constraint checking since we've just successfully done a modify
                    octxt = new OperationContext(octxt).unsetChangeConstraint();
                }
            }
        //} // synchronized on mailbox
        
        Element response = getResponseElement(zsc);
        return response;
    }

    void cancelInstance(ZimbraSoapContext zsc, OperationContext octxt, Element msgElem, Account acct, Mailbox mbox,
            CalendarItem calItem, Invite inv, RecurId recurId, List<ZAttendee> toNotify) 
    throws ServiceException {
        boolean onBehalfOf = isOnBehalfOfRequest(zsc);
        Account authAcct = getAuthenticatedAccount(zsc);
        Locale locale = !onBehalfOf ? acct.getLocale() : authAcct.getLocale();
        String text = L10nUtil.getMessage(MsgKey.calendarCancelAppointmentInstance, locale);

        Invite cancelInvite = CalendarUtils.buildCancelInstanceCalendar(acct, authAcct, zsc.isUsingAdminPrivileges(), onBehalfOf, calItem, inv, text, recurId);
        CalSendData dat = new CalSendData();
        dat.mOrigId = new ItemId(mbox, inv.getMailItemId());
        dat.mReplyType = MailSender.MSGTYPE_REPLY;
        dat.mInvite = cancelInvite;

        ZVCalendar iCal = dat.mInvite.newToICalendar(true);

        // did they specify a custom <m> message?  If so, then we don't have to build one...
        if (msgElem != null) {
            String desc = ParseMimeMessage.getTextPlainContent(msgElem);
            String html = ParseMimeMessage.getTextHtmlContent(msgElem);
            iCal.addDescription(desc, html);

            MimeBodyPart[] mbps = new MimeBodyPart[1];
            mbps[0] = CalendarMailSender.makeICalIntoMimePart(iCal);

            // the <inv> element is *NOT* allowed -- we always build it manually
            // based on the params to the <CancelCalendarItem> and stick it in the 
            // mbps (additionalParts) parameter...
            dat.mMm = ParseMimeMessage.parseMimeMsgSoap(zsc, octxt, mbox, msgElem, 
                    mbps, ParseMimeMessage.NO_INV_ALLOWED_PARSER, dat);
            
        } else {
            List<Address> rcpts = CalendarMailSender.toListFromAttendees(toNotify);
            dat.mMm = CalendarMailSender.createCancelMessage(
                    acct, authAcct, zsc.isUsingAdminPrivileges(), onBehalfOf, rcpts,
                    calItem, cancelInvite, text, iCal);
        }
        
        if (!inv.isOrganizer()) {
            try {
                Address[] rcpts = dat.mMm.getAllRecipients();
                if (rcpts != null && rcpts.length > 0) {
                    throw MailServiceException.MUST_BE_ORGANIZER("CancelCalendarItem");
                }
            } catch (MessagingException e) {
                throw ServiceException.FAILURE("Checking recipients of outgoing msg ", e);
            }
        }
        
        sendCalendarCancelMessage(zsc, octxt, calItem.getFolderId(), acct, mbox, dat, true);
    }

    protected void cancelInvite(ZimbraSoapContext zsc, OperationContext octxt, Element msgElem, Account acct, Mailbox mbox, CalendarItem calItem, Invite inv)
    throws ServiceException {
        boolean onBehalfOf = isOnBehalfOfRequest(zsc);
        Account authAcct = getAuthenticatedAccount(zsc);
        Locale locale = !onBehalfOf ? acct.getLocale() : authAcct.getLocale();
        String text = L10nUtil.getMessage(MsgKey.calendarCancelAppointment, locale);

        CalSendData csd = new CalSendData();
        csd.mOrigId = new ItemId(mbox, inv.getMailItemId());
        csd.mReplyType = MailSender.MSGTYPE_REPLY;
        csd.mInvite = CalendarUtils.buildCancelInviteCalendar(acct, authAcct, zsc.isUsingAdminPrivileges(), onBehalfOf, calItem, inv, text);
        
        ZVCalendar iCal = csd.mInvite.newToICalendar(true);

        // did they specify a custom <m> message?  If so, then we don't have to build one...        
        if (msgElem != null) {
            String desc = ParseMimeMessage.getTextPlainContent(msgElem);
            String html = ParseMimeMessage.getTextHtmlContent(msgElem);
            iCal.addDescription(desc, html);

            MimeBodyPart[] mbps = new MimeBodyPart[1];
            mbps[0] = CalendarMailSender.makeICalIntoMimePart(iCal);
            
            // the <inv> element is *NOT* allowed -- we always build it manually
            // based on the params to the <CancelCalendarItem> and stick it in the 
            // mbps (additionalParts) parameter...
            csd.mMm = ParseMimeMessage.parseMimeMsgSoap(zsc, octxt, mbox, msgElem, mbps, ParseMimeMessage.NO_INV_ALLOWED_PARSER, csd);
            
        } else {
            List<Address> rcpts;
            if (inv.isOrganizer())
                rcpts = CalendarMailSender.toListFromAttendees(inv.getAttendees());
            else
                rcpts = new ArrayList<Address>(0);
            csd.mMm = CalendarMailSender.createCancelMessage(
                    acct, authAcct, zsc.isUsingAdminPrivileges(), onBehalfOf, rcpts,
                    calItem, inv, text, iCal);
        }
        
        if (!inv.isOrganizer()) {
            try {
                Address[] rcpts = csd.mMm.getAllRecipients();
                if (rcpts != null && rcpts.length > 0) {
                    throw MailServiceException.MUST_BE_ORGANIZER("CancelCalendarItem");
                }
            } catch (MessagingException e) {
                throw ServiceException.FAILURE("Checking recipients of outgoing msg ", e);
            }
        }
        
        sendCalendarCancelMessage(zsc, octxt, calItem.getFolderId(), acct, mbox, csd, true);
    }
}
