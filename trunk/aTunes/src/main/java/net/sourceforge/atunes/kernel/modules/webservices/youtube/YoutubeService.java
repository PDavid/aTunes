/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.webservices.youtube;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Calls the YouTube API
 * 
 * @author Tobias Melcher
 */
public class YoutubeService {

    /**
     * Query string wildcard
     */
    private static final String QUERY_STRING_WILDCARD = "%QUERY%";

    /**
     * Start index wildcard
     */
    private static final String START_INDEX_WILDCARD = "%STARTINDEX%";

    /**
     * Max results for a search
     */
    public static final int MAX_RESULTS = 10;

    /**
     * youtube search API: http://gdata.youtube.com/feeds/api/videos
     * &start-index= &max-results=10 &vq == search string
     */
    private static final String SEARCH_URL = StringUtils.getString("http://gdata.youtube.com/feeds/api/videos?vq=", QUERY_STRING_WILDCARD, "&max-results=", Integer
            .toString(MAX_RESULTS), "&start-index=", START_INDEX_WILDCARD);

    /**
     * Logger for this class
     */
    private Logger logger = new Logger();

    /**
     * The proxy
     */
    private Proxy proxy;

    /**
     * Singleton instance
     */
    private static YoutubeService instance;

    private YoutubeService(ProxyBean proxyBean) {
        Proxy proxy = null;
        try {
            if (proxyBean != null) {
                proxy = Proxy.getProxy(proxyBean);
            }
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
        }
        this.proxy = proxy;
    }

    /**
     * Getter for singleton instance
     * 
     * @return
     */
    public static YoutubeService getInstance() {
        if (instance == null) {
            instance = new YoutubeService(ApplicationState.getInstance().getProxy());
        }
        return instance;
    }

    /**
     * Updates service after a configuration change
     */
    public void updateService() {
        // Force create service again
        instance = null;
    }

    /**
     * triggers youtube search and returns result in table model structure by
     * default only the first 10 result entries will be returned. Specify
     * startIndex to get the next 10 results from given startIndex.
     * 
     * @param searchString
     * @param startIndex
     * @return
     */
    public List<YoutubeResultEntry> searchInYoutube(String searchString, int startIndex) {
        try {
            String searchStringEncoded = NetworkUtils.encodeString(searchString);
            searchStringEncoded = searchStringEncoded.replaceAll("\\+", "%20");

            //construct search url
            String url = SEARCH_URL.replaceAll(QUERY_STRING_WILDCARD, searchStringEncoded).replaceAll(START_INDEX_WILDCARD, Integer.toString(startIndex));

            //get the XML dom; very very nice API, I like it.		
            Document xml = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(url, proxy)));

            if (xml == null) {
                return Collections.emptyList();
            } else {
                // parse xml and construct result structure
                return analyzeResultXml(startIndex, xml);
            }
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
        }

        return Collections.emptyList();
    }

    private List<YoutubeResultEntry> analyzeResultXml(int startIndex, Document xml) throws IOException {
        List<YoutubeResultEntry> result = new ArrayList<YoutubeResultEntry>();

        NodeList entryList = xml.getElementsByTagName("entry");
        for (int i = 0; i < entryList.getLength(); i++) {
            Node item = entryList.item(i);
            if (item instanceof Element) {
                YoutubeResultEntry entry = new YoutubeResultEntry();

                Element el = (Element) item;
                Element mediaGroup = (Element) el.getElementsByTagName("media:group").item(0);
                //get title
                String title = mediaGroup.getElementsByTagName("media:title").item(0).getTextContent();
                entry.setName(title);
                //get url
                NodeList mediaPlayerList = mediaGroup.getElementsByTagName("media:player");
                if (mediaPlayerList != null) {
                    Node mpItem = mediaPlayerList.item(0);
                    if (mpItem != null) {
                        String url = ((Element) mpItem).getAttribute("url");
                        entry.setUrl(url);
                    }
                }

                //get image
                NodeList thumbnails = mediaGroup.getElementsByTagName("media:thumbnail");
                if (thumbnails != null) {
                    int index = thumbnails.getLength() / 2;
                    Node tn = thumbnails.item(index);
                    if (tn != null) {
                        String tnUrl = ((Element) tn).getAttribute("url");
                        Image image = NetworkUtils.getImage(NetworkUtils.getConnection(tnUrl, proxy));
                        entry.setImage(ImageUtils.scaleImageBicubic(image, Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
                    }
                }

                //get duration
                NodeList durationNodes = mediaGroup.getElementsByTagName("yt:duration");
                if (durationNodes != null) {
                    Node durationNode = durationNodes.item(0);
                    if (durationNode != null) {
                        String duration = ((Element) durationNode).getAttribute("seconds");
                        if (duration != null) {
                            entry.setDuration(StringUtils.seconds2String(Long.parseLong(duration)));
                        }
                    }
                }

                if (entry.getUrl() != null) {
                    result.add(entry);
                }
            }
        }

        return result;
    }

    /**
     * returns a URL which allows to download the youtube video. The html page
     * is opened and the swfArgs javascript is parsed to construct the download
     * URL.
     * 
     * @param url
     * @return
     */
    public String getDirectUrlToBeAbleToPlaySong(String url) {
        try {
            String response = NetworkUtils.readURL(NetworkUtils.getConnection(url, proxy));
            //now try to construct the download url from youtube
            int ind = response.indexOf("swfArgs");
            response = response.substring(ind + 1, response.length());
            ind = response.indexOf("\"video_id\":");
            String substr = response.substring(ind, response.length());
            int start = substr.indexOf(":");
            int end = substr.indexOf("\",");
            String video_id = substr.substring(start + 3, end);

            ind = response.indexOf("\"t\":");
            substr = response.substring(ind, response.length());
            start = substr.indexOf(":");
            end = substr.indexOf("\",");
            String t = substr.substring(start + 3, end);

            String downloadurl = StringUtils.getString("http://youtube.com/get_video?video_id=", video_id, "&t=", t);

            return downloadurl;
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Returns text to search at YouTube
     * 
     * @param ao
     * @return
     */
    public String getSearchForAudioObject(AudioObject ao) {
        StringBuilder builder = new StringBuilder();

        // Add artist if it's not unknown
        if (!Artist.isUnknownArtist(ao.getArtist())) {
            builder.append(ao.getArtist());
        }

        // Add processed title
        /*
         * Titles often contain chars between parentheses, brackets, etc. In
         * this cases search at YouTube can have no results as search string
         * contains information that does not match any video. So remove this
         * chars and its content
         */
        String title = ao.getTitle();
        if (title != null && !title.trim().equals("")) {
            // Remove () {} []
            title = title.replaceAll("\\(.*\\)", "");
            title = title.replaceAll("\\{.*\\}", "");
            title = title.replaceAll("\\[.*\\]", "");

            // ... but if we replaced all title then use original string as maybe we have removed too much ;)
            if (title.trim().isEmpty()) {
                title = ao.getTitle();
            }

            builder.append(" ").append(title);
        }

        return builder.toString();
    }
}
