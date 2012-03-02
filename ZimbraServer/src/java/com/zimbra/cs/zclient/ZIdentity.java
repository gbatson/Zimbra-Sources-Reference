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

package com.zimbra.cs.zclient;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.KeyValuePair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Provisioning;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZIdentity  implements ToZJSONObject {

    private String mName;
    private String mId;
    private Map<String, Object> mAttrs;

    public ZIdentity(Element e) throws ServiceException {
        mName = e.getAttribute(AccountConstants.A_NAME);
        mId = e.getAttribute(AccountConstants.A_ID, null);
        mAttrs = new HashMap<String, Object>();
        for (KeyValuePair pair : e.listKeyValuePairs(AccountConstants.E_A, AccountConstants.A_NAME)) {
            StringUtil.addToMultiMap(mAttrs, pair.getKey(), pair.getValue());
        }
    }

    public ZIdentity(String name, Map<String, Object> attrs) {
        mName = name;
        mAttrs = attrs;
        mId = get(Provisioning.A_zimbraPrefIdentityId);
    }

    public String getRawName() {
        return mName;
    }

    public String getName() {
        return get(Provisioning.A_zimbraPrefIdentityName);
    }

    public String getId() {
        return mId;
    }
    
    /**
     * @param name name of pref to get
     * @return null if unset, or first value in list
     */
    public String get(String name) {
        Object value = mAttrs.get(name);
        if (value == null) {
            return null;
        } else if (value instanceof String[]) {
            return ((String[])value)[0];
        } else if (value instanceof List) {
            return (String) ((List)value).get(0);
        } else {
            return value.toString();
        }
    }
    
    public Map<String, Object> getAttrs() {
        return new HashMap<String, Object>(mAttrs);
    }

    public boolean getBool(String name) {
        return Provisioning.TRUE.equals(get(name));
    }

    public boolean isDefault() { return mName.equals(Provisioning.DEFAULT_IDENTITY_NAME); }
    
    public String getFromAddress() { return get(Provisioning.A_zimbraPrefFromAddress); }

    public String getFromDisplay() { return get(Provisioning.A_zimbraPrefFromDisplay); }

    public ZEmailAddress getFromEmailAddress() {
        return new ZEmailAddress(getFromAddress(), null, getFromDisplay(), ZEmailAddress.EMAIL_TYPE_FROM);
    }

    public String getSignatureId() { return get(Provisioning.A_zimbraPrefDefaultSignatureId); }

    public String getReplyToAddress() { return get(Provisioning.A_zimbraPrefReplyToAddress); }

    public String getReplyToDisplay() { return get(Provisioning.A_zimbraPrefReplyToDisplay); }

    public ZEmailAddress getReplyToEmailAddress() {
        return new ZEmailAddress(getReplyToAddress(), null, getReplyToDisplay(), ZEmailAddress.EMAIL_TYPE_REPLY_TO);
    }

    public boolean getReplyToEnabled() { return getBool(Provisioning.A_zimbraPrefReplyToEnabled); }

    public String[] getMulti(String name) {
        Object o = mAttrs.get(name);
        if (o instanceof String[]) {
            return (String[]) o;
        } else if (o instanceof String) {
            return new String[] { o.toString() };
        } else {
            return new String[0];
        }
    }
    
    public String[] getWhenInFolderIds() {
        return getMulti(Provisioning.A_zimbraPrefWhenInFolderIds);
    }

    public boolean getWhenInFoldersEnabled() { return getBool(Provisioning.A_zimbraPrefWhenInFoldersEnabled); }

    public boolean containsFolderId(String folderId) {
        for (String id : getWhenInFolderIds()) {
            if (id.equals(folderId)) {
                return true;
            }
        }
        return false;
    }

    public Element toElement(Element parent) {
        Element identity = parent.addElement(AccountConstants.E_IDENTITY);
        identity.addAttribute(AccountConstants.A_NAME, mName).addAttribute(AccountConstants.A_ID, mId);
        for (Map.Entry<String,Object> entry : mAttrs.entrySet()) {
            if (entry.getValue() instanceof String[]) {
                String[] values = (String[]) entry.getValue();
                for (String value : values) {
                    identity.addKeyValuePair(entry.getKey(), value, AccountConstants.E_A,  AccountConstants.A_NAME);
                }
            } else {
                identity.addKeyValuePair(entry.getKey(), entry.getValue().toString(), AccountConstants.E_A,  AccountConstants.A_NAME);
            }
        }
        return identity;
    }
    
    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject zjo = new ZJSONObject();
        zjo.put("name", mName);
        zjo.put("id", mId);
        JSONObject jo = new JSONObject();
        zjo.put("attrs",jo);
        for (Map.Entry<String, Object> entry : mAttrs.entrySet()) {
            if (entry.getValue() instanceof String[]) {
                String[] values = (String[]) entry.getValue();
                JSONArray ja = new JSONArray();
                jo.put(entry.getKey(), ja);
                for (String v: values) {
                    ja.put(v);
                }
            } else {
                jo.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return zjo;
    }

    public String toString() {
        return String.format("[ZIdentity %s]", mName);
    }

    public String dump() {
        return ZJSONObject.toString(this);
    }

}
