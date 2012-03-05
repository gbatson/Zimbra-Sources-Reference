/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
 * Created on Jun 1, 2004
 *
 */
package com.zimbra.common.service;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

import com.zimbra.common.util.HttpUtil;

@SuppressWarnings("serial")
public class ServiceException extends Exception {

    public static final String FAILURE = "service.FAILURE";
    public static final String INVALID_REQUEST = "service.INVALID_REQUEST";
    public static final String UNKNOWN_DOCUMENT = "service.UNKNOWN_DOCUMENT";
    public static final String PARSE_ERROR = "service.PARSE_ERROR";
    public static final String RESOURCE_UNREACHABLE = "service.RESOURCE_UNREACHABLE";
    public static final String TEMPORARILY_UNAVAILABLE = "service.TEMPORARILY_UNAVAILABLE";
    public static final String PERM_DENIED = "service.PERM_DENIED";
    public static final String AUTH_REQUIRED = "service.AUTH_REQUIRED";
    public static final String AUTH_EXPIRED = "service.AUTH_EXPIRED";
    public static final String WRONG_HOST = "service.WRONG_HOST";
    public static final String NON_READONLY_OPERATION_DENIED = "service.NON_READONLY_OPERATION_DENIED";
    public static final String PROXY_ERROR = "service.PROXY_ERROR";
    public static final String TOO_MANY_HOPS = "service.TOO_MANY_HOPS";
    public static final String ALREADY_IN_PROGRESS = "service.ALREADY_IN_PROGRESS";
    public static final String NOT_IN_PROGRESS = "service.NOT_IN_PROGRESS";
    public static final String INTERRUPTED = "service.INTERRUPTED";
    public static final String NO_SPELL_CHECK_URL = "service.NO_SPELL_CHECK_URL"; 
    public static final String SAX_READER_ERROR = "service.SAX_READER_ERROR";
    
    protected String mCode;
    protected Argument[] mArgs = null;
    private String mId;

    public static final String HOST            = "host";
    public static final String URL             = "url"; 
    public static final String MAILBOX_ID      = "mboxId";
    public static final String ACCOUNT_ID      = "acctId"; 

    public static final String PROXIED_FROM_ACCT  = "proxiedFromAcct"; // exception proxied from remote account

    // to ensure that exception id is unique across multiple servers.
    private static String ID_KEY = null;

    static {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[8];
        random.nextBytes(key);
        ID_KEY = new String(Hex.encodeHex(key));
    }


    @Override public String toString() {
        StringBuilder toRet = new StringBuilder(super.toString());
        toRet.append("\nExceptionId:").append(mId);
        toRet.append("\nCode:").append(mCode);
        if (mArgs != null) {
            for (Argument arg : mArgs) {
                toRet.append(" Arg:").append(arg.toString()).append("");
            }
        }

        return toRet.toString();
    }

    public static class Argument {
        public static enum Type {
            IID,       // mail-item ID or mailbox-id 
            ACCTID,    // account ID
            STR,       // opaque string
            NUM        // opaque number
        }

        public Argument(String name, String value, Type typ) {
            mName = name;
            mValue = value;
            mType = typ;
        }

        public Argument(String name, long value, Type type) {
            mName = name;
            mValue = Long.toString(value);
            mType = type;
        }

        public boolean externalVisible() {
            return true;
        }

        public String mName;
        public String mValue;
        public Type mType;

        @Override 
        public String toString() {
            return "(" + mName + ", " + mType.name() + ", \"" + mValue + "\")";
        }
        
        public String getName() {
            return mName;
        }
    }

    /**
     * Argument that should not be included in SOAP fault.
     * For example: url
     */
    public static class InternalArgument extends Argument {
        public InternalArgument(String name, String value, Type typ) {
            super(name, value, typ);
        }

        public InternalArgument(String name, long value, Type type) {
            super(name, value, type);
        }

        @Override public boolean externalVisible() {
            return false;
        }
    }

