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
 * An overview ties a TreeStore to a NestedList to represent a set of organizers (folders,
 * searches, tags).
 *
 * @author Conrad Damon <cdamon@zimbra.com>
 */
Ext.define('ZCS.view.ZtOverview', {

	extend: 'Ext.Container',

	requires: [
		'Ext.dataview.NestedList',
		'ZCS.view.ZtOrganizerList',
		'ZCS.model.ZtOrganizer',
		'ZCS.store.ZtOrganizerStore'
	],

	xtype: 'overview',

	config: {
		layout:     'vbox',
		height:     '100%',
		cls:        'zcs-overview',
		app:        null,
		title:      null,
		showEdit:   false
	},

	initialize: function() {

		this.callParent(arguments);

		// Grab all our organizers in one big list. Attempting to display the organizers as separate
		// lists (folders, searches, tags) within a scrollable container does not work. The workaround
		// is to display them in a single nested list that is grouped by organizer type.

		// get the organizer data for this app
		var me = this,
			app = this.getApp(),
			listType = ZCS.constant.ORG_LIST_OVERVIEW,
			organizerData = {
				items: ZCS.session.getOrganizerData(app, null, listType)
			};

		// create a store for the organizers
		var organizerStore = Ext.create('ZCS.store.ZtOrganizerStore', {
			storeId:    [ app, listType ].join('-'),
			data:       organizerData
		});

		// show the account name at the top of the overview
		var accountName = ZCS.session.getAccountName();

		// create the nested list that contains the grouped organizers
		var organizerList = Ext.create('ZCS.view.ZtOrganizerList', {
			flex:           1,
			title:          ZCS.constant.APP_NAME[app],
			displayField:   'displayName',
			store:          organizerStore,
			grouped:        true,
			type:           listType,
			toolbar : {
				items : [{
					xtype:      'button',
					cls:        'zcs-apps-btn',
					itemId: 	'zcs-overview-apps-btn',
					iconCls:    'apps',
					align:      'left',
					handler: function() {
						this.up('organizerlist').fireEvent('showAppsMenu');
					}
				}, {
					xtype:  'button',
					hidden: !this.config.showEdit,
					cls:    'zcs-text-btn',
					itemId: 'zcs-overview-edit-btn',
					text:   ZtMsg.edit,
					action: 'edit',
					align:  'right',
					scope:  this
				}]
			}
		});

		this.add(organizerList);

		if (this.config.showEdit) {
			var organizerEditToolbar = Ext.create('Ext.Toolbar', {
				//height: 50,
				docked: 'bottom',
				hidden: true,
				items: [{
					xtype: 'spacer'
				}, {
					text:   ZtMsg.newFolder,
					action: 'newFolder',
					cls:    'zcs-text-btn'
				}, {
					xtype: 'spacer'
				}, {
					text:   ZtMsg.newTag,
					action: 'newTag',
					cls:    'zcs-text-btn'
				}, {
					xtype: 'spacer'
				}]
			});
			this.add(organizerEditToolbar);
		}
	}
});
