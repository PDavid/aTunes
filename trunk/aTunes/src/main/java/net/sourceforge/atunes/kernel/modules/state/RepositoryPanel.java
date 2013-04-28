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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.model.IControlsBuilder;
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

	private IControlsBuilder controlsBuilder;

	private JCheckBox useRatingsStoredInTag;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

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
		this.refreshTime = new JComboBox(new Integer[] { 0, 5, 10, 15, 30, 60 });
		this.commandBeforeAccessRepository = this.controlsBuilder
				.createTextField();
		this.commandBeforeAccessRepository.setColumns(20);
		this.commandAfterAccessRepository = this.controlsBuilder
				.createTextField();
		this.commandAfterAccessRepository.setColumns(20);
		this.repositoryFoldersList = this.lookAndFeelManager
				.getCurrentLookAndFeel().getList();
		this.repositoryFoldersList
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addFolderButton = new JButton(I18nUtils.getString("ADD"));
		this.removeFolderButton = new JButton(I18nUtils.getString("REMOVE"));
		this.repositoryFoldersList
				.addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(final ListSelectionEvent event) {
						RepositoryPanel.this.removeFolderButton
								.setEnabled(RepositoryPanel.this.repositoryFoldersList
										.getSelectedIndex() != -1);
					}
				});
		this.addFolderButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				IFolderSelectorDialog dialog = RepositoryPanel.this.dialogFactory
						.newDialog(IFolderSelectorDialog.class);
				dialog.setTitle(I18nUtils.getString("ADD_FOLDER_TO_REPOSITORY"));
				File folder = dialog
						.selectFolder(RepositoryPanel.this.osManager
								.getUserHome());
				if (folder != null) {
					RepositoryPanel.this.repositoryFolders.add(folder);
					RepositoryPanel.this.repositoryFoldersList
							.setListData(RepositoryPanel.this.repositoryFolders
									.toArray());
				}
			}
		});
		this.removeFolderButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				RepositoryPanel.this.repositoryFolders
						.remove(RepositoryPanel.this.repositoryFoldersList
								.getSelectedIndex());
				RepositoryPanel.this.repositoryFoldersList
						.setListData(RepositoryPanel.this.repositoryFolders
								.toArray());
			}
		});
		this.useRatingsStoredInTag = new JCheckBox(
				I18nUtils.getString("USE_RATINGS_STORED_IN_TAG"));
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
		add(this.refreshTime, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 0, 0);
		add(new JLabel(I18nUtils.getString("COMMAND_BEFORE_REPOSITORY_ACCESS")),
				c);
		c.gridx = 1;
		c.weightx = 1;
		add(this.commandBeforeAccessRepository, c);
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		add(new JLabel(I18nUtils.getString("COMMAND_AFTER_REPOSITORY_ACCESS")),
				c);
		c.gridx = 1;
		c.weightx = 1;
		add(this.commandAfterAccessRepository, c);
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(20, 10, 0, 0);
		JScrollPane scrollPane = this.controlsBuilder
				.createScrollPane(this.repositoryFoldersList);
		scrollPane.setMinimumSize(new Dimension(400, 300));
		scrollPane.setPreferredSize(new Dimension(400, 300));
		add(scrollPane, c);
		JPanel addRemovePanel = new JPanel(new GridLayout(1, 2, 5, 0));
		addRemovePanel.add(this.addFolderButton);
		addRemovePanel.add(this.removeFolderButton);
		c.gridy = 4;
		c.insets = new Insets(10, 10, 0, 0);
		add(addRemovePanel, c);
		c.gridx = 0;
		c.gridy = 5;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		add(this.useRatingsStoredInTag, c);
	}

	@Override
	public boolean applyPreferences() {
		this.stateRepository
				.setAutoRepositoryRefreshTime((Integer) this.refreshTime
						.getSelectedItem());
		this.stateRepository
				.setCommandBeforeAccessRepository(this.commandBeforeAccessRepository
						.getText());
		this.stateRepository
				.setCommandAfterAccessRepository(this.commandAfterAccessRepository
						.getText());
		if (repositoryFoldersHaveChanged(this.repositoryFolders,
				this.repositoryHandler.getFolders())) {
			this.repositoryHandler.setRepositoryFolders(this.repositoryFolders);
		}

		this.stateRepository.setStoreRatingInFile(this.useRatingsStoredInTag
				.isSelected());
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
		this.refreshTime.setSelectedItem(time);
	}

	/**
	 * Sets the command to execute before access repository
	 * 
	 * @param command
	 */
	private void setCommandBeforeAccessRepository(final String command) {
		this.commandBeforeAccessRepository.setText(command);
	}

	/**
	 * Sets the command to execute after access repository
	 * 
	 * @param command
	 */
	private void setCommandAfterAccessRepository(final String command) {
		this.commandAfterAccessRepository.setText(command);
	}

	private void setUseRatingsStoredInTag(final boolean storeRatingInFile) {
		this.useRatingsStoredInTag.setSelected(storeRatingInFile);
	}

	@Override
	public void updatePanel() {
		setRefreshTime(this.stateRepository.getAutoRepositoryRefreshTime());
		setCommandBeforeAccessRepository(this.stateRepository
				.getCommandBeforeAccessRepository());
		setCommandAfterAccessRepository(this.stateRepository
				.getCommandAfterAccessRepository());
		setRepositoryFolders();
		setUseRatingsStoredInTag(this.stateRepository.isStoreRatingInFile());
	}

	private void setRepositoryFolders() {
		this.repositoryFolders = new ArrayList<File>(
				this.repositoryHandler.getFolders());
		this.repositoryFoldersList
				.setListData(this.repositoryFolders.toArray());
		this.removeFolderButton.setEnabled(false);
	}

	@Override
	public void validatePanel() throws PreferencesValidationException {
		if (CollectionUtils.isEmpty(this.repositoryFolders)) {
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
