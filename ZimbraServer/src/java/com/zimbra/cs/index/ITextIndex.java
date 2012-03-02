/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;

/**
 * Currently, only the index-write APIs are fully abstracted from Lucene.  The search APIs
 * use ILuceneIndex directly.
 */
interface ITextIndex {
    
    public void beginWriteOperation() throws IOException;
    public void endWriteOperation() throws IOException;
    
    String generateIndexId(int itemId);
    
    /**
     * Store the specified MailItem in the Index.  If deleteFirst is false, then we are sure that
     * this MailItem is not already in the index, and so we can skip the check-update step.
     */
    void addDocument(IndexDocument[] docs, MailItem item, int itemId, String indexId, int modContent, 
                     long receivedDate, long size, String sortSubject, String sortSender, boolean deleteFirst) throws IOException;

    /**
     * Delete all the documents from the index that have indexIds as specified 
     */
    List<String> deleteDocuments(List<String> itemIds) throws IOException;

    /**
     * Delete this index completely.
     */
    void deleteIndex() throws IOException;

    /**
     * @return TRUE if all tokens were expanded or FALSE if more tokens were available but we hit the specified maximum
     */
    boolean expandWildcardToken(Collection<String> toRet, String field, String token, int maxToReturn) throws ServiceException;

    /**
     * Force all outstanding index writes to go through.
     */
    void flush();
    
    /**
     * @param fieldName - a lucene field (e.g. LuceneFields.L_H_CC)
     * @param collection - Strings which correspond to all of the domain terms stored in a given field.
     * @throws IOException
     */
    void getDomainsForField(String fieldName, String regex, Collection<BrowseTerm> collection) throws IOException;

    
    /**
     * @param collection - Strings which correspond to all of the attachment types in the index
     * @throws IOException
     */
    void getAttachments(String regex, Collection<BrowseTerm> collection) throws IOException;
    
    /**
     * Return the list of objects (e.g. PO, etc) from the index, for SearchBuilder browsing
     */
    void getObjects(String regex, Collection<BrowseTerm> collection) throws IOException;

    /**
     * Suggest alternate spellings for the given token 
     */
    List<SpellSuggestQueryInfo.Suggestion> suggestSpelling(String field, String token) throws ServiceException;
}
