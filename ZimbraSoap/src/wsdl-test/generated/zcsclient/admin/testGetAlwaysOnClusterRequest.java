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
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAttributeSelectorImpl;


/**
 * <p>Java class for getAlwaysOnClusterRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAlwaysOnClusterRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="alwaysOnCluster" type="{urn:zimbraAdmin}alwaysOnClusterSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAlwaysOnClusterRequest", propOrder = {
    "alwaysOnCluster"
})
public class testGetAlwaysOnClusterRequest
    extends testAttributeSelectorImpl
{

    protected testAlwaysOnClusterSelector alwaysOnCluster;

    /**
     * Gets the value of the alwaysOnCluster property.
     * 
     * @return
     *     possible object is
     *     {@link testAlwaysOnClusterSelector }
     *     
     */
    public testAlwaysOnClusterSelector getAlwaysOnCluster() {
        return alwaysOnCluster;
    }

    /**
     * Sets the value of the alwaysOnCluster property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAlwaysOnClusterSelector }
     *     
     */
    public void setAlwaysOnCluster(testAlwaysOnClusterSelector value) {
        this.alwaysOnCluster = value;
    }

}
