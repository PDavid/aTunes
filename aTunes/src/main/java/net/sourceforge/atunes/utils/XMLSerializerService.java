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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;

/**
 * Reads and writes objects in XML
 * @author alex
 *
 */
public class XMLSerializerService {
	
	private XStreamFactory xstreamFactory;
	
	/**
	 * @param xstreamFactory
	 */
	public void setXstreamFactory(XStreamFactory xstreamFactory) {
		this.xstreamFactory = xstreamFactory;
	}
	
    /**
     * Reads an object from a file as xml.
     * 
     * @param filename
     *            filename
     * 
     * @return The object read from the xml file or null if can't be read
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public Object readObjectFromFile(String filename) throws IOException {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            return getXStream().fromXML(inputStreamReader);
        } catch (ConversionException e) {
        	return null;
        } finally {
            ClosingUtils.close(inputStreamReader);
        }
    }

    /**
     * Reads an object from a file
     * @param inputStream
     * @return
     * @throws IOException
     */
    public Object readObjectFromFile(InputStream inputStream) throws IOException {
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
    public Object readObjectFromString(String string) {
        return getXStream().fromXML(string);
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
    public void writeObjectToFile(Object object, String filename) throws IOException {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
            getXStream().toXML(object, outputStreamWriter);
        } finally {
            ClosingUtils.close(outputStreamWriter);
        }
    }

    /**
     * @return
     */
    private XStream getXStream() {
    	try {
			return xstreamFactory.getXStream();
		} catch (ClassNotFoundException e) {
			Logger.error(e);
			return null;
		}
    }
}
