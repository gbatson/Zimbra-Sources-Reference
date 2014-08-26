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

package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for importanceTest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="importanceTest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}filterTest">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="imp" use="required" type="{urn:zimbraMail}importance" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "importanceTest")
public class testImportanceTest
    extends testFilterTest
{

    @XmlAttribute(name = "imp", required = true)
    protected testImportance imp;

    /**
     * Gets the value of the imp property.
     * 
     * @return
     *     possible object is
     *     {@link testImportance }
     *     
     */
    public testImportance getImp() {
        return imp;
    }

    /**
     * Sets the value of the imp property.
     * 
     * @param value
     *     allowed object is
     *     {@link testImportance }
     *     
     */
    public void setImp(testImportance value) {
        this.imp = value;
    }

}
