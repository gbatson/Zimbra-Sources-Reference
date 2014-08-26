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
 * <p>Java class for getDataSourcesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDataSourcesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="imap" type="{urn:zimbraMail}mailImapDataSource"/>
 *           &lt;element name="pop3" type="{urn:zimbraMail}mailPop3DataSource"/>
 *           &lt;element name="caldav" type="{urn:zimbraMail}mailCaldavDataSource"/>
 *           &lt;element name="yab" type="{urn:zimbraMail}mailYabDataSource"/>
 *           &lt;element name="rss" type="{urn:zimbraMail}mailRssDataSource"/>
 *           &lt;element name="gal" type="{urn:zimbraMail}mailGalDataSource"/>
 *           &lt;element name="cal" type="{urn:zimbraMail}mailCalDataSource"/>
 *           &lt;element name="unknown" type="{urn:zimbraMail}mailUnknownDataSource"/>
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
@XmlType(name = "getDataSourcesResponse", propOrder = {
    "imapOrPop3OrCaldav"
})
public class testGetDataSourcesResponse {

    @XmlElements({
        @XmlElement(name = "pop3", type = testMailPop3DataSource.class),
        @XmlElement(name = "rss", type = testMailRssDataSource.class),
        @XmlElement(name = "cal", type = testMailCalDataSource.class),
        @XmlElement(name = "gal", type = testMailGalDataSource.class),
        @XmlElement(name = "yab", type = testMailYabDataSource.class),
        @XmlElement(name = "imap", type = testMailImapDataSource.class),
        @XmlElement(name = "caldav", type = testMailCaldavDataSource.class),
        @XmlElement(name = "unknown", type = testMailUnknownDataSource.class)
    })
    protected List<testMailDataSource> imapOrPop3OrCaldav;

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
     * {@link testMailPop3DataSource }
     * {@link testMailRssDataSource }
     * {@link testMailCalDataSource }
     * {@link testMailGalDataSource }
     * {@link testMailYabDataSource }
     * {@link testMailImapDataSource }
     * {@link testMailCaldavDataSource }
     * {@link testMailUnknownDataSource }
     * 
     * 
     */
    public List<testMailDataSource> getImapOrPop3OrCaldav() {
        if (imapOrPop3OrCaldav == null) {
            imapOrPop3OrCaldav = new ArrayList<testMailDataSource>();
        }
        return this.imapOrPop3OrCaldav;
    }

}
