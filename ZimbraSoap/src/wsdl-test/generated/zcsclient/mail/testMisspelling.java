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
 * <p>Java class for misspelling complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="misspelling">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="word" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="suggestions" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "misspelling")
public class testMisspelling {

    @XmlAttribute(name = "word", required = true)
    protected String word;
    @XmlAttribute(name = "suggestions")
    protected String suggestions;

    /**
     * Gets the value of the word property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWord() {
        return word;
    }

    /**
     * Sets the value of the word property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWord(String value) {
        this.word = value;
    }

    /**
     * Gets the value of the suggestions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuggestions() {
        return suggestions;
    }

    /**
     * Sets the value of the suggestions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuggestions(String value) {
        this.suggestions = value;
    }

}
