/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.CustomJFileChooser;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

public final class DevicePanel extends PreferencesPanel {

    private static class AvailablePatternsDefaultTableModel extends
			DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3054134384773947174L;

		@Override
		public int getRowCount() {
		    return AbstractPattern.getPatterns().size();
		}

		@Override
		public int getColumnCount() {
		    return 2;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
		    return false;
		}

		@Override
		public Object getValueAt(int row, int column) {
		    if (column == 0) {
		        return AbstractPattern.getPatterns().get(row).getPattern();
		    }
		    return AbstractPattern.getPatterns().get(row).getDescription();
		}

		@Override
		public String getColumnName(int column) {
		    return column == 0 ? I18nUtils.getString("PATTERN") : I18nUtils.getString("VALUE");
		}
	}

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
    JTextField fileNamePatternTextField;

    /**
     * The radio button used to select no changes in folder name when copying to
     * device
     */
    private JRadioButton folderPathNoChangeRadioButton;

    /** The radio button used to set folder when copying to device */
    private JRadioButton folderPathCustomizedRadioButton;

    /** Text Field used to define custom folder path */
    JTextField folderPathPatternTextField;

    /** A table to show available pattern transformations */
    private JTable availablePatternsTable;

    /**
     * A check box to set if user wants to copy the same song several times (if
     * song is repeated for different albums)
     */
    private JCheckBox copySameSongForDifferentAlbums;

    /**
     * Instantiates a new device panel.
     */
    public DevicePanel() {
        super(I18nUtils.getString("DEVICE"));
        JLabel label = new JLabel(I18nUtils.getString("DEVICE_DEFAULT_LOCATION"));
        locationFileChooser = new CustomJFileChooser(this, 20, JFileChooser.DIRECTORIES_ONLY);

        JPanel fileNamePanel = new JPanel(new GridBagLayout());
        fileNamePanel.setBorder(BorderFactory.createTitledBorder(I18nUtils.getString("FILE_NAME")));
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
        folderPathPanel.setBorder(BorderFactory.createTitledBorder(I18nUtils.getString("FOLDER")));
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

        availablePatternsTable = new JTable();
        availablePatternsTable.setModel(new AvailablePatternsDefaultTableModel());

        JPanel patternsPanel = new JPanel(new BorderLayout());
        patternsPanel.setPreferredSize(new Dimension(250, 200));
        patternsPanel.setBorder(BorderFactory.createTitledBorder(I18nUtils.getString("AVAILABLE_PATTERNS")));
        patternsPanel.add(new JScrollPane(availablePatternsTable), BorderLayout.CENTER);

        copySameSongForDifferentAlbums = new JCheckBox(I18nUtils.getString("ALLOW_COPY_TO_DEVICE_SAME_SONG_FOR_DIFFERENT_ALBUMS"));

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

    @Override
    public boolean applyPreferences(ApplicationState state) {
        state.setDefaultDeviceLocation(locationFileChooser.getResult());
        String fileNamePattern = fileNameNoChangeRadioButton.isSelected() ? "" : fileNamePatternTextField.getText();
        state.setDeviceFileNamePattern(fileNamePattern == null || fileNamePattern.trim().equals("") ? null : fileNamePattern);
        String folderPathPattern = folderPathNoChangeRadioButton.isSelected() ? "" : folderPathPatternTextField.getText();
        state.setDeviceFolderPathPattern(folderPathPattern == null || folderPathPattern.equals("") ? null : folderPathPattern);
        state.setAllowRepeatedSongsInDevice(copySameSongForDifferentAlbums.isSelected());
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
    public void updatePanel(ApplicationState state) {
        setDefaultDeviceLocation(state.getDefaultDeviceLocation());
        if (state.getDeviceFileNamePattern() == null || state.getDeviceFileNamePattern().trim().equals("")) {
            fileNameNoChangeRadioButton.setSelected(true);
            fileNamePatternTextField.setEnabled(false);
        } else {
            fileNameCustomizedRadioButton.setSelected(true);
            fileNamePatternTextField.setEnabled(true);
            fileNamePatternTextField.setText(state.getDeviceFileNamePattern());
        }
        if (state.getDeviceFolderPathPattern() == null || state.getDeviceFolderPathPattern().trim().equals("")) {
            folderPathNoChangeRadioButton.setSelected(true);
            folderPathPatternTextField.setEnabled(false);
        } else {
            folderPathCustomizedRadioButton.setSelected(true);
            folderPathPatternTextField.setEnabled(true);
            folderPathPatternTextField.setText(state.getDeviceFolderPathPattern());
        }
        copySameSongForDifferentAlbums.setSelected(state.isAllowRepeatedSongsInDevice());
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
        return Images.getImage(Images.DEVICE);
    }

}
