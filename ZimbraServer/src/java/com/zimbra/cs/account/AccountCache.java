/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 Zimbra, Inc.
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
 * Created on Oct 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zimbra.cs.account;

import org.apache.commons.collections.map.LRUMap;

import com.zimbra.common.stats.Counter;

public class AccountCache {
    
    private LRUMap mNameCache;
    private LRUMap mIdCache;
    private LRUMap mForeignPrincipalCache;
    private Counter mHitRate = new Counter();
    
    private long mRefreshTTL;

    static class CacheEntry {
        long mLifetime;
        Account mEntry;
        CacheEntry(Account entry, long expires) {
            mEntry = entry;
            mLifetime = System.currentTimeMillis() + expires;
        }
        
        boolean isStale() {
            return mLifetime < System.currentTimeMillis();
        }
    }
    
/**
 * @param maxItems
 * @param refreshTTL
 */
    public AccountCache(int maxItems, long refreshTTL) {
        mNameCache = new LRUMap(maxItems);
        mIdCache = new LRUMap(maxItems);
        mForeignPrincipalCache = new LRUMap(maxItems);  
        mRefreshTTL = refreshTTL;
    }

    public synchronized void clear() {
        mNameCache.clear();
        mIdCache.clear();
        mForeignPrincipalCache.clear();
    }

    public synchronized void remove(Account entry) {
        if (entry != null) {
            mNameCache.remove(entry.getName());
            mIdCache.remove(entry.getId());
            String fps[] = entry.getMultiAttr(Provisioning.A_zimbraForeignPrincipal);            
            for (String fp : fps)
                mForeignPrincipalCache.remove(fp);
        }
    }
    
    public synchronized void put(Account entry) {
        if (entry != null) {
            CacheEntry cacheEntry = new CacheEntry(entry, mRefreshTTL);
            mNameCache.put(entry.getName(), cacheEntry);
            mIdCache.put(entry.getId(), cacheEntry);
            String fps[] = entry.getMultiAttr(Provisioning.A_zimbraForeignPrincipal);            
            for (String fp : fps)
                mForeignPrincipalCache.put(fp, cacheEntry);            
        }
    }
    
    public synchronized void replace(Account entry) {
        remove(entry);
        put(entry);
    }

    private Account get(String key, LRUMap cache) {
        CacheEntry ce = (CacheEntry) cache.get(key);
        if (ce != null) {
            if (mRefreshTTL != 0 && ce.isStale()) {
                remove(ce.mEntry);
                mHitRate.increment(0);
                return null;
            } else {
                mHitRate.increment(100);
                return ce.mEntry;
            }
        } else {
            mHitRate.increment(0);
            return null;
        }
    }
    
    public synchronized Account getById(String key) {
        return get(key, mIdCache);
    }
    
    public synchronized Account getByName(String key) {
        return get(key.toLowerCase(), mNameCache);
    }
    
    public synchronized Account getByForeignPrincipal(String key) {
        return get(key, mForeignPrincipalCache);
    }
    
    public synchronized int getSize() {
        return mIdCache.size();
    }
    
    /**
     * Returns the cache hit rate as a value between 0 and 100.
     */
    public synchronized double getHitRate() {
        return mHitRate.getAverage();
    }
}

