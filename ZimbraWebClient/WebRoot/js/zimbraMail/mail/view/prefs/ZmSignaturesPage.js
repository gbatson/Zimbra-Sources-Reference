/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

ZmSignaturesPage = function(parent, section, controller) {

	ZmPreferencesPage.call(this, parent, section, controller);

	this._minEntries = appCtxt.get(ZmSetting.SIGNATURES_MIN);
	this._maxEntries = appCtxt.get(ZmSetting.SIGNATURES_MAX);
};
ZmSignaturesPage.prototype = new ZmPreferencesPage;
ZmSignaturesPage.prototype.constructor = ZmSignaturesPage;

ZmSignaturesPage.prototype.toString = function() {
	return "ZmSignaturesPage";
};

//
// Data
//

ZmSignaturesPage.SIGNATURE_TEMPLATE = "prefs.Pages#SignatureSplitView";

//
// Public methods
//

ZmSignaturesPage.prototype.showMe =
function() {

	ZmPreferencesPage.prototype.showMe.call(this);

	// bug fix #41719 - always resize when in multi-account mode.
	if (!this._firstTime) {
		this._resetSize();
		this._firstTime = true;
	}

	// bug fix #31849 - reset the signature html editor when in multi-account mode
	// since the view gets re-rendered whenever the account changes
	if (appCtxt.multiAccounts) {
		this._signatureEditor = null;
	}
};

ZmSignaturesPage.prototype.getAllSignatures =
function(includeEmpty, includeNonModified) {
	return [].concat(
		this.getNewSignatures(includeEmpty),
		this.getModifiedSignatures(includeNonModified)
	);
};

ZmSignaturesPage.prototype.getNewSignatures =
function(includeEmpty) {
	var array = [];
	for (var id in this._signatureComps) {
		var signature = this._signatureComps[id];
		if (!signature._new) continue;

		var hasName = signature.name.replace(/\s*/g,"") != "";
		var hasValue = signature.getValue().replace(/\s*/g,"") != "";
		var isNameDefault = ZmSignaturesPage.isNameDefault(signature.name);
                
		if ((includeEmpty && !isNameDefault) || (hasName && hasValue)) {
			array.push(signature);
		}
	}
	return array;
};

ZmSignaturesPage.prototype.getDeletedSignatures =
function() {
	var array = [];
	for (var id in this._deletedSignatures) {
		var signature = this._deletedSignatures[id];
		array.push(signature);
	}
	return array;
};

ZmSignaturesPage.prototype.getModifiedSignatures =
function(includeNonModified) {
	var array = [];
	for (var id in this._signatureComps) {
		var signature = this._signatureComps[id];
		if (signature._new) continue;

		var name = signature._orig.name;
		var value = signature._orig.value;
		var contentType = signature._orig.contentType;
		var modified = includeNonModified ||
					   (name != null && name != signature.name) ||
					   (value != null && value != signature.getValue()) ||
					   (contentType != signature.getContentType());
		if (modified) {
			array.push(signature);
		}
	}
	return array;
};

ZmSignaturesPage.prototype.reset =
function(useDefaults) {
	if (this._selSignature) {
		this._updateSignature();
	}

	ZmPreferencesPage.prototype.reset.apply(this, arguments);

	this._populateSignatures(true);
};

ZmSignaturesPage.prototype.resetOnAccountChange =
function() {
	ZmPreferencesPage.prototype.resetOnAccountChange.apply(this, arguments);
	this._selSignature = null;
	this._firstTime = false;
};

// saving

ZmSignaturesPage.prototype.isDirty =
function() {
	if (this._selSignature) {
		this._updateSignature();
	}

	return this.getNewSignatures(true).length > 0 ||
		   this.getDeletedSignatures().length > 0 ||
		   this.getModifiedSignatures().length > 0;
};


ZmSignaturesPage.defaultNameRegex = new RegExp("^"+ZmMsg.signature+"\\s#(\\d+)$", "i");
ZmSignaturesPage.isNameDefault =
function(name) {
	return name && name.match(ZmSignaturesPage.defaultNameRegex);
};

ZmSignaturesPage.prototype.validate =
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
		var isNameDefault = ZmSignaturesPage.isNameDefault(signature.name);
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

