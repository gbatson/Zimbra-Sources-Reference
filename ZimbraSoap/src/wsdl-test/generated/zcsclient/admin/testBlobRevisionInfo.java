/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013, 2014 Zimbra, Inc.
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

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for blobRevisionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="blobRevisionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fileSize" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="rev" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="external" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "blobRevisionInfo")
public class testBlobRevisionInfo {

    @XmlAttribute(name = "path", required = true)
    protected String path;
    @XmlAttribute(name = "fileSize", required = true)
    protected long fileSize;
    @XmlAttribute(name = "rev", required = true)
    protected int rev;
    @XmlAttribute(name = "external", required = true)
    protected boolean external;

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the fileSize property.
     * 
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the value of the fileSize property.
     * 
     */
    public void setFileSize(long value) {
        this.fileSize = value;
    }

    /**
     * Gets the value of the rev property.
     * 
     */
    public int getRev() {
        return rev;
    }

    /**
     * Sets the value of the rev property.
     * 
     */
    public void setRev(int value) {
        this.rev = value;
    }

    /**
     * Gets the value of the external property.
     * 
     */
    public boolean isExternal() {
        return external;
    }

    /**
     * Sets the value of the external property.
     * 
     */
    public void setExternal(boolean value) {
        this.external = value;
    }

}