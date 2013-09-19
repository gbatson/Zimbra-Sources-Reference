/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2013 Zimbra Software, LLC.
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

/**
 * This class provides an easy way to look up Zimbra items by ID. An item could
 * be a message, conversation, contact, folder, saved search, or tag. IDs are unique
 * across all of those. Local IDs are numeric. A compound ID will join the account
 * ID to the local ID with a colon.
 *
 * To cache something by a key other than ID, an altKey can be used. For example,
 * a folder can be cached with an altKey of 'path' and a key of 'Inbox/work'.
 *
 * @author Conrad Damon <cdamon@zimbra.com>
 */
Ext.define('ZCS.common.ZtItemCache', {

	singleton: true,

	alternateClassName: 'ZCS.cache',

	constructor: function() {
		this._cache = {};
	},

	/**
	 * Returns the item with the given key and/or altKey.
	 *
	 * @param {string}  key         item key, defaults to ID
	 * @param {string}  altKey      (optional) name of key type if not ID
	 *
	 * @return {object}     the item with the given key/altKey
	 */
	get: function(key, altKey) {
		var cache = altKey ? this._cache[altKey] : this._cache;
		return cache ? cache[key] : null;
	},

	/**
	 * Stores the item with the given key and/or altKey in the cache.
	 *
	 * @param {string}  key         item key, defaults to ID
	 * @param {object}  item        object to store
	 * @param {string}  altKey      (optional) name of key type if not ID
	 */
	set: function(key, item, altKey) {

		if (!key) {
            //<debug>
			Ext.Logger.warn('Trying to add item to item cache without a key');
            //</debug>
			return;
		}

		var cache = altKey ? this._cache[altKey] : this._cache;
		if (altKey && !cache) {
			cache = this._cache[altKey] = {};
		}

/*
		if (this.get(key, altKey) === item) {
			Ext.Logger.warn('Setting item in cache that is already there. Key: ' + key);
		}
		else if (this.get(key, altKey)) {
			Ext.Logger.warn('Overwriting item in cache. Key: ' + key);
		}
*/

		cache[key] = item;
	},

	/**
	 * Removes the item with the given key from the cache. Does not destroy the item.
	 *
	 * @param {string}  key         item key, defaults to ID
	 * @param {string}  altKey      (optional) name of key type if not ID
	 */
	remove: function(key, altKey) {
		var cache = altKey ? this._cache[altKey] : this._cache;
		cache[key] = null;
		delete cache[key];
	}
});
