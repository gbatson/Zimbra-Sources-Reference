/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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

package com.zimbra.cs.upgrade;

import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.mailbox.*;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.cs.service.mail.ItemActionHelper;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Utility class to upgrade contacts.
 *
 * @author Andy Clark
 */
public class MailboxUpgrade {

	//
	// Constants
	//

	private static final Map<Long,Byte> UPGRADE_TO_1_7_COLORS = new HashMap<Long,Byte>();
    private static final Map<Long,Byte> UPGRADE_TO_1_8_COLORS = UPGRADE_TO_1_7_COLORS;

	static {
		Map<Long,Byte> map = UPGRADE_TO_1_7_COLORS;
		// common colors
		map.put(0x1000000L,(byte)0); // none
		// original RGB colors
		map.put(0x10000ffL,(byte)1); // blue
		map.put(0x1008284L,(byte)2); // cyan
        map.put(0x1848284L,(byte)8); // gray
		map.put(0x1008200L,(byte)3); // green
        map.put(0x1ff8000L,(byte)9); // orange
		map.put(0x1840084L,(byte)4); // purple
        map.put(0x1ff0084L,(byte)7); // pink
		map.put(0x1ff0000L,(byte)5); // red
		map.put(0x1848200L,(byte)6); // yellow
		// newer RGB colors
		map.put(0x19EB6F5L,(byte)1); // blue
		map.put(0x1A4E6E6L,(byte)2); // cyan
        map.put(0x1D3D3D3L,(byte)8); // gray
		map.put(0x197C8B1L,(byte)3); // green
        map.put(0x1FDBC55L,(byte)9); // orange
        map.put(0x1FE9BD3L,(byte)7); // pink
		map.put(0x1BA86E5L,(byte)4); // purple
		map.put(0x1FC9696L,(byte)5); // red
		map.put(0x1FFF6B3L,(byte)6); // yellow
        // newest RGB colors
        map.put(0x10252d4L,(byte)1); // blue
        map.put(0x1008284L,(byte)2); // cyan
        map.put(0x1848284L,(byte)8); // gray
        map.put(0x12ca10bL,(byte)3); // green
        map.put(0x1f57802L,(byte)9); // orange
        map.put(0x1b027aeL,(byte)7); // pink
        map.put(0x1492ba1L,(byte)4); // purple
        map.put(0x1e51717L,(byte)5); // red
        map.put(0x1848200L,(byte)6); // yellow
	}

	//
	// Constructors
	//

	/** This class can not be instantiated. */
	private MailboxUpgrade() {}

	//
	// Public static functions
	//

	public static void upgradeTo1_7(Mailbox mbox) throws ServiceException {
		// bug 41893: revert folder colors back to mapped value
		OperationContext octxt = new OperationContext(mbox);
		for (Folder folder : mbox.getFolderList(octxt, SortBy.NONE)) {
			MailItem.Color color = folder.getRgbColor();
			if (!color.hasMapping()) {
				Byte value = UPGRADE_TO_1_7_COLORS.get(color.getValue());
				if (value != null) {
					MailItem.Color newcolor = new MailItem.Color((byte)value);
					mbox.setColor(octxt, new int[] { folder.getId() }, folder.getType(), newcolor);
				}
			}
		}
	}

    public static void upgradeTo1_8(Mailbox mbox) throws ServiceException {
        // bug 41850: revert tag colors back to mapped value
        OperationContext octxt = new OperationContext(mbox);
        for (Tag tag : mbox.getTagList(octxt)) {
            MailItem.Color color = tag.getRgbColor();
            if (!color.hasMapping()) {
                Byte value = UPGRADE_TO_1_8_COLORS.get(color.getValue());
                if (value != null) {
                    MailItem.Color newcolor = new MailItem.Color((byte)value);
                    mbox.setColor(octxt, new int[] { tag.getId() }, tag.getType(), newcolor);
                }
            }
        }
    }

} // class MailboxUpgrade