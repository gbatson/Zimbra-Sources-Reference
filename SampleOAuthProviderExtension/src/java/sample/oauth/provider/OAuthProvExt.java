/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
/*
 * Copyright 2009 Yutaka Obuchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.oauth.provider;

import sample.oauth.provider.soap.OAuthProviderService;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.soap.SoapServlet;

public class OAuthProvExt implements ZimbraExtension {

	static {
    }

    @Override
    public void init() throws ServiceException {
    	ExtensionDispatcherServlet.register(this, new RequestTokenHandler());
    	ExtensionDispatcherServlet.register(this, new AuthorizationHandler());
    	ExtensionDispatcherServlet.register(this, new AccessTokenHandler());

    	AuthProvider.register(new ZimbraAuthProviderForOAuth());
    	AuthProvider.refresh();

        SoapServlet.addService("SoapServlet", new OAuthProviderService());
    }

    @Override
    public void destroy() {
    	ExtensionDispatcherServlet.unregister(this);
    }

    @Override
    public String getName() {
    	return "sampleoauth";
    }

}
