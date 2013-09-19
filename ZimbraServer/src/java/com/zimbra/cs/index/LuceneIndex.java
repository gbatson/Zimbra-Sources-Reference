/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.index;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyTermEnum;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.store.SingleInstanceLockFactory;
import org.apache.lucene.util.Version;

import com.google.common.base.Objects;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.index.SortBy.SortDirection;
import com.zimbra.cs.localconfig.DebugConfig;
import com.zimbra.cs.service.util.SyncToken;

/**
 * Lucene provider that uses {@link IndexWritersCache} to manage the index LRU.
 */
public final class LuceneIndex extends IndexWritersCache.CacheEntry {

    /**
     * We don't want to enable StopFilter preserving position increments,
     * which is enabled on or after 2.9, because we want phrases to match across
     * removed stop words.
     */
    public static final Version VERSION = Version.LUCENE_24;

    private static IndexReadersCache sIndexReadersCache;
    private static IndexWritersCache sIndexWritersCache;

    /**
     * If documents are being constantly added to an index, then it will stay at
     * the front of the LRU cache and will never flush itself to disk: this
     * setting specifies the maximum number of writes we will allow to the index
     * before we force a flush. Higher values will improve batch-add
     * performance, at the cost of longer-lived transactions in the redolog.
     */
    private static int sMaxUncommittedOps;

    /**
     * This static array saves us from the time required to create a new array
     * everytime editDistance is called.
     */
    private int e[][] = new int[1][1];
    private final LuceneDirectory luceneDirectory;

    private IndexWriter mIndexWriter;

    private volatile long mLastWriteTime = 0;

    private Sort mLatestSort = null;
    private SortBy mLatestSortBy = null;
    private MailboxIndex mailboxIndex;
    private int mNumUncommittedItems = 0;
    private SyncToken mHighestUncomittedModContent = new SyncToken(0);
    private int beginWritingNestLevel = 0;
    private List<IndexReaderRef> mOpenReaders = new ArrayList<IndexReaderRef>();

    static void flushAllWriters() {
        if (DebugConfig.disableIndexing)
            return;

        sIndexWritersCache.flushAllWriters();
    }

    static void shutdown() {
        if (DebugConfig.disableIndexing)
            return;

        sIndexReadersCache.signalShutdown();
        try {
            sIndexReadersCache.join();
        } catch (InterruptedException e) {}

        sIndexWritersCache.shutdown();
    }

    static void startup() {
        if (DebugConfig.disableIndexing) {
            ZimbraLog.index.info("Indexing is disabled by the localconfig 'debug_disable_indexing' flag");
            return;
        }

        if (sIndexWritersCache != null) {
            // in case startup is somehow called twice
            sIndexWritersCache.shutdown();
        }

        sMaxUncommittedOps = LC.zimbra_index_max_uncommitted_operations.intValue();
        sIndexReadersCache = new IndexReadersCache(
                LC.zimbra_index_reader_lru_size.intValue(),
                LC.zimbra_index_reader_idle_flush_time.longValue() * 1000,
                LC.zimbra_index_reader_idle_sweep_frequency.longValue() * 1000);
        sIndexReadersCache.start();

        sIndexWritersCache = new IndexWritersCache();
    }

    /**
     * Finds and returns the smallest of three integers.
     */
    private static final int min(int a, int b, int c) {
        int t = (a < b) ? a : b;
        return (t < c) ? t : c;
    }

    /**
     * Returns total bytes written to the filesystem by Lucene - for stats.
     * logging.
     *
     * @return bytes count
     */
    public long getBytesWritten() {
        return luceneDirectory.getBytesWritten();
    }

    /**
     * Returns total bytes read from the filesystem by Lucene - for stats
     * logging.
     *
     * @return bytes count
     */
    public long getBytesRead() {
        return luceneDirectory.getBytesRead();
    }

