/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * @overview
 * This file contains the command class.
 */

/**
 * Creates a command.
 * @class
 * This class represents a command.
 * 
 */
ZmCsfeCommand = function() {
};

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmCsfeCommand.prototype.toString =
function() {
	return "ZmCsfeCommand";
};

// Static properties

// Global settings for each CSFE command
ZmCsfeCommand._COOKIE_NAME = "ZM_AUTH_TOKEN";
ZmCsfeCommand.serverUri = null;

ZmCsfeCommand._sessionId = null;	// current session ID
ZmCsfeCommand._staleSession = {};	// old sessions

// Reasons for re-sending a request
ZmCsfeCommand.REAUTH	= "reauth";
ZmCsfeCommand.RETRY		= "retry";

// Static methods

/**
 * Gets the auth token cookie.
 * 
 * @return	{String}	the auth token
 */
ZmCsfeCommand.getAuthToken =
function() {
	return AjxCookie.getCookie(document, ZmCsfeCommand._COOKIE_NAME);
};

/**
 * Sets the auth token cookie name.
 * 
 * @param	{String}	cookieName		the cookie name to user
 */
ZmCsfeCommand.setCookieName =
function(cookieName) {
	ZmCsfeCommand._COOKIE_NAME = cookieName;
};

/**
 * Sets the server URI.
 * 
 * @param	{String}	uri		the URI
 */
ZmCsfeCommand.setServerUri =
function(uri) {
	ZmCsfeCommand.serverUri = uri;
};

/**
 * Sets the auth token.
 * 
 * @param	{String}	authToken		the auth token
 * @param	{int}		lifetimeMs		the token lifetime in milliseconds
 * @param	{String}	sessionId		the session id
 * @param	{Boolean}	secure		<code>true</code> for secure
 * 
 */
ZmCsfeCommand.setAuthToken =
function(authToken, lifetimeMs, sessionId, secure) {
	ZmCsfeCommand._curAuthToken = authToken;
	if (lifetimeMs != null) {
		var exp = null;
		if(lifetimeMs > 0) {
			exp = new Date();
			var lifetime = parseInt(lifetimeMs);
			exp.setTime(exp.getTime() + lifetime);
		}
		AjxCookie.setCookie(document, ZmCsfeCommand._COOKIE_NAME, authToken, exp, "/", null, secure);
	} else {
		AjxCookie.deleteCookie(document, ZmCsfeCommand._COOKIE_NAME, "/");
	}
	if (sessionId) {
		ZmCsfeCommand.setSessionId(sessionId);
	}
};

/**
 * Clears the auth token cookie.
 * 
 */
ZmCsfeCommand.clearAuthToken =
function() {
	AjxCookie.deleteCookie(document, ZmCsfeCommand._COOKIE_NAME, "/");
};

/**
 * Gets the session id.
 * 
 * @return	{String}	the session id
 */
ZmCsfeCommand.getSessionId =
function() {
	return ZmCsfeCommand._sessionId;
};

/**
 * Sets the session id.
 * 
 * @param	{String}	sessionId		the session id
 * 
 */
ZmCsfeCommand.setSessionId =
function(id) {
	var sid = (typeof id == "number") ? id : ZmCsfeCommand.extractSessionId(id);
	if (sid) {
		ZmCsfeCommand._sessionId = sid;
	}
};

ZmCsfeCommand.clearSessionId =
function() {
	ZmCsfeCommand._sessionId = null;
};

ZmCsfeCommand.extractSessionId =
function(session) {
	if (!session) { return null; }
	var id = (session instanceof Array) ? session[0].id : session.id;
	return id ? parseInt(id) : null;
};

/**
 * Converts a fault to an exception.
 * 
 * @param	{Hash}	fault		the fault
 * @param	{Hash}	params		a hash of parameters
 * @return	{ZmCsfeException}	the exception
 */
ZmCsfeCommand.faultToEx =
function(fault, params) {
	var newParams = {
		msg: AjxStringUtil.getAsString(fault.Reason.Text),
		code: AjxStringUtil.getAsString(fault.Detail.Error.Code),
		method: (params ? params.methodNameStr : null),
		detail: AjxStringUtil.getAsString(fault.Code.Value),
		data: fault.Detail.Error.a,
		trace: (fault.Detail.Error.Trace || "")
	};

	var request;
	if (params) {
		if (params.soapDoc) {
			// note that we don't pretty-print XML if we get a soapDoc
			newParams.request = params.soapDoc.getXml();
		} else if (params.jsonRequestObj) {
			if (params.jsonRequestObj && params.jsonRequestObj.Header && params.jsonRequestObj.Header.context) {
				params.jsonRequestObj.Header.context.authToken = "(removed)";
			}
			newParams.request = AjxStringUtil.prettyPrint(params.jsonRequestObj, true);
		}
	}

	return new ZmCsfeException(newParams);
};

