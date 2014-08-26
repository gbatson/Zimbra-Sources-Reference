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
 * <p>Java class for indexStats complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indexStats">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *       &lt;/all>
 *       &lt;attribute name="maxDocs" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="deletedDocs" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "indexStats", propOrder = {

})
public class testIndexStats {

    @XmlAttribute(name = "maxDocs", required = true)
    protected int maxDocs;
    @XmlAttribute(name = "deletedDocs", required = true)
    protected int deletedDocs;

    /**
     * Gets the value of the maxDocs property.
     * 
     */
    public int getMaxDocs() {
        return maxDocs;
    }

    /**
     * Sets the value of the maxDocs property.
     * 
     */
    public void setMaxDocs(int value) {
        this.maxDocs = value;
    }

    /**
     * Gets the value of the deletedDocs property.
     * 
     */
    public int getDeletedDocs() {
        return deletedDocs;
    }

    /**
     * Sets the value of the deletedDocs property.
     * 
     */
    public void setDeletedDocs(int value) {
        this.deletedDocs = value;
    }

}
