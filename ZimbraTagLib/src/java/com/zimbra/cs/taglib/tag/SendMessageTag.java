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
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.client.ZEmailAddress;
import com.zimbra.client.ZMailbox;
import com.zimbra.client.ZMailbox.ZOutgoingMessage;
import com.zimbra.client.ZMailbox.ZSendMessageResponse;
import com.zimbra.client.ZMailbox.ZOutgoingMessage.AttachedMessagePart;
import com.zimbra.client.ZMailbox.ZOutgoingMessage.MessagePart;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendMessageTag extends ZimbraSimpleTag {

    private String mVar;
    private String mTo;
    private String mReplyTo;
    private String mCc;
    private String mBcc;
    private String mFrom;
    private String mSubject;
    private String mPriority;
    private String mContentType = "text/plain";
    private String mContent;
    private String mReplyType;
    private String mInReplyTo;
    private String mMessageId;    
    private String mMessages;
    private String mAttachments;
    private ZMessageComposeBean mCompose;

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }
    
    public void setVar(String var) { this.mVar = var; }
    
    public void setTo(String to) { mTo = to; }

    public void setReplyto(String replyTo) { mReplyTo = replyTo; }
    
    public void setReplytype(String replyType) { mReplyType = replyType; }

    public void setContent(String content) { mContent = content; }

    public void setContenttype(String contentType) { mContentType = contentType; }

    public void setSubject(String subject) { mSubject = subject; }

    public void setPriority(String priority) { mPriority = priority; }

    public void setMessageid(String id) { mMessageId = id; }

    public void setInreplyto(String inReplyto) { mInReplyTo = inReplyto; }

    public void setFrom(String from) { mFrom = from; }

    public void setBcc(String bcc) { mBcc = bcc; }

    public void setCc(String cc) { mCc = cc; }

    public void setMessages(String messages) { mMessages = messages; }

    public void setAttachments(String attachments) { mAttachments = attachments; }
    
    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            
            ZOutgoingMessage m = mCompose != null ? mCompose.toOutgoingMessage(mbox) :  getOutgoingMessage();
            
            ZSendMessageResponse response = mbox.sendMessage(m, mCompose != null ? mCompose.getSendUID() :  null, false);
            jctxt.setAttribute(mVar, response, PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }

    private ZOutgoingMessage getOutgoingMessage() throws ServiceException {

        List<ZEmailAddress> addrs = new ArrayList<ZEmailAddress>();

        if (mTo != null && mTo.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mTo, ZEmailAddress.EMAIL_TYPE_TO));

        if (mReplyTo != null && mReplyTo.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mReplyTo, ZEmailAddress.EMAIL_TYPE_REPLY_TO));

        if (mCc != null && mCc.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mCc, ZEmailAddress.EMAIL_TYPE_CC));

        if (mFrom != null && mFrom.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mFrom, ZEmailAddress.EMAIL_TYPE_FROM));

        if (mBcc != null && mBcc.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mBcc, ZEmailAddress.EMAIL_TYPE_BCC));

        List<String> messages;

        if (mMessages != null && mMessages.length() > 0) {
            messages = new ArrayList<String>();
            for (String m : mMessages.split(",")) {
                messages.add(m);
            }
        } else {
            messages = null;
        }

        List<AttachedMessagePart> attachments;
        if (mAttachments != null && mAttachments.length() > 0) {
            attachments = new ArrayList<AttachedMessagePart>();
            for (String partName : mAttachments.split(",")) {
                attachments.add(new AttachedMessagePart(mMessageId, partName, null));
            }
        } else {
            attachments = null;
        }

        ZOutgoingMessage m = new ZOutgoingMessage();

        m.setAddresses(addrs);

        m.setSubject(mSubject);

        m.setPriority(mPriority);

        if (mInReplyTo != null && mInReplyTo.length() > 0)
            m.setInReplyTo(mInReplyTo);

        m.setMessagePart(new MessagePart(mContentType, mContent));

        m.setMessageIdsToAttach(messages);

        m.setMessagePartsToAttach(attachments);

        if (mMessageId != null && mMessageId.length() > 0 && mReplyType != null && mReplyType.length() > 0)
            m.setOriginalMessageId(mMessageId);

        if (mReplyType != null && mReplyType.length() > 0)
            m.setReplyType(mReplyType);

        return m;

    }



}
