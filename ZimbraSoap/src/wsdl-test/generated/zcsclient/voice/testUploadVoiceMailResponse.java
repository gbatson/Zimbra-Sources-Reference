/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013, 2014 Zimbra, Inc.
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

package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uploadVoiceMailResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uploadVoiceMailResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="upload" type="{urn:zimbraVoice}voiceMsgUploadInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadVoiceMailResponse", propOrder = {
    "upload"
})
public class testUploadVoiceMailResponse {

    protected testVoiceMsgUploadInfo upload;

    /**
     * Gets the value of the upload property.
     * 
     * @return
     *     possible object is
     *     {@link testVoiceMsgUploadInfo }
     *     
     */
    public testVoiceMsgUploadInfo getUpload() {
        return upload;
    }

    /**
     * Sets the value of the upload property.
     * 
     * @param value
     *     allowed object is
     *     {@link testVoiceMsgUploadInfo }
     *     
     */
    public void setUpload(testVoiceMsgUploadInfo value) {
        this.upload = value;
    }

}
