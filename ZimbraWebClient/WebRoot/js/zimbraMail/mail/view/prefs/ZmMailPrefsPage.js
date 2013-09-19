/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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

ZmMailPrefsPage = function(parent, section, controller) {
	ZmPreferencesPage.apply(this, arguments);

	this._initialized = false;
};

ZmMailPrefsPage.prototype = new ZmPreferencesPage;
ZmMailPrefsPage.prototype.constructor = ZmMailPrefsPage;

ZmMailPrefsPage.prototype.toString =
function() {
	return "ZmMailPrefsPage";
};

//
// ZmPreferencesPage methods
//

ZmMailPrefsPage.prototype.showMe =
function() {
	ZmPreferencesPage.prototype.showMe.call(this);
    if(appCtxt.isOffline){
        if(this._initializedAcctId != appCtxt.getActiveAccount().id) {
            this._initialized = false;
            this._initializedAcctId = appCtxt.getActiveAccount().id;
        }
    }
	if (!this._initialized) {
		this._initialized = true;
		if (this._blackListControl && this._whiteListControl) {
			var soapDoc = AjxSoapDoc.create("GetWhiteBlackListRequest", "urn:zimbraAccount");
			var callback = new AjxCallback(this, this._handleResponseLoadWhiteBlackList);
			appCtxt.getRequestMgr().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:callback});
		}
	}
};

ZmMailPrefsPage.prototype.reset =
function(useDefaults) {
	ZmPreferencesPage.prototype.reset.apply(this, arguments);

	var cbox = this.getFormObject(ZmSetting.VACATION_MSG_ENABLED);
	if (cbox) {
		this._handleEnableVacationMsg(cbox);
	}
	this._setPopDownloadSinceControls();

	if (this._blackListControl && this._whiteListControl) {
		this._blackListControl.reset();
		this._whiteListControl.reset();
	}
};

ZmMailPrefsPage.prototype.isDirty =
function() {
	var isDirty = ZmPreferencesPage.prototype.isDirty.call(this);
	return (!isDirty) ? this.isWhiteBlackListDirty() : isDirty;
};

ZmMailPrefsPage.prototype.isWhiteBlackListDirty =
function() {
	if (this._blackListControl && this._whiteListControl) {
		return this._blackListControl.isDirty() ||
			   this._whiteListControl.isDirty();
	}
	return false;
};

ZmMailPrefsPage.prototype.addCommand =
function(batchCmd) {
	if (this.isWhiteBlackListDirty()) {
		var soapDoc = AjxSoapDoc.create("ModifyWhiteBlackListRequest", "urn:zimbraAccount");
		this._blackListControl.setSoapContent(soapDoc, "blackList");
		this._whiteListControl.setSoapContent(soapDoc, "whiteList");

		var respCallback = new AjxCallback(this, this._handleResponseModifyWhiteBlackList);
		batchCmd.addNewRequestParams(soapDoc, respCallback);
	}
};

ZmMailPrefsPage.prototype._handleResponseModifyWhiteBlackList =
function(result) {
	this._blackListControl.saveLocal();
	this._whiteListControl.saveLocal();
};

ZmMailPrefsPage.prototype._setPopDownloadSinceControls =
function() {
	var popDownloadSinceValue = this.getFormObject(ZmSetting.POP_DOWNLOAD_SINCE_VALUE);
	if (popDownloadSinceValue) {
		var value = appCtxt.get(ZmSetting.POP_DOWNLOAD_SINCE);
		if (value) {
			var date = AjxDateFormat.parse("yyyyMMddHHmmss'Z'", value);
			date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
			value = date;
		}
		var pattern = value ? ZmMsg.externalAccessPopCurrentValue : ZmMsg.externalAccessPopNotSet;
		popDownloadSinceValue.setText(AjxMessageFormat.format(pattern, value));
	}

	var popDownloadSince = this.getFormObject(ZmSetting.POP_DOWNLOAD_SINCE);
	if (popDownloadSince) {
		popDownloadSince.setSelectedValue(appCtxt.get(ZmSetting.POP_DOWNLOAD_SINCE));
	}
};

