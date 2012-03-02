/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
// Turn on provider in templates

skin.PROVIDER_ENABLED = true;

//
// Comcast.net data source
//

VelodromeSkin.PROVIDER ={
	id: "comcast-imap",
	name: "Comcast",
	type: "imap",
	connectionType: "ssl",
	host: "mail.comcast.net",
	port: 993,
	_host: "comcast.net",
	_nosender: true
};


//
// ZmComposeView method overrides
//

// NOTE: All of the methods in this section execute within the context
//       of the ZmComposeView instance object.

VelodromeSkin.prototype._getIdentityText =
function(identity) {
	var name = identity.name;
	if (identity.isDefault && name == ZmIdentity.DEFAULT_NAME) {
		name = ZmMsg.accountDefault;
	}

	// default replacement parameters
	var defaultIdentity = appCtxt.getIdentityCollection().defaultIdentity;
	var params = [
		name, identity.sendFromDisplay, identity.sendFromAddress,
		ZmMsg.accountDefault, identity.sendFromDisplay, defaultIdentity.sendFromAddress // identity.sendFromDisplay instead of appCtxt.get(ZmSetting.DISPLAY_NAME)
	];

	// get appropriate pattern
	var pattern;
	if (identity.isDefault) {
		pattern = ZmMsg.identityTextPrimary;
	}
	else if (identity.isFromDataSource) {
		var ds = appCtxt.getDataSourceCollection().getById(identity.id);
		params[1] = ds.userName;
		params[2] = ds.getEmail();		
		var provider = ZmDataSource.getProviderForAccount(ds);
		pattern = (provider && ((params[4] && params[4]!="" && ZmMsg["identityText-"+provider.id]) || (ZmMsg["identityText_fallback-"+provider.id]) ) ) || ZmMsg.identityTextExternal;
	}
	else {
		pattern = ZmMsg.identityTextPersona;
	}
	// format text
	return AjxMessageFormat.format(pattern, params);
};

//
// ZmAccountsPage method overrides
//

// NOTE: All of the methods in this section execute within the context
//	   of the ZmAccountsPage instance object.

VelodromeSkin.prototype._setDataSourceFields = function(account, section) {
	arguments.callee.func.apply(this, arguments);

	var value = account.type == ZmAccount.TYPE_POP ? "zimbra-pop" : "zimbra-imap";

	if (account.mailServer == VelodromeSkin.PROVIDER.host) {
		value = "comcast-imap";
	}

	this._setControlValue("X-TYPE", this._currentSection, value);
	this._setControlEnabled("X-TYPE", this._currentSection, Boolean(account._new));

	var displayname = appCtxt.get(ZmSetting.DISPLAY_NAME);
	var identity = account.getIdentity();
	if (!account.isMain && account._new) {
		if ((!identity.setReplyToDisplay || identity.setReplyToDisplay=="") && (displayname!=null && displayname!="")) {
			identity.setReplyToDisplay = displayname;
		}
	}
};


VelodromeSkin.prototype._setupButtons =
function() {
	arguments.callee.func.apply(this, arguments);

	// Let these specific buttons fill the width of their container (otherwise they'd have differing widths and thus be unaligned with each other)
	if (this._deleteButton)
		this._deleteButton.getHtmlElement().firstChild.style.width="auto";
	
	if (this._addExternalButton)
		this._addExternalButton.getHtmlElement().firstChild.style.width="auto";
	
	if (this._addPersonaButton)
		this._addPersonaButton.getHtmlElement().firstChild.style.width="auto";
};

VelodromeSkin.prototype._setupSelect = function(id, setup, value) {
        var select;
	if (id == "FROM_EMAIL") {
		setup.displayOptions = this._getAllAddresses();
		if (appCtxt.get(ZmSetting.ALLOW_ANY_FROM_ADDRESS)) {
		        select = this._setupComboBox(id, setup, value);
			// By setting the setSelectedValue method on the combox
			// box, it fakes the setter method of a DwtSelect.
			select.setSelectedValue = select.setValue;
			// NOTE: For this control, we always want the text value 
			select.getValue = select.getText;
		}
	}
	else if (id == "REPLY_TO_EMAIL") {
		var addresses = this._getAllAddresses(); // Get the addresses we can from _getAllAddresses()
	
		// Apparently not all (any?) external account addresses are retrieved from _getAllAddresses(), so we dig them out from DataSource and add them ourselves
		// this._accounts is not ready at this point
    		var accounts = [].concat(appCtxt.getDataSourceCollection().getImapAccounts(), appCtxt.getDataSourceCollection().getPopAccounts());
		addresses = this._getAddressesFromAccounts(accounts, addresses, true, true);
		setup.displayOptions = addresses; // Put 'em in the options list
	}
	else if (setup.displayOptions && setup.displayOptions.length < 2) {
		select = this._setupInput(id, setup, value);
		select.setEnabled(false);
		select.setSelectedValue = select.setValue;
	}
	if (!select) {
		select = ZmPreferencesPage.prototype._setupSelect.apply(this, arguments);
	}
	if (id == "SIGNATURE") {
		var collection = appCtxt.getSignatureCollection();
		collection.addChangeListener(new AjxListener(this, this._resetSignatureSelect, [select]));
		this._resetSignatureSelect(select);
	}
	if (id == "PROVIDER") {
		select.addChangeListener(new AjxListener(this, this._handleProviderChange));
	}
	
	return select;
};