ZmSignaturesPage.prototype.getErrorMessage =
function() {
	return this._errorMsg;
};

ZmSignaturesPage.prototype.addCommand =
function(batchCommand) {
	// delete signatures
	var deletedSigs = this.getDeletedSignatures();
	for (var i = 0; i < deletedSigs.length; i++) {
		var signature = deletedSigs[i];
		var callback = new AjxCallback(this, this._handleDeleteResponse, [signature]);
		signature.doDelete(callback, null, batchCommand);
	}

	// modify signatures
	var modifiedSigs = this.getModifiedSignatures();
	for (var i = 0; i < modifiedSigs.length; i++) {
		var signature = modifiedSigs[i];
		var comps = this._signatureComps[signature._htmlElId];
		var callback = new AjxCallback(this, this._handleModifyResponse, [signature]);
		var errorCallback = new AjxCallback(this, this._handleModifyError, [signature]);
		signature.save(callback, errorCallback, batchCommand);
	}

	// add signatures
	var newSigs = this.getNewSignatures();
	for (var i = 0; i < newSigs.length; i++) {
		var signature = newSigs[i];
		signature._id = signature.id; // Clearing existing dummy id
		signature.id = null;
		var callback = new AjxCallback(this, this._handleNewResponse, [signature]);
		signature.create(callback, null, batchCommand);
	}
};

//
// Protected methods
//

ZmSignaturesPage.prototype._initialize =
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

	// Signature ADD
	var addEl = document.getElementById(this._htmlElId+"_SIG_ADD");
	var button = new DwtButton(this);
	button.setText(ZmMsg.addSignature);
	button.addSelectionListener(new AjxListener(this, this._handleAddButton));
	this._replaceControlElement(addEl, button);
	this._sigAddBtn = button;

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

ZmSignaturesPage.prototype._resetSize =
function() {
	this._resetEditorSize();

	// Make sure they are on the the same level
	var sigSize = Dwt.getSize(this._sigEditor.getHtmlElement().parentNode);
	this._sigList.setSize(Dwt.CLEAR, sigSize.y);
};

ZmSignaturesPage.prototype._resetEditorSize =
function() {
	// Adjust Size of the HTML Editor
	var size = Dwt.getSize(this._sigEditor.getHtmlElement().parentNode);
	this._sigEditor.setSize(Dwt.CLEAR, size.y);
};

ZmSignaturesPage.prototype._setupCustom =
function(id, setup, value) {
	if (id == ZmSetting.SIGNATURES) {
		// create container control
		var container = new DwtComposite(this);
		this.setFormObject(id, container);

		// create radio group for defaults
		this._defaultRadioGroup = new DwtRadioButtonGroup();

		this._initialize(container);
		// populate signatures
		this._populateSignatures();

		return container;
	}

	return ZmPreferencesPage.prototype._setupCustom.apply(this, arguments);
};

ZmSignaturesPage.prototype._selectionListener =
function(ev) {
	if (this._selSignature) {
		this._updateSignature();
	}

	var signature = this._sigList.getSelection()[0];
	if (signature) {
		this._resetSignature(this._signatureComps[signature.id]);
	}
};

ZmSignaturesPage.prototype._updateSignature =
function(select) {
	var oldSignature = this._selSignature;
	if (!oldSignature) { return; }

	var newName = this._sigName.getValue();
	var isNameModified = newName != oldSignature.name;

	oldSignature.name = newName;

	var isText = this._sigFormat ? this._sigFormat.getValue() : true;
	oldSignature.setContentType(isText ? ZmMimeTable.TEXT_PLAIN : ZmMimeTable.TEXT_HTML);

	if (!isText) {
		this._restoreSignatureInlineImages();
	}
	oldSignature.value = this._sigEditor.getContent(false, true);

	if (isNameModified) {
		this._sigList.redrawItem(oldSignature);
	}

	this._signatureComps[oldSignature.id] = oldSignature;
};

