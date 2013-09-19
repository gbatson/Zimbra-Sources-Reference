/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.mime;

import java.io.InputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.convert.ConversionException;
import com.zimbra.cs.index.Fragment;
import com.zimbra.cs.index.LuceneFields;
import com.zimbra.cs.index.IndexDocument;
import com.zimbra.cs.index.ZimbraAnalyzer;
import com.zimbra.cs.index.analysis.RFC822AddressTokenStream;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.StoreManager;

/**
 * @since Feb 15, 2006
 */
public final class ParsedDocument {
    private Blob mBlob;
    private int mSize;
    private String mDigest;
    private String mContentType;
    private String mFilename;
    private String mCreator;
    private IndexDocument mZDocument = null;
    private String mFragment;
    private long mCreatedDate;
    private String mDescription;
    private boolean mDescEnabled;

    /** if TRUE then there was a _temporary_ failure analyzing the message.  We should attempt
     * to re-index this message at a later time */
    private boolean mTemporaryAnalysisFailure = false;

    private static Blob saveInputAsBlob(InputStream in) throws ServiceException, IOException {
        return StoreManager.getInstance().storeIncoming(in, null);
    }
    
    public ParsedDocument(InputStream in, String filename, String ctype, long createdDate, String creator, String description)
        throws ServiceException, IOException {
        this(saveInputAsBlob(in), filename, ctype, createdDate, creator, description, true);
    }

    public ParsedDocument(InputStream in, String filename, String ctype, long createdDate, String creator, String description, boolean descEnabled)
    throws ServiceException, IOException {
        this(saveInputAsBlob(in), filename, ctype, createdDate, creator, description, descEnabled);
    }
    
    public ParsedDocument(Blob blob, String filename, String ctype, long createdDate, String creator, String description, boolean descEnabled)
        throws ServiceException, IOException {

        mBlob = blob;
        mSize = (int) blob.getRawSize();
        mDigest = blob.getDigest();
        mContentType = ctype;
        mFilename = StringUtil.sanitizeFilename(filename);
        mCreatedDate = createdDate;
        mCreator = creator;
        mDescription = description;
        mDescEnabled = descEnabled;

        try {
            MimeHandler handler = MimeHandlerManager.getMimeHandler(ctype, filename);
            assert(handler != null);

            if (handler.isIndexingEnabled()) {
                handler.init(new BlobDataSource(mBlob, ctype));
            }
            handler.setFilename(filename);
            handler.setPartName(LuceneFields.L_PARTNAME_TOP);
            handler.setSize(mSize);

            String textContent = "";
            try {
                textContent = handler.getContent();
            } catch (MimeHandlerException e) {
                if (ConversionException.isTemporaryCauseOf(e)) {
                    ZimbraLog.wiki.warn("Temporary failure extracting from the document.  (is convertd down?)", e);
                    mTemporaryAnalysisFailure = true;
                } else {
                    ZimbraLog.index.warn("Failure indexing wiki document "+filename+".  Item will be partially indexed", e);
                }
            }
            mFragment = Fragment.getFragment(textContent, Fragment.Source.NOTEBOOK);
            try {
                mZDocument = new IndexDocument(handler.getDocument());
                mZDocument.addSubject(filename);

                StringBuilder content = new StringBuilder();
                appendToContent(content, filename);
                appendToContent(content, ZimbraAnalyzer.getAllTokensConcatenated(LuceneFields.L_FILENAME, filename));
                appendToContent(content, textContent);
                appendToContent(content, description);

                mZDocument.addContent(content.toString());
                mZDocument.addFrom(new RFC822AddressTokenStream(creator));
                mZDocument.addFilename(filename);
            } catch (MimeHandlerException e) {
                if (ConversionException.isTemporaryCauseOf(e)) {
                    ZimbraLog.wiki.warn("Temporary failure extracting from the document.  (is convertd down?)", e);
                    mTemporaryAnalysisFailure = true;
                } else {
                    ZimbraLog.index.warn("Failure indexing wiki document " + filename + ".  Item will be partially indexed", e);
                }
            } catch (Exception e) {
                ZimbraLog.index.warn("Failure indexing wiki document " + filename + ".  Item will be partially indexed", e);
            }
        } catch (MimeHandlerException mhe) {
            throw ServiceException.FAILURE("cannot create ParsedDocument", mhe);
        }
    }

    private static final void appendToContent(StringBuilder sb, String s) {
        if (s == null)
            return;
        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(s);
    }


    public void setVersion(int version) {
        // should be indexed so we can add search constraints on the index version
        if (mZDocument == null) {
            ZimbraLog.wiki.warn("Can't index document version.  (is convertd down?)");
        } else {
            mZDocument.addVersion(version);
        }
    }

    public int getSize() {
        return mSize;
    }

    public String getDigest() {
        return mDigest;
    }

    public Blob getBlob() {
        return mBlob;
    }

    public String getFilename() {
        return mFilename;
    }

    public String getContentType() {
        return mContentType;
    }

    /**
     * Could return null if the conversion has failed.
     */
    public IndexDocument getDocument() {
        return mZDocument;
    }

    public List<IndexDocument> getDocumentList() {
        if (mZDocument == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(mZDocument);
    }

    public String getFragment() {
        return mFragment;
    }

    public String getCreator() {
        return mCreator;
    }

    public long getCreatedDate() {
        return mCreatedDate;
    }

    public String getDescription() {
        return mDescription;
    }
    
    public boolean isDescriptionEnabled() {
        return mDescEnabled;
    }

    public boolean hasTemporaryAnalysisFailure() {
        return mTemporaryAnalysisFailure;
    }

}
