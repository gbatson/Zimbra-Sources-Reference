/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire;

/**
 * <p>A simple meta-data class that stores several related tools for
 * generic IQ protocol handling.</p>
 * <p/>
 * <p>To handle an IQ packet, the server needs to know:</p>
 * <ul>
 * <li>The fully qualified name of the iq sub-element. IQ packets are
 * identified using this information when matching to a handler.</li>
 * <li>The IQHandler that will handle this packet if addressed to the
 * server (no 'to' attribute).</li>
 * <li>The IQ parser to use to generate the correct IQ packet.</li>
 * </ul>
 * <p/>
 * <p>We provide this information by having all IQHandlers report
 * their info. Interested parties can watch for IQHandlers in the service
 * lookup and build appropriate data structures on the current state of
 * IQ handlers in the system.</p>
 *
 * @author Iain Shigeoka
 */
public class IQHandlerInfo {

    private String name;
    private String namespace;

    /**
     * <p>Construct an info object.</p>
     *
     * @param name      The name of the root iq element
     * @param namespace The namespace of the root iq element
     */
    public IQHandlerInfo(String name, String namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    /**
     * <p>Obtain the name of the root iq element for this packet type.</p>
     *
     * @return The name of the root iq element
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Obtain the namespace of the root iq element for this packet type.</p>
     *
     * @return the namespace of the root iq element.
     */
    public String getNamespace() {
        return namespace;
    }
}
