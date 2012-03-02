AjxTemplate.register("prefs.Pages#General", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<!-- xr-feat-167 --><div class='PrefsTitleContainer'><div class='PrefsTitle'><table cellpadding=\"0\" cellspacing=\"0\"><tr><td class='PrefsTitleText'>General Preferences</td></tr></table></div><div class='PrefsRightAction'><table class='ZOptionsDefaultsTable'><tr><td class='ZOptionsDefaultsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REVERT_PAGE' tabindex='10'></div></td></tr></table></div></div><!-- End xr-feat-167 --><table cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>\n";
	buffer[_i++] = "\t\t\t\tXFINITY Connect&nbsp;&nbsp;<a href='http://ccqa4.comcast.com/Pages/FAQViewer.aspx?Guid=203ecc6a_686e_4f2d_be60_c0e21ba8b352' target='_blank'>Help</a>\n";
	buffer[_i++] = "\t\t\t</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain'><table width='100%'><tr><td class='ZOptionsLabelTop'>Version:</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_CLIENT_TYPE' tabindex='20'></div></td></tr></table></td></tr></table>";
	 if (data.isEnabled(ZmSetting.MAIL_ENABLED)) { 
	buffer[_i++] = "<table cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>";
	buffer[_i++] = ZmMsg.searches;
	buffer[_i++] = "</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain'><table width='100%'><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.searchSettingsLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SEARCH_INCLUDES_SPAM' tabindex='30' type='checkbox'></td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SEARCH_INCLUDES_TRASH' tabindex='40' type='checkbox'></td></tr></table></td></tr></table>";
	 } 

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#General"
}, true);
AjxTemplate.register("prefs.Pages", AjxTemplate.getTemplate("prefs.Pages#General"), AjxTemplate.getParams("prefs.Pages#General"));

