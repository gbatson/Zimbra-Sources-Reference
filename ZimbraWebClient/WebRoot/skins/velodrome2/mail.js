/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

//
// ZmMailListController method overrides
//

// NOTE: All of the methods in this section execute within the context
//       of the ZmMailListController instance object.

// Initialize our own toolbar (overrides function in ZmMailListController.js)
VelodromeSkin.prototype._MailList_initializeToolBar =
function(view) {

    arguments.callee.func.call(this, view); // call overridden function

    var tb = this._toolbar[view];
    if (tb) {
        var button;

        button = tb.getButton(ZmOperation.CHECK_MAIL);
        if (button) {
            if (!appCtxt.isOffline) {
                var tooltip = (appCtxt.get(ZmSetting.GET_MAIL_ACTION) == ZmSetting.GETMAIL_ACTION_DEFAULT)
                    ? ZmMsg.checkMailPrefDefault_velodrome : ZmMsg.checkMailPrefUpdate_velodrome;
                button.setToolTipContent(tooltip);
            }
        }
        
        button = tb.getButton(ZmOperation.PRINT);
        if (button) {
            button.setText(ZmMsg.print);
        }

        if (!appCtxt.isChildWindow) {
            button = tb.getButton(ZmOperation.MOVE);
            if (button) {
                button.setText(ZmMsg.move);
            }
            button = tb.getButton(ZmOperation.DELETE_MENU);
            if (button) {
                button.setText(ZmMsg.del);
            }
            button = tb.getButton(ZmOperation.DELETE);
            if (button) {
                button.setText(ZmMsg.del);
            }

            if (AjxEnv.is800x600orLower) {
                button = tb.getButton(REPLY);
                if (button) {
                    button.setText("");
                }
                button = tb.getButton(REPLY_ALL);
                if (button) {
                    button.setText("");
                }
            }
        }
    }
};

VelodromeSkin.prototype._DoublePane_getToolBarOps =
VelodromeSkin.prototype._MailList_getToolBarOps =
function() {
    if (appCtxt.isChildWindow) {
        return [ZmOperation.PRINT, ZmOperation.CLOSE];
    }
    var list = this._getDefaultToolBarOps();
    list.push(ZmOperation.SEP, ZmOperation.VIEW_MENU);
    return list;
};

// Toolbar for single mail view (override in ZmMsgController.js and ZmConvController.js)
VelodromeSkin.prototype._Msg_getToolBarOps =
VelodromeSkin.prototype._Conv_getToolBarOps =
function() {
    if (appCtxt.isChildWindow) {
        return [ZmOperation.PRINT, ZmOperation.CLOSE];
    }
    var list = this._getDefaultToolBarOps();
    list.push(ZmOperation.TAG_MENU, ZmOperation.SEP);
    if (appCtxt.get(ZmSetting.DETACH_MAILVIEW_ENABLED)) list.push(ZmOperation.DETACH);
    return list;
};

// Common toolbar operations
VelodromeSkin.prototype._Mail_getDefaultToolBarOps =
function() {
    var list = [];
    list.push(ZmOperation.NEW_MENU, ZmOperation.CHECK_MAIL, ZmOperation.SEP, ZmOperation.REPLY, ZmOperation.REPLY_ALL, ZmOperation.FORWARD, ZmOperation.SEP, ZmOperation.EDIT);
    list.push((window.ZmConvController && this instanceof ZmConvController) ? ZmOperation.DELETE_MENU : ZmOperation.DELETE);
    list.push(ZmOperation.MOVE, ZmOperation.SPAM, ZmOperation.PRINT);
    return list;
}

// Override existing function to ensure detach option is enabled/disabled
VelodromeSkin.prototype._Mail_resetOperations =
function(parent, num) {
    arguments.callee.func.call(this, parent, num); // Call overridden function

    if (parent && parent instanceof ZmToolBar) {
        var item;
        var folderId = this._getSearchFolderId();
        if (num == 1 && (folderId != ZmFolder.ID_DRAFTS)) {
            var sel = this._listView[this._currentView].getSelection();
            if (sel && sel.length) {
                item = sel[0];
            }
        }
        var isDraft = (item && item.isDraft) || (folderId == ZmFolder.ID_DRAFTS);
        
        // Make sure the detach option is correctly enabled/disabled when an update occurs
        var btn = parent.getButton && parent.getButton(ZmOperation.VIEW_MENU);
        if (btn) {
            var menu = btn.getMenu();
            if (menu) {
                menu.enable(ZmOperation.DETACH, (appCtxt.get(ZmSetting.DETACH_MAILVIEW_ENABLED) && !isDraft && num == 1));
            }
        }
    }
};


VelodromeSkin.prototype._Mail_setupGroupByMenuItems =
function(view, menu) {
    arguments.callee.func.call(this, view, menu); // Call overridden function
    if (menu) {
        var mi = menu.createMenuItem(ZmOperation.DETACH, {image:"OpenInNewWindow", text:ZmMsg.detach});    
        mi.addSelectionListener(this._listeners[ZmOperation.DETACH]);
    }
};



