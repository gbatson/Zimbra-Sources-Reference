/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2010, 2011, 2014 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at: http://www.zimbra.com/license
 * The License is based on the Mozilla Public License Version 1.1 but Sections 14 and 15 
 * have been added to cover use of software over a computer network and provide for limited attribution 
 * for the Original Developer. In addition, Exhibit A has been modified to be consistent with Exhibit B. 
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. 
 * See the License for the specific language governing rights and limitations under the License. 
 * The Original Code is Zimbra Open Source Web Client. 
 * The Initial Developer of the Original Code is Zimbra, Inc. 
 * All portions of the code are Copyright (C) 2007, 2010, 2011, 2014 Zimbra, Inc. All Rights Reserved. 
 * ***** END LICENSE BLOCK *****
 */
if(window.console && window.console.log) console.log("Loaded zimbra_cert.js");
if(ZaSettings && ZaSettings.EnabledZimlet["com_zimbra_cert_manager"]){
if (ZaOperation) ZaOperation.INSTALLCERT = ++ZA_OP_INDEX;
ZaItem.CERT = "cert" ;

if(ZaOverviewPanelController.treeModifiers)
	ZaOverviewPanelController.treeModifiers.push(ZaCert.certOvTreeModifier);

ZaZimbraAdmin._CERTS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

ZaApp.prototype.getCertViewController =
function(viewId) {
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaCertViewController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getCertsServerListController =
function() {
	if (this._controllers[ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW] = new ZaCertsServerListController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW];
}
}