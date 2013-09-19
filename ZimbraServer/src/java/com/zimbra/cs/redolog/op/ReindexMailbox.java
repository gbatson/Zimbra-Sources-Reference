/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on 2005. 4. 4.
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class ReindexMailbox extends RedoableOp {
    
    private Set<Byte> mTypes = null;
    private Set<Integer> mItemIds = null;
    private int mCompletionId = 0;
    private boolean mSkipDelete = false;

    public ReindexMailbox() { }

    public ReindexMailbox(int mailboxId, Set<Byte> typesOrNull, Set<Integer> itemIdsOrNull, int completionId, boolean skipDelete) {
        setMailboxId(mailboxId);
        assert(typesOrNull == null || itemIdsOrNull == null);
        mTypes = typesOrNull;
        mItemIds = itemIdsOrNull;
        mCompletionId = completionId;
        mSkipDelete = skipDelete;
    }

    @Override public int getOpCode() {
        return OP_REINDEX_MAILBOX;
    }

    @Override public boolean deferCrashRecovery() {
        return true;
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.reIndex(new OperationContext(this), mTypes, mItemIds, mSkipDelete);
    }

    @Override protected String getPrintableData() {
        StringBuilder sb = new StringBuilder("Completion="+mCompletionId);
        sb.append(" SkipDelete="+(mSkipDelete?"TRUE":"FALSE"));
        if (mItemIds != null) {
            sb.append(" ITEMIDS[");
            boolean atStart = true;
            for (Integer i : mItemIds) {
                if (!atStart) 
                    sb.append(',');
                else
                    atStart = false;
                sb.append(i);
            }
            sb.append(']');

            return sb.toString();
        } else if (mTypes != null) {
            sb.append(" TYPES[");
            boolean atStart = true;
            for (Byte b : mTypes) {
                if (!atStart) 
                    sb.append(',');
                else
                    atStart = false;
                sb.append(MailItem.getNameForType(b));
            }
            sb.append(']');
                        
            return sb.toString();
        } else {
            return null;
        }
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        if (getVersion().atLeast(1,9)) {
            // completion ID
            out.writeInt(mCompletionId);
            
            // types
            if (mTypes != null) {
                out.writeBoolean(true);
                int count = mTypes.size();
                out.writeInt(count);
                for (Byte b : mTypes) {
                    out.writeByte(b);
                    count--;
                }
                assert(count == 0);
            } else {
                out.writeBoolean(false);
            }

            // itemIds
            if (getVersion().atLeast(1,10)) {
                if (mItemIds != null) {
                    out.writeBoolean(true);
                    int count = mItemIds.size();
                    out.writeInt(count);
                    for (Integer i : mItemIds) {
                        out.writeInt(i);
                        count--;
                    }
                    assert(count == 0);
                } else {
                    out.writeBoolean(false);
                }
                
                if (getVersion().atLeast(1,20)) {
                    out.writeBoolean(mSkipDelete);
                }
                                    
            } // v10
        } // v9
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        if (getVersion().atLeast(1,9)) {
            // completionId
            mCompletionId = in.readInt();
            
            // types
            if (in.readBoolean()) {
                mTypes = new HashSet<Byte>();
                for (int count = in.readInt(); count > 0; count--) {
                    mTypes.add(in.readByte());
                }
            } else {
                mTypes = null;
            }
            
            // itemIds
            if (getVersion().atLeast(1,10)) {
                if (in.readBoolean()) {
                    mItemIds = new HashSet<Integer>();
                    for (int count = in.readInt(); count > 0; count--) {
                        mItemIds.add(in.readInt());
                    }
                }
                if (getVersion().atLeast(1,20)) {
                    mSkipDelete = in.readBoolean();
                }
            } else {
                mItemIds = null;
                mSkipDelete = false;
            } // v10
            
        } // v9
    }
}
