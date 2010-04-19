/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.GridBagConstraints;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

public final class PlayListPrefPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = -7814272907267661918L;

    public static final String PROP_PLAYLIST_STOP_ON_SWITCH = "PLAYLIST_STOP_ON_SWITCH";

    public static final String PROP_PLAYLIST_STOP_ON_CLEAR = "PLAYLIST_STOP_ON_CLEAR";

    public static final String PROP_AUTO_SCROLL_PLAYLIST = "AUTO_SCROLL_PLAYLIST";

    private JCheckBox stopSongWhenSwitching;
    private JCheckBox stopSongWhenClearing;
    private JCheckBox autoScrollPlayList;

    /**
     * Instantiates a new radio panel.
     */
    public PlayListPrefPanel() {
        super(I18nUtils.getString("PLAYLIST"));

        stopSongWhenSwitching = new JCheckBox(I18nUtils.getString(PROP_PLAYLIST_STOP_ON_SWITCH));
        stopSongWhenClearing = new JCheckBox(I18nUtils.getString(PROP_PLAYLIST_STOP_ON_CLEAR));
        autoScrollPlayList = new JCheckBox(I18nUtils.getString(PROP_AUTO_SCROLL_PLAYLIST));

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
        c.weighty = 1;
        add(autoScrollPlayList, c);

    }

    @Override
    public boolean applyPreferences(ApplicationState state) {
        state.setStopPlayerOnPlayListSwitch(stopSongWhenSwitching.isSelected());
        state.setStopPlayerOnPlayListClear(stopSongWhenClearing.isSelected());
        state.setAutoScrollPlayListEnabled(autoScrollPlayList.isSelected());
        return false;
    }

    /**
     * @param stop
     */
    private void setStopSongWhenSwitching(boolean stop) {
        stopSongWhenSwitching.setSelected(stop);
    }

    /**
     * @param stop
     */
    private void setStopSongWhenClearing(boolean stop) {
        stopSongWhenClearing.setSelected(stop);
    }

    /**
     * 
     * @param autoScroll
     */
    private void setAutoScrollPlayList(boolean autoScroll) {
        autoScrollPlayList.setSelected(autoScroll);
    }

    @Override
    public void updatePanel(ApplicationState state) {
        setStopSongWhenSwitching(state.isStopPlayerOnPlayListSwitch());
        setStopSongWhenClearing(state.isStopPlayerOnPlayListClear());
        setAutoScrollPlayList(state.isAutoScrollPlayListEnabled());
    }

    @Override
    public void resetImmediateChanges(ApplicationState state) {
        // Do nothing
    }

    @Override
    public boolean validatePanel() {
        return true;
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

    @Override
    public ImageIcon getIcon() {
        return Images.getImage(Images.PLAYLIST);
    }

}
