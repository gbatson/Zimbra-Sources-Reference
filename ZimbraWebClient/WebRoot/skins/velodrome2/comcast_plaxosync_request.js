Comcast_plaxosync_requestmgr = function() {
	this.session = null;
	this.token = null;
	this.clientId = "cal";
};

Comcast_plaxosync_requestmgr.prototype.init = function() {
	//this.getCIMAAccess();
	this._cimaQueue = [];
};

Comcast_plaxosync_requestmgr.PLAXO_CONTACT_JSON = window.PLAXO_URL_BASE+"/axis/json/contact";
Comcast_plaxosync_requestmgr.PLAXO_CONTACT_URL = window.PLAXO_URL_BASE+"/axis/contact";
Comcast_plaxosync_requestmgr.PLAXO_FOLDER_JSON = window.PLAXO_URL_BASE+"/axis/json/folder";
Comcast_plaxosync_requestmgr.SEARCH_FIELDS = ["firstName","lastName","fullName"];
Comcast_plaxosync_requestmgr.PROMPT_IF_MISSING = true;

Comcast_plaxosync_requestmgr.prototype.getCIMAAccess = function() {
	this._cimaQueue = [];
	var proxy = "/service/proxy?target="+AjxStringUtil.urlComponentEncode(window.CIMA_URL);
	var token = AjxCookie.getCookie(document, "s_ticket") || (Comcast_plaxosync_requestmgr.PROMPT_IF_MISSING && prompt('Please enter a valid comcast s_ticket cookie value',''));
	if (token) {
		var headers = {
			"Authorization": "cima-session "+token,
			"Content-type": "application/x-www-form-urlencoded"
		};
		var callback = new AjxCallback(this, this._handleGetCIMAAccess);
		AjxRpc.invoke("", proxy, headers, callback, false, 0);
	} else {
		appCtxt.setStatusMsg("Login failed: nonexistent s_ticket", ZmStatusView.LEVEL_CRITICAL);
		this._cimaQueue = null;
	}
};

Comcast_plaxosync_requestmgr.findDescendant = function(obj) {
	if (arguments.length<2) return obj;
	var arr = AjxUtil.isArray(arguments[1]) ? arguments[1] : Array.prototype.slice.call(arguments, 1);
	for (var i=0; i<arr.length; i++) {
		var name = arr[i];
		if (name instanceof RegExp) {
			for (var candidate in obj) {
				if (name.test(candidate)) {
					obj = obj[candidate];
					break;
				}
			}
		} else {
			obj = obj[name];
		}
		if (!obj) return null;
	}
	return obj;
};

Comcast_plaxosync_requestmgr.prototype._handleGetCIMAAccess = function(response) {
	if (response.success) {
		var obj = AjxXmlDoc.createFromDom(response.xml).toJSObject();
		var t = Comcast_plaxosync_requestmgr.findDescendant(obj, "cima:ServiceToken", /^[^:]+:GetSessionResponse/, "response", "session", "__msh_content");
		if (t) {
			if (t.match(/^[\d:]+$/)) // Sessions consist of only numbers and colon
				this.session = t;
			else
				this.token = t;
		}
		this.userid = Comcast_plaxosync_requestmgr.findDescendant(obj, "cima:ServiceToken", /^[^:]+:GetSessionResponse/, "response", "userId", "__msh_content");
		if (!this.token && !this.session) {
			appCtxt.setStatusMsg("Login failed: invalid s_ticket", ZmStatusView.LEVEL_CRITICAL);
		} else {
			if (this._cimaQueue && this._cimaQueue.length) {
				for (var i=0; i<this._cimaQueue.length; i++) {
					this._cimaQueue[i].run();
				}
			}
			this._cimaQueue = null;
		}
	} else {
		appCtxt.setStatusMsg("Login failed: cannot connect to single-signon service", ZmStatusView.LEVEL_CRITICAL);
	}
};

