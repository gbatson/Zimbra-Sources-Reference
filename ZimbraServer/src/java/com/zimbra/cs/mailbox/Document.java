/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/*
 * Created on Aug 23, 2004
 */
package com.zimbra.cs.mailbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.index.IndexDocument;
import com.zimbra.cs.mailbox.MetadataList;
import com.zimbra.cs.mailbox.MailItem.CustomMetadata.CustomMetadataList;
import com.zimbra.cs.mime.ParsedDocument;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

public class Document extends MailItem {
    protected String mContentType;
    protected String mCreator;
    protected String mFragment;

    public Document(Mailbox mbox, UnderlyingData data) throws ServiceException {
        super(mbox, data);
    }

    public String getContentType() {
        return mContentType;
    }

    @Override
    public String getSender() {
        return getCreator();
    }

    public String getCreator() {
        return mCreator == null ? "" : mCreator;
    }

    public String getFragment() {
    	return mFragment == null ? "" : mFragment;
    }

    @Override boolean isTaggable()      { return true; }
    @Override boolean isCopyable()      { return true; }
    @Override boolean isMovable()       { return true; }
    @Override boolean isMutable()       { return true; }
    @Override boolean isIndexed()       { return true; }
    @Override boolean canHaveChildren() { return false; }

    @Override int getMaxRevisions() throws ServiceException {
        return getAccount().getIntAttr(Provisioning.A_zimbraNotebookMaxRevisions, 0);
    }

    @Override public List<IndexDocument> generateIndexData(boolean doConsistencyCheck) throws MailItem.TemporaryIndexingException {
        ParsedDocument pd = null;
        try {
            MailboxBlob mblob = getBlob();
            if (mblob == null) {
                ZimbraLog.index.warn("Unable to fetch blob for Document id "+mId+" version "+mVersion+" on volume "+getLocator());  
                throw new MailItem.TemporaryIndexingException();
            }

            synchronized (mMailbox) {
                pd = new ParsedDocument(mblob.getLocalBlob(), getName(), getContentType(), getChangeDate(), getCreator());
                if (pd.hasTemporaryAnalysisFailure())
                    throw new MailItem.TemporaryIndexingException();
            }

            IndexDocument doc = pd.getDocument();
            if (doc != null) {
                List<IndexDocument> toRet = new ArrayList<IndexDocument>(1);
                toRet.add(doc);
                return toRet;
            } else {
                return new ArrayList<IndexDocument>(0);
            }
        } catch (IOException e) {
            ZimbraLog.index.warn("Error generating index data for Wiki Document "+getId()+". Item will not be indexed", e);
            return new ArrayList<IndexDocument>(0);
        } catch (ServiceException e) {
            ZimbraLog.index.warn("Error generating index data for Wiki Document "+getId()+". Item will not be indexed", e);
            return new ArrayList<IndexDocument>(0);
        }
    }

    @Override public void reanalyze(Object obj) throws ServiceException {
        if (!(obj instanceof ParsedDocument))
            throw ServiceException.FAILURE("cannot reanalyze non-ParsedDocument object", null);
        if ((mData.flags & Flag.BITMASK_UNCACHED) != 0)
            throw ServiceException.FAILURE("cannot reanalyze an old item revision", null);

        ParsedDocument pd = (ParsedDocument) obj;

        mContentType = pd.getContentType();
        mCreator     = pd.getCreator();
        mFragment    = pd.getFragment();
        mData.date    = (int) (pd.getCreatedDate() / 1000L);
        mData.name    = pd.getFilename();
        mData.subject = pd.getFilename();
        pd.setVersion(getVersion());

        if (mData.size != pd.getSize()) {
            markItemModified(Change.MODIFIED_SIZE);
            mMailbox.updateSize(pd.getSize() - mData.size, false);
            getFolder().updateSize(0, pd.getSize() - mData.size);
            mData.size = pd.getSize();
        }

        saveData(null);
    }

