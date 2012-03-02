/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011 Zimbra, Inc.
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
package com.zimbra.cs.imap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimerTask;

import com.google.common.base.Function;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.imap.ImapHandler.ImapExtension;
import com.zimbra.cs.index.SearchParams;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.index.ZimbraHit;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.localconfig.DebugConfig;
import com.zimbra.cs.mailbox.Flag;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.SearchFolder;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.session.Session;
import com.zimbra.cs.util.Zimbra;

final class ImapSessionManager {
    private static final long SERIALIZER_INTERVAL_MSEC =
        DebugConfig.imapSessionSerializerFrequency * Constants.MILLIS_PER_SECOND;
    private static final long SESSION_INACTIVITY_SERIALIZATION_TIME =
        DebugConfig.imapSessionInactivitySerializationTime * Constants.MILLIS_PER_SECOND;
    private static final int TOTAL_SESSION_FOOTPRINT_LIMIT = DebugConfig.imapTotalNonserializedSessionFootprintLimit;
    private static final int MAX_NONINTERACTIVE_SESSIONS = DebugConfig.imapNoninteractiveSessionLimit;
    private static final boolean CONSISTENCY_CHECK = DebugConfig.imapCacheConsistencyCheck;

    private static final boolean TERMINATE_ON_CLOSE = DebugConfig.imapTerminateSessionOnClose;
    private static final boolean SERIALIZE_ON_CLOSE = DebugConfig.imapSerializeSessionOnClose;

    private final LinkedHashMap<ImapSession, Object> sessions = new LinkedHashMap<ImapSession, Object>(128, 0.75F, true);
    private static final ImapSessionManager SINGLETON = new ImapSessionManager();

    private ImapSessionManager() {
        if (SERIALIZER_INTERVAL_MSEC > 0) {
            Zimbra.sTimer.schedule(new SessionSerializerTask(), SERIALIZER_INTERVAL_MSEC, SERIALIZER_INTERVAL_MSEC);
            ZimbraLog.imap.debug("initializing IMAP session serializer task");
        }
    }

    static ImapSessionManager getInstance() {
        return SINGLETON;
    }

    void recordAccess(ImapSession session) {
        synchronized (sessions) {
            // LinkedHashMap bumps to beginning of iterator order on access
            sessions.get(session);
        }
    }

    void uncacheSession(ImapSession session) {
        synchronized (sessions) {
            sessions.remove(session);
        }
    }

    /**
     * <ol>
     *  <li>deserialize/reserialize sessions with notification overflow
     *  <li>serialize enough sessions to get under the max memory footprint
     *  <li>prune noninteractive sessions beyond a specified count
     *  <li>maybe checkpoint a few "dirty" sessions if we're not doing anything else?
     * </ol>
     */
    final class SessionSerializerTask extends TimerTask {

        @Override
        public void run() {
            ZimbraLog.imap.debug("running IMAP session serializer task");

            long cutoff = SESSION_INACTIVITY_SERIALIZATION_TIME > 0 ?
                    System.currentTimeMillis() - SESSION_INACTIVITY_SERIALIZATION_TIME : Long.MIN_VALUE;

            List<ImapSession> overflow = new ArrayList<ImapSession>();
            List<ImapSession> pageable = new ArrayList<ImapSession>();
            List<ImapSession> droppable = new ArrayList<ImapSession>();

            synchronized (sessions) {
                // first, figure out the set of sessions that'll need to be brought into memory and reserialized
                int footprint = 0, maxOverflow = 0, noninteractive = 0;
                for (ImapSession session : sessions.keySet()) {
                    if (session.requiresReload()) {
                        overflow.add(session);
                        // note that these will add to the memory footprint temporarily, so need the largest size...
                        maxOverflow = Math.max(maxOverflow, session.getEstimatedSize());
                    }
                }
                footprint += Math.min(maxOverflow, TOTAL_SESSION_FOOTPRINT_LIMIT - 1000);

                // next, get the set of in-memory sessions that need to get serialized out
                for (ImapSession session : sessions.keySet()) {
                    int size = session.getEstimatedSize();
                    // want to serialize enough sessions to get below the memory threshold
                    // also going to serialize anything that's been idle for a while
                    if (!session.isInteractive() && ++noninteractive > MAX_NONINTERACTIVE_SESSIONS) {
                        droppable.add(session);
                    } else if (!session.isSerialized() && session.getLastAccessTime() < cutoff) {
                        pageable.add(session);
                    } else if (footprint + size > TOTAL_SESSION_FOOTPRINT_LIMIT) {
                        pageable.add(session);
                    } else {
                        footprint += size;
                    }
                }
            }

            for (ImapSession session : pageable) {
                try {
                    ZimbraLog.imap.debug("paging out session due to staleness or total memory footprint: %s (sid %s)",
                            session.getPath(), session.getSessionId());
                    session.unload();
                } catch (Exception e) {
                    ZimbraLog.imap.warn("error serializing session; clearing", e);
                    // XXX: make sure this doesn't result in a loop
                    quietRemoveSession(session);
                }
            }

            for (ImapSession session : overflow) {
                try {
                    ZimbraLog.imap.debug("loading/unloading paged session due to queued notification overflow: %s (sid %s)",
                            session.getPath(), session.getSessionId());
                    session.reload();
                    session.unload();
                } catch (Exception e) {
                    ZimbraLog.imap.warn("error deserializing overflowed session; clearing", e);
                    // XXX: make sure this doesn't result in a loop
                    quietRemoveSession(session);
                }
            }

            for (ImapSession session : droppable) {
                ZimbraLog.imap.debug("removing session due to having too many noninteractive sessions: %s (sid %s)",
                        session.getPath(), session.getSessionId());
                // only noninteractive sessions get added to droppable list, so this next conditional should never be true
                quietRemoveSession(session);
            }
        }

