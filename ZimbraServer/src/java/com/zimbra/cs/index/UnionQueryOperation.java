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
 * Created on Oct 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zimbra.cs.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.SetUtil;

import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;


/************************************************************************
 * 
 * UnionQueryOperation
 *
 * 
 * -- a list of query operations which are unioned together.
 * 
 ***********************************************************************/
class UnionQueryOperation extends CombiningQueryOperation
{
    private static Log mLog = LogFactory.getLog(UnionQueryOperation.class);

    private boolean atStart = true; // don't re-fill buffer twice if they call hasNext() then reset() w/o actually getting next

    QueryTargetSet getQueryTargets() {
        QueryTargetSet toRet = new QueryTargetSet();

        for (QueryOperation op : mQueryOperations) {
            toRet = (QueryTargetSet)SetUtil.union(toRet, op.getQueryTargets());
        }
        return toRet;
    }

    /******************
     * 
     * Hits iteration
     *
     *******************/
    public void resetIterator() throws ServiceException {
        if (!atStart) {
            for (Iterator iter = mQueryOperations.iterator(); iter.hasNext(); )
            {
                QueryOperation q = (QueryOperation)iter.next();
                q.resetIterator();
            }
            mCachedNextHit = null;
            internalGetNext();
        }
    }

    private ZimbraHit mCachedNextHit = null;
    
    public ZimbraHit getNext() throws ServiceException {
        atStart = false;
        ZimbraHit toRet = mCachedNextHit;
        if (mCachedNextHit != null) { // this "if" is here so we don't keep calling internalGetNext when we've reached the end of the results...
            mCachedNextHit = null;
            internalGetNext();
        }

        return toRet;
    }

    public ZimbraHit peekNext() throws ServiceException
    {
        return mCachedNextHit;
    }

    private void internalGetNext() throws ServiceException
    {
        if (mCachedNextHit == null) {
            
            if (getResultsSet().getSortBy() == SortBy.NONE) {
                for (QueryOperation op : mQueryOperations) {
                    mCachedNextHit = op.getNext();
                    if (mCachedNextHit != null)
                        return;
                }
                // no more results!

            } else {
                // mergesort: loop through QueryOperations and find the "best" hit
                int currentBestHitOffset = -1;
                ZimbraHit currentBestHit = null;
                for (int i = 0; i < mQueryOperations.size(); i++) {
                    QueryOperation op = (QueryOperation)(mQueryOperations.get(i));
                    if (op.hasNext()) {
                        if (currentBestHitOffset == -1) {
                            currentBestHitOffset = i;
                            currentBestHit = op.peekNext();
                        } else {
                            ZimbraHit opNext = op.peekNext();
                            int result = opNext.compareBySortField(getResultsSet().getSortBy(), currentBestHit);
                            if (result < 0) {
                                // "before"
                                currentBestHitOffset = i;
                                currentBestHit = opNext;
                            }
                        }
                    }
                }
                if (currentBestHitOffset > -1) {
                    mCachedNextHit = ((QueryOperation)(mQueryOperations.get(currentBestHitOffset))).getNext();
                    assert(mCachedNextHit == currentBestHit);
                }
            }
        }
    }


    public void doneWithSearchResults() throws ServiceException {
        for (Iterator iter = mQueryOperations.iterator(); iter.hasNext(); )
        {
            QueryOperation q = (QueryOperation)iter.next();
            q.doneWithSearchResults();
        }
    }

//    ArrayList<QueryOperation>mQueryOperations = new ArrayList<QueryOperation>();

    public boolean hasSpamTrashSetting() {
        boolean hasAll = true;
        for (Iterator iter = mQueryOperations.iterator(); hasAll && iter.hasNext(); )
        {
            QueryOperation op = (QueryOperation)iter.next();
            hasAll = op.hasSpamTrashSetting();
        }
        return hasAll;
    }

    void forceHasSpamTrashSetting() {
        for (Iterator iter = mQueryOperations.iterator(); iter.hasNext(); )
        {
            QueryOperation op = (QueryOperation)iter.next();
            op.forceHasSpamTrashSetting();
        }
    }

    QueryTarget getQueryTarget(QueryTarget targetOfParent) {
        return targetOfParent;
    }


    boolean hasNoResults() {
        return false;
    }
    boolean hasAllResults() {
        return false;
    }
    
    QueryOperation expandLocalRemotePart(Mailbox mbox) throws ServiceException {
        ArrayList<QueryOperation> newList = new ArrayList<QueryOperation>();
        for (QueryOperation op : mQueryOperations) {
            newList.add(op.expandLocalRemotePart(mbox));
        }
        mQueryOperations = newList;
        return this;
    }
    
    QueryOperation ensureSpamTrashSetting(Mailbox mbox, boolean includeTrash, boolean includeSpam) throws ServiceException
    {
        ArrayList<QueryOperation> newList = new ArrayList<QueryOperation>();

        for (Iterator iter = mQueryOperations.iterator(); iter.hasNext(); )
        {
            QueryOperation op = (QueryOperation)iter.next();
            if (!op.hasSpamTrashSetting()) {
                newList.add(op.ensureSpamTrashSetting(mbox, includeTrash, includeSpam));
            } else {
                newList.add(op);
            }
        }
        assert(newList.size() == mQueryOperations.size());
        mQueryOperations = newList;
        return this;
    }


    public void add(QueryOperation op) {
        mQueryOperations.add(op);
    }