Comcast_plaxosync_requestmgr.prototype.send = function(url, body, callback, useGet, extraHeaders) {
	var cb = new AjxCallback(this, this.receive, [callback || null]);
	var u = "/service/proxy?target="+AjxStringUtil.urlEncode(url);
	if (AjxUtil.isObject(body)) {
		body = AjxStringUtil.objToString(body);
	}

	var headers = {"Content-Type": "application/json; charset=utf-8"};
	if (AjxEnv.isIE6 && (location.protocol == "https:")) {
		headers["Connection"] = "Close";
	}
	if (extraHeaders && AjxUtil.isObject(extraHeaders)) {
		headers = Comcast_plaxosync_requestmgr.hashUpdate(headers, extraHeaders, true);
	}
	AjxRpc.invoke(body, u, headers, cb, useGet);
};

Comcast_plaxosync_requestmgr.prototype.receive = function(callback, response) {
	if (response.success && callback) {
		var data = Comcast_plaxosync_requestmgr.evalJSON(AjxStringUtil.trim(response.text));
		callback.run(data);
	}
};

Comcast_plaxosync_requestmgr.prototype.createContactRequest = function(request, callback) {
	if (this.isLoggedIn()) {
		var attrSets = request.cn.a;
		var params = {};
		var attrs = {};
		for (var i=0; i<attrSets.length; i++) {
			var attrSet = attrSets[i];
			attrs[attrSet.n] = attrSet._content;
		}
		if (!attrs["firstName"] && !attrs.containsKey["lastName"] && !attrs["fullName"] && !attrs["displayName"]) {
			if (attrs["email"]) {
				attrs["fullName"] = attrs["email"];
			}
		}
		var root = {
			"AddContactRequest": {
				"authInfo": this.getAuthInfo(),
				"contact": Comcast_plaxosync_map.map_z2p(attrs)
			}
		};
		this.send(Comcast_plaxosync_requestmgr.PLAXO_CONTACT_JSON, root, new AjxCallback(this, this.createContactResponse, [request, callback]), false);
	} else if (this._cimaQueue !== null) {
		this._cimaQueue.push(new AjxCallback(this, this.createContactRequest, [request, callback]));
	} else {
		this.createContactResponse(request, callback, {});
	}
};


Comcast_plaxosync_requestmgr.prototype.createContactResponse = function(request, callback, data) {
	var rawContact = data && data["contact"] && Comcast_plaxosync_map.map_p2z(data["contact"]);
	var attrs,id,idStr,fileAs;
	if (rawContact) {
		attrs = this.pruneAttrs(rawContact);
		id = rawContact.itemId;
		idStr = ""+id;
		fileAs = ZmContact.computeFileAs(attrs);
	}
	
	var account = appCtxt.getActiveAccount().name;
	var folderId = request.cn.l;
	var time = new Date().getTime();
	
	var rev = 40500;
	var seq = appCtxt.getRequestMgr()._highestNotifySeen+1;

	var response = {
		Header:{
			context:{
				change:{token:rev},
				notify:[
					{
						seq:seq,
						created:rawContact?{
							cn:[
								{id:idStr, l:folderId, d:time, rev:rev, fileAsStr:fileAs, _attrs:attrs}
							]
						} : {},
						modified:rawContact?{
							folder:[
								{id:folderId, n:8, s:0, i4ms:rev, i4next:id+1}
							]
						}:{}
					}
				],
				_jsns:"urn:zimbra"
			}
		},
		Body:{
			CreateContactResponse:{
				cn:rawContact?[
					{id:idStr, l:folderId, d:time, rev:rev, fileAsStr:fileAs, _attrs:attrs}
				]:[],
				_jsns:"urn:zimbraMail"
			}
		},
		_jsns:"urn:zimbraSoap"
	};
	if (callback)
		callback.run(response);
	return response;
};

