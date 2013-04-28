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

package net.sourceforge.atunes.kernel.modules.os;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomFileChooser;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Dialog to select player engine in OS X
 * 
 * @author alex
 * 
 */
public class MacOSXPlayerSelectionDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = -1119645857786634652L;

	private static final String SEARCH_PANEL = "searchPanel";
	private static final String FIRST_PANEL = "firstPanel";
	private static final String SEARCH_RESULTS_PANEL = "searchResultsPanel";
	private static final String ENTER_PATH_PANEL = "enterPathPanel";

	/**
	 * URL to MPlayerX in Mac App Store
	 */
	static final String MPLAYER_APP_STORE_URL = "http://itunes.apple.com/en/app/mplayerx/id421131143?mt=12";

	private IOSManager osManager;

	private JPanel panelContainer;

	private JList matchesList;

	private JRadioButton automaticSearch;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param parent
	 * @param controlsBuilder
	 */
	public MacOSXPlayerSelectionDialog(final IFrame parent,
			final IControlsBuilder controlsBuilder) {
		super(parent, 450, 250, controlsBuilder);
	}

	@Override
	public void initialize() {
		setResizable(false);
		setTitle(I18nUtils.getString("PLAYER_ENGINE_SELECTION"));

		this.panelContainer = new JPanel(new CardLayout());
		this.panelContainer.add(FIRST_PANEL, getFirstOptionsPanel());
		this.panelContainer.add(SEARCH_PANEL, getSearchPanel());
		this.panelContainer.add(SEARCH_RESULTS_PANEL, getSearchResultsPanel());
		this.panelContainer.add(ENTER_PATH_PANEL, getEnterPlayerEnginePanel());
		add(this.panelContainer);

		((CardLayout) this.panelContainer.getLayout()).show(
				this.panelContainer, FIRST_PANEL);
	}

	/**
	 * Set first options panel
	 */
	private JPanel getFirstOptionsPanel() {
		JTextPane instructions = getControlsBuilder().createReadOnlyTextPane(
				I18nUtils.getString("MAC_PLAYER_ENGINE_INSTRUCTIONS"));
		UrlLabel appStoreURL = (UrlLabel) getControlsBuilder().getUrlLabel(
				I18nUtils.getString("MAC_PLAYER_ENGINE_URL"),
				MPLAYER_APP_STORE_URL);
		this.automaticSearch = new JRadioButton(
				I18nUtils.getString("SEARCH_PLAYER_ENGINE"));
		JRadioButton enterPath = new JRadioButton(
				I18nUtils.getString("ENTER_PLAYER_ENGINE_PATH"));
		ButtonGroup b = new ButtonGroup();
		b.add(this.automaticSearch);
		b.add(enterPath);
		this.automaticSearch.setSelected(true);
		JButton nextButton = new JButton(I18nUtils.getString("NEXT"));
		nextButton
				.addActionListener(new MacOSXPlayerSelectionDialogNextButtonListener(
						this, ENTER_PATH_PANEL));

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0.3;
		c.fill = GridBagConstraints.BOTH;
		panel.add(instructions, c);
		c.gridy = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(appStoreURL, c);
		JPanel options = new JPanel(new GridLayout(2, 1));
		options.add(this.automaticSearch);
		options.add(enterPath);
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(options, c);
		c.gridy = 3;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		panel.add(nextButton, c);

		return panel;
	}

	/**
	 * Panel shown while searching
	 * 
	 * @return
	 */
	private JPanel getSearchPanel() {
		JLabel label = new JLabel(
				I18nUtils.getString("SEARCHING_PLAYER_ENGINE"));
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(label, c);
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTH;
		panel.add(bar, c);

		return panel;
	}

	/**
	 * Looks for paths where mplayer is installed and show in a dialog
	 */
	void searchPlayerEngine() {
		((CardLayout) this.panelContainer.getLayout()).show(
				this.panelContainer, SEARCH_PANEL);
		MacOSXPlayerEngineSelectionDialogSearchPlayerEngineBackgroundWorker worker = this.beanFactory
				.getBean(MacOSXPlayerEngineSelectionDialogSearchPlayerEngineBackgroundWorker.class);
		worker.setDialog(this);
		worker.execute();
	}

	/**
	 * Shows a panel with search results
	 * 
	 * @param results
	 */
	void showSearchResults(final List<String> results) {
		this.matchesList.setListData(results.toArray());
		((CardLayout) this.panelContainer.getLayout()).show(
				this.panelContainer, SEARCH_RESULTS_PANEL);
	}

	/**
	 * Panel with search results
	 * 
	 * @return
	 */
	private JPanel getSearchResultsPanel() {
		JTextPane instructions = getControlsBuilder().createReadOnlyTextPane(
				I18nUtils.getString("MAC_PLAYER_ENGINE_SELECTION"));

		this.matchesList = getLookAndFeel().getList();
		this.matchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JButton previousButton = new JButton(I18nUtils.getString("PREVIOUS"));
		final JButton finishButton = new JButton(I18nUtils.getString("FINISH"));
		finishButton.setEnabled(false);

		finishButton
				.addActionListener(new MacOSXPlayerSelectionDialogSearchResultsFinishButtonListener(
						this, this.osManager, this.matchesList));
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton
				.addActionListener(new MacOSXPlayerSelectionDialogCancelButtonActionListener(
						this));
		this.matchesList
				.addListSelectionListener(new MacOSXPlayerSelectionDialogEnableFinishButtonListener(
						finishButton));
		previousButton
				.addActionListener(new MacOSXPlayerSelectionDialogGoToFirstPanelListener(
						this.panelContainer, FIRST_PANEL));

		JPanel buttons = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(10, 5, 0, 5);
		buttons.add(previousButton, c);
		c.gridx = 1;
		c.weightx = 0;
		buttons.add(finishButton, c);
		c.gridx = 2;
		c.insets = new Insets(10, 5, 0, 0);
		buttons.add(cancelButton, c);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0.2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(instructions, c);
		c.gridy = 1;
		c.weighty = 0.8;
		panel.add(getControlsBuilder().createScrollPane(this.matchesList), c);
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(buttons, c);

		return panel;
	}

	/**
	 * Panel to enter player engine manually
	 * 
	 * @return
	 */
	private JPanel getEnterPlayerEnginePanel() {
		JTextPane instructions = getControlsBuilder().createReadOnlyTextPane(
				I18nUtils.getString("MAC_PLAYER_ENGINE_ENTER_PATH"));
		final CustomFileChooser locationFileChooser = new CustomFileChooser(
				I18nUtils.getString("ENTER_PLAYER_ENGINE_PATH"), this, 0,
				JFileChooser.FILES_ONLY, this.osManager, this.beanFactory,
				getControlsBuilder());
		JButton previousButton = new JButton(I18nUtils.getString("PREVIOUS"));
		previousButton
				.addActionListener(new MacOSXPlayerSelectionDialogGoToFirstPanelListener(
						this.panelContainer, FIRST_PANEL));
		final JButton finishButton = new JButton(I18nUtils.getString("FINISH"));
		finishButton.setEnabled(false);
		finishButton
				.addActionListener(new MacOSXPlayerSelectionDialogEnterPlayerEngineFinishButtonListener(
						this, this.osManager, locationFileChooser));
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton
				.addActionListener(new MacOSXPlayerSelectionDialogCancelButtonActionListener(
						this));
		locationFileChooser
				.addDocumentListener(new MacOSXPlayerSelectionDialogLocationFileChooserDocumentListener(
						finishButton, locationFileChooser));

		JPanel buttons = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(10, 5, 0, 5);
		buttons.add(previousButton, c);
		c.gridx = 1;
		c.weightx = 0;
		buttons.add(finishButton, c);
		c.gridx = 2;
		c.insets = new Insets(10, 5, 0, 0);
		buttons.add(cancelButton, c);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		panel.add(instructions, c);
		c.gridy = 1;
		panel.add(locationFileChooser, c);
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(buttons, c);

		return panel;
	}

	/**
	 * Returns if automatic player engine selection is selected
	 * 
	 * @return
	 */
	protected boolean automaticSearchSelected() {
		return this.automaticSearch.isSelected();
	}

	/**
	 * Returns panel container
	 */
	protected JPanel getPanelContainer() {
		return this.panelContainer;
	}
}