    void pruneIncompatibleTargets(QueryTargetSet targets) {
        // go from end--front so we don't get confused when entries are removed
        for (int i = mQueryOperations.size()-1; i >= 0; i--) {
            QueryOperation op = mQueryOperations.get(i);
            if (op instanceof UnionQueryOperation) {
                assert(false); // shouldn't be here, should have optimized already
                ((UnionQueryOperation)op).pruneIncompatibleTargets(targets);
            } else if (op instanceof IntersectionQueryOperation) {
                ((IntersectionQueryOperation)op).pruneIncompatibleTargets(targets);
            } else {
                QueryTargetSet qts = op.getQueryTargets();
                assert(qts.size() <= 1);
                if ((qts.size() == 0) || (!qts.isSubset(targets) && !qts.contains(QueryTarget.UNSPECIFIED))) {
                    mQueryOperations.remove(i);
                }
            }
        }
    }

    public QueryOperation optimize(Mailbox mbox) throws ServiceException {
        restartSubOpt:
            do {
                for (Iterator iter = mQueryOperations.iterator(); iter.hasNext(); )
                {
                    QueryOperation q = (QueryOperation)iter.next();
                    QueryOperation newQ = q.optimize(mbox);
                    if (newQ != q) {
                        iter.remove();
                        if (newQ != null) {
                            mQueryOperations.add(newQ);
                        }
                        continue restartSubOpt;
                    }
                }
                break;
            } while(true);

    if (mQueryOperations.size() == 0) {
        return new NoTermQueryOperation();
    }

    outer:
        do {
            for (int i = 0; i < mQueryOperations.size(); i++) {
                QueryOperation lhs = (QueryOperation)mQueryOperations.get(i);

                // if one of our direct children is an OR, then promote all of its
                // elements to our level -- this can happen if a subquery has
                // ORed terms at the top level
                if (lhs instanceof UnionQueryOperation) {
                    combineOps(lhs, true);
                    mQueryOperations.remove(i);
                    continue outer;
                }

                for (int j = i+1; j < mQueryOperations.size(); j++) {
                    QueryOperation rhs = (QueryOperation)mQueryOperations.get(j);
                    QueryOperation joined = lhs.combineOps(rhs,true);
                    if (joined != null) {
                        mQueryOperations.remove(j);
                        mQueryOperations.remove(i);
                        mQueryOperations.add(joined);
                        continue outer;
                    }
                }
            }
            break;
        } while(true);


    // now - check to see if we have only one child -- if so, then WE can be
    // eliminated, so push the child up
    if (mQueryOperations.size() == 1) {
        return (QueryOperation)mQueryOperations.get(0);
    }

    return this;
    }

    String toQueryString() {
        StringBuilder ret = new StringBuilder("(");

        boolean atFirst = true;

        for (QueryOperation op : mQueryOperations) {
            if (!atFirst)
                ret.append(" OR ");

            ret.append(op.toQueryString());
            atFirst = false;
        }

        ret.append(')');
        return ret.toString();
    }

    public String toString() {
        StringBuilder retval = new StringBuilder("UNION{");
        
        boolean atFirst = true;

        for (int i = 0; i < mQueryOperations.size(); i++) {
            if (atFirst)
                atFirst = false;
            else
                retval.append(" OR ");
            
            retval.append(mQueryOperations.get(i).toString());
        }
        retval.append("}");
        return retval.toString();
    }

    public Object clone() {
        UnionQueryOperation toRet = null;
        toRet = (UnionQueryOperation)super.clone();

        assert(mCachedNextHit == null);

        toRet.mQueryOperations = new ArrayList<QueryOperation>(mQueryOperations.size());
        for (QueryOperation q : mQueryOperations)
            toRet.mQueryOperations.add((QueryOperation)(q.clone()));

        return toRet;
    }
    
    protected QueryOperation combineOps(QueryOperation other, boolean union) {
//        if (mLog.isDebugEnabled()) {
//            mLog.debug("combineOps("+toString()+","+other.toString()+")");
//        }
        if (union && other instanceof UnionQueryOperation) {
            mQueryOperations.addAll(((UnionQueryOperation)other).mQueryOperations);
            return this;
        }
        return null;
    }


    protected void prepare(Mailbox mbx, ZimbraQueryResultsImpl res, MailboxIndex mbidx, SearchParams params, int chunkSize) throws ServiceException, IOException
    {
        mParams = params;
        this.setupResults(mbx, res);

        for (int i = 0; i < mQueryOperations.size(); i++) {
            QueryOperation qop = (QueryOperation)mQueryOperations.get(i);
            if (mLog.isDebugEnabled()) {
                mLog.debug("Executing: "+qop.toString());
            }
            qop.prepare(mbx, res, mbidx, mParams, chunkSize+1); // add 1 to chunksize b/c we buffer
        }

        internalGetNext();
    }
    
    public List<QueryInfo> getResultInfo() { 
        List<QueryInfo> toRet = new ArrayList<QueryInfo>();
        for (QueryOperation op : mQueryOperations) {
            toRet.addAll(op.getResultInfo());
        }
        return toRet;
    }
    
    public int estimateResultSize() throws ServiceException {
        int total = 0;
        for (QueryOperation qop : mQueryOperations) {
            // assume ORed terms are independent for now
            total += qop.estimateResultSize();
        }
        return total;
    }
    
    protected void depthFirstRecurse(RecurseCallback cb) {
        for (int i = 0; i < mQueryOperations.size(); i++) {
            QueryOperation op = (QueryOperation) mQueryOperations.get(i);
            op.depthFirstRecurse(cb);
        }
        cb.recurseCallback(this);
    }
}