        private void quietRemoveSession(ImapSession session) {
            // XXX: make sure this doesn't result in a loop
            try {
                if (session.isInteractive()) {
                    session.cleanup();
                }
                session.detach();
            } catch (Exception e) {
                ZimbraLog.imap.warn("skipping error while trying to remove session", e);
            }
        }
    }

    static class InitialFolderValues {
        final int uidnext, modseq;
        int firstUnread = -1;

        InitialFolderValues(Folder folder) {
            uidnext = folder.getImapUIDNEXT();
            modseq = folder.getImapMODSEQ();
        }
    }

    Pair<ImapSession, InitialFolderValues> openFolder(ImapPath path, byte params, ImapHandler handler) throws ServiceException {
        ZimbraLog.imap.debug("opening folder: %s", path);

        if (!path.isSelectable()) {
            throw ServiceException.PERM_DENIED("cannot select folder: " + path);
        }
        if ((params & ImapFolder.SELECT_CONDSTORE) != 0) {
            handler.activateExtension(ImapExtension.CONDSTORE);
        }

        Folder folder = (Folder) path.getFolder();
        int folderId = folder.getId();
        Mailbox mbox = folder.getMailbox();
        // don't have a session when the folder is loaded...
        OperationContext octxt = handler.getCredentials().getContext();

        mbox.beginTrackingImap();

        List<ImapMessage> i4list = null;
        // *always* recalculate the contents of search folders
        if (folder instanceof SearchFolder) {
            i4list = loadVirtualFolder(octxt, (SearchFolder) folder);
        }

        synchronized (mbox) {
            // need mInitialRecent to be set *before* loading the folder so we can determine what's \Recent
            folder = mbox.getFolderById(octxt, folderId);
            int recentCutoff = folder.getImapRECENTCutoff();

            if (i4list == null) {
                List<Session> listeners = mbox.getListeners(Session.Type.IMAP);
                // first option is to duplicate an existing registered session
                //   (could try to just activate an inactive session, but this logic is simpler for now)
                i4list = duplicateExistingSession(folderId, listeners);
                // no matching session means we next check for serialized folder data
                if (i4list == null) {
                    i4list = duplicateSerializedFolder(folder);
                }
                // do the consistency check, if requested
                if (CONSISTENCY_CHECK) {
                    i4list = consistencyCheck(i4list, mbox, octxt, folder);
                }
                // no matching serialized session means we have to go to the DB to get the messages
                if (i4list == null) {
                    i4list = mbox.openImapFolder(octxt, folderId);
                }
            }

            Collections.sort(i4list);
            // check messages for imapUid <= 0 and assign new IMAP IDs if necessary
            renumberMessages(octxt, mbox, i4list);

            ImapFolder i4folder = new ImapFolder(path, params, handler);

            // don't rely on the <code>Folder</code> object being updated in place
            folder = mbox.getFolderById(octxt, folderId);
            // can't set these until *after* loading the folder because UID renumbering affects them
            InitialFolderValues initial = new InitialFolderValues(folder);

            for (ImapMessage i4msg : i4list) {
                i4folder.cache(i4msg, i4msg.imapUid > recentCutoff);
                if (initial.firstUnread == -1 && (i4msg.flags & Flag.BITMASK_UNREAD) != 0) {
                    initial.firstUnread = i4msg.sequence;
                }
            }
            i4folder.setInitialSize();
            ZimbraLog.imap.debug("added %s", i4list);

            ImapSession session = null;
            try {
                session = new ImapSession(i4folder, handler);
                session.register();
                synchronized (sessions) {
                    sessions.put(session, null);
                }
                return new Pair<ImapSession, InitialFolderValues>(session, initial);
            } catch (ServiceException e) {
                if (session != null) {
                    session.unregister();
                }
                throw e;
            }
        }
    }

