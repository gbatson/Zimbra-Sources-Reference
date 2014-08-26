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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for voiceMailPrefsFeature complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voiceMailPrefsFeature">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraVoice}callFeatureInfo">
 *       &lt;sequence>
 *         &lt;element name="pref" type="{urn:zimbraVoice}prefInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="unusedCodeGenHelper" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voiceMailPrefsFeature", propOrder = {
    "pref"
})
public class testVoiceMailPrefsFeature
    extends testCallFeatureInfo
{

    protected List<testPrefInfo> pref;
    @XmlAttribute(name = "unusedCodeGenHelper")
    protected String unusedCodeGenHelper;

    /**
     * Gets the value of the pref property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pref property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPref().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testPrefInfo }
     * 
     * 
     */
    public List<testPrefInfo> getPref() {
        if (pref == null) {
            pref = new ArrayList<testPrefInfo>();
        }
        return this.pref;
    }

    /**
     * Gets the value of the unusedCodeGenHelper property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnusedCodeGenHelper() {
        return unusedCodeGenHelper;
    }

    /**
     * Sets the value of the unusedCodeGenHelper property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnusedCodeGenHelper(String value) {
        this.unusedCodeGenHelper = value;
    }

}
