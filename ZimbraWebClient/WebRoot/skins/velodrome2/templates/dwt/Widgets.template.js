AjxTemplate.register("dwt.Widgets#ZButton", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	 var buttonClass = data.buttonClass || "Button"; 
	buffer[_i++] = "<table class='ZWidgetBorder Z";
	buffer[_i++] = buttonClass;
	buffer[_i++] = "Border ZWidgetTable Z";
	buffer[_i++] = buttonClass;
	buffer[_i++] = "Table' cellspacing=0 cellpadding=0><tr><td class='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = "_L'><div></div></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_left_icon'  \tclass='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = " ZLeftIcon ZWidgetIcon'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title'\t\tclass='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = " ZWidgetTitle'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_right_icon' \tclass='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = " ZRightIcon ZWidgetIcon'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_dropdown' \tclass='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = " ZDropDown'></td><td class='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = "_R'><div></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZButton",
	"class": "ZWidget"
}, true);
AjxTemplate.register("dwt.Widgets", AjxTemplate.getTemplate("dwt.Widgets#ZButton"), AjxTemplate.getParams("dwt.Widgets#ZButton"));

AjxTemplate.register("dwt.Widgets#ZSelect", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table class='ZSelectBorder ZSelectTable ZWidgetTable' cellspacing=0 cellpadding=0><tr><td><div class='ImgSelect_L'></div></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_left_icon'\tclass='ImgSelect ZLeftIcon ZWidgetIcon'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title'\t\tclass='ImgSelect ZWidgetTitle'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_right_icon'\tclass='ImgSelect ZRightIcon ZWidgetIcon'></td><td><div class='ImgSelect_R'></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZSelect",
	"class": "ZWidget"
}, true);

AjxTemplate.register("dwt.Widgets#DwtComboBoxButton", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	 var buttonClass = data.buttonClass || "Button"; 
	buffer[_i++] = "<table class='ZWidgetBorder Z";
	buffer[_i++] = buttonClass;
	buffer[_i++] = "Border ZWidgetTable Z";
	buffer[_i++] = buttonClass;
	buffer[_i++] = "Table' cellspacing=0 cellpadding=0><tr><td><div class='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = "_L'></div></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_dropdown' \tclass='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = " ZDropDown'></td><td><div class='Img";
	buffer[_i++] = buttonClass;
	buffer[_i++] = "_R'></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#DwtComboBoxButton",
	"class": "ZWidget"
}, true);

AjxTemplate.register("dwt.Widgets#ZTab", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table class='ZWidgetBorder ZTabTable ZWidgetTable' cellspacing=0 cellpadding=0><tr><td><div class='ImgTab_L'></div></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_left_icon'\tclass='ImgTab ZLeftIcon ZWidgetIcon'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title'\t\tclass='ImgTab ZWidgetTitle'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_right_icon'\tclass='ImgTab ZRightIcon ZWidgetIcon'></td><td><div class='ImgTab_R'></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZTab",
	"class": "ZWidget"
}, true);

AjxTemplate.register("dwt.Widgets#ZTabBarPrefix", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_prefix' class='ZTabBarPrefix ImgTab'>&nbsp;</td>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZTabBarPrefix"
}, true);

AjxTemplate.register("dwt.Widgets#ZTabBarSuffix", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_suffix' class='ZTabBarSuffix ImgTab'>&nbsp;</td>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZTabBarSuffix"
}, true);

AjxTemplate.register("dwt.Widgets#ZAccordionItem", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_header_";
	buffer[_i++] = data["itemNum"];
	buffer[_i++] = "' class='ZAccordionHeader'><table class='ZWidgetBorder ZWidgetTable' cellspacing=0 cellpadding=0><tr><td class='ImgAccordion_L'><div></div></td><td class='ImgAccordion'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_status_";
	buffer[_i++] = data["itemNum"];
	buffer[_i++] = "' class='ImgAccordionClosed'></div></td><td class='ImgAccordion'><div class='ZAccordionTitle' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title_";
	buffer[_i++] = data["itemNum"];
	buffer[_i++] = "'>";
	buffer[_i++] = data["title"];
	buffer[_i++] = "</div></td><td class='ImgAccordion_R'><div></div></td></tr></table></div><div class='ZAccordionBody' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_body_";
	buffer[_i++] = data["itemNum"];
	buffer[_i++] = "'></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZAccordionItem"
}, true);