VelodromeSkin.prototype._updateSelect = function(id, extras) {
	var dwtElement = this.getFormObject(id);
	if (dwtElement && AjxUtil.isFunction(dwtElement.getValue) && AjxUtil.isFunction(dwtElement.clearOptions) && AjxUtil.isFunction(dwtElement.addOption)) {
		if (id == "REPLY_TO_EMAIL") {
			if (!AjxUtil.isArray(extras))
				extras = AjxUtil.isString(extras) ? [extras] : [];

			var addresses = this._getAllAddresses().concat(extras); // Get the addresses we can from _getAllAddresses()
			// Apparently not all (any?) external account addresses are retrieved from _getAllAddresses(), so we dig them out from this._accounts
			// this._accounts is ready when the interface has been drawn
			var accounts = this._accounts.getArray();
			addresses = this._getAddressesFromAccounts(accounts, addresses, true, true);

			var value = dwtElement.getValue();
			dwtElement.clearOptions();
			for (var i=0; i<addresses.length; i++) {
				dwtElement.addOption(addresses[i], addresses[i] == value);
			}
		}
	}
}

VelodromeSkin.prototype._updateList = function(account) {
	this._updateSelect("REPLY_TO_EMAIL");
	arguments.callee.func.apply(this, arguments);
};

VelodromeSkin.prototype._updateReplyToEmail =
function(email) {
	if (AjxUtil.isEmailAddress(email))
		this._updateSelect("REPLY_TO_EMAIL", email);
	else
		this._updateSelect("REPLY_TO_EMAIL");
}

/*
 * Takes a list of accounts and extracts their email addresses
 * @param accounts	array of account objects
 * @param unique	boolean: if true, addresses will be included in output only if they are not already present. Defaults to true
 * @param valid		boolean: if true, performs a validation check on the address and only includes it if it passes. Defaults to true
 * @param addresses	optional array of addresses (as strings) to append to. Defaults to an empty array
*/
VelodromeSkin.prototype._getAddressesFromAccounts = function(accounts, addresses, unique, valid) {
	if (!addresses) addresses = [];
	unique = unique !== false;
	valid = valid !== false;
	for (var i=0; i<accounts.length; i++) {
		var account = accounts[i];
		if (account.isMain || account.enabled) {
			var address = account.getEmail();
			 // Make sure we are not adding an empty address and that we are not adding the address twice
			if (!AjxUtil.isEmpty(address) && (!valid || AjxUtil.isEmailAddress(address)) && (!unique || AjxUtil.indexOf(addresses, address, false) == -1))
				addresses.push(address);
		}
	}
	return addresses;
};

VelodromeSkin.prototype._setupRadioGroup = function(id, setup, value) {
	var container = arguments.callee.func.apply(this, arguments);
	if (id == "X-TYPE") {
		var group = this.getFormObject(id);
		group.addSelectionListener(new AjxListener(this, this._handleXTypeChange));
	}
	return container;
};

VelodromeSkin.prototype._handleXTypeChange = function() {
	var section = this._currentSection;
	var account = this._currentAccount;

	var value = this._getControlValue("X-TYPE", section);
	var isComcastAcct = value == "comcast-imap";
	var type = /-pop$/.test(value) ? ZmAccount.TYPE_POP : ZmAccount.TYPE_IMAP;

	// reset acct
	this._setControlValue("PROVIDER", section, isComcastAcct ? "comcast-imap" : "");
	this._setControlValue("ACCOUNT_TYPE", section, type);
	account.setType(type);
	account.reset();

	// set account values
	if (isComcastAcct) {
		var provider = VelodromeSkin.PROVIDER;
		for (var p in provider) {
			if (p == "id" || p == "type" || p == "name") continue;
			if (ZmDataSource.DATASOURCE_ATTRS[p]) {
				account[ZmDataSource.DATASOURCE_ATTRS[p]] = provider[p];
			}
		}
	}

	// update page
	this.setAccount(account, true, false);
};