ZmSignaturesPage.prototype._populateSignatures =
function(reset) {
	this._signatureComps = {};
	this._deletedSignatures = {};
	this._selSignature = null;
	this._sigList.removeAll(true);
	this._sigList._resetList();

	var signatures = appCtxt.getSignatureCollection().getSignatures();
	var sigNames = AjxUtil.keys(signatures).sort();
	var lessThanEqual = sigNames.length <= this._maxEntries;
	var count = lessThanEqual ? sigNames.length : this._maxEntries;

	this._calcAutoSignatureNames(signatures);

	for (var i = 0; i < count; i++) {
		this._addSignature(signatures[sigNames[i]], true, reset);
	}
	for (var i = count; i < this._minEntries; i++) {
		this._addNewSignature(true);
	}

	var selectSig = this._sigList.getList().get(0);
	this._sigList.setSelection(selectSig);
};

ZmSignaturesPage.prototype._calcAutoSignatureNames =
function(signatures) {
	var autoNames = [];
	for (var i = 0; i < signatures.length; i++) {
		var match = ZmSignaturesPage.defaultNameRegex.exec(signatures[i].name);
		if (match && match.length>=2) {
			autoNames.push(match[1]);
		}
		ZmSignaturesPage.defaultNameRegex.lastIndex = 0;
	}
	autoNames.sort(function(a, b) { return a-b; });

	var newNames = [];
	for (var i = 1; i < 25; i++) { // Should be ideally appCtxt.get(ZmSetting.SIGNATURE_MAX_LENGTH).
		newNames.push(i);
	}

	if (autoNames.length > 0) {
		newNames = AjxUtil.arraySubstract(newNames, autoNames);
	}

	this._newNames = newNames;
};

ZmSignaturesPage.prototype._getNewSignatureName =
function() {
	return AjxMessageFormat.format(ZmMsg.signatureNewName, this._newNames.shift());
};

ZmSignaturesPage.prototype._getNewSignature =
function() {
	var signature = new ZmSignature(null);
	signature.id = Dwt.getNextId();
	signature.name = this._getNewSignatureName();
	signature._new = true;

	return signature;
};

ZmSignaturesPage.prototype._addNewSignature =
function(skipControls) {
	// add new signature
	var signature = this._getNewSignature();
	signature =  this._addSignature(signature, skipControls);

	return signature;
};

ZmSignaturesPage.prototype._addSignature =
function(signature, skipControls, reset, index) {
	if (!signature._new) {
		if (reset) {
			signature.name = signature._orig.name;
			signature.value = signature._orig.value;
			signature.setContentType(signature._orig.contentType);
		} else if (!signature._orig) {
			signature._orig = {
				name:  signature.name,
				value: signature.getValue(),
				contentType:  signature.getContentType()
			};
		}
	}

	this._signatureComps[signature.id] = signature;

	if (this._sigList.getItemIndex(signature) == null) {
		this._sigList.addItem(signature, index);
	}

	if (!skipControls) {
		this._resetSignature(signature); // initialize state
	}

	this._resetAddButton();

	return signature;
};

ZmSignaturesPage.prototype._fixSignatureInlineImages_onTimer =
function(msg) {
	// first time the editor is initialized, idoc.getElementsByTagName("img") is empty
	// Instead of waiting for 500ms, trying to add this callback. Risky but works.
	if (!this._firstTimeFixImages) {
		this._sigEditor.addOnContentIntializedListener(new AjxCallback(this, this._fixSignatureInlineImages));
	} else {
		this._fixSignatureInlineImages();
	}
};

ZmSignaturesPage.prototype._fixSignatureInlineImages =
function() {
	var idoc = this._sigEditor.getIframeDoc();
	if (idoc) {
		if (!this._firstTimeFixImages) {
			this._firstTimeFixImages = true;
			this._sigEditor.removeOnContentIntializedListener();
		}

		var images = idoc.getElementsByTagName("img");
		var path = appCtxt.get(ZmSetting.REST_URL) + ZmFolder.SEP;
		var img;
		for (var i = 0; i < images.length; i++) {
			img = images[i];
			var dfsrc = img.getAttribute("dfsrc");
			if (dfsrc && dfsrc.indexOf("doc:") == 0) {
				img.src = [path, dfsrc.substring(4)].join('');
			}
		}
	}
};

ZmSignaturesPage.prototype._restoreSignatureInlineImages =
function() {
	var idoc = this._sigEditor.getIframeDoc();
	if (idoc) {
		var images = idoc.getElementsByTagName("img");
		var img;
		for (var i = 0; i < images.length; i++) {
			img = images[i];
			var dfsrc = img.getAttribute("dfsrc");
			if (dfsrc && dfsrc.substring(0, 4) == "doc:") {
				img.removeAttribute("src");
			}
		}
	}
};

