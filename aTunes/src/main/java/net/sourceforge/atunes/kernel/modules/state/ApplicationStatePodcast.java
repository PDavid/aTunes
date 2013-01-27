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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.Map;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.model.IStatePodcast;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStatePodcast implements IStatePodcast {

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public long getPodcastFeedEntriesRetrievalInterval() {
		return this.preferenceHelper
				.getPreference(
						Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL,
						Long.class,
						PodcastFeedHandler.DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL);
	}

	@Override
	public void setPodcastFeedEntriesRetrievalInterval(
			final long podcastFeedEntriesRetrievalInterval) {
		this.preferenceHelper.setPreference(
				Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL,
				podcastFeedEntriesRetrievalInterval);
	}

	@Override
	public String getPodcastFeedEntryDownloadPath() {
		return this.preferenceHelper.getPreference(
				Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, String.class,
				null);
	}

	@Override
	public void setPodcastFeedEntryDownloadPath(
			final String podcastFeedEntryDownloadPath) {
		this.preferenceHelper.setPreference(
				Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH,
				podcastFeedEntryDownloadPath);
	}

	@Override
	public boolean isUseDownloadedPodcastFeedEntries() {
		return this.preferenceHelper.getPreference(
				Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, Boolean.class,
				true);
	}

	@Override
	public void setUseDownloadedPodcastFeedEntries(
			final boolean useDownloadedPodcastFeedEntries) {
		this.preferenceHelper.setPreference(
				Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES,
				useDownloadedPodcastFeedEntries);
	}

	@Override
	public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed() {
		return this.preferenceHelper
				.getPreference(
						Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED,
						Boolean.class, false);
	}

	@Override
	public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(
			final boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {
		this.preferenceHelper
				.setPreference(
						Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED,
						removePodcastFeedEntriesRemovedFromPodcastFeed);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
