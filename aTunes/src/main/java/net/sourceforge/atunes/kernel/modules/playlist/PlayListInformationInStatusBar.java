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

package net.sourceforge.atunes.kernel.modules.playlist;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListObjectFilter;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Shows information about current playlist in frame status bar
 * 
 * @author alex
 * 
 */
public class PlayListInformationInStatusBar {

    private static final String SLASH = " / ";

    private static final String PLAYLIST = "PLAYLIST";

    private IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter;

    private IFrame frame;

    /**
     * @param frame
     */
    public void setFrame(final IFrame frame) {
	this.frame = frame;
    }

    /**
     * @param playListLocalAudioObjectFilter
     */
    public void setPlayListLocalAudioObjectFilter(
	    final IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter) {
	this.playListLocalAudioObjectFilter = playListLocalAudioObjectFilter;
    }

    /**
     * Show play list information.
     * 
     * @param playList
     *            the play list
     */
    void showPlayListInformation(final IPlayList playList) {
	int audioFiles = playListLocalAudioObjectFilter.getObjects(playList)
		.size();
	int radios = new PlayListRadioFilter().getObjects(playList).size();
	int podcastFeedEntries = new PlayListPodcastFeedEntryFilter()
		.getObjects(playList).size();
	int audioObjects = playList.size();

	final String toolTip = getToolTip(playList, audioFiles, radios,
		podcastFeedEntries, audioObjects);
	final String text = StringUtils.getString(
		I18nUtils.getString(PLAYLIST), ": ", audioObjects, " - ",
		audioFiles, SLASH, radios, SLASH, podcastFeedEntries);

	GuiUtils.callInEventDispatchThread(new Runnable() {
	    @Override
	    public void run() {
		frame.setRightStatusBarText(text, toolTip);
	    }
	});
    }

    /**
     * @param playList
     * @param audioFiles
     * @param radios
     * @param podcastFeedEntries
     * @param audioObjects
     * @return
     */
    private String getToolTip(final IPlayList playList, final int audioFiles,
	    final int radios, final int podcastFeedEntries,
	    final int audioObjects) {
	// Check if differenciation is required (needed by some slavic
	// languages)
	String toolTip = StringUtils
		.getString(
			I18nUtils.getString(PLAYLIST),
			": ",
			audioObjects,
			" ",
			I18nUtils.getString("SONGS"),
			" (",
			playList.getLength(),
			") ",
			" - ",
			audioFiles,
			" ",
			I18nUtils.getString("SONGS"),
			SLASH,
			radios,
			" ",
			I18nUtils.getString("RADIOS"),
			SLASH,
			podcastFeedEntries,
			" ",
			(I18nUtils.getString("PODCAST_ENTRIES_COUNTER")
				.isEmpty() ? I18nUtils
				.getString("PODCAST_ENTRIES") : I18nUtils
				.getString("PODCAST_ENTRIES_COUNTER")));
	return toolTip;
    }
}
