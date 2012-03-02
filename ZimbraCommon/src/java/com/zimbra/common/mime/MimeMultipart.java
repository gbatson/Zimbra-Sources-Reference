/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.common.mime;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MimeMultipart extends MimePart implements Iterable<MimePart> {
    static final String UNSET_BOUNDARY = "";

    private String mBoundary;
    private MimePart mPreamble, mEpilogue;
    private List<MimePart> mChildren = new ArrayList<MimePart>(3);

    public MimeMultipart(String subtype) {
        super(new ContentType("multipart/" + (subtype == null || subtype.trim().equals("") ? "mixed" : subtype) + "; boundary=\"" + generateBoundary() + '"'));
        mBoundary = getContentType().getParameter("boundary");
    }

    MimeMultipart(ContentType ctype, MimePart parent, long start, long body, MimeHeaderBlock headers) {
        super(ctype, parent, start, body, headers);
        mBoundary = ctype.getParameter("boundary");
        if (mBoundary == null || mBoundary.trim().equals(""))
            mBoundary = UNSET_BOUNDARY;
        // RFC 2046 5.1.1: "The only mandatory global parameter for the "multipart" media type is
        //                  the boundary parameter, which consists of 1 to 70 characters from a
        //                  set of characters known to be very robust through mail gateways, and
        //                  NOT ending with white space."
        while (mBoundary.length() > 0 && Character.isWhitespace(mBoundary.charAt(mBoundary.length() - 1)))
            mBoundary = mBoundary.substring(0, mBoundary.length() - 1);
    }

    private static String generateBoundary() {
        // RFC 1521 5.1: "A good strategy is to choose a boundary that includes a character
        //                sequence such as "=_" which can never appear in a quoted-printable body."
        return "=_" + UUID.randomUUID().toString();
    }

    /** Returns the number of child parts of this multipart. */
    public int getCount() {
        return mChildren.size();
    }

    public MimePart getPreamble()  { return mPreamble; }
    public MimePart getEpilogue()  { return mEpilogue; }

    /** Returns the (1-based) <tt>index</tt>th child of this multipart. */
    public MimePart getSubpart(int index) {
        return (index < 1 || index > mChildren.size() ? null : mChildren.get(index - 1));
    }

    @Override public MimePart getSubpart(String part) {
        if (part == null || part.equals(""))
            return this;

        int dot = part.indexOf('.');
        if (dot == 0 || dot == part.length() - 1)
            return null;
        MimePart subpart = null;
        try {
            subpart = getSubpart(Integer.valueOf(dot == -1 ? part : part.substring(0, dot)));
        } catch (NumberFormatException nfe) { }

        if (dot == -1 || subpart == null)
            return subpart;
        else
            return subpart.getSubpart(part.substring(dot + 1));
    }

    @Override Map<String, MimePart> listMimeParts(Map<String, MimePart> parts, String prefix) {
        if (!prefix.equals(""))
            prefix += '.';

        for (int i = 0; i < mChildren.size(); i++) {
            MimePart child = mChildren.get(i);
            String childName = prefix + (i + 1);

            parts.put(childName, child);
            child.listMimeParts(parts, childName);
        }
        return parts;
    }

    /** Returns an iterator over the child parts of this multipart.  Note that
     *  changes made via the iterator (e.g. {@link Iterator#remove()}) will not
     *  affect the contents of the multipart. */
    public Iterator<MimePart> iterator() {
        return new ArrayList<MimePart>(mChildren).iterator();
    }

    public MimeMultipart addPart(MimePart mp) {
        return addPart(mp, mChildren.size() + 1);
    }

    public MimeMultipart addPart(MimePart mp, int index) {
        if (mp == null)
            throw new NullPointerException();
        if (index < 1 || index > mChildren.size() + 1)
            throw new IndexOutOfBoundsException(Integer.toString(index));

        mp.setParent(this);
        mChildren.add(index - 1, mp);
        return this;
    }

    @Override void removeChild(MimePart mp) {
        mChildren.remove(mp);
    }


    @Override void checkContentType(ContentType ctype) {
        if (ctype == null || !ctype.getPrimaryType().equals("multipart"))
            throw new UnsupportedOperationException("cannot change a multipart to text");
    }

    @Override public void setContentType(ContentType ctype) {
        // changing the boundary forces a recalc of the content
        String newBoundary = ctype.getParameter("boundary");
        if (!mBoundary.equals(newBoundary)) {
            markDirty(true);  mBoundary = newBoundary;
        }

        super.setContentType(ctype);
        // FIXME: if moving to/from multipart/digest, make sure to recalculate defaults on subparts
    }


    @Override public InputStream getRawContentStream() throws IOException {
        if (!isDirty())
            return super.getRawContentStream();

        byte[] startBoundary = ("\r\n--" + mBoundary + "\r\n").getBytes();
        List<Object> sources = new ArrayList<Object>(mChildren.size() * 2 + 3);
        if (mPreamble != null)
            sources.add(mPreamble);
        for (MimePart mp : mChildren) {
            sources.add(sources.isEmpty() ? ("--" + mBoundary + "\r\n").getBytes() : startBoundary);
            sources.add(mp);
        }
        sources.add(("\r\n--" + mBoundary + "--\r\n").getBytes());
        if (mEpilogue != null)
            sources.add(mEpilogue);
        return new VectorInputStream(sources);
    }


    @Override List<String> getActiveBoundaries() {
        List<String> inherited = getParent() == null ? null : getParent().getActiveBoundaries();
        if (mBoundary != null)
            (inherited = inherited == null ? new ArrayList<String>(4) : inherited).add(0, mBoundary);
        return inherited;
    }

    @Override MimePart readContent(ParseState pstate) throws IOException {
        // first read the MIME preamble
        MimePart preamble = new MimeBodyPart(new ContentType(ContentType.TEXT_PLAIN), this, getBodyOffset(), pstate.getPosition(), null).readContent(pstate);
        ParseState.BoundaryTerminator bterm = pstate.getBoundaryTerminator();

        // if the Content-Type didn't define the multipart boundary, pick it up from the preamble terminator
        if (mBoundary == UNSET_BOUNDARY && bterm != null && !bterm.mWasEndBoundary && !getActiveBoundaries().contains(bterm.mBoundary))
            mBoundary = bterm.mBoundary;

        if (bterm == null || bterm.mWasEndBoundary || mBoundary == null || mBoundary.equals(UNSET_BOUNDARY) || !mBoundary.equals(bterm.mBoundary)) {
            // if there's no boundary match or we're otherwise fucked, the preamble becomes the only content part
            mChildren.add(preamble);
        } else {
            if (preamble.getSize() > 0)
                mPreamble = preamble;

            // read parts until we hit our end boundary, a parent's boundary, or EOF
            String defaultContentType = getContentType().getSubType().equals("digest") ? ContentType.MESSAGE_RFC822 : ContentType.TEXT_PLAIN;
            do {
                MimePart subpart = MimePart.parse(pstate, this, defaultContentType);
                mChildren.add(subpart);
                bterm = pstate.getBoundaryTerminator();
            } while (bterm != null && !bterm.mWasEndBoundary && mBoundary.equals(bterm.mBoundary));

            // skip the epilogue if appropriate
            if (bterm != null && bterm.mWasEndBoundary && mBoundary.equals(bterm.mBoundary)) {
                MimePart epilogue = new MimeBodyPart(new ContentType("text/plain"), this, pstate.getPosition(), pstate.getPosition(), null).readContent(pstate);
                bterm = pstate.getBoundaryTerminator();
                if (epilogue.getSize() > 0)
                    mEpilogue = epilogue;
            }
        }

        recordEndpoint(bterm == null ? pstate.getPosition() : bterm.mBoundaryStart);
        return this;
    }
}
