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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getImportStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getImportStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="imap" type="{urn:zimbraMail}imapImportStatusInfo"/>
 *           &lt;element name="pop3" type="{urn:zimbraMail}pop3ImportStatusInfo"/>
 *           &lt;element name="caldav" type="{urn:zimbraMail}caldavImportStatusInfo"/>
 *           &lt;element name="yab" type="{urn:zimbraMail}yabImportStatusInfo"/>
 *           &lt;element name="rss" type="{urn:zimbraMail}rssImportStatusInfo"/>
 *           &lt;element name="gal" type="{urn:zimbraMail}galImportStatusInfo"/>
 *           &lt;element name="cal" type="{urn:zimbraMail}calImportStatusInfo"/>
 *           &lt;element name="unknown" type="{urn:zimbraMail}unknownImportStatusInfo"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getImportStatusResponse", propOrder = {
    "imapOrPop3OrCaldav"
})
public class testGetImportStatusResponse {

    @XmlElements({
        @XmlElement(name = "cal", type = testCalImportStatusInfo.class),
        @XmlElement(name = "unknown", type = testUnknownImportStatusInfo.class),
        @XmlElement(name = "caldav", type = testCaldavImportStatusInfo.class),
        @XmlElement(name = "rss", type = testRssImportStatusInfo.class),
        @XmlElement(name = "yab", type = testYabImportStatusInfo.class),
        @XmlElement(name = "pop3", type = testPop3ImportStatusInfo.class),
        @XmlElement(name = "gal", type = testGalImportStatusInfo.class),
        @XmlElement(name = "imap", type = testImapImportStatusInfo.class)
    })
    protected List<testImportStatusInfo> imapOrPop3OrCaldav;

    /**
     * Gets the value of the imapOrPop3OrCaldav property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imapOrPop3OrCaldav property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImapOrPop3OrCaldav().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testCalImportStatusInfo }
     * {@link testUnknownImportStatusInfo }
     * {@link testCaldavImportStatusInfo }
     * {@link testRssImportStatusInfo }
     * {@link testYabImportStatusInfo }
     * {@link testPop3ImportStatusInfo }
     * {@link testGalImportStatusInfo }
     * {@link testImapImportStatusInfo }
     * 
     * 
     */
    public List<testImportStatusInfo> getImapOrPop3OrCaldav() {
        if (imapOrPop3OrCaldav == null) {
            imapOrPop3OrCaldav = new ArrayList<testImportStatusInfo>();
        }
        return this.imapOrPop3OrCaldav;
    }

}
