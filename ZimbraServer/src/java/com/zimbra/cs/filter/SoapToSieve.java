/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.filter;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.filter.FilterUtil.Condition;
import com.zimbra.cs.filter.FilterUtil.DateComparison;
import com.zimbra.cs.filter.FilterUtil.Flag;
import com.zimbra.cs.filter.FilterUtil.NumberComparison;
import com.zimbra.cs.filter.FilterUtil.StringComparison;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SoapToSieve {
    
    private Element mRoot;
    private StringBuilder mBuf;

    public SoapToSieve(Element filterRulesRoot)
    throws ServiceException {
        String name = filterRulesRoot.getName(); 
        if (!name.equals(MailConstants.E_FILTER_RULES)) {
            throw ServiceException.FAILURE("Invalid element: " + name, null);
        }
        mRoot = filterRulesRoot;
    }
    
    public String getSieveScript()
    throws ServiceException {
        if (mBuf == null) {
            mBuf = new StringBuilder();
            mBuf.append("require [\"fileinto\", \"reject\", \"tag\", \"flag\"];\n");
            for (Element rule : mRoot.listElements(MailConstants.E_FILTER_RULE)) {
                mBuf.append("\n");
                handleRule(rule);
            }
        }
        return mBuf.toString();
    }
    
    private void handleRule(Element rule)
    throws ServiceException {
        String name = rule.getAttribute(MailConstants.A_NAME);
        boolean isActive = rule.getAttributeBool(MailConstants.A_ACTIVE, true);
        
        Element testsElement = rule.getElement(MailConstants.E_FILTER_TESTS);
        String s = testsElement.getAttribute(MailConstants.A_CONDITION, Condition.allof.toString());
        s = s.toLowerCase();
        Condition condition = Condition.fromString(s);
        
        // Rule name
        mBuf.append("# ").append(name).append("\n");
        if (isActive) {
            mBuf.append("if ");
        } else {
            mBuf.append("disabled_if ");
        }
        mBuf.append(condition).append(" (");
        
        // Handle tests
        List<Element> testElements = testsElement.listElements();
        Map<Integer, String> tests = new TreeMap<Integer, String>();
        for (Element test : testElements) {
            s = handleTest(test);
            if (s != null) {
                int index = FilterUtil.getIndex(test);
                FilterUtil.addToMap(tests, index, s);
            }
        }
        mBuf.append(StringUtil.join(",\n  ", tests.values()));
        mBuf.append(") {\n");
        
        // Handle actions
        Element actionsElement = rule.getElement(MailConstants.E_FILTER_ACTIONS);
        Map<Integer, String> actions = new TreeMap<Integer, String>(); // Sorts by index 
        for (Element action : actionsElement.listElements()) {
            s = handleAction(action);
            if (s != null) {
                int index = FilterUtil.getIndex(action);
                FilterUtil.addToMap(actions, index, s);
            }
        }
        for (String action : actions.values()) {
            mBuf.append("    ").append(action).append(";\n");
        }
        mBuf.append("}\n");
    }
    
    private String handleTest(Element test)
    throws ServiceException {
        String name = test.getName();
        String snippet = null;
        
        if (name.equals(MailConstants.E_HEADER_TEST)) {
            snippet = generateHeaderTest(test, "header");
        } else if (name.equals(MailConstants.E_MIME_HEADER_TEST)) {
            snippet = generateHeaderTest(test, "mime_header");
        } else if (name.equals(MailConstants.E_HEADER_EXISTS_TEST)) {
            String header = test.getAttribute(MailConstants.A_HEADER);
            snippet = String.format("exists \"%s\"", FilterUtil.escape(header));
        } else if (name.equals(MailConstants.E_SIZE_TEST)) {
            String s = test.getAttribute(MailConstants.A_NUMBER_COMPARISON);
            s = s.toLowerCase();
            NumberComparison comparison = NumberComparison.fromString(s);
            String sizeString = test.getAttribute(MailConstants.A_SIZE);
            try {
                FilterUtil.parseSize(sizeString);
            } catch (NumberFormatException e) {
                throw ServiceException.INVALID_REQUEST("Invalid size: " + sizeString, e);
            }
            snippet = String.format("size :%s %s", comparison, sizeString);
        } else if (name.equals(MailConstants.E_DATE_TEST)) {
            String s = test.getAttribute(MailConstants.A_DATE_COMPARISON);
            s = s.toLowerCase();
            DateComparison comparison = DateComparison.fromString(s);
            Date date = new Date(test.getAttributeLong(MailConstants.A_DATE) * 1000);
            snippet = String.format("date :%s \"%s\"",
                comparison, FilterUtil.SIEVE_DATE_PARSER.format(date));
        } else if (name.equals(MailConstants.E_BODY_TEST)) {
            boolean caseSensitive = test.getAttributeBool(MailConstants.A_CASE_SENSITIVE, false);
            String value = test.getAttribute(MailConstants.A_VALUE);
            String snippetFormat = caseSensitive ? "body :contains :comparator \"i;octet\" \"%s\"" : "body :contains \"%s\"";
            snippet = String.format(snippetFormat, FilterUtil.escape(value));
        } else if (name.equals(MailConstants.E_ADDRESS_BOOK_TEST)) {
            String header = test.getAttribute(MailConstants.A_HEADER);
            String folderPath = test.getAttribute(MailConstants.A_FOLDER_PATH);
            snippet = String.format("addressbook :in \"%s\" \"%s\"",
                FilterUtil.escape(header), FilterUtil.escape(folderPath));
        } else if (name.equals(MailConstants.E_ATTACHMENT_TEST)) {
            snippet = "attachment";
        } else if (name.equals(MailConstants.E_INVITE_TEST)) {
            snippet = convertInviteTest(test);
        } else if (name.equals(MailConstants.E_CURRENT_TIME_TEST)) {
            String s = test.getAttribute(MailConstants.A_DATE_COMPARISON);
            s = s.toLowerCase();
            DateComparison comparison = DateComparison.fromString(s);
            String time = test.getAttribute(MailConstants.A_TIME);
            // validate time value
            String timeFormat = "HHmm";
            SimpleDateFormat parser = new SimpleDateFormat(timeFormat);
            parser.setLenient(false);
            try {
                parser.parse(time);
            } catch (ParseException e) {
                throw ServiceException.INVALID_REQUEST("Invalid time: " + time, e);
            }
            if (time.length() != timeFormat.length()) {
                throw ServiceException.INVALID_REQUEST("Time string must be of length " + timeFormat.length(), null);
            }
            snippet = String.format("current_time :%s \"%s\"", comparison, time);
        } else if (name.equals(MailConstants.E_CURRENT_DAY_OF_WEEK_TEST)) {
            String value = test.getAttribute(MailConstants.A_VALUE);
            String[] daysOfWeek = value.split(",");
            for (int i = 0; i < daysOfWeek.length; i ++) {
                // first validate value
                try {
                    int day = Integer.valueOf(daysOfWeek[i]);
                    if (day < 0 || day > 6)
                        throw ServiceException.INVALID_REQUEST(
                                "Day of week index must be from 0 (Sunday) to 6 (Saturday)", null);
                } catch (NumberFormatException e) {
                    throw ServiceException.INVALID_REQUEST("Invalid day of week index: " + daysOfWeek[i], e);
                }

                daysOfWeek[i] = StringUtil.enclose(daysOfWeek[i], '"');
            }
            snippet = String.format("current_day_of_week :is %s",
                                    new StringBuilder().append('[').
                                            append(StringUtil.join(",", daysOfWeek)).
                                            append(']').toString());
        } else if (name.equals(MailConstants.E_TRUE_TEST)) {
            snippet = "true";
        } else {
            ZimbraLog.soap.debug("Ignoring unexpected test %s.", name);
        }
        
        if (snippet != null && test.getAttributeBool(MailConstants.A_NEGATIVE, false)) {
            snippet = "not " + snippet;
        }
        return snippet;
    }
    
    private String generateHeaderTest(Element test, String testName)
    throws ServiceException {
        String header = test.getAttribute(MailConstants.A_HEADER);
        if (!StringUtil.isNullOrEmpty(header)) {
            String[] headerNames = header.split(",");
            for (int i = 0; i < headerNames.length; i ++) {
                headerNames[i] = StringUtil.enclose(FilterUtil.escape(headerNames[i]), '"');
            }
            header = new StringBuilder().append('[').append(StringUtil.join(",", headerNames)).append(']').toString();
        }
        String s = test.getAttribute(MailConstants.A_STRING_COMPARISON);
        s = s.toLowerCase();
        StringComparison comparison = StringComparison.fromString(s);
        boolean caseSensitive = test.getAttributeBool(MailConstants.A_CASE_SENSITIVE, false);
        String value = test.getAttribute(MailConstants.A_VALUE);
        String snippetFormat = caseSensitive ? "%s :%s :comparator \"i;octet\" %s \"%s\"" : "%s :%s %s \"%s\"";
        String snippet = String.format(snippetFormat, testName, comparison, header, FilterUtil.escape(value));
        
        // Bug 35983: disallow more than four asterisks in a row.
        if (comparison == StringComparison.matches && value != null && value.contains("*****")) {
            throw ServiceException.INVALID_REQUEST(
                "Wildcard match value cannot contain more than four asterisks in a row.", null);
        }
        return snippet;
    }
    
    private String convertInviteTest(Element test) {
        StringBuilder buf = new StringBuilder("invite");
        List<Element> methods = test.listElements(MailConstants.E_METHOD);
        if (!methods.isEmpty()) {
            buf.append(" :method [");
            boolean firstTime = true;
            for (Element method : methods) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    buf.append(", ");
                }
                buf.append('"');
                buf.append(FilterUtil.escape(method.getText()));
                buf.append('"');
            }
            buf.append("]");
        }
        return buf.toString();
    }
    
    private String handleAction(Element action)
    throws ServiceException {
        String name = action.getName();
        if (name.equals(MailConstants.E_ACTION_KEEP)) {
            return "keep";
        } else if (name.equals(MailConstants.E_ACTION_DISCARD)) {
            return "discard";
        } else if (name.equals(MailConstants.E_ACTION_FILE_INTO)) {
            String folderPath = action.getAttribute(MailConstants.A_FOLDER_PATH);
            return String.format("fileinto \"%s\"", FilterUtil.escape(folderPath));
        } else if (name.equals(MailConstants.E_ACTION_TAG)) {
            String tagName = action.getAttribute(MailConstants.A_TAG_NAME);
            return String.format("tag \"%s\"", FilterUtil.escape(tagName));
        } else if (name.equals(MailConstants.E_ACTION_FLAG)) {
            String s = action.getAttribute(MailConstants.A_FLAG_NAME);
            Flag flag = Flag.valueOf(s);
            return String.format("flag \"%s\"", flag);
        } else if (name.equals(MailConstants.E_ACTION_REDIRECT)) {
            String address = action.getAttribute(MailConstants.A_ADDRESS);
            return String.format("redirect \"%s\"", FilterUtil.escape(address));
        } else if (name.equals(MailConstants.E_ACTION_REPLY)) {
            String bodyTemplate = action.getAttribute(MailConstants.E_CONTENT);
            return new StringBuilder("reply text:\r\n").
                    append(getDotStuffed(bodyTemplate)).
                    append("\r\n.\r\n").toString();
        } else if (name.equals(MailConstants.E_ACTION_NOTIFY)) {
            String emailAddr = action.getAttribute(MailConstants.A_ADDRESS);
            String subjectTemplate = action.getAttribute(MailConstants.A_SUBJECT, "");
            String bodyTemplate = action.getAttribute(MailConstants.E_CONTENT, "");
            int maxBodyBytes = action.getAttributeInt(MailConstants.A_MAX_BODY_SIZE, -1);
            return new StringBuilder("notify ").
                    append(StringUtil.enclose(emailAddr, '"')).append(" ").
                    append(StringUtil.enclose(subjectTemplate, '"')).append(" ").
                    append("text:\r\n").append(getDotStuffed(bodyTemplate)).append("\r\n.\r\n").
                    append(maxBodyBytes == -1 ? "" : " " + maxBodyBytes).toString();
        } else if (name.equals(MailConstants.E_ACTION_STOP)) {
            return "stop";
        } else {
            ZimbraLog.soap.debug("Ignoring unexpected action '%s'", name);
        }
        return null;
    }

    private static String getDotStuffed(String bodyTemplate) {
        return bodyTemplate.replaceAll("\r\n\\.", "\r\n..");
    }
}
