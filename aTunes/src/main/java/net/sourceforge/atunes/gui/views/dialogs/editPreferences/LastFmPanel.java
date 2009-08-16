/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddBannedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * The preferences panel for Last.fm settings.
 */
public class LastFmPanel extends PreferencesPanel {

    private static final long serialVersionUID = -9216216930198145476L;

    /** The Last.fm enabled check box */
    private JCheckBox lastFmEnabled;

    /** The last fm user. */
    private JTextField lastFmUser;

    /** The last fm password. */
    private JPasswordField lastFmPassword;

    /**
     * Checkbox to select if application must send a love request when user adds
     * a favorite song
     */
    JCheckBox autoLoveFavoriteSongs;

    /**
     * Instantiates a new last fm panel.
     */
    public LastFmPanel() {
        super("Last.fm");
        JLabel lastFmLabel = new JLabel(LanguageTool.getString("LASTFM_PREFERENCES"));
        lastFmEnabled = new JCheckBox(LanguageTool.getString("LASTFM_ENABLED"));
        JLabel userLabel = new JLabel(LanguageTool.getString("LASTFM_USER"));
        lastFmUser = new JTextField(15);
        JLabel passwordLabel = new JLabel(LanguageTool.getString("LASTFM_PASSWORD"));
        lastFmPassword = new JPasswordField(15);
        autoLoveFavoriteSongs = new JCheckBox(LanguageTool.getString("AUTOMATICALLY_LOVE_IN_LASTFM_FAVORITE_SONGS"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(5, 2, 5, 2);
        add(lastFmEnabled, c);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        add(lastFmLabel, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(5, 2, 5, 2);
        add(userLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        add(lastFmUser, c);
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0;
        c.insets = new Insets(5, 2, 5, 2);
        add(passwordLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        add(lastFmPassword, c);
        c.gridx = 0;
        c.gridy = 4;
        c.weighty = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(autoLoveFavoriteSongs, c);
    }

    @Override
    public boolean applyPreferences(ApplicationState state) {
        state.setLastFmUser(lastFmUser.getText());
        state.setLastFmPassword(String.valueOf(lastFmPassword.getPassword()));
        state.setLastFmEnabled(lastFmEnabled.isSelected());
        state.setAutoLoveFavoriteSong(autoLoveFavoriteSongs.isSelected());
        Actions.getAction(AddLovedSongInLastFMAction.class).setEnabled(state.isLastFmEnabled());
        Actions.getAction(AddBannedSongInLastFMAction.class).setEnabled(state.isLastFmEnabled());
        return false;
    }

    /**
     * Sets if Last.fm is enabled
     * 
     * @param enabled
     *            if Last.fm is enabled
     */
    private void setLastFmEnabled(boolean enabled) {
        lastFmEnabled.setSelected(enabled);
    }

    /**
     * Sets the last fm password.
     * 
     * @param password
     *            the new last fm password
     */
    private void setLastFmPassword(String password) {
        lastFmPassword.setText(password);
    }

    /**
     * Sets the last fm user.
     * 
     * @param user
     *            the new last fm user
     */
    private void setLastFmUser(String user) {
        lastFmUser.setText(user);
    }

    /**
     * Sets if application must send a love request when adding to favorites
     * 
     * @param enabled
     */
    private void setAutoLoveFavoriteSong(boolean enabled) {
        autoLoveFavoriteSongs.setSelected(enabled);
    }

    @Override
    public void updatePanel(ApplicationState state) {
        setLastFmUser(state.getLastFmUser());
        setLastFmPassword(state.getLastFmPassword());
        setLastFmEnabled(state.isLastFmEnabled());
        setAutoLoveFavoriteSong(state.isAutoLoveFavoriteSong());
    }

    @Override
    public boolean validatePanel() {
        return true;
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

}