ZmMailPrefsPage.prototype._createControls =
function() {
	ZmPreferencesPage.prototype._createControls.apply(this, arguments);

	this._sId = this._htmlElId + "_startMiniCal";
	this._eId = this._htmlElId + "_endMiniCal";
	this._startDateField = Dwt.byId(this._htmlElId + "_VACATION_FROM1");
	this._endDateField = Dwt.byId(this._htmlElId + "_VACATION_UNTIL1");

	if (this._startDateField && this._endDateField) {
		this._startDateVal = Dwt.byId(this._htmlElId + "_VACATION_FROM");
		this._endDateVal = Dwt.byId(this._htmlElId + "_VACATION_UNTIL");
        if(this._startDateVal.value.length < 15){
            this._startDateVal.value = appCtxt.get(ZmSetting.VACATION_FROM);
        }
        if(this._endDateVal.value.length < 15){
            this._endDateVal.value = appCtxt.get(ZmSetting.VACATION_UNTIL);            
        }
		this._formatter = new AjxDateFormat("yyyyMMddHHmmss'Z'");

		this._startDateField.value = (this._startDateVal.value != null && this._startDateVal.value != "")
			? (AjxDateUtil.simpleComputeDateStr(this._formatter.parse(this._startDateVal.value)))
			: (AjxDateUtil.simpleComputeDateStr(new Date()));

		this._endDateField.value = (this._endDateVal.value != null && this._endDateVal.value != "")
			? (AjxDateUtil.simpleComputeDateStr(this._formatter.parse(this._endDateVal.value)))
			: (AjxDateUtil.simpleComputeDateStr(AjxDateUtil.getDateForNextDay(new Date(),AjxDateUtil.FRIDAY)));

		var dateButtonListener = new AjxListener(this, this._dateButtonListener);
		var dateCalSelectionListener = new AjxListener(this, this._dateCalSelectionListener);
		var dateFieldListener = AjxCallback.simpleClosure(this._dateFieldListener, this);

		this._startDateButton = ZmCalendarApp.createMiniCalButton(this, this._sId, dateButtonListener, dateCalSelectionListener);
		this._endDateButton = ZmCalendarApp.createMiniCalButton(this, this._eId, dateButtonListener, dateCalSelectionListener);

		Dwt.setHandler(this._startDateField, DwtEvent.ONBLUR, dateFieldListener);
		Dwt.setHandler(this._endDateField, DwtEvent.ONBLUR, dateFieldListener);

		this._startDateCheckbox = this.getFormObject(ZmSetting.START_DATE_ENABLED);
		this._endDateCheckbox = this.getFormObject(ZmSetting.END_DATE_ENABLED);
	}

	var cbox = this.getFormObject(ZmSetting.VACATION_MSG_ENABLED);

	if (cbox) {
		this._handleEnableVacationMsg(cbox);
	}

	// enable downloadSince appropriately based on presence of downloadSinceEnabled
	var downloadSinceCbox = this.getFormObject(ZmSetting.POP_DOWNLOAD_SINCE_ENABLED);
	if (downloadSinceCbox) {
		var downloadSince = this.getFormObject(ZmSetting.POP_DOWNLOAD_SINCE);
		if (downloadSince) {
			var enabled = downloadSince.getValue() != "";
			downloadSinceCbox.setSelected(enabled);
			downloadSince.setEnabled(enabled);
		}
	}

	// Break the link between the label and the radio button for MARK_READ_TIME, so that when the user clicks on the
	// text input, focus doesn't immediately go to the radio button
	var input = Dwt.byId(DwtId._makeId(ZmId.WIDGET_INPUT, ZmId.OP_MARK_READ));
	var lbl = input && input.parentNode;
	if (lbl) {
		lbl.htmlFor = "";
	}
	// If pref's value is number of seconds, populate the input
	var value = appCtxt.get(ZmSetting.MARK_MSG_READ);
	if (value > 0) {
		input.value = value;
	}

	this._setPopDownloadSinceControls();
};

ZmMailPrefsPage.prototype._dateButtonListener =
function(ev) {
	var calDate = ev.item == this._startDateButton
		? this._fixAndGetValidDateFromField(this._startDateField)
		: this._fixAndGetValidDateFromField(this._endDateField);

	var menu = ev.item.getMenu();
	var cal = menu.getItem(0);
	cal.setDate(calDate, true);
	ev.item.popup();
};

ZmMailPrefsPage.prototype._fixAndGetValidDateFromField =
function(field) {
	var d = AjxDateUtil.simpleParseDateStr(field.value);
	if (!d || isNaN(d)) {
		d = new Date();
		field.value = AjxDateUtil.simpleComputeDateStr(d);
	}
	return d;
};

ZmMailPrefsPage.prototype._dateCalSelectionListener =
function(ev) {
	var parentButton = ev.item.parent.parent;

	var newDate = AjxDateUtil.simpleComputeDateStr(ev.detail);

	if (parentButton == this._startDateButton) {
		this._startDateField.value = newDate;
	} else {
		if (ev.detail < new Date()) { return; }
		this._endDateField.value = newDate;
	}

	var sd = this._fixAndGetValidDateFromField(this._startDateField);
	var ed = this._fixAndGetValidDateFromField(this._endDateField);
	
	this._fixDates(sd, ed, parentButton == this._endDateButton);

	if (this._startDateCheckbox.isSelected()) {
		this._startDateVal.value = this._formatter.format(AjxDateUtil.simpleParseDateStr(this._startDateField.value));
	}
	if (this._endDateCheckbox.isSelected()) {
		this._endDateVal.value = this._formatter.format(AjxDateUtil.simpleParseDateStr(this._endDateField.value));
	}
};

ZmMailPrefsPage.prototype._dateFieldListener =
function(ev) {
	var sd = this._fixAndGetValidDateFromField(this._startDateField);
	var ed = this._fixAndGetValidDateFromField(this._endDateField);
	this._fixDates(sd, ed, DwtUiEvent.getTarget(ev) == this._endDateField);
	this._startDateVal.value = this._formatter.format(AjxDateUtil.simpleParseDateStr(this._startDateField.value));
	this._endDateVal.value = this._formatter.format(AjxDateUtil.simpleParseDateStr(this._endDateField.value));
};

/* Fixes the field values so that end date always is later than or equal to start date
 * @param startDate	{Date}	The value of the start date field or calendar selection
 * @param endDate	{Date}	The value of the end date field or calendar selection
 * @param modifyStart {boolean}	Whether to modify the start date or end date when dates overlap. true for start date, false for end date
*/

ZmMailPrefsPage.prototype._fixDates =
function(startDate, endDate, modifyStart) {
	if (startDate > endDate) {
		// Mismatch; start date is after end date
		if (modifyStart) {
			// Set them to be equal
			this._startDateField.value = AjxDateUtil.simpleComputeDateStr(endDate);
		} else {
			// Put endDate a bit into the future
			this._endDateField.value = AjxDateUtil.simpleComputeDateStr(AjxDateUtil.getDateForNextDay(startDate,AjxDateUtil.FRIDAY));
		}
	}
};

ZmMailPrefsPage.prototype._setupCheckbox =
function(id, setup, value) {
	var cbox = ZmPreferencesPage.prototype._setupCheckbox.apply(this, arguments);
	if (id == ZmSetting.VACATION_MSG_ENABLED ||
		id == ZmSetting.START_DATE_ENABLED ||
		id == ZmSetting.END_DATE_ENABLED)
	{
		cbox.addSelectionListener(new AjxListener(this, this._handleEnableVacationMsg, [cbox, id]));
	}
	return cbox;
};

