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
package com.zimbra.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;

public class HttpUtil {

    public enum Browser { IE, FIREFOX, MOZILLA, OPERA, SAFARI, APPLE_ICAL, UNKNOWN };

    public static Browser guessBrowser(HttpServletRequest req) {
        String ua = req.getHeader("User-Agent");
        return guessBrowser(ua);
    }

    /**
     * 
     * @param ua User-Agent string
     * @return
     */
    public static Browser guessBrowser(String ua) {
        if (ua == null || ua.trim().equals(""))
            return Browser.UNKNOWN;
        else if (ua.indexOf("MSIE") != -1)
            return Browser.IE;
        else if (ua.indexOf("Firefox") != -1)
            return Browser.FIREFOX;
        else if (ua.indexOf("AppleWebKit") != -1)
            return Browser.SAFARI;
        else if (ua.indexOf("Opera") != -1)
            return Browser.OPERA;
        else if (ua.indexOf("iCal") != -1)
            return Browser.APPLE_ICAL;
        else
            return Browser.UNKNOWN;
    }

    public static String encodeFilename(HttpServletRequest req, String filename) {
        if (StringUtil.isAsciiString(filename) && filename.indexOf('"') == -1)
            return '"' + filename.replace('\t', ' ') + '"';
        return encodeFilename(guessBrowser(req), filename);
    }

    public static String encodeFilename(Browser browser, String filename) {
        // Windows does not allow tabs in filenames - replacing with ' ' is safe
        filename = filename.replace('\t', ' ');
        if (StringUtil.isAsciiString(filename) && filename.indexOf('"') == -1)
            return '"' + filename + '"';
        try {
            if (browser == Browser.IE)
                return URLEncoder.encode(filename, "utf-8");
            else if (browser == Browser.FIREFOX)
                return '"' + MimeUtility.encodeText(filename, "utf-8", "B") + '"';
            else
                return '"' + MimeUtility.encodeText(filename, "utf-8", "B") + '"';
        } catch (UnsupportedEncodingException uee) {
            return filename;
        }
    }

    /** Strips any userinfo (username/password) data from the passed-in URL
     *  and returns the result. */
    public static String sanitizeURL(String url) {
        if (url != null && url.indexOf('@') != -1) {
            try {
                HttpURL httpurl = (url.indexOf("https:") == 0) ? new HttpsURL(url) : new HttpURL(url);
                if (httpurl.getPassword() != null) {
                    httpurl.setPassword("");
                    return httpurl.toString();
                }
            } catch (org.apache.commons.httpclient.URIException urie) { }
        }
        return url;
    }

    /** Returns the full URL (including query string) associated with the
     *  given <code>HttpServletRequest</code>. */
    public static String getFullRequestURL(HttpServletRequest req) {
        if (req == null)
            return null;

        String uri = encodePath(req.getRequestURI()), qs = req.getQueryString();
        if (qs != null)
            uri += '?' + qs;
        return uri;
    }

    public static Map<String, String> getURIParams(HttpServletRequest req) {
        return getURIParams(req.getQueryString());
    }

    public static Map<String, String> getURIParams(String queryString) {
        Map<String, String> params = new HashMap<String, String>();
        if (queryString == null || queryString.trim().equals(""))
            return params;

        for (String pair : queryString.split("&")) {
            String[] keyVal = pair.split("=");
            try {
                String value = keyVal.length > 1 ? URLDecoder.decode(keyVal[1], "utf-8") : "";
                params.put(URLDecoder.decode(keyVal[0], "utf-8"), value);
            } catch (UnsupportedEncodingException uee) { }
        }
        return params;
    }

    /**
     * URL-encodes the given URL path.
     * 
     * @return the encoded path, or the original path if it
     * is malformed
     */
    public static String encodePath(String path) {
        String encoded = path;
        try {
            URI uri = new URI(null, null, path, null);
            encoded = uri.toString();
        } catch (URISyntaxException e) {
            // ignore and just return the orig path
        }
        return encoded;
    }
    
    /**
     * bug 32207
     * 
     * The apache reverse proxy is re-writing the Host header to be the MBS IP.  It sets 
     * the original request hostname in the X-Forwarded-Host header.  To work around it, 
     * we first check for X-Forwarded-Host and then fallback to Host.
     *
     * @param req
     * @return the original request hostname 
     */
    public static String getVirtualHost(HttpServletRequest req) {
        String virtualHost = req.getHeader("X-Forwarded-Host");
        if (virtualHost != null)
            return virtualHost;
        else 
            return req.getServerName();
    }

    public static void main(String[] args) {
        System.out.println(getURIParams((String) null));
        System.out.println(getURIParams("foo=bar"));
        System.out.println(getURIParams("foo=bar&baz&ben=wak"));
        System.out.println(getURIParams("foo=bar&%45t%4E=%33%20%6eford"));
    }
}