ZmSignaturesPage.prototype._resetSignature =
function(signature, clear) {
	this._selSignature = signature;
	this._sigList.setSelection(signature, true);
	this._sigName.setValue(signature.name);
	this._sigName._origName = signature.name;
	if (this._sigFormat) {
		this._sigFormat.setSelectedValue(signature.getContentType() == ZmMimeTable.TEXT_PLAIN);
	}

	var editorMode = (appCtxt.get(ZmSetting.HTML_COMPOSE_ENABLED) && signature.getContentType() == ZmMimeTable.TEXT_HTML)
		? DwtHtmlEditor.HTML : DwtHtmlEditor.TEXT;
	var htmlModeInited = this._sigEditor.isHtmlModeInited();
	if (editorMode != this._sigEditor.getMode()) {
		this._sigEditor.setMode(editorMode);
		this._resetEditorSize();
	}
	this._sigEditor.setContent(signature.getValue(editorMode == DwtHtmlEditor.HTML ? ZmMimeTable.TEXT_HTML : ZmMimeTable.TEXT_PLAIN));
	if (editorMode == DwtHtmlEditor.HTML) {
		this._fixSignatureInlineImages_onTimer();
	}
	this._resetDeleteButtons();
};

ZmSignaturesPage.prototype._resetAddButton =
function() {
	if (this._sigAddBtn) {
		var more = this.getAllSignatures(true, true).length < this._maxEntries;
		this._sigAddBtn.setEnabled(more);
	}
};

ZmSignaturesPage.prototype._resetDeleteButtons =
function() {
	var allSignatures = this.getAllSignatures(true, true);
	this._deleteState = allSignatures.length > this._minEntries;
	this._sigBtn.setText(this._deleteState ? ZmMsg.del : ZmMsg.clear);
};

// buttons
ZmSignaturesPage.prototype._handleFormatSelect =
function(ev) {
	var signature = this._selSignature;
	var isText = this._sigFormat ? this._sigFormat.getValue() : true;
	this._sigEditor.setMode( isText ? DwtHtmlEditor.TEXT : DwtHtmlEditor.HTML, true);

	signature.setContentType(isText ? ZmMimeTable.TEXT_HTML : ZmMimeTable.TEXT_PLAIN);

	this._resetSize();
};

ZmSignaturesPage.prototype._handleAddButton =
function(ev) {
	if (this._selSignature) {
		this._updateSignature();
	}

	this._addNewSignature();
};

ZmSignaturesPage.prototype._clearSignature =
function(signature, keepName, keepValue) {
	if (!keepName || !keepValue) {
		signature = signature || this._selSignature;
		if (!signature._orig) {
			signature._orig = {
				name:  signature.name,
				value: signature.getValue(),
				contentType:  signature.getContentType()
			};
		}
		if (!keepName && !ZmSignaturesPage.isNameDefault(signature.name))
			signature.name = this._getNewSignatureName();
		if (!keepValue)
			signature.value = "";
		this._resetSignature(signature);
	}
};

ZmSignaturesPage.prototype._deleteSignature =
function(signature) {
	signature = signature || this._selSignature;
	this._sigList.removeItem(signature);
	delete this._signatureComps[signature.id];
	if (!signature._new) {
		this._deletedSignatures[signature.id] = signature;
	}
};

ZmSignaturesPage.prototype._handleDeleteButton =
function(evt) {
	var signature = this._selSignature;

	// update controls
	if (this._deleteState) {
		this._deleteSignature();
		this._selSignature = null;

		var sel = this._sigList.getList().get(0);
		if (sel) {
			this._sigList.setSelection(sel);
		}
		this._resetAddButton();
	} else {
		this._clearSignature(signature);
	}
};

// saving

ZmSignaturesPage.prototype._handleDeleteResponse =
function(signature, resp) {
	delete this._deletedSignatures[signature.id];
};

ZmSignaturesPage.prototype._handleModifyResponse =
function(signature, resp) {
	signature._orig = {
		name:  signature.name,
		value: signature.getValue(),
		contentType:  signature.getContentType()
	};
};

