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

package generated.zcsclient.zm;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.zcsclient.zm package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Context_QNAME = new QName("urn:zimbra", "context");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.zcsclient.zm
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link testOpValue }
     * 
     */
    public testOpValue createtestOpValue() {
        return new testOpValue();
    }

    /**
     * Create an instance of {@link testUrlAndValue }
     * 
     */
    public testUrlAndValue createtestUrlAndValue() {
        return new testUrlAndValue();
    }

    /**
     * Create an instance of {@link testShareInfo }
     * 
     */
    public testShareInfo createtestShareInfo() {
        return new testShareInfo();
    }

    /**
     * Create an instance of {@link testKeyValuePair }
     * 
     */
    public testKeyValuePair createtestKeyValuePair() {
        return new testKeyValuePair();
    }

    /**
     * Create an instance of {@link testSectionAttr }
     * 
     */
    public testSectionAttr createtestSectionAttr() {
        return new testSectionAttr();
    }

    /**
     * Create an instance of {@link testId }
     * 
     */
    public testId createtestId() {
        return new testId();
    }

    /**
     * Create an instance of {@link testAttributeName }
     * 
     */
    public testAttributeName createtestAttributeName() {
        return new testAttributeName();
    }

    /**
     * Create an instance of {@link testNamedElement }
     * 
     */
    public testNamedElement createtestNamedElement() {
        return new testNamedElement();
    }

    /**
     * Create an instance of {@link testDataSources }
     * 
     */
    public testDataSources createtestDataSources() {
        return new testDataSources();
    }

    /**
     * Create an instance of {@link testHeaderContext }
     * 
     */
    public testHeaderContext createtestHeaderContext() {
        return new testHeaderContext();
    }

    /**
     * Create an instance of {@link testWildcardExpansionQueryInfo }
     * 
     */
    public testWildcardExpansionQueryInfo createtestWildcardExpansionQueryInfo() {
        return new testWildcardExpansionQueryInfo();
    }

    /**
     * Create an instance of {@link testGranteeChooser }
     * 
     */
    public testGranteeChooser createtestGranteeChooser() {
        return new testGranteeChooser();
    }

    /**
     * Create an instance of {@link testTzOnsetInfo }
     * 
     */
    public testTzOnsetInfo createtestTzOnsetInfo() {
        return new testTzOnsetInfo();
    }

    /**
     * Create an instance of {@link testContactAttr }
     * 
     */
    public testContactAttr createtestContactAttr() {
        return new testContactAttr();
    }

    /**
     * Create an instance of {@link testCursorInfo }
     * 
     */
    public testCursorInfo createtestCursorInfo() {
        return new testCursorInfo();
    }

    /**
     * Create an instance of {@link testWaitSetAddSpec }
     * 
     */
    public testWaitSetAddSpec createtestWaitSetAddSpec() {
        return new testWaitSetAddSpec();
    }

    /**
     * Create an instance of {@link testIdAndType }
     * 
     */
    public testIdAndType createtestIdAndType() {
        return new testIdAndType();
    }

    /**
     * Create an instance of {@link testAccountSelector }
     * 
     */
    public testAccountSelector createtestAccountSelector() {
        return new testAccountSelector();
    }

    /**
     * Create an instance of {@link testSimpleSearchHit }
     * 
     */
    public testSimpleSearchHit createtestSimpleSearchHit() {
        return new testSimpleSearchHit();
    }

    /**
     * Create an instance of {@link testDistributionListSelector }
     * 
     */
    public testDistributionListSelector createtestDistributionListSelector() {
        return new testDistributionListSelector();
    }

    /**
     * Create an instance of {@link testNamedValue }
     * 
     */
    public testNamedValue createtestNamedValue() {
        return new testNamedValue();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testHeaderContext }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbra", name = "context")
    public JAXBElement<testHeaderContext> createContext(testHeaderContext value) {
        return new JAXBElement<testHeaderContext>(_Context_QNAME, testHeaderContext.class, null, value);
    }

}
