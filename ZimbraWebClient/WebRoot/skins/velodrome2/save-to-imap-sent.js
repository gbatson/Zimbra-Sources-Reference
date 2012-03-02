/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
//
// Application launch methods
//

VelodromeSkin.prototype._saveToImapSent_handlePreStartup = function() {
	appCtxt.set(ZmSetting.SAVE_TO_IMAP_SENT, true);
};

// register app listeners

ZmZimbraMail.addListener(
	ZmAppEvent.PRE_STARTUP, new AjxListener(skin, skin._saveToImapSent_handlePreStartup)
);
