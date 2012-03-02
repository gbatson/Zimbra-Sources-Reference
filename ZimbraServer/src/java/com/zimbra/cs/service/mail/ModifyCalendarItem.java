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
import java.util.Map;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.service.util.ItemIdFormatter;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.soap.ZimbraSoapContext;


public class ModifyCalendarItem extends CalendarRequest {

    // very simple: generate a new UID and send a REQUEST
    protected class ModifyCalendarItemParser extends ParseMimeMessage.InviteParser {
        private Invite mInv;
        private Invite mSeriesInv;
        private List<ZAttendee> mAttendeesAdded;
        private List<ZAttendee> mAttendeesCanceled;
        
        ModifyCalendarItemParser(Invite inv, Invite seriesInv) {
            mInv = inv;
            mSeriesInv = seriesInv;
            mAttendeesAdded = new ArrayList<ZAttendee>();
            mAttendeesCanceled = new ArrayList<ZAttendee>();
        }

        public List<ZAttendee> getAttendeesAdded() { return mAttendeesAdded; }
        public List<ZAttendee> getAttendeesCanceled() { return mAttendeesCanceled; }

        public ParseMimeMessage.InviteParserResult parseInviteElement(
                ZimbraSoapContext lc, OperationContext octxt, Account account, Element inviteElem)
        throws ServiceException {
            ParseMimeMessage.InviteParserResult toRet = CalendarUtils.parseInviteForModify(
                    account, getItemType(), inviteElem, mInv, mSeriesInv,
                    mAttendeesAdded, mAttendeesCanceled, !mInv.hasRecurId());
            return toRet;
        }
    };

    
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account acct = getRequestedAccount(zsc);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);

        // proxy handling

        Element msgElem = request.getElement(MailConstants.E_MSG);
        String folderStr = msgElem.getAttribute(MailConstants.A_FOLDER, null);
        ItemId iid = new ItemId(request.getAttribute(MailConstants.A_ID), zsc);
        if (!iid.belongsTo(acct)) {
            // Proxy it.
            if (folderStr != null) {
                // make sure that the folder ID is fully qualified
                ItemId folderFQ = new ItemId(folderStr, zsc);
                msgElem.addAttribute(MailConstants.A_FOLDER, folderFQ.toString());
            }
            return proxyRequest(request, context, iid.getAccountId());
        }

        // Check if moving to a different mailbox.
        boolean isInterMboxMove = false;
        ItemId iidFolder = null;
        if (folderStr != null) {
            iidFolder = new ItemId(folderStr, zsc);
            isInterMboxMove = !iidFolder.belongsTo(mbox);
        }
        
        Element response = getResponseElement(zsc);
        int compNum = (int) request.getAttributeLong(MailConstants.A_CAL_COMP, 0);
        synchronized(mbox) {
            CalendarItem calItem = mbox.getCalendarItemById(octxt, iid.getId());
            if (calItem == null) {
                throw MailServiceException.NO_SUCH_CALITEM(iid.toString(), "Could not find calendar item");
            }

            // Reject the request if calendar item is under trash or is being moved to trash.
            if (calItem.inTrash())
                throw ServiceException.INVALID_REQUEST("cannot modify a calendar item under trash", null);
            if (!isInterMboxMove && iidFolder != null) {
                if (iidFolder.getId() != calItem.getFolderId()) {
                    Folder destFolder = mbox.getFolderById(octxt, iidFolder.getId());
                    if (destFolder.inTrash())
                        throw ServiceException.INVALID_REQUEST("cannot combine with a move to trash", null);
                }
            }

            // Conflict detection.  Do it only if requested by client.  (for backward compat)
            int modSeq = (int) request.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE, 0);
            int revision = (int) request.getAttributeLong(MailConstants.A_REVISION, 0);
            if (modSeq != 0 && revision != 0 &&
                (modSeq < calItem.getModifiedSequence() || revision < calItem.getSavedSequence()))
                throw MailServiceException.INVITE_OUT_OF_DATE(iid.toString());

            Invite inv = calItem.getInvite(iid.getSubpartId(), compNum);
            if (inv == null) {
                throw MailServiceException.INVITE_OUT_OF_DATE(iid.toString());
            }
            Invite seriesInv = calItem.getDefaultInviteOrNull();
            int folderId = calItem.getFolderId();
            if (!isInterMboxMove && iidFolder != null)
                folderId = iidFolder.getId();
            modifyCalendarItem(zsc, octxt, request, acct, mbox, folderId, calItem, inv, seriesInv,
                               response, isInterMboxMove);
        }

        // Inter-mailbox move if necessary.
        if (isInterMboxMove) {
            CalendarItem calItem = mbox.getCalendarItemById(octxt, iid.getId());
            List<Integer> ids = new ArrayList<Integer>(1);
            ids.add(calItem.getId());
            ItemActionHelper.MOVE(octxt, mbox, zsc.getResponseProtocol(), ids, calItem.getType(), null, iidFolder);
        }

        return response;
    }

    private Element modifyCalendarItem(
            ZimbraSoapContext zsc, OperationContext octxt, Element request,
            Account acct, Mailbox mbox, int folderId,
            CalendarItem calItem, Invite inv, Invite seriesInv, Element response, boolean isInterMboxMove)
    throws ServiceException {
        // <M>
        Element msgElem = request.getElement(MailConstants.E_MSG);
        
        ModifyCalendarItemParser parser = new ModifyCalendarItemParser(inv, seriesInv);
        
        CalSendData dat = handleMsgElement(zsc, octxt, msgElem, acct, mbox, parser);
        dat.mDontNotifyAttendees = isInterMboxMove;

        if (!dat.mInvite.hasRecurId())
            ZimbraLog.calendar.info("<ModifyCalendarItem> id=%d, folderId=%d, subject=\"%s\", UID=%s",
                    calItem.getId(), folderId, dat.mInvite.isPublic() ? dat.mInvite.getName() : "(private)",
                    dat.mInvite.getUid());
        else
            ZimbraLog.calendar.info("<ModifyCalendarItem> id=%d, folderId=%d, subject=\"%s\", UID=%s, recurId=%s",
                    calItem.getId(), folderId, dat.mInvite.isPublic() ? dat.mInvite.getName() : "(private)",
                    dat.mInvite.getUid(), dat.mInvite.getRecurId().getDtZ());

        boolean hasRecipients;
        try {
            Address[] rcpts = dat.mMm.getAllRecipients();
            hasRecipients = rcpts != null && rcpts.length > 0;
        } catch (MessagingException e) {
            throw ServiceException.FAILURE("Checking recipients of outgoing msg ", e);
        }
        // If we are sending this to other people, then we MUST be the organizer!
        if (!dat.mInvite.isOrganizer() && hasRecipients)
            throw MailServiceException.MUST_BE_ORGANIZER("ModifyCalendarItem");

        if (!dat.mInvite.isOrganizer()) {
            // neverSent is always false for attendee users.
            dat.mInvite.setNeverSent(false);
        } else if (!dat.mInvite.hasOtherAttendees()) {
            // neverSent is always false for appointments without attendees.
            dat.mInvite.setNeverSent(false);
        } else if (hasRecipients) {
            // neverSent is set to false when attendees are notified.
            dat.mInvite.setNeverSent(false);
        } else {
            // This is the case of organizer saving an invite with attendees, but without sending the notification.
            assert(dat.mInvite.hasOtherAttendees() && !hasRecipients);
            if (!inv.hasOtherAttendees()) {
                // Special case of going from a personal appointment (no attendees) to a draft appointment with
                // attendees.  neverSent was false for being a personal appointment, so we need to explicitly set it to true.
                // This case is essentially identical to creating a new appointment with attendees without notification.
                dat.mInvite.setNeverSent(true);
            } else {
                // Set neverSent to false, but only if it wasn't already set to true before.
                // !inv.isNeverSent() ? false : true ==> inv.isNeverSent()
                dat.mInvite.setNeverSent(inv.isNeverSent());
            }
        }

        // If updating recurrence series, remove newly added attendees from the email
        // sent to existing attendees.  The new attendees are notified in a separate email.
        List<ZAttendee> atsAdded = parser.getAttendeesAdded();
        if (!atsAdded.isEmpty() && inv.isRecurrence() && inv.isOrganizer()) {
            try {
                RecipientType rcptTypes[] = { RecipientType.TO, RecipientType.CC, RecipientType.BCC };
                for (RecipientType rcptType : rcptTypes) {
                    Address[] rcpts = dat.mMm.getRecipients(rcptType);
                    List<Address> filtered = new ArrayList<Address>();
                    boolean foundDuplicate = false;
                    if (rcpts != null && rcpts.length > 0) {
                        for (Address rcpt : rcpts) {
                            if (rcpt instanceof InternetAddress) {
                                String email = ((InternetAddress) rcpt).getAddress();
                                if (email != null) {
                                    boolean keep = true;
                                    for (ZAttendee at : atsAdded) {
                                        if (email.equalsIgnoreCase(at.getAddress())) {
                                            keep = false;
                                            foundDuplicate = true;
                                            break;
                                        }
                                    }
                                    if (keep)
                                        filtered.add(rcpt);
                                }
                            }
                        }
                    }
                    if (foundDuplicate) {
                        Address[] newRcpts = filtered.toArray(new Address[0]);
                        dat.mMm.setRecipients(rcptType, newRcpts);
                    }
                }
            } catch (MessagingException e) {
                throw ServiceException.FAILURE("Checking recipients of outgoing msg ", e);
            }
        }

        // Apply the change and notify existing attendees.
        sendCalendarMessage(zsc, octxt, folderId, acct, mbox, dat, response, true);
        boolean echo = request.getAttributeBool(MailConstants.A_CAL_ECHO, false);
        if (echo && dat.mAddInvData != null) {
            ItemIdFormatter ifmt = new ItemIdFormatter(zsc);
            int maxSize = (int) request.getAttributeLong(MailConstants.A_MAX_INLINED_LENGTH, 0);
            boolean wantHTML = request.getAttributeBool(MailConstants.A_WANT_HTML, false);
            boolean neuter = request.getAttributeBool(MailConstants.A_NEUTER, true);
            echoAddedInvite(response, ifmt, octxt, mbox, dat.mAddInvData, maxSize, wantHTML, neuter);
        }

        if (!inv.isNeverSent()) {
            // Notify removed attendees.
            List<ZAttendee> atsCanceled = parser.getAttendeesCanceled();
            if (!atsCanceled.isEmpty() && inv.isOrganizer()) {
                updateRemovedInvitees(zsc, octxt, acct, mbox, inv.getCalendarItem(), inv, atsCanceled);
            }
        }

        // Notify attendees added to recurrence series.
        if (!atsAdded.isEmpty() && inv.isRecurrence() && inv.isOrganizer()) {
            updateAddedInvitees(zsc, octxt, acct, mbox, inv.getCalendarItem(), inv, atsAdded);
        }

        return response;
    }
}
