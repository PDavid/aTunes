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
package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is responsible for retrieving the entries from a podcast feed and
 * refreshing the user interface if necessary.
 */
public class PodcastFeedEntryRetriever implements Runnable {

    private static class RefreshViewRunnable implements Runnable {
		@Override
		public void run() {
		    // refresh view
		    NavigationHandler.getInstance().refreshView(PodcastNavigationView.class);
		    logger.info(LogCategories.PODCAST, "Podcast feed entries retrieval done");
		}
	}

	static Logger logger = new Logger();

    List<PodcastFeed> podcastFeeds;

    public PodcastFeedEntryRetriever(List<PodcastFeed> podcastFeeds) {
        this.podcastFeeds = podcastFeeds;
    }

    /**
     * Retrieves Podcast Feed Entries and refreshes view
     */
    public List<PodcastFeed> retrievePodcastFeedEntries(final boolean removePodcastFeedEntriesRemovedFromPodcastFeed, Proxy proxy) {

        final List<PodcastFeed> podcastFeedsWithNewEntries = new ArrayList<PodcastFeed>();

        for (final PodcastFeed podcastFeed : podcastFeeds) {
            try {
                Document feedXml = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(podcastFeed.getUrl(), proxy)));

                if (feedXml != null) {

                    // Determine the feed type
                    FeedType feedType = determineFeedType(feedXml);
                    if (feedType != null) {
                        podcastFeed.setFeedType(feedType);
                    } else {
                        logger.info(LogCategories.PODCAST, podcastFeed + " is not a rss or atom feed");
                        continue;
                    }

                    // Retrieve feed name if necessary
                    if (podcastFeed.isRetrieveNameFromFeed()) {
                        retrieveNameFromFeed(podcastFeed, feedXml);
                    }

                    // Get entry nodes
                    NodeList entries = XMLUtils.evaluateXPathExpressionAndReturnNodeList(feedType.getEntryXPath(), feedXml);

                    final List<PodcastFeedEntry> newEntries = new ArrayList<PodcastFeedEntry>();
                    for (int i = 0; i < entries.getLength(); i++) {

                        String title = "";
                        String url = "";
                        String author = "";
                        String description = "";
                        Date date = null;
                        int duration = 0;

                        // Check if audio podcast feed entry
                        Node typeNode = XMLUtils.evaluateXPathExpressionAndReturnNode(feedType.getTypeXPath(), entries.item(i));
                        if (typeNode == null || !typeNode.getTextContent().matches(".*audio.*")) {
                            logger.info(LogCategories.PODCAST, StringUtils.getString("podcast feed entry is not from type audio: ", (typeNode != null ? typeNode.getTextContent()
                                    : "no type node")));
                            continue;
                        }

                        // Get title of podcast entry
                        Node titleNode = XMLUtils.evaluateXPathExpressionAndReturnNode(feedType.getTitleXPath(), entries.item(i));

                        if (titleNode != null) {
                            title = titleNode.getTextContent();
                        }

                        // Get url of podcast entry
                        Node urlNode = XMLUtils.evaluateXPathExpressionAndReturnNode(feedType.getUrlXPath(), entries.item(i));

                        if (urlNode != null) {
                            url = urlNode.getTextContent();
                        } else {
                            continue;
                        }

                        // Get Author of podcast entry
                        Node authorNode = XMLUtils.evaluateXPathExpressionAndReturnNode(feedType.getAuthorXPath(), entries.item(i));

                        if (authorNode != null) {
                            author = authorNode.getTextContent();
                        }

                        // Get description of podcast entry
                        Node descriptionNode = XMLUtils.evaluateXPathExpressionAndReturnNode(feedType.getDescriptionXPath(), entries.item(i));

                        if (descriptionNode != null) {
                            description = descriptionNode.getTextContent();
                            description = description.replaceAll("\\<.*?\\>", "");
                        }

                        // Get date of podcast entry
                        Node dateNode = XMLUtils.evaluateXPathExpressionAndReturnNode(feedType.getDateXPath(), entries.item(i));

                        if (dateNode != null) {
                            date = DateUtils.parseRFC822Date(dateNode.getTextContent());
                            if (date == null) {
                                date = DateUtils.parseRFC3339Date(dateNode.getTextContent());
                            }
                        }

                        // Try to find out duration
                        Node durationNode = XMLUtils.evaluateXPathExpressionAndReturnNode(feedType.getDurationXPath(), entries.item(i));

                        if (durationNode != null) {
                            String durationText = durationNode.getTextContent();
                            // Transform "01:01:22" to seconds
                            if (durationText != null) {
                                String[] result = durationText.split(":");
                                try {
                                    for (int j = result.length - 1; j >= 0; j--) {
                                        duration = duration + Integer.parseInt(result[j]) * (int) Math.pow(60, result.length - 1 - j);
                                    }
                                } catch (NumberFormatException e) {
                                    duration = 0;
                                    logger.info(LogCategories.PODCAST, "could not extract podcast feed entry duration");
                                }
                            }
                        }
                        newEntries.add(new PodcastFeedEntry(title, author, url, description, date, duration, podcastFeed));
                    }

                    podcastFeed.addEntries(newEntries, removePodcastFeedEntriesRemovedFromPodcastFeed);
                    if (podcastFeed.hasNewEntries()) {
                        podcastFeedsWithNewEntries.add(podcastFeed);
                    }

                }
            } catch (DOMException e) {
                logger.error(LogCategories.PODCAST, StringUtils.getString("Could not retrieve podcast feed entries from ", podcastFeed, ": ", e));
            } catch (IOException e) {
                logger.error(LogCategories.PODCAST, StringUtils.getString("Could not retrieve podcast feed entries from ", podcastFeed, ": ", e));
            }
        }

        return podcastFeedsWithNewEntries;

    }

    private void refreshView() {
        SwingUtilities.invokeLater(new RefreshViewRunnable());
    }

    private void showMessage(final List<PodcastFeed> podcastFeedsWithNewEntries) {
        synchronized (podcastFeeds) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (PodcastFeed podcastFeedWithNewEntries : podcastFeedsWithNewEntries) {
                        // Check if podcast feed wasn't removed during retrieval
                        if (podcastFeeds.contains(podcastFeedWithNewEntries)) {
                            // Remove "new" flag from podcasts
                            for (PodcastFeed podcastFeed : podcastFeeds) {
                                podcastFeed.markEntriesAsNotNew();
                            }
                            GuiHandler.getInstance().showNewPodcastFeedEntriesInfo();
                            break;
                        }
                    }
                }
            });
        }
    }

    private FeedType determineFeedType(Document feed) {
        if (XMLUtils.evaluateXPathExpressionAndReturnNode("/rss", feed) != null) {
            return FeedType.RSS;
        } else {
            if (XMLUtils.evaluateXPathExpressionAndReturnNode("/feed", feed) != null) {
                return FeedType.ATOM;
            } else {
                return null;
            }
        }
    }

    private void retrieveNameFromFeed(final PodcastFeed podcastFeed, Document feed) {
        Node node = XMLUtils.evaluateXPathExpressionAndReturnNode(podcastFeed.getFeedType().getNameXPath(), feed);
        if (node != null) {
            String name = node.getTextContent();
            podcastFeed.setName(name == null ? "" : name);
        }
    }

    @Override
    public void run() {
        try {
            boolean removePodcastFeedEntriesRemovedFromPodcastFeed = ApplicationState.getInstance().isRemovePodcastFeedEntriesRemovedFromPodcastFeed();
            Proxy proxy = Proxy.getProxy(ApplicationState.getInstance().getProxy());
            List<PodcastFeed> podcastFeedsWithNewEntries = retrievePodcastFeedEntries(removePodcastFeedEntriesRemovedFromPodcastFeed, proxy);
            // If there are new entries show a message and refresh view
            showMessage(podcastFeedsWithNewEntries);
            refreshView();
        } catch (IOException e) {
            logger.error(LogCategories.PODCAST, StringUtils.getString("Could not retrieve podcast feed entries : ", e));
        }
    }

}
