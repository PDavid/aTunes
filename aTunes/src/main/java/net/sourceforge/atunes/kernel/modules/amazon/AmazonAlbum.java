/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.amazon;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class represents data about an album retrieved from Amazon Web Services
 * It's responsible of parsing a xml document returned by Amazon containing data
 * And example of xml document returned can be viewed in a web browser with the
 * following url:
 * 
 * http://ecs.amazonaws.com/onca/xml?Service=AWSECommerceService&Operation=
 * ItemSearch
 * &SubscriptionId=06SX9FYP905XDBHBCZR2&SearchIndex=Music&Artist=Metallica
 * &Title=Load&ResponseGroup=Images,Tracks
 */
public class AmazonAlbum {

    /** Artist. */
    private String artist;

    /** Album name. */
    private String album;

    /** Url of cover. */
    private String imageURL;

    /** Discs. */
    private List<AmazonDisc> discs;

    /**
     * Default constructor.
     */
    protected AmazonAlbum() {
        discs = new ArrayList<AmazonDisc>();
    }

    /**
     * Parses xml document and gets data into a new AmazonAlbum object.
     * 
     * @param xml
     *            the xml
     * 
     * @return the album
     */
    protected static AmazonAlbum getAlbum(Document xml) {
        AmazonAlbum album = new AmazonAlbum();

        Element root = (Element) xml.getElementsByTagName("ItemSearchResponse").item(0);
        Element operationRequest = (Element) root.getElementsByTagName("OperationRequest").item(0);
        Element argument = (Element) operationRequest.getElementsByTagName("Arguments").item(0);
        NodeList arguments = argument.getChildNodes();

        for (int i = 0; i < arguments.getLength(); i++) {
            Element arg = (Element) arguments.item(i);
            if (arg.getAttribute("Name").equals("Artist")) { // Artist
                album.artist = arg.getAttribute("Value");
            }
            if (arg.getAttribute("Name").equals("Title")) { // Album
                album.album = arg.getAttribute("Value");
            }
        }

        Element items = (Element) root.getElementsByTagName("Items").item(0);
        Element item = (Element) items.getElementsByTagName("Item").item(0);
        if (item == null) {
            return album;
        }
        Element mediumImage = (Element) item.getElementsByTagName("MediumImage").item(0);
        album.imageURL = XMLUtils.getChildElementContent(mediumImage, "URL");

        Element tracks = (Element) item.getElementsByTagName("Tracks").item(0);
        if (tracks == null) {
            return album;
        }
        NodeList discs = tracks.getChildNodes();

        for (int i = 0; i < discs.getLength(); i++) {
            List<String> t = new ArrayList<String>();

            Element disc = (Element) discs.item(i);
            NodeList trackList = disc.getElementsByTagName("Track");

            for (int j = 0; j < trackList.getLength(); j++) {
                Element tr = (Element) trackList.item(j);
                t.add(tr.getTextContent());
            }
            AmazonDisc d = new AmazonDisc(t);
            album.discs.add(d);
        }

        return album;
    }

    /**
     * Returns the name of the album.
     * 
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Returns the name of the artist.
     * 
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Return discs.
     * 
     * @return the discs
     */
    public List<AmazonDisc> getDiscs() {
        return discs;
    }

    /**
     * Returns cover url.
     * 
     * @return the image url
     */
    public String getImageURL() {
        return imageURL;
    }
}
