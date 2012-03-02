AjxTemplate.register("abook.Contacts#ZmEditContactView", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table valign=top width='100%' cellspacing=0><tr valign=top class=contactHeaderTable><td class=contactHeaderCell><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_IMAGE\" tabindex=\"100\"></div><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REMOVE_IMAGE_row\" style='padding-left:7px;font-size:.8em'><nobr><a id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_VIEW_IMAGE\" href=\"#view\" tabindex=\"101\">";
	buffer[_i++] = ZmMsg.view;
	buffer[_i++] = "</a>\n";
	buffer[_i++] = "\t\t\t\t\t\t|\n";
	buffer[_i++] = "\t\t\t\t\t\t<a id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_REMOVE_IMAGE\" href=\"#remove\" tabindex=\"102\">";
	buffer[_i++] = ZmMsg.remove;
	buffer[_i++] = "</a></nobr></div></td><td class=contactHeaderCell valign=bottom><table style='float:right;'><tr><td>";
	buffer[_i++] = ZmMsg.fileAsLabel;
	buffer[_i++] = "</td><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_FILE_AS\" tabindex=\"400\"></div></td></tr>";
	 if (appCtxt.multiAccounts) { 
	buffer[_i++] = "<tr><td>";
	buffer[_i++] = ZmMsg.accountLabel;
	buffer[_i++] = "</td><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_ACCOUNT\"></div></td></tr>";
	 } 
	buffer[_i++] = "</table><div style='padding:.125em'><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_FULLNAME\"></div></div><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_NAME_row\"><table><tr><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_FIRST\" tabindex=\"201\"></div></td><td id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MIDDLE_row\"><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_MIDDLE\" tabindex=\"202\"></div></td><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_LAST\" tabindex=\"204\"></div></td><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_DETAILS\" tabindex=\"206\"></div></td></tr></table></div><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_NICKNAME_row\"><table><tr><td>&ldquo;</td><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_NICKNAME\" tabindex=\"250\"></div></td><td>&rdquo;</td></tr></table></div><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_COMPANY_row\"><table><tr><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_COMPANY\" tabindex=\"300\"></div></td></tr></table></div><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_JOB_row\"><table><tr><td id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_TITLE_row\"><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_TITLE\" tabindex=\"301\"></div></td><td id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_TITLE_DEPARTMENT_SEP\">&nbsp;-&nbsp;</td><td id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_DEPARTMENT_row\"><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_DEPARTMENT\" tabindex=\"302\"></div></td></tr></table></div></td></tr><tr valign=top><td class=rowLabel>";
	buffer[_i++] = ZmMsg.emailLabel;
	buffer[_i++] = "</td><td class=rowValue><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_EMAIL\" tabindex=\"500\"></div></td></tr><tr valign=top><td class=rowLabel>";
	buffer[_i++] = ZmMsg.phoneLabel;
	buffer[_i++] = "</td><td class=rowValue><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_PHONE\" tabindex=\"600\"></div></td></tr><tr valign=top><td class=rowLabel>";
	buffer[_i++] = ZmMsg.imLabel;
	buffer[_i++] = "</td><td class=rowValue><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_IM\" tabindex=\"700\"></div></td></tr><tr valign=top><td class=rowLabel>";
	buffer[_i++] = ZmMsg.addressLabel;
	buffer[_i++] = "</td><td class=rowValue><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_VELODROME_ADDRESS\" tabindex=\"800\"></div></td></tr><tr valign=top><td class=rowLabel>";
	buffer[_i++] = ZmMsg.webPageLabelShort;
	buffer[_i++] = "</td><td class=rowValue><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_URL\" tabindex=\"900\"></div></td></tr><tr valign=top><td class=rowLabel>";
	buffer[_i++] = ZmMsg.otherLabel;
	buffer[_i++] = "</td><td class=rowValue><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_OTHER\" tabindex=\"1000\"></div></td></tr><tr valign=top><td class=rowLabel>";
	buffer[_i++] = ZmMsg.notesLabel;
	buffer[_i++] = "</td><td class=rowValue style='padding-left:3'><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_NOTES\" tabindex=\"1100\"></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#ZmEditContactView"
}, true);
AjxTemplate.register("abook.Contacts", AjxTemplate.getTemplate("abook.Contacts#ZmEditContactView"), AjxTemplate.getParams("abook.Contacts#ZmEditContactView"));

