/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddBannedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.ImportLovedTracksFromLastFMAction;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * The preferences panel for Last.fm settings.
 */
public final class LastFmPanel extends AbstractPreferencesPanel {

    private final class TestLoginActionListener implements ActionListener {
		private final class TestLoginSwingWorker extends
				SwingWorker<Boolean, Void> {
			@Override
			protected Boolean doInBackground() {
			    return Context.getBean(IWebServicesHandler.class).testLogin(lastFmUser.getText(), String.valueOf(lastFmPassword.getPassword()));
			}

			@Override
			protected void done() {
			    try {
			        boolean loginSuccessful;
			        loginSuccessful = get();
			        if (loginSuccessful) {
			        	Context.getBean(IMessageDialog.class).showMessage(I18nUtils.getString("LOGIN_SUCCESSFUL"), getPreferenceDialog());
			        } else {
			        	Context.getBean(IErrorDialog.class).showErrorDialog(I18nUtils.getString("LOGIN_FAILED"), getPreferenceDialog());
			        }
			    } catch (InterruptedException e) {
			        Logger.error(e);
			    } catch (ExecutionException e) {
			        Logger.error(e);
			    } finally {
			        testLogin.setEnabled(true);
			    }
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		    testLogin.setEnabled(false);
		    new TestLoginSwingWorker().execute();
		}
	}

	private static final long serialVersionUID = -9216216930198145476L;

    private JCheckBox lastFmEnabled;
    private JTextField lastFmUser;
    private JPasswordField lastFmPassword;
    private JButton testLogin;

    /**
     * Checkbox to select if application must send a love request when user adds
     * a favorite song
     */
    private JCheckBox autoLoveFavoriteSongs;

    /**
     * Instantiates a new last fm panel.
     */
    public LastFmPanel() {
        super("Last.fm");
        JLabel lastFmLabel = new JLabel(I18nUtils.getString("LASTFM_PREFERENCES"));
        lastFmEnabled = new JCheckBox(I18nUtils.getString("LASTFM_ENABLED"));
        JLabel userLabel = new JLabel(I18nUtils.getString("LASTFM_USER"));
        lastFmUser = new CustomTextField(15);
        JLabel passwordLabel = new JLabel(I18nUtils.getString("LASTFM_PASSWORD"));
        lastFmPassword = new JPasswordField(15);
        autoLoveFavoriteSongs = new JCheckBox(I18nUtils.getString("AUTOMATICALLY_LOVE_IN_LASTFM_FAVORITE_SONGS"));
        testLogin = new JButton(I18nUtils.getString("TEST_LOGIN"));

        testLogin.addActionListener(new TestLoginActionListener());

        lastFmEnabled.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                enableControls();
            }
        });

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
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 0;
        add(testLogin, c);
        c.gridx = 0;
        c.gridy = 5;
        c.weighty = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(autoLoveFavoriteSongs, c);
    }

    @Override
    public boolean applyPreferences(IState state) {
        state.setLastFmUser(lastFmUser.getText());
        state.setLastFmPassword(String.valueOf(lastFmPassword.getPassword()));
        state.setLastFmEnabled(lastFmEnabled.isSelected());
        state.setAutoLoveFavoriteSong(autoLoveFavoriteSongs.isSelected());
        Actions.getAction(AddLovedSongInLastFMAction.class).setEnabled(state.isLastFmEnabled());
        Actions.getAction(AddBannedSongInLastFMAction.class).setEnabled(state.isLastFmEnabled());
        Actions.getAction(ImportLovedTracksFromLastFMAction.class).setEnabled(state.isLastFmEnabled());
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
    public void updatePanel(IState state) {
        setLastFmUser(state.getLastFmUser());
        setLastFmPassword(state.getLastFmPassword());
        setLastFmEnabled(state.isLastFmEnabled());
        setAutoLoveFavoriteSong(state.isAutoLoveFavoriteSong());
        enableControls();
    }

    /**
     * Enables all controls if main checkbox is selected
     */
    protected void enableControls() {
        boolean enabled = lastFmEnabled.isSelected();
        lastFmUser.setEnabled(enabled);
        lastFmPassword.setEnabled(enabled);
        autoLoveFavoriteSongs.setEnabled(enabled);
        testLogin.setEnabled(enabled);
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
