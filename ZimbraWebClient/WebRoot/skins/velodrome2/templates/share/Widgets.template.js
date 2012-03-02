AjxTemplate.register("share.Widgets#ZmChooseFolderDialog", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table cellpadding=0 cellspacing=0 border=0 width='260'><tr><td class='Label' colspan=2 id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_folderDesc'></td></tr><tr><td colspan=2 id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_folderTreeCell'></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "share.Widgets#ZmChooseFolderDialog"
}, true);
AjxTemplate.register("share.Widgets", AjxTemplate.getTemplate("share.Widgets#ZmChooseFolderDialog"), AjxTemplate.getParams("share.Widgets#ZmChooseFolderDialog"));

AjxTemplate.register("share.Widgets#ZmChooseFolderDialog", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table cellpadding=0 cellspacing=0 border=0 width='260'><tr><td class='Label' colspan=2 id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_folderDesc'><p id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_folderDescDivId' style='margin: 0; margin-bottom: 5px'></p><div>";
	buffer[_i++] =  ZmMsg.chooserDescription ;
	buffer[_i++] = "</div></td></tr><tr><td colspan=2 id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_folderTreeCell'><div id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_inputDivId'></div><div class='overview' id='";
	buffer[_i++] = data["id"];
	buffer[_i++] = "_folderTreeDivId'></div></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "share.Widgets#ZmChooseFolderDialog"
}, true);