Comcast_plaxosync_requestmgr.prototype.modifyContactRequest = function(request, callback) {
	if (this.isLoggedIn()) {
		var id = request.cn.id;
		var attrSets = request.cn.a;
		var params = {};
		var attrs = {};
		for (var i=0; i<attrSets.length; i++) {
			var attrSet = attrSets[i];
			attrs[attrSet.n] = attrSet._content;
		}
		var addressFieldFound;
		for (var i=0; i<ZmContact.ADDRESS_FIELDS.length && !addressFieldFound; i++) {
			if (params[ZmContact.ADDRESS_FIELDS[i]])
				addressFieldFound=true;
		}
		if (!Comcast_plaxosync_requestmgr.NAME_FIELDS) {
			Comcast_plaxosync_requestmgr.NAME_FIELDS = [
				ZmContact.F_firstName,
				ZmContact.F_lastName,
				ZmContact.F_maidenName,
				ZmContact.F_middleName,
				ZmContact.F_namePrefix,
				ZmContact.F_nameSuffix
			];
		}
		var nameFieldFound;
		for (var i=0; i<Comcast_plaxosync_requestmgr.NAME_FIELDS.length && !nameFieldFound; i++) {
			if (attrs[Comcast_plaxosync_requestmgr.NAME_FIELDS[i]])
				nameFieldFound=true;
		}
		if (addressFieldFound || nameFieldFound) {
			var contact = appCtxt.getById(id);
			if (contact) {
				if (addressFieldFound)
					Comcast_plaxosync_requestmgr.hashUpdate(attrs, contact.attr, true, null, ZmContact.ADDRESS_FIELDS);
				if (nameFieldFound)
					Comcast_plaxosync_requestmgr.hashUpdate(attrs, contact.attr, true, null, Comcast_plaxosync_requestmgr.NAME_FIELDS);
			}
		}

		attrs["itemId"] = id;
		var root = {
			"SetContactRequest": {
				"authInfo": this.getAuthInfo(),
				"contact": Comcast_plaxosync_map.map_z2p(attrs)
			}
		};
		this.send(Comcast_plaxosync_requestmgr.PLAXO_CONTACT_JSON, root, new AjxCallback(this, this.modifyContactResponse, [request, callback]), false);
	} else if (this._cimaQueue !== null) {
		this._cimaQueue.push(new AjxCallback(this, this.modifyContactRequest, [request, callback]));
	} else {
		this.modifyContactResponse(request, callback, {});
	}
};

Comcast_plaxosync_requestmgr.hashUpdate =
function(hash1, hash2, overwrite, ignore, include) {
	for (var key in hash2) {
		if ((overwrite || !(key in hash1)) && (!ignore || AjxUtil.indexOf(ignore, key)==-1) && (!include || AjxUtil.indexOf(include, key)!=-1)) {
			hash1[key] = hash2[key];
		}
	}
	return hash1;
};

Comcast_plaxosync_requestmgr.prototype.modifyContactResponse = function(request, callback, data) {
	var rawContact = data && data["contact"] && Comcast_plaxosync_map.map_p2z(data["contact"]);
	var attrs,fileAs,id,idStr;
	if (rawContact) {
		attrs = this.pruneAttrs(rawContact);
		fileAs = ZmContact.computeFileAs(attrs);
		id = rawContact.itemId;
		idStr = ""+id;
	}
	var account = appCtxt.getActiveAccount().name;
	var folderId = request.cn.l;
	var time = new Date().getTime();
	
	var rev = 41200;
	var seq = appCtxt.getRequestMgr()._highestNotifySeen+1;

	var response = {
		Header:{
			context:{
				change:{token:rev},
				notify:[
					{
						seq:seq,
						modified: rawContact ? {
							folder:[
								{id:folderId, n:12, s:0, i4ms:rev, i4next:id+1}
							],
							cn:[
								{id:id, f:"", d:time, rev:rev, fileAsStr:fileAs, _attrs:attrs}
							]
						} : {}
					}
				],
				_jsns:"urn:zimbra"
			}
		},
		Body:{
			ModifyContactResponse:{
				cn:rawContact ? [
					{id:id, l:folderId, d:time, rev:rev, fileAsStr:fileAs, _attrs:attrs}
				] : [],
				_jsns:"urn:zimbraMail"
			}
		},
		_jsns:"urn:zimbraSoap"
	};
	if (callback)
		callback.run(response);
	return response;
};