AjxTemplate.register("abook.Contacts#ZmEditContactViewRow", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table><tr id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_row\" class=\"DwtFormRow ZmEditContactViewRow\"><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "\" tabindex=\"100\"></div></td><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_add\" class=\"DwtFormRowAdd\" tabindex=\"200\"></div></td><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_remove\" class=\"DwtFormRowRemove\" tabindex=\"300\"></div></td><td><div id=\"";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_helptext\" class=\"ZmEditContactViewRowHelp\"></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#ZmEditContactViewRow"
}, true);

AjxTemplate.register("abook.Contacts#CardsView", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<center><table border=0 cellpadding=5 cellspacing=5 id='";
	buffer[_i++] = data["cardTableId"];
	buffer[_i++] = "'>";
	
					var count = 0;
					for (var i = 0; i < data.list.length; i++) {
						var contact = data.list[i];
						if (count % 2 == 0) {
				
	buffer[_i++] = "<tr>";
	 }
							count++;
						
	buffer[_i++] = "<td valign=top id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_contact_";
	buffer[_i++] =  contact.id ;
	buffer[_i++] = "'></td>";
	 if (count % 2 == 0) { 
	buffer[_i++] = "</tr><tr><td colspan=2><hr size=1 color=\"#CCCCCC\"></td></tr>";
	 } 
	 } 
	buffer[_i++] = "</table></center>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#CardsView"
}, true);

AjxTemplate.register("abook.Contacts#CardBase", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div name='";
	buffer[_i++] = data["name"];
	buffer[_i++] = "' class='";
	buffer[_i++] = data["className"];
	buffer[_i++] = "' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "' style='width:";
	buffer[_i++] = data["width"];
	buffer[_i++] = "'><table border=0 width=100% height=100% cellpadding=0 cellspacing=0><tr><td colspan=2 class='contactHeader'>";
	buffer[_i++] =  data.contact.getFileAs() ;
	buffer[_i++] = "</td></tr>";
	 if (data.contact.isGroup()) { 
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardGroup", {data:data}) ;
	 } else { 
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardContact", {data:data}) ;
	 } 
	buffer[_i++] = "</table>\n";
	buffer[_i++] = "\t\t";
	buffer[_i++] = data["imgHtml"];
	buffer[_i++] = "\n";
	buffer[_i++] = "\t</div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#CardBase"
}, true);

AjxTemplate.register("abook.Contacts#CardContact", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	
			var contact		= data.data.contact;
	
			var company		= contact.getAttr(ZmContact.F_company);
			var homeAddr	= contact.getAttr("homeAddress");
			var workAddr	= contact.getAttr("workAddress");
			var homePhone	= contact.getAttr(ZmContact.F_homePhone);
			var email		= contact.getEmail();
			var workPhone	= contact.getAttr(ZmContact.F_workPhone);
			var mobile		= contact.getAttr(ZmContact.F_mobilePhone);
		
	buffer[_i++] = "<tr><td valign=top><table border=0 cellpadding=2 cellspacing=2 width=100%>";
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardContactField", {data:data, fname:ZmMsg.AB_FIELD_company, value:company}) ;
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardContactField", {data:data, fname:ZmMsg.AB_ADDR_HOME, value:homeAddr}) ;
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardContactField", {data:data, fname:ZmMsg.AB_ADDR_WORK, value:workAddr}) ;
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardContactField", {data:data, fname:ZmMsg.AB_FIELD_homePhone, value:homePhone, type:ZmObjectManager.PHONE}) ;
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardContactField", {data:data, fname:ZmMsg.AB_FIELD_email, value:email, type:ZmObjectManager.EMAIL}) ;
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardContactField", {data:data, fname:ZmMsg.AB_FIELD_workPhone, value:workPhone, type:ZmObjectManager.PHONE}) ;
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#CardContactField", {data:data, fname:ZmMsg.AB_FIELD_mobilePhone, value:mobile, type:ZmObjectManager.PHONE}) ;
	buffer[_i++] = "</table></td></tr>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#CardContact"
}, true);

AjxTemplate.register("abook.Contacts#CardContactField", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	
			var newValue = data.data.data.isDnd
				? data.value
				: data.data.data.view._generateObject(data.value, data.type)
			if (newValue) {
		
	buffer[_i++] = "<tr><td valign=top class='ZmContactFieldValue'>";
	buffer[_i++] =  data.fname ;
	buffer[_i++] = ":\n";
	buffer[_i++] = "\t\t\t</td><td valign=top class='ZmContactField'>";
	buffer[_i++] =  AjxStringUtil.nl2br(newValue) ;
	buffer[_i++] = "</td></tr>";
	 } 

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#CardContactField"
}, true);

