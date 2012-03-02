/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.forms.spi;

import org.jivesoftware.wildfire.forms.FormField;

import java.util.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;

/**
 * A concrete DataForm capable of sending itself to a writer and recover its state from an XMPP
 * stanza. XDataForms are packets of the form:
 * <code><pre>
 * &lt;x xmlns='jabber:x:data' type='{form-type}'&gt;
 * &lt;title/&gt;
 * &lt;instructions/&gt;
 * &lt;field var='field-name'
 *       type='{field-type}'
 *       label='description'&gt;
 *   &lt;desc/&gt;
 *   &lt;required/&gt;
 *   &lt;value&gt;field-value&lt;/value&gt;
 *   &lt;option label='option-label'&gt;&lt;value&gt;option-value&lt;/value&gt;&lt;/option&gt;
 *   &lt;option label='option-label'&gt;&lt;value&gt;option-value&lt;/value&gt;&lt;/option&gt;
 * &lt;/field&gt;
 * &lt;/x&gt;
 * </pre></code>
 * <p/>
 * An XDataFormImpl can contain zero or more XFormFieldImpl 'field' fragments.<p>
 * <p/>
 * To learn more follow this link: <a href="http://www.jabber.org/jeps/jep-0004.html">JEP-04</a>.
 *
 * @author gdombiak
 */
public class XDataFormImpl {

    private String type;
    private String title;
    private List instructions = new ArrayList();
    private List fields = new ArrayList();
    private List reportedFields = new ArrayList();
    private List reportedItems = new ArrayList();

    public XDataFormImpl() {
        super();
    }

    public XDataFormImpl(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInstructions(List instructions) {
        this.instructions = instructions;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Iterator getInstructions() {
        synchronized (instructions) {
            return Collections.unmodifiableList(new ArrayList(instructions)).iterator();
        }
    }

    public FormField getField(String variable) {
        if (variable == null || variable.equals("")) {
            throw new IllegalArgumentException("Variable must not be null or blank.");
        }
        // Look for the field whose variable matches the requested variable
        FormField field;
        for (Iterator it = getFields(); it.hasNext();) {
            field = (FormField)it.next();
            if (variable.equals(field.getVariable())) {
                return field;
            }
        }
        return null;
    }

    public Iterator getFields() {
        synchronized (fields) {
            return Collections.unmodifiableList(new ArrayList(fields)).iterator();
        }
    }

    public int getFieldsSize() {
        return fields.size();
    }

    public void addInstruction(String instruction) {
        synchronized (instructions) {
            instructions.add(instruction);
        }
    }

    public void addField(FormField field) {
        synchronized (fields) {
            fields.add(field);
        }
    }

    public void addReportedField(FormField field) {
        synchronized (reportedFields) {
            reportedFields.add(field);
        }
    }

    public void addItemFields(ArrayList itemFields) {
        synchronized (reportedItems) {
            // We are nesting a List (of fields) inside of the List of items
            reportedItems.add(itemFields);
        }
    }

    public String getNamespace() {
        // Is someone sending this message?
        return "jabber:x:data";
    }

    public void setNamespace(String namespace) {
        // Is someone sending this message?
        // Do nothing
    }

    public String getName() {
        // Is someone sending this message?
        return "x";
    }

    public void setName(String name) {
        // Is someone sending this message?
        // Do nothing
    }

    public Element asXMLElement() {
        Element x = DocumentHelper.createElement(QName.get("x", "jabber:x:data"));
        if (getType() != null) {
            x.addAttribute("type", getType());
        }
        if (getTitle() != null) {
            x.addElement("title").addText(getTitle());
        }
        if (instructions.size() > 0) {
            Iterator instrItr = getInstructions();
            while (instrItr.hasNext()) {
                x.addElement("instructions").addText((String)instrItr.next());
            }
        }
        // Append the list of fields returned from a search
        if (reportedFields.size() > 0) {
            Element reportedElement = x.addElement("reported");
            Iterator fieldsItr = reportedFields.iterator();
            while (fieldsItr.hasNext()) {
                XFormFieldImpl field = (XFormFieldImpl)fieldsItr.next();
                reportedElement.add(field.asXMLElement());
            }
        }

        // Append the list of items returned from a search
        // Note: each item contains a List of XFormFieldImpls
        if (reportedItems.size() > 0) {
            Iterator itemsItr = reportedItems.iterator();
            while (itemsItr.hasNext()) {
                // Add a new item element for this list of fields
                Element itemElement = x.addElement("item");
                List fields = (List)itemsItr.next();
                Iterator fieldsItr = fields.iterator();
                // Iterate on the fields and add them to the new item
                while (fieldsItr.hasNext()) {
                    XFormFieldImpl field = (XFormFieldImpl)fieldsItr.next();
                    itemElement.add(field.asXMLElement());
                }
            }
        }

        if (fields.size() > 0) {
            Iterator fieldsItr = getFields();
            while (fieldsItr.hasNext()) {
                XFormFieldImpl field = (XFormFieldImpl)fieldsItr.next();
                x.add(field.asXMLElement());
            }
        }

        return x;
    }

    public void parse(Element formElement) {
        type = formElement.attributeValue("type");
        Element titleElement = formElement.element("title");
        if (titleElement != null) {
            setTitle(titleElement.getTextTrim());
        }
        Iterator instructionElements = formElement.elementIterator("instructions");
        while (instructionElements.hasNext()) {
            addInstruction(((Element)instructionElements.next()).getTextTrim());
        }
        Iterator fieldElements = formElement.elementIterator("field");
        while (fieldElements.hasNext()) {
            XFormFieldImpl field = new XFormFieldImpl();
            field.parse((Element)fieldElements.next());
            addField(field);
        }

        Element reportedElement = formElement.element("reported");
        if (reportedElement != null) {
            Iterator reportedFieldElements = reportedElement.elementIterator("field");
            while (reportedFieldElements.hasNext()) {
                XFormFieldImpl field = new XFormFieldImpl();
                field.parse((Element)reportedFieldElements.next());
                addReportedField(field);
            }
        }

        Iterator itemElements = formElement.elementIterator("item");
        while (itemElements.hasNext()) {
            Element itemElement = (Element)itemElements.next();
            Iterator itemFieldElements = itemElement.elementIterator("field");
            ArrayList itemFields = new ArrayList();
            while (itemFieldElements.hasNext()) {
                XFormFieldImpl field = new XFormFieldImpl();
                field.parse((Element)itemFieldElements.next());
                itemFields.add(field);
            }
            addItemFields(itemFields);
        }
    }
}