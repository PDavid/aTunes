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

package net.sourceforge.atunes.kernel.modules.player;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.IEqualizerDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.PlayerEngineCapability;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Allows changing equalizer settings for mplayer. Mplayer offers a 10 band
 * equalizer function.
 * 
 * @author sylvain
 */
public final class EqualizerDialog extends AbstractCustomDialog implements
		IEqualizerDialog {

	private static final long serialVersionUID = 7295438534550341824L;

	private IEqualizer equalizer;

	/** The bands. */
	private JSlider[] bands;

	private IPlayerHandler playerHandler;

	private IDialogFactory dialogFactory;

	private JCheckBox equalizerEnabled;

	private JLabel[] labels;

	private JLabel l12;

	private JLabel l0;

	private JLabel lm12;

	private JButton loadPresetButton;

	private JButton applyButton;

	private JLabel changeWhenStopped;

	/**
	 * Draws the equalizer dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public EqualizerDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		// Width required by german translation
		super(frame, 600, 400, controlsBuilder);
	}

	/**
	 * Updates equalizer
	 */
	void updateEqualizer() {
		this.equalizer.setEqualizerFromGUI(this.equalizerEnabled.isSelected(),
				this.bands);
	}

	/**
	 * @param equalizer
	 */
	public void setEqualizer(final IEqualizer equalizer) {
		this.equalizer = equalizer;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * Initializes dialog
	 */
	@Override
	public void initialize() {
		setTitle(StringUtils.getString(I18nUtils.getString("EQUALIZER"), " - ",
				Constants.APP_NAME, " ", Constants.VERSION.toShortString()));
		add(getContent());
		setResizable(false);
		setEqualizerState();
	}

	JSlider[] getBands() {
		return this.bands;
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * Gets the new j slider.
	 * 
	 * @return the new j slider
	 */
	private JSlider getNewJSlider() {
		JSlider slider = new JSlider(1, -32, 32, 0);
		slider.setInverted(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(32);
		slider.setMinorTickSpacing(4);

		return slider;
	}

	/**
	 * Updates sliders with current equalizer settings.
	 */
	private void setEqualizerState() {
		int[] eqSettings = this.equalizer.getEqualizerSettingsToShowInGUI();
		if (eqSettings != null) {
			for (int i = 0; i < 10; i++) {
				this.bands[i].setValue(eqSettings[i]);
			}
		}
		this.equalizerEnabled.setSelected(this.equalizer.isEnabled());
		updateState(this.equalizer.isEnabled());
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		this.equalizerEnabled = new JCheckBox(I18nUtils.getString("EQUALIZER"));

		String[] freqs = { "31Hz", "62Hz", "125Hz", "250Hz", "500Hz", "1kHz",
				"2kHz", "4kHz", "8kHz", "16kHz" };
		this.bands = new JSlider[10];
		this.labels = new JLabel[10];

		for (int i = 0; i < 10; i++) {
			this.bands[i] = getNewJSlider();
			this.labels[i] = new JLabel(freqs[i]);
		}

		this.changeWhenStopped = new JLabel(
				I18nUtils.getString("CAN_ONLY_CHANGE_WHEN_STOPPED"));

		this.loadPresetButton = new JButton(I18nUtils.getString("LOAD_PRESET"));
		this.loadPresetButton.addActionListener(new LoadPresetActionListener(
				this.equalizer, this.dialogFactory, this));

		JButton okButton = new JButton(I18nUtils.getString("OK"));
		okButton.addActionListener(new OkActionListener(this));

		this.applyButton = new JButton(I18nUtils.getString("APPLY"));
		this.applyButton.addActionListener(new ApplyActionListener(this));

		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new CancelActionListener(this));

		arrangePanel(panel, this.equalizerEnabled, this.labels,
				this.changeWhenStopped, this.loadPresetButton, okButton,
				this.applyButton, cancelButton);

		this.equalizerEnabled.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				boolean active = ((JCheckBox) event.getSource()).isSelected();
				updateState(active);
			}
		});

		return panel;
	}

	/**
	 * @param active
	 */
	private void updateState(final boolean active) {
		this.equalizer.setEnabled(active);
		for (JSlider band : this.bands) {
			band.setEnabled(active);
		}
		for (JLabel label : this.labels) {
			label.setEnabled(active);
		}
		this.changeWhenStopped.setEnabled(active);
		this.l0.setEnabled(active);
		this.l12.setEnabled(active);
		this.lm12.setEnabled(active);
		this.loadPresetButton.setEnabled(active);
		this.applyButton.setEnabled(active);
	}

	/**
	 * @param panel
	 * @param equalizerActive
	 * @param labels
	 * @param changeWhenStopped
	 * @param loadPresetButton
	 * @param okButton
	 * @param applyButton
	 * @param cancelButton
	 */
	private void arrangePanel(final JPanel panel,
			final JCheckBox equalizerActive, final JLabel[] labels,
			final JLabel changeWhenStopped, final JButton loadPresetButton,
			final JButton okButton, final JButton applyButton,
			final JButton cancelButton) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 11;
		c.insets = new Insets(10, 0, 10, 0);
		panel.add(equalizerActive, c);

		c.gridy = 1;
		c.insets = new Insets(3, 3, 3, 3);
		c.gridwidth = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		for (int i = 0; i < this.bands.length; i++) {
			c.gridx = i;
			panel.add(this.bands[i], c);
		}

		this.l12 = new JLabel("+12db");
		this.l0 = new JLabel("0");
		this.lm12 = new JLabel("-12db");

		JPanel labelPanel = new JPanel(new GridLayout(3, 1, 0, 50));
		labelPanel.add(this.l12);
		labelPanel.add(this.l0);
		labelPanel.add(this.lm12);

		c.gridx = 10;
		panel.add(labelPanel, c);

		c.weighty = 0;
		c.gridy = 2;

		for (int i = 0; i < labels.length; i++) {
			c.gridx = i;
			panel.add(labels[i], c);
		}

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 11;
		c.fill = GridBagConstraints.SOUTH;
		c.insets = new Insets(10, 0, 10, 0);
		panel.add(loadPresetButton, c);

		if (!this.playerHandler
				.supportsCapability(PlayerEngineCapability.EQUALIZER_CHANGE)) {
			c.gridy = 4;
			panel.add(changeWhenStopped, c);
		}

		JPanel auxPanel = new JPanel();
		auxPanel.add(applyButton);
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);

		c.gridy = 5;
		panel.add(auxPanel, c);
	}
}