AjxTemplate.register("prefs.Pages#Mail", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<!-- xr-feat-167 --><div class='PrefsTitleContainer'><div class='PrefsTitle'><table cellpadding=\"0\" cellspacing=\"0\"><tr><td class='PrefsTitleText'>Email Preferences</td></tr></table></div><div class='PrefsRightAction'><table class='ZOptionsDefaultsTable'><tr><td class='ZOptionsDefaultsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REVERT_PAGE' tabindex='10'></div></td></tr></table></div></div><!-- End xr-feat-167 --><table cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>Settings Managed on comcast.net</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain ZOptionsSectionMainLinks'><p><a tabindex='12' target='_new' href='https://ccqa4.comcast.com/SmartUrl/Secure/Users.aspx'>My Account Homepage</a></p><p><a tabindex='13' target='_new' href='https://ccqa4.comcast.com/SmartUrl/Secure/restrictEmail'>Restrict Incoming Email</a></p><p><a tabindex='14' target='_new' href='https://ccqa4.comcast.com/SmartUrl/Secure/spamFilter'>Spam Filtering / Confirmation</a></p><p><a tabindex='15' target='_new' href='https://ccqa4.comcast.com/SmartUrl/Secure/autoReply'>Auto Reply</a></p><p><a tabindex='16' target='_new' href='https://ccqa4.comcast.com/SmartUrl/Secure/autoForward'>Auto Forward</a></p></td></tr></table><table cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>";
	buffer[_i++] = ZmMsg.display;
	buffer[_i++] = "</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain'><table width='100%'>";
		
	buffer[_i++] = "<tr><td class='ZOptionsLabelTop'>";
	buffer[_i++] = ZmMsg.displayFormatLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><select id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_VIEW_AS_HTML' tabindex='80'></select></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.imagesLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_DISPLAY_EXTERNAL_IMAGES' tabindex='100' type='checkbox'></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.showFragments;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SHOW_FRAGMENTS' tabindex='110' type='checkbox'></td></tr><tr><td class='ZOptionsLabelTop'>";
	buffer[_i++] = ZmMsg.messageReadLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MARK_MSG_READ'></td></tr>";	
	
	//Begin Move Deleted Messages Prefs Change
	buffer[_i++] = "<tr><td class='ZOptionsLabelTop'>";
	buffer[_i++] = ZmMsg.selectAfterDeleteLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SELECT_AFTER_DELETE' tabindex='97'></div></td></tr>";
	//End Move Deleted Messages Prefs Change
	
	
	buffer[_i++] = "</table></td></tr></table>";
	
	
	//Begin receiving messages prefs change

		buffer[_i++] = "<table border=0 cellspacing=0 cellpadding=0 width=100%><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>";
	buffer[_i++] = ZmMsg.messagesReceiving;
	buffer[_i++] = "</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' border=0 cellspacing=0 cellpadding=0 width=100%><tr><td class='ZOptionsSectionMain'><table width=100%>";
	 if (data.isEnabled(ZmSetting.MAIL_NOTIFY_SOUNDS)) { 
	buffer[_i++] = "<tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.messageArrivalLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_NOTIFY_SOUNDS' tabindex='105' type=checkbox /></td></tr>";
	 } 
	if (data.isEnabled(ZmSetting.MAIL_NOTIFY_BROWSER)) { 
	buffer[_i++] = "<tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_NOTIFY_BROWSER' tabindex='107' type=checkbox /></td></tr>";
	 } 
	 if (!appCtxt.multiAccounts || appCtxt.isFamilyMbox || (!data.activeAccount.isMain && data.activeAccount.isZimbraAccount)) { 
	 
	/*
	 if (data.isEnabled(ZmSetting.MAIL_READ_RECEIPT_ENABLED)) { 
	buffer[_i++] = "<tr><td colspan=2><hr></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.readReceipt;
	buffer[_i++] = "</td><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.readReceiptPref;
	buffer[_i++] = "</td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_SEND_READ_RECEIPTS' tabindex='170'></div></td></tr><tr><td colspan=2><hr></td></tr>";
	 } 
	buffer[_i++] = "<tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.removeDupesToSelfLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.removeDupesToSelf;
	buffer[_i++] = "</td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_DEDUPE_MSG_TO_SELF' tabindex='180'></div></td></tr>";
	*/
	 } 
	buffer[_i++] = "</table></td></tr></table>";
	//End prefs change JJM	
	
	
	
	
	buffer[_i++] = "<table cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>Schedule Email Deletion&nbsp;&nbsp;<a href='http://ccqa4.comcast.com/Pages/FAQViewer.aspx?Guid=12a47f81_7551_436b_863d_583a2e1b3e02' target='_blank'>Help</a></td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><!-- xr-feat-154 --><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain ZOptionsSectionMainTable' colspan='3'><table width='95%' cellspacing='0' cellpadding='3'><colgroup><col width='1'><col width='*'></colgroup><tr><td class='ZOptionsTableLabel Line1'>Inbox (Unread):</td><td class='ZOptionsTableRow Line1'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_LIFETIME_INBOX_UNREAD' tabindex='20'></div></td></tr><tr><td class='ZOptionsTableLabel Line2'>Inbox (Read):</td><td class='ZOptionsTableRow Line2'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_LIFETIME_INBOX_READ' tabindex='30'></div></td></tr><tr><td class='ZOptionsTableLabel Line1'>Sent Email:</td><td class='ZOptionsTableRow Line1'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_LIFETIME_SENT' tabindex='40'></div></td></tr><tr><td class='ZOptionsTableLabel Line2'>Spam:</td><td class='ZOptionsTableRow Line2'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_LIFETIME_JUNK' tabindex='50'></div></td></tr><tr><td class='ZOptionsTableLabel Line1'>Trash:</td><td class='ZOptionsTableRow Line1'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_LIFETIME_TRASH' tabindex='60'></div></td></tr></table></td></tr></table><!-- End xr-feat-154 -->";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#Mail"
}, true);

