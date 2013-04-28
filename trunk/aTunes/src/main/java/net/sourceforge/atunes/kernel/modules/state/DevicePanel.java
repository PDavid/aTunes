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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.views.controls.CustomFileChooser;
import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Panel for setup
 * 
 * @author alex
 * 
 */
public final class DevicePanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = 3331810461314007217L;

	/** The location file chooser. */
	private CustomFileChooser locationFileChooser;

	/**
	 * The radio button used to select no changes in file names when copying to
	 * device
	 */
	private JRadioButton fileNameNoChangeRadioButton;

	/** The radio button used to rename files when copying to device */
	private JRadioButton fileNameCustomizedRadioButton;

	/** Text Field used to define custom file name pattern */
	private JTextField fileNamePatternTextField;

	/**
	 * The radio button used to select no changes in folder name when copying to
	 * device
	 */
	private JRadioButton folderPathNoChangeRadioButton;

	/** The radio button used to set folder when copying to device */
	private JRadioButton folderPathCustomizedRadioButton;

	/** Text Field used to define custom folder path */
	private JTextField folderPathPatternTextField;

	/**
	 * A check box to set if user wants to copy the same song several times (if
	 * song is repeated for different albums)
	 */
	private JCheckBox copySameSongForDifferentAlbums;

	private IOSManager osManager;

	private ILookAndFeelManager lookAndFeelManager;

	private IStateDevice stateDevice;

	private Patterns patterns;

	private IBeanFactory beanFactory;

	private IControlsBuilder controlsBuilder;

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
	 * @param patterns
	 */
	public void setPatterns(final Patterns patterns) {
		this.patterns = patterns;
	}

	/**
	 * @param stateDevice
	 */
	public void setStateDevice(final IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Instantiates a new device panel.
	 */
	public DevicePanel() {
		super(I18nUtils.getString("DEVICE"));
	}

	/**
	 * Initializes panel
	 */
	public void initialize() {
		JLabel label = new JLabel(
				I18nUtils.getString("DEVICE_DEFAULT_LOCATION"));
		this.locationFileChooser = new CustomFileChooser(
				I18nUtils.getString("DEVICE_DEFAULT_LOCATION"), this, 20,
				JFileChooser.DIRECTORIES_ONLY, this.osManager,
				this.beanFactory, this.controlsBuilder);

		JPanel fileNamePanel = new JPanel(new GridBagLayout());
		fileNamePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(0, 0, 10, 0),
				I18nUtils.getString("FILE_NAME")));
		this.fileNameNoChangeRadioButton = new JRadioButton(
				I18nUtils.getString("NO_CHANGE"));
		this.fileNameNoChangeRadioButton
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						DevicePanel.this.fileNamePatternTextField
								.setEnabled(false);
					}
				});
		this.fileNameCustomizedRadioButton = new JRadioButton(
				I18nUtils.getString("CUSTOM"));
		this.fileNameCustomizedRadioButton
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						DevicePanel.this.fileNamePatternTextField
								.setEnabled(true);
					}
				});
		this.fileNamePatternTextField = this.controlsBuilder.createTextField();
		this.fileNamePatternTextField.setColumns(10);
		ButtonGroup group = new ButtonGroup();
		group.add(this.fileNameNoChangeRadioButton);
		group.add(this.fileNameCustomizedRadioButton);

		JPanel folderPathPanel = new JPanel(new GridBagLayout());
		folderPathPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(0, 0, 10, 0),
				I18nUtils.getString("FOLDER")));
		this.folderPathNoChangeRadioButton = new JRadioButton(
				I18nUtils.getString("FLAT"));
		this.folderPathNoChangeRadioButton
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						DevicePanel.this.folderPathPatternTextField
								.setEnabled(false);
					}
				});
		this.folderPathCustomizedRadioButton = new JRadioButton(
				I18nUtils.getString("CUSTOM"));
		this.folderPathCustomizedRadioButton
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						DevicePanel.this.folderPathPatternTextField
								.setEnabled(true);
					}
				});
		this.folderPathPatternTextField = this.controlsBuilder
				.createTextField();
		this.folderPathPatternTextField.setColumns(10);
		ButtonGroup group2 = new ButtonGroup();
		group2.add(this.folderPathNoChangeRadioButton);
		group2.add(this.folderPathCustomizedRadioButton);

		JTable availablePatternsTable = this.lookAndFeelManager
				.getCurrentLookAndFeel().getTable();
		availablePatternsTable.setModel(new AvailablePatternsTableModel(
				this.patterns.getPatternsList()));

		JPanel patternsPanel = new JPanel(new BorderLayout());
		patternsPanel.setPreferredSize(new Dimension(250, 200));
		patternsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(0, 0, 0, 0),
				I18nUtils.getString("AVAILABLE_PATTERNS")));
		patternsPanel.add(
				this.controlsBuilder.createScrollPane(availablePatternsTable),
				BorderLayout.CENTER);

		this.copySameSongForDifferentAlbums = new JCheckBox(
				I18nUtils
						.getString("ALLOW_COPY_TO_DEVICE_SAME_SONG_FOR_DIFFERENT_ALBUMS"));

		arrangePanel(label, fileNamePanel, folderPathPanel, patternsPanel);
	}

	/**
	 * @param label
	 * @param fileNamePanel
	 * @param folderPathPanel
	 * @param patternsPanel
	 */
	private void arrangePanel(final JLabel label, final JPanel fileNamePanel,
			final JPanel folderPathPanel, final JPanel patternsPanel) {

		GridBagConstraints c;
		arrangeFileNamePanel(fileNamePanel);
		arrangeFolderPathPanel(folderPathPanel);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(label, c);
		c.gridx = 1;
		add(this.locationFileChooser, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(fileNamePanel, c);

		c.gridx = 2;
		c.gridwidth = 1;
		c.gridheight = 3;
		c.weightx = 0;
		c.anchor = GridBagConstraints.NORTH;
		add(patternsPanel, c);

		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 2;
		c.weightx = 1;
		c.gridheight = 1;
		add(folderPathPanel, c);

		c.gridy = 3;
		c.weighty = 1;
		add(this.copySameSongForDifferentAlbums, c);
	}

	/**
	 * @param folderPathPanel
	 */
	private void arrangeFolderPathPanel(final JPanel folderPathPanel) {
		GridBagConstraints c;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		folderPathPanel.add(this.folderPathNoChangeRadioButton, c);
		c.gridx = 0;
		c.gridy = 1;
		folderPathPanel.add(this.folderPathCustomizedRadioButton, c);
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 20, 0, 20);
		c.fill = GridBagConstraints.HORIZONTAL;
		folderPathPanel.add(this.folderPathPatternTextField, c);
	}

	/**
	 * @param fileNamePanel
	 */
	private void arrangeFileNamePanel(final JPanel fileNamePanel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		fileNamePanel.add(this.fileNameNoChangeRadioButton, c);
		c.gridx = 0;
		c.gridy = 1;
		fileNamePanel.add(this.fileNameCustomizedRadioButton, c);
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 20, 0, 20);
		c.fill = GridBagConstraints.HORIZONTAL;
		fileNamePanel.add(this.fileNamePatternTextField, c);
	}

	@Override
	public boolean applyPreferences() {
		this.stateDevice.setDefaultDeviceLocation(this.locationFileChooser
				.getResult());
		String fileNamePattern = this.fileNameNoChangeRadioButton.isSelected() ? ""
				: this.fileNamePatternTextField.getText();
		this.stateDevice.setDeviceFileNamePattern(fileNamePattern == null
				|| fileNamePattern.trim().equals("") ? null : fileNamePattern);
		String folderPathPattern = this.folderPathNoChangeRadioButton
				.isSelected() ? "" : this.folderPathPatternTextField.getText();
		this.stateDevice.setDeviceFolderPathPattern(folderPathPattern == null
				|| folderPathPattern.equals("") ? null : folderPathPattern);
		this.stateDevice
				.setAllowRepeatedSongsInDevice(this.copySameSongForDifferentAlbums
						.isSelected());
		return false;
	}

	/**
	 * Sets the default device location.
	 * 
	 * @param location
	 *            the new default device location
	 */
	private void setDefaultDeviceLocation(final String location) {
		this.locationFileChooser.setText(location);
	}

	@Override
	public void updatePanel() {
		setDefaultDeviceLocation(this.stateDevice.getDefaultDeviceLocation());
		if (this.stateDevice.getDeviceFileNamePattern() == null
				|| this.stateDevice.getDeviceFileNamePattern().trim()
						.equals("")) {
			this.fileNameNoChangeRadioButton.setSelected(true);
			this.fileNamePatternTextField.setEnabled(false);
		} else {
			this.fileNameCustomizedRadioButton.setSelected(true);
			this.fileNamePatternTextField.setEnabled(true);
			this.fileNamePatternTextField.setText(this.stateDevice
					.getDeviceFileNamePattern());
		}
		if (this.stateDevice.getDeviceFolderPathPattern() == null
				|| this.stateDevice.getDeviceFolderPathPattern().trim()
						.equals("")) {
			this.folderPathNoChangeRadioButton.setSelected(true);
			this.folderPathPatternTextField.setEnabled(false);
		} else {
			this.folderPathCustomizedRadioButton.setSelected(true);
			this.folderPathPatternTextField.setEnabled(true);
			this.folderPathPatternTextField.setText(this.stateDevice
					.getDeviceFolderPathPattern());
		}
		this.copySameSongForDifferentAlbums.setSelected(this.stateDevice
				.isAllowRepeatedSongsInDevice());
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