// <BUG:43043>
// Overrides necessary for displaying an app chooser button with an icon
VelodromeSkin.prototype._createComcastAppIconButton = function() {
    if (!appCtxt.isChildWindow) {
        VelodromeSkin.prototype._Msg_getTabParams = 
        function(tabId, tabCallback) {
            return {id:tabId, image:"MessageView", textPrecedence:85, tooltip:ZmMsgController.DEFAULT_TAB_TEXT, tabCallback: tabCallback, hasIcon:true}; // add hasIcon attribute
        };

        VelodromeSkin.prototype._Compose_getTabParams =
        function() {
			return {id:this.tabId, image:"NewMessage", textPrecedence:75, tooltip:ZmComposeController.DEFAULT_TAB_TEXT, hasIcon:true}; // add hasIcon attribute
        };

        // A small subclass of ZmAppButton that solely defines another template
        ComcastAppIconButton = function(params) {
            if (arguments.length == 0) { return; }
            ZmAppButton.call(this, params);
        };

        ComcastAppIconButton.prototype = new ZmAppButton;
        ComcastAppIconButton.prototype.constructor = ComcastAppIconButton;

        ComcastAppIconButton.prototype.toString =
        function() {
            return "ComcastAppIconButton";
        };

        ComcastAppIconButton.prototype.TEMPLATE = "share.Widgets#ZmAppChooserIconButton"; // Refer to new template which enables the icon (see templates/dwt/Widgets.template)

        // Override method in ZmAppChooser
        VelodromeSkin.prototype._appChooser_addButton =
        function(id, params) {
            var buttonParams = {parent:this, id:ZmId.getButtonId(ZmId.APP, id), text:params.text, image:params.image, index:params.index};
            var button = (params.hasIcon) ? new ComcastAppIconButton(buttonParams) : new ZmAppButton(buttonParams); // Pick up on hasIcon and use our ComcastAppIconButton class if it is set
            button.setToolTipContent(params.tooltip);
            button.textPrecedence = params.textPrecedence;
            button.imagePrecedence = params.imagePrecedence;
            button.setData(Dwt.KEY_ID, id);
            button.addSelectionListener(this._buttonListener);
            this._buttons[id] = button;
            if (button.textPrecedence || button.imagePrecedence) {
            	this._createPrecedenceList();
            }
            this.adjustSize();
            return button;
        };
    }
}

VelodromeSkin.prototype._mail_handleStartup2Load = function() {
    if (appCtxt && !appCtxt.isChildWindow) {
        this._createComcastAppIconButton(); // Only necessary when not in child window
        var proto = window.ZmAppChooser && ZmAppChooser.prototype;
        if (proto) {
            this.overrideAPI(ZmAppChooser.prototype, "addButton", this._appChooser_addButton);
        }
    }
}

AjxDispatcher.addPackageLoadFunction("Startup2", new AjxCallback(skin, skin._mail_handleStartup2Load));
// </BUG:43043>

VelodromeSkin.prototype._mail_handleMailCoreLoad = function() {

    // override/add API to ZmDoublePaneController
    var proto = window.ZmDoublePaneController && ZmDoublePaneController.prototype
    if (proto) {
        this.overrideAPI(proto, "_getToolBarOps", this._DoublePane_getToolBarOps);
        this.overrideAPI(proto, "_getDefaultToolBarOps", this._Mail_getDefaultToolBarOps);
        this.overrideAPI(proto, "_resetOperations", this._Mail_resetOperations);
    }

    // override/add API to ZmMailListController
    var proto = window.ZmMailListController && ZmMailListController.prototype;
    if (proto) {
        this.overrideAPI(proto, "_initializeToolBar", this._MailList_initializeToolBar);
        this.overrideAPI(proto, "_getToolBarOps", this._MailList_getToolBarOps);
        this.overrideAPI(proto, "_getDefaultToolBarOps", this._Mail_getDefaultToolBarOps);
        this.overrideAPI(proto, "_resetOperations", this._Mail_resetOperations);
        this.overrideAPI(proto, "_setupGroupByMenuItems", this._Mail_setupGroupByMenuItems);
    }

	ZmOperation.NEW_ITEM_KEY[ZmOperation.NEW_MESSAGE] = "newMessage";
	ZmOperation.NEW_ITEM_KEY[ZmOperation.NEW_FOLDER] = "newFolder";
};

VelodromeSkin.prototype._mail_handleMailLoad = function() {

    // override/add API to ZmMsgController
    var proto = window.ZmMsgController && ZmMsgController.prototype;
    if (proto) {
        this.overrideAPI(proto, "_getToolBarOps", this._Msg_getToolBarOps);
        this.overrideAPI(proto, "_getDefaultToolBarOps", this._Mail_getDefaultToolBarOps);
        this.overrideAPI(proto, "_getTabParams", this._Msg_getTabParams);
    }

    // override/add API to ZmConvController
    var proto = window.ZmConvController && ZmConvController.prototype;
    if (proto) {
        this.overrideAPI(proto, "_getToolBarOps", this._Conv_getToolBarOps);
        this.overrideAPI(proto, "_getDefaultToolBarOps", this._Mail_getDefaultToolBarOps);
        this.overrideAPI(proto, "_resetOperations", this._Mail_resetOperations);
    }

	// override/add API to ZmComposeController
    var proto = window.ZmComposeController && ZmComposeController.prototype;
    if (proto) {
        this.overrideAPI(proto, "_getTabParams", this._Compose_getTabParams);
    }

};

AjxDispatcher.addPackageLoadFunction("MailCore", new AjxCallback(skin, skin._mail_handleMailCoreLoad));
AjxDispatcher.addPackageLoadFunction("Mail", new AjxCallback(skin, skin._mail_handleMailLoad));