AjxTemplate.register("abook.Contacts#SplitView_header", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	
			var contact		= data.contact;
			var imageUrl	= contact.getImageUrl();
			var nickname	= contact.getAttr(ZmContact.F_nickname);
			var company		= contact.getAttr(ZmContact.F_company);
			var title		= contact.getAttr(ZmContact.F_jobTitle);
			var department	= contact.getAttr(VelodromeSkin.F_otherDepartment);
			var fullname	= contact.getFullName();
			var accountName = appCtxt.multiAccounts && contact.account && contact.account.getDisplayName();
		
	buffer[_i++] = "<table border=0 cellspacing=2 cellpadding=2 width=100% class='contactHeaderTable ";
	buffer[_i++] =  data.contactHdrClass ;
	buffer[_i++] = "'><tr>";
	 if (imageUrl) { 
	buffer[_i++] = "<td valign=bottom width=48 rowspan=2><img src=\"";
	buffer[_i++] =  imageUrl ;
	buffer[_i++] = "\" width=48 height=48 border=0></td>";
	 } else { 
	buffer[_i++] = "<td valign=bottom width=48 rowspan=2>";
	buffer[_i++] =  AjxImg.getImageHtml("Person_48") ;
	buffer[_i++] = "</td>";
	 } 
	buffer[_i++] = "<td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_tags' align='right' colspan=2></td></tr><tr><td valign=bottom><div class='contactHeader ";
	buffer[_i++] =  data.isInTrash ? "Trash" : "" ;
	buffer[_i++] = "'>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(fullname) ;
	buffer[_i++] = "</div>";
	 if (nickname) { 
	buffer[_i++] = "<div class='companyName'>&ldquo;";
	buffer[_i++] =  AjxStringUtil.htmlEncode(nickname) ;
	buffer[_i++] = "&rdquo;</div>";
	 } 
	 if (title || department) { 
	buffer[_i++] = "<div class='companyName'>";
	 if (title) { 
	buffer[_i++] = "<span>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(title) ;
	buffer[_i++] = "</span>";
	 } 
	 if (department) { 
	 if (title) { 
	buffer[_i++] = "\n";
	buffer[_i++] = "\t\t\t\t\t\t\t&nbsp;-&nbsp;\n";
	buffer[_i++] = "\t\t\t\t\t\t";
	 } 
	buffer[_i++] = "<span>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(department) ;
	buffer[_i++] = "</span>";
	 } 
	buffer[_i++] = "</div>";
	 } 
	 if (company) { 
	buffer[_i++] = "<div class='companyName'>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(company) ;
	buffer[_i++] = "</div>";
	 } 
	buffer[_i++] = "</td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#SplitView_header"
}, true);

AjxTemplate.register("abook.Contacts#SplitView_addresses", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	
		var attrs = data.attrs;
		var id = data.id;
		var label = ZmMsg.addressLabel;
		var types = {"work":ZmMsg.AB_FIELD_workAddress,"home":ZmMsg.AB_FIELD_homeAddress,"other":ZmMsg.AB_FIELD_otherAddress};
		var fields = ZmEditContactView.LISTS["VELODROME_ADDRESS"].attrs;
		var seenone = false;
		var count = {};
	
		for (var i = 0; i < fields.length; i++) {
			var field = fields[i];
			var type = ZmMsg["AB_FIELD_" + field] || field;
			var address = attrs[field];
			if (address) {
				count[field] = count[field] ? count[field] + 1 : 1;
				var name = [field, count[field]>1?count[field]:""].join("");
				
	buffer[_i++] = "<tr valign=top><td class='rowLabel'>";
	buffer[_i++] =  seenone ? "" : label ;
	buffer[_i++] = "</td><td class='rowValue' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_";
	buffer[_i++] = name;
	buffer[_i++] = "'>";
	 if (address) { 
	buffer[_i++] = "<div>";
	buffer[_i++] =  address ;
	buffer[_i++] = "</div>";
	 } 
	buffer[_i++] = "</td><td style='color:gray' class='rowType'>&nbsp;";
	buffer[_i++] =  type ;
	buffer[_i++] = "</td></tr>";
	
				seenone = true;
			}
		}
		

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#SplitView_addresses"
}, true);

