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
 * <p>Java class for reindexProgressInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reindexProgressInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="numSucceeded" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numFailed" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numRemaining" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reindexProgressInfo")
public class testReindexProgressInfo {

    @XmlAttribute(name = "numSucceeded", required = true)
    protected int numSucceeded;
    @XmlAttribute(name = "numFailed", required = true)
    protected int numFailed;
    @XmlAttribute(name = "numRemaining", required = true)
    protected int numRemaining;

    /**
     * Gets the value of the numSucceeded property.
     * 
     */
    public int getNumSucceeded() {
        return numSucceeded;
    }

    /**
     * Sets the value of the numSucceeded property.
     * 
     */
    public void setNumSucceeded(int value) {
        this.numSucceeded = value;
    }

    /**
     * Gets the value of the numFailed property.
     * 
     */
    public int getNumFailed() {
        return numFailed;
    }

    /**
     * Sets the value of the numFailed property.
     * 
     */
    public void setNumFailed(int value) {
        this.numFailed = value;
    }

    /**
     * Gets the value of the numRemaining property.
     * 
     */
    public int getNumRemaining() {
        return numRemaining;
    }

    /**
     * Sets the value of the numRemaining property.
     * 
     */
    public void setNumRemaining(int value) {
        this.numRemaining = value;
    }

}
