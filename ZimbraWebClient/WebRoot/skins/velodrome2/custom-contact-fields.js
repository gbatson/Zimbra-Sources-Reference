/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

//
// Constants
//

VelodromeSkin.F_homeAddress = "homeAddress";
VelodromeSkin.F_otherAddress = "otherAddress";
VelodromeSkin.F_otherAnniversary = "otherAnniversary";
VelodromeSkin.F_otherAsstName = "otherAsstName";
VelodromeSkin.F_otherCustom = "otherCustom";
VelodromeSkin.F_otherDepartment = "otherDepartment";
VelodromeSkin.F_otherMgrName = "otherMgrName";
VelodromeSkin.F_otherOffice = "otherOffice";
VelodromeSkin.F_otherProfession = "otherProfession";
VelodromeSkin.F_tollFree = "tollFree";
VelodromeSkin.F_workAddress = "workAddress";
VelodromeSkin.F_workEmail = "workEmail";
VelodromeSkin.F_workIMAddress = "workIM";
VelodromeSkin.F_workMobile = "workMobile";
VelodromeSkin.F_imNone = "_NONE";
VelodromeSkin.F_imYahoo = "yahoo";
VelodromeSkin.F_imAOL = "aol";
VelodromeSkin.F_imMSN = "msn";
VelodromeSkin.F_imOther = "other";

//
// Application launch methods
//

VelodromeSkin.prototype._customContactFields_packageLoad = function() {
// console.log("VelodromeSkin.prototype._customContactFields_packageLoad");
	// Custom other contact field row item
	window.VelodromeSkinOther = function() {
		ZmEditContactViewOther.apply(this, arguments);
	};
	VelodromeSkinOther.prototype = new ZmEditContactViewOther;
	VelodromeSkinOther.prototype.constructor = VelodromeSkinOther;

	VelodromeSkinOther.prototype.DATE_ATTRS = { "birthday": true, "otherAnniversary": true };

	VelodromeSkinOther.prototype._createSelect = function() {
		var select = ZmEditContactViewInputSelect.prototype._createSelect.apply(this, arguments);
		select.addChangeListener(new AjxListener(this, this._resetPicker));
		return select;
	};

	ZmEditContactView.updateFieldLists();
	ZmEditContactView.LISTS["EMAIL"].onlyvalue = false;
	ZmEditContactView.LISTS["IM"].onlyvalue = false;
	ZmEditContactView.LISTS["VELODROME_ADDRESS"] = ZmEditContactView.LISTS["ADDRESS"];

	ZmEditContactView.ATTRS["DEPARTMENT"] = VelodromeSkin.F_otherDepartment;

	delete ZmEditContactView.LISTS["ADDRESS"];

	ZmEditContactView.ALWAYS_SHOW = {
		FIRST: true, LAST: true, TITLE: true, COMPANY: true
	};

	// override APIs
	var object = ZmEditContactView.prototype;
	this.overrideAPI(object, "getFormItems");
	this.overrideAPI(object, "getEmailOptions");
	this.overrideAPI(object, "getPhoneOptions");
	this.overrideAPI(object, "getIMOptions");
	this.overrideAPI(object, "getIMOptions2");
	this.overrideAPI(object, "getAddressOptions");
	this.overrideAPI(object, "getURLOptions");
	this.overrideAPI(object, "getOtherOptions");
	this.overrideAPI(object, "getFileAsOptions");
};

VelodromeSkin.prototype._customContactFields_basePackageLoad = function() {

	// override constants
	ZmContact.EMAIL_FIELDS = [
		ZmContact.F_email,
		VelodromeSkin.F_workEmail
	];
	ZmContact.PHONE_FIELDS = [
		ZmContact.F_mobilePhone,
		ZmContact.F_homePhone,
		ZmContact.F_otherPhone,
		ZmContact.F_homeFax,
		ZmContact.F_pager,
		ZmContact.F_workPhone,
		VelodromeSkin.F_workMobile,
		ZmContact.F_workAltPhone,
		ZmContact.F_workFax,
		ZmContact.F_assistantPhone,
		ZmContact.F_companyPhone,
		ZmContact.F_otherFax,
		VelodromeSkin.F_tollFree
	];
	ZmContact.IM_FIELDS = [
       ZmContact.F_imAddress,
       VelodromeSkin.F_workIMAddress
       /*VelodromeSkin.F_imNone,
		VelodromeSkin.F_imYahoo,
		VelodromeSkin.F_imAOL,
		VelodromeSkin.F_imMSN,
		VelodromeSkin.F_imOther*/
	];
	ZmContact.ADDRESS_FIELDS = [
		VelodromeSkin.F_homeAddress,
		VelodromeSkin.F_workAddress,
		VelodromeSkin.F_otherAddress
	];
	ZmContact.OTHER_FIELDS = [
		ZmContact.F_birthday,
		VelodromeSkin.F_otherAnniversary,
		VelodromeSkin.F_otherOffice,
		VelodromeSkin.F_otherProfession,
		VelodromeSkin.F_otherMgrName,
		VelodromeSkin.F_otherAsstName,
		VelodromeSkin.F_otherCustom
	];
	ZmContact.updateFieldConstants();

	ZmContact.IS_ADDONE[VelodromeSkin.F_workEmail] = true;
	ZmContact.IS_ADDONE[VelodromeSkin.F_workIMAddress] = true;
	ZmContact.IS_ADDONE[VelodromeSkin.F_otherCustom] = true;

	ZmContact.IGNORE_NORMALIZATION = [ZmContact.F_homePhone];
};


// NOTE: All of the methods in this section are called in the context
// NOTE: of the ZmEditContactView instance.