    LuceneIndex(MailboxIndex mbidx, String idxParentDir, int mailboxId) throws ServiceException {
        mailboxIndex = mbidx;
        mIndexWriter = null;

        // this must be different from the idxParentDir (see the IMPORTANT comment below)
        String idxPath = idxParentDir + File.separatorChar + '0';

        {
            File parentDirFile = new File(idxParentDir);

            // IMPORTANT!  Don't make the actual index directory (mIdxDirectory) yet!
            //
            // The runtime open-index code checks the existance of the actual index directory:
            // if it does exist but we cannot open the index, we do *NOT* create it under the
            // assumption that the index was somehow corrupted and shouldn't be messed-with....on the
            // other hand if the index dir does NOT exist, then we assume it has never existed (or
            // was deleted intentionally) and therefore we should just create an index.
            if (!parentDirFile.exists()) {
                parentDirFile.mkdirs();
            }

            if (!parentDirFile.canRead()) {
                throw ServiceException.FAILURE("Cannot READ index directory (mailbox=" + mailboxId + " idxPath=" + idxPath + ")", null);
            }
            if (!parentDirFile.canWrite()) {
                throw ServiceException.FAILURE("Cannot WRITE index directory (mailbox=" + mailboxId + " idxPath=" + idxPath + ")", null);
            }

            // the Lucene code does not atomically swap the "segments" and "segments.new"
            // files...so it is possible that a previous run of the server crashed exactly in such
            // a way that we have a "segments.new" file but not a "segments" file.  We we will check here
            // for the special situation that we have a segments.new
            // file but not a segments file...
            File segments = new File(idxPath, "segments");
            if (!segments.exists()) {
                File segments_new = new File(idxPath, "segments.new");
                if (segments_new.exists()) {
                    segments_new.renameTo(segments);
                }
            }

            try {
                luceneDirectory = LuceneDirectory.open(new File(idxPath),
                        new SingleInstanceLockFactory());
            } catch (IOException e) {
                throw ServiceException.FAILURE(
                        "Cannot create LuceneDirectory: " + idxPath, e);
            }
        }
    }

    /**
     * Adds the list of documents to the index.
     * <p>
     * If {@code deleteFirst} is false, then we are sure that this item is not
     * already in the index, and so we can skip the check-update step.
     */
    public void addDocument(IndexDocument[] docs, int itemId,
            String indexId, int modContent, long receivedDate, long size,
            String sortSubject, String sortSender, boolean deleteFirst)
        throws IOException {

        if (docs.length == 0) {
            return;
        }

        synchronized (getLock()) {
            beginWriteOperation();
            try {
                assert(mIndexWriter != null);

                for (IndexDocument doc : docs) {
                    // doc can be shared by multiple threads if multiple mailboxes
                    // are referenced in a single email
                    synchronized (doc) {
                        doc.removeSortSubject();
                        doc.removeSortName();

                        doc.addSortSubject(sortSubject);
                        doc.addSortName(sortSender);

                        doc.removeMailboxBlobId();
                        doc.addMailboxBlobId(indexId);

                        // If this doc is shared by multi threads, then the date might just be wrong,
                        // so remove and re-add the date here to make sure the right one gets written!
                        doc.removeSortDate();
                        doc.addSortDate(receivedDate);

                        doc.removeSortSize();
                        doc.addSortSize(size);
                        doc.addAll();

                        if (deleteFirst) {
                            String itemIdStr = indexId;
                            Term toDelete = new Term(LuceneFields.L_MAILBOX_BLOB_ID, itemIdStr);
                            mIndexWriter.updateDocument(toDelete, doc.toDocument());
                        } else {
                            mIndexWriter.addDocument(doc.toDocument());
                        }

                    } // synchronized(doc)
                } // foreach Document

                if (modContent > 0) {
                    SyncToken token = new SyncToken(modContent, itemId);
                    mNumUncommittedItems++;
                    assert(token.after(mHighestUncomittedModContent));
                    if (token.after(mHighestUncomittedModContent)) {
                        mHighestUncomittedModContent = token;
                    } else {
                        ZimbraLog.index_add.info("Index items not submitted in order: curHighest=" +
                                             mHighestUncomittedModContent + " new highest=" + modContent + " " +
                                             "indexId=" + indexId);
                    }
                }

                // TODO: tim: this might seem bad, since an index in
                // steady-state-of-writes will never get flushed, however we
                // also track the number of uncomitted-operations on the index,
                // and will force a flush if the index has had a lot written to
                // it without a flush.
                updateLastWriteTime();
            } finally {
                endWriteOperation();
            }

        }
    }

