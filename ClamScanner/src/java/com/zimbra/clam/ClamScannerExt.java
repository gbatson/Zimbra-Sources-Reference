/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2011, 2013, 2014 Zimbra Software, LLC.
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

package com.zimbra.clam;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.service.mail.UploadScanner;

public class ClamScannerExt implements ZimbraExtension {

    private static final Log LOG = ZimbraLog.extensions;
   
    public ClamScannerExt() {
    }

    @Override
    public synchronized void init() {
        
        try {
            mConfig = new ClamScannerConfig();
           
            if (!mConfig.getEnabled()) {
                LOG.info("attachment scan is disabled");
                return;
            }
            
            String[] urls = mConfig.getURL();
            for (int i = 0; i < urls.length; i++) {
                ClamScanner clamScanner = new ClamScanner();
                String url = urls[i];
                clamScanner.setURL(url);
                UploadScanner.registerScanner(clamScanner);
                clamScannerList.add(clamScanner);
            }
        } catch (ServiceException e) {
            LOG.error("error creating scanner", e);
        } catch (MalformedURLException e) {
            LOG.error("error creating scanner", e);
        }
    }

    @Override
    public void destroy() {
        for (Iterator iter = clamScannerList.iterator(); iter.hasNext();) {
            ClamScanner clamScanner = (ClamScanner)iter.next();
            UploadScanner.unregisterScanner(clamScanner);
        }
    }

    private ClamScannerConfig mConfig;
    private List<ClamScanner> clamScannerList = new LinkedList<ClamScanner>();  

    @Override
    public String getName() {
        return "clamscanner";
    }
}
