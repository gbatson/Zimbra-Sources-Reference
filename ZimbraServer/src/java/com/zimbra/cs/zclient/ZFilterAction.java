/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.zclient.ZClientException;
import com.zimbra.cs.filter.FilterUtil.Flag;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ZFilterAction implements ToZJSONObject {

    public static final String A_DISCARD = "discard";
    public static final String A_FILEINTO = "fileinto";
    public static final String A_FLAG = "flag";
    public static final String A_KEEP = "keep";
    public static final String A_REDIRECT = "redirect";
    public static final String A_REPLY = "reply";
    public static final String A_NOTIFY = "notify";
    public static final String A_STOP = "stop";
    public static final String A_TAG = "tag";

    public enum MarkOp {

        READ, FLAGGED;

        private static String[] mArgs = { "read", "flagged" };

        public static MarkOp fromString(String s) throws ServiceException {
            try {
                return valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw ZClientException.CLIENT_ERROR("invalid op: "+s+", value values: "+Arrays.asList(values()), null);
            }
        }

        public String toProtoArg() { return mArgs[ordinal()]; }

        public static MarkOp fromProtoString(String s) throws ServiceException {
            s = s.trim().toLowerCase();
            for (int i=0; i < mArgs.length; i++)
                if (mArgs[i].equals(s)) return values()[i];
            throw ZClientException.CLIENT_ERROR("invalid arg "+s+", valid values: "+ Arrays.asList(mArgs), null);
        }
    }

    private String mName;
    protected List<String> mArgs;

    private ZFilterAction(String name, String... args) {
        mName = name;
        mArgs = Collections.unmodifiableList(args != null ? Arrays.asList(args) : new ArrayList<String>());
    }

    public String getName() {
        return mName;
    }

    public List<String> getArgs() {
        return mArgs;
    }

    public abstract String toActionString();

    abstract Element toElement(Element parent);

    public static ZFilterAction getAction(Element actionElement) throws ServiceException {
        String n = actionElement.getName();
        if (n.equals(MailConstants.E_ACTION_KEEP))
            return new ZKeepAction();
        else if (n.equals(MailConstants.E_ACTION_DISCARD))
            return new ZDiscardAction();
        else if (n.equals(MailConstants.E_ACTION_STOP))
            return new ZStopAction();
        else if (n.equals(MailConstants.E_ACTION_FILE_INTO)) {
            String folderPath = actionElement.getAttribute(MailConstants.A_FOLDER_PATH);
            return new ZFileIntoAction(folderPath);
        } else if (n.equals(MailConstants.E_ACTION_TAG)) {
            String tagName = actionElement.getAttribute(MailConstants.A_TAG_NAME);
            return new ZTagAction(tagName);
        } else if (n.equals(MailConstants.E_ACTION_REDIRECT)) {
            String address = actionElement.getAttribute(MailConstants.A_ADDRESS);
            return new ZRedirectAction(address);
        } else if (n.equals(MailConstants.E_ACTION_REPLY)) {
            String bodyTemplate = actionElement.getAttribute(MailConstants.E_CONTENT);
            return new ZReplyAction(bodyTemplate);
        } else if (n.equals(MailConstants.E_ACTION_NOTIFY)) {
            String emailAddr = actionElement.getAttribute(MailConstants.A_ADDRESS);
            String subjectTemplate = actionElement.getAttribute(MailConstants.A_SUBJECT, "");
            String bodyTemplate = actionElement.getAttribute(MailConstants.E_CONTENT, "");
            int maxBodyBytes = actionElement.getAttributeInt(MailConstants.A_MAX_BODY_SIZE, -1);
            String origHeaders = actionElement.getAttribute(MailConstants.A_ORIG_HEADERS, null);
            return new ZNotifyAction(emailAddr, subjectTemplate, bodyTemplate, maxBodyBytes, origHeaders);
        } else if (n.equals(MailConstants.E_ACTION_FLAG)) {
            Flag flag = Flag.fromString(actionElement.getAttribute(MailConstants.A_FLAG_NAME));
            MarkOp op = (flag == Flag.flagged ? MarkOp.FLAGGED : MarkOp.READ); 
            return new ZMarkAction(op);
        } else
            throw ZClientException.CLIENT_ERROR("unknown filter action: "+n, null);
    }

    public static class ZKeepAction extends ZFilterAction {
        public ZKeepAction() { super(A_KEEP); }

        public String toActionString() { return "keep"; }
        
        Element toElement(Element parent) {
            return parent.addElement(MailConstants.E_ACTION_KEEP);
        }
    }

    public static class ZDiscardAction extends ZFilterAction {
        public ZDiscardAction() { super(A_DISCARD); }

        public String toActionString() { return "discard"; }

        Element toElement(Element parent) {
            return parent.addElement(MailConstants.E_ACTION_DISCARD);
        }
    }

    public static class ZStopAction extends ZFilterAction {
        public ZStopAction() { super(A_STOP); }

        public String toActionString() { return "stop"; }

        Element toElement(Element parent) {
            return parent.addElement(MailConstants.E_ACTION_STOP);
        }
    }

    public static class ZFileIntoAction extends ZFilterAction {
        public ZFileIntoAction(String folderPath) { super(A_FILEINTO, folderPath); }

        public String getFolderPath() { return mArgs.get(0); }

        public String toActionString() { return "fileinto " + ZFilterRule.quotedString(getFolderPath()); }

        Element toElement(Element parent) {
            Element action = parent.addElement(MailConstants.E_ACTION_FILE_INTO);
            action.addAttribute(MailConstants.A_FOLDER_PATH, getFolderPath());
            return action;
        }
    }

    public static class ZTagAction extends ZFilterAction {
        public ZTagAction(String tagName) { super(A_TAG, tagName); }
        public String getTagName() { return mArgs.get(0); }

        public String toActionString() { return "tag " + ZFilterRule.quotedString(getTagName()); }

        Element toElement(Element parent) {
            Element action = parent.addElement(MailConstants.E_ACTION_TAG);
            action.addAttribute(MailConstants.A_TAG_NAME, getTagName());
            return action;
        }
    }

    public static class ZMarkAction extends ZFilterAction {
        private MarkOp mMarkOp;
        public ZMarkAction(MarkOp op) {
            super(A_FLAG, op.toProtoArg());
            mMarkOp = op;
        }

        public String getMarkOp() { return mMarkOp.name(); }
        public String toActionString() { return "mark " + mMarkOp.name().toLowerCase(); }
        
        Element toElement(Element parent) {
            Element action = parent.addElement(MailConstants.E_ACTION_FLAG);
            Flag flag = (mMarkOp == MarkOp.FLAGGED ? Flag.flagged : Flag.read);
            action.addAttribute(MailConstants.A_FLAG_NAME, flag.toString());
            return action;
        }
    }

    public static class ZRedirectAction extends ZFilterAction {
        public ZRedirectAction(String address) { super(A_REDIRECT, address); }
        public String getAddress() { return mArgs.get(0); }
        public String toActionString() { return "redirect " + ZFilterRule.quotedString(getAddress()); }
        
        Element toElement(Element parent) {
            Element action = parent.addElement(MailConstants.E_ACTION_REDIRECT);
            action.addAttribute(MailConstants.A_ADDRESS, getAddress());
            return action;
        }
    }

    public static class ZReplyAction extends ZFilterAction {
        public ZReplyAction(String bodyTemplate) { super(A_REPLY, bodyTemplate); }
        public String getBodyTemplate() { return mArgs.get(0); }
        public String toActionString() { return "reply " + getBodyTemplate(); }

        Element toElement(Element parent) {
            Element action = parent.addElement(MailConstants.E_ACTION_REPLY);
            action.addElement(MailConstants.E_CONTENT).addText(getBodyTemplate());
            return action;
        }
    }

    public static class ZNotifyAction extends ZFilterAction {
        public ZNotifyAction(String emailAddr, String subjectTemplate, String bodyTemplate) {
            this(emailAddr, subjectTemplate, bodyTemplate, -1);
        }

        public ZNotifyAction(String emailAddr, String subjectTemplate, String bodyTemplate, int maxBodyBytes) {
            this(emailAddr, subjectTemplate, bodyTemplate, maxBodyBytes, null);
        }

        public ZNotifyAction(
                String emailAddr, String subjectTemplate, String bodyTemplate, int maxBodyBytes, String origHeaders) {
            super(A_NOTIFY, emailAddr, subjectTemplate, bodyTemplate, Integer.toString(maxBodyBytes), origHeaders);
        }

        public String getEmailAddr() {
            return mArgs.get(0);
        }

        public String getSubjectTemplate() {
            return mArgs.get(1);
        }

        public String getBodyTemplate() {
            return mArgs.get(2);
        }

        public int getMaxBodyBytes() {
            return Integer.valueOf(mArgs.get(3));
        }

        public String getOrigHeaders() {
            return mArgs.get(4);
        }

        public String toActionString() {
            return "notify " + getEmailAddr() + " " +
                    (StringUtil.enclose(getSubjectTemplate(), '"')) + " " +
                    (StringUtil.enclose(getBodyTemplate(), '"')) +
                    (getMaxBodyBytes() == -1 ? "" : " " + getMaxBodyBytes());
        }

        Element toElement(Element parent) {
            Element action = parent.addElement(MailConstants.E_ACTION_NOTIFY);
            action.addAttribute(MailConstants.A_ADDRESS, getEmailAddr());
            if (!StringUtil.isNullOrEmpty(getSubjectTemplate()))
                action.addAttribute(MailConstants.A_SUBJECT, getSubjectTemplate());
            if (!StringUtil.isNullOrEmpty(getBodyTemplate()))
                action.addElement(MailConstants.E_CONTENT).addText(getBodyTemplate());
            if (getMaxBodyBytes() != -1)
                action.addAttribute(MailConstants.A_MAX_BODY_SIZE, getMaxBodyBytes());
            if (!StringUtil.isNullOrEmpty(getOrigHeaders()))
                action.addAttribute(MailConstants.A_ORIG_HEADERS, getOrigHeaders());
            return action;
        }
    }

    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject jo = new ZJSONObject();
        jo.put("name", mName);
        jo.putList("args", mArgs);
        return jo;
    }

    public String toString() {
        return String.format("[ZFilterAction %s]", mName);
    }

    public String dump() {
        return ZJSONObject.toString(this);
    }
}
