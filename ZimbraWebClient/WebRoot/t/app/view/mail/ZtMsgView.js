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
 * This class displays a mail message using two components: a header and a body.
 *
 * @author Conrad Damon <cdamon@zimbra.com>
 */
Ext.define('ZCS.view.mail.ZtMsgView', {

	extend: 'Ext.dataview.component.ListItem',

	requires: [
		'ZCS.view.mail.ZtMsgHeader',
		'ZCS.view.mail.ZtMsgBody'
	],

	xtype: 'msgview',

	config: {

		// Using add() in an initialize method did not work for adding these components
		items: [
			{
				xtype: 'msgheader'
			}, {
				xtype: 'msgbody'
			}
		],

		msg: null,              // ZtMailMsg underlying this view

		expanded: undefined,    // true if this view is expanded (shows header and body)

		state: ZCS.constant.HDR_COLLAPSED, // Display state of this header: ZCS.constant.HDR_*

		listeners: {

			// In general, we manually render a msg view as needed, without using this event. Sometimes,
			// we need to catch this event and do some rendering (eg when the store removes a record),
			// since the support for a component-based List within Sencha Touch isn't great. Template-based
			// Lists do a much better job of keeping the view sync'ed with the store.
			//
			// Since the 'updatedata' event is triggered a lot by Sencha core code, we whitelist when we
			// want to handle it using a flag in the controller.

			updatedata: function(msgView, msgData) {

				var controller = ZCS.app.getConvController();

				if (msgView && msgData && controller.getHandleUpdateDataEvent()) {

					var msgId = msgData.id,
						msg = ZCS.cache.get(msgId),
						oldMsgView = controller.getMsgViewById(msgId),
						modelExpanded = !!msg.get('isLoaded'),
						modelState = modelExpanded ? ZCS.constant.HDR_EXPANDED : ZCS.constant.HDR_COLLAPSED;


					//<debug>
					if (oldMsgView) {
						Ext.Logger.info('updatedata for msg ' + msgId + ' ("' + msg.get('fragment') + '") from msg view ' + oldMsgView.getId() + ' into msg view ' + msgView.getId());
					}
					//</debug>

					// maintain the msg's display state - it's just getting moved to a different msg view
					if (msg) {
						msgView.setMsg(msg);
						msgView.setExpanded(oldMsgView ? oldMsgView.getExpanded() : modelExpanded);
						msgView.setState(oldMsgView ? oldMsgView.getState() : modelState);
						msgView.refreshView();
						controller.setMsgViewById(msgId, msgView);
					}
				}
			}
		}
	},

	/**
	 * Renders the given message.
	 *
	 * @param {ZtMailMsg}   msg     mail message
	 */
	render: function(msg) {

		var loaded = !!msg.get('isLoaded');

		if (msg) {
			this.setMsg(msg);
			this.setExpanded(loaded);
			this.setState(this.getExpanded() ? ZCS.constant.HDR_EXPANDED : ZCS.constant.HDR_COLLAPSED);

			this.renderHeader();
			if (loaded) {
				this.renderBody();
			}
			this.updateExpansion();
		}
	},

	/**
	 * Renders the header, which can be expanded or collapsed.
	 *
	 * @param {String}  state       ZCS.constant.HDR_*
	 */
	renderHeader: function(state) {
		this.down('msgheader').render(this.getMsg(), state || this.getState());
	},

	/**
	 * Renders the message body.
	 *
	 * @param {Boolean} showQuotedText  if true, show quoted text
	 */
	renderBody: function(showQuotedText) {

		var msgBody = this.down('msgbody');
		msgBody.on('msgContentResize', function () {
			this.updateHeight();
		}, this, {
			single: true
		});

		msgBody.render(this.getMsg(), showQuotedText);
	},

	/**
	 * Makes sure components are rendered correctly as expanded or collapsed.
	 */
	refreshView: function() {
		this.updateExpansion();
		this.renderHeader();
		this.updateHeight();
	},

	/**
	 * Displays view according to whether it is collapsed or expanded. When the view is
	 * collapsed, the body is hidden.
	 *
	 * @param {boolean} doNotRecomputeHeights		True to recompute heights, false to not recompute.
	 * @private
	 */
	updateExpansion: function() {
		var msgBody = this.down('msgbody');
		if (this.getExpanded()) {
			msgBody.show();
		} else {
			msgBody.hide();
		}
	},

	setReadOnly: function(isReadOnly) {
		this.readOnly = isReadOnly;
		if (!isReadOnly) {
			this.updateExpansion();
		}
	},

	/**
	 * Returns the width of the msg header, as that is the only child guarenteed to be
	 * rendered.  Other children, like the body, may need to know this information before
	 * they lay themselves out.
	 *
	 * @return {Number} The width of a child in the message view.
	 */
	getChildWidth: function () {
		return this.down('msgheader').element.getWidth();
	},

	updateHeight: function (doNotRecomputeHeights) {
		//Only recompute heights if this function has been called without parameters
		if (!doNotRecomputeHeights) {
			var listRef = this.up('.list');
			// Let the list know this item got updated.
			listRef.updatedItems.push(this);
			listRef.handleItemHeights();
			listRef.refreshScroller(listRef.getScrollable().getScroller());
		}
	},

	/**
	 * When an iframe, that has a parent which has a translate3d value for its transform property, receives a touch event, it
	 * incorrectly interprets the target of that event.  However, if that same parent element has absolute positioning, then
	 * the iframe does correctly interpret that event.  So when this list item gets translated to its position, and it's expanded,
	 * give it absolute positoning.
	 *
	 */
	translate: function(x, y) {


        if (!isNaN(x) && typeof x == 'number') {
            this.x = x;
        }

        if (!isNaN(y) && typeof y == 'number') {
            this.y = y;
        }

		// Sencha hides a list item by scrolling it up 10000. That doesn't work for a very large
		// message, so move it left as well to make sure it's offscreen.
		if (this.y === -10000) {
			this.x = this.y;
		}

        if (this.element.forceAbsolutePositioning) {
            //In case the element was not forced before.
            this.element.dom.style.position = "absolute";
            this.element.dom.style.webkitTransform = 'translate3d(0px, 0px, 0px)';
            this.element.dom.style.top = this.y + 'px';
        } else {
        	if (this.element.dom.style.top) {
        		this.element.dom.style.top = '0px';
        	}
        	this.element.translate(x, y);
        }
    }

});
