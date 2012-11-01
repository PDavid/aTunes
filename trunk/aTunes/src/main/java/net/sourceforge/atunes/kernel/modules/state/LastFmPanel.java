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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.kernel.actions.AddBannedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.ImportLovedTracksFromLastFMAction;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The preferences panel for Last.fm settings.
 */
public final class LastFmPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = -9216216930198145476L;

    private final JCheckBox lastFmEnabled;
    private final JTextField lastFmUser;
    private final JPasswordField lastFmPassword;
    private final JButton testLogin;

    private IStateContext stateContext;

    private IBeanFactory beanFactory;

    /**
     * @param beanFactory
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

    /**
     * @param stateContext
     */
    public void setStateContext(final IStateContext stateContext) {
	this.stateContext = stateContext;
    }

    /**
     * Checkbox to select if application must send a love request when user adds
     * a favorite song
     */
    private final JCheckBox autoLoveFavoriteSongs;

    /**
     * Instantiates a new last fm panel.
     */
    public LastFmPanel() {
	super("Last.fm");
	JLabel lastFmLabel = new JLabel(
		I18nUtils.getString("LASTFM_PREFERENCES"));
	lastFmEnabled = new JCheckBox(I18nUtils.getString("LASTFM_ENABLED"));
	JLabel userLabel = new JLabel(I18nUtils.getString("LASTFM_USER"));
	lastFmUser = new CustomTextField(15);
	JLabel passwordLabel = new JLabel(
		I18nUtils.getString("LASTFM_PASSWORD"));
	lastFmPassword = new JPasswordField(15);
	autoLoveFavoriteSongs = new JCheckBox(
		I18nUtils
			.getString("AUTOMATICALLY_LOVE_IN_LASTFM_FAVORITE_SONGS"));
	testLogin = new JButton(I18nUtils.getString("TEST_LOGIN"));

	testLogin.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(final ActionEvent e) {
		testLogin.setEnabled(false);
		new TestLastFmLoginSwingWorker(lastFmUser.getText(), String
			.valueOf(lastFmPassword.getPassword()), testLogin,
			getPreferenceDialog(), beanFactory).execute();
	    }
	});

	lastFmEnabled.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(final ActionEvent e) {
		enableControls();
	    }
	});

	arrangePanel(lastFmLabel, userLabel, passwordLabel);
    }

    /**
     * @param lastFmLabel
     * @param userLabel
     * @param passwordLabel
     */
    private void arrangePanel(JLabel lastFmLabel, JLabel userLabel,
	    JLabel passwordLabel) {
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
    public boolean applyPreferences() {
	stateContext.setLastFmUser(lastFmUser.getText());
	stateContext.setLastFmPassword(String.valueOf(lastFmPassword
		.getPassword()));
	stateContext.setLastFmEnabled(lastFmEnabled.isSelected());
	stateContext
		.setAutoLoveFavoriteSong(autoLoveFavoriteSongs.isSelected());
	beanFactory.getBean(AddLovedSongInLastFMAction.class).setEnabled(
		stateContext.isLastFmEnabled());
	beanFactory.getBean(AddBannedSongInLastFMAction.class).setEnabled(
		stateContext.isLastFmEnabled());
	beanFactory.getBean(ImportLovedTracksFromLastFMAction.class)
		.setEnabled(stateContext.isLastFmEnabled());
	return false;
    }

    /**
     * Sets if Last.fm is enabled
     * 
     * @param enabled
     *            if Last.fm is enabled
     */
    private void setLastFmEnabled(final boolean enabled) {
	lastFmEnabled.setSelected(enabled);
    }

    /**
     * Sets the last fm password.
     * 
     * @param password
     *            the new last fm password
     */
    private void setLastFmPassword(final String password) {
	lastFmPassword.setText(password);
    }

    /**
     * Sets the last fm user.
     * 
     * @param user
     *            the new last fm user
     */
    private void setLastFmUser(final String user) {
	lastFmUser.setText(user);
    }

    /**
     * Sets if application must send a love request when adding to favorites
     * 
     * @param enabled
     */
    private void setAutoLoveFavoriteSong(final boolean enabled) {
	autoLoveFavoriteSongs.setSelected(enabled);
    }

    @Override
    public void updatePanel() {
	setLastFmUser(stateContext.getLastFmUser());
	setLastFmPassword(stateContext.getLastFmPassword());
	setLastFmEnabled(stateContext.isLastFmEnabled());
	setAutoLoveFavoriteSong(stateContext.isAutoLoveFavoriteSong());
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