AjxTemplate.register("dwt.Widgets#ZField", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table class='ZWidgetBorder' cellspacing=0 cellpadding=0><tr><td><div class='ImgField_L'></div></td><td width=100% class='ImgField'><input id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_field' class='ZFieldInput'></td><td><div class='ImgField_R'></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZField",
	"class": "ZWidget"
}, true);

AjxTemplate.register("dwt.Widgets#ZTreeItem", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='";
	buffer[_i++] = data["divClassName"];
	buffer[_i++] = "' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_div'><table id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_table' border=0 cellpadding=0 cellspacing=0 width=100%><tr><td align=center nowrap id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_nodeCell'></td>";
	 if (data.isCheckedStyle) { 
	buffer[_i++] = "<td nowrap id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_checkboxCell' class=\"ZTreeItemCheckboxCell\"><div class=\"ZTreeItemCheckbox\" id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_checkbox'><div class=\"ZTreeItemCheckboxImg\" id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_checkboxImg'>";
	buffer[_i++] =  AjxImg.getImageHtml("MenuCheck") ;
	buffer[_i++] = "</div></div></td>";
	 } 
	buffer[_i++] = "<td nowrap class='imageCell' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_imageCell'></td><td nowrap class='";
	buffer[_i++] = data["textClassName"];
	buffer[_i++] = "' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_textCell'></td><td nowrap width=16 class='imageCell' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_extraCell'></td></tr></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZTreeItem"
}, true);

AjxTemplate.register("share.Widgets#ZmAppChooser", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table class='ZmAppChooserTable' cellpadding=0 cellspacing=0><tr id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_items'></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "share.Widgets#ZmAppChooser",
	"class": "ZWidget"
}, true);

AjxTemplate.register("share.Widgets#ZmAppChooserItem", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] =  AjxTemplate.expand("dwt.Widgets#ZToolbarItem", data) ;

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "share.Widgets#ZmAppChooserItem"
}, true);

AjxTemplate.register("share.Widgets#ZmAppChooserButton", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_select' class='ZWidgetTable  ZWidgetBorder' cellspacing=0 cellpadding=0><tr><td class='ImgAppTab_L'></td><td class='ImgAppTab'><nobr><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title'></div></nobr></td><td class='ImgAppTab_R'></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "share.Widgets#ZmAppChooserButton"
}, true);

AjxTemplate.register("share.Widgets#ZmAppChooserIconButton", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_select' class='ZWidgetTable ZWidgetBorder' cellspacing=0 cellpadding=0><tr><td class='ImgAppTab_L'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_left_icon' class='ImgAppTab' style='padding:0px'></td><td class='ImgAppTab'><nobr><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title'></div></nobr></td><td class='ImgAppTab_R'></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "share.Widgets#ZmAppChooserIconButton"
}, true);

AjxTemplate.register("share.Widgets#ZmSearchToolBar", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table border=0 cellpadding=0 cellspacing=0 height=20><tr><td class='ImgField_L searchwidth'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_inputField' class='ImgField'></td><td class='ImgField_R searchwidth'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_searchMenuButton' style='padding-left: 2px;'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_searchButton' hint='text' style='padding-left: 2px;'></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "share.Widgets#ZmSearchToolBar"
}, true);

AjxTemplate.register("dwt.Widgets#DwtBaseDialog", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='DwtDialog'><table cellspacing=0 cellpadding=0 style='cursor:move;' border='0'><tr id='";
	buffer[_i++] = data["dragId"];
	buffer[_i++] = "'><td class='ImgDialog_NW' width='6'></td><td><table border='0' cellpadding='0' cellspacing='0' width='100%'><tr><td class='ImgDialog_N minWidth' width='1%'>";
	buffer[_i++] = data["icon"];
	buffer[_i++] = "</td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title' class='ImgDialog_N DwtDialogTitle' nowrap='nowrap'>";
	buffer[_i++] = data["title"];
	buffer[_i++] = "</td><td class='ImgDialog_N minWidth' width='1%'><div class='";
	buffer[_i++] = data["closeIcon2"];
	buffer[_i++] = "'></div></td><td class='ImgDialog_N minWidth width='1%'><div class='";
	buffer[_i++] = data["closeIcon1"];
	buffer[_i++] = "'></div></td></tr></table></td><td class='ImgDialog_NE' width='6'></td></tr><tr><td class='ImgDialog_W' width='6'></td><td class='DwtDialogBody' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_content'></td><td class='ImgDialog_E' width='6'></td></tr><tr><td valign='bottom' class='ImgDialog_SW' width='6'></td><td class='ImgDialog_S'>";
	 if (data.controlsTemplateId) { 
	buffer[_i++] =  AjxTemplate.expand(data.controlsTemplateId, data) ;
	 } 
	buffer[_i++] = "</td><td valign='bottom' class='ImgDialog_SE' width='6'></td></tr></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#DwtBaseDialog",
	"height": "32",
	"width": "20"
}, true);