ZmSignaturesPage.prototype._handleModifyError =
function(signature) {
	if (signature._orig) {
		signature.name = signature._orig.name;
		signature.value = signature._orig.value;
		signature.contentType = signature._orig.contentType;
	};
	if (this._selSignature.id == signature.id) {
		this._resetSignature(signature);
	}
	return true;
};

ZmSignaturesPage.prototype._handleNewResponse =
function(signature, resp) {
	var id = signature.id;
	signature.id = signature._id;

	var index = this._sigList.getItemIndex(signature);
	this._deleteSignature(signature);

	signature.id = id;
	this._addSignature(signature, false, false, index);

	delete signature._new;

	signature._orig = {
			name:  signature.name,
			value: signature.getValue(),
			contentType:  signature.getContentType()
	};
};


// validation

ZmSignaturesPage.prototype._updateName =
function(value) {
	var signature = this._selSignature;
	if (!signature) { return; }

	signature.name = value;
	this._sigList.redrawItem(signature);
	this._sigList.setSelection(signature, true);
};

ZmSignaturesPage.prototype._validateName =
function(value) {
	if (value.replace(/\s*/g,"") == "") {
		throw ZmMsg.errorMissingRequired;
	}

	var signature = this._selSignature;
	if (!signature) { return; }

	signature.name = value;
	this._sigList.redrawItem(signature);

	return value;
};

//
// Classes
//

//ZmSignatureListView:  Signatures List

ZmSignatureListView = function(parent) {
	if (arguments.length == 0) { return; }

	DwtListView.call(this, {parent:parent, headerList:this._getHeaderList(parent), className:"ZmSignatureListView"});
};

ZmSignatureListView.prototype = new DwtListView;
ZmSignatureListView.prototype.constructor = ZmSignatureListView;

ZmSignatureListView.SIGNATURE = "signature";

ZmSignatureListView.prototype.toString =
function() {
	return "ZmSignatureListView";
};

ZmSignatureListView.prototype._getHeaderList =
function() {
	return [(new DwtListHeaderItem({field:ZmSignatureListView.SIGNATURE, text:ZmMsg.signature}))];
};

ZmSignatureListView.prototype._getCellContents =
function(html, idx, signature, field, colIdx, params) {
	if (field == ZmSignatureListView.SIGNATURE ) {
		html[idx++] = AjxStringUtil.htmlEncode(signature.name, true);
	}
	return idx;
};

ZmSignatureListView.prototype._getItemId =
function(signature) {
	return (signature && signature.id) ? signature.id : Dwt.getNextId();
};

ZmSignatureListView.prototype.setSignatures =
function(signatures) {
	this._resetList();
	this.addItems(signatures);
	var list = this.getList();
	if (list && list.size() > 0) {
		this.setSelection(list.get(0));
	}
};


//ZmSignatureEditor

ZmSignatureEditor = function(parent) {
	ZmHtmlEditor.call(this, parent);
};

ZmSignatureEditor.prototype = new ZmHtmlEditor;
ZmSignatureEditor.prototype.constructor = ZmSignatureEditor;

ZmSignatureEditor.prototype._createToolbars =
function() {
	if (!this._toolbar1) {
		ZmHtmlEditor.prototype._createToolbars.call(this, true);

		//TODO: Need to clean up the code to follow ZCS Toolbar/Opertation model
		var tb = this._toolbar1;
		this._createFontFamilyMenu(tb);
		this._createFontSizeMenu(tb);
		this._createStyleMenu(tb);
		this._createJustifyMenu(tb);
		new DwtControl({parent:tb, className:"vertSep"});
		this._createBUIButtons(tb);
		new DwtControl({parent:tb, className:"vertSep"});
		this._createFontColorButtons(tb);
		new DwtControl({parent:tb, className:"vertSep"});
		this._createHorizRuleButton(tb);
		this._createUrlButton(tb);
		this._createUrlImageButton(tb);

        if (appCtxt.get(ZmSetting.BRIEFCASE_ENABLED)) {
            this._createImageButton(tb);
        }

		this._resetFormatControls();
	}
};