    /**
     * Sets the specified argument if it is not already set, updates 
     * it if it is.
     * 
     * @param name
     * @param value
     */
    public void setArgument(String name, String value, Argument.Type type) {
        if (mArgs == null) {
            mArgs = new Argument[1];
            mArgs[0] = new Argument(name, value, type);
        } else {
            for (Argument arg : mArgs) {
                if ((arg.mName.equals(name)) && (arg.mType == type)) {
                    arg.mValue = value;
                    return;
                }
            }

            // not found -- enlarge array
            Argument[] newArgs = new Argument[mArgs.length + 1];
            for (int i = mArgs.length-1; i>=0; i--) {
                newArgs[i] = mArgs[i];
            }

            // add new argument
            newArgs[mArgs.length] = new Argument(name, value, type);
            mArgs = newArgs;
        }
    }

    /**
     * Comment for <code>mReceiver</code>
     * 
     * Causes a Sender/Receiver element to show up in a soap fault. It is supposed to let the client know whether it 
     * did something wrong or something is wrong on the server.  

     * For example, ServiceException.FAILURE sets it to true, meaning something bad happened on the server-side and 
     * the client could attempt a retry. The rest are false, as it generally means the client made a bad request.
     * 
     */
    public static final boolean RECEIVERS_FAULT = true; // server's fault
    public static final boolean SENDERS_FAULT = false; // client's fault
    private boolean mReceiver;

    /**
     * This is for exceptions that are usually not logged and thus need to include an unique "label" 
     * in the exception id so the thrown(or instantiation) location can be identified by the exception id alone
     * (without referencing the log - the stack won't be in the log).
     * 
     * @param callSite call site of the stack where the caller wants to include in the exception id
     */
    public void setIdLabel(StackTraceElement callSite) {
        String fileName = callSite.getFileName();
        int i = fileName.lastIndexOf('.');
        if (i != -1)
            fileName = fileName.substring(0, i);

        mId = mId + ":" + fileName + callSite.getLineNumber();
    }

    private void setId() {
        mId = Thread.currentThread().getName() + ":"+ System.currentTimeMillis() + ":" + ID_KEY;
    }

    protected void setId(String id) { mId = id; }

    protected ServiceException(String message, String code, boolean isReceiversFault, Throwable cause, Argument... arguments)
    {
        super(message, cause);
        mCode = code;
        mReceiver = isReceiversFault;

        mArgs = arguments;

        setId();
    }

    protected ServiceException(String message, String code, boolean isReceiversFault, Argument... arguments)
    {
        super(message);
        mCode = code;
        mReceiver = isReceiversFault;

        mArgs = arguments;

        setId();
    }

    public String getCode() {
        return mCode;
    }

    public Argument[] getArgs() {
        return mArgs;
    }

    public String getId() {
        return mId;
    }

    /**
     * @return See the comment for the mReceiver member
     */
    public boolean isReceiversFault() {
        return mReceiver;
    }

    /**
     * generic system failure. most likely a temporary situation.
     */
    public static ServiceException FAILURE(String message, Throwable cause) {
        return new ServiceException("system failure: "+message, FAILURE, RECEIVERS_FAULT, cause);
    }

    /**
     * The request was somehow invalid (wrong parameter, wrong target, etc)
     */
    public static ServiceException INVALID_REQUEST(String message, Throwable cause) {
        return new ServiceException("invalid request: "+message, INVALID_REQUEST, SENDERS_FAULT, cause);
    }

    /**
     * User sent an unknown SOAP command (the "document" is the Soap Request)
     */
    public static ServiceException UNKNOWN_DOCUMENT(String message, Throwable cause) {
        return new ServiceException("unknown document: "+message, UNKNOWN_DOCUMENT, SENDERS_FAULT, cause);
    }

    public static ServiceException PARSE_ERROR(String message, Throwable cause) {
        return new ServiceException("parse error: "+message, PARSE_ERROR, SENDERS_FAULT, cause);
    }

    public static ServiceException RESOURCE_UNREACHABLE(String message, Throwable cause, Argument... arguments) {
        return new ServiceException("resource unreachable: " + message, RESOURCE_UNREACHABLE, RECEIVERS_FAULT, cause, arguments);
    }

    public static ServiceException TEMPORARILY_UNAVAILABLE() {
        return new ServiceException("service temporarily unavailable", TEMPORARILY_UNAVAILABLE, RECEIVERS_FAULT);
    }

    public static ServiceException PERM_DENIED(String message) {
        return new ServiceException("permission denied: "+message, PERM_DENIED, SENDERS_FAULT);
    }
    
    public static ServiceException PERM_DENIED(String message, Argument... arguments) {
        return new ServiceException("permission denied: "+message, PERM_DENIED, SENDERS_FAULT, arguments);
    }

