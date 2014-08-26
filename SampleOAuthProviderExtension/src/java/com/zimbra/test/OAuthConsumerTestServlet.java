/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2014 Zimbra, Inc.
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
package com.zimbra.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
/**
 *
 * @author gsolovyev
 * servlet to test oAuth
 */
public class OAuthConsumerTestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    String CONSUMER_KEY="consumer2";
    String CONSUMER_SECRET="secret2";
    String REQUEST_TOKEN_ENDPOINT_URL="http://localhost:7070/service/extension/sampleoauth/req_token";
    String ACCESS_TOKEN_ENDPOINT_URL="http://localhost:7070/service/extension/sampleoauth/access_token";
    String AUTHORIZE_WEBSITE_URL="http://localhost:7070/service/extension/sampleoauth/authorization";
    String CALLBACK_URL="ttp://localhost:7070/oauthConsumer/oauthtest";
    OAuthConsumer consumer = null;
    OAuthProvider provider = null;
    String restURL = "";
    @Override
    public void init() throws ServletException {
        CONSUMER_KEY = getInitParameter("CONSUMER_KEY");
        CONSUMER_SECRET = getInitParameter("CONSUMER_SECRET");
        REQUEST_TOKEN_ENDPOINT_URL = getInitParameter("REQUEST_TOKEN_ENDPOINT_URL");
        ACCESS_TOKEN_ENDPOINT_URL = getInitParameter("ACCESS_TOKEN_ENDPOINT_URL");
        AUTHORIZE_WEBSITE_URL = getInitParameter("AUTHORIZE_WEBSITE_URL");
        CALLBACK_URL = getInitParameter("CALLBACK_URL");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
      restURL = req.getParameter("rest_url");
      try {
            consumer = new DefaultOAuthConsumer(CONSUMER_KEY,CONSUMER_SECRET);
            provider = new DefaultOAuthProvider(
                    REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                    AUTHORIZE_WEBSITE_URL);

            String url = provider.retrieveRequestToken(consumer, CALLBACK_URL);
            resp.sendRedirect(url);
      } catch (OAuthMessageSignerException | OAuthNotAuthorizedException
                | OAuthExpectationFailedException | OAuthCommunicationException e) {
            e.printStackTrace();
      }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");

        String request_token = req.getParameter("oauth_token");
        String verifier = req.getParameter("oauth_verifier");
        String zimbra_cn = req.getParameter("zimbra_cn");
        String zimbra_displayname = req.getParameter("zimbra_displayname");
        String zimbra_email = req.getParameter("zimbra_email");
        String zimbra_sn = req.getParameter("zimbra_sn");
        String zimbra_givenname = req.getParameter("zimbra_givenname");
        OutputStream out = resp.getOutputStream();
        try {
            //consumer.setSigningStrategy(new QueryStringSigningStrategy());
            provider.retrieveAccessToken(consumer, verifier);
            HttpURLConnection request = (HttpURLConnection) new URL(restURL).openConnection();
            request.addRequestProperty("X-Zimbra-Orig-Url", restURL);
            // sign the request
            consumer.sign(request);
            // send the request
            request.connect();
            BufferedInputStream in = new BufferedInputStream(request.getInputStream());
            byte[  ] buf = new byte[1024];  // 1K buffer
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

        } catch (OAuthMessageSignerException | OAuthNotAuthorizedException
                | OAuthExpectationFailedException | OAuthCommunicationException | IOException e ) {
            e.printStackTrace();
        }
        String additionalContent = "<html>\n" +
                "<head><title>Hello World</title></head>\n"+
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">zimbra_cn=" + zimbra_cn + "</h1>\n" +
                "<h1 align=\"center\">zimbra_displayname=" + zimbra_displayname + "</h1>\n" +
                "<h1 align=\"center\">zimbra_email=" + zimbra_email + "</h1>\n" +
                "<h1 align=\"center\">zimbra_sn=" + zimbra_sn + "</h1>\n" +
                "<h1 align=\"center\">zimbra_givenname=" + zimbra_givenname + "</h1>\n</body></html>";
                out.write(additionalContent.getBytes());
                out.close();
    }

}