    /**
     * Deletes all the documents from the index that have {@code indexIds} as
     * specified.
     */
    public List<Integer> deleteDocuments(List<Integer> itemIds) throws IOException {
        synchronized (getLock()) {
            beginWriteOperation();
            try {
                int i = 0;
                for (Integer itemId : itemIds) {
                    try {
                        Term toDelete = new Term(LuceneFields.L_MAILBOX_BLOB_ID, itemId.toString());
                        mIndexWriter.deleteDocuments(toDelete);
                        // NOTE!  The numDeleted may be < you expect here, the document may
                        // already be deleted and just not be optimized out yet -- some lucene
                        // APIs (e.g. docFreq) will still return the old count until the indexes
                        // are optimized...
                        if (ZimbraLog.index_add.isDebugEnabled()) {
                            ZimbraLog.index_add.debug("Deleted index documents for itemId " + itemId);
                        }
                    } catch (IOException ioe) {
                        ZimbraLog.index_add.debug("deleteDocuments exception on index " + i +
                                " out of "+itemIds.size() + " (id=" + itemIds.get(i) + ")");
                        List<Integer> toRet = new ArrayList<Integer>(i);
                        for (int j = 0; j < i; j++) {
                            toRet.add(itemIds.get(j));
                        }
                        return toRet;
                    }
                    i++;
                }
            } finally {
                endWriteOperation();
            }
            return itemIds; // success
        }
    }

    /**
     * Deletes this index completely.
     */
    public void deleteIndex() throws IOException {
        synchronized (getLock()) {
            flush();
            if (ZimbraLog.index_add.isDebugEnabled()) {
                ZimbraLog.index_add.debug("Deleting index " + luceneDirectory);
            }

            String[] files;
            try {
                files = luceneDirectory.listAll();
            } catch (NoSuchDirectoryException ignore) {
                return;
            } catch (IOException e) {
                ZimbraLog.index_add.warn("Failed to delete index: %s",
                        luceneDirectory, e);
                return;
            }

            for (String file : files) {
                luceneDirectory.deleteFile(file);
            }
        }
    }

    private void enumerateTermsForField(String regex, Term firstTerm,
            TermEnumInterface callback) throws IOException {
        synchronized(getLock()) {
            IndexSearcherRef searcher = this.getIndexSearcherRef();
            try {
                IndexReader iReader = searcher.getReader();

                TermEnum terms = iReader.terms(firstTerm);
                boolean hasDeletions = iReader.hasDeletions();

                // HACK!
                boolean stripAtBeforeRegex = false;
                if (callback instanceof DomainEnumCallback)
                    stripAtBeforeRegex = true;

                Pattern p = null;
                if (regex != null && regex.length() > 0) {
                    p = Pattern.compile(regex);
                }

                do {
                    Term cur = terms.term();
                    if (cur != null) {
                        if (!cur.field().equals(firstTerm.field())) {
                            break;
                        }

                        boolean skipIt = false;
                        if (p != null) {
                            String compareTo = cur.text();
                            if (stripAtBeforeRegex)
                                if (compareTo.length() > 1 && compareTo.charAt(0) == '@') {
                                    compareTo = compareTo.substring(1);
                                }
                            if (!p.matcher(compareTo).matches()) {
                                skipIt = true;
                            }
                        }

                        if (!skipIt) {
                            // NOTE: the term could exist in docs, but they might all be deleted. Unfortunately this means
                            // that we need to actually walk the TermDocs enumeration for this document to see if it is
                            // non-empty
                            if ((!hasDeletions) || (iReader.termDocs(cur).next())) {
                                callback.onTerm(cur, terms.docFreq());
                            }
                        }
                    }
                } while (terms.next());
            } finally {
                searcher.dec();
            }
        }

    }

