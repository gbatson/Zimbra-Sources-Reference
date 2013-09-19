/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.versioncheck;

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;

/**
 * @author Greg Solovyev
 */
public class VersionCheckService implements DocumentService {
	public static final String NAMESPACE_STR = "urn:zimbraAdmin";
	
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);
    
    public static final QName VC_REQUEST = QName.get("VersionCheckRequest", NAMESPACE);
    public static final QName VC_RESPONSE = QName.get("VersionCheckResponse", NAMESPACE);
	
	public static String VERSION_CHECK_STATUS = "status";
	public static String VERSION_CHECK_CHECK = "check";
	
	
	public void registerHandlers(DocumentDispatcher dispatcher) {
		dispatcher.registerHandler(VC_REQUEST, new VersionCheck());

	}

}