AjxTemplate.register("prefs.Pages#Composing", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<!-- xr-feat-167 --><div class='PrefsTitleContainer'><div class='PrefsTitle'><table cellpadding=\"0\" cellspacing=\"0\"><tr><td class='PrefsTitleText'>Composing Preferences</td></tr></table></div><div class='PrefsRightAction'><table class='ZOptionsDefaultsTable'><tr><td class='ZOptionsDefaultsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REVERT_PAGE' tabindex='10'></div></td></tr></table></div></div><!-- End xr-feat-167 --><table cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>";
	buffer[_i++] = ZmMsg.general;
	buffer[_i++] = "</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain'><table width='100%'><tr><td class='ZOptionsLabelTop'>";
	buffer[_i++] = ZmMsg.messagesSentLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SAVE_TO_SENT' tabindex='20'></div></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.composeLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_NEW_WINDOW_COMPOSE' tabindex='30' type='checkbox'></td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MAIL_MANDATORY_SPELLCHECK' tabindex=\"40\" type='checkbox'></td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_AUTO_SAVE_DRAFT_INTERVAL' tabindex='50' type='checkbox'></td></tr></table></td></tr></table><table cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>Format Email</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain'><table width='100%'><tr><td class='ZOptionsLabelTop'>";
	buffer[_i++] = ZmMsg.composeLabel;
	buffer[_i++] = "</td><td class='ZOptionsNestedTable'><table><colgroup><col width='1'><col width='1'><col> <!-- family --><col width='1'><col> <!-- size --><col width='1'><col> <!-- color --></colgroup><tr valign='top'><td class='ZOptionsFieldTop'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_COMPOSE_AS_FORMAT' tabindex='40'></div></td><td class='ZOptionsLabelTop ZOptionsLabelNarrow'>";
	buffer[_i++] = ZmMsg.fontFamilyLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><select id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_COMPOSE_INIT_FONT_FAMILY' tabindex='50'></select></td><td class='ZOptionsLabelTop ZOptionsLabelNarrow'>";
	buffer[_i++] = ZmMsg.fontSizeLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><select id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_COMPOSE_INIT_FONT_SIZE' tabindex='60'></select></td><td class='ZOptionsLabelTop ZOptionsLabelNarrow'>";
	buffer[_i++] = ZmMsg.fontColorLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><select id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_COMPOSE_INIT_FONT_COLOR' tabindex='70'></select></td></tr></table></td></tr><tr><td colspan='2'><div class='bottomSeparator'></div></td></tr>";
	
	
	//BEGIN 6.0.10 Prefs Update
	buffer[_i++] = "<tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.composeReplyLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.composeReplyEmail;
	buffer[_i++] = "</td></tr><tr><td>&nbsp;</td><td class='ZOptionsField'><table><tr><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REPLY_INCLUDE_WHAT' tabindex='70' /></td><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REPLY_USE_PREFIX' tabindex='72' /></td><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REPLY_INCLUDE_HEADERS' tabindex='74' /></td></tr></table></td></tr><tr><td colspan=2></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.forwardingLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.forwardingEmail;
	buffer[_i++] = "</td></tr><tr><td>&nbsp;</td><td class='ZOptionsField'><table><tr><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_FORWARD_INCLUDE_WHAT' tabindex='80' /></td><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_FORWARD_USE_PREFIX' tabindex='82' /></td><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_FORWARD_INCLUDE_HEADERS' tabindex='84' /></td></tr></table></td></tr><tr><td colspan=2></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.prefixLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.prefixTextWith;
	buffer[_i++] = "</td></tr><tr><td>&nbsp;</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REPLY_PREFIX' tabindex='90' /></td></tr><tr><td>&nbsp;</td><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.prefixNote;
	buffer[_i++] = "</td></tr>";
	//END 6.0.10 Prefs Update
	
	
	buffer[_i++] = "<tr><td colspan='2'><div class='bottomSeparator'></div></td></tr><tr><td colspan='2'><b>Additional composing options can be set on the\n";
	buffer[_i++] = "\t\t\t\t\t\t<a tabindex='105' href='#Prefs.Accounts' onclick='skin.gotoPrefs(\"ACCOUNTS\");return false'>Email Manager Page</a></b></td></tr></table></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#Composing"
}, true);



AjxTemplate.register("prefs.Pages#Signatures", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<!-- xr-feat-167 --><div class='PrefsTitleContainer'><div class='PrefsTitle'><table cellpadding=\"0\" cellspacing=\"0\"><tr><td class='PrefsTitleText'>Signature Preferences</td></tr></table></div>";
	buffer[_i++] = "<div align=right style='margin-right: 20px; margin-bottom: 10px;'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REVERT_PAGE' tabindex='10' style='margin-right: 25px;'></div></div><table border=0 cellspacing=0 cellpadding=0 width=100%><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>";
	buffer[_i++] = ZmMsg.signatures;
	buffer[_i++] = "</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' border=0 cellspacing=0 cellpadding=0 width=100%><tr><td class='ZOptionsSectionMain'><table width=100% cellspacing=0><tr><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SIGNATURES' tabindex='20'></div></td></tr></table></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=100%><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>";
	buffer[_i++] = ZmMsg.signaturesUsing;
	buffer[_i++] = "</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' border=0 cellspacing=0 cellpadding=0 width=100%><tr><td class='ZOptionsSectionMain'><table width=100% cellspacing=0><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.placeSignature;
	buffer[_i++] = "</td><td class='ZOptionsInfo'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SIGNATURE_STYLE' tabindex='40'></div></td></tr><tr><td colspan=2><hr></td></tr><tr><td colspan=2><b>";
	buffer[_i++] = ZmMsg.signatureMoreOptions;
	buffer[_i++] = "</b></td></tr></table></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#Signatures"
}, false);

