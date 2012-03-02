/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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

package com.zimbra.cs.index;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.document.Document;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.db.DbSearch.SearchResult;
import com.zimbra.cs.imap.ImapMessage;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Message;
import com.zimbra.cs.mailbox.Note;
import com.zimbra.cs.mailbox.Task;

/**
 * @since Oct 15, 2004
 */
abstract class ZimbraQueryResultsImpl implements ZimbraQueryResults {

    static final class LRUHashMap<T, U> extends LinkedHashMap<T, U> {
        private static final long serialVersionUID = -6181398012977532525L;

        private final int mMaxSize;

        LRUHashMap(int maxSize) {
            super(maxSize, 0.75f, true);
            mMaxSize = maxSize;
        }

        LRUHashMap(int maxSize, int tableSize) {
            super(tableSize, 0.75f, true);
            mMaxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<T, U> eldest) {
            return size() > mMaxSize;
        }
    }

    private static final int MAX_LRU_ENTRIES = 2048;
    private static final int INITIAL_TABLE_SIZE = 100;

    private Map<Integer, ConversationHit> mConversationHits;
    private Map<Integer, MessageHit> mMessageHits;
    private Map<String, MessagePartHit> mPartHits;
    private Map<Integer, ContactHit> mContactHits;
    private Map<Integer, NoteHit>  mNoteHits;
    private Map<Integer, CalendarItemHit> mCalItemHits;

    private byte[] mTypes;
    private SortBy mSearchOrder;
    private Mailbox.SearchResultMode mMode;

    ZimbraQueryResultsImpl(byte[] types, SortBy searchOrder, Mailbox.SearchResultMode mode) {
        mTypes = types;
        mMode = mode;
        mSearchOrder = searchOrder;

        mConversationHits = new LRUHashMap<Integer, ConversationHit>(MAX_LRU_ENTRIES, INITIAL_TABLE_SIZE);
        mMessageHits = new LRUHashMap<Integer, MessageHit>(MAX_LRU_ENTRIES, INITIAL_TABLE_SIZE);
        mPartHits = new LRUHashMap<String, MessagePartHit>(MAX_LRU_ENTRIES, INITIAL_TABLE_SIZE);
        mContactHits = new LRUHashMap<Integer, ContactHit>(MAX_LRU_ENTRIES, INITIAL_TABLE_SIZE);
        mNoteHits = new LRUHashMap<Integer, NoteHit>(MAX_LRU_ENTRIES, INITIAL_TABLE_SIZE);
        mCalItemHits = new LRUHashMap<Integer, CalendarItemHit>(MAX_LRU_ENTRIES, INITIAL_TABLE_SIZE);
    };

    @Override
    public abstract void doneWithSearchResults() throws ServiceException;

    @Override
    public abstract ZimbraHit skipToHit(int hitNo) throws ServiceException;

    @Override
    public boolean hasNext() throws ServiceException {
        return (peekNext() != null);
    }

    @Override
    public ZimbraHit getFirstHit() throws ServiceException {
        resetIterator();
        return getNext();
    }

    @Override
    public SortBy getSortBy() {
        return mSearchOrder;
    }

    byte[] getTypes() {
        return mTypes;
    }

    public Mailbox.SearchResultMode getSearchMode() {
        return mMode;
    }

    protected ConversationHit getConversationHit(Mailbox mbx, int convId) {
        ConversationHit ch = mConversationHits.get(convId);
        if (ch == null) {
            ch = new ConversationHit(this, mbx, convId);
            mConversationHits.put(convId, ch);
        }
        return ch;
    }

    protected ContactHit getContactHit(Mailbox mbx, int mailItemId, Contact contact) {
        ContactHit hit = mContactHits.get(mailItemId);
        if (hit == null) {
            hit = new ContactHit(this, mbx, mailItemId, contact);
            mContactHits.put(mailItemId, hit);
        }
        return hit;
    }

    protected NoteHit getNoteHit(Mailbox mbx, int mailItemId, Note note) {
        NoteHit hit = mNoteHits.get(mailItemId);
        if (hit == null) {
            hit = new NoteHit(this, mbx, mailItemId, note);
            mNoteHits.put(mailItemId, hit);
        }
        return hit;
    }

    protected CalendarItemHit getAppointmentHit(Mailbox mbx, int mailItemId, CalendarItem cal) {
        CalendarItemHit hit = mCalItemHits.get(mailItemId);
        if (hit == null) {
            hit = new CalendarItemHit(this, mbx, mailItemId, cal);
            mCalItemHits.put(mailItemId, hit);
        }
        return hit;
    }