/**
 * Gets the method name of the given request or response.
 *
 * @param {AjxSoapDoc|Object}	request	the request
 * @return	{String}			the method name or "[unknown]"
 */
ZmCsfeCommand.getMethodName =
function(request) {

	// SOAP request
	var methodName = (request && request._methodEl && request._methodEl.tagName)
		? request._methodEl.tagName : null;

	if (!methodName) {
		for (var prop in request) {
			if (/Request|Response$/.test(prop)) {
				methodName = prop;
				break;
			}
		}
	}
	return (methodName || "[unknown]");
};

/**
 * Sends a SOAP request to the server and processes the response. The request can be in the form
 * of a SOAP document, or a JSON object.
 *
 * @param {Hash}	params			a hash of parameters
 * @param	{AjxSoapDoc}	params.soapDoc			the SOAP document that represents the request
 * @param	{Object}	params.jsonObj			the JSON object that represents the request (alternative to soapDoc)
 * @param	{Boolean}	params.noAuthToken		if <code>true</code>, the check for an auth token is skipped
 * @param	{Boolean}	params.authToken		authToken to use instead of the local one
 * @param	{String}	params.serverUri			the URI to send the request to
 * @param	{String}	params.targetServer		the host that services the request
 * @param	{Boolean}	params.useXml			if <code>true</code>, an XML response is requested
 * @param	{Boolean}	params.noSession			if <code>true</code>, no session info is included
 * @param	{String}	params.changeToken		the current change token
 * @param	{int}	params.highestNotifySeen 	the sequence # of the highest notification we have processed
 * @param	{Boolean}	params.asyncMode			if <code>true</code>, request sent asynchronously
 * @param	{AjxCallback}	params.callback		the callback to run when response is received (async mode)
 * @param	{Boolean}	params.logRequest		if <code>true</code>, SOAP command name is appended to server URL
 * @param	{String}	params.accountId			the ID of account to execute on behalf of
 * @param	{String}	params.accountName		the name of account to execute on behalf of
 * @param	{Boolean}	params.skipAuthCheck		if <code>true</code> to skip auth check (i.e. do not check if auth token has changed)
 * @param	{constant}	params.resend			the reason for resending request
 */
ZmCsfeCommand.prototype.invoke =
function(params) {
	this.cancelled = false;
	if (!(params && (params.soapDoc || params.jsonObj))) { return; }

	var requestStr = ZmCsfeCommand.getRequestStr(params);

	var rpcCallback;
	try {
		var uri = (params.serverUri || ZmCsfeCommand.serverUri) + params.methodNameStr;
		this._st = new Date();
		
		var requestHeaders = {"Content-Type": "application/soap+xml; charset=utf-8"};
		if (AjxEnv.isIE6 && (location.protocol == "https:")) { //bug 22829
			requestHeaders["Connection"] = "Close";
		}
			
		if (params.asyncMode) {
			//DBG.println(AjxDebug.DBG1, "set callback for asynchronous response");
			rpcCallback = new AjxCallback(this, this._runCallback, [params]);
			this._rpcId = AjxRpc.invoke(requestStr, uri, requestHeaders, rpcCallback);
		} else {
			//DBG.println(AjxDebug.DBG1, "parse response synchronously");
			var response = AjxRpc.invoke(requestStr, uri, requestHeaders);
			return (!params.returnXml) ? (this._getResponseData(response, params)) : response;
		}
	} catch (ex) {
		this._handleException(ex, params, rpcCallback);
	}
};

/**
 * Sends a REST request to the server via GET and returns the response.
 *
 * @param {Hash}	params			a hash of parameters
 * @param	{String}       params.restUri			the REST URI to send the request to
 * @param	{Boolean}       params.asyncMode			if <code>true</code> request sent asynchronously
 * @param	{AjxCallback}	params.callback			the callback to run when response is received (async mode)
 */