AjxTemplate.register("prefs.Pages#SignatureSplitView", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;
	
	buffer[_i++] = "<!-- xr-feat-167 --><div class='PrefsTitleContainer'><div class='PrefsTitle'><table cellpadding=\"0\" cellspacing=\"0\"><tr><td class='PrefsTitleText'>Signature Preferences</td></tr></table></div>";
	buffer[_i++] = "<table border=0 cellpadding=0 cellspacing=5 width='100%'><tr><td style=\"width:150px;min-width:150px\" height='100%' valign=top><table border=0 cellpadding=3 cellspacing=0 width='100%' height='100%'><tr><td class='ZOptionsField' align='left' valign='top'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SIG_ADD' tabindex='30'></div></td></tr><tr><td class='ZOptionsField' valign='top' height='150px' width='150px'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SIG_LIST' width='100%' tabindex='40'></div></td></tr></table></td><td valign=top><table border=0 cellpadding='4' cellspacing='0' width='100%'><tr><td class='ZOptionsField' nowrap=\"nowrap\" width=\"10%\">";
	buffer[_i++] = ZmMsg.signatureNameLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SIG_NAME' tabindex='50' size=30></td><td class='ZOptionsField'>&nbsp;</td><td class=\"ZOptionsField\" align='right'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SIG_FORMAT' tabIndex='60'></div></td><td class='ZOptionsField' width='80'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SIG_BUTTON' tabindex='70'></div></td></tr><tr><td class='ZOptionsField' width=\"100%\" valign='top' style='border:1px solid #CCCCCC; padding: 0px;' height='150px' colspan=6><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_SIG_EDITOR' tabindex='80'></div></td></tr></table></td></tr><tr><td colspan=2><table width=1%><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.noteLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><nobr>";
	buffer[_i++] = ZmMsg.signatureNote;
	buffer[_i++] = "</nobr></td></tr></table></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#SignatureSplitView"
}, false);

AjxTemplate.register("prefs.Pages#Contacts", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>";
	buffer[_i++] = ZmMsg.importExport;
	buffer[_i++] = "</td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain'><table width='100%'><tr><td class='ZOptionsLabelTop'>";
	buffer[_i++] = ZmMsg.importLabel;
	buffer[_i++] = "</td><td class='ZOptionsInfo'>";
	buffer[_i++] = ZmMsg.importFromCSVHint;
	buffer[_i++] = "</td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsInfo'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_IMPORT' tabindex='50'></div></td></tr><tr><td colspan='2'><div class='bottomSeparator'></div></td></tr><tr><td class='ZOptionsLabelTop'>";
	buffer[_i++] = ZmMsg.exportLabel;
	buffer[_i++] = "</td><td class='ZOptionsInfo'>";
	buffer[_i++] = ZmMsg.exportToCSVHint;
	buffer[_i++] = "</td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsInfo'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXPORT' tabindex='60'></div></td></tr></table></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#Contacts"
}, true);

AjxTemplate.register("prefs.Pages#Accounts", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<!-- xr-feat-167 --><div class='PrefsTitleContainer'><div class='PrefsTitle'><table cellpadding=\"0\" cellspacing=\"0\"><tr><td class='PrefsTitleText'>Email Management Preferences</td></tr></table></div><div class='PrefsRightAction'><table class='ZOptionsDefaultsTable'><tr><td class='ZOptionsDefaultsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REVERT_PAGE' tabindex='10'></div></td></tr></table></div></div><!-- End xr-feat-167 --><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr class='ZOptionsHeaderRow'><td class='ZOptionsHeaderL'><div class='ImgPrefsHeader_L'></div></td><td class='ZOptionsHeader ImgPrefsHeader'>";
	buffer[_i++] = ZmMsg.accounts;
	buffer[_i++] = "&nbsp;&nbsp;<a href='http://ccqa4.comcast.com/Pages/FAQViewer.aspx?seoid=view_all_accounts_one_login_smartzone' target='_blank'>Help</a></td><td class='ZOptionsHeaderR'><div class='ImgPrefsHeader_R'></div></td></tr></table><table class='ZOptionsSectionTable' cellspacing='0' cellpadding='0' width='100%'><tr><td class='ZOptionsSectionMain'><table cellspacing='0' cellpadding='0' width='100%'><tr><td>&nbsp;</td><td>Select the account you want to manage then select the options for that account.</td></tr><tr><td class='ZOptionsLabelTop' style='width:10%;padding-right:10px'>Accounts:</td><td class='ZOptionsField' style='padding-right:10px;vertical-align:top;width:80%'><div style='padding:0;margin:0;'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_ACCOUNTS'></div></div></td><td align='center' style='height:100%;width:10%'><table border='0' cellspacing='0' style='height:100%'><!-- xr-feat-165 --><tr><td align='center' style='height:100%;vertical-align:top'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_ADD_EXTERNAL' tabindex='20'></div></td></tr><tr><td align='center' style='height:100%;vertical-align:bottom'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_DELETE' tabindex='30'></div></td></tr><!-- End xr-feat-165 --></table></td></tr></table></td></tr><tr><td class='ZOptionsSectionMain'><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_PRIMARY\" class='ZAccountSettings'>";
	buffer[_i++] =  AjxTemplate.expand("#PrimaryAccount", data) ;
	buffer[_i++] = "</div><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL\" class='ZAccountSettings' style='display:none;'>";
	buffer[_i++] =  AjxTemplate.expand("#ExternalAccount", data) ;
	buffer[_i++] = "</div>";
	 if (skin.PROVIDER_ENABLED) { 
	buffer[_i++] = "<div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap\" class='ZAccountSettings' style='display:none;'>";
	buffer[_i++] =  AjxTemplate.expand("#ExternalAccount-comcast-imap", data) ;
	buffer[_i++] = "</div>";
	 } 
	buffer[_i++] = "</td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#Accounts"
}, true);

AjxTemplate.register("prefs.Pages#PrimaryAccount", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='ZOptionsNestedFormHeader'><span id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_PRIMARY_HEADER'>";
	buffer[_i++] = ZmMsg.accountName;
	buffer[_i++] = "</span></div><div class='ZOptionsNestedForm'><table width='100%'><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.accountNameLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_PRIMARY_NAME' tabindex='1010'></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.emailAddrLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><span id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_PRIMARY_EMAIL' tabindex='1020'></span></td></tr><tr><td colspan='2'><div class='bottomSeparator'></div></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.fromLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.fromDisplayLabel;
	buffer[_i++] = "</td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_PRIMARY_FROM_NAME' tabindex='1030' size='30' title='e.g. Bob Smith'></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.signatureLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><select id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_PRIMARY_SIGNATURE' tabindex='1040'></select></td></tr></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#PrimaryAccount"
}, true);