    /**
     * Returns {@code true} if all tokens were expanded or {@code false} if more
     * tokens were available but we hit the specified maximum.
     */
    public boolean expandWildcardToken(Collection<String> toRet, String field,
            String token, int maxToReturn) throws ServiceException {
        // all lucene text should be in lowercase...
        token = token.toLowerCase();

        try {
            IndexSearcherRef searcher = this.getIndexSearcherRef();
            try {
                Term firstTerm = new Term(field, token);

                IndexReader iReader = searcher.getReader();

                TermEnum terms = iReader.terms(firstTerm);

                do {
                    Term cur = terms.term();
                    if (cur != null) {
                        if (!cur.field().equals(firstTerm.field())) {
                            break;
                        }

                        String curText = cur.text();

                        if (curText.startsWith(token)) {
                            if (toRet.size() >= maxToReturn) {
                                return false;
                            }
                            // we don't care about deletions, they will be filtered later
                            toRet.add(cur.text());
                        } else {
                            if (curText.compareTo(token) > 0) {
                                break;
                            }
                        }
                    }
                } while (terms.next());

                return true;
            } finally {
                searcher.dec();
            }
        } catch (IOException e) {
            throw ServiceException.FAILURE("Caught IOException opening index", e);
        }
    }

    /**
     * Forces all outstanding index writes to go through, won't return until it
     * completes.
     */
    public void flush() {
        synchronized (getLock()) {
            sIndexWritersCache.flush(this);
            sIndexReadersCache.removeIndexReader(this);
        }
    }

    /**
     * @param fieldName Lucene field (e.g. LuceneFields.L_H_CC)
     * @param collection Strings which correspond to all of the domain terms
     * stored in a given field
     * @throws IOException
     */
    public void getDomainsForField(String fieldName, String regex,
            Collection<BrowseTerm> collection) throws IOException {
        if (regex == null) {
            regex = "";
        }
        enumerateTermsForField(regex, new Term(fieldName,""),
                new DomainEnumCallback(collection));
    }

    /**
     * @param collection Strings which correspond to all of the attachment types
     * in the index
     * @throws IOException
     */
    public void getAttachments(String regex, Collection<BrowseTerm> collection)
        throws IOException {

        if (regex == null) {
            regex = "";
        }
        enumerateTermsForField(regex, new Term(LuceneFields.L_ATTACHMENTS, ""),
                new TermEnumCallback(collection));
    }

    /**
     * Return the list of objects (e.g. PO, etc) from the index, for
     * SearchBuilder browsing.
     */
    public void getObjects(String regex, Collection<BrowseTerm> collection)
        throws IOException {

        if (regex == null) {
            regex = "";
        }
        enumerateTermsForField(regex, new Term(LuceneFields.L_OBJECTS, ""),
                new TermEnumCallback(collection));
    }