Comcast_plaxosync_requestmgr.prototype.contactActionRequest = function(request, callback) {
	var id = request.action.id;
	var folderId = request.action.l;
	var op = request.action.op;

	if (op == "delete" || (op == "move" && folderId == 3) || op == "trash") {
		this.deleteContactRequest(new AjxCallback(this, this.contactActionResponse, [request, callback]), id);
	}
};

Comcast_plaxosync_requestmgr.prototype.deleteContactRequest = function(callback, id) {
	return this.deleteContactsRequest(callback, [id]);
};

Comcast_plaxosync_requestmgr.prototype.deleteContactsRequest = function(callback, ids) {
	if (this.isLoggedIn()) {
	var root = {
		"DeleteContactsRequest": {
			"authInfo": this.getAuthInfo(),
			"itemIds": {
				"delimited": {
					"data": ids.join(",")
				}
			}
		}
	};
	this.send(Comcast_plaxosync_requestmgr.PLAXO_CONTACT_JSON, root, new AjxCallback(this, this.deleteContactsResponse, [callback]), false);
	} else if (this._cimaQueue !== null) {
		this._cimaQueue.push(new AjxCallback(this, this.deleteContactsRequest, [callback, ids]));
	} else {
		this.deleteContactsResponse(callback, {});
	}
};

Comcast_plaxosync_requestmgr.prototype.deleteContactsResponse = function(callback, data) {
	var affected = data && data["affected"];
	var code = data["response"] && data["response"]["code"];
	if (callback) {
		callback.run(data);
	}
};

Comcast_plaxosync_requestmgr.prototype.contactActionResponse = function(request, callback, data) {
	var success = !!(data && data["affected"]);
	var account = appCtxt.getActiveAccount().name;

	var id = request.action.id;
	var item = appCtxt.getById(id);
	var folderId = request.action.l;
	var oldFolderId = item.folderId;
	var op = request.action.op;
	var idStr = ""+id;

	var time = new Date().getTime();
	var rev = 41200;
	var seq = appCtxt.getRequestMgr()._highestNotifySeen+1;

	var response = {
		Header:{
			context:{
				change:{token:rev},
				notify:[
					{
						seq:seq,
						modified:success?{
							folder:[
								{id:oldFolderId, n:11, s:0, i4ms:rev, i4next:5261},
								{id:folderId, n:167, s:491637, i4ms:rev, i4next:5280}
							],
							cn:[
								{id:idStr,l:folderId}
							]
						}:{}
					}
				],
				_jsns:"urn:zimbra"
			}
		},
		Body:{
			ContactActionResponse:{
				action:{
					id:idStr,
					op:op
				},
				_jsns:"urn:zimbraMail"
			}
		},
		_jsns:"urn:zimbraSoap"
	};
	if (callback)
		callback.run(response);
	return response;
};