ZmCsfeCommand.prototype.invokeRest =
function(params) {

	if (!(params && params.restUri)) { return; }

	var rpcCallback;
	try {
		this._st = new Date();
		if (params.asyncMode) {
			rpcCallback = new AjxCallback(this, this._runCallback, [params]);
			this._rpcId = AjxRpc.invoke(null, params.restUri, null, rpcCallback, true);
		} else {
			var response = AjxRpc.invoke(null, params.restUri, null, null, true);
			return response.text;
		}
	} catch (ex) {
		this._handleException(ex, params, rpcCallback);
	}
};

/**
 * Cancels this request (which must be async).
 * 
 */
ZmCsfeCommand.prototype.cancel =
function() {
	if (!this._rpcId) { return; }
	this.cancelled = true;
	var req = AjxRpc.getRpcRequestById(this._rpcId);
	if (req) {
		req.cancel();
	}
};

/**
 * Gets the request string.
 * 
 * @param	{Hash}	params		a hash of parameters
 * @return	{String}	the request string
 */
ZmCsfeCommand.getRequestStr =
function(params) {
	return 	params.soapDoc ? ZmCsfeCommand._getSoapRequestStr(params) : ZmCsfeCommand._getJsonRequestStr(params);
};

/**
 * @private
 */
ZmCsfeCommand._getJsonRequestStr =
function(params) {

	var obj = {Header:{}, Body:params.jsonObj};

	var context = obj.Header.context = {_jsns:"urn:zimbra"};
	var ua_name = ["ZimbraWebClient - ", AjxEnv.browser, " (", AjxEnv.platform, ")"].join("");
	context.userAgent = {name:ua_name};
	if (ZmCsfeCommand.clientVersion) {
		context.userAgent.version = ZmCsfeCommand.clientVersion;
	}
	if (params.noSession) {
		context.nosession = {};
	} else {
		var sessionId = ZmCsfeCommand.getSessionId();
		if (sessionId) {
			context.session = {_content:sessionId, id:sessionId};
		} else {
			context.session = {};
		}
	}
	if (params.targetServer) {
		context.targetServer = {_content:params.targetServer};
	}
	if (params.highestNotifySeen) {
		context.notify = {seq:params.highestNotifySeen};
	}
	if (params.changeToken) {
		context.change = {token:params.changeToken, type:"new"};
	}

	// if we're not checking auth token, we don't want token/acct mismatch	
	if (!params.skipAuthCheck) {
		if (params.accountId) {
			context.account = {_content:params.accountId, by:"id"}
		} else if (params.accountName) {
			context.account = {_content:params.accountName, by:"name"}
		}
	}
	
	// Tell server what kind of response we want
	if (params.useXml) {
		context.format = {type:"xml"};
	}

	params.methodNameStr = ZmCsfeCommand.getMethodName(params.jsonObj);

	// Get auth token from cookie if required
	if (!params.noAuthToken) {
		var authToken = params.authToken || ZmCsfeCommand.getAuthToken();
		if (!authToken) {
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, params.methodNameStr);
		}
		if (ZmCsfeCommand._curAuthToken && !params.skipAuthCheck && 
			(params.resend != ZmCsfeCommand.REAUTH) && (authToken != ZmCsfeCommand._curAuthToken)) {
			throw new ZmCsfeException("AuthToken has changed", ZmCsfeException.AUTH_TOKEN_CHANGED, params.methodNameStr);
		}
		context.authToken = ZmCsfeCommand._curAuthToken = authToken;
	}

	if (window.DBG) {
		var ts = DBG._getTimeStamp();
		DBG.println(["<b>", params.methodNameStr, params.asyncMode ? " (asynchronous)" : "" , " - ", ts, "</b>"].join(""), params.methodNameStr);
		DBG.dumpObj(AjxDebug.DBG1, obj);
	}

	params.jsonRequestObj = obj;

	return AjxStringUtil.objToString(obj);
};

/**
 * @private
 */
