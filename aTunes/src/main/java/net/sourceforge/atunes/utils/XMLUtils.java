/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sourceforge.atunes.kernel.modules.playlist.PlayList;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.favorites.Favorites;
import net.sourceforge.atunes.kernel.modules.statistics.AudioFileStats;
import net.sourceforge.atunes.kernel.modules.statistics.Statistics;
import net.sourceforge.atunes.kernel.modules.statistics.StatisticsAlbum;
import net.sourceforge.atunes.kernel.modules.tags.DefaultTag;
import net.sourceforge.atunes.misc.PointedList;
import net.sourceforge.atunes.misc.RankList;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.model.RepositoryStructure;

import org.commonjukebox.plugins.model.PluginApi;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.thoughtworks.xstream.XStream;

/**
 * Utility methods for XML.
 */
@PluginApi
public final class XMLUtils {

    private XMLUtils() {
    }

    private static ThreadLocal<XPath> xPath;

    /**
     * Evaluates a XPath expression from a XML node, returning a Node object.
     * 
     * @param expression
     *            A XPath expression
     * @param node
     *            The Node for which this expression should be evaluated
     * 
     * @return The result of evaluating the XPath expression to the given Node
     *         or <code>null</code> if an ecxception occured
     */
    public static Node evaluateXPathExpressionAndReturnNode(String expression, Node node) {
        try {
            return (Node) getXPath().get().evaluate(expression, node, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Evaluates a XPath expression from a XML node, returning a String object.
     * 
     * @param expression
     *            A XPath expression
     * @param node
     *            The Node for which this expression should be evaluated
     * 
     * @return The result of evaluating the XPath expression to the given Node
     *         or <code>null</code> if an ecxception occured
     */
    public static String evaluateXPathExpressionAndReturnString(String expression, Node node) {
        try {
            return (String) getXPath().get().evaluate(expression, node, XPathConstants.STRING);
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
            try {
                DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                return parser.parse(new InputSource(new StringReader(xml)));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Reads an object from an XML file.
     * 
     * @param filename
     *            the filename
     * 
     * @return the object
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static Object readBeanFromFile(String filename) throws IOException {
        XMLDecoder decoder = null;
        try {
            decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
            return decoder.readObject();
        } finally {
            ClosingUtils.close(decoder);
        }
    }

    /**
     * Reads an object from a file as xml.
     * 
     * @param filename
     *            filename
     * 
     * @return The object read from the xml file
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static Object readObjectFromFile(String filename) throws IOException {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            return getXStream().fromXML(inputStreamReader);
        } finally {
            ClosingUtils.close(inputStreamReader);
        }
    }

    public static Object readObjectFromFile(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            return getXStream().fromXML(inputStreamReader);
        } finally {
            ClosingUtils.close(inputStreamReader);
        }
    }

    /**
     * Reads an object from a String as xml.
     * 
     * @param string
     *            the string
     * 
     * @return The object read from the xml string
     */
    public static Object readObjectFromString(String string) {
        return getXStream().fromXML(string);
    }

    /**
     * Writes an object to an XML file.
     * 
     * @param bean
     *            the bean
     * @param filename
     *            the filename
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeBeanToFile(Object bean, String filename) throws IOException {
        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
            encoder.writeObject(bean);
        } finally {
            ClosingUtils.close(encoder);
        }
    }

    /**
     * Writes an object to a file as xml.
     * 
     * @param object
     *            Object that should be writen to a xml file
     * @param filename
     *            filename
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeObjectToFile(Object object, String filename) throws IOException {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
            getXStream().toXML(object, outputStreamWriter);
        } finally {
            ClosingUtils.close(outputStreamWriter);
        }
    }

    private static XStream getXStream() {
        XStream xStream = new XStream();

        xStream.alias("Repository", Repository.class);
        xStream.alias("RepositoryStats", Statistics.class);
        xStream.alias("RepositoryStructure", RepositoryStructure.class);
        xStream.alias("SongStats", AudioFileStats.class);
        xStream.alias("RankList", RankList.class);
        xStream.alias("StatisticsAlbum", StatisticsAlbum.class);
        xStream.alias("AudioFile", AudioFile.class);
        xStream.alias("Radio", Radio.class);
        xStream.alias("PodcastFeed", PodcastFeed.class);
        xStream.alias("PodcastFeedEntry", PodcastFeedEntry.class);
        xStream.alias("Artist", Artist.class);
        xStream.alias("Album", Album.class);
        xStream.alias("Folder", Folder.class);
        xStream.alias("Genre", Genre.class);
        xStream.alias("DefaultTag", DefaultTag.class);
        xStream.alias("PlayList", PlayList.class);
        xStream.alias("Favorites", Favorites.class);

        xStream.omitField(Radio.class, "title");
        xStream.omitField(Radio.class, "artist");
        xStream.omitField(Radio.class, "songInfoAvailable");

        xStream.omitField(PointedList.class, "list");

        return xStream;
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