ZmSignatureEditor.prototype._createImageButton =
function(tb) {
	var button = new DwtToolBarButton({parent:tb});
	button.setImage("InsertImage");
	button.setToolTipContent(ZmMsg.insertImage);
	button.addSelectionListener(new AjxListener(this, this._insertImagesListener));
};

ZmSignatureEditor.prototype._createUrlImageButton =
function(tb) {
	var button = new DwtToolBarButton({parent:tb});
	button.setImage("ImageDoc");
	button.setToolTipContent(ZmMsg.insertImageUrl);
	button.addSelectionListener(new AjxListener(this, this._insertUrlImagesListener));
};

ZmSignatureEditor.prototype._insertUrlImagesListener =
function(ev) {
	this._getImgSelDlg().popup();
};

ZmSignatureEditor.prototype._insertImagesListener =
function(ev) {
	AjxDispatcher.require("BriefcaseCore");    
	appCtxt.getApp(ZmApp.BRIEFCASE)._createDeferredFolders();

	var callback = new AjxCallback(this, this._imageUploaded);
	var cFolder = appCtxt.getById(ZmOrganizer.ID_BRIEFCASE);
	var dialog = appCtxt.getUploadDialog();
	dialog.popup(cFolder,callback, ZmMsg.uploadImage, null, true);
};

ZmSignatureEditor.prototype._imageUploaded =
function(folder, fileNames, files) {
	for (var i=0; i< files.length; i++) {
		var file = files[i];
		var path = appCtxt.get(ZmSetting.REST_URL) + ZmFolder.SEP;
		var docPath = folder.getRestUrl() + ZmFolder.SEP + file.name;
		docPath = ["doc:", docPath.substr(docPath.indexOf(path) + path.length)].join("");

		file.docpath = docPath;
		file.rest = folder.getRestUrl() + ZmFolder.SEP + AjxStringUtil.urlComponentEncode(file.name);

		this.insertImageDoc(file);
	}
};

ZmSignatureEditor.prototype.insertImageDoc =
function(file, width, height) {
	var src = file.rest;
	if (!src) { return; }

	var doc = this._getIframeDoc();
	var img = doc.createElement("img");
	img.src = src;
	if (file.docpath) {
		img.setAttribute("dfsrc", file.docpath);
	}
	if (width) {
		img.width = width;
	} else {
		img.removeAttribute('width');
	}
	if (height) {
		img.height = height;
	} else {
		img.removeAttribute('height');
	}

	var df = doc.createDocumentFragment();
	df.appendChild(img);
	this._insertNodeAtSelection(df);
};

ZmSignatureEditor.prototype._getImgSelDlg =
function() {
	if (this._imgSelDlg) {
		this._imgSelField.value = '';
		return this._imgSelDlg;
	}

	var dlg = this._imgSelDlg = new DwtDialog(appCtxt.getShell(), null, ZmMsg.addImg);
	dlg.getButton(DwtDialog.OK_BUTTON).setText(ZmMsg.add);

	var inputId = Dwt.getNextId();
	var html = [
			"<div style='padding:5px;' class='ImgSel'>",
				"<strong>Url:&nbsp;</strong>","<input size='50' id='",inputId,"' type='text' value=''>",
			"</div>"
	].join("");
	dlg.setContent(html);

	this._imgSelField = document.getElementById(inputId);
	delete inputId;

	dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this,function() {
		var imgUrl = this._imgSelField.value;
		if(imgUrl != '')
			this.insertImage(imgUrl);
		dlg.popdown();
	}));

	return this._imgSelDlg;
};

ZmSignatureEditor.prototype.setContent =
function(content, callback) {
	this._setContentCallback = callback;
	ZmHtmlEditor.prototype.setContent.call(this, content);
};

ZmSignatureEditor.prototype._onContentInitialized =
function() {
	ZmHtmlEditor.prototype._onContentInitialized.call(this);
	if (this._setContentCallback) {
		this._setContentCallback.run();
	}
};

ZmSignatureEditor.prototype.insertLink =
function(params) {
	if (this.getContent() == "") {
		this.setContent("<br>");
	}
	ZmHtmlEditor.prototype.insertLink.call(this, params);
};

ZmSignatureEditor.prototype.getIframeDoc =
function() {
	if (!this._iframeDoc) {
		this._iframeDoc = this._getIframeDoc();
	}
	return this._iframeDoc;
};
