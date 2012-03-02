/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/*
 * Created on Nov 11, 2004
 *
 */
package com.zimbra.cs.filter.jsieve;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ListIterator;

import javax.mail.Part;

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.tests.AbstractTest;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.HtmlTextExtractor;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.mime.MimeConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.filter.ZimbraMailAdapter;
import com.zimbra.cs.mime.MPartInfo;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.mime.ParsedMessage;

public class BodyTest extends AbstractTest {

    static final String CONTAINS = ":contains";
    
    @Override
    protected boolean executeBasic(MailAdapter mail, Arguments arguments, SieveContext context)
            throws SieveException {
        String comparator = null;
        String key = null;
        @SuppressWarnings("unchecked")
        ListIterator<Argument> argumentsIter = arguments.getArgumentList().listIterator();

        // First argument MUST be a tag of ":contains"
        // TODO: handles ":matches" with * and ?
        if (argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof TagArgument)
            {
                String tag = ((TagArgument) argument).getTag();
                if (tag.equals(CONTAINS))
                    comparator = tag;
                else
                    throw new SyntaxException(
                        "Found unexpected TagArgument: \"" + tag + "\"");
            }
        }
        if (null == comparator)
            throw new SyntaxException("Expecting \"" + CONTAINS + "\"");

        // Second argument MUST be a string
        if (argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof StringListArgument) {
                StringListArgument strList = (StringListArgument) argument;
                key = strList.getList().get(0);
            }
        }
        if (null == key)
            throw new SyntaxException("Expecting a string");

        // There MUST NOT be any further arguments
        if (argumentsIter.hasNext())
            throw new SyntaxException("Found unexpected argument(s)");               
        if (!(mail instanceof ZimbraMailAdapter))
            return false;
        return test(mail, key);

    }
    
    @Override
    protected void validateArguments(Arguments arguments, SieveContext context) {
        // override validation -- it's already done in executeBasic above
    }

    private boolean test(MailAdapter mail, String substring) {
        ZimbraMailAdapter zimbraMail = (ZimbraMailAdapter) mail;
        ParsedMessage pm = zimbraMail.getParsedMessage();
        if (pm == null) {
            return false;
        }

        Account acct = null;
        try {
            acct = zimbraMail.getMailbox().getAccount();
        } catch (ServiceException ignored) { }
        String charset = (acct == null ? null : acct.getAttr(Provisioning.A_zimbraPrefMailDefaultCharset, null));

        for (MPartInfo mpi : pm.getMessageParts()) {
            String cType = mpi.getContentType();
            // Check only parts that are text/plain or text/html and are not attachments.
            if (!Part.ATTACHMENT.equals(mpi.getDisposition())) {
                if (cType.equals(MimeConstants.CT_TEXT_PLAIN)) {
                    InputStream in = null;
                    try {
                        in = mpi.getMimePart().getInputStream();
                        Reader reader = (charset == null ? new InputStreamReader(in) : new InputStreamReader(in, charset));
                        if (contains(reader, substring)) {
                            return true;
                        }
                    } catch (Exception e) {
                        ZimbraLog.filter.warn("Unable to test text body for substring '%s'", substring, e);
                    } finally {
                        ByteUtil.closeStream(in);
                    }
                } else if (cType.equals(MimeConstants.CT_TEXT_HTML)) {
                    InputStream in = null;

                    try {
                        // Extract up to 1MB of text and check for substring.
                        in = mpi.getMimePart().getInputStream();
                        Reader reader = Mime.getTextReader(in, cType, charset);
                        String text = HtmlTextExtractor.extract(reader, 1024 * 1024);
                        if (contains(new StringReader(text), substring)) {
                            return true;
                        }
                    } catch (Exception e) {
                        ZimbraLog.filter.warn("Unable to test HTML body for substring '%s'", substring, e);
                    } finally {
                        ByteUtil.closeStream(in);
                    }
                }
            }
        }
        return false;
    }
    
    private boolean contains(Reader reader, String substring)
    throws IOException {
        int matchIndex = 0;
        substring = substring.toLowerCase(); // Do case-insensitive matching, like we do with headers.
        PushbackReader pb = new PushbackReader(reader, substring.length() - 1);
        char[] substringArray = substring.toCharArray();
        int c;
        while ((c = getNextChar(pb)) > 0) {
            if (substring.charAt(matchIndex) == Character.toLowerCase(c)) {
                matchIndex++;
                if (matchIndex == substring.length())
                    return true;
            } else if (matchIndex > 0) {
                // unread this non-matching char
                pb.unread(c);
                // unread matched chars except the first char that matched
                pb.unread(substringArray, 1, matchIndex - 1);
                matchIndex = 0;
            }
        }
        return false;
    }
    
    private int getNextChar(PushbackReader reader)
    throws IOException {
        int c = reader.read();
        if (c != '\r' && c != '\n') {
            // The end, or not a newline character.
            return c;
        }
        
        // Replace multiple newline characters with a single space.
        do {
            c = reader.read();
        } while (c == '\r' || c == '\n');
            
        if (c >= 0) {
            // Push the last character back, so that it's read next time.
            reader.unread(c);
        }
        return ' ';
    }
}
