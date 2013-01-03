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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility methods for XPath
 * @author alex
 *
 */
public final class XPathUtils {
	
	private XPathUtils() {}
	
    private static volatile ThreadLocal<XPath> xPath;

    /**
     * Evaluates a XPath expression from a XML node, returning a Node object.
     * 
     * @param expression
     *            A XPath expression
     * @param node
     *            The Node for which this expression should be evaluated
     * 
     * @return The result of evaluating the XPath expression to the given Node
     *         or <code>null</code> if an exception occured
     */
    public static Node evaluateXPathExpressionAndReturnNode(String expression, Node node) {
        try {
            return (Node) getXPath().get().evaluate(expression, node, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Evaluates a XPath expression from a XML node, returning a NodeList.
     * 
     * @param expression
     *            A XPath expression
     * @param node
     *            The NodeList for which this expression should be evaluated
     * 
     * @return The result of evaluating the XPath expression to the given or
     *         <code>null</code> if an ecxception occured NodeList
     */
    public static NodeList evaluateXPathExpressionAndReturnNodeList(String expression, Node node) {
        try {
            return (NodeList) getXPath().get().evaluate(expression, node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            return null;
        }
    }
    
    private static ThreadLocal<XPath> getXPath() {
        if (xPath == null) {
            xPath = new ThreadLocal<XPath>() {
                @Override
                protected XPath initialValue() {
                    return XPathFactory.newInstance().newXPath();
                }
            };
        }
        return xPath;
    }
}