AjxTemplate.register("abook.Contacts#SplitViewGroup", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table border=0 cellpadding=2 cellspacing=2 width=100% bgcolor=\"#FFFFFF\"><tr><td>";
	buffer[_i++] =  AjxTemplate.expand("abook.Contacts#SplitViewGroup_header", data) ;
	buffer[_i++] = "<div style='width:100%; height:100%; overflow:auto' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_body'><table border=0>";
	 for (var i = 0; i < data.groupMembers.length; i++) { 
	buffer[_i++] = "<tr><td width=20>";
	buffer[_i++] =  AjxImg.getImageHtml("Message") ;
	buffer[_i++] = "</td><td><nobr>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(data.groupMembers[i].toString()) ;
	buffer[_i++] = "</nobr></td></tr>";
	 } 
	buffer[_i++] = "</table></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#SplitViewGroup"
}, true);

AjxTemplate.register("abook.Contacts#SplitViewGroup_header", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	
			var contact		= data.contact;
			var imageUrl	= contact.getImageUrl();
			var nickname	= contact.getAttr(ZmContact.F_nickname); 
			var company		= contact.getAttr(ZmContact.F_company);
			var title		= contact.getAttr(ZmContact.F_jobTitle);
			var department	= contact.getAttr(ZmContact.F_department);
			var fullname	= contact.getFullName();
			var accountName = appCtxt.multiAccounts && contact.account && contact.account.getDisplayName();
		
	buffer[_i++] = "<table border=0 cellspacing=2 cellpadding=2 width=100% class='contactHeaderTable ";
	buffer[_i++] =  data.contactHdrClass ;
	buffer[_i++] = "'><tr>";
	 if (imageUrl) { 
	buffer[_i++] = "<td valign=bottom width=48 rowspan=2><img src=\"";
	buffer[_i++] =  imageUrl ;
	buffer[_i++] = "\" width=48 height=48 border=0></td>";
	 } else { 
	buffer[_i++] = "<td valign=bottom width=48 rowspan=2>";
	buffer[_i++] =  AjxImg.getImageHtml("Group_48") ;
	buffer[_i++] = "</td>";
	 } 
	buffer[_i++] = "<td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_tags' align='right' colspan=2></td></tr><tr><td valign=bottom><div class='contactHeader ";
	buffer[_i++] =  data.isInTrash ? "Trash" : "" ;
	buffer[_i++] = "'>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(fullname) ;
	buffer[_i++] = "</div>";
	 if (nickname) { 
	buffer[_i++] = "<div class='companyName'>&ldquo;";
	buffer[_i++] =  AjxStringUtil.htmlEncode(nickname) ;
	buffer[_i++] = "&rdquo;</div>";
	 } 
	 if (title || department) { 
	buffer[_i++] = "<div class='companyName'>";
	 if (title) { 
	buffer[_i++] = "<span>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(title) ;
	buffer[_i++] = "</span>";
	 } 
	 if (department) { 
	 if (title) { 
	buffer[_i++] = "\n";
	buffer[_i++] = "\t\t\t\t\t\t\t&nbsp;-&nbsp;\n";
	buffer[_i++] = "\t\t\t\t\t\t";
	 } 
	buffer[_i++] = "<span>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(department) ;
	buffer[_i++] = "</span>";
	 } 
	buffer[_i++] = "</div>";
	 } 
	 if (company) { 
	buffer[_i++] = "<div class='companyName'>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(company) ;
	buffer[_i++] = "</div>";
	 } 
	buffer[_i++] = "</td><td valign=bottom align='right'>";
	 if (accountName) { 
	buffer[_i++] = "<table border=0 cellpadding=0 cellspacing=0 width=1%><tr><td class='contactLocation' nowrap=\"nowrap\">";
	buffer[_i++] =  ZmMsg.accountLabel ;
	buffer[_i++] = "&nbsp;</td><td class='companyFolder' colspan=10>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(accountName) ;
	buffer[_i++] = "</td></tr></table>";
	 } 
	buffer[_i++] = "</td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#SplitViewGroup_header"
}, true);

AjxTemplate.register("abook.Contacts#SimpleView-NoResults", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table width='100%' cellspacing=0 cellpadding=1><tr><td style='font-size:14px; padding:15px' valign='top' ><br/>";
	buffer[_i++] =  ZmMsg.plaxoMessage1 ;
	buffer[_i++] = "<br/><br/>";
	buffer[_i++] =  ZmMsg.plaxoMessage2 ;
	buffer[_i++] = "<br/><br/><br/><input type=\"button\" onclick=\"window.open('http://plaxo.comcast.net/scc?action=uabb&src=smartzone_ab', '_blank')\" value=\"";
	buffer[_i++] =  ZmMsg.plaxoButtonLabel ;
	buffer[_i++] = "\"/></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#SimpleView-NoResults"
}, true);