    protected CalendarItemHit getTaskHit(Mailbox mbx, int mailItemId, Task task) {
        CalendarItemHit hit = mCalItemHits.get(mailItemId);
        if (hit == null) {
            hit = new TaskHit(this, mbx, mailItemId, task);
            mCalItemHits.put(mailItemId, hit);
        }
        return hit;
    }

    protected MessageHit getMessageHit(Mailbox mbx, int mailItemId, Document doc, Message message) {
        MessageHit hit = mMessageHits.get(mailItemId);
        if (hit == null) {
            hit = new MessageHit(this, mbx, mailItemId, doc, message);
            mMessageHits.put(mailItemId, hit);
        }
        return hit;
    }

    protected MessagePartHit getMessagePartHit(Mailbox mbx, int mailItemId, Document doc, Message message) {
        String partKey = Integer.toString(mailItemId) + "-" + doc.get(LuceneFields.L_PARTNAME);
        MessagePartHit hit = mPartHits.get(partKey);
        if (hit == null) {
            hit = new MessagePartHit(this, mbx, mailItemId, doc, message);
            mPartHits.put(partKey, hit);
        }
        return hit;
    }

    /**
     * @param type
     * @return
     *          TRUE if this type of SearchResult should be added multiple times if there are multiple
     *          hits (e.g. if multiple document parts match) -- currently only true for MessageParts,
     *          false for all other kinds of result
     */
    static final boolean shouldAddDuplicateHits(byte type) {
        return (type == MailItem.TYPE_CHAT || type == MailItem.TYPE_MESSAGE);
    }

    /**
     * We've got a mailbox, a DBMailItem.SearchResult and (optionally) a Lucene Doc...
     * that's everything we need to build a real ZimbraHit.
     *
     * @param doc - Optional, only set if this search had a Lucene part
     */
    ZimbraHit getZimbraHit(Mailbox mbox, SearchResult sr, Document doc, SearchResult.ExtraData extra) {
        MailItem item = null;
        ImapMessage i4msg = null;
        int modseq = -1, parentId = 0;
        switch (extra) {
            case MAIL_ITEM:
                item = (MailItem) sr.extraData;
                break;
            case IMAP_MSG:
                i4msg = (ImapMessage) sr.extraData;
                break;
            case MODSEQ:
                modseq = sr.extraData != null ? (Integer) sr.extraData : -1;
                break;
            case PARENT:
                parentId = sr.extraData != null ? (Integer) sr.extraData : 0;
                break;
        }

        ZimbraHit toRet = null;
        switch (sr.type) {
            case MailItem.TYPE_CHAT:
            case MailItem.TYPE_MESSAGE:
                if (doc != null) {
                    toRet = getMessagePartHit(mbox, sr.id, doc, (Message) item);
                } else {
                    toRet = getMessageHit(mbox, sr.id, null, (Message) item);
                }
                toRet.cacheSortField(getSortBy(), sr.sortkey);
                break;
            case MailItem.TYPE_CONTACT:
                toRet = getContactHit(mbox, sr.id, (Contact) item);
                break;
            case MailItem.TYPE_NOTE:
                toRet = getNoteHit(mbox, sr.id, (Note) item);
                break;
            case MailItem.TYPE_APPOINTMENT:
                toRet = getAppointmentHit(mbox, sr.id, (CalendarItem) item);
                break;
            case MailItem.TYPE_TASK:
                toRet = getTaskHit(mbox, sr.id, (Task) item);
                break;
            case MailItem.TYPE_DOCUMENT:
            case MailItem.TYPE_WIKI:
                toRet = getDocumentHit(mbox, sr.id, doc, (com.zimbra.cs.mailbox.Document) item);
                break;
            default:
                assert(false);
        }

        if (i4msg != null) {
            toRet.cacheImapMessage(i4msg);
        }
        if (modseq > 0) {
            toRet.cacheModifiedSequence(modseq);
        }
        if (parentId != 0) {
            toRet.cacheParentId(parentId);

        }
        return toRet;
    }

    protected DocumentHit getDocumentHit(Mailbox mbx, int mailItemId, Document luceneDoc,
            com.zimbra.cs.mailbox.Document docItem) {
        return new DocumentHit(this, mbx, mailItemId, luceneDoc, docItem);
    }
}
