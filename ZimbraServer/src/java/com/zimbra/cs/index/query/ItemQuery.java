/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2012, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.cs.index.query;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.DBQueryOperation;
import com.zimbra.cs.index.NoResultsQueryOperation;
import com.zimbra.cs.index.QueryOperation;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.service.util.ItemId;

/**
 * Query by item ID.
 *
 * @author tim
 * @author ysasaki
 */
public final class ItemQuery extends Query {

    private final boolean isAllQuery;
    private final boolean isNoneQuery;
    private final List<ItemId> itemIds;

    public static Query create(Mailbox mbox, String str) throws ServiceException {
        boolean allQuery = false;
        boolean noneQuery = false;
        List<ItemId> itemIds = new ArrayList<ItemId>();

        if (str.equalsIgnoreCase("all")) {
            allQuery = true;
        } else if (str.equalsIgnoreCase("none")) {
            noneQuery = true;
        } else {
            String[] items = str.split(",");
            for (int i = 0; i < items.length; i++) {
                if (items[i].length() > 0) {
                    ItemId iid = new ItemId(items[i], mbox.getAccountId());
                    itemIds.add(iid);
                }
            }
            if (itemIds.size() == 0) {
                noneQuery = true;
            }
        }

        return new ItemQuery(allQuery, noneQuery, itemIds);
    }

    ItemQuery(boolean all, boolean none, List<ItemId> ids) {
        this.isAllQuery = all;
        this.isNoneQuery = none;
        this.itemIds = ids;
    }

    @Override
    public boolean hasTextOperation() {
        return false;
    }

    @Override
    public QueryOperation compile(Mailbox mbox, boolean bool) {
        DBQueryOperation dbOp = new DBQueryOperation();
        bool = evalBool(bool);
        if (bool && isAllQuery || !bool && isNoneQuery) {
            // adding no constraints should match everything...
        } else if (bool && isNoneQuery || !bool && isAllQuery) {
            return new NoResultsQueryOperation();
        } else {
            for (ItemId iid : itemIds) {
                dbOp.addItemIdClause(mbox, iid, bool);
            }
        }
        return dbOp;
    }

    @Override
    public void dump(StringBuilder out) {
        out.append("ITEMID");
        if (isAllQuery) {
            out.append(",all");
        } else if (isNoneQuery) {
            out.append(",none");
        } else {
            for (ItemId id : itemIds) {
                out.append(',');
                out.append(id.toString());
            }
        }
    }

    @Override
    public void sanitizedDump(StringBuilder out) {
        out.append("ITEMID");
        if (isAllQuery) {
            out.append(",all");
        } else if (isNoneQuery) {
            out.append(",none");
        } else {
            out.append(Strings.repeat(",$TEXT", itemIds.size()));
        }
    }
}
