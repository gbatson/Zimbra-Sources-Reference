AjxTemplate.register("share.Quota#UsedLimited", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	
			// set background color based on percent used
			var progressClassName = "quotaUsed";
			if (data.percent < 85 && data.percent > 65)
				progressClassName = "quotaWarning";
			else if (data.percent >= 85)
				progressClassName = "quotaCritical";
		
	buffer[_i++] = "<center><table height=100% border=0 cellpadding=0 cellspacing=0 class='BannerBar'>";
	 var linStyle = AjxEnv.isLinux ? " style='line-height: 13px'" : ""; 
	buffer[_i++] = "<tr ";
	buffer[_i++] =  linStyle ;
	buffer[_i++] = "><td class='BannerTextQuota'>";
	buffer[_i++] =  ZmMsg.email ;
	buffer[_i++] = ":</td><td class='BannerTextQuota'><div class='quotabar'><div style='width:";
	buffer[_i++] = data["percent"];
	buffer[_i++] = "%' class='";
	buffer[_i++] =  progressClassName ;
	buffer[_i++] = "'></div></div></td><td class='BannerTextQuota' style='white-space: nowrap'>";
	buffer[_i++] = data["desc"];
	buffer[_i++] = "</td></tr></table></center>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "share.Quota#UsedLimited"
}, true);
AjxTemplate.register("share.Quota", AjxTemplate.getTemplate("share.Quota#UsedLimited"), AjxTemplate.getParams("share.Quota#UsedLimited"));