ZmCsfeCommand._getSoapRequestStr =
function(params) {

	var soapDoc = params.soapDoc;

	if (!params.resend) {

		// Add the SOAP header and context
		var hdr = soapDoc.createHeaderElement();
		var context = soapDoc.set("context", null, hdr, "urn:zimbra");
	
		var ua = soapDoc.set("userAgent", null, context);
		var name = ["ZimbraWebClient - ", AjxEnv.browser, " (", AjxEnv.platform, ")"].join("");
		ua.setAttribute("name", name);
		if (ZmCsfeCommand.clientVersion) {
			ua.setAttribute("version", ZmCsfeCommand.clientVersion);
		}
	
		if (params.noSession) {
			soapDoc.set("nosession", null, context);
		} else {
			var sessionId = ZmCsfeCommand.getSessionId();
			var si = soapDoc.set("session", null, context);
			if (sessionId) {
				si.setAttribute("id", sessionId);
			}
		}
		if (params.targetServer) {
			soapDoc.set("targetServer", params.targetServer, context);
		}
		if (params.highestNotifySeen) {
		  	var notify = soapDoc.set("notify", null, context);
		  	notify.setAttribute("seq", params.highestNotifySeen);
		}
		if (params.changeToken) {
			var ct = soapDoc.set("change", null, context);
			ct.setAttribute("token", params.changeToken);
			ct.setAttribute("type", "new");
		}
	
		// if we're not checking auth token, we don't want token/acct mismatch	
		if (!params.skipAuthCheck) {
			if (params.accountId) {
				var acc = soapDoc.set("account", params.accountId, context);
				acc.setAttribute("by", "id");
			} else if (params.accountName) {
				var acc = soapDoc.set("account", params.accountName, context);
				acc.setAttribute("by", "name");
			}
		}
		
		// Tell server what kind of response we want
		if (!params.useXml) {
			var js = soapDoc.set("format", null, context);
			js.setAttribute("type", "js");
		}
	}

	params.methodNameStr = ZmCsfeCommand.getMethodName(soapDoc);

	// Get auth token from cookie if required
	if (!params.noAuthToken) {
		var authToken = params.authToken || ZmCsfeCommand.getAuthToken();
		if (!authToken) {
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, params.methodNameStr);
		}
		if (ZmCsfeCommand._curAuthToken && !params.skipAuthCheck && 
			(params.resend != ZmCsfeCommand.REAUTH) && (authToken != ZmCsfeCommand._curAuthToken)) {
			throw new ZmCsfeException("AuthToken has changed", ZmCsfeException.AUTH_TOKEN_CHANGED, params.methodNameStr);
		}
		ZmCsfeCommand._curAuthToken = authToken;
		if (params.resend == ZmCsfeCommand.REAUTH) {
			// replace old auth token with current one
			var nodes = soapDoc.getDoc().getElementsByTagName("authToken");
			if (nodes && nodes.length == 1) {
				DBG.println(AjxDebug.DBG1, "Re-auth: replacing auth token");
				nodes[0].firstChild.data = authToken;
			} else {
				// can't find auth token, just add it to context element
				nodes = soapDoc.getDoc().getElementsByTagName("context");
				if (nodes && nodes.length == 1) {
					DBG.println(AjxDebug.DBG1, "Re-auth: re-adding auth token");
					soapDoc.set("authToken", authToken, nodes[0]);
				} else {
					DBG.println(AjxDebug.DBG1, "Re-auth: could not find context!");
				}
			}
		} else if (!params.resend){
			soapDoc.set("authToken", authToken, context);
		}
	}

	if (window.DBG) {
		var ts = DBG._getTimeStamp();
		DBG.println(["<b>", params.methodNameStr, params.asyncMode ? " (asynchronous)" : "" , " - ", ts, "</b>"].join(""), params.methodNameStr);
		DBG.printXML(AjxDebug.DBG1, soapDoc.getXml());
	}

	return soapDoc.getXml();
};

/**
 * Runs the callback that was passed to invoke() for an async command.
 *
 * @param {AjxCallback}	callback	the callback to run with response data
 * @param {Hash}	params	a hash of parameters (see method invoke())
 * 
 * @private
 */
ZmCsfeCommand.prototype._runCallback =
function(params, result) {
	if (!result) { return; }
	if (this.cancelled && params.skipCallbackIfCancelled) {	return; }

	var response;
	if (result instanceof ZmCsfeResult) {
		response = result; // we already got an exception and packaged it
	} else {
		response = this._getResponseData(result, params);
	}
	this._en = new Date();

	if (params.callback) {
		params.callback.run(response);
	} else {
		DBG.println(AjxDebug.DBG1, "ZmCsfeCommand.prototype._runCallback: Missing callback!");
	}
};

/**
 * Takes the response to an RPC request and returns a JS object with the response data.
 *
 * @param {Object}	response	the RPC response with properties "text" and "xml"
 * @param {Hash}	params	a hash of parameters (see method invoke())
 */
