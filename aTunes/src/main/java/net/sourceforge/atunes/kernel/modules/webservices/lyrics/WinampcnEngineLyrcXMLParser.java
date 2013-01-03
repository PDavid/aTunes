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

package net.sourceforge.atunes.kernel.modules.webservices.lyrics;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.atunes.utils.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class WinampcnEngineLyrcXMLParser extends DefaultHandler {
	
    private List<WinampcnEngineLyrcCandidate> lyrcs = new ArrayList<WinampcnEngineLyrcCandidate>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("LyricUrl")) {
            WinampcnEngineLyrcCandidate lyrc = new WinampcnEngineLyrcCandidate();
            lyrc.setId(Integer.parseInt(attributes.getValue("id")));
            lyrc.setSongName(attributes.getValue("SongName"));
            lyrc.setDownloadCount(Integer.parseInt(attributes.getValue("downloadtimes")));
            lyrcs.add(lyrc);
        }
    }

    /**
     * Parses xml from Winampcn
     * @param xml
     * @return
     */
    public List<WinampcnEngineLyrcCandidate> parse(String xml) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new ByteArrayInputStream(xml.getBytes("gbk")), this);
        } catch (Exception e) {
            Logger.error("Cannot parse lyrics list from winampcn: ", e.getMessage());
        }
        return this.lyrcs;
    }
}