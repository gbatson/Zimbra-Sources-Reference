AjxTemplate.register("mail.Message#MessageHeader", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div><!-- empty container DIV here so Dwt.parseHtmlFragment returns the infoBar div below --><table class='MsgHeaderTable' cellspacing=0 cellpadding=0 border=0 width=100%><tr><td align=\"center\"><table id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_hdrTable' cellspacing=2 cellpadding=0 border=0 width=100%><tr id='";
	buffer[_i++] = data["hdrTableTopRowId"];
	buffer[_i++] = "'>";
	 if (data.closeBtnCellId) { 
	buffer[_i++] = "<td id='";
	buffer[_i++] = data["closeBtnCellId"];
	buffer[_i++] = "'></td>";
	 } 
	buffer[_i++] = "<!-- td class='SubjectCol LabelColName' valign=top>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(ZmMsg.subjectLabel); ;
	buffer[_i++] = "</td --><td class='SubjectCol' valign=middle width=100% colspan=\"2\">";
	buffer[_i++] =  data.subject ;
	buffer[_i++] = "</td></tr><tr id='";
	buffer[_i++] = data["expandRowId"];
	buffer[_i++] = "'><td width=\"1%\" nowrap=\"nowrap\"><table align=right border=0 cellpadding=0 cellspacing=0><tr><td valign=top id='";
	buffer[_i++] = data["expandHeaderId"];
	buffer[_i++] = "' class='arrowbtn'></td><td class='LabelColName' style='vertical-align:middle'><strong>";
	buffer[_i++] =  ZmMsg.sentByLabel ;
	buffer[_i++] = "</strong></td></tr></table></td><td class='LabelColValue' style=\"padding-top: 6px;\"><table border=0 cellpadding=0 cellspacing=0><tr>";
	 if (data.sentByIcon) { 
	buffer[_i++] = "<!-- td valign=top><a href=\"javascript:;\" onclick=\"ZmMailMsgView.contactIconCallback('";
	buffer[_i++] = data["sentByNormal"];
	buffer[_i++] = "', '";
	buffer[_i++] = data["sentByIcon"];
	buffer[_i++] = "'); return false;\">";
	buffer[_i++] =  AjxImg.getImageHtml(data.sentByIcon, "cursor:pointer") ;
	buffer[_i++] = "</a></td --><!-- td>&nbsp;</td -->";
	 } 
	buffer[_i++] = "<td><strong>";
	buffer[_i++] = data["sentBy"];
	buffer[_i++] = "</strong>&nbsp;&nbsp;</td><td class='LabelColName' nowrap='nowrap'><strong>";
	buffer[_i++] =  ZmMsg.onLabel ;
	buffer[_i++] = "</strong></td><td class='LabelColValue' nowrap='nowrap'>";
	buffer[_i++] = data["dateString"];
	buffer[_i++] = "</td></tr></table></td></tr>";
	 if ( data.obo && ( AjxStringUtil.stripTags(data.obo) != AjxStringUtil.stripTags(data.sentBy) ) ) { //If Email zimlet is deployed. 
	buffer[_i++] = "<tr><td width='110' valign='top' class='LabelColName'><strong>";
	buffer[_i++] =  ZmMsg.onBehalfOfLabel ;
	buffer[_i++] = "</strong></td><td class='LabelColValue' colspan=10>";
	buffer[_i++] =  data.obo ;
	buffer[_i++] = "</td></tr>";
	 } 
	 for (var i = 0; i < data.participants.length; i++) { 
	buffer[_i++] = "<tr><td width=110 valign='top' class='LabelColName'>";
	buffer[_i++] =  data.participants[i].prefix ;
	buffer[_i++] = ":</td><td class='LabelColValue' colspan=10>";
	buffer[_i++] =  data.participants[i].partStr ;
	buffer[_i++] = "</td></tr>";
	 } 
	 if(data.hasAttachments) {
	                           var c = (data.attachmentsCount/2+data.attachmentsCount%2);
	                           if(c < 2){ c = 2;}
	                           if(c > 4){ c = 4;} 
	
	                        
	buffer[_i++] = "<tr><td width=\"100\" class=\"LabelColName\"></td><td colspan=\"3\"><div id=\"";
	buffer[_i++] = data["attachId"];
	buffer[_i++] = "\" style='overflow: auto; width:100%; margin-top: 5px;'></div> <!-- height:";
	buffer[_i++] =  c*28 ;
	buffer[_i++] = ";width: ";
	buffer[_i++] =  Math.round(screen.width*0.3) ;
	buffer[_i++] = " --></td></tr>";
	 } 
	buffer[_i++] = "</table></td><td id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_contactArea' class='ContactArea'><!-- Area reserved zimlets to add to message views. --></td></tr></table><div id='";
	buffer[_i++] = data["infoBarId"];
	buffer[_i++] = "'></div></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "mail.Message#MessageHeader"
}, true);
AjxTemplate.register("mail.Message", AjxTemplate.getTemplate("mail.Message#MessageHeader"), AjxTemplate.getParams("mail.Message#MessageHeader"));

