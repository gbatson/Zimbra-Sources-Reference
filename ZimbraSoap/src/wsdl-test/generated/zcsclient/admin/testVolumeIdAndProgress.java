/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2014 Zimbra, Inc.
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
 * <p>Java class for volumeIdAndProgress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="volumeIdAndProgress">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="volumeId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="progress" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "volumeIdAndProgress")
public class testVolumeIdAndProgress {

    @XmlAttribute(name = "volumeId", required = true)
    protected String volumeId;
    @XmlAttribute(name = "progress", required = true)
    protected String progress;

    /**
     * Gets the value of the volumeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVolumeId() {
        return volumeId;
    }

    /**
     * Sets the value of the volumeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVolumeId(String value) {
        this.volumeId = value;
    }

    /**
     * Gets the value of the progress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgress() {
        return progress;
    }

    /**
     * Sets the value of the progress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgress(String value) {
        this.progress = value;
    }

}
