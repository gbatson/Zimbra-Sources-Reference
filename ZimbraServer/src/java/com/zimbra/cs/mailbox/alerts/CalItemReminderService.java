/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.mailbox.alerts;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxListener;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.ScheduledTaskManager;
import com.zimbra.cs.mailbox.calendar.Alarm;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.session.PendingModifications;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author vmahajan
 */
public class CalItemReminderService extends MailboxListener {

    public void handleMailboxChange(String accountId, PendingModifications mods, OperationContext octxt, int lastChangeId) {
        Account account = null;
        try {
            account = Provisioning.getInstance().getAccountById(accountId);
        } catch (ServiceException e) {
            ZimbraLog.scheduler.error("Error in getting account", e);
        }
        if (account == null) {
            ZimbraLog.scheduler.error("Account not found for id %s", accountId);
            return;
        }
        if (mods.created != null) {
            for (Map.Entry<PendingModifications.ModificationKey, MailItem> entry : mods.created.entrySet()) {
                MailItem item = entry.getValue();
                if (item instanceof CalendarItem) {
                    if (ZimbraLog.scheduler.isDebugEnabled())
                        ZimbraLog.scheduler.debug("Handling creation of calendar item (id=%s,mailboxId=%s)", item.getId(), item.getMailboxId());
                    scheduleNextReminders((CalendarItem) item);
                }
            }
        }
        if (mods.modified != null) {
            for (Map.Entry<PendingModifications.ModificationKey, PendingModifications.Change> entry : mods.modified.entrySet()) {
                PendingModifications.Change change = entry.getValue();
                if (change.what instanceof CalendarItem) {
                    CalendarItem calItem = (CalendarItem) change.what;
                    if (ZimbraLog.scheduler.isDebugEnabled())
                        ZimbraLog.scheduler.debug("Handling modification of calendar item (id=%s,mailboxId=%s)", calItem.getId(), calItem.getMailboxId());
                    boolean calItemCanceled = false;
                    try {
                        if ((change.why & PendingModifications.Change.MODIFIED_FOLDER) != 0 && calItem.inTrash()) {
                            calItemCanceled = true;
                        }
                    } catch (ServiceException e) {
                        ZimbraLog.scheduler.error("Error in fetching calendar item's folder", e);
                    }
                    // cancel any existing reminders and schedule new ones if cal item not canceled
                    if (cancelExistingReminders(calItem) && !calItemCanceled)
                        scheduleNextReminders(calItem);
                }
            }
        }
        if (mods.deleted != null) {
            for (Map.Entry<PendingModifications.ModificationKey, Object> entry : mods.deleted.entrySet()) {
                Object deletedObj = entry.getValue();
                if (deletedObj instanceof CalendarItem) {
                    CalendarItem calItem = (CalendarItem) deletedObj;
                    if (ZimbraLog.scheduler.isDebugEnabled())
                        ZimbraLog.scheduler.debug("Handling deletion of calendar item (id=%s,mailboxId=%s)", calItem.getId(), calItem.getMailboxId());
                    cancelExistingReminders(calItem);
                } else if (deletedObj instanceof Integer) {
                    // We only have item id
                    Mailbox mbox = null;
                    try {
                        mbox = MailboxManager.getInstance().getMailboxByAccountId(accountId, MailboxManager.FetchMode.DO_NOT_AUTOCREATE);
                    } catch (ServiceException e) {
                        ZimbraLog.scheduler.error("Error looking up the mailbox of account %s", accountId, e);
                    }
                    if (mbox != null) {
                        cancelExistingReminders((Integer) deletedObj, mbox.getId());
                    }
                }
            }
        }
    }

    /**
     * Cancels email reminders for the calendar item.
     *
     * @param calItem
     * @return true if no error was encountered during cancellation
     */
    static boolean cancelExistingReminders(CalendarItem calItem) {
        return cancelExistingReminders(calItem.getId(), calItem.getMailboxId());
    }

    /**
     * Cancels existing reminders for the calendar item.
     *
     * @param calItemId
     * @param mailboxId
     * @return true if no error was encountered during cancellation
     */
    static boolean cancelExistingReminders(int calItemId, int mailboxId) {
        try {
            ScheduledTaskManager.cancel(CalItemEmailReminderTask.class.getName(),
                                        CalItemEmailReminderTask.TASK_NAME_PREFIX + Integer.toString(calItemId),
                                        mailboxId,
                                        false);
            ScheduledTaskManager.cancel(CalItemSmsReminderTask.class.getName(),
                                        CalItemSmsReminderTask.TASK_NAME_PREFIX + Integer.toString(calItemId),
                                        mailboxId,
                                        false);
        } catch (ServiceException e) {
            ZimbraLog.scheduler.warn("Canceling reminder tasks failed", e);
            return false;
        }
        return true;
    }

    /**
     * Schedules next reminders for the calendar item.
     *
     * @param calItem
     */
    static void scheduleNextReminders(CalendarItem calItem) {
        try {
            CalendarItem.AlarmData alarmData = calItem.getNextEmailAlarm();
            if (alarmData == null)
                return;
            boolean sendEmail = true;
            boolean sendSms = false;
            Alarm emailAlarm = alarmData.getAlarm();
            List<ZAttendee> recipients = emailAlarm.getAttendees();
            if (recipients != null && !recipients.isEmpty()) {
                sendEmail = false;
                Account acct = calItem.getAccount();
                String defaultEmailAddress = acct.getAttr(Provisioning.A_zimbraPrefCalendarReminderEmail);
                String defaultDeviceAddress = acct.getAttr(Provisioning.A_zimbraCalendarReminderDeviceEmail);
                for (ZAttendee recipient : recipients) {
                    if (recipient.getAddress().equals(defaultEmailAddress))
                        sendEmail = true;
                    if (recipient.getAddress().equals(defaultDeviceAddress))
                        sendSms = true;
                }
            }
            if (sendEmail)
                scheduleReminder(new CalItemEmailReminderTask(), calItem, alarmData);
            if (sendSms)
                scheduleReminder(new CalItemSmsReminderTask(), calItem, alarmData);
        } catch (ServiceException e) {
            ZimbraLog.scheduler.error("Error in scheduling reminder task", e);
        }
    }

    private static void scheduleReminder(
            CalItemReminderTaskBase reminderTask, CalendarItem calItem, CalendarItem.AlarmData alarmData)
            throws ServiceException {
        reminderTask.setMailboxId(calItem.getMailboxId());
        reminderTask.setExecTime(new Date(alarmData.getNextAt()));
        reminderTask.setProperty(CalItemReminderTaskBase.CAL_ITEM_ID_PROP_NAME, Integer.toString(calItem.getId()));
        reminderTask.setProperty(CalItemReminderTaskBase.INV_ID_PROP_NAME, Integer.toString(alarmData.getInvId()));
        reminderTask.setProperty(CalItemReminderTaskBase.COMP_NUM_PROP_NAME, Integer.toString(alarmData.getCompNum()));
        reminderTask.setProperty(CalItemReminderTaskBase.NEXT_INST_START_PROP_NAME, Long.toString(alarmData.getNextInstanceStart()));
        ScheduledTaskManager.schedule(reminderTask);
    }

    public int registerForItemTypes() {
        return MailItem.typeToBitmask(MailItem.TYPE_APPOINTMENT) | MailItem.typeToBitmask(MailItem.TYPE_TASK);
    }
}