Comcast_plaxosync_requestmgr.prototype.search = function(params, callback) {
	if (this.isLoggedIn()) {
		var queryString = params.query && AjxStringUtil.trim(params.query) || "";
		var returnfields = params.fields;
		var locFound,match,location,searchfor;

		var p = queryString && Comcast_plaxosync.parseQuery(queryString);
		var limit = params.limit || 0;
		var offset = params.offset || 0;
		var query = p && p.searchfor;
		if (query)
            query = query.replace(/[^\w\d]/g,"");

		var folderId = -1;
		var root = {};
		var request = {};

		request["authInfo"] = this.getAuthInfo();
	
		if (limit > 0) {
			var page = {};
			page["limit"] = limit;
			if (offset > 0) {
				page["offset"] = offset;
			}
			request["page"] = page;
		}

		var hasquery = (query && query.length);
		var hasfolder = (folderId>=0);

		if (hasquery || hasfolder) {
			var filters = {};
			if (hasquery) {
				var byFields = {};
				var fields = {};
				var byFieldArr = [];
				for (var i=0; i<Comcast_plaxosync_requestmgr.SEARCH_FIELDS.length; i++) {
					var byField = {
						"field": Comcast_plaxosync_requestmgr.SEARCH_FIELDS[i],
						"op": "StartsWith",
						"value": query
					}
					byFieldArr.push(byField);
				}
				fields["byField"] = byFieldArr;
				byFields["fields"] = fields;
				byFields["op"] = "Or";
				filters["byFields"] = byFields;
			}
			if (hasfolder) {
				filters["byFolderId"] = folderId;
			}
			request["filters"] = filters;
		}
		if (returnfields) {
			var fieldsArr = [];
			for (var i=0; i<returnfields.length; i++) {
				fieldsArr.push(returnfields[i].toString());
			}
			request["fields"] = {"field":fieldsArr};
		}
		root["GetContactsRequest"] = request;
		this.send(Comcast_plaxosync_requestmgr.PLAXO_CONTACT_JSON, root, new AjxCallback(this, this._handleSearch, [callback, offset]), false);
	} else if (this._cimaQueue !== null) {
		this._cimaQueue.push(new AjxCallback(this, this.search, [params, callback]));
	} else {
		this._handleSearch(callback);
	}
};

Comcast_plaxosync_requestmgr.prototype._handleSearch = function(callback, offset, data) {
	var contacts = data && data["contacts"] && data["contacts"]["contact"];
	var count = data && data["editCounter"];
	var totalCount = data && data["totalCount"];
	if (callback) {
		callback.run(contacts && Comcast_plaxosync_map.map_p2z(contacts), count, totalCount, offset);
	}
};

Comcast_plaxosync_requestmgr.prototype.searchRequest = function(params, callback) {
	this.search(params, new AjxCallback(this, this.searchResponse, [callback]));
};

Comcast_plaxosync_requestmgr.prototype.searchResponse = function(callback, rawContacts, count, totalCount, offset) {
	var rev = 41200;
	rawContacts = rawContacts || [];
	var envContacts = [];
	var time = new Date().getTime();

	for (var i=0; i<rawContacts.length; i++) {
		var rawContact = rawContacts[i];
		var attrs = this.pruneAttrs(rawContact);
		var envContact = {id:rawContact.itemId, f:"", d:time, rev:rev, fileAsStr:ZmContact.computeFileAs(rawContact), _attrs:attrs};
		envContacts.push(envContact);
	}
	envContacts.sort(function(a,b){return a.fileAsStr>b.fileAsStr});

	var account = appCtxt.getActiveAccount().name;

	var time = new Date().getTime();

	var response = {
		Header:{
			context:{
				change:{token:rev},
				_jsns:"urn:zimbra"
			}
		},
		Body:{
			SearchResponse:{
				sortBy:"com.zimbra.cs.index.LocalizedSortBy@a4f040",
				offset:offset || 0,
				cn:	envContacts,
				more:(rawContacts.length + offset < totalCount) ? true : false,
				_jsns:"urn:zimbraMail"
			}
		},
		_jsns:"urn:zimbraSoap"
	};

	if (callback)
		callback.run(response);
	return response;
};

Comcast_plaxosync_requestmgr.prototype.autoCompleteRequest = function(request, callback) {
	var params = {
		query: request.name._content
	};
	this.search(params, new AjxCallback(this, this.autoCompleteResponse, [request, callback]));
};

