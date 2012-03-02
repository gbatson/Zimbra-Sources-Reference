/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire.sasl;

import org.jivesoftware.util.ClassUtils;
import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * Manages the AuthorizationProvider objects.
 * <p/>
 * Overall description of the authentication and authorization process:
 * <p/>
 * After a client connects, and idicates a desire to use SASL, the
 * SASLAuthentication object decides which SASL mechanisms to advertise,
 * and then performs the authentication. If authentication is successful,
 * the XMPPCallbackHandler is asked to handle() an AuthorizeCallback.  The
 * XMPPCallbackHandler asks the AuthorizationManager to authorize the
 * principal to the requested username.  The AuthorizationManager manages
 * a list of AuthorizationProvider classes, and tries them one at a time
 * and returns true with the first AuthorizationProvider that authorizes
 * the principal to the username.  If no classes authorize the principal,
 * false is returned, which traces all the way back to give the client an
 * unauthorized message. Its important to note that the message the client
 * recieves will give no indication if the principal authentiated successfully,
 * you will need to check the server logs for that information.
 *
 * @author Jay Kline
 */
public class AuthorizationManager {

    private static ArrayList<AuthorizationProvider> providers =
            new ArrayList<AuthorizationProvider>();
    private static AuthorizationManager instance = new AuthorizationManager();

    static {
        String classList = IMConfig.XMPP_AUTHPROVIDER_CLASSLIST.getString();
        if (classList != null) {
            StringTokenizer st = new StringTokenizer(classList, " ,\t\n\r\f");
            while (st.hasMoreTokens()) {
                String s_provider = st.nextToken();
                try {
                    Class c_provider = ClassUtils.forName(s_provider);
                    AuthorizationProvider provider =
                            (AuthorizationProvider) (c_provider.newInstance());
                    Log.debug("AuthorizationManager: Loaded " + s_provider);
                    providers.add(provider);
                } catch (Exception e) {
                    Log.error("Error loading AuthorizationProvider: " + s_provider + "\n" + e);
                }
            }
        }
        if (providers.isEmpty()) {
            Log.debug("No AuthorizationProvider's found. Loading DefaultAuthorizationPolicy");
            providers.add(new DefaultAuthorizationPolicy());
        }
    }

    private AuthorizationManager() {

    }

    /**
     * Returns the currently-installed AuthorizationProvider. Warning: You
     * should not be calling the AuthorizationProvider directly to perform
     * authorizations, it will not take into account the policy selected in
     * the <tt>wildfire.xml</tt>.  Use @see{authorize} in this class, instead.
     *
     * @return the current AuthorizationProvider.
     */
    public static Collection<AuthorizationProvider> getAuthorizationProviders() {
        return providers;
    }

    /**
     * Returns a singleton AuthorizationManager instance.
     *
     * @return a AuthorizationManager instance.
     */
    public static AuthorizationManager getInstance() {
        return instance;
    }

    /**
     * Authorize the authenticated used to the requested username.  This uses the
     * selected the selected AuthenticationProviders.
     *
     * @return true if the user is authorized.
     */

    public static boolean authorize(String authorId, String authenId) {
        for (AuthorizationProvider ap : providers) {
            if (ap.authorize(authorId, authenId)) {
                return true;
            }
        }
        return false;
    }
}
