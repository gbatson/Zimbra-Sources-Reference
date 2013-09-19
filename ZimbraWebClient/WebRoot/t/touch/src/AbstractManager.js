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
 * @private
 */
Ext.define('Ext.AbstractManager', {

    /* Begin Definitions */

    requires: ['Ext.util.HashMap'],

    /* End Definitions */

    typeName: 'type',

    constructor: function(config) {
        Ext.apply(this, config || {});

        /**
         * @property {Ext.util.HashMap} all
         * Contains all of the items currently managed
         */
        this.all = Ext.create('Ext.util.HashMap');

        this.types = {};
    },

    /**
     * Returns an item by id.
     * For additional details see {@link Ext.util.HashMap#get}.
     * @param {String} id The `id` of the item.
     * @return {Object} The item, `undefined` if not found.
     */
    get : function(id) {
        return this.all.get(id);
    },

    /**
     * Registers an item to be managed.
     * @param {Object} item The item to register.
     */
    register: function(item) {
        this.all.add(item);
    },

    /**
     * Unregisters an item by removing it from this manager.
     * @param {Object} item The item to unregister.
     */
    unregister: function(item) {
        this.all.remove(item);
    },

    /**
     * Registers a new item constructor, keyed by a type key.
     * @param {String} type The mnemonic string by which the class may be looked up.
     * @param {Function} cls The new instance class.
     */
    registerType : function(type, cls) {
        this.types[type] = cls;
        cls[this.typeName] = type;
    },

    /**
     * Checks if an item type is registered.
     * @param {String} type The mnemonic string by which the class may be looked up.
     * @return {Boolean} Whether the type is registered.
     */
    isRegistered : function(type){
        return this.types[type] !== undefined;
    },

    /**
     * Creates and returns an instance of whatever this manager manages, based on the supplied type and
     * config object.
     * @param {Object} config The config object.
     * @param {String} defaultType If no type is discovered in the config object, we fall back to this type.
     * @return {Object} The instance of whatever this manager is managing.
     */
    create: function(config, defaultType) {
        var type        = config[this.typeName] || config.type || defaultType,
            Constructor = this.types[type];

        //<debug>
        if (Constructor == undefined) {
            Ext.Error.raise("The '" + type + "' type has not been registered with this manager");
        }
        //</debug>

        return new Constructor(config);
    },

    /**
     * Registers a function that will be called when an item with the specified id is added to the manager.
     * This will happen on instantiation.
     * @param {String} id The item `id`.
     * @param {Function} fn The callback function. Called with a single parameter, the item.
     * @param {Object} scope The scope (`this` reference) in which the callback is executed.
     * Defaults to the item.
     */
    onAvailable : function(id, fn, scope){
        var all = this.all,
            item;

        if (all.containsKey(id)) {
            item = all.get(id);
            fn.call(scope || item, item);
        } else {
            all.on('add', function(map, key, item){
                if (key == id) {
                    fn.call(scope || item, item);
                    all.un('add', fn, scope);
                }
            });
        }
    },

    /**
     * Executes the specified function once for each item in the collection.
     * @param {Function} fn The function to execute.
     * @param {String} fn.key The key of the item
     * @param {Number} fn.value The value of the item
     * @param {Number} fn.length The total number of items in the collection
     * @param {Boolean} fn.return False to cease iteration.
     * @param {Object} [scope=this] The scope to execute in.
     */
    each: function(fn, scope){
        this.all.each(fn, scope || this);
    },

    /**
     * Gets the number of items in the collection.
     * @return {Number} The number of items in the collection.
     */
    getCount: function(){
        return this.all.getCount();
    }
});