    public static ServiceException AUTH_EXPIRED(String message) {
        return new ServiceException("auth credentials have expired" + (message==null ? "" : ": "+message), AUTH_EXPIRED, SENDERS_FAULT);
    }
    
    public static ServiceException AUTH_EXPIRED() {
        return AUTH_EXPIRED(null);
    }
    
    // to defend against harvest attacks throw PERM_DENIED instead of NO_SUCH_ACCOUNT
    public static ServiceException DEFEND_ACCOUNT_HARVEST(String account) {
        return PERM_DENIED("can not access account " + account);
        // return new ServiceException("permission denied: can not access account " + account, PERM_DENIED, SENDERS_FAULT);
    }

    public static ServiceException AUTH_REQUIRED() {
        return new ServiceException("no valid authtoken present", AUTH_REQUIRED, SENDERS_FAULT);
    }

    public static ServiceException WRONG_HOST(String target, Throwable cause) {
        return new ServiceException("operation sent to wrong host (you want '" + target + "')", WRONG_HOST, SENDERS_FAULT, cause, new Argument(HOST, target, Argument.Type.STR));
    }

    public static ServiceException NON_READONLY_OPERATION_DENIED() {
        return new ServiceException("non-readonly operation denied", NON_READONLY_OPERATION_DENIED, SENDERS_FAULT);
    }

    public static ServiceException PROXY_ERROR(Throwable cause, String url) {
        return new ServiceException("error while proxying request to target server: " + (cause != null ? cause.getMessage() : "unknown reason"), 
                PROXY_ERROR, RECEIVERS_FAULT, cause, new InternalArgument(URL, url, Argument.Type.STR));
    }

    public static ServiceException PROXY_ERROR(String statusLine, String url) {
        return new ServiceException("error while proxying request to target server: " + statusLine, 
                PROXY_ERROR, RECEIVERS_FAULT, new InternalArgument(URL, url, Argument.Type.STR));
    }

    public static ServiceException TOO_MANY_HOPS() {
        return new ServiceException("mountpoint or proxy loop detected", TOO_MANY_HOPS, SENDERS_FAULT);
    }

    public static ServiceException TOO_MANY_HOPS(String acctId) {
        return new ServiceException("mountpoint or proxy loop detected", TOO_MANY_HOPS, SENDERS_FAULT, new Argument(ACCOUNT_ID, acctId, Argument.Type.STR));
    }

    public static ServiceException TOO_MANY_PROXIES(String url) {
        return new ServiceException("proxy loop detected", TOO_MANY_HOPS, SENDERS_FAULT, new Argument(URL, HttpUtil.sanitizeURL(url), Argument.Type.STR));
    }

    public static ServiceException ALREADY_IN_PROGRESS(String message) {
        return new ServiceException(message, ALREADY_IN_PROGRESS, SENDERS_FAULT);
    }

    public static ServiceException ALREADY_IN_PROGRESS(String mboxId, String action) {
        return new ServiceException("mbox "+mboxId+" is already running action "+action, ALREADY_IN_PROGRESS, SENDERS_FAULT, new Argument(MAILBOX_ID, mboxId, Argument.Type.IID), new Argument("action", action, Argument.Type.STR));
    }

    public static ServiceException NOT_IN_PROGRESS(String mboxId, String action) {
        return new ServiceException("mbox "+mboxId+" is not currently running action "+action, NOT_IN_PROGRESS, SENDERS_FAULT, new Argument(MAILBOX_ID, mboxId, Argument.Type.IID), new Argument("action", action, Argument.Type.STR));
    }

    public static ServiceException INTERRUPTED(String str) {
        return new ServiceException("The operation has been interrupted "+str!=null?str:"", INTERRUPTED, RECEIVERS_FAULT);
    }

    public static ServiceException NO_SPELL_CHECK_URL(String str) {
        return new ServiceException("Spell Checking Not Available "+str!=null?str:"", NO_SPELL_CHECK_URL, RECEIVERS_FAULT);
    }
    
    public static ServiceException SAX_READER_ERROR(String str, Throwable cause) {
        return new ServiceException("SAX Reader Error: " + (str != null ? str : ""), SAX_READER_ERROR, SENDERS_FAULT, cause);
    }
}
