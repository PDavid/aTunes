/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import net.sourceforge.atunes.gui.views.controls.CustomJFileChooser;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;

public final class PodcastFeedPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = -1298749333908609956L;

    /** The retrieval interval. */
    private JComboBox retrievalInterval;

    /** The download folder file chooser. */
    private CustomJFileChooser downloadFolderFileChooser;

    /** The use downloaded podcast feed entries. */
    private JCheckBox useDownloadedPodcastFeedEntries;

    /** The remove podcast feed entries removed from podcast feed. */
    private JCheckBox removePodcastFeedEntriesRemovedFromPodcastFeed;

    private IOSManager osManager;
    
    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    /**
     * Instantiates a new podcast feed panel.
     */
    public PodcastFeedPanel() {
        super(I18nUtils.getString("PODCAST_FEEDS"));
    }
    
    /**
     * Initializes panel
     */
    public void initialize() {
        JLabel retrievalIntervalLabel = new JLabel(I18nUtils.getString("PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL"));
        retrievalInterval = new JComboBox(new Long[] { 1l, 3l, 5l, 10l, 15l, 30l, 60l });
        JLabel downloadFolderLabel = new JLabel(I18nUtils.getString("PODCAST_FEED_ENTRIES_DOWNLOAD_FOLDER"));
        downloadFolderFileChooser = new CustomJFileChooser(this, 20, JFileChooser.DIRECTORIES_ONLY, osManager);
        useDownloadedPodcastFeedEntries = new JCheckBox(I18nUtils.getString("USE_DOWNLOADED_PODCAST_FEED_ENTRIES"));
        removePodcastFeedEntriesRemovedFromPodcastFeed = new JCheckBox(I18nUtils.getString("REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(retrievalIntervalLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 5, 0, 0);
        add(retrievalInterval, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.insets = new Insets(5, 0, 0, 0);
        add(downloadFolderLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 0, 0);
        add(downloadFolderFileChooser, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.insets = new Insets(5, 0, 0, 0);
        add(useDownloadedPodcastFeedEntries, c);
        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);
        add(removePodcastFeedEntriesRemovedFromPodcastFeed, c);
    }

    @Override
    public boolean applyPreferences(IState state) {
        state.setPodcastFeedEntriesRetrievalInterval(((Long) retrievalInterval.getSelectedItem()) * 60);
        state.setPodcastFeedEntryDownloadPath(downloadFolderFileChooser.getResult());
        state.setUseDownloadedPodcastFeedEntries(useDownloadedPodcastFeedEntries.isSelected());
        state.setRemovePodcastFeedEntriesRemovedFromPodcastFeed(removePodcastFeedEntriesRemovedFromPodcastFeed.isSelected());
        return false;
    }

    /**
     * Sets the retrieval interval.
     * 
     * @param time
     *            the new retrieval interval
     */
    private void setRetrievalInterval(long time) {
        retrievalInterval.setSelectedItem(time / 60);
    }

    /**
     * Sets the download path.
     * 
     * @param path
     *            the new download path
     */
    private void setDownloadPath(String path) {
        downloadFolderFileChooser.setText(path);
    }

    /**
     * Sets the use downloaded podcast feed entries.
     * 
     * @param use
     *            the new use downloaded podcast feed entries
     */
    private void setUseDownloadedPodcastFeedEntries(boolean use) {
        useDownloadedPodcastFeedEntries.setSelected(use);
    }

    /**
     * Sets the removes the podcast feed entries removed from podcast feed.
     * 
     * @param remove
     *            the new removes the podcast feed entries removed from podcast
     *            feed
     */
    private void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean remove) {
        removePodcastFeedEntriesRemovedFromPodcastFeed.setSelected(remove);
    }

    @Override
    public void updatePanel(IState state) {
        setRetrievalInterval(state.getPodcastFeedEntriesRetrievalInterval());
        setDownloadPath(state.getPodcastFeedEntryDownloadPath());
        setUseDownloadedPodcastFeedEntries(state.isUseDownloadedPodcastFeedEntries());
        setRemovePodcastFeedEntriesRemovedFromPodcastFeed(state.isRemovePodcastFeedEntriesRemovedFromPodcastFeed());
    }

    @Override
    public void resetImmediateChanges(IState state) {
        // Do nothing
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

}
