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
 * This base class supports a contact form field that can appear multiple times.
 * It is a container that holds one or more instances of a form field. It will
 * have plus and minus icons to add and remove instances.
 *
 * @author Conrad Damon
 */
Ext.define('ZCS.view.contacts.ZtMultiField', {

    extend: 'Ext.Container',

    config: {
        layout:     	'vbox',
	    cls: 			'multifield',
        labelName:  	'',
	    type:       	'',
	    // label of ZCS.view.contacts.ZtAddButton which is added to this container
	    addButtonLabel: '',
        // only used by "name" or "company" container type
        optionalFields: [],
        // only used by "name" or "company" container type
        visibleFields: 	[]
    },

	initialize: function() {
		// add a ZCS.view.contacts.ZtAddButton to the bottom of this container
		var addButton = Ext.create('ZCS.view.contacts.ZtAddButton',{
			text: [
	            '<div class="contact-form-add-field-button-text">',
	            	this.getAddButtonLabel(),
	            '</div>'
	        ].join(" "),

	        itemId: ZCS.util.getUniqueId({
		                type: this.getType(),
		                action: 'add'
		            }),
	        handler: function(){
	            this.up('contactpanel').fireEvent('multiAddRemove', this);
	        }
		});

		addButton.addCls('last');

		if (this.getType()!=="name" && this.getType()!=="company"){
			addButton.addCls('first');
		}

		this.add(addButton);
	},

	// Label for field type
    getLabelConfig: function() {
	    return {
	    	xtype: 'label',
	    	width: '0%'
	    }
    },

	// Plus and minus buttons for adding/removing fields
	getRemoveConfig: function(fieldId) {
		return [
			{
				xtype:      'button',
				ui: 		'plain',
				iconCls:    'contactFormMinus',
				itemId:     ZCS.util.getUniqueId({
								type:       this.getType(),
								action:     'remove',
								fieldId:    fieldId
							}),
				flex:       0,
				align:      'left',
				cls: 		"contact-form-remove-field-button", 

				handler: function() {
					this.up('contactpanel').fireEvent('multiAddRemove', this);
				}
			}
		];
	},

	addField: function(opts) {
		this.getAt(0).removeCls('first');

		var items = [],
			fieldId = ZCS.util.getUniqueId();

		items.push(this.getLabelConfig(opts));
		items.push(this.getFieldConfig(fieldId, opts));
		var config = {
			layout: 'hbox',
			items:  items,
			itemId: fieldId,
			cls: 'zcs-contact-form-multifield-field',
			opts: opts
		};

		// determine the index at which the new field will be put in the container
		var insertionIndex = this.findInsertionIndex(opts);
		// then add it
		var item = this.insert(insertionIndex,Ext.factory(config, 'Ext.Container'));

		//Manually set this property because in production build it won't get copied over.
		item.opts = opts;

		this.getAt(0).addCls('first');

		if (this.getAt(this.getItems().length-1).getHidden()){
			this.getAt(this.getItems().length-2).addCls('last');
		}
	},

	/*
	 * determine the index at which the new field will be inserted
	 * basically this method just return the index right before
	 * the ADD button, which is last item
	 */
	findInsertionIndex: function(opts){
		return this.getItems().length-1;
	},

	removeField: function(fieldId) {
		var me = this;
		Ext.Msg.confirm(
			ZtMsg.contactFormRemoveFieldConfirmationMessageHeader,
			ZtMsg.contactFormRemoveFieldConfirmationMessageBody,
			function(buttonid){
				if (buttonid == 'yes'){
					me.getAt(0).removeCls('first');
					var field = me.down('#' + fieldId);
					if (field) {
						Ext.Anim.run(field, 'slide', {
							out: true,
							direction: 'left',
							duration: 300,
							after: function(){
								// re-populate options list
								if (me.getType() === "name" || me.getType() === "company" ){
									var visibleFields = Ext.Array.clone(me.getVisibleFields());
									Ext.Array.remove(visibleFields,field.opts.order);
									me.setVisibleFields(visibleFields);

									// show ADD FIELD button again for Name and Company information fieldset
						            var addFieldButton = field.up(me.getType()+ 'container').down('addtionalFieldsAddButton');
						            if (addFieldButton && addFieldButton.isHidden()){
						                addFieldButton.show();
									}
					            }

								field.destroy();

								me.getAt(0).addCls('first');
								if (!me.getAt(me.getItems().length-1).getHidden()){
									if (me.getAt(me.getItems().length-2)){
										me.getAt(me.getItems().length-2).removeCls('last');	
									}
								}
							}
						})
					}

				}
			}
		);
	},

	reset: function() {
		// remove all EXCEPT the last items i.e. the ADD field button
		var nItems = this.getItems().length;
		for (var i=0;i<nItems-1;i++){
			this.removeAt(0);
		}
	}
});