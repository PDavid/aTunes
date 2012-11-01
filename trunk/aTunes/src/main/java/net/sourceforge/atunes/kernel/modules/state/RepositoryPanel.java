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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The preferences panel for respository settings.
 */
public final class RepositoryPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = 3331810461314007217L;

    /**
     * The refresh time of repository
     */
    private JComboBox refreshTime;

    /**
     * Text field to specify command to execute before access repository
     */
    private JTextField commandBeforeAccessRepository;

    /**
     * Text field to specify command to execute after access repository (when
     * application finish)
     */
    private JTextField commandAfterAccessRepository;

    private JList repositoryFoldersList;

    private IStateRepository stateRepository;

    private ILookAndFeelManager lookAndFeelManager;

    private IRepositoryHandler repositoryHandler;

    private JButton addFolderButton;

    private IDialogFactory dialogFactory;

    private JButton removeFolderButton;

    private List<File> repositoryFolders;

    private IOSManager osManager;

    /**
     * @param osManager
     */
    public void setOsManager(final IOSManager osManager) {
	this.osManager = osManager;
    }

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }

    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
	this.repositoryHandler = repositoryHandler;
    }

    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(
	    final ILookAndFeelManager lookAndFeelManager) {
	this.lookAndFeelManager = lookAndFeelManager;
    }

    /**
     * @param stateRepository
     */
    public void setStateRepository(final IStateRepository stateRepository) {
	this.stateRepository = stateRepository;
    }

    /**
     * Instantiates a new repository panel.
     */
    public RepositoryPanel() {
	super(I18nUtils.getString("REPOSITORY"));
    }

    /**
     * Initializes panel
     */
    public void initialize() {
	refreshTime = new JComboBox(new Integer[] { 0, 5, 10, 15, 30, 60 });
	commandBeforeAccessRepository = new CustomTextField(20);
	commandAfterAccessRepository = new CustomTextField(20);
	repositoryFoldersList = lookAndFeelManager.getCurrentLookAndFeel()
		.getList();
	repositoryFoldersList
		.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	addFolderButton = new JButton(I18nUtils.getString("ADD"));
	removeFolderButton = new JButton(I18nUtils.getString("REMOVE"));
	repositoryFoldersList
		.addListSelectionListener(new ListSelectionListener() {

		    @Override
		    public void valueChanged(final ListSelectionEvent event) {
			removeFolderButton.setEnabled(repositoryFoldersList
				.getSelectedIndex() != -1);
		    }
		});
	addFolderButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(final ActionEvent arg0) {
		IFolderSelectorDialog dialog = dialogFactory
			.newDialog(IFolderSelectorDialog.class);
		dialog.setTitle(I18nUtils.getString("ADD_FOLDER_TO_REPOSITORY"));
		File folder = dialog.selectFolder(osManager.getUserHome());
		if (folder != null) {
		    repositoryFolders.add(folder);
		    repositoryFoldersList.setListData(repositoryFolders
			    .toArray());
		}
	    }
	});
	removeFolderButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(final ActionEvent event) {
		repositoryFolders.remove(repositoryFoldersList
			.getSelectedIndex());
		repositoryFoldersList.setListData(repositoryFolders.toArray());
	    }
	});
	setupPanel();
    }

    /**
     * Add components to panel
     */
    private void setupPanel() {
	GridBagConstraints c = new GridBagConstraints();
	c.gridx = 0;
	c.gridy = 0;
	c.weightx = 0;
	c.anchor = GridBagConstraints.WEST;
	c.insets = new Insets(0, 10, 0, 0);
	add(new JLabel(I18nUtils.getString("REPOSITORY_REFRESH_TIME")), c);
	c.gridx = 1;
	c.weightx = 1;
	c.insets = new Insets(0, 10, 0, 0);
	add(refreshTime, c);
	c.gridx = 0;
	c.gridy = 1;
	c.weightx = 1;
	c.insets = new Insets(10, 10, 0, 0);
	add(new JLabel(I18nUtils.getString("COMMAND_BEFORE_REPOSITORY_ACCESS")),
		c);
	c.gridx = 1;
	c.weightx = 1;
	add(commandBeforeAccessRepository, c);
	c.gridx = 0;
	c.gridy = 2;
	c.weightx = 0;
	add(new JLabel(I18nUtils.getString("COMMAND_AFTER_REPOSITORY_ACCESS")),
		c);
	c.gridx = 1;
	c.weightx = 1;
	add(commandAfterAccessRepository, c);
	c.gridx = 0;
	c.gridy = 3;
	c.insets = new Insets(20, 10, 0, 0);
	JScrollPane scrollPane = lookAndFeelManager.getCurrentLookAndFeel()
		.getScrollPane(repositoryFoldersList);
	scrollPane.setMinimumSize(new Dimension(400, 300));
	scrollPane.setPreferredSize(new Dimension(400, 300));
	add(scrollPane, c);
	JPanel addRemovePanel = new JPanel(new GridLayout(1, 2, 5, 0));
	addRemovePanel.add(addFolderButton);
	addRemovePanel.add(removeFolderButton);
	c.gridy = 4;
	c.weighty = 1;
	c.anchor = GridBagConstraints.NORTHWEST;
	c.insets = new Insets(10, 10, 0, 0);
	add(addRemovePanel, c);
    }

    @Override
    public boolean applyPreferences() {
	stateRepository.setAutoRepositoryRefreshTime((Integer) refreshTime
		.getSelectedItem());
	stateRepository
		.setCommandBeforeAccessRepository(commandBeforeAccessRepository
			.getText());
	stateRepository
		.setCommandAfterAccessRepository(commandAfterAccessRepository
			.getText());
	if (repositoryFoldersHaveChanged(repositoryFolders,
		repositoryHandler.getFolders())) {
	    repositoryHandler.setRepositoryFolders(repositoryFolders);
	}
	return false;
    }

    private boolean repositoryFoldersHaveChanged(
	    final List<File> filesSelected, final List<File> repositoryFolders) {
	return !org.apache.commons.collections.CollectionUtils
		.isEqualCollection(filesSelected, repositoryFolders);
    }

    /**
     * Sets the refresh time.
     * 
     * @param time
     *            the new refresh time
     */
    private void setRefreshTime(final int time) {
	refreshTime.setSelectedItem(time);
    }

    /**
     * Sets the command to execute before access repository
     * 
     * @param command
     */
    private void setCommandBeforeAccessRepository(final String command) {
	commandBeforeAccessRepository.setText(command);
    }

    /**
     * Sets the command to execute after access repository
     * 
     * @param command
     */
    private void setCommandAfterAccessRepository(final String command) {
	commandAfterAccessRepository.setText(command);
    }

    @Override
    public void updatePanel() {
	setRefreshTime(stateRepository.getAutoRepositoryRefreshTime());
	setCommandBeforeAccessRepository(stateRepository
		.getCommandBeforeAccessRepository());
	setCommandAfterAccessRepository(stateRepository
		.getCommandAfterAccessRepository());
	setRepositoryFolders();
    }

    private void setRepositoryFolders() {
	repositoryFolders = new ArrayList<File>(repositoryHandler.getFolders());
	repositoryFoldersList.setListData(repositoryFolders.toArray());
	removeFolderButton.setEnabled(false);
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
	if (CollectionUtils.isEmpty(repositoryFolders)) {
	    throw new PreferencesValidationException(
		    I18nUtils
			    .getString("REPOSITORY_MUST_CONTAIN_AT_LEAST_ONE_FOLDER"),
		    null);
	}
    }

    @Override
    public void dialogVisibilityChanged(final boolean visible) {
	// Do nothing
    }

    @Override
    public void resetImmediateChanges() {
	// Do nothing
    }
}
