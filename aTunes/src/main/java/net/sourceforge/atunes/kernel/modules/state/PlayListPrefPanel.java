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

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;

import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Panel for preferences of play list
 * 
 * @author alex
 * 
 */
public final class PlayListPrefPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = -7814272907267661918L;

    private static final String PROP_PLAYLIST_STOP_ON_SWITCH = "PLAYLIST_STOP_ON_SWITCH";

    private static final String PROP_PLAYLIST_STOP_ON_CLEAR = "PLAYLIST_STOP_ON_CLEAR";

    private static final String PROP_AUTO_SCROLL_PLAYLIST = "AUTO_SCROLL_PLAYLIST";

    private final JCheckBox stopSongWhenSwitching;
    private final JCheckBox stopSongWhenClearing;
    private final JCheckBox autoScrollPlayList;
    private final JCheckBox showPlayListSelectorCombo;

    private IStatePlaylist statePlaylist;

    /**
     * @param statePlaylist
     */
    public void setStatePlaylist(final IStatePlaylist statePlaylist) {
	this.statePlaylist = statePlaylist;
    }

    /**
     * Instantiates a new radio panel.
     */
    public PlayListPrefPanel() {
	super(I18nUtils.getString("PLAYLIST"));

	stopSongWhenSwitching = new JCheckBox(
		I18nUtils.getString(PROP_PLAYLIST_STOP_ON_SWITCH));
	stopSongWhenClearing = new JCheckBox(
		I18nUtils.getString(PROP_PLAYLIST_STOP_ON_CLEAR));
	autoScrollPlayList = new JCheckBox(
		I18nUtils.getString(PROP_AUTO_SCROLL_PLAYLIST));
	showPlayListSelectorCombo = new JCheckBox(
		I18nUtils.getString("SHOW_PLAYLIST_SELECTOR_COMBO"));

	GridBagConstraints c = new GridBagConstraints();
	c.gridx = 1;
	c.gridy = 0;
	c.weightx = 1;
	c.weighty = 0;
	c.anchor = GridBagConstraints.FIRST_LINE_START;
	add(stopSongWhenSwitching, c);
	c.gridy = 1;
	add(stopSongWhenClearing, c);
	c.gridy = 2;
	add(autoScrollPlayList, c);
	c.gridy = 3;
	c.weighty = 1;
	add(showPlayListSelectorCombo, c);
    }

    @Override
    public boolean applyPreferences() {
	statePlaylist.setStopPlayerOnPlayListSwitch(stopSongWhenSwitching
		.isSelected());
	statePlaylist.setStopPlayerOnPlayListClear(stopSongWhenClearing
		.isSelected());
	statePlaylist.setAutoScrollPlayListEnabled(autoScrollPlayList
		.isSelected());
	statePlaylist.setShowPlayListSelectorComboBox(showPlayListSelectorCombo
		.isSelected());
	return false;
    }

    @Override
    public void updatePanel() {
	stopSongWhenSwitching.setSelected(statePlaylist
		.isStopPlayerOnPlayListSwitch());
	stopSongWhenClearing.setSelected(statePlaylist
		.isStopPlayerOnPlayListClear());
	autoScrollPlayList.setSelected(statePlaylist
		.isAutoScrollPlayListEnabled());
	showPlayListSelectorCombo.setSelected(statePlaylist
		.isShowPlayListSelectorComboBox());

    }

    @Override
    public void resetImmediateChanges() {
	// Do nothing
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
    }

    @Override
    public void dialogVisibilityChanged(final boolean visible) {
	// Do nothing
    }

}