Comcast_plaxosync_requestmgr.prototype.autoCompleteResponse = function(request, callback, rawContacts) {
    var rev = 41200;
    var reqname = request.name._content;
    var envContacts = [];
    if (rawContacts) {
		var time = new Date().getTime();
        for (var i=0; i<rawContacts.length && i<1000; i++) {
            var rawContact = rawContacts[i];
            var attrs = this.pruneAttrs(rawContact);
            var name = this.getFirstMatching(reqname, [attrs.displayName, attrs.fullName, AjxUtil.collapseList([attrs.firstName,attrs.middleName,attrs.lastName]).join(" ")]);
            var emails = [attrs[ZmContact.F_email], attrs[ZmContact.F_workEmail1], attrs[ZmContact.F_email2], attrs[ZmContact.F_workEmail2], attrs[ZmContact.F_email3], attrs[ZmContact.F_workEmail3]];
            var presentEmails = AjxUtil.uniq(AjxUtil.collapseList(emails));
 
            for (var j=0; j<presentEmails.length; j++) {
                var email = presentEmails[j];
                var full = new AjxEmailAddress(email, null, name).toString(); 
                var score = this.getMatchScore(reqname, name, email);
                var envContact = {id:rawContact.itemId, email: full, type:"contact", ranking:score};
                envContacts.push(envContact);
            }
        }
        envContacts.sort(function(a,b){return a.ranking && a.ranking<b.ranking});
    }
 
    var response = {
        Header:{
            context:{
                change:{token:rev},
                _jsns:"urn:zimbra"
            }
        },
        Body:{
            AutoCompleteResponse:{
                canBeCached:true,
                match: envContacts,
                _jsns:"urn:zimbraMail"
            }
        },
        _jsns:"urn:zimbraSoap"
    };
 
    if (callback)
        callback.run(response);
    return response;
};

Comcast_plaxosync_requestmgr.prototype.getContacts = function(callback) {
	this.search({fields:["NameFields","EmailFields","PhoneFields"]}, new AjxCallback(this, this._handleGetContacts, [callback]));
};

Comcast_plaxosync_requestmgr.prototype._handleGetContacts = function(callback, rawContacts) {
	if (rawContacts) {
		var contacts = {};
		for (var i=0; i<rawContacts.length; i++) {
			contacts[rawContacts[i].itemId] = this.pruneAttrs(rawContacts[i]);
		}
		if (callback)
			callback.run(contacts);
		return contacts;
	}
};

Comcast_plaxosync_requestmgr.prototype.getFirstMatching = function(str, candidates, def) {
	var regex = new RegExp(str,i);
	for (var i=0; i<candidates.length; i++) {
		if (regex.test(candidates[i]))
			return candidates[i];
	}
	if (!AjxUtil.isUndefined(def)) return def;
	for (var i=0; i<candidates.length; i++) {
		if (candidates[i])
			return candidates[i];
	}
	return candidates[candidates.length-1];
};

Comcast_plaxosync_requestmgr.prototype.getMatchScore = function(match, name, email) {
	var score = 0;
	if (match) {
		match = match.toLowerCase();
		name = name && name.toLowerCase();
		email = email && email.toLowerCase();
		if (name) {
			var nIndex = name.indexOf(match);
			if (nIndex != -1)
				score += (name.length - nIndex) / name.length;
		}
		if (email) {
			var nIndex = email.indexOf(match);
			if (nIndex != -1)
				score += (email.length - nIndex) / email.length;
		}
	}
	return 0.5 * score;
};

Comcast_plaxosync_requestmgr.evalJSON = function(str) {
	try {
		return JSON.parse(str);
	}
	catch (e) {
		return {};
	}
};

