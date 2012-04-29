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

import net.sourceforge.atunes.gui.views.controls.CustomJFileChooser;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.utils.I18nUtils;

public final class DevicePanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = 3331810461314007217L;

    /** The location file chooser. */
    private CustomJFileChooser locationFileChooser;

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
    
    /**
     * @param stateDevice
     */
    public void setStateDevice(IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}
    
    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
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
        JLabel label = new JLabel(I18nUtils.getString("DEVICE_DEFAULT_LOCATION"));
        locationFileChooser = new CustomJFileChooser(this, 20, JFileChooser.DIRECTORIES_ONLY, osManager);

        JPanel fileNamePanel = new JPanel(new GridBagLayout());
        fileNamePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,10,0), I18nUtils.getString("FILE_NAME")));
        fileNameNoChangeRadioButton = new JRadioButton(I18nUtils.getString("NO_CHANGE"));
        fileNameNoChangeRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileNamePatternTextField.setEnabled(false);
            }
        });
        fileNameCustomizedRadioButton = new JRadioButton(I18nUtils.getString("CUSTOM"));
        fileNameCustomizedRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileNamePatternTextField.setEnabled(true);
            }
        });
        fileNamePatternTextField = new CustomTextField(10);
        ButtonGroup group = new ButtonGroup();
        group.add(fileNameNoChangeRadioButton);
        group.add(fileNameCustomizedRadioButton);

        JPanel folderPathPanel = new JPanel(new GridBagLayout());
        folderPathPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,10,0), I18nUtils.getString("FOLDER")));
        folderPathNoChangeRadioButton = new JRadioButton(I18nUtils.getString("FLAT"));
        folderPathNoChangeRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                folderPathPatternTextField.setEnabled(false);
            }
        });
        folderPathCustomizedRadioButton = new JRadioButton(I18nUtils.getString("CUSTOM"));
        folderPathCustomizedRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                folderPathPatternTextField.setEnabled(true);
            }
        });
        folderPathPatternTextField = new CustomTextField(10);
        ButtonGroup group2 = new ButtonGroup();
        group2.add(folderPathNoChangeRadioButton);
        group2.add(folderPathCustomizedRadioButton);

        JTable availablePatternsTable = lookAndFeelManager.getCurrentLookAndFeel().getTable();
        availablePatternsTable.setModel(new AvailablePatternsDefaultTableModel());

        JPanel patternsPanel = new JPanel(new BorderLayout());
        patternsPanel.setPreferredSize(new Dimension(250, 200));
        patternsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,0,0), I18nUtils.getString("AVAILABLE_PATTERNS")));
        patternsPanel.add(lookAndFeelManager.getCurrentLookAndFeel().getTableScrollPane(availablePatternsTable), BorderLayout.CENTER);

        copySameSongForDifferentAlbums = new JCheckBox(I18nUtils.getString("ALLOW_COPY_TO_DEVICE_SAME_SONG_FOR_DIFFERENT_ALBUMS"));

        arrangePanel(label, fileNamePanel, folderPathPanel, patternsPanel);
    }

	/**
	 * @param label
	 * @param fileNamePanel
	 * @param folderPathPanel
	 * @param patternsPanel
	 */
	private void arrangePanel(JLabel label, JPanel fileNamePanel,
			JPanel folderPathPanel, JPanel patternsPanel) {
		
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
        add(locationFileChooser, c);

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
        add(copySameSongForDifferentAlbums, c);
	}

	/**
	 * @param folderPathPanel
	 */
	private void arrangeFolderPathPanel(JPanel folderPathPanel) {
		GridBagConstraints c;
		c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        folderPathPanel.add(folderPathNoChangeRadioButton, c);
        c.gridx = 0;
        c.gridy = 1;
        folderPathPanel.add(folderPathCustomizedRadioButton, c);
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 20, 0, 20);
        c.fill = GridBagConstraints.HORIZONTAL;
        folderPathPanel.add(folderPathPatternTextField, c);
	}

	/**
	 * @param fileNamePanel
	 */
	private void arrangeFileNamePanel(JPanel fileNamePanel) {
		GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        fileNamePanel.add(fileNameNoChangeRadioButton, c);
        c.gridx = 0;
        c.gridy = 1;
        fileNamePanel.add(fileNameCustomizedRadioButton, c);
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 20, 0, 20);
        c.fill = GridBagConstraints.HORIZONTAL;
        fileNamePanel.add(fileNamePatternTextField, c);
	}

    @Override
    public boolean applyPreferences() {
    	stateDevice.setDefaultDeviceLocation(locationFileChooser.getResult());
        String fileNamePattern = fileNameNoChangeRadioButton.isSelected() ? "" : fileNamePatternTextField.getText();
        stateDevice.setDeviceFileNamePattern(fileNamePattern == null || fileNamePattern.trim().equals("") ? null : fileNamePattern);
        String folderPathPattern = folderPathNoChangeRadioButton.isSelected() ? "" : folderPathPatternTextField.getText();
        stateDevice.setDeviceFolderPathPattern(folderPathPattern == null || folderPathPattern.equals("") ? null : folderPathPattern);
        stateDevice.setAllowRepeatedSongsInDevice(copySameSongForDifferentAlbums.isSelected());
        return false;
    }

    /**
     * Sets the default device location.
     * 
     * @param location
     *            the new default device location
     */
    private void setDefaultDeviceLocation(String location) {
        locationFileChooser.setText(location);
    }

    @Override
    public void updatePanel() {
        setDefaultDeviceLocation(stateDevice.getDefaultDeviceLocation());
        if (stateDevice.getDeviceFileNamePattern() == null || stateDevice.getDeviceFileNamePattern().trim().equals("")) {
            fileNameNoChangeRadioButton.setSelected(true);
            fileNamePatternTextField.setEnabled(false);
        } else {
            fileNameCustomizedRadioButton.setSelected(true);
            fileNamePatternTextField.setEnabled(true);
            fileNamePatternTextField.setText(stateDevice.getDeviceFileNamePattern());
        }
        if (stateDevice.getDeviceFolderPathPattern() == null || stateDevice.getDeviceFolderPathPattern().trim().equals("")) {
            folderPathNoChangeRadioButton.setSelected(true);
            folderPathPatternTextField.setEnabled(false);
        } else {
            folderPathCustomizedRadioButton.setSelected(true);
            folderPathPatternTextField.setEnabled(true);
            folderPathPatternTextField.setText(stateDevice.getDeviceFolderPathPattern());
        }
        copySameSongForDifferentAlbums.setSelected(stateDevice.isAllowRepeatedSongsInDevice());
    }

    @Override
    public void resetImmediateChanges() {
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