    /** Fetches the messages contained within a search folder.  When a search
     *  folder is IMAP-visible, it appears in folder listings, is SELECTable
     *  READ-ONLY, and appears to have all matching messages as its contents.
     *  If it is not visible, it will be completely hidden from all IMAP
     *  commands.
     * @param octxt   Encapsulation of the authenticated user.
     * @param search  The search folder being exposed. */
    private static List<ImapMessage> loadVirtualFolder(OperationContext octxt, SearchFolder search) throws ServiceException {
        List<ImapMessage> i4list = new ArrayList<ImapMessage>();

        byte[] types = ImapFolder.getTypeConstraint(search);
        if (types.length == 0) {
            return i4list;
        }

        SearchParams params = new SearchParams();
        params.setQueryStr(search.getQuery());
        params.setIncludeTagDeleted(true);
        params.setTypes(types);
        params.setSortBy(SortBy.DATE_ASCENDING);
        params.setChunkSize(1000);
        params.setMode(Mailbox.SearchResultMode.IMAP);

        Mailbox mbox = search.getMailbox();
        try {
            ZimbraQueryResults zqr = mbox.search(SoapProtocol.Soap12, octxt, params);
            try {
                for (ZimbraHit hit = zqr.getNext(); hit != null; hit = zqr.getNext()) {
                    i4list.add(hit.getImapMessage());
                }
            } finally {
                zqr.doneWithSearchResults();
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw ServiceException.FAILURE("failure opening search folder", e);
        }
        return i4list;
    }

    private static List<ImapMessage> duplicateExistingSession(int folderId, List<Session> sessionList) {
        for (Session session : sessionList) {
            ImapSession i4listener = (ImapSession) session;
            if (i4listener.getFolderId() == folderId) {
                //   FIXME: may want to prefer loaded folders over paged-out folders
                synchronized (i4listener) {
                    try {
                        ImapFolder i4selected = i4listener.getImapFolder();
                        if (i4selected == null) {
                            return null;
                        }
                        // found a matching session, so just copy its contents!
                        ZimbraLog.imap.debug("copying message data from existing session: %s", i4listener.getPath());
                        final List<ImapMessage> i4list = new ArrayList<ImapMessage>(i4selected.getSize());
                        i4selected.traverse(new Function<ImapMessage, Void>() {
                            @Override
                            public Void apply(ImapMessage i4msg) {
                                if (!i4msg.isExpunged()) {
                                    i4list.add(new ImapMessage(i4msg));
                                }
                                return null;
                            }
                        });

                        // if we're duplicating an inactive session, nuke that other session
                        // XXX: watch out for deadlock between this and the SessionCache
                        if (!i4listener.isInteractive()) {
                            i4listener.unregister();
                        }

                        return i4list;
                    } catch (IOException e) {
                        ZimbraLog.imap.warn("skipping error while trying to page in for copy (%s)",
                                i4listener.getPath(), e);
                    }
                }
            }
        }
        return null;
    }

    private static List<ImapMessage> duplicateSerializedFolder(Folder folder) {
        try {
            ImapFolder i4folder = mSerializer.deserialize(cacheKey(folder));
            if (i4folder == null) {
                return null;
            }

            ZimbraLog.imap.debug("copying message data from serialized session: %s", folder.getPath());

            final List<ImapMessage> i4list = new ArrayList<ImapMessage>(i4folder.getSize());
            i4folder.traverse(new Function<ImapMessage, Void>() {
                @Override
                public Void apply(ImapMessage i4msg) {
                    if (!i4msg.isExpunged()) {
                        i4list.add(i4msg.reset());
                    }
                    return null;
                }
            });
            return i4list;
        } catch (IOException ioe) {
            if (!(ioe instanceof FileNotFoundException))
                ZimbraLog.imap.warn("skipping error while trying to deserialize for copy (" + new ItemId(folder) + ")", ioe);
            return null;
        }
    }

    private static List<ImapMessage> consistencyCheck(List<ImapMessage> i4list, Mailbox mbox, OperationContext octxt, Folder folder) {
        if (i4list == null) {
            return i4list;
        }

        String fid = mbox.getAccountId() + ":" + folder.getId();
        try {
            List<ImapMessage> actualContents = mbox.openImapFolder(octxt, folder.getId());
            Collections.sort(actualContents);

            if (i4list.size() != actualContents.size()) {
                ZimbraLog.imap.error("IMAP session cache consistency check failed (%s): inconsistent list lengths", fid);
                clearCache(folder);
                return actualContents;
            }

            for (Iterator<ImapMessage> it1 = i4list.iterator(), it2 = actualContents.iterator(); it1.hasNext() || it2.hasNext(); ) {
                ImapMessage msg1 = it1.next(), msg2 = it2.next();
                if (msg1.msgId != msg2.msgId || msg1.imapUid != msg2.imapUid) {
                    ZimbraLog.imap.error("IMAP session cache consistency check failed (%s): id mismatch (%d/%d vs %d/%d)",
                            fid, msg1.msgId, msg1.imapUid, msg2.msgId, msg2.imapUid);
                    clearCache(folder);
                    return actualContents;
                } else if (msg1.tags != msg2.tags || msg1.flags != msg2.flags || msg1.sflags != msg2.sflags) {
                    ZimbraLog.imap.error("IMAP session cache consistency check failed (%s): flag/tag/sflag mismatch (%X/%X/%X vs %X/%X/%X)",
                            fid, msg1.flags, msg1.tags, msg1.sflags, msg2.flags, msg2.tags, msg2.sflags);
                    clearCache(folder);
                    return actualContents;
                }
            }
            return i4list;
        } catch (ServiceException e) {
            ZimbraLog.imap.info("error caught during IMAP session cache consistency check; falling back to reload", e);
            clearCache(folder);
            return null;
        }
    }

    private static void renumberMessages(OperationContext octxt, Mailbox mbox, List<ImapMessage> i4sorted)
    throws ServiceException {
        List<ImapMessage> unnumbered = new ArrayList<ImapMessage>();
        List<Integer> renumber = new ArrayList<Integer>();
        while (!i4sorted.isEmpty() && i4sorted.get(0).imapUid <= 0) {
            ImapMessage i4msg = i4sorted.remove(0);
            unnumbered.add(i4msg);  renumber.add(i4msg.msgId);
        }
        if (!renumber.isEmpty()) {
            List<Integer> newIds = mbox.resetImapUid(octxt, renumber);
            for (int i = 0; i < newIds.size(); i++) {
                unnumbered.get(i).imapUid = newIds.get(i);
            }
            i4sorted.addAll(unnumbered);
        }
    }

    void closeFolder(ImapSession session, boolean isUnregistering) {
        // XXX: does this require synchronization?

        // detach session from handler and jettison session state from folder
        if (session.isInteractive()) {
            session.inactivate();
        }

        // no fancy stuff for search folders since they're always recalculated on load
        if (session.isVirtual()) {
            session.detach();
            return;
        }

        // checkpoint the folder data if desired
        if (SERIALIZE_ON_CLOSE) {
            try {
                // could use session.serialize() if we want to leave it in memory...
                ZimbraLog.imap.debug("paging session during close: " + session.getPath());
                session.unload();
            } catch (Exception e) {
                ZimbraLog.imap.warn("skipping error while trying to serialize during close (" + session.getPath() + ")", e);
            }
        }

        if (isUnregistering) {
            return;
        }

        // recognize if we're not configured to allow sessions to hang around after end of SELECT
        if (TERMINATE_ON_CLOSE) {
            session.detach();
            return;
        }

        // if there are still other listeners on this folder, this session is unnecessary
        Mailbox mbox = session.getMailbox();
        if (mbox != null) {
            synchronized (mbox) {
                for (Session listener : mbox.getListeners(Session.Type.IMAP)) {
                    ImapSession i4listener = (ImapSession) listener;
                    if (i4listener != session && i4listener.getFolderId() == session.getFolderId()) {
                        session.detach();
                        recordAccess(i4listener);
                        return;
                    }
                }
            }
        }
    }


    private static void clearCache(Folder folder) {
        mSerializer.remove(cacheKey(folder));
    }

    public static String cacheKey(ImapSession session) throws ServiceException {
        Mailbox mbox = session.getMailbox();
        if (mbox == null) {
            mbox = MailboxManager.getInstance().getMailboxByAccountId(session.getTargetAccountId());
        }

        String cachekey = cacheKey(mbox.getFolderById(session.getFolderId()));
        // if there are unnotified expunges, *don't* use the default cache key
        //   ('+' is a good separator because it alpha-sorts before the '.' of the filename extension)
        return session.hasExpunges() ? cachekey + "+" + session.getQualifiedSessionId() : cachekey;
    }

    public static String cacheKey(Folder folder) {
        Mailbox mbox = folder.getMailbox();
        int modseq = folder instanceof SearchFolder ? mbox.getLastChangeID() : folder.getImapMODSEQ();
        int uvv = folder instanceof SearchFolder ? mbox.getLastChangeID() : ImapFolder.getUIDValidity(folder);
        // 0-pad the MODSEQ and UVV so alpha ordering sorts properly
        return String.format("%s_%d_%010d_%010d", mbox.getAccountId(), folder.getId(), modseq, uvv);
    }

    private static final FolderSerializer mSerializer = new DiskSerializer();

    static void serialize(String cachekey, ImapFolder i4folder) throws IOException {
        mSerializer.serialize(cachekey, i4folder);
    }

    static ImapFolder deserialize(String cachekey) throws IOException {
        return mSerializer.deserialize(cachekey);
    }

    interface FolderSerializer {
        void serialize(String cachekey, ImapFolder i4folder) throws IOException;
        ImapFolder deserialize(String cachekey) throws IOException;
        void remove(String cachekey);
    }

    static class DiskSerializer implements FolderSerializer {
        private static final String CACHE_DATA_SUBDIR = "data" + File.separator + "mailboxd" + File.separator + "imap" + File.separator + "cache";
        private static final File sCacheDir = new File(LC.zimbra_home.value() + File.separator + CACHE_DATA_SUBDIR);
        private static final String IMAP_CACHEFILE_SUFFIX = ".i4c";

        DiskSerializer() {
            sCacheDir.mkdirs();

            // iterate over all serialized folders and delete all but the most recent
            File[] allCached = sCacheDir.listFiles();
            Arrays.sort(allCached, new Comparator<File>() {
                @Override public int compare(File o1, File o2)  { return o1.getName().compareTo(o2.getName()); }
            });
            File previous = null;
            String lastOwner = "", lastId = "";
            for (File cached : allCached) {
                String[] parts = cached.getName().split("_");
                if (previous != null && parts.length >= 4) {
                    if (lastOwner.equals(parts[0]) && lastId.equals(parts[1])) {
                        previous.delete();
                    } else {
                        removeSessionFromFilename(previous);
                    }
                }
                lastOwner = parts[0];  lastId = parts[1];
                previous = cached;
            }
            removeSessionFromFilename(previous);
        }

        /** Renames the passed-in <code>File</code> by removing everything
         *  from the <tt>+</tt> character to the extension.  If the filename
         *  does not contain '<tt>+</tt>', does nothing. */
        private static void removeSessionFromFilename(File file) {
            if (file == null)
                return;
            String filename = file.getName();
            if (filename.indexOf("+") != -1) {
                file.renameTo(new File(sCacheDir, filename.substring(0, filename.lastIndexOf("+")) + IMAP_CACHEFILE_SUFFIX));
            }
        }

        @Override
        public void serialize(String cachekey, ImapFolder i4folder) throws IOException {
            File pagefile = new File(sCacheDir, cachekey + IMAP_CACHEFILE_SUFFIX);
            if (pagefile.exists())
                return;

            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(fos = new FileOutputStream(pagefile));
                synchronized (i4folder) {
                    oos.writeObject(i4folder);
                }
            } catch (IOException ioe) {
                ByteUtil.closeStream(oos);  oos = null;
                ByteUtil.closeStream(fos);  fos = null;
                pagefile.delete();
                throw ioe;
            } finally {
                ByteUtil.closeStream(oos);
                ByteUtil.closeStream(fos);
            }
        }

        @Override
        public ImapFolder deserialize(String cachekey) throws IOException {
            File pagefile = new File(sCacheDir, cachekey + IMAP_CACHEFILE_SUFFIX);
            if (!pagefile.exists())
                throw new FileNotFoundException("unable to deserialize folder state (pagefile not found)");

            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                // read serialized ImapFolder from cache
                ois = new ObjectInputStream(fis = new FileInputStream(pagefile));
                return (ImapFolder) ois.readObject();
            } catch (Exception e) {
                ByteUtil.closeStream(ois);  ois = null;
                ByteUtil.closeStream(fis);  fis = null;
                pagefile.delete();
                // IOException(String, Throwable) exists only since 1.6.
                IOException ioe = new IOException("unable to deserialize folder state");
                ioe.initCause(e);
                throw ioe;
            } finally {
                ByteUtil.closeStream(ois);
                ByteUtil.closeStream(fis);
            }
        }

        @Override
        public void remove(String cachekey) {
            File pagefile = new File(sCacheDir, cachekey + IMAP_CACHEFILE_SUFFIX);
            pagefile.delete();
        }
    }
}
