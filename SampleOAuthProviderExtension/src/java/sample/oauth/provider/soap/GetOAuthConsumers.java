/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013, 2014 Zimbra, Inc.
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
package sample.oauth.provider.soap;

import java.util.Map;
import java.util.Set;

import net.oauth.OAuthConsumer;

import org.dom4j.QName;

import sample.oauth.provider.core.SampleZmOAuthProvider;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

/**
 */
public class GetOAuthConsumers extends DocumentHandler {

    public static final QName GET_OAUTH_CONSUMERS_REQUEST =
            QName.get("GetOAuthConsumersRequest", MailConstants.NAMESPACE);
    public static final QName GET_OAUTH_CONSUMERS_RESPONSE =
            QName.get("GetOAuthConsumersResponse", MailConstants.NAMESPACE);

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mailbox = getRequestedMailbox(zsc);
        Set<String> authzedConsumerKeys = null;
        Metadata oAuthConfig = mailbox.getConfig(null, "zwc:sampleoauth");
        if (oAuthConfig != null) {
            Metadata metadata = oAuthConfig.getMap("authorized_consumers", true);
            if (metadata != null) {
                authzedConsumerKeys = metadata.asMap().keySet();
            }
        }
        Element response = zsc.createElement(GET_OAUTH_CONSUMERS_RESPONSE);
        if (authzedConsumerKeys != null) {
            for (String key : authzedConsumerKeys) {
                OAuthConsumer consumer;
                try {
                    consumer = SampleZmOAuthProvider.getConsumer(key);
                } catch (Exception e) {
                    throw ServiceException.FAILURE("Unable to find registered OAuth Consumer with key " + key, null);
                }
                response.addElement("oauthConsumer").
                         addAttribute("key", key).
                         addAttribute("desc", (String) consumer.getProperty("description"));
            }
        }
        return response;
    }
}