Comcast_plaxosync_requestmgr.prototype.pruneAttrs = function(rawAttrs) {
	if (!this.validAttrs) {
		if (ZmContact && ZmContact.DISPLAY_FIELDS && window.VelodromeSkinOther) {
			this.validAttrs = AjxUtil.arrayAsHash(ZmContact.DISPLAY_FIELDS);
		} else {
			return Comcast_plaxosync_requestmgr.hashUpdate({}, rawAttrs, true, ["itemId"]);
		}
	}
	var attrs = {};
	for (var key in rawAttrs) {
		var k = key.replace(/\d+$/,"");
		if (this.validAttrs[k]) {
			attrs[key] = rawAttrs[key];
		}
	}
	return attrs;
	//return rawAttrs;
};

Comcast_plaxosync_requestmgr.prototype.urlFormat = function(hash) {
	var buf=[], i=0;
	for (var key in hash) {
		if (hash[key]!==undefined)
			buf[i++] = key+"="+hash[key];
	}
	return buf.join("&");
};

Comcast_plaxosync_requestmgr.prototype.getCommonParams = function() {
	if (!this._commonParams) {
		this._commonParams = {
			u: appCtxt.getActiveAccount().getEmail()
		};
	}
	return this._commonParams;
}

//----------------------------------------------------------------
// AUTH

Comcast_plaxosync_requestmgr.prototype.isLoggedIn = function() {
	return !!((this.userid && this.token) || this.session);
};

Comcast_plaxosync_requestmgr.prototype.getSessionType = function() {
	return "ShortSession";
};

Comcast_plaxosync_requestmgr.prototype.getClientInfo = function() {
	var clientInfo = {};
	clientInfo["clientId"] = this.clientId;
	return clientInfo;
};

Comcast_plaxosync_requestmgr.prototype.getAuthInfo = function() {
	var auth = {};
	auth["sessionType"] = "NoSession";
	if (this.clientId)
		auth["clientInfo"] = this.getClientInfo();
	if (this.userid && this.token)
		auth["authByToken"] = Comcast_plaxosync_requestmgr.getAuthByToken(this.userid, this.token);
	else if (this.session)
		auth["authBySession"] = Comcast_plaxosync_requestmgr.getAuthBySession(this.session);
	return auth;
};

Comcast_plaxosync_requestmgr.prototype.loginPlain = function(username, password) {
	var root = {
		"GetSessionRequest": {
			"authInfo": {
				"sessionType": this.getSessionType(),
				"clientInfo": this.getClientInfo(),
				"authByEmail": Comcast_plaxosync_requestmgr.getAuthByEmail(username, password)
			}
		}
	};
	this.send(Comcast_plaxosync_requestmgr.PLAXO_CONTACT_JSON, root, new AjxCallback(this, this._handleLoginResponse, [username]), false);
};

Comcast_plaxosync_requestmgr.prototype._handleLoginResponse = function(username, data) {
	if (data) {
		var response = data["response"];
		if (response) {
			var code = response["code"];
			if (code == 200) {
				this.session = response["session"];
				this.username = username;
				return;
			} else {
				appCtxt.setStatusMsg("Login failed: "+response["message"]+" (Code "+response["code"]+")", ZmStatusView.LEVEL_CRITICAL);
				return;
			}
		}
	}
	appCtxt.setStatusMsg("Login failed: Plaxo server sent invalid response", ZmStatusView.LEVEL_CRITICAL);
};

Comcast_plaxosync_requestmgr.getAuthBySession = function(session) {
	return {"session": session};
};

Comcast_plaxosync_requestmgr.getAuthByUhid = function(userid, password) {
	return {"userId": userid, "password": password};
};

Comcast_plaxosync_requestmgr.getAuthByEmail = function(email, password) {
	return {"email":email, "password": password};
};

Comcast_plaxosync_requestmgr.getAuthByComcast = function(comcastUsername, password) {
	return {"comcastUsername": comcastUsername, "password": password};
};

Comcast_plaxosync_requestmgr.getAuthByIdentity = function(identity, password) {
	return {"identity": identity, "password": password};
};

Comcast_plaxosync_requestmgr.getAuthByToken = function(userId, token) {
	return {"userId": userId, "token": token};
};