// Will override method in ZmDataSourceCollection
VelodromeSkin.prototype.__gotoPrefSection = function(prefSectionId) {
	var controller = appCtxt.getApp(ZmApp.PREFERENCES).getPrefController();
	skin._provider_handlePrefsPostLaunch();
	controller.getPrefsView().selectSection(prefSectionId);
}


VelodromeSkin.prototype._handleEmailChange = function(evt) {
	// update email cell
	var section = this._currentSection;
	var email = this._getControlValue("EMAIL", section);
	this._updateEmailCell(email);

	// auto-fill username and host
	var m = email.match(/^(.*?)(?:@(.*))?$/);
	if (!m) return;

	var dataSource = this._currentAccount;
	if (dataSource.userName == "") {
		this._setControlValue("USERNAME", section, m[1]);
	}
	this._updateReplyToEmail(email);
};

//
// Application launch methods
//

VelodromeSkin.prototype._provider_handlePrefsPreLaunch = function() {
	if (!VelodromeSkin.__registeredProvider) {
		VelodromeSkin.__registeredProvider = true;
		ZmDataSource.addProvider(VelodromeSkin.PROVIDER);
	}
	if (window.ZmDataSourceCollection) {
		this.overrideAPI(ZmDataSourceCollection.prototype, "__gotoPrefSection");
	}
};
VelodromeSkin.prototype._provider_handleMailPreLaunch = VelodromeSkin.prototype._provider_handlePrefsPreLaunch;
VelodromeSkin.prototype._provider_handleMailCoreLoad  = VelodromeSkin.prototype._provider_handlePrefsPreLaunch;

VelodromeSkin.prototype._provider_handlePrefsPostLaunch = function() {
	if (!VelodromeSkin.__handledPostLaunch) {
		VelodromeSkin.__handledPostLaunch = true;

		// register our new preference
		ZmAccountsPage.PREFS["X-TYPE"] = {
			displayContainer: ZmPref.TYPE_RADIO_GROUP,
			orientation:	  ZmPref.ORIENT_HORIZONTAL,
			options:		  [ "zimbra-pop", "zimbra-imap", "comcast-imap" ],
			displayOptions:   [ "POP3",	   "IMAP",		"Comcast.net" ]
		};
		ZmAccountsPage.SECTIONS["EXTERNAL"].prefs.push("X-TYPE");

		ZmAccountsPage.PREFS.REPLY_TO_EMAIL.displayContainer = ZmPref.TYPE_SELECT;

		// override/add API to ZmAccountsPage
		var proto = window.ZmAccountsPage && ZmAccountsPage.prototype;
		if (proto) {
			this.overrideAPI(proto, "_setDataSourceFields");
			this.overrideAPI(proto, "_setupRadioGroup");
			this.overrideAPI(proto, "_setupButtons");
			this.overrideAPI(proto, "_setupSelect");
			this.overrideAPI(proto, "_updateSelect");
			this.overrideAPI(proto, "_updateReplyToEmail");
			this.overrideAPI(proto, "_handleXTypeChange");
			this.overrideAPI(proto, "_updateList");
			this.overrideAPI(proto, "_handleEmailChange");
			this.overrideAPI(proto, "_getAddressesFromAccounts");
		}
	}
};

VelodromeSkin.prototype._provider_handleMailLoad = function() {
	if (window.ZmComposeView) {
		this.overrideAPI(ZmComposeView.prototype, "_getIdentityText");
	}
};

// register app listeners

ZmZimbraMail.addAppListener(
	ZmApp.PREFERENCES, ZmAppEvent.PRE_LAUNCH, new AjxListener(skin, skin._provider_handlePrefsPreLaunch)
);
ZmZimbraMail.addAppListener(
	ZmApp.MAIL, ZmAppEvent.PRE_LAUNCH, new AjxListener(skin, skin._provider_handleMailPreLaunch)
);
AjxDispatcher.addPackageLoadFunction("MailCore", new AjxCallback(skin, skin._provider_handleMailCoreLoad));

ZmZimbraMail.addAppListener(
	ZmApp.PREFERENCES, ZmAppEvent.POST_LAUNCH, new AjxListener(skin, skin._provider_handlePrefsPostLaunch)
);