    protected static UnderlyingData prepareCreate(byte type, int id, Folder folder, String name, String mimeType,
                                                  ParsedDocument pd, Metadata meta, CustomMetadata custom) 
    throws ServiceException {
        if (folder == null || !folder.canContain(TYPE_DOCUMENT))
            throw MailServiceException.CANNOT_CONTAIN();
        if (!folder.canAccess(ACL.RIGHT_INSERT))
            throw ServiceException.PERM_DENIED("you do not have the required rights on the folder");
        name = validateItemName(name);

        CustomMetadataList extended = (custom == null ? null : custom.asList());

        Mailbox mbox = folder.getMailbox();

        UnderlyingData data = new UnderlyingData();
        data.id          = id;
        data.type        = type;
        data.folderId    = folder.getId();
        if (!folder.inSpam() || mbox.getAccount().getBooleanAttr(Provisioning.A_zimbraJunkMessagesIndexingEnabled, false))
            data.indexId = mbox.generateIndexId(id);
        data.imapId      = id;
        data.date        = (int) (pd.getCreatedDate() / 1000L);
        data.size        = pd.getSize();
        data.name        = name;
        data.subject     = name;
        data.setBlobDigest(pd.getDigest());
        data.metadata    = encodeMetadata(meta, DEFAULT_COLOR_RGB, 1, extended, mimeType, pd.getCreator(), pd.getFragment()).toString();
        return data;
    }

    static Document create(int id, Folder folder, String filename, String type, ParsedDocument pd, CustomMetadata custom)
    throws ServiceException {
        assert(id != Mailbox.ID_AUTO_INCREMENT);

        Mailbox mbox = folder.getMailbox();
        UnderlyingData data = prepareCreate(TYPE_DOCUMENT, id, folder, filename, type, pd, null, custom);
        data.contentChanged(mbox);

        ZimbraLog.mailop.info("Adding Document %s: id=%d, folderId=%d, folderName=%s.", filename, data.id, folder.getId(), folder.getName());
        DbMailItem.create(mbox, data, null);

        Document doc = new Document(mbox, data);
        doc.finishCreation(null);
        pd.setVersion(doc.getVersion());
        return doc;
    }

    @Override void decodeMetadata(Metadata meta) throws ServiceException {
        // roll forward from the old versioning mechanism (old revisions are lost)
        MetadataList revlist = meta.getList(Metadata.FN_REVISIONS, true);
        if (revlist != null && !revlist.isEmpty()) {
            try {
                Metadata rev = revlist.getMap(revlist.size() - 1);
                mCreator = rev.get(Metadata.FN_CREATOR, null);
                mFragment = rev.get(Metadata.FN_FRAGMENT, null);

                int version = (int) rev.getLong(Metadata.FN_VERSION, 1);
                if (version > 1 && rev.getLong(Metadata.FN_VERSION, 1) != 1)
                    meta.put(Metadata.FN_VERSION, version);
            } catch (ServiceException e) {
            }
        }

        super.decodeMetadata(meta);

        mContentType = meta.get(Metadata.FN_MIME_TYPE);
        mCreator     = meta.get(Metadata.FN_CREATOR, mCreator);
        mFragment    = meta.get(Metadata.FN_FRAGMENT, mFragment);
    }

    @Override Metadata encodeMetadata(Metadata meta) {
        return encodeMetadata(meta, mRGBColor, mVersion, mExtendedData, mContentType, mCreator, mFragment);
    }

    static Metadata encodeMetadata(Metadata meta, Color color, int version, CustomMetadataList extended, String mimeType, String creator, String fragment) {
        if (meta == null)
            meta = new Metadata();
        meta.put(Metadata.FN_MIME_TYPE, mimeType);
        meta.put(Metadata.FN_CREATOR, creator);
        meta.put(Metadata.FN_FRAGMENT, fragment);
        return MailItem.encodeMetadata(meta, color, version, extended);
    }


    private static final String CN_FRAGMENT  = "fragment";
    private static final String CN_MIME_TYPE = "mime_type";
    private static final String CN_FILE_NAME = "filename";
    private static final String CN_EDITOR    = "edited_by";

    @Override public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getNameForType(this)).append(": {");
        sb.append(CN_FILE_NAME).append(": ").append(getName()).append(", ");
        sb.append(CN_EDITOR).append(": ").append(getCreator()).append(", ");
        sb.append(CN_MIME_TYPE).append(": ").append(mContentType).append(", ");
        sb.append(CN_FRAGMENT).append(": ").append(mFragment);
        appendCommonMembers(sb).append(", ");
        sb.append("}");
        return sb.toString();
    }

    @Override protected boolean trackUserAgentInMetadata() {
        return true;
    }
}
