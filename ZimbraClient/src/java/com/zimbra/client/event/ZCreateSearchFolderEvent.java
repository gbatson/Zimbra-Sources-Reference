/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010, 2011, 2013, 2014 Zimbra, Inc.
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

package com.zimbra.client.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.client.ZItem;
import com.zimbra.client.ZSearchFolder;

public class ZCreateSearchFolderEvent implements ZCreateItemEvent {

    protected ZSearchFolder mSearchFolder;

    public ZCreateSearchFolderEvent(ZSearchFolder searchFolder) throws ServiceException {
        mSearchFolder = searchFolder;
    }

    /**
     * @return id of created search folder.
     * @throws com.zimbra.common.service.ServiceException
     */
    public String getId() throws ServiceException {
        return mSearchFolder.getId();
    }

    public ZItem getItem() throws ServiceException {
        return mSearchFolder;
    }

    public ZSearchFolder getSearchFolder() {
        return mSearchFolder;
    }
    
    public String toString() {
    	return mSearchFolder.toString();
    }
}
