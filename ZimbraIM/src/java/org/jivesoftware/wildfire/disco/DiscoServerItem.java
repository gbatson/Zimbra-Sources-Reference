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
package org.jivesoftware.wildfire.disco;

/**
 * Represent a DiscoItem provided by the server. Therefore, the DiscoServerItems are responsible
 * for providing the DiscoInfoProvider and DiscoItemsProvider that will provide the information and
 * items related to this item.<p>
 * <p/>
 * When the server starts up, IQDiscoItemsHandler will request to all the services that implement
 * the ServerItemsProvider interface for their DiscoServerItems. Each DiscoServerItem will provide
 * its DiscoInfoProvider which will automatically be included in IQDiscoInfoHandler as the provider
 * for this item's JID. Moreover, each DiscoServerItem will also provide its DiscoItemsProvider
 * which will automatically be included in IQDiscoItemsHandler. Special attention must be paid to
 * the JID since all the items with the same host will share the same DiscoInfoProvider or
 * DiscoItemsProvider.
 *
 * @author Gaston Dombiak
 */
public interface DiscoServerItem extends DiscoItem {

    /**
     * Returns the DiscoInfoProvider responsible for providing the information related to this item.
     * The DiscoInfoProvider will be automatically included in IQDiscoInfoHandler as the provider
     * for this item's JID.
     *
     * @return the DiscoInfoProvider responsible for providing the information related to this item.
     */
    public abstract DiscoInfoProvider getDiscoInfoProvider();

    /**
     * Returns the DiscoItemsProvider responsible for providing the items related to this item.
     * The DiscoItemsProvider will be automatically included in IQDiscoItemsHandler as the provider
     * for this item's JID.
     *
     * @return the DiscoItemsProvider responsible for providing the items related to this item.
     */
    public abstract DiscoItemsProvider getDiscoItemsProvider();
}