AjxTemplate.register("dwt.Widgets#DwtDialogControls", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_buttons' class='DwtDialogButtonBar'>";
	 if (AjxEnv.isNav) { 
	buffer[_i++] = "<input type='button' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_focus' style='height:0px;width:0px;display:none;'>";
	 } 
	buffer[_i++] = "</div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#DwtDialogControls"
}, true);

AjxTemplate.register("XXXdwt.Widgets#DwtSemiModalDialog", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] =  AjxTemplate.expand("dwt.Widgets#DwtDialog", data) ;

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "XXXdwt.Widgets#DwtSemiModalDialog",
	"height": "32",
	"width": "20"
}, true);

AjxTemplate.register("dwt.Widgets#DwtHorizontalSlider", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table class='ZWidgetBorder ZVolumeSliderBorder' cellspacing=0 cellpadding=0 width=\"100%\"><tr><td width=7><div class='ImgSoundPlayer_Full_L'></div></td><td width='100%'><div class='ImgSoundPlayer_Full'></div></td><td width=7><div class='ImgSoundPlayer_Full_R'></div></td></tr></table><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_button' class='ImgSoundPlayer_Thumb' style='position:relative;'></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#DwtHorizontalSlider",
	"class": "DwtSlider DwtHorizontalSlider"
}, true);

AjxTemplate.register("dwt.Widgets#DwtVerticalSlider", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table class='ZWidgetBorder' cellspacing=0 cellpadding=0><tr><td><div class='ImgVolumeSlider_Full_T'></div></td></tr><tr><td height=100%><div class='ImgVolumeSlider_Full'></div></td></tr><tr><td><div class='ImgVolumeSlider_Full_B'></div></td></tr></tr></table><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_button' class='ImgVolumeSlider_Thumb' style='position:relative;'></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#DwtVerticalSlider",
	"class": "DwtSlider DwtVerticalSlider"
}, true);

AjxTemplate.register("dwt.Widgets#ZMenuItemSeparator", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table class='ZWidgetTable ZMenuItemTable ZMenuItemBorder ZMenuItemSeparatorBorder' cellspacing=0 cellpadding=0><tr><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_check'\t \tclass='ZCheckIcon'><div></div></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_left_icon'\tclass='ZLeftIcon ZWidgetIcon'><div></div></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title'\t\tclass='ZWidgetTitle'><div class='ZMenuItem-Separator'></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#ZMenuItemSeparator",
	"class": "ZWidget"
}, true);

AjxTemplate.register("dwt.Widgets#DwtVerticalSash", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='DwtVerticalSash ImgVSash'><center><div class='ImgVSash_thumb'></div></center></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#DwtVerticalSash",
	"height": "0",
	"width": "0",
	"class": "ZWidget"
}, true);

AjxTemplate.register("dwt.Widgets#DwtHorizontalSash", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='DwtHorizontalSash ImgHSash'><center><div class='ImgHSash_thumb'></div></center></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#DwtHorizontalSash",
	"height": "0",
	"width": "0",
	"class": "ZWidget"
}, true);

AjxTemplate.register("dwt.Widgets#DwtListView-NoResults", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table width='100%' cellspacing=0 cellpadding=1><tr><td class='NoResults' valign='top' ><br/><br/>";
	buffer[_i++] = data["message"];
	buffer[_i++] = "\n";
	buffer[_i++] = "\t\t\t</td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "dwt.Widgets#DwtListView-NoResults"
}, true);

