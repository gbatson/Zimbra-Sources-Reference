/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire.vcard;

import org.dom4j.Element;
import org.jivesoftware.util.AlreadyExistsException;
import org.jivesoftware.util.NotFoundException;

/**
 * Provider interface for users vcards.
 *
 * @author Gaston Dombiak
 */
public interface VCardProvider {

    /**
     * Loads the specified user vcard by username. Returns <tt>null</tt> if no
     * vCard was found for the specified username.
     *
     * @param username the username
     * @return the vCard as an DOM element or <tt>null</tt> if none was found.
     */
    Element loadVCard(String username);

    /**
     * Creates and saves the new user vcard. This method should throw an
     * UnsupportedOperationException if this operation is not supported by
     * the backend vcard store.
     *
     * @param username the username.
     * @param vCardElement the vCard to save.
     * @throws AlreadyExistsException if the user already has a vCard.
     * @throws UnsupportedOperationException if the provider does not support the
     *      operation.
     */
    void createVCard(String username, Element vCardElement) throws AlreadyExistsException;

    /**
     * Updates the user vcard in the backend store. This method should throw an
     * UnsupportedOperationException if this operation is not supported by
     * the backend vcard store.
     *
     * @param username the username.
     * @param vCardElement the vCard to save.
     * @throws NotFoundException if the vCard to update does not exist.
     * @throws UnsupportedOperationException if the provider does not support the
     *      operation.
     */
    void updateVCard(String username, Element vCardElement) throws NotFoundException;

    /**
     * Delets a user vcard. This method should throw an UnsupportedOperationException
     * if this operation is not supported by the backend vcard store.
     *
     * @param username the username to delete.
     * @throws UnsupportedOperationException if the provider does not support the
     *      operation.
     */
    void deleteVCard(String username);

    /**
     * Returns true if this VCardProvider is read-only. When read-only,
     * vcards can not be created, deleted, or modified.
     *
     * @return true if the vcard provider is read-only.
     */
    boolean isReadOnly();
}