ZmMailPrefsPage.prototype._setupRadioGroup =
function(id, setup, value) {
	var control = ZmPreferencesPage.prototype._setupRadioGroup.apply(this, arguments);
	if (id == ZmSetting.POP_DOWNLOAD_SINCE) {
		var radioGroup = this.getFormObject(id);
		var radioButton = radioGroup.getRadioButtonByValue(ZmMailApp.POP_DOWNLOAD_SINCE_NO_CHANGE);
		radioButton.setVisible(false);
	}
	return control;
};

ZmMailPrefsPage.prototype._setupCustom =
function(id, setup, value) {
	var el = document.getElementById([this._htmlElId, id].join("_"));
	if (!el) { return; }

	if (id == ZmSetting.MAIL_BLACKLIST) {
		this._blackListControl = new ZmWhiteBlackList(this, id, "BlackList");
		this._replaceControlElement(el, this._blackListControl);
	}

	if (id == ZmSetting.MAIL_WHITELIST) {
		this._whiteListControl = new ZmWhiteBlackList(this, id, "WhiteList");
		this._replaceControlElement(el, this._whiteListControl);
	}
};

ZmMailPrefsPage.prototype._handleResponseLoadWhiteBlackList =
function(result) {
	var resp = result.getResponse().GetWhiteBlackListResponse;
	this._blackListControl.loadFromJson(resp.blackList[0].addr);
	this._whiteListControl.loadFromJson(resp.whiteList[0].addr);
};


//
// Protected methods
//

ZmMailPrefsPage.prototype._handleEnableVacationMsg =
function(cbox, id, evt) {
	var textarea = this.getFormObject(ZmSetting.VACATION_MSG);
	if (textarea) {
		if (id == ZmSetting.START_DATE_ENABLED) {
			this._setEnabledStartDate(cbox.isSelected());
		} else if (id == ZmSetting.END_DATE_ENABLED) {
			this._setEnabledEndDate(cbox.isSelected());
		} else {
			var enabled = cbox.isSelected();
			textarea.setEnabled(enabled);

			this._startDateCheckbox.setEnabled(enabled);
			this._endDateCheckbox.setEnabled(enabled);

			var val = !this._startDateVal.value ? false : true;
			this._startDateCheckbox.setSelected(val);

			val = !this._endDateVal.value ? false : true;
			this._endDateCheckbox.setSelected(val);

			this._setEnabledStartDate(enabled && this._startDateCheckbox.isSelected());
			this._setEnabledEndDate(enabled && this._endDateCheckbox.isSelected());
		}
	}
};

ZmMailPrefsPage.prototype._setEnabledStartDate =
function(val) {
	var condition = val && this._startDateCheckbox.isSelected();
	this._startDateField.disabled = !condition;
	this._startDateButton.setEnabled(condition);
	this._startDateVal.value = (!condition)
		? "" : (this._formatter.format(AjxDateUtil.simpleParseDateStr(this._startDateField.value)));
};

ZmMailPrefsPage.prototype._setEnabledEndDate =
function(val) {
	//this._endDateCheckbox.setEnabled(val);
	var condition = val && this._endDateCheckbox.isSelected();
	this._endDateField.disabled = !condition;
	this._endDateButton.setEnabled(condition);
	this._endDateVal.value = (!condition)
		? "" : (this._formatter.format(AjxDateUtil.simpleParseDateStr(this._endDateField.value)));
};

ZmMailPrefsPage.prototype.getPostSaveCallback =
function() {
	return new AjxCallback(this, this._postSave);
};