ZmCsfeCommand.prototype._getResponseData =
function(response, params) {
	this._en = new Date();
	DBG.println(AjxDebug.DBG1, "ROUND TRIP TIME: " + (this._en.getTime() - this._st.getTime()));

	var result = new ZmCsfeResult();
	var xmlResponse = false;
	var restResponse = Boolean(params.restUri);
	var respDoc = null;

	// check for un-parseable HTML error response from server
	if (!response.success && !response.xml && (/<html/i.test(response.text))) {
		// bad XML or JS response that had no fault
		var ex = new ZmCsfeException(null, ZmCsfeException.CSFE_SVC_ERROR, params.methodNameStr, "HTTP response status " + response.status);
		if (params.asyncMode) {
			result.set(ex, true);
			return result;
		} else {
			throw ex;
		}
	}

	if (typeof(response.text) == "string" && response.text.indexOf("{") == 0) {
		respDoc = response.text;
	} else if (!restResponse) {
		// an XML response if we requested one, or a fault
		try {
			xmlResponse = true;
			if (!(response.text || (response.xml && (typeof response.xml) == "string"))) {
				// If we can't reach the server, req returns immediately with an empty response rather than waiting and timing out
				throw new ZmCsfeException(null, ZmCsfeException.EMPTY_RESPONSE, params.methodNameStr);
			}
			// responseXML is empty under IE
			respDoc = (AjxEnv.isIE || response.xml == null) ? AjxSoapDoc.createFromXml(response.text) :
															  AjxSoapDoc.createFromDom(response.xml);
		} catch (ex) {
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (params.asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}
		if (!respDoc) {
			var ex = new ZmCsfeException(null, ZmCsfeException.SOAP_ERROR, params.methodNameStr, "Bad XML response doc");
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (params.asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}
	}

	var obj = restResponse ? response.text : {};

	if (xmlResponse) {
		DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
		obj = respDoc._xmlDoc.toJSObject(true, false, true);
	} else if (!restResponse) {
		try {
			eval("obj=" + respDoc);
		} catch (ex) {
			if (ex.name == "SyntaxError") {
				ex = new ZmCsfeException(null, ZmCsfeException.BAD_JSON_RESPONSE, params.methodNameStr);
			}
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (params.asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}

	}

	if (window.DBG) {
		var ts = DBG._getTimeStamp();
		var method = ZmCsfeCommand.getMethodName(obj.Body);
		DBG.println(["<b>" + method, params.asyncMode ? " (asynchronous)" : "" , " - ", ts, "</b>"].join(""), method);
	}
	DBG.dumpObj(AjxDebug.DBG1, obj, -1);

	var fault = obj && obj.Body && obj.Body.Fault;
	if (fault) {
		// JS response with fault
		var ex = ZmCsfeCommand.faultToEx(fault, params);
		if (params.asyncMode) {
			result.set(ex, true, obj.Header);
			return result;
		} else {
			throw ex;
		}
	} else if (!response.success) {
		// bad XML or JS response that had no fault
		var ex = new ZmCsfeException(null, ZmCsfeException.CSFE_SVC_ERROR, params.methodNameStr, "HTTP response status " + response.status);
		if (params.asyncMode) {
			result.set(ex, true);
			return result;
		} else {
			throw ex;
		}
	} else {
		// good response
		if (params.asyncMode) {
			result.set(obj);
		}
	}

	// check for new session ID
	var session = obj.Header && obj.Header.context && obj.Header.context.session;
	var sid = session && ZmCsfeCommand.extractSessionId(session);
	if (sid && !ZmCsfeCommand._staleSession[sid]) {
		if (sid != ZmCsfeCommand._sessionId) {
			if (ZmCsfeCommand._sessionId) {
				ZmCsfeCommand._staleSession[ZmCsfeCommand._sessionId] = true;
			}
			ZmCsfeCommand.setSessionId(sid);
		}
	}

	return params.asyncMode ? result : obj;
};

/**
 * @private
 */
ZmCsfeCommand.prototype._handleException =
function(ex, params, callback) {
	if (!(ex && (ex instanceof ZmCsfeException || ex instanceof AjxSoapException || ex instanceof AjxException))) {
		var newEx = new ZmCsfeException();
		newEx.method = params.methodNameStr || params.restUri;
		newEx.detail = ex ? ex.toString() : "undefined exception";
		newEx.code = ZmCsfeException.UNKNOWN_ERROR;
		newEx.msg = "Unknown Error";
		ex = newEx;
	}
	if (params.asyncMode) {
		callback.run(new ZmCsfeResult(ex, true));
	} else {
		throw ex;
	}
};
