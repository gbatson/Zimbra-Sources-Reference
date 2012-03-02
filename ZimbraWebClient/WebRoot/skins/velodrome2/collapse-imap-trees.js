/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
//
// Application launch methods
//

VelodromeSkin.prototype._collapseImapTrees_handlePreStartup = function() {
	appCtxt.set(ZmSetting.COLLAPSE_IMAP_TREES, true);
};

// register app listeners

ZmZimbraMail.addListener(
	ZmAppEvent.PRE_STARTUP, new AjxListener(skin, skin._collapseImapTrees_handlePreStartup)
);
