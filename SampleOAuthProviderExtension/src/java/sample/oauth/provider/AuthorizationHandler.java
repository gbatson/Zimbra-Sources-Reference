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

// Original is from example in OAuth Java library(http://oauth.googlecode.com/svn/code/java/)
// and modified for integratin with Zimbra

// Original's copyright and license terms
/*
 * Copyright 2007 AOL, LLC.
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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;
import sample.oauth.provider.core.SampleZmOAuthProvider;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.extension.ExtensionHttpHandler;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.servlet.ZimbraServlet;

/**
 * Autherization request handler for zimbra extension.
 *
 * @author Yutaka Obuchi
 */

public class AuthorizationHandler extends ExtensionHttpHandler {


	@Override
    public void init(ZimbraExtension ext) throws ServiceException {
        super.init(ext);
    }

    @Override
    public String getPath() {
        return super.getPath() + "/authorization";
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	ZimbraLog.extensions.debug("Authorization Hndler doGet requested!");

        try{
            OAuthMessage oAuthMessage = OAuthServlet.getMessage(request, null);

            OAuthAccessor accessor = SampleZmOAuthProvider.getAccessor(oAuthMessage);

            if (Boolean.TRUE.equals(accessor.getProperty("authorized"))) {
                // already authorized send the user back
                returnToConsumer(request, response, accessor);
            } else {
                sendToAuthorizePage(request, response, accessor);
            }

        } catch (Exception e){
            SampleZmOAuthProvider.handleException(e, request, response, true);
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException{
    	ZimbraLog.extensions.debug("Authorization Hndler doPost requested!");

        try{
            OAuthMessage requestMessage = OAuthServlet.getMessage(request, null);

            OAuthAccessor accessor = SampleZmOAuthProvider.getAccessor(requestMessage);

            String username = request.getParameter("username");
            String zmtoken = (String)request.getAttribute("ZM_AUTH_TOKEN");

            ZimbraLog.extensions.debug("[AuthorizationHandlerInput]username:"+username+",oauth_token:"+request.getParameter("oauth_token")+",ZM_AUTH_TOKEN:"+zmtoken);

            if(zmtoken == null){
                sendToAuthorizePage(request, response, accessor);
            }else{
            	SampleZmOAuthProvider.markAsAuthorized(accessor, request.getParameter("username"), zmtoken);
            	SampleZmOAuthProvider.generateVerifier(accessor);

            	returnToConsumer(request, response, accessor);
            }
        } catch (Exception e){
            ZimbraLog.extensions.debug("AuthorizationHandler exception", e);
            SampleZmOAuthProvider.handleException(e, request, response, true);
        }
    }

    private void sendToAuthorizePage(HttpServletRequest request,
            HttpServletResponse response, OAuthAccessor accessor)
    throws IOException, ServletException{

        String consumer_description = (String)accessor.consumer.getProperty("description");
        ZimbraLog.extensions.debug("[AuthorizationHandlerOutputToAuthorizePage]reuest token:"+accessor.requestToken+",description:"+consumer_description);
        request.setAttribute("CONS_DESC", consumer_description);
        request.setAttribute("TOKEN", accessor.requestToken);


        HttpServlet servlet = ZimbraServlet.getServlet("ExtensionDispatcherServlet");
        servlet.getServletContext().getContext("/zimbra").getRequestDispatcher //
        	("/public/authorize.jsp").forward(request,response);


    }

    private void returnToConsumer(HttpServletRequest request,
            HttpServletResponse response, OAuthAccessor accessor)
    throws IOException, ServletException{
        // send the user back to site's callBackUrl
    	String callback = (String) accessor.getProperty(OAuth.OAUTH_CALLBACK);


        if( "oob".equals(callback) ) {
            // no call back it must be a client
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println("You have successfully authorized '"
                    + accessor.consumer.getProperty("description")
                    + "'. Your verification code is "
                    + accessor.getProperty(OAuth.OAUTH_VERIFIER).toString()
                    + ". Please close this browser window and click continue"
                    + " in the client.");
            out.close();
        } else {
            String token = accessor.requestToken;
            String verifier = accessor.getProperty(OAuth.OAUTH_VERIFIER).toString();
            if (token != null) {
                callback = OAuth.addParameters(callback,
                								"oauth_token", token,
                								OAuth.OAUTH_VERIFIER,verifier);
            }

            callback = String.format("%s&zimbra_cn=%s&zimbra_givenname=%s&zimbra_sn=%s&zimbra_email=%s&zimbra_displayname=%s",callback,
                    accessor.getProperty("ZM_ACC_CN"),
                    accessor.getProperty("ZM_ACC_GIVENNAME"),
                    accessor.getProperty("ZM_ACC_SN"),
                    accessor.getProperty("ZM_ACC_EMAIL"),
                    accessor.getProperty("ZM_ACC_DISPLAYNAME"));

            ZimbraLog.extensions.debug("[AuthorizationHandlerRedirectURL]"+callback);

            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", callback);
            //not sending back ZM_AUTH_TOKEN to consumer
            response.setHeader("Set-Cookie", "");
        }
    }

    private static final long serialVersionUID = 1L;

}