AjxTemplate.register("abook.Contacts#SimpleView-NoResults-Search", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table width='100%' cellspacing=0 cellpadding=1><tr><td class='NoResults' valign='top' ><br/><br/>";
	buffer[_i++] =  AjxMsg.noResults ;
	buffer[_i++] = "</td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#SimpleView-NoResults-Search"
}, true);

AjxTemplate.register("abook.Contacts#GroupView", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<!-- title bar --><table cellspacing=0 cellpadding=0 width=100%><tr class='contactHeaderRow' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_headerRow'><td width=20><center>";
	buffer[_i++] =  AjxImg.getImageHtml("Group") ;
	buffer[_i++] = "</center></td><td><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_title' class='contactHeader'></div></td><td align='right' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_tags'></td></tr></table><table border=0 cellpadding=5 cellspacing=5 width=100% height=100%><tr><!-- content: left pane --><td width=50% valign=top><table border=0 cellpadding=2 cellspacing=2 width=100%><tr><td colspan=2 nowrap>*&nbsp;";
	buffer[_i++] =  ZmMsg.groupNameLabel ;
	buffer[_i++] = "&nbsp;<input type='text' autocomplete='off' size=18 id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_groupName'></td></tr><tr><td nowrap><!--*&nbsp;-->";
	buffer[_i++] =  ZmMsg.groupMembers ;
	buffer[_i++] = "</td><!--<td class='hintLabel'>";
	buffer[_i++] =  ZmMsg.groupHint ;
	buffer[_i++] = "</td>--></tr></table><div class='groupMembers'  id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_groupMembers'></div><table border=0 cellpadding=3 cellspacing=2 width=100%><tr><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_delButton'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_delAllButton'></td><td width=100%></td></tr></table></td><!-- content: right pane --><td width=50% valign=top><fieldset><legend class='groupFieldset'>";
	buffer[_i++] =  ZmMsg.addMembers ;
	buffer[_i++] = "</legend><table border=0><tr><td align=right>";
	buffer[_i++] =  ZmMsg.findLabel ;
	buffer[_i++] = "</td><td><input type='text' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_searchField'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_searchButton'></td></tr>";
	 if (data.showSearchIn) { 
	buffer[_i++] = "<tr><td align=right>";
	buffer[_i++] =  ZmMsg.searchIn ;
	buffer[_i++] = "</td><td colspan=2 id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_listSelect'></td></tr>";
	 } 
	buffer[_i++] = "</table><div class='groupMembers' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_listView'></div><table border=0 cellpadding=3 cellspacing=2 width=100%><tr><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_addButton'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_addAllButton'></td><td width=100%></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_prevButton'></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_nextButton'></td></tr></table></fieldset></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#GroupView"
}, true);

AjxTemplate.register("abook.Contacts#ZmAlphabetBar", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='AlphabetBarBorder ImgTab'><table class='AlphabetBarTable' border=0 cellpadding=0 cellspacing=0 width=80% id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_alphabet'><tr>";
	 for (var i = 0; i < data.numLetters; i++) { 
	buffer[_i++] = "<td _idx='";
	buffer[_i++] =  i ;
	buffer[_i++] = "' onclick='ZmContactAlphabetBar.alphabetClicked(this\n";
	buffer[_i++] = "\t\t\t\t\t\t";
	 if (i > 0) { 
	buffer[_i++] = "\n";
	buffer[_i++] = "\t\t\t\t\t\t\t, \"";
	buffer[_i++] =  i == 1 ? '0' : data.alphabet[i] ;
	buffer[_i++] = "\"\n";
	buffer[_i++] = "\t\t\t\t\t\t\t";
	 if (i+1 < data.numLetters) { 
	buffer[_i++] = "\n";
	buffer[_i++] = "\t\t\t\t\t\t\t\t, \"";
	buffer[_i++] =  i == 1 ? 'A' : data.alphabet[i+1] ;
	buffer[_i++] = "\"\n";
	buffer[_i++] = "\t\t\t\t\t\t\t";
	 } 
	 } 
	buffer[_i++] = "\n";
	buffer[_i++] = "\t\t\t\t\t\t); return false;' class='DwtButton AlphabetBarCell' onmouseover='ZmContactAlphabetBar._onMouseOver(this)' onmouseout='ZmContactAlphabetBar._onMouseOut(this)' ";
	buffer[_i++] =  (i>0) ? " style='border-left-width:0;'>" : ">" ;
	buffer[_i++] =  data.alphabet[i] ;
	buffer[_i++] = "</td>";
	 } 
	buffer[_i++] = "</tr></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "abook.Contacts#ZmAlphabetBar"
}, true);

