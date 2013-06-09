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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sourceforge.atunes.kernel.actions.AddBannedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.ImportLovedTracksFromLastFMAction;
import net.sourceforge.atunes.kernel.actions.ShowRecommendedEventsFromLastFMAction;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The preferences panel for Last.fm settings.
 */
public final class LastFmPanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = -9216216930198145476L;

	private JCheckBox lastFmEnabled;
	private JTextField lastFmUser;
	private JPasswordField lastFmPassword;
	private JButton testLogin;

	private IStateContext stateContext;

	private IBeanFactory beanFactory;

	private IControlsBuilder controlsBuilder;

	private JCheckBox cacheContent;

	/**
	 * Checkbox to select if application must send a love request when user adds
	 * a favorite song
	 */
	private JCheckBox autoLoveFavoriteSongs;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

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
	 * Instantiates a new last fm panel.
	 */
	public LastFmPanel() {
		super("Last.fm");
	}

	/**
	 * Initialize panel
	 */
	public void initialize() {
		JLabel lastFmLabel = new JLabel(
				I18nUtils.getString("LASTFM_PREFERENCES"));
		this.lastFmEnabled = new JCheckBox(
				I18nUtils.getString("LASTFM_ENABLED"));
		JLabel userLabel = new JLabel(I18nUtils.getString("LASTFM_USER"));
		this.lastFmUser = this.controlsBuilder.createTextField();
		this.lastFmUser.setColumns(15);
		JLabel passwordLabel = new JLabel(
				I18nUtils.getString("LASTFM_PASSWORD"));
		this.lastFmPassword = new JPasswordField(15);
		this.autoLoveFavoriteSongs = new JCheckBox(
				I18nUtils
						.getString("AUTOMATICALLY_LOVE_IN_LASTFM_FAVORITE_SONGS"));
		this.testLogin = new JButton(I18nUtils.getString("TEST_LOGIN"));

		this.testLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				LastFmPanel.this.testLogin.setEnabled(false);
				LastFmPanel.this.beanFactory.getBean(
						TestLastFmLoginBackgroundWorker.class).test(
						LastFmPanel.this.lastFmUser.getText(),
						String.valueOf(LastFmPanel.this.lastFmPassword
								.getPassword()), LastFmPanel.this.testLogin,
						getPreferenceDialog());
			}
		});

		this.lastFmEnabled.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				enableControls();
			}
		});

		this.cacheContent = new JCheckBox(
				I18nUtils.getString("CACHE_LASTFM_CONTENT"));

		arrangePanel(lastFmLabel, userLabel, passwordLabel);
	}

	/**
	 * @param lastFmLabel
	 * @param userLabel
	 * @param passwordLabel
	 */
	private void arrangePanel(final JLabel lastFmLabel, final JLabel userLabel,
			final JLabel passwordLabel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(5, 2, 5, 2);
		add(this.lastFmEnabled, c);
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
		add(this.lastFmUser, c);
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		c.insets = new Insets(5, 2, 5, 2);
		add(passwordLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		add(this.lastFmPassword, c);
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 0;
		add(this.testLogin, c);
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		add(this.autoLoveFavoriteSongs, c);
		c.gridy = 6;
		c.weighty = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(this.cacheContent, c);
	}

	@Override
	public boolean applyPreferences() {
		this.stateContext.setLastFmUser(this.lastFmUser.getText());
		this.stateContext.setLastFmPassword(String.valueOf(this.lastFmPassword
				.getPassword()));
		this.stateContext.setLastFmEnabled(this.lastFmEnabled.isSelected());
		this.stateContext.setAutoLoveFavoriteSong(this.autoLoveFavoriteSongs
				.isSelected());
		this.beanFactory.getBean(AddLovedSongInLastFMAction.class).setEnabled(
				this.stateContext.isLastFmEnabled());
		this.beanFactory.getBean(AddBannedSongInLastFMAction.class).setEnabled(
				this.stateContext.isLastFmEnabled());
		this.beanFactory.getBean(ImportLovedTracksFromLastFMAction.class)
				.setEnabled(this.stateContext.isLastFmEnabled());
		this.beanFactory.getBean(ShowRecommendedEventsFromLastFMAction.class)
				.setEnabled(this.stateContext.isLastFmEnabled());
		this.stateContext.setCacheLastFmContent(this.cacheContent.isSelected());
		return false;
	}

	/**
	 * Sets if Last.fm is enabled
	 * 
	 * @param enabled
	 *            if Last.fm is enabled
	 */
	private void setLastFmEnabled(final boolean enabled) {
		this.lastFmEnabled.setSelected(enabled);
	}

	/**
	 * Sets the last fm password.
	 * 
	 * @param password
	 *            the new last fm password
	 */
	private void setLastFmPassword(final String password) {
		this.lastFmPassword.setText(password);
	}

	/**
	 * Sets the last fm user.
	 * 
	 * @param user
	 *            the new last fm user
	 */
	private void setLastFmUser(final String user) {
		this.lastFmUser.setText(user);
	}

	/**
	 * Sets if application must send a love request when adding to favorites
	 * 
	 * @param enabled
	 */
	private void setAutoLoveFavoriteSong(final boolean enabled) {
		this.autoLoveFavoriteSongs.setSelected(enabled);
	}

	/**
	 * Sets if app must cache last.fm contents
	 * 
	 * @param cache
	 */
	private void setCacheContent(final boolean cache) {
		this.cacheContent.setSelected(cache);
	}

	@Override
	public void updatePanel() {
		setLastFmUser(this.stateContext.getLastFmUser());
		setLastFmPassword(this.stateContext.getLastFmPassword());
		setLastFmEnabled(this.stateContext.isLastFmEnabled());
		setAutoLoveFavoriteSong(this.stateContext.isAutoLoveFavoriteSong());
		setCacheContent(this.stateContext.isCacheLastFmContent());
		enableControls();
	}

	/**
	 * Enables all controls if main checkbox is selected
	 */
	protected void enableControls() {
		boolean enabled = this.lastFmEnabled.isSelected();
		this.lastFmUser.setEnabled(enabled);
		this.lastFmPassword.setEnabled(enabled);
		this.autoLoveFavoriteSongs.setEnabled(enabled);
		this.testLogin.setEnabled(enabled);
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