ZmMailPrefsPage.prototype._postSave =
function() {
    var form = this.getFormObject(ZmSetting.POLLING_INTERVAL);
    if (form && form.getSelectedOption() && form.getSelectedOption().getDisplayValue() == ZmMsg.pollInstant && appCtxt.get(ZmSetting.INSTANT_NOTIFY)
            && !appCtxt.getAppController().getInstantNotify()){
        //turn on instant notify if not already on
        appCtxt.getAppController().setInstantNotify(true);
    } else{
        //turn instant notify off if it's on
        if (appCtxt.getAppController().getInstantNotify())
            appCtxt.getAppController().setInstantNotify(false);
    }
};

// ??? SHOULD THIS BE IN A NEW FILE?       ???
// ??? IT IS ONLY USED BY ZmMailPrefsPage. ???
/**
 * Custom control used to handle adding/removing addresses for white/black list
 *
 * @param parent
 * @param id
 *
 * @private
 */
ZmWhiteBlackList = function(parent, id, templateId) {
	DwtComposite.call(this, {parent:parent});

	this._settingId = id;
	this._tabGroup = new DwtTabGroup(this._htmlElId);
    switch(id) {
        case ZmSetting.MAIL_BLACKLIST:
            this._max = appCtxt.get(ZmSetting.MAIL_BLACKLIST_MAX_NUM_ENTRIES);
            break;
        case ZmSetting.MAIL_WHITELIST:
            this._max = appCtxt.get(ZmSetting.MAIL_WHITELIST_MAX_NUM_ENTRIES);
            break;
        case ZmSetting.TRUSTED_ADDR_LIST:
            this._max = appCtxt.get(ZmSetting.TRUSTED_ADDR_LIST_MAX_NUM_ENTRIES);
            break;
    }
	this._setContent(templateId);

	this._list = [];
	this._add = {};
	this._remove = {};
};

ZmWhiteBlackList.prototype = new DwtComposite;
ZmWhiteBlackList.prototype.constructor = ZmWhiteBlackList;

ZmWhiteBlackList.prototype.toString =
function() {
	return "ZmWhiteBlackList";
};

ZmWhiteBlackList.prototype.getTabGroupMember =
function() {
	return this._tabGroup;
};

ZmWhiteBlackList.prototype.getTabGroup = ZmWhiteBlackList.prototype.getTabGroupMember;

ZmWhiteBlackList.prototype.reset =
function() {
	this._inputEl.setValue("");
	this._listView.set(AjxVector.fromArray(this._list).clone(), null, true);
	this._add = {};
	this._remove = {};

	this.updateNumUsed();
};

ZmWhiteBlackList.prototype.getValue =
function() {
    return this._listView.getList().clone().getArray().join(',').replace(';', ',').split(',');
};


ZmWhiteBlackList.prototype.loadFromJson =
function(data) {
	if (data) {
        for (var i = 0; i < data.length; i++) {
            var content = AjxUtil.isSpecified(data[i]._content) ? data[i]._content : data[i];
            if(content){
			    var item = this._addEmail(content);
			    this._list.push(item);
            }
		}
	}
	this.updateNumUsed();
};

ZmWhiteBlackList.prototype.setSoapContent =
function(soapDoc, method) {
	if (!this.isDirty()) { return; }

	var methodEl = soapDoc.set(method);

	for (var i in this._add) {
		var addrEl = soapDoc.set("addr", i, methodEl);
		addrEl.setAttribute("op", "+");
	}

	for (var i in this._remove) {
		var addrEl = soapDoc.set("addr", i, methodEl);
		addrEl.setAttribute("op", "-");
	}
};

ZmWhiteBlackList.prototype.isDirty =
function() {
	var isDirty = false;

	for (var i in this._add) {
		isDirty = true;
		break;
	}

	if (!isDirty) {
		for (var i in this._remove) {
			isDirty = true;
			break;
		}
	}

	return isDirty;
};

ZmWhiteBlackList.prototype.saveLocal =
function() {
	if (this.isDirty()) {
		this._list = this._listView.getList().clone().getArray();
		this._add = {};
		this._remove = {};
	}
};

