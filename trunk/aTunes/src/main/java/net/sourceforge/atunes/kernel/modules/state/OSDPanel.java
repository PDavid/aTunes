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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.INotificationEngine;
import net.sourceforge.atunes.model.INotificationsHandler;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Panel for OSD preferences
 * 
 * @author alex
 * 
 */
public final class OSDPanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = 4489293347321979288L;

	private JComboBox notificationEngines;

	private JLabel engineAvailability;

	private JLabel engineDescription;

	private UrlLabel engineMoreInformation;

	private JPanel osdSettings;
	private JComboBox osdDuration;
	private JComboBox osdWidth;
	private JComboBox osdHorizontalAlignment;
	private JComboBox osdVerticalAlignment;

	private static final String LEFT = I18nUtils.getString("LEFT");
	private static final String CENTER = I18nUtils.getString("CENTER");
	private static final String RIGHT = I18nUtils.getString("RIGHT");
	private static final String TOP = I18nUtils.getString("TOP");
	private static final String BOTTOM = I18nUtils.getString("BOTTOM");

	private INotificationsHandler notificationsHandler;

	private IStateUI stateUI;

	private IStateCore stateCore;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param notificationsHandler
	 */
	public void setNotificationsHandler(
			final INotificationsHandler notificationsHandler) {
		this.notificationsHandler = notificationsHandler;
	}

	/**
	 * Instantiates a new OSD panel.
	 */
	public OSDPanel() {
		super(I18nUtils.getString("OSD"));
	}

	/**
	 * Initializes panel
	 */
	public void initialize() {
		JLabel enginesLabel = new JLabel(
				I18nUtils.getString("NOTIFICATION_ENGINE"));
		this.notificationEngines = new JComboBox(this.notificationsHandler
				.getNotificationEngines().toArray());
		this.notificationEngines.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				updatePanel((String) e.getItem());
			}
		});

		this.engineAvailability = new JLabel(
				I18nUtils.getString("NOTIFICATION_ENGINE_NOT_AVAILABLE"));
		this.engineAvailability.setVisible(false);

		this.engineDescription = new JLabel();

		this.engineMoreInformation = (UrlLabel) this.controlsBuilder
				.getUrlLabel();

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 0, 0);
		add(enginesLabel, c);

		c.gridx = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		add(this.notificationEngines, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		add(this.engineAvailability, c);

		c.gridy = 2;
		add(this.engineDescription, c);

		c.gridy = 3;
		add(this.engineMoreInformation, c);

		this.osdSettings = getOSDSettings();
		this.osdSettings.setVisible(false);
		JPanel container = new JPanel(new BorderLayout());
		container.add(this.osdSettings, BorderLayout.CENTER);
		c.gridx = 0;
		c.gridy = 4;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		add(container, c);

	}

	private JPanel getOSDSettings() {
		JPanel panel = new JPanel(new GridBagLayout());

		JLabel label = new JLabel(I18nUtils.getString("OSD_DURATION"));
		this.osdDuration = new JComboBox(new Integer[] { 2, 4, 6 });
		JLabel label2 = new JLabel(I18nUtils.getString("OSD_WIDTH"));
		this.osdWidth = new JComboBox(new Integer[] { 400, 500, 600 });
		JLabel label3 = new JLabel(I18nUtils.getString("HORIZONTAL_ALIGNMENT"));
		this.osdHorizontalAlignment = new JComboBox(new String[] { LEFT,
				CENTER, RIGHT });
		JLabel label4 = new JLabel(I18nUtils.getString("VERTICAL_ALIGNMENT"));
		this.osdVerticalAlignment = new JComboBox(new String[] { TOP, CENTER,
				BOTTOM });

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 0, 0);
		panel.add(label, c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(5, 10, 0, 0);
		panel.add(this.osdDuration, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.insets = new Insets(10, 10, 0, 0);
		panel.add(label2, c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(5, 10, 0, 0);
		panel.add(this.osdWidth, c);
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.insets = new Insets(10, 10, 0, 0);
		panel.add(label3, c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(5, 10, 0, 0);
		panel.add(this.osdHorizontalAlignment, c);
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		c.insets = new Insets(10, 10, 0, 0);
		panel.add(label4, c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(5, 10, 0, 0);
		panel.add(this.osdVerticalAlignment, c);

		return panel;
	}

	@Override
	public boolean applyPreferences() {
		this.stateCore.setNotificationEngine((String) this.notificationEngines
				.getSelectedItem());
		this.stateUI.setOsdDuration((Integer) this.osdDuration
				.getSelectedItem());
		this.stateUI.setOsdWidth((Integer) this.osdWidth.getSelectedItem());
		this.stateUI
				.setOsdHorizontalAlignment(getAlignment((String) this.osdHorizontalAlignment
						.getSelectedItem()));
		this.stateUI
				.setOsdVerticalAlignment(getAlignment((String) this.osdVerticalAlignment
						.getSelectedItem()));
		return false;
	}

	/**
	 * Sets the oSD duration.
	 * 
	 * @param time
	 *            the new oSD duration
	 */
	private void setOSDDuration(final int time) {
		this.osdDuration.setSelectedItem(time);
	}

	/**
	 * Sets the OSD width
	 * 
	 * @param width
	 */
	private void setOSDWidth(final int width) {
		this.osdWidth.setSelectedItem(width);
	}

	/**
	 * Sets the OSD horizontal alignment
	 * 
	 * @param alignment
	 */
	private void setOSDHorizontalAlignment(final int alignment) {
		if (alignment == SwingConstants.LEFT) {
			this.osdHorizontalAlignment.setSelectedItem(LEFT);
		} else if (alignment == SwingConstants.CENTER) {
			this.osdHorizontalAlignment.setSelectedItem(CENTER);
		} else if (alignment == SwingConstants.RIGHT) {
			this.osdHorizontalAlignment.setSelectedItem(RIGHT);
		}
	}

	/**
	 * Returns alignment constant given a localized string
	 * 
	 * @param alignment
	 * @return
	 */
	private int getAlignment(final String alignment) {
		if (LEFT.equals(alignment)) {
			return SwingConstants.LEFT;
		} else if (RIGHT.equals(alignment)) {
			return SwingConstants.RIGHT;
		} else if (TOP.equals(alignment)) {
			return SwingConstants.TOP;
		} else if (BOTTOM.equals(alignment)) {
			return SwingConstants.BOTTOM;
		} else if (CENTER.equals(alignment)) {
			return SwingConstants.CENTER;
		}
		return 0;
	}

	/**
	 * Sets the OSD vertical alignment
	 * 
	 * @param alignment
	 */
	private void setOSDVerticalAlignment(final int alignment) {
		if (alignment == SwingConstants.TOP) {
			this.osdVerticalAlignment.setSelectedItem(TOP);
		} else if (alignment == SwingConstants.CENTER) {
			this.osdVerticalAlignment.setSelectedItem(CENTER);
		} else if (alignment == SwingConstants.BOTTOM) {
			this.osdVerticalAlignment.setSelectedItem(BOTTOM);
		}
	}

	/**
	 * Sets notification engine in combo
	 * 
	 * @param engine
	 */
	private void setNotificationEngine(final String engine) {
		this.notificationEngines.setSelectedItem(engine != null ? engine
				: this.notificationsHandler.getDefaultEngine().getName());
		updatePanel((String) this.notificationEngines.getSelectedItem());
	}

	/**
	 * Updates panel depending in notification engine
	 * 
	 * @param notificationEngine
	 */
	private void updatePanel(final String notificationEngine) {
		INotificationEngine engine = this.notificationsHandler
				.getNotificationEngine(notificationEngine);
		if (engine != null) {
			this.engineAvailability.setVisible(!engine.isEngineAvailable());
			this.engineDescription.setText(engine.getDescription());
			this.engineMoreInformation
					.setText(engine.getUrl(), engine.getUrl());
			// Show settings for default engine
			this.osdSettings.setVisible(engine.getName().equals(
					this.notificationsHandler.getDefaultEngine().getName()));
		}
	}

	@Override
	public void updatePanel() {
		setNotificationEngine(this.stateCore.getNotificationEngine());
		setOSDDuration(this.stateUI.getOsdDuration());
		setOSDWidth(this.stateUI.getOsdWidth());
		setOSDHorizontalAlignment(this.stateUI.getOsdHorizontalAlignment());
		setOSDVerticalAlignment(this.stateUI.getOsdVerticalAlignment());
	}

	@Override
	public void resetImmediateChanges() {
		// Do nothing
	}

	@Override
	public void validatePanel() throws PreferencesValidationException {
		// Notification engine must be available
		if (!this.notificationsHandler.getNotificationEngine(
				(String) this.notificationEngines.getSelectedItem())
				.isEngineAvailable()) {
			throw new PreferencesValidationException(
					I18nUtils.getString("NOTIFICATION_ENGINE_NOT_AVAILABLE"),
					null);
		}
	}

	@Override
	public void dialogVisibilityChanged(final boolean visible) {
		// Do nothing
	}

}
