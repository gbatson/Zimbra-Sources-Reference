/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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

package com.zimbra.cs.im;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zimbra.common.mime.MimeConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.html.BrowserDefang;
import com.zimbra.cs.html.DefangFactory;
import com.zimbra.cs.html.HtmlDefang;

/**
 *
 */
public class IMMessage {
    public static enum Lang {
        EN, DEFAULT;
    }
    
    public static class TextPart {
        private Lang mLang;
        private String mPlaintext = null;
        private String mXHTMLAsText = null;
        private org.dom4j.Element mXHTML; // includes <body> as root
        
        public TextPart(Lang lang, String plaintext) {
            mLang = lang;
            mPlaintext = plaintext;
        }
        
        public TextPart(org.dom4j.Element body) {
            mXHTML = body;
        }
        
        public TextPart(String plaintext) {
            this(Lang.DEFAULT, plaintext);
        }
        
        public TextPart(String plaintext, org.dom4j.Element body) {
            mXHTML = body;
            mPlaintext = plaintext;
        }
            
        public Lang getLang() { return mLang; }
        
        public String getXHTMLAsString() {
            if (mXHTMLAsText != null)
                return mXHTMLAsText;
            if (mXHTML == null)
                return null;
            
            // mXHTML is the <body>...</body> -- we don't want to include the
            // body tag, so we'll just hack it out of the text for now
            String s = mXHTML.asXML();
            int start = s.indexOf('>');
            int end = s.lastIndexOf('<');
            if (start > 0 && end > 0) {
                mXHTMLAsText = s.substring(start+1, end);
            } else {
                mXHTMLAsText = s;
            }
            return mXHTMLAsText;
        }
        
        public String getPlainText() {
            if (mPlaintext != null)
                return mPlaintext;
            if (mXHTML == null)
                return mPlaintext;
            
            mPlaintext = depthFirstTextExtract(mXHTML);
            return mPlaintext;
        }
        
        public org.dom4j.Element getXHTML() {
            return mXHTML;
        }
        
        public boolean hasXHTML() {
            return getXHTMLAsString() != null;
        }
        
        private String depthFirstTextExtract(org.dom4j.Element cur) {
            StringBuilder toRet = new StringBuilder();
            for (Iterator nodeIter = cur.nodeIterator(); nodeIter.hasNext();) {
                org.dom4j.Node node = (org.dom4j.Node)nodeIter.next();
                if (node instanceof org.dom4j.Element)
                    toRet.append(depthFirstTextExtract((org.dom4j.Element)node));
                else if (node instanceof org.dom4j.Namespace) 
                    ;
                else
                    toRet.append(node.asXML());
            }
            
            return toRet.toString();
        }
        
        @Override
        public String toString() {
            if (hasXHTML()) 
                return getXHTMLAsString();
            else
                return getPlainText();
        }
    }
    
    @Override
    public String toString() {
        return "MESSAGE("+mDate+"): "+mSubject+" "+mBody;
    }
    
    public void setFrom(IMAddr from) { mFrom = from; }
    public void setTo(IMAddr to) { mTo = to; }
    
    
    public IMMessage(TextPart subject, TextPart body, boolean isTyping) {
        mSubject = subject;
        mBody = body;
        mDate = new Date();
        mIsTyping = isTyping;
    }
    
    void addSubject(TextPart subj) {
        if (subj.mLang == Lang.DEFAULT) {
            mSubject = subj;
            return;
        }
        
        if (mSubject == null) {
            mSubject = subj;
        }

        if (mLangSubjects == null) {
            mLangSubjects = new HashMap();
        }
        
        mLangSubjects.put(subj.mLang, subj);
    }
    
    void addBody(TextPart body) {
        if (body.mLang == Lang.DEFAULT) {
            mBody = body;
            return;
        }
        
        if (mBody == null) {
            mBody = body;
        }
        
        if (mLangBodies == null) {
            mLangBodies = new HashMap();
        }
        
        mLangBodies.put(body.mLang, body);
    }
    
    public boolean isTyping() { return mIsTyping; }
    
    private TextPart mSubject;
    private TextPart mBody;
    
    private Map<Lang, TextPart> mLangSubjects;
    private Map<Lang, TextPart> mLangBodies;
    private Date mDate;
    private IMAddr mFrom;
    private IMAddr mTo;
    private boolean mIsTyping;
    
    public Element toXml(Element parent) {
        {
            if (mSubject != null) {
                Element e = parent.addElement("subject");
                e.addElement(mSubject.getPlainText());
            }
        }
        {
            if (mBody != null) {
                Element e = parent.addElement("body");
                try {
                    if (mBody.hasXHTML()) {
                        e.addAttribute("html", true);
                        BrowserDefang defanger = DefangFactory.getDefanger(MimeConstants.CT_TEXT_HTML);
                        e.setText(defanger.defang(mBody.getXHTMLAsString(), true));
                    } else {
                        e.addAttribute("html", false);
                        e.setText(mBody.getPlainText());
                    }
                } catch(IOException ex) {
                    e.addAttribute("html", true);
                    e.setText("Unable to encode text: "+ex);
                    ZimbraLog.im.warn("Caught exception while HtmlDefang-ing IM message: \""+mBody.toString()+"\"", ex);
                }
            }
        }
        return parent;
    }

    public IMAddr getFrom() { return mFrom; }
    public IMAddr getTo() { return mTo; }
    public long getTimestamp() { return mDate.getTime(); }
    public Date getDate() { return mDate; }
    
    public TextPart getSubject(Lang lang) {
        if (lang == Lang.DEFAULT
                || (mSubject != null && mSubject.mLang == lang)
                || mLangSubjects==null 
                || !mLangSubjects.containsKey(lang)
        ) {
            return mSubject;
        }
        
        return mLangSubjects.get(lang);
    }
    
    public TextPart getBody() {
        return getBody(Lang.DEFAULT);
    }
    
    public TextPart getBody(Lang lang) {
        if (lang == Lang.DEFAULT
                || (mBody != null && mBody.mLang == lang)
                || mLangBodies ==null 
                || !mLangBodies.containsKey(lang)
        ) {
            return mBody;
        }
        
        return mLangBodies.get(lang);
    }
}
