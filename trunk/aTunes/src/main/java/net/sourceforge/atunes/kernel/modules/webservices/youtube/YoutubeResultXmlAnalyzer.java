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

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IVideoEntry;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.TimeUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses XML from Youtube
 * 
 * @author alex
 * 
 */
public final class YoutubeResultXmlAnalyzer {

	private INetworkHandler networkHandler;

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * Returns a list of youtube results from a xml document
	 * 
	 * @param artist
	 * @param xml
	 * @return
	 * @throws IOException
	 */
	List<IVideoEntry> analyzeResultXml(final String artist, final Document xml)
			throws IOException {
		List<IVideoEntry> result = new ArrayList<IVideoEntry>();

		NodeList entryList = xml.getElementsByTagName("entry");
		for (int i = 0; i < entryList.getLength(); i++) {
			Node item = entryList.item(i);
			if (item instanceof Element) {
				analizeEntry(artist, result, (Element) item);
			}
		}

		return result;
	}

	/**
	 * @param artist
	 * @param result
	 * @param item
	 * @throws IOException
	 */
	private void analizeEntry(final String artist,
			final List<IVideoEntry> result, final Element item)
			throws IOException {
		IVideoEntry entry = new YoutubeResultEntry();

		Element mediaGroup = (Element) item.getElementsByTagName("media:group")
				.item(0);

		// set artist
		entry.setArtist(artist);

		// get title
		getTitle(entry, mediaGroup);

		// get url
		getUrl(entry, mediaGroup);

		// get image
		getImage(entry, mediaGroup);

		// get duration
		getDuration(entry, mediaGroup);

		if (entry.getUrl() != null) {
			result.add(entry);
		}
	}

	/**
	 * @param entry
	 * @param mediaGroup
	 */
	private void getTitle(final IVideoEntry entry, final Element mediaGroup) {
		String title = mediaGroup.getElementsByTagName("media:title").item(0)
				.getTextContent();
		entry.setName(title);
	}

	/**
	 * @param entry
	 * @param mediaGroup
	 */
	private void getUrl(final IVideoEntry entry, final Element mediaGroup) {
		NodeList mediaPlayerList = mediaGroup
				.getElementsByTagName("media:player");
		if (mediaPlayerList != null) {
			Node mpItem = mediaPlayerList.item(0);
			if (mpItem != null) {
				String url = ((Element) mpItem).getAttribute("url");
				entry.setUrl(url);
			}
		}
	}

	/**
	 * @param entry
	 * @param mediaGroup
	 */
	private void getDuration(final IVideoEntry entry, final Element mediaGroup) {
		NodeList durationNodes = mediaGroup.getElementsByTagName("yt:duration");
		if (durationNodes != null) {
			Node durationNode = durationNodes.item(0);
			if (durationNode != null) {
				String duration = ((Element) durationNode)
						.getAttribute("seconds");
				if (duration != null) {
					entry.setDuration(TimeUtils
							.secondsToHoursMinutesSeconds(Long
									.parseLong(duration)));
				}
			}
		}
	}

	/**
	 * @param entry
	 * @param mediaGroup
	 * @throws IOException
	 */
	private void getImage(final IVideoEntry entry, final Element mediaGroup)
			throws IOException {
		NodeList thumbnails = mediaGroup
				.getElementsByTagName("media:thumbnail");
		if (thumbnails != null) {
			int index = thumbnails.getLength() / 2;
			Node tn = thumbnails.item(index);
			if (tn != null) {
				String tnUrl = ((Element) tn).getAttribute("url");
				Image image = networkHandler.getImage(networkHandler
						.getConnection(tnUrl));
				entry.setImage(ImageUtils.scaleImageBicubic(image,
						Constants.THUMB_IMAGE_WIDTH,
						Constants.THUMB_IMAGE_HEIGHT));
			}
		}
	}

}
