/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.utils;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility methods for XML.
 */

public final class XMLUtils {

    private XMLUtils() {
    }

    /**
     * Returns the value of an attribute of a given XML element.
     * 
     * @param element
     *            A element
     * @param attributeName
     *            The name of the attribute
     * 
     * @return The value of the attribute or <code>null</code> if no such
     *         attribute exists
     */
    public static String getAttributeValue(Element element, String attributeName) {
        return (null == element ? null : element.getAttribute(attributeName));
    }

    /**
     * Returns a child element with a given name from an XML element.
     * 
     * @param element
     *            A Element
     * @param tagName
     *            The name of the child element
     * 
     * @return The child element or <code>null</code> if no such child exists
     */
    public static Element getChildElement(Element element, String tagName) {
        if (element == null) {
            return null;
        }
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            return (Element) list.item(0);
        }
        return null;
    }

    /**
     * Returns value of a child element from a given XML element.
     * 
     * @param element
     *            A Elemnt
     * @param tagName
     *            The name of the child elment
     * 
     * @return The value of the child element
     */
    public static String getChildElementContent(Element element, String tagName) {
        Element el2 = getChildElement(element, tagName);
        return el2 == null ? "" : el2.getTextContent();
    }

    /**
     * Returns a XML Document object from an XML String.
     * 
     * @param xml
     *            The String that should be parsed to an XML document
     * 
     * @return The parsed XML document or <code>null</code> if the String
     *         couldn't be parsed
     */
    public static Document getXMLDocument(String xml) {
        if ((null != xml) && (!xml.isEmpty())) {
        	DocumentBuilder parser;
        	try {
        		parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        		return parser.parse(new InputSource(new StringReader(xml)));
        	} catch (ParserConfigurationException e) {
        		Logger.error(e);
        		return null;
        	} catch (SAXException e) {
        		Logger.error(e);
        		return null;
        	} catch (IOException e) {
        		Logger.error(e);
        		return null;
			}
        }
        return null;
    }
}
