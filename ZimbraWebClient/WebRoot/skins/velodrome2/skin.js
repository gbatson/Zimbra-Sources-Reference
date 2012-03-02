/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
//
// Skin class
//
function VelodromeSkin() {
    BaseSkin.call(this, {
        // specific components
        appChooser: {direction:"LR", fullWidth:true},
        helpButton: {style:"link", container:"quota", hideIcon:true, url: "http://www.comcast.net/help/faq/index.jsp?cat=Email#SmartZone"},
        logoutButton: {style:"link", container:"quota", hideIcon:true},
        // skin regions
        quota: {containers: ["skin_td_quota"]},
        userInfo: {position: "static"},
        sidebarAd: {
            containers: function(skin, state) {
                skin._showEl("skin_sidebar_ad_outer", state);
                skin._reflowApp();
            }
        },
        fullScreen: {containers: ["skin_tr_main_full", "!skin_tr_main","!skin_td_tree_outer", "!skin_td_tree_app_sash"]},
        searchBuilder: {containers: ["skin_tr_search_builder_toolbar", "skin_tr_search_builder", "skin_td_search_builder_toolbar", "skin_td_search_builder"] },
        treeFooter: {containers: ["skin_tr_tree_footer", "skin_td_tree_footer","skin_container_tree_footer"]}
    });
}
VelodromeSkin.prototype = new BaseSkin;
VelodromeSkin.prototype.constructor = VelodromeSkin;

//
// Public methods
//

VelodromeSkin.prototype.show = function(id, visible) {
    ZmFolder.HIDE_ID[ZmFolder.ID_AUTO_ADDED] = true;

    BaseSkin.prototype.show.apply(this, arguments);

    if (id == "fullScreen") {
        // true, if unspecified
        visible = visible == null || visible;

        // swap toolbar containers
        var parentId = visible ? "skin_full_toolbar_container" : "skin_main_toolbar_container";
        var parentEl = document.getElementById(parentId);

        var toolbarId = "skin_container_app_top_toolbar";
        var toolbarEl = document.getElementById(toolbarId);

        parentEl.appendChild(toolbarEl);
    }
};

VelodromeSkin.prototype.getSidebarAdContainer = function() {
    return document.getElementById("skin_container_sidebar_ad");
};

VelodromeSkin.prototype._getPortalToolBarOps = function() {
    return [];
}

//
// Skin instance
//

skin = new VelodromeSkin();

//
// App listeners
//

skin.__handleMailLaunch = function() {
    appCtxt.set(ZmSetting.SEND_ON_BEHALF_OF, true);
};

ZmZimbraMail.addAppListener(ZmApp.MAIL, ZmAppEvent.PRE_LAUNCH, new AjxListener(skin, skin.__handleMailLaunch));

skin.__handleVoiceLaunch = function() {
    ZmVoiceApp.overviewFallbackApp      = ZmApp.MAIL;
};

ZmZimbraMail.addAppListener(ZmApp.VOICE, ZmAppEvent.PRE_LAUNCH, new AjxListener(skin, skin.__handleVoiceLaunch));

skin.__handlePortalLaunch = function() {
    this.overrideAPI(ZmPortalController.prototype, "_getToolBarOps", this._getPortalToolBarOps);
};
AjxDispatcher.addPackageLoadFunction("Portal", new AjxCallback(skin, skin.__handlePortalLaunch));

//
// utility methods
//

// Overrides a function in an object (usually a class prototype) with a new function "newfunc"
// The overridden function will be saved as the "func" attribute before we perform the call to newfunc.
// It may be called as arguments.callee.func from inside the new function (A), or as object[funcname].func from other functions (B).
// newfunc may be available to several classes (call overrideAPI on several class prototypes), and remain the same shared function object.
// Each time object[funcname] is called, the newfunc.func attribute is set to the appropriate overridden function.
// (Previous implementation overwrote newfunc.func for good, throwing overridden functions into the void)
VelodromeSkin.prototype.overrideAPI = function(object, funcname, newfunc) {
    newfunc = newfunc || this[funcname];
    if (newfunc) {
        var oldfunc = object[funcname];
        object[funcname] = function() {
            newfunc.func = oldfunc; // (A)
            return newfunc.apply(this, arguments);
        }
        object[funcname].func = oldfunc; // (B)
    }
};
/*
VelodromeSkin.prototype.overrideAPI = function(object, funcname, newfunc) {
    newfunc = newfunc || this[funcname];
    if (newfunc) {
        newfunc.func = object[funcname]; // saves reference to old func
        object[funcname] = newfunc;
    }
};*/

VelodromeSkin.prototype.setShortcut =
function(shortcut) {};
// DwtMenuItem is already loaded
skin.overrideAPI(DwtMenuItem.prototype, "setShortcut");

