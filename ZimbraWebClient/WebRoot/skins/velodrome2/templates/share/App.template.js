AjxTemplate.register("velodrome2..templates.share.App", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "";

	return _hasBuffer ? buffer.length : buffer.join("");
},
null, true);
