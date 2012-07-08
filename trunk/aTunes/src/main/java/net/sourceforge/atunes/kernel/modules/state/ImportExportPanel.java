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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class ImportExportPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = 3331810461314007217L;

    /**
     * The radio button used to select no changes in file names when importing /
     * exporting
     */
    private JRadioButton fileNameNoChangeRadioButton;

    /**
     * The radio button used to rename files when importing / exporting to a
     * custom name
     */
    private JRadioButton fileNameCustomizedRadioButton;

    /** Text Field used to define custom file name pattern */
    private JTextField fileNamePatternTextField;

    /**
     * The radio button used to select no changes in folder name when importing
     * / exporting
     */
    private JRadioButton folderPathNoChangeRadioButton;

    /**
     * The radio button used to set folder when importing / exporting to a
     * custom pattern
     */
    private JRadioButton folderPathCustomizedRadioButton;

    /** Text Field used to define custom folder path */
    private JTextField folderPathPatternTextField;

    /** Check box to enable tag revision before importing */
    private JCheckBox reviewTagsBeforeImportCheckBox;

    /** Check box to apply changes in tags to original files before copy them */
    private JCheckBox applyChangesToSourceFilesCheckBox;

    /** Check box to enable track number auto complete when importing */
    private JCheckBox setTrackNumberWhenImportingCheckBox;

    /** Check box to enable title auto complete when importing */
    private JCheckBox setTitlesWhenImportingCheckBox;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private IStateRepository stateRepository;
    
    private Patterns patterns;
    
    /**
     * @param patterns
     */
    public void setPatterns(Patterns patterns) {
		this.patterns = patterns;
	}
    
    /**
     * @param stateRepository
     */
    public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

    /**
     * Instantiates a new import / export panel.
     */
    public ImportExportPanel() {
        super(StringUtils.getString(I18nUtils.getString("IMPORT"), "/", I18nUtils.getString("EXPORT")));
    }
    
    /**
     * Initializes panel
     */
    public void initialize() {
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
        folderPathNoChangeRadioButton = new JRadioButton(I18nUtils.getString("NO_CHANGE"));
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
        availablePatternsTable.setModel(new AvailablePatternsTableModel(patterns.getPatternsList()));

        JPanel patternsPanel = getPatternsPanel(availablePatternsTable);

        reviewTagsBeforeImportCheckBox = new JCheckBox(I18nUtils.getString("REVIEW_TAGS_BEFORE_IMPORTING"));
        reviewTagsBeforeImportCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyChangesToSourceFilesCheckBox.setEnabled(((JCheckBox) e.getSource()).isSelected());
            }
        });
        applyChangesToSourceFilesCheckBox = new JCheckBox(I18nUtils.getString("APPLY_CHANGES_TO_SOURCE_FILES"));
        setTrackNumberWhenImportingCheckBox = new JCheckBox(I18nUtils.getString("AUTO_SET_TRACK_NUMBER"));
        setTitlesWhenImportingCheckBox = new JCheckBox(I18nUtils.getString("AUTO_SET_TITLE"));

        
        JPanel specificImportOptions = getSpecificImportOptionsPanel();

        arrangePanel(fileNamePanel, folderPathPanel, patternsPanel, specificImportOptions);
    }

	/**
	 * @param availablePatternsTable
	 * @return
	 */
	private JPanel getPatternsPanel(JTable availablePatternsTable) {
		JPanel patternsPanel = new JPanel(new BorderLayout());
        patternsPanel.setPreferredSize(new Dimension(250, 200));
        patternsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,0,0), I18nUtils.getString("AVAILABLE_PATTERNS")));
        patternsPanel.add(lookAndFeelManager.getCurrentLookAndFeel().getTableScrollPane(availablePatternsTable), BorderLayout.CENTER);
		return patternsPanel;
	}

	/**
	 * @return
	 */
	private JPanel getSpecificImportOptionsPanel() {
		JPanel specificImportOptions = new JPanel(new GridLayout(4, 1));
        specificImportOptions.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,10,0), I18nUtils.getString("SPECIFIC_IMPORT_OPTIONS")));
        specificImportOptions.add(reviewTagsBeforeImportCheckBox);
        specificImportOptions.add(applyChangesToSourceFilesCheckBox);
        specificImportOptions.add(setTrackNumberWhenImportingCheckBox);
        specificImportOptions.add(setTitlesWhenImportingCheckBox);
		return specificImportOptions;
	}

	/**
	 * @param fileNamePanel
	 * @param folderPathPanel
	 * @param patternsPanel
	 * @param specificImportOptions
	 */
	private void arrangePanel(JPanel fileNamePanel, JPanel folderPathPanel,
			JPanel patternsPanel, JPanel specificImportOptions) {
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
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(fileNamePanel, c);

        c.gridx = 1;
        c.gridheight = 3;
        c.weightx = 0;
        c.anchor = GridBagConstraints.NORTH;
        add(patternsPanel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.gridheight = 1;
        add(folderPathPanel, c);

        c.gridy = 2;
        c.weighty = 1;
        add(specificImportOptions, c);
	}

    @Override
    public boolean applyPreferences() {
        String fileNamePattern = fileNameNoChangeRadioButton.isSelected() ? "" : fileNamePatternTextField.getText();
        stateRepository.setImportExportFileNamePattern(fileNamePattern == null || fileNamePattern.trim().equals("") ? null : fileNamePattern);
        String folderPathPattern = folderPathNoChangeRadioButton.isSelected() ? "" : folderPathPatternTextField.getText();
        stateRepository.setImportExportFolderPathPattern(folderPathPattern == null || folderPathPattern.equals("") ? null : folderPathPattern);
        stateRepository.setReviewTagsBeforeImport(reviewTagsBeforeImportCheckBox.isSelected());
        stateRepository.setApplyChangesToSourceFilesBeforeImport(applyChangesToSourceFilesCheckBox.isSelected());
        stateRepository.setSetTrackNumbersWhenImporting(setTrackNumberWhenImportingCheckBox.isSelected());
        stateRepository.setSetTitlesWhenImporting(setTitlesWhenImportingCheckBox.isSelected());
        return false;
    }

    @Override
    public void updatePanel() {
        if (stateRepository.getImportExportFileNamePattern() == null || stateRepository.getImportExportFileNamePattern().trim().equals("")) {
            fileNameNoChangeRadioButton.setSelected(true);
            fileNamePatternTextField.setEnabled(false);
        } else {
            fileNameCustomizedRadioButton.setSelected(true);
            fileNamePatternTextField.setEnabled(true);
            fileNamePatternTextField.setText(stateRepository.getImportExportFileNamePattern());
        }
        if (stateRepository.getImportExportFolderPathPattern() == null || stateRepository.getImportExportFolderPathPattern().trim().equals("")) {
            folderPathNoChangeRadioButton.setSelected(true);
            folderPathPatternTextField.setEnabled(false);
        } else {
            folderPathCustomizedRadioButton.setSelected(true);
            folderPathPatternTextField.setEnabled(true);
            folderPathPatternTextField.setText(stateRepository.getImportExportFolderPathPattern());
        }
        reviewTagsBeforeImportCheckBox.setSelected(stateRepository.isReviewTagsBeforeImport());
        applyChangesToSourceFilesCheckBox.setEnabled(stateRepository.isReviewTagsBeforeImport());
        applyChangesToSourceFilesCheckBox.setSelected(stateRepository.isApplyChangesToSourceFilesBeforeImport());
        setTrackNumberWhenImportingCheckBox.setSelected(stateRepository.isSetTrackNumbersWhenImporting());
        setTitlesWhenImportingCheckBox.setSelected(stateRepository.isSetTitlesWhenImporting());
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
