/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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

package com.zimbra.soap.admin.message;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.admin.type.DomainInfo;

@XmlRootElement(name=AdminConstants.E_GET_ALL_DOMAINS_RESPONSE)
public class GetAllDomainsResponse {

    /**
     * @zm-api-field-description Information on domains
     */
    @XmlElement(name=AdminConstants.E_DOMAIN)
    private List <DomainInfo> domainList = new ArrayList<DomainInfo>();

    public GetAllDomainsResponse() {
    }

    public void addDomain(DomainInfo domain ) {
        this.getDomainList().add(domain);
    }

    public List <DomainInfo> getDomainList() { return domainList; }
}
