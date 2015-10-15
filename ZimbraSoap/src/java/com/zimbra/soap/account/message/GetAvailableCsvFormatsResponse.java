/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013, 2014 Zimbra, Inc.
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

package com.zimbra.soap.account.message;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.soap.type.NamedElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=AccountConstants.E_GET_AVAILABLE_CSV_FORMATS_RESPONSE)
public class GetAvailableCsvFormatsResponse {

    /**
     * @zm-api-field-description The known CSV formats that can be used for import and export of addressbook.
     */
    @XmlElement(name=AccountConstants.E_CSV, required=false)
    private List<NamedElement> csvFormats = Lists.newArrayList();

    public GetAvailableCsvFormatsResponse() {
    }

    public void setCsvFormats(Iterable <NamedElement> csvFormats) {
        this.csvFormats.clear();
        if (csvFormats != null) {
            Iterables.addAll(this.csvFormats,csvFormats);
        }
    }

    public GetAvailableCsvFormatsResponse addCsvFormat(NamedElement csvFormat) {
        this.csvFormats.add(csvFormat);
        return this;
    }

    public List<NamedElement> getCsvFormats() {
        return Collections.unmodifiableList(csvFormats);
    }
}