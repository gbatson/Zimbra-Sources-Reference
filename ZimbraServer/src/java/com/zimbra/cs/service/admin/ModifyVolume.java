/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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

package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.store.file.Volume;
import com.zimbra.soap.ZimbraSoapContext;

public class ModifyVolume extends AdminDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        
        Server localServer = Provisioning.getInstance().getLocalServer();
        checkRight(lc, context, localServer, Admin.R_manageVolume);

        long idLong = request.getAttributeLong(AdminConstants.A_ID);
        Volume.validateID(idLong);  // avoid Java truncation
        short id = (short) idLong;
        Volume vol = Volume.getById(id);

        Element eVol = request.getElement(AdminConstants.E_VOLUME);
        String name  = eVol.getAttribute(AdminConstants.A_VOLUME_NAME, vol.getName());
        String path  = eVol.getAttribute(AdminConstants.A_VOLUME_ROOTPATH, vol.getRootPath());
        short type   = (short) eVol.getAttributeLong(AdminConstants.A_VOLUME_TYPE, vol.getType());

        // TODO: These "bits" parameters are ignored inside Volume.update() for now.
//        short mgbits = (short) eVol.getAttributeLong(AdminService.A_VOLUME_MGBITS, vol.getMboxGroupBits());
//        short mbits  = (short) eVol.getAttributeLong(AdminService.A_VOLUME_MBITS,  vol.getMboxBits());
//        short fgbits = (short) eVol.getAttributeLong(AdminService.A_VOLUME_FGBITS, vol.getFileGroupBits());
//        short fbits  = (short) eVol.getAttributeLong(AdminService.A_VOLUME_FBITS,  vol.getFileBits());

        boolean compressBlobs = eVol.getAttributeBool(AdminConstants.A_VOLUME_COMPRESS_BLOBS, vol.getCompressBlobs());
        long compressionThreshold = eVol.getAttributeLong(AdminConstants.A_VOLUME_COMPRESSION_THRESHOLD,
                                                          vol.getCompressionThreshold());
//        Volume.update(vol.getId(), type, name, path, mgbits, mbits, fgbits, fbits,
//                      compressBlobs, compressionThreshold);
        Volume.update(vol.getId(), type, name, path, (short) 0, (short) 0, (short) 0, (short) 0,
                      compressBlobs, compressionThreshold);

        Element response = lc.createElement(AdminConstants.MODIFY_VOLUME_RESPONSE);
        return response;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_manageVolume);
    }
}
