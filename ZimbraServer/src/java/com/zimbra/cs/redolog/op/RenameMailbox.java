/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 VMware, Inc.
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

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class RenameMailbox extends RedoableOp {

    private String mNewName;
    private String mOldName;

    public RenameMailbox() {}

    public RenameMailbox(int mailboxId, String oldName, String newName) {
        setMailboxId(mailboxId);
        mNewName = newName;
    }

    @Override public int getOpCode() {
        return OP_RENAME_MAILBOX;
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeUTF(mNewName);
        if (getVersion().atLeast(1,25))
            out.writeUTF(mOldName);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mNewName = in.readUTF();
        if (getVersion().atLeast(1,25)) 
            mOldName = in.readUTF();
    }

    @Override protected String getPrintableData() {
        return "newName=" + mNewName+" oldName="+mOldName;
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        if (mNewName != null)
            mbox.renameMailbox(mOldName, mNewName);
    }
}
