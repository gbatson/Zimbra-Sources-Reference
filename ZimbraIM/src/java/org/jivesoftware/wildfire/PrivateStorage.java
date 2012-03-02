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
package org.jivesoftware.wildfire;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.wildfire.event.UserEventListener;
import org.jivesoftware.wildfire.event.UserEventDispatcher;
import org.jivesoftware.wildfire.user.User;
import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;

/**
 * Private storage for user accounts (JEP-0049). It is used by some XMPP systems
 * for saving client settings on the server.
 *
 * @author Iain Shigeoka
 */
public class PrivateStorage extends BasicModule implements UserEventListener {

    private static final String LOAD_PRIVATE =
        "SELECT value FROM jivePrivate WHERE username=? AND namespace=?";
    private static final String INSERT_PRIVATE =
        "INSERT INTO jivePrivate (value,name,username,namespace) VALUES (?,?,?,?)";
    private static final String UPDATE_PRIVATE =
        "UPDATE jivePrivate SET value=?, name=? WHERE username=? AND namespace=?";
    private static final String DELETE_PRIVATES =
        "DELETE FROM jivePrivate WHERE username=?";

    // Currently no delete supported, we can detect an add of an empty element and
    // use that to signal a delete but that optimization doesn't seem necessary.
    // private static final String DELETE_PRIVATE =
    //     "DELETE FROM jivePrivate WHERE userID=? AND name=? AND namespace=?";
    private boolean enabled = IMConfig.XMPP_PRIVATE_STORAGE_ENABLED.getBoolean();

    /**
     * Pool of SAX Readers. SAXReader is not thread safe so we need to have a pool of readers.
     */
    private BlockingQueue<SAXReader> xmlReaders = new LinkedBlockingQueue<SAXReader>();

    /**
     * Constructs a new PrivateStore instance.
     */
    public PrivateStorage() {
        super("Private user data storage");
    }

    /**
     * Returns true if private storage is enabled.
     *
     * @return true if private storage is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Stores private data. If the name and namespace of the element matches another
     * stored private data XML document, then replace it with the new one.
     *
     * @param data the data to store (XML element)
     * @param username the username of the account where private data is being stored
     */
    public void add(String username, Element data) {
        if (enabled) {
            java.sql.Connection con = null;
            PreparedStatement pstmt = null;
            try {
                StringWriter writer = new StringWriter();
                data.write(writer);
                con = DbConnectionManager.getConnection();
                pstmt = con.prepareStatement(LOAD_PRIVATE);
                pstmt.setString(1, username);
                pstmt.setString(2, data.getNamespaceURI());
                ResultSet rs = pstmt.executeQuery();
                boolean update = false;
                if (rs.next()) {
                    update = true;
                }
                rs.close();
                pstmt.close();

                if (update) {
                    pstmt = con.prepareStatement(UPDATE_PRIVATE);
                }
                else {
                    pstmt = con.prepareStatement(INSERT_PRIVATE);
                }
                pstmt.setString(1, writer.toString());
                pstmt.setString(2, data.getName());
                pstmt.setString(3, username);
                pstmt.setString(4, data.getNamespaceURI());
                pstmt.executeUpdate();
            }
            catch (Exception e) {
                Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
            finally {
                try { if (pstmt != null) { pstmt.close(); } }
                catch (Exception e) { Log.error(e); }
                try { if (con != null) { con.close(); } }
                catch (Exception e) { Log.error(e); }
            }
        }
    }

    /**
     * Returns the data stored under a key corresponding to the name and namespace
     * of the given element. The Element must be in the form:<p>
     *
     * <code>&lt;name xmlns='namespace'/&gt;</code><p>
     *
     * If no data is currently stored under the given key, an empty element will be
     * returned.
     *
     * @param data an XML document who's element name and namespace is used to
     *      match previously stored private data.
     * @param username the username of the account where private data is being stored.
     * @return the data stored under the given key or the data element.
     */
    public Element get(String username, Element data) {
        if (enabled) {
            Connection con = null;
            PreparedStatement pstmt = null;
            SAXReader xmlReader = null;
            try {
                // Get a sax reader from the pool
                xmlReader = xmlReaders.take();
                con = DbConnectionManager.getConnection();
                pstmt = con.prepareStatement(LOAD_PRIVATE);
                pstmt.setString(1, username);
                pstmt.setString(2, data.getNamespaceURI());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    data.clearContent();
                    String result = rs.getString(1).trim();
                    Document doc = xmlReader.read(new StringReader(result));
                    data = doc.getRootElement();
                }
                rs.close();
            }
            catch (Exception e) {
                Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
            finally {
                // Return the sax reader to the pool
                if (xmlReader != null) {
                    xmlReaders.add(xmlReader);
                }
                try { if (pstmt != null) { pstmt.close(); } }
                catch (Exception e) { Log.error(e); }
                try { if (con != null) { con.close(); } }
                catch (Exception e) { Log.error(e); }
            }
        }
        return data;
    }

    public void userCreated(User user, Map params) {
        //Do nothing
    }

    public void userDeleting(User user, Map params) {
        // Delete all private properties of the user
        java.sql.Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(DELETE_PRIVATES);
            pstmt.setString(1, user.getUsername());
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
        finally {
            try { if (pstmt != null) { pstmt.close(); } }
            catch (Exception e) { Log.error(e); }
            try { if (con != null) { con.close(); } }
            catch (Exception e) { Log.error(e); }
        }
    }

    public void userModified(User user, Map params) {
        //Do nothing
    }

    public void start() throws IllegalStateException {
        super.start();
        // Initialize the pool of sax readers
        for (int i=0; i<10; i++) {
            xmlReaders.add(new SAXReader());
        }
        // Add this module as a user event listener so we can delete
        // all user properties when a user is deleted
        UserEventDispatcher.addListener(this);
    }

    public void stop() {
        super.stop();
        // Clean up the pool of sax readers
        xmlReaders.clear();
        // Remove this module as a user event listener
        UserEventDispatcher.removeListener(this);
    }
}