    /**
     * Caller is responsible for calling {@link IndexSearcherRef#dec()} before
     * allowing it to go out of scope (otherwise a RuntimeException will occur).
     *
     * @return A {@link IndexSearcherRef} for this index.
     * @throws IOException if opening an {@link IndexReader} failed
     */
    public IndexSearcherRef getIndexSearcherRef() throws IOException {
        synchronized (getLock()) {
            return new IndexSearcherRef(getIndexReaderRef());
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("mbox", mailboxIndex.getMailboxId())
            .add("dir", luceneDirectory)
            .toString();
    }

    @Override
    long getLastWriteTime() {
        return mLastWriteTime;
    }

    private final Object getLock() {
        return mailboxIndex.getLock();
    }

    public Sort getSort(SortBy searchOrder) {
        if (searchOrder == null || searchOrder == SortBy.NONE) {
            return null;
        }

        synchronized(getLock()) {
            if ((mLatestSortBy == null) ||
                    ((searchOrder.getCriterion() != mLatestSortBy.getCriterion()) ||
                            (searchOrder.getDirection() != mLatestSortBy.getDirection()))) {
                String field;
                int type;
                boolean reverse = false;;

                if (searchOrder.getDirection() == SortDirection.DESCENDING) {
                    reverse = true;
                }

                switch (searchOrder.getCriterion()) {
                    case NAME:
                    case NAME_NATURAL_ORDER:
                    case SENDER:
                        field = LuceneFields.L_SORT_NAME;
                        type = SortField.STRING;
                        break;
                    case SUBJECT:
                        field = LuceneFields.L_SORT_SUBJECT;
                        type = SortField.STRING;
                        break;
                    case SIZE:
                        field = LuceneFields.L_SORT_SIZE;
                        type = SortField.LONG;
                        break;
                    case DATE:
                    default:
                        // default to DATE_DESCENDING!
                        field = LuceneFields.L_SORT_DATE;
                        type = SortField.STRING;
                        reverse = true;;
                        break;
                }

                mLatestSort = new Sort(new SortField(field, type, reverse));
                mLatestSortBy = searchOrder;
            }
            return mLatestSort;
        }
    }

    /**
     * Suggests alternate spellings for the given token.
     */
    public List<SpellSuggestQueryInfo.Suggestion> suggestSpelling(String field,
            String token) throws ServiceException {
        LinkedList<SpellSuggestQueryInfo.Suggestion> toRet = null;

        token = token.toLowerCase();

        try {
            IndexSearcherRef searcher = this.getIndexSearcherRef();
            try {
                IndexReader iReader = searcher.getReader();

                Term term = new Term(field, token);
                int freq = iReader.docFreq(term);
                int numDocs = iReader.numDocs();

                if (freq == 0 && numDocs > 0) {
                    toRet = new LinkedList<SpellSuggestQueryInfo.Suggestion>();
                    FuzzyTermEnum fuzzyEnum = new FuzzyTermEnum(iReader, term, 0.5f, 1);
                    do {
                        Term cur = fuzzyEnum.term();
                        if (cur != null) {
                            String curText = cur.text();
                            int curDiff = editDistance(curText, token, curText.length(), token.length());

                            SpellSuggestQueryInfo.Suggestion sug = new SpellSuggestQueryInfo.Suggestion();
                            sug.mStr = curText;
                            sug.mEditDist = curDiff;
                            sug.mDocs = fuzzyEnum.docFreq();
                            toRet.add(sug);
                        }
                    } while (fuzzyEnum.next());
                }
            } finally {
                searcher.dec();
            }
        } catch (IOException e) {
            throw ServiceException.FAILURE("Caught IOException opening index", e);
        }

        return toRet;
    }

    /**
     * Levenshtein distance also known as edit distance is a measure of
     * similiarity between two strings where the distance is measured as the
     * number of character deletions, insertions or substitutions required to
     * transform one string to the other string.
     * <p>
     * This method takes in four parameters; two strings and their respective
     * lengths to compute the Levenshtein distance between the two strings.
     * The result is returned as an integer.
     */
    private final int editDistance(String s, String t, int n, int m) {
        if (e.length <= n || e[0].length <= m) {
            e = new int[Math.max(e.length, n + 1)][Math.max(e[0].length, m + 1)];
        }
        int d[][] = e; // matrix
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s

        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        // init matrix d
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // start computing edit distance
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);
            for (j = 1; j <= m; j++) {
                if (s_i != t.charAt(j - 1)) {
                    d[i][j] = min(d[i - 1][j], d[i][j - 1], d[i - 1][j - 1]) + 1;
                } else {
                    d[i][j] = min(d[i - 1][j]+1, d[i][j - 1] + 1, d[i - 1][j - 1]);
                }
            }
        }

