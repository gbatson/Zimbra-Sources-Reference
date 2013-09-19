/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.offline.util;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;

public class ExtractData {

    public static String getAttributefromChildren(Element elt, String key, String defaultValue) throws ServiceException {
        for (Element eField : elt.listElements()) {
            String result;
            if ((result = (String) eField.getAttribute(key, defaultValue)) != null)
                return result;
            for (Element subChild : eField.listElements()) {
                if (!subChild.getName().equals(MailConstants.E_METADATA)) {
                    if (subChild.getAttribute(Element.XMLElement.A_ATTR_NAME).equals(key))
                        return subChild.getText();
                }
            }
        }
        return defaultValue;
    }
}
