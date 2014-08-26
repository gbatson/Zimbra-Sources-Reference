/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013, 2014 Zimbra, Inc.
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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for syncDeletedInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="syncDeletedInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="folder" type="{urn:zimbraMail}folderIdsAttr"/>
 *           &lt;element name="search" type="{urn:zimbraMail}searchFolderIdsAttr"/>
 *           &lt;element name="link" type="{urn:zimbraMail}mountIdsAttr"/>
 *           &lt;element name="tag" type="{urn:zimbraMail}tagIdsAttr"/>
 *           &lt;element name="c" type="{urn:zimbraMail}convIdsAttr"/>
 *           &lt;element name="chat" type="{urn:zimbraMail}chatIdsAttr"/>
 *           &lt;element name="m" type="{urn:zimbraMail}msgIdsAttr"/>
 *           &lt;element name="cn" type="{urn:zimbraMail}contactIdsAttr"/>
 *           &lt;element name="appt" type="{urn:zimbraMail}apptIdsAttr"/>
 *           &lt;element name="task" type="{urn:zimbraMail}taskIdsAttr"/>
 *           &lt;element name="notes" type="{urn:zimbraMail}noteIdsAttr"/>
 *           &lt;element name="w" type="{urn:zimbraMail}wikiIdsAttr"/>
 *           &lt;element name="doc" type="{urn:zimbraMail}docIdsAttr"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="ids" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncDeletedInfo", propOrder = {
    "folderOrSearchOrLink"
})
public class testSyncDeletedInfo {

    @XmlElements({
        @XmlElement(name = "appt", type = testApptIdsAttr.class),
        @XmlElement(name = "cn", type = testContactIdsAttr.class),
        @XmlElement(name = "m", type = testMsgIdsAttr.class),
        @XmlElement(name = "notes", type = testNoteIdsAttr.class),
        @XmlElement(name = "w", type = testWikiIdsAttr.class),
        @XmlElement(name = "search", type = testSearchFolderIdsAttr.class),
        @XmlElement(name = "c", type = testConvIdsAttr.class),
        @XmlElement(name = "chat", type = testChatIdsAttr.class),
        @XmlElement(name = "folder", type = testFolderIdsAttr.class),
        @XmlElement(name = "task", type = testTaskIdsAttr.class),
        @XmlElement(name = "tag", type = testTagIdsAttr.class),
        @XmlElement(name = "link", type = testMountIdsAttr.class),
        @XmlElement(name = "doc", type = testDocIdsAttr.class)
    })
    protected List<testIdsAttr> folderOrSearchOrLink;
    @XmlAttribute(name = "ids", required = true)
    protected String ids;

    /**
     * Gets the value of the folderOrSearchOrLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folderOrSearchOrLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolderOrSearchOrLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testApptIdsAttr }
     * {@link testContactIdsAttr }
     * {@link testMsgIdsAttr }
     * {@link testNoteIdsAttr }
     * {@link testWikiIdsAttr }
     * {@link testSearchFolderIdsAttr }
     * {@link testConvIdsAttr }
     * {@link testChatIdsAttr }
     * {@link testFolderIdsAttr }
     * {@link testTaskIdsAttr }
     * {@link testTagIdsAttr }
     * {@link testMountIdsAttr }
     * {@link testDocIdsAttr }
     * 
     * 
     */
    public List<testIdsAttr> getFolderOrSearchOrLink() {
        if (folderOrSearchOrLink == null) {
            folderOrSearchOrLink = new ArrayList<testIdsAttr>();
        }
        return this.folderOrSearchOrLink;
    }

    /**
     * Gets the value of the ids property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIds() {
        return ids;
    }

    /**
     * Sets the value of the ids property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIds(String value) {
        this.ids = value;
    }

}