        // we got the result!
        return d[n][m];
    }

    private static final int MAX_TERMS_PER_QUERY =
        LC.zimbra_index_lucene_max_terms_per_query.intValue();

    private IndexReader openIndexReader(boolean tryRepair) throws IOException {
        try {
            return IndexReader.open(luceneDirectory, null, true,
                    LC.zimbra_index_lucene_term_index_divisor.intValue());
        } catch (CorruptIndexException e) {
            if (!tryRepair) {
                throw e;
            }
            flush(); // close IndexWriter
            repair(e);
            return openIndexReader(false);
        } catch (AssertionError e) {
            if (!tryRepair) {
                throw e;
            }
            flush(); // close IndexWriter
            repair(e);
            return openIndexReader(false);
        }
    }

    private IndexWriter openIndexWriter(boolean create, boolean tryRepair) throws IOException {
        try {
            IndexWriter writer = new IndexWriter(luceneDirectory, mailboxIndex.getAnalyzer(),
                    create, IndexWriter.MaxFieldLength.LIMITED);
            if (ZimbraLog.index_lucene.isDebugEnabled()) {
                writer.setInfoStream(new PrintStream(new LoggingOutputStream(
                        ZimbraLog.index_lucene, Log.Level.debug)));
            }
            return writer;
        } catch (AssertionError e) {
            unlock();
            if (!tryRepair) {
                throw e;
            }
            repair(e);
            return openIndexWriter(false, false);
        } catch (CorruptIndexException e) {
            unlock();
            if (!tryRepair) {
                throw e;
            }
            repair(e);
            return openIndexWriter(false, false);
        }
    }

    private <T extends Throwable> void repair(T ex) throws T {
        ZimbraLog.index_lucene.error("Index corrupted", ex);
        LuceneIndexRepair repair = new LuceneIndexRepair(luceneDirectory);
        try {
            if (repair.repair() > 0) {
                ZimbraLog.index_lucene.info("Index repaired, re-indexing is recommended.");
            } else {
                ZimbraLog.index_lucene.warn("Unable to repair, re-indexing is required.");
                throw ex;
            }
        } catch (IOException e) {
            ZimbraLog.index_lucene.warn("Failed to repair, re-indexing is required.", e);
            throw ex;
        }
    }

    private void unlock() {
        try {
            IndexWriter.unlock(luceneDirectory);
        } catch (IOException e) {
            ZimbraLog.index_lucene.warn("Failed to unlock", e);
        }
    }

    /**
     * Caller is responsible for calling {@link IndexReaderRef#dec()} before
     * allowing it to go out of scope (otherwise a RuntimeException will occur).
     *
     * @return A {@link IndexReaderRef} for this index.
     * @throws IOException
     */
    private IndexReaderRef getIndexReaderRef() throws IOException {
        BooleanQuery.setMaxClauseCount(MAX_TERMS_PER_QUERY);

        synchronized (getLock()) {
            sIndexWritersCache.flush(this); // flush writer if writing

            IndexReaderRef toRet = sIndexReadersCache.getIndexReader(this);
            if (toRet != null) {
                return toRet;
            }

            IndexReader reader = null;
            try {
                reader = openIndexReader(true);
            } catch (IOException e) {
                // Handle the special case of trying to open a not-yet-created
                // index, by opening for write and immediately closing.  Index
                // directory should get initialized as a result.
                File indexDir = luceneDirectory.getFile();
                if (indexDirIsEmpty(indexDir)) {
                    beginWriteOperation();
                    endWriteOperation();
                    flush();
                    try {
                        reader = openIndexReader(false);
                    } catch (IOException e1) {
                        if (reader != null) {
                            reader.close();
                        }
                        throw e1;
                    }
                } else {
                    if (reader != null) {
                        reader.close();
                    }
                    throw e;
                }
            }

            synchronized (mOpenReaders) {
                toRet = new IndexReaderRef(this, reader); // refcount starts at 1
                mOpenReaders.add(toRet);
            }

            sIndexReadersCache.putIndexReader(this, toRet); // addrefs if put in cache
            return toRet;
        }
    }

    /**
     * Check to see if it is OK for us to create an index in the specified
     * directory.
     *
     * @param indexDir
     * @return TRUE if the index directory is empty or doesn't exist,
     *         FALSE if the index directory exists and has files in it or if we cannot list files in the directory
     * @throws IOException
     */
    private boolean indexDirIsEmpty(File indexDir) {
        if (!indexDir.exists()) {
            // dir doesn't even exist yet.  Create the parents and return true
            indexDir.mkdirs();
            return true;
        }

        // Empty directory is okay, but a directory with any files
        // implies index corruption.

        File[] files = indexDir.listFiles();

        // if files is null here, we are likely running into file permission issue
        // log a WARN and return false
        if (files == null) {
            ZimbraLog.index.warn("Could not list files in directory " + indexDir.getAbsolutePath());
            return false;
        }

        int numFiles = 0;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            String fname = f.getName();
            if (f.isDirectory() && (fname.equals(".") || fname.equals("..")))
                continue;
            numFiles++;
        }
        return (numFiles <= 0);
    }

    public void beginWriteOperation() throws IOException {
        assert(Thread.holdsLock(getLock()));
        if (beginWritingNestLevel == 0) {
            // uncache the IndexReader if it is cached
            sIndexReadersCache.removeIndexReader(this);
            sIndexWritersCache.beginWriting(this);
        }
        beginWritingNestLevel++;
    }

    public void endWriteOperation() {
        assert(Thread.holdsLock(getLock()));

        // If nestLevel is 0 before the decrement, the corresponding begin()
        // probably got an IOException so nestLevel didn't get incremented. We
        // really don't want to proceed here, otherwise the nestLevel will
        // become negative, which is a situation that cannot be recovered until
        // a server restart.
        if (beginWritingNestLevel <= 0) {
            ZimbraLog.index.warn("nestLevel=%d, flushing skipped.", beginWritingNestLevel);
            return;
        }

        beginWritingNestLevel--;
        if (beginWritingNestLevel == 0) {
            if (mNumUncommittedItems > sMaxUncommittedOps) {
                if (ZimbraLog.index_add.isDebugEnabled()) {
                    ZimbraLog.index_add.debug("Flushing " + toString() + " because of too many uncommitted redo ops");
                }
                flush();
            } else {
                sIndexWritersCache.doneWriting(this);
            }
            updateLastWriteTime();
        }
    }

    @Override
    void doWriterOpen() throws IOException {
        if (mIndexWriter != null) {
            return; // already open!
        }

        assert(Thread.holdsLock(getLock()));

        boolean useBatchIndexing;
        try {
            useBatchIndexing = mailboxIndex.useBatchedIndexing();
        } catch (ServiceException e) {
            throw new IOException("Caught IOException checking BatchedIndexing flag " + e);
        }

        LuceneConfig config = new LuceneConfig(useBatchIndexing);

        try {
            mIndexWriter = openIndexWriter(false, true);
        } catch (IOException e) {
            // the index (the segments* file in particular) probably didn't exist
            // when new IndexWriter was called in the try block, we would get a
            // FileNotFoundException for that case. If the directory is empty,
            // this is the very first index write for this this mailbox (or the
            // index might be deleted), the FileNotFoundException is benign.
            if (indexDirIsEmpty(luceneDirectory.getFile())) {
                mIndexWriter = openIndexWriter(true, false);
            } else {
                throw e;
            }
        }

        if (config.useSerialMergeScheduler) {
            mIndexWriter.setMergeScheduler(new SerialMergeScheduler());
        }

        mIndexWriter.setMaxBufferedDocs(config.maxBufferedDocs);
        mIndexWriter.setRAMBufferSizeMB(config.ramBufferSizeKB / 1024.0);
        mIndexWriter.setMergeFactor(config.mergeFactor);

        if (config.useDocScheduler) {
            LogDocMergePolicy policy = new LogDocMergePolicy(mIndexWriter);
            mIndexWriter.setMergePolicy(policy);
            policy.setUseCompoundDocStore(config.useCompoundFile);
            policy.setUseCompoundFile(config.useCompoundFile);
            policy.setMergeFactor(config.mergeFactor);
            policy.setMinMergeDocs((int) config.minMerge);
            if (config.maxMerge != Integer.MAX_VALUE) {
                policy.setMaxMergeDocs((int) config.maxMerge);
            }
        } else {
            LogByteSizeMergePolicy policy = new LogByteSizeMergePolicy(mIndexWriter);
            mIndexWriter.setMergePolicy(policy);
            policy.setUseCompoundDocStore(config.useCompoundFile);
            policy.setUseCompoundFile(config.useCompoundFile);
            policy.setMergeFactor(config.mergeFactor);
            policy.setMinMergeMB(config.minMerge / 1024.0);
            if (config.maxMerge != Integer.MAX_VALUE) {
                policy.setMaxMergeMB(config.maxMerge / 1024.0);
            }
        }
    }

    @Override
    void doWriterClose() {
        if (mIndexWriter == null) {
            return;
        }

        ZimbraLog.index_add.debug("Closing IndexWriter %s", this);

        boolean success = false;
        try {
            // Flush all changes to file system before committing redos.
            mIndexWriter.close();
            success = true;
        } catch (CorruptIndexException e) {
            try {
                repair(e);
            } catch (CorruptIndexException ignore) {
            }
        } catch (AssertionError e) {
            repair(e);
        } catch (IOException e) {
            ZimbraLog.index_lucene.error("Failed to close IndexWriter %s", this, e);
            // fall through to finally here with success=false
        } finally {
            mIndexWriter = null;
            unlock();
            if (mNumUncommittedItems > 0) {
                assert(mHighestUncomittedModContent.getChangeId() > 0);
                mailboxIndex.indexingCompleted(mNumUncommittedItems, mHighestUncomittedModContent, success);
            }
            mNumUncommittedItems = 0;
            mHighestUncomittedModContent = new SyncToken(0);
        }
    }

    private void updateLastWriteTime() {
        mLastWriteTime = System.currentTimeMillis();
    }

    static class DomainEnumCallback implements TermEnumInterface {
        DomainEnumCallback(Collection<BrowseTerm> collection) {
            mCollection = collection;
        }

        @Override
        public void onTerm(Term term, int docFreq) {
            String text = term.text();
            if (text.length() > 1 && text.charAt(0) == '@') {
                mCollection.add(new BrowseTerm(text.substring(1), docFreq));
            }
        }

        private Collection<BrowseTerm> mCollection;
    }

    static class TermEnumCallback implements TermEnumInterface {
        TermEnumCallback(Collection<BrowseTerm> collection) {
            mCollection = collection;
        }

        @Override
        public void onTerm(Term term, int docFreq) {
            String text = term.text();
            if (text.length() > 1) {
                mCollection.add(new BrowseTerm(text, docFreq));
            }
        }

        private Collection<BrowseTerm> mCollection;
    }

    interface TermEnumInterface {
        abstract void onTerm(Term term, int docFreq);
    }

    /**
     * Called when the reader is closed by the {@link IndexReadersCache}.
     *
     * @param ref reference to {@link IndexReader}
     */
    public void onReaderClose(IndexReaderRef ref) {
        synchronized (mOpenReaders) {
            mOpenReaders.remove(ref);
        }
    }

    public IndexReader reopenReader(IndexReader reader) throws IOException {
        return reader.reopen();
    }

    int getMailboxId() {
        return mailboxIndex.getMailboxId();
    }

    private static final class LuceneConfig {

        final boolean useDocScheduler;
        final long minMerge;
        final long maxMerge;
        final int mergeFactor;
        final boolean useCompoundFile;
        final boolean useSerialMergeScheduler;
        final int maxBufferedDocs;
        final int ramBufferSizeKB;

        LuceneConfig(boolean batchIndexing) {
            if (batchIndexing) {
                useDocScheduler = LC.zimbra_index_lucene_batch_use_doc_scheduler.booleanValue();
                minMerge = LC.zimbra_index_lucene_batch_min_merge.longValue();
                maxMerge = LC.zimbra_index_lucene_batch_max_merge.longValue();
                mergeFactor = LC.zimbra_index_lucene_batch_merge_factor.intValue();
                useCompoundFile = LC.zimbra_index_lucene_batch_use_compound_file.booleanValue();
                useSerialMergeScheduler = LC.zimbra_index_lucene_batch_use_serial_merge_scheduler.booleanValue();
                maxBufferedDocs = LC.zimbra_index_lucene_batch_max_buffered_docs.intValue();
                ramBufferSizeKB = LC.zimbra_index_lucene_batch_ram_buffer_size_kb.intValue();
            } else {
                useDocScheduler = LC.zimbra_index_lucene_nobatch_use_doc_scheduler.booleanValue();
                minMerge = LC.zimbra_index_lucene_nobatch_min_merge.longValue();
                maxMerge = LC.zimbra_index_lucene_nobatch_max_merge.longValue();
                mergeFactor = LC.zimbra_index_lucene_nobatch_merge_factor.intValue();
                useCompoundFile = LC.zimbra_index_lucene_nobatch_use_compound_file.booleanValue();
                useSerialMergeScheduler = LC.zimbra_index_lucene_nobatch_use_serial_merge_scheduler.booleanValue();
                maxBufferedDocs = LC.zimbra_index_lucene_nobatch_max_buffered_docs.intValue();
                ramBufferSizeKB = LC.zimbra_index_lucene_nobatch_ram_buffer_size_kb.intValue();
            }
        }

    }

}
