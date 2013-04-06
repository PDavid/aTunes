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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.FeedType;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IStatePodcast;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;
import net.sourceforge.atunes.utils.XPathUtils;

import org.joda.time.DateTime;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is responsible for retrieving the entries from a podcast feed and
 * refreshing the user interface if necessary.
 */
public class PodcastFeedEntryRetriever implements Runnable {

	private class RefreshViewRunnable implements Runnable {
		@Override
		public void run() {
			// refresh view
			PodcastFeedEntryRetriever.this.navigationHandler
					.refreshView(PodcastFeedEntryRetriever.this.podcastNavigationView);
			Logger.info("Podcast feed entries retrieval done");
		}
	}

	private List<IPodcastFeed> podcastFeeds;

	private IStateUI stateUI;

	private IFrame frame;

	private INavigationHandler navigationHandler;

	private INetworkHandler networkHandler;

	private INavigationView podcastNavigationView;

	private IStatePodcast statePodcast;

	private IDialogFactory dialogFactory;

	private IStateService stateService;

	/**
	 * @param stateService
	 */
	public void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * @param podcastNavigationView
	 */
	public void setPodcastNavigationView(
			final INavigationView podcastNavigationView) {
		this.podcastNavigationView = podcastNavigationView;
	}

	/**
	 * @param statePodcast
	 */
	public void setStatePodcast(final IStatePodcast statePodcast) {
		this.statePodcast = statePodcast;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param podcastFeeds
	 */
	public void setPodcastFeeds(final List<IPodcastFeed> podcastFeeds) {
		this.podcastFeeds = podcastFeeds;
	}

	/**
	 * Retrieves Podcast Feed Entries and refreshes view
	 * 
	 * @param removePodcastFeedEntriesRemovedFromPodcastFeed
	 * @return
	 */
	List<IPodcastFeed> retrievePodcastFeedEntries(
			final boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {

		final List<IPodcastFeed> podcastFeedsWithNewEntries = new ArrayList<IPodcastFeed>();

		for (final IPodcastFeed podcastFeed : this.podcastFeeds) {
			try {
				Document feedXml = XMLUtils.getXMLDocument(this.networkHandler
						.readURL(this.networkHandler.getConnection(podcastFeed
								.getUrl())));

				if (feedXml != null) {

					// Determine the feed type
					FeedType feedType = determineFeedType(feedXml);
					if (feedType != null) {
						podcastFeed.setFeedType(feedType);
					} else {
						Logger.info(podcastFeed + " is not a rss or atom feed");
						continue;
					}

					// Retrieve feed name if necessary
					if (podcastFeed.isRetrieveNameFromFeed()) {
						retrieveNameFromFeed(podcastFeed, feedXml);
					}

					// Get entry nodes
					NodeList entries = XPathUtils
							.evaluateXPathExpressionAndReturnNodeList(
									feedType.getEntryXPath(), feedXml);

					final List<IPodcastFeedEntry> newEntries = new ArrayList<IPodcastFeedEntry>();
					for (int i = 0; i < entries.getLength(); i++) {
						PodcastFeedEntry entry = getEntry(feedType,
								entries.item(i), podcastFeed);
						if (entry != null) {
							newEntries.add(entry);
						}
					}

					podcastFeed.addEntries(newEntries,
							removePodcastFeedEntriesRemovedFromPodcastFeed);
					if (podcastFeed.hasNewEntries()) {
						podcastFeedsWithNewEntries.add(podcastFeed);
					}

				}
			} catch (DOMException e) {
				Logger.error(StringUtils.getString(
						"Could not retrieve podcast feed entries from ",
						podcastFeed, ": ", e));
			} catch (IOException e) {
				Logger.error(StringUtils.getString(
						"Could not retrieve podcast feed entries from ",
						podcastFeed, ": ", e));
			}
		}

		return podcastFeedsWithNewEntries;
	}

	private PodcastFeedEntry getEntry(final FeedType feedType,
			final Node entry, final IPodcastFeed podcastFeed) {
		// Check if audio podcast feed entry
		Node typeNode = XPathUtils.evaluateXPathExpressionAndReturnNode(
				feedType.getTypeXPath(), entry);
		if (typeNode == null || !typeNode.getTextContent().matches(".*audio.*")) {
			Logger.info(StringUtils.getString(
					"podcast feed entry is not from type audio: ",
					(typeNode != null ? typeNode.getTextContent()
							: "no type node")));
			return null;
		}

		String title = getTitle(feedType, entry);
		String url = getUrl(feedType, entry);
		if (url == null) {
			return null;
		}

		String author = getAuthor(feedType, entry);
		String description = getDescription(feedType, entry);

		DateTime date = getDate(feedType, entry);

		int duration = getDuration(feedType, entry);
		return new PodcastFeedEntry(title, author, url, description, date,
				duration, podcastFeed);
	}

	/**
	 * @param feedType
	 * @param entry
	 * @return
	 */
	private int getDuration(final FeedType feedType, final Node entry) {
		int duration = 0;
		// Try to find out duration
		Node durationNode = XPathUtils.evaluateXPathExpressionAndReturnNode(
				feedType.getDurationXPath(), entry);
		if (durationNode != null) {
			String durationText = durationNode.getTextContent();
			// Transform "01:01:22" to seconds
			if (durationText != null) {
				String[] result = durationText.split(":");
				try {
					for (int j = result.length - 1; j >= 0; j--) {
						duration = duration + Integer.parseInt(result[j])
								* (int) Math.pow(60, result.length - 1 - j);
					}
				} catch (NumberFormatException e) {
					duration = 0;
					Logger.info("could not extract podcast feed entry duration");
				}
			}
		}
		return duration;
	}

	/**
	 * @param feedType
	 * @param entry
	 * @return
	 */
	private DateTime getDate(final FeedType feedType, final Node entry) {
		// Get date of podcast entry
		Node dateNode = XPathUtils.evaluateXPathExpressionAndReturnNode(
				feedType.getDateXPath(), entry);
		if (dateNode != null) {
			DateTime date = DateUtils
					.parseRFC822Date(dateNode.getTextContent());
			if (date == null) {
				date = DateUtils.parseRFC3339Date(dateNode.getTextContent());
			}
			return date;
		}
		return null;
	}

	/**
	 * @param feedType
	 * @param entry
	 * @return
	 */
	private String getDescription(final FeedType feedType, final Node entry) {
		// Get description of podcast entry
		Node descriptionNode = XPathUtils.evaluateXPathExpressionAndReturnNode(
				feedType.getDescriptionXPath(), entry);
		if (descriptionNode != null) {
			String description = descriptionNode.getTextContent();
			description = description.replaceAll("\\<.*?\\>", "");
			return description;
		}
		return "";
	}

	/**
	 * @param feedType
	 * @param entry
	 * @return
	 */
	private String getAuthor(final FeedType feedType, final Node entry) {
		// Get Author of podcast entry
		Node authorNode = XPathUtils.evaluateXPathExpressionAndReturnNode(
				feedType.getAuthorXPath(), entry);
		if (authorNode != null) {
			return authorNode.getTextContent();
		}
		return "";
	}

	/**
	 * @param feedType
	 * @param entry
	 * @return
	 */
	private String getUrl(final FeedType feedType, final Node entry) {
		// Get url of podcast entry
		Node urlNode = XPathUtils.evaluateXPathExpressionAndReturnNode(
				feedType.getUrlXPath(), entry);
		if (urlNode != null) {
			return urlNode.getTextContent();
		} else {
			return null;
		}
	}

	/**
	 * @param feedType
	 * @param entry
	 * @return
	 */
	private String getTitle(final FeedType feedType, final Node entry) {
		// Get title of podcast entry
		Node titleNode = XPathUtils.evaluateXPathExpressionAndReturnNode(
				feedType.getTitleXPath(), entry);
		if (titleNode != null) {
			return titleNode.getTextContent();
		}
		return "";
	}

	private void refreshView() {
		SwingUtilities.invokeLater(new RefreshViewRunnable());
	}

	private void showMessage(final List<IPodcastFeed> podcastFeedsWithNewEntries) {
		synchronized (this.podcastFeeds) {
			GuiUtils.callInEventDispatchThread(new Runnable() {
				@Override
				public void run() {
					for (IPodcastFeed podcastFeedWithNewEntries : podcastFeedsWithNewEntries) {
						// Check if podcast feed wasn't removed during retrieval
						if (PodcastFeedEntryRetriever.this.podcastFeeds
								.contains(podcastFeedWithNewEntries)) {
							// Remove "new" flag from podcasts
							for (IPodcastFeed podcastFeed : PodcastFeedEntryRetriever.this.podcastFeeds) {
								podcastFeed.markEntriesAsNotNew();
							}
							if (!PodcastFeedEntryRetriever.this.stateUI
									.isShowStatusBar()) {
								PodcastFeedEntryRetriever.this.dialogFactory
										.newDialog(IMessageDialog.class)
										.showMessage(
												I18nUtils
														.getString("NEW_PODCAST_ENTRIES"));
							} else {
								PodcastFeedEntryRetriever.this.frame
										.showNewPodcastFeedEntriesInfo(true);
							}
							break;
						}
					}
				}
			});
		}
	}

	private FeedType determineFeedType(final Document feed) {
		if (XPathUtils.evaluateXPathExpressionAndReturnNode("/rss", feed) != null) {
			return FeedType.RSS;
		} else {
			if (XPathUtils.evaluateXPathExpressionAndReturnNode("/feed", feed) != null) {
				return FeedType.ATOM;
			} else {
				return null;
			}
		}
	}

	private void retrieveNameFromFeed(final IPodcastFeed podcastFeed,
			final Document feed) {
		Node node = XPathUtils.evaluateXPathExpressionAndReturnNode(podcastFeed
				.getFeedType().getNameXPath(), feed);
		if (node != null) {
			String name = node.getTextContent();
			podcastFeed.setName(name == null ? "" : name);
		}
	}

	@Override
	public void run() {
		boolean removePodcastFeedEntriesRemovedFromPodcastFeed = this.statePodcast
				.isRemovePodcastFeedEntriesRemovedFromPodcastFeed();
		List<IPodcastFeed> podcastFeedsWithNewEntries = retrievePodcastFeedEntries(removePodcastFeedEntriesRemovedFromPodcastFeed);
		// If there are new entries show a message and refresh view
		showMessage(podcastFeedsWithNewEntries);
		refreshView();
		// Also store podcast information
		this.stateService.persistPodcastFeedCache(this.podcastFeeds);
	}
}
