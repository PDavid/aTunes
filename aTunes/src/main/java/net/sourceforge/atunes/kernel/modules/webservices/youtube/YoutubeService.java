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

package net.sourceforge.atunes.kernel.modules.webservices.youtube;

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IVideoEntry;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;

/**
 * Calls the YouTube API
 * 
 * @author Tobias Melcher
 */
public final class YoutubeService {

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
    private static final String SEARCH_URL = StringUtils.getString(
	    "http://gdata.youtube.com/feeds/api/videos?vq=",
	    QUERY_STRING_WILDCARD, "&max-results=",
	    Integer.toString(MAX_RESULTS), "&start-index=",
	    START_INDEX_WILDCARD);

    private INetworkHandler networkHandler;

    private YoutubeResultXmlAnalyzer youtubeResultXmlAnalyzer;

    private IUnknownObjectChecker unknownObjectChecker;

    /**
     * @param unknownObjectChecker
     */
    public void setUnknownObjectChecker(
	    final IUnknownObjectChecker unknownObjectChecker) {
	this.unknownObjectChecker = unknownObjectChecker;
    }

    /**
     * @param youtubeResultXmlAnalyzer
     */
    public void setYoutubeResultXmlAnalyzer(
	    final YoutubeResultXmlAnalyzer youtubeResultXmlAnalyzer) {
	this.youtubeResultXmlAnalyzer = youtubeResultXmlAnalyzer;
    }

    /**
     * @param networkHandler
     */
    public void setNetworkHandler(final INetworkHandler networkHandler) {
	this.networkHandler = networkHandler;
    }

    /**
     * triggers youtube search and returns result in table model structure by
     * default only the first 10 result entries will be returned. Specify
     * startIndex to get the next 10 results from given startIndex.
     * 
     * @param artist
     * @param searchString
     * @param startIndex
     * @return
     */
    public List<IVideoEntry> searchInYoutube(final String artist,
	    final String searchString, final int startIndex) {
	try {
	    String searchStringEncoded = networkHandler
		    .encodeString(searchString);
	    searchStringEncoded = searchStringEncoded.replaceAll("\\+", "%20");

	    // construct search url
	    String url = SEARCH_URL.replaceAll(QUERY_STRING_WILDCARD,
		    searchStringEncoded).replaceAll(START_INDEX_WILDCARD,
		    Integer.toString(startIndex));

	    // get the XML dom; very very nice API, I like it.
	    Document xml = XMLUtils.getXMLDocument(networkHandler
		    .readURL(networkHandler.getConnection(url)));

	    if (xml == null) {
		return Collections.emptyList();
	    } else {
		// parse xml and construct result structure
		return youtubeResultXmlAnalyzer.analyzeResultXml(artist, xml);
	    }
	} catch (Exception e) {
	    Logger.error(e);
	}

	return Collections.emptyList();
    }

    /**
     * returns a URL which allows to download the youtube video. The html page
     * is opened and the swfArgs javascript is parsed to construct the download
     * URL.
     * 
     * @param url
     * @return
     */
    public String getDirectUrlToBeAbleToPlaySong(final String url) {
	try {
	    String response = networkHandler.readURL(networkHandler
		    .getConnection(url));
	    // now try to construct the download url from youtube
	    int ind = response.indexOf("swfArgs");
	    response = response.substring(ind + 1, response.length());
	    ind = response.indexOf("\"video_id\":");
	    String substr = response.substring(ind, response.length());
	    int start = substr.indexOf(':');
	    int end = substr.indexOf("\",");
	    String videoId = substr.substring(start + 3, end);

	    ind = response.indexOf("\"t\":");
	    substr = response.substring(ind, response.length());
	    start = substr.indexOf(':');
	    end = substr.indexOf("\",");
	    String t = substr.substring(start + 3, end);
	    /**
	     * Quality settings for downloading Youtube video: No &fmt = FLV
	     * (very low - same as &fmt=5) &fmt=5 = FLV (very low) &fmt=6 = FLV
	     * (does not always work) &fmt=13 = 3GP (mobile phone) &fmt=18 = MP4
	     * (normal) &fmt=22 = MP4 (hd)
	     */
	    String videoQuality = "18";
	    String downloadurl = StringUtils.getString(
		    "http://youtube.com/get_video?video_id=", videoId, "&t=",
		    t, "&fmt=", videoQuality);

	    return downloadurl;
	} catch (Exception e) {
	    Logger.error(e);
	}
	return null;
    }

    /**
     * Returns text to search at YouTube
     * 
     * @param ao
     * @return
     */
    public String getSearchForAudioObject(final IAudioObject ao) {
	StringBuilder builder = new StringBuilder();

	// Add artist if it's not unknown
	if (!unknownObjectChecker.isUnknownArtist(ao
		.getArtist(unknownObjectChecker))) {
	    builder.append(ao.getArtist(unknownObjectChecker));
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

	    // ... but if we replaced all title then use original string as
	    // maybe we have removed too much ;)
	    if (title.trim().isEmpty()) {
		title = ao.getTitle();
	    }

	    builder.append(" ").append(title);
	}

	return builder.toString();
    }
}