AjxTemplate.register("prefs.Pages#ExternalAccount", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='ZOptionsNestedFormHeader'><span id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_HEADER'>";
	buffer[_i++] = ZmMsg.accountName;
	buffer[_i++] = "</span></div><div class='ZOptionsNestedForm'><table width='100%'><tr><td colspan='2'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_ALERT'></div></td></tr>";
	 if (skin.PROVIDER_ENABLED) { 
	buffer[_i++] = "<tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.accountTypeLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_X-TYPE' tabindex='2005'></div></td></tr><tr><td colspan='2'><div class='bottomSeparator'></div></td></tr>";
	 } 
	buffer[_i++] = "<tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.accountNameLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_NAME' tabindex='2010' size='30'></div></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.emailAddrLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_EMAIL' tabindex='2020' title='e.g. bob@comcast.net' size='30'></td></tr><tr><td colspan='2'><div class='bottomSeparator'></div></td></tr>";
	 if (!skin.PROVIDER_ENABLED) { 
	buffer[_i++] = "<tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.accountTypeLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_ACCOUNT_TYPE' tabindex='2030'></div></td></tr>";
	 } 
	buffer[_i++] = "<tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.accountServerLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_HOST' tabindex='2040' title='e.g. pop.comcast.net' size='30'></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.usernameLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_USERNAME' tabindex='2050' title='e.g. bob'></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.passwordLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><!-- NOTE: impossible to set a hint because it will just be turned into ****** --><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_PASSWORD' tabindex='2060' type='password'></td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><table><tr><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_TEST' tabindex='2070'></div></td></tr></table></td></tr><tr><td colspan='2'><div class='bottomSeparator'></div></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.advancedSettingsLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_SSL' tabindex='2080'></div></td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><table><tr><td><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_CHANGE_PORT' tabindex='2090' type='checkbox'></td><td><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_PORT' tabindex='2100' size='4'></td><td class='ZOptionsInfo'><span id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_PORT_DEFAULT'>(110 is the default)</span></td></tr></table></td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_DELETE_AFTER_DOWNLOAD' tabindex='2110' type='checkbox'></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.setReplyTo;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_REPLY_TO' tabindex='2120' type='checkbox'></td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsNestedTable'><table><tr><td class='ZOptionsFieldTop'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_REPLY_TO_NAME' tabindex='2130'></td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EXTERNAL_REPLY_TO_EMAIL' tabindex='2140'></td></tr></table></td></tr></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#ExternalAccount"
}, true);

AjxTemplate.register("prefs.Pages#ExternalAccount-comcast-imap", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='ZOptionsNestedFormHeader'><span id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_HEADER'>";
	buffer[_i++] = ZmMsg.accountName;
	buffer[_i++] = "</span></div><div class='ZOptionsNestedForm'><table width='100%'><tr><td colspan='2'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_ALERT'></div></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.accountTypeLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_X-TYPE' tabindex='2000'></div></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.accountNameLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_NAME' tabindex='2010' size='30'></div></td></tr><tr><td class='ZOptionsLabelTop' rowspan='2'>";
	buffer[_i++] = ZmMsg.usernameLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_USERNAME' tabindex='2050' title='e.g. bob'></td></tr><tr><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.usernameTip;
	buffer[_i++] = "</td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.passwordLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><!-- NOTE: impossible to set a hint because it will just be turned into ****** --><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_PASSWORD' tabindex='2060' type='password'></td></tr><tr><td class=\"ZSpacer\" colspan=\"2\"/><div class='bottomSeparator'></div></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.fromLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'>";
	buffer[_i++] = ZmMsg.fromDisplayLabel;
	buffer[_i++] = "</td></tr><tr><td class='ZOptionsLabel'>&nbsp;</td><td class='ZOptionsField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_FROM_NAME' tabindex='2070' size='30' title='e.g. bob sending on behalf of john'><span style='display:none'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_FROM_EMAIL'></span></td></tr><tr><td class='ZOptionsLabel'>";
	buffer[_i++] = ZmMsg.signatureLabel;
	buffer[_i++] = "</td><td class='ZOptionsField'><select id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_comcast-imap_SIGNATURE' tabindex='2080'></select></td></tr></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#ExternalAccount-comcast-imap"
}, true);

AjxTemplate.register("prefs.Pages#AccountTestItem", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<tr><td class='ZmTestItem'>";
	buffer[_i++] = AjxStringUtil.htmlEncode(data.account.name);
	buffer[_i++] = "</td><td id='";
	buffer[_i++] = data["account"]["id"];
	buffer[_i++] = "_test_status' class='ZmTestStatus'></td></tr><tr id='";
	buffer[_i++] = data["account"]["id"];
	buffer[_i++] = "_test_details' style='display:none'><td><table border='0'><tr valign='top'><td class='ZmTestError'>";
	buffer[_i++] = ZmMsg.errorLabel;
	buffer[_i++] = "</td><td id='";
	buffer[_i++] = data["account"]["id"];
	buffer[_i++] = "_test_error' class='ZmTestError'></td></tr><tr valign='top'><td class='ZmTestNote'>";
	buffer[_i++] = ZmMsg.noteLabel;
	buffer[_i++] = "</td><td class='ZmTestNote'>";
	buffer[_i++] = ZmMsg.popAccountTestNote;
	buffer[_i++] = "</td></tr></table></td></tr>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "prefs.Pages#AccountTestItem"
}, true);