AjxTemplate.register("mail.Message#Compose", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	 var labelStyle = "width:"+(AjxEnv.isIE ? 60 : 64)+"px; overflow:visible; white-space:nowrap"; 
	 var inputStyle = AjxEnv.isSafari && !AjxEnv.isSafariNightly ? "height:52px;" : "height:21px; overflow:hidden" 
	buffer[_i++] = "<!-- header --><table id='";
	buffer[_i++] = data["headerId"];
	buffer[_i++] = "' border=0 cellpadding=2 cellspacing=2 width=100%>";
	 if (appCtxt.multiAccounts) { 
	buffer[_i++] = "<tr><td align=right style='";
	buffer[_i++] = labelStyle;
	buffer[_i++] = "'>";
	buffer[_i++] = ZmMsg.fromLabel;
	buffer[_i++] = "</td><td width='96%' colspan=2><div id='";
	buffer[_i++] = data["fromSelectId"];
	buffer[_i++] = "'></div></td></tr>";
	 } else { 
	buffer[_i++] = "<tr id='";
	buffer[_i++] = data["identityRowId"];
	buffer[_i++] = "' style='display:none;'><td align=right style='";
	buffer[_i++] = labelStyle;
	buffer[_i++] = "'><div>";
	buffer[_i++] = ZmMsg.fromLabel;
	buffer[_i++] = "</div></td><td colspan='2'><select id='";
	buffer[_i++] = data["identitySelectId"];
	buffer[_i++] = "'></select></td></tr>";
	 } 
	buffer[_i++] = "<tr id='";
	buffer[_i++] = data["toRowId"];
	buffer[_i++] = "'><td align=right valign=middle style='";
	buffer[_i++] = labelStyle;
	buffer[_i++] = "'><div id='";
	buffer[_i++] = data["toPickerId"];
	buffer[_i++] = "'>";
	buffer[_i++] = ZmMsg.toLabel;
	buffer[_i++] = "</div></td><td width='96%' colspan=2><textarea id='";
	buffer[_i++] = data["toInputId"];
	buffer[_i++] = "' class='addresses' style='";
	buffer[_i++] = inputStyle;
	buffer[_i++] = "' rows=1></textarea></td></tr><tr id='";
	buffer[_i++] = data["ccRowId"];
	buffer[_i++] = "' style='display:none;'><td align=right valign=middle style='";
	buffer[_i++] = labelStyle;
	buffer[_i++] = "'><div id='";
	buffer[_i++] = data["ccPickerId"];
	buffer[_i++] = "'>";
	buffer[_i++] = ZmMsg.ccLabel;
	buffer[_i++] = "</div></td><td><textarea id='";
	buffer[_i++] = data["ccInputId"];
	buffer[_i++] = "' class='addresses' style='";
	buffer[_i++] = inputStyle;
	buffer[_i++] = "' rows=1></textarea></td><td width='5%'><div><a href='#' id='";
	buffer[_i++] = data["bccToggleId"];
	buffer[_i++] = "'>";
	buffer[_i++] = ZmMsg.showBCC;
	buffer[_i++] = "</a></div></td></tr><tr id='";
	buffer[_i++] = data["bccRowId"];
	buffer[_i++] = "' style='display:none;'><td align=right valign=middle style='";
	buffer[_i++] = labelStyle;
	buffer[_i++] = "'><div id='";
	buffer[_i++] = data["bccPickerId"];
	buffer[_i++] = "'>";
	buffer[_i++] = ZmMsg.bccLabel;
	buffer[_i++] = "</div></td><td colspan='2'><textarea id='";
	buffer[_i++] = data["bccInputId"];
	buffer[_i++] = "' class='addresses' style='";
	buffer[_i++] = inputStyle;
	buffer[_i++] = "' rows=1></textarea></td></tr><tr id='";
	buffer[_i++] = data["subjectRowId"];
	buffer[_i++] = "'><td align=right style='";
	buffer[_i++] = labelStyle;
	buffer[_i++] = "'>";
	buffer[_i++] = ZmMsg.subjectLabel;
	buffer[_i++] = "</td><td colspan='2'><table border=0 cellpadding=0 cellspacing=0 width='100%'><tr><td width='99%' style='padding-right:4px;'><input id='";
	buffer[_i++] = data["subjectInputId"];
	buffer[_i++] = "' class='subjectField' autocomplete=off></td><td id='";
	buffer[_i++] = data["identityRowId"];
	buffer[_i++] = "' nowrap width='10%' style='display:none;'><table border=0 cellspacing=0 cellpadding=0><tr><td align=right style='";
	buffer[_i++] = labelStyle;
	buffer[_i++] = "'>";
	buffer[_i++] = ZmMsg.accountLabel;
	buffer[_i++] = "&nbsp;</td><td width=\"*\"><select id='";
	buffer[_i++] = data["identitySelectId"];
	buffer[_i++] = "'></select></td></tr></table></td>";
	 if (appCtxt.get(ZmSetting.MAIL_PRIORITY_ENABLED)) { 
	buffer[_i++] = "<td nowrap width='1%'><table border=0 cellspacing=0 cellpadding=0><tr><td align=right style='";
	buffer[_i++] = labelStyle;
	buffer[_i++] = "' nowrap=\"nowrap\">";
	buffer[_i++] = ZmMsg.priorityLabel;
	buffer[_i++] = "&nbsp;</td><td id=\"";
	buffer[_i++] = data["priorityId"];
	buffer[_i++] = "\"></td></tr></table></td>";
	 } 
	buffer[_i++] = "</tr></table></td></tr><tr id='";
	buffer[_i++] = data["oboRowId"];
	buffer[_i++] = "' style='display:none;'><td align=right><input type='checkbox' id='";
	buffer[_i++] = data["oboCheckboxId"];
	buffer[_i++] = "'></td><td colspan=2><div id='";
	buffer[_i++] = data["oboLabelId"];
	buffer[_i++] = "'></div></td></tr><tr id='";
	buffer[_i++] = data["attRowId"];
	buffer[_i++] = "'><td colspan=3><div id='";
	buffer[_i++] = data["attDivId"];
	buffer[_i++] = "'></div></td></tr></table><!-- compose editor is automatically appended below the header -->";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "mail.Message#Compose"
}, true);

