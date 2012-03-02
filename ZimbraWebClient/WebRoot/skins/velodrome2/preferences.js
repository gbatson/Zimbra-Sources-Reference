/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
VelodromeSkin.prototype._SignaturesPage_initialize =
function(container) {

	container.getHtmlElement().innerHTML = AjxTemplate.expand(ZmSignaturesPage.SIGNATURE_TEMPLATE, {id:this._htmlElId});

	//Signature LIST
	var listEl = document.getElementById(this._htmlElId+"_SIG_LIST");
	var list = new ZmSignatureListView(this);
	this._replaceControlElement(listEl, list);
	list.setMultiSelect(false);
	list.addSelectionListener(new AjxListener(this, this._selectionListener));
	list.setUI(null, true); // renders headers and empty list
	this._sigList = list;

	// REMOVED Signature ADD

	// Signature Name
	var nameEl = document.getElementById(this._htmlElId+"_SIG_NAME");

	var params = {
		parent: this,
		type: DwtInputField.STRING,
		required: true,
		validationStyle: DwtInputField.CONTINUAL_VALIDATION,
		validator: AjxCallback.simpleClosure(this._updateName, this)
	};

	var input = this._sigName = new DwtInputField(params);
	this._replaceControlElement(nameEl, input);

	// Signature FORMAT
	var formatEl = document.getElementById(this._htmlElId+"_SIG_FORMAT");
	if (formatEl && appCtxt.get(ZmSetting.HTML_COMPOSE_ENABLED)) {
		var select = new DwtSelect(this);
		select.setToolTipContent(ZmMsg.formatTooltip);
		select.addOption(ZmMsg.formatAsText, 1 , true);
		select.addOption(ZmMsg.formatAsHtml, 0, false);
		select.addChangeListener(new AjxListener(this,this._handleFormatSelect));
		this._replaceControlElement(formatEl, select);
		this._sigFormat = select;
	}

	// Signature EDIT/DONE
	var actionEl = document.getElementById(this._htmlElId+"_SIG_BUTTON");
	var button = new DwtButton(this);
	button.setText(ZmMsg.del);
	button.addSelectionListener(new AjxListener(this, this._handleDeleteButton));
	this._replaceControlElement(actionEl, button);
	this._sigBtn = button;

	// Signature CONTENT
	var valueEl = document.getElementById(this._htmlElId+"_SIG_EDITOR");
	var htmlEditor = new ZmSignatureEditor(this);
	this._replaceControlElement(valueEl, htmlEditor);
	this._sigEditor = htmlEditor;
};

VelodromeSkin.prototype._SignaturesPage_validate =
function() {
	if (this._selSignature) {
		this._updateSignature();
	}

	var signatures = this.getAllSignatures(true);
	var maxLength = appCtxt.get(ZmSetting.SIGNATURE_MAX_LENGTH);
	for (var i = 0; i < signatures.length; i++) {
		var signature = signatures[i];
		var isNameEmpty = (signature.name.replace(/\s*/g,"") == "");
		var isValueEmpty = (signature.value.replace(/\s*/g,"") == "");
		var sigregex = new RegExp("^"+ZmMsg.signature+"\\s#(\\d+)$", "i");
		var isNameDefault = signature.name.match(sigregex);
		if (isNameEmpty && isValueEmpty) {
			this._deleteSignature(signature);
		} else if (isNameEmpty || (isValueEmpty && !isNameDefault)) {
			this._errorMsg = isNameEmpty ? ZmMsg.signatureNameMissingRequired : ZmMsg.signatureValueMissingRequired;
			return false;
		}
		var sigValue = signature.value;
		if (sigValue.length > maxLength) {
			this._errorMsg = AjxMessageFormat.format((signature.contentType == ZmMimeTable.TEXT_HTML)
				? ZmMsg.errorHtmlSignatureTooLong
				: ZmMsg.errorSignatureTooLong, maxLength);
			return false;
		}
	}
	return true;
};

VelodromeSkin.prototype._preferences_handlePreferencesLoad = function() {
	var proto = window.ZmSignaturesPage && ZmSignaturesPage.prototype;
    if (proto) {
        this.overrideAPI(proto, "_initialize", this._SignaturesPage_initialize);
		this.overrideAPI(proto, "validate", this._SignaturesPage_validate);
	}
};

AjxDispatcher.addPackageLoadFunction("Preferences", new AjxCallback(skin, skin._preferences_handlePreferencesLoad));