VelodromeSkin.prototype.getFormItems = function() {
	var items = arguments.callee.func.apply(this, arguments);
	if (!items._modified) {
		items._modified = true;
		var imDef = this.getFormItemById("IM", items);
		imDef.type = "ZmEditContactViewInputDoubleSelectRows";
		imDef.rowitem = {
			type: "ZmEditContactViewIMDouble", equals:ZmEditContactViewInputSelect.equals,
			params: { hint: ZmMsg.imScreenNameHint,  cols: 60, options: this.getIMOptions(), options2: this.getIMOptions2() }
		};
		imDef.maxrows = 4;
		var addressDef = this.getFormItemById("ADDRESS", items);
		addressDef.id = "VELODROME_ADDRESS",
		addressDef.rowitem = {
			type: "ZmEditContactViewInputSelect", equals: ZmEditContactViewInputSelect.equals,
			template: "abook.Contacts#ZmEditContactViewAddressSelect",
			params: { hint: "Address", cols: 40, rows: 3, options: this.getAddressOptions() }
		};
		var otherDef = this.getFormItemById("OTHER", items);
		otherDef.rowitem.type = "VelodromeSkinOther";
	}
	return items;
};

VelodromeSkin.prototype.getEmailOptions = function() {
//	return arguments.callee.func.apply(this, arguments);
	return [
		{ value: ZmContact.F_email, label: "Home", max: 3 },
		{ value: VelodromeSkin.F_workEmail, label: "Work", max: 3 }
	];
};
VelodromeSkin.prototype.getPhoneOptions = function() {
//	return arguments.callee.func.apply(this, arguments);
	return [
		{ value: ZmContact.F_mobilePhone, label: "Mobile", max: 1 },
		{ value: ZmContact.F_homePhone, label: "Home", max: 1 },
		{ value: ZmContact.F_otherPhone, label: "Home Alternate", max: 1 },
		{ value: ZmContact.F_homeFax, label: "Home Fax", max: 1 },
		{ value: ZmContact.F_pager, label: "Work Pager", max: 1 },
		{ value: ZmContact.F_workPhone, label: "Work", max: 1 },
		{ value: VelodromeSkin.F_workMobile, label: "Work Mobile", max: 1 },
		{ value: ZmContact.F_workAltPhone, label: "Work Alternate", max: 1 },
		{ value: ZmContact.F_workFax, label: "Work Fax", max: 1 },
		{ value: ZmContact.F_assistantPhone, label: "Assistant", max: 1 },
		{ value: ZmContact.F_companyPhone, label: "Company", max: 1 },
		{ value: ZmContact.F_otherFax, label: "Other Fax", max: 1 },
		{ value: VelodromeSkin.F_tollFree, label: "Toll Free", max: 1 }
	];
};
VelodromeSkin.prototype.getIMOptions2 = function() {
	//return arguments.callee.func.apply(this, arguments); // Display default options (zimbra/yahoo/aol/msn/other)
	return [
		{ value: VelodromeSkin.F_imNone, label: ZmMsg.none },
		{ value: VelodromeSkin.F_imYahoo, label: ZmMsg.imGateway_yahoo },
		{ value: VelodromeSkin.F_imAOL, label: ZmMsg.imGateway_aol },
		{ value: VelodromeSkin.F_imMSN, label: ZmMsg.imGateway_msn },
		{ value: VelodromeSkin.F_imOther, label: ZmMsg.other }
	];
};
VelodromeSkin.prototype.getIMOptions = function() {
// return arguments.callee.func.apply(this, arguments);
	return [
		{ value: ZmContact.F_imAddress, label: "Home", max: 2 },
		{ value: VelodromeSkin.F_workIMAddress, label: "Work", max: 2 }
	];
};
VelodromeSkin.prototype.getAddressOptions = function() {
//	return arguments.callee.func.apply(this, arguments);
	return [
		{ value: VelodromeSkin.F_homeAddress, label: "Home", max: 1 },
		{ value: VelodromeSkin.F_workAddress, label: "Work", max: 1 },
		{ value: VelodromeSkin.F_otherAddress, label: ZmMsg.other, max: 1 }
	];
};
VelodromeSkin.prototype.getURLOptions = function() {
//	return arguments.callee.func.apply(this, arguments);
	return [
		{ value: ZmContact.F_homeURL, label: "Home", max: 1 },
		{ value: ZmContact.F_workURL, label: "Work", max: 1 }
	];
};
VelodromeSkin.prototype.getOtherOptions = function() {
//	return arguments.callee.func.apply(this, arguments);
	return [
		{ value: ZmContact.F_birthday, label: "Birthday", max: 1 },
		{ value: VelodromeSkin.F_otherAnniversary, label: "Anniversary", max: 1 },
		{ value: VelodromeSkin.F_otherOffice, label: "Office", max: 1 },
		{ value: VelodromeSkin.F_otherProfession, label: "Profession", max: 1 },
		{ value: VelodromeSkin.F_otherMgrName, label: "Manager", max: 1 },
		{ value: VelodromeSkin.F_otherAsstName, label: "Assistant", max: 1 },
		{ value: VelodromeSkin.F_otherCustom, label: "Custom", max: 4 }
	];
};
VelodromeSkin.prototype.getFileAsOptions = function() {
	return arguments.callee.func.apply(this, arguments);
};

// register package listeners

AjxDispatcher.addPackageLoadFunction("Contacts", new AjxCallback(skin, skin._customContactFields_packageLoad));

AjxDispatcher.addPackageLoadFunction("ContactsCore", new AjxCallback(skin, skin._customContactFields_basePackageLoad));