ZmWhiteBlackList.prototype.updateNumUsed =
function() {
	this._numUsedText.innerHTML = AjxMessageFormat.format(ZmMsg.whiteBlackNumUsed, [this._listView.size(), this._max]);
};

ZmWhiteBlackList.prototype._setContent =
function(templateId) {
	this.getHtmlElement().innerHTML = AjxTemplate.expand("prefs.Pages#"+templateId, {id:this._htmlElId});

	var id = this._htmlElId + "_EMAIL_ADDRESS";
	var el = document.getElementById(id);
	this._inputEl = new DwtInputField({parent:this, parentElement:id, size:35, hint:ZmMsg.enterEmailAddressOrDomain});
	this._inputEl.getInputElement().style.width = "210px";
	this._inputEl._showHint();
	this._inputEl.addListener(DwtEvent.ONKEYUP, new AjxListener(this, this._handleKeyUp));
	this.parent._addControlTabIndex(el, this._inputEl);

	id = this._htmlElId + "_LISTVIEW";
	el = document.getElementById(id);
	this._listView = new DwtListView({parent:this, parentElement:id});
	this._listView.addClassName("ZmWhiteBlackList");
	this.parent._addControlTabIndex(el, this._listView);

	id = this._htmlElId + "_ADD_BUTTON";
	el = document.getElementById(id);
	var addButton = new DwtButton({parent:this, parentElement:id});
	addButton.setText(ZmMsg.add);
	addButton.addSelectionListener(new AjxListener(this, this._addListener));
	this.parent._addControlTabIndex(el, addButton);

	id = this._htmlElId + "_REMOVE_BUTTON";
	el = document.getElementById(id);
	var removeButton = new DwtButton({parent:this, parentElement:id});
	removeButton.setText(ZmMsg.remove);
	removeButton.addSelectionListener(new AjxListener(this, this._removeListener));
	this.parent._addControlTabIndex(el, removeButton);

	id = this._htmlElId + "_NUM_USED";
	this._numUsedText = document.getElementById(id);
};

ZmWhiteBlackList.prototype._addEmail =
function(addr) {
	var item = new ZmWhiteBlackListItem(addr);
	this._listView.addItem(item, null, true);
	return item;
};

ZmWhiteBlackList.prototype._addListener =
function() {
	if (this._listView.size() >= this._max) {
		var dialog = appCtxt.getMsgDialog();
		dialog.setMessage(ZmMsg.errorWhiteBlackListExceeded);
		dialog.popup();
		return;
	}

	var val,
        items = AjxStringUtil.trim(this._inputEl.getValue(), true);
	if (items.length) {
        items = AjxStringUtil.split(items, [',', ';', ' ']);
        for(var i=0; i<items.length; i++) {
            val = items[i];
            if(val) {
                this._addEmail(AjxStringUtil.htmlEncode(val));
                if (!this._add[val]) {
                    this._add[val] = true;
                }
            }
        }
		this._inputEl.setValue("", true);
		this._inputEl.blur();
		this._inputEl.focus();

		this.updateNumUsed();
	}
};

ZmWhiteBlackList.prototype._removeListener =
function() {
	var items = this._listView.getSelection();
	for (var i = 0; i < items.length; i++) {
		var item = items[i];
		this._listView.removeItem(item, true);
		var addr = item.toString();
		if (this._add[addr]) {
			delete this._add[addr];
		} else {
			this._remove[addr] = true;
		}
	}

	this.updateNumUsed();
};

ZmWhiteBlackList.prototype._handleKeyUp =
function(ev) {
	var charCode = DwtKeyEvent.getCharCode(ev);
	if (charCode == 13 || charCode == 3) {
		this._addListener();
	}
};

// Helper
ZmWhiteBlackListItem = function(addr) {
	this.addr = addr;
	this.id = Dwt.getNextId();
};

ZmWhiteBlackListItem.prototype.toString =
function() {
	return this.addr;
};
