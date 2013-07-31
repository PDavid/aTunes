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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The search results dialog.
 */
public final class SearchResultsDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = 7219089044964361102L;

	/** Table to show search results. */
	private JTable searchResultsTable;

	/** Button to show element info. */
	private JButton showElementInfo;

	/** Button to add selected results to current play list. */
	private JButton addToCurrentPlayList;

	/** Button to add selected results to a new play list. */
	private JButton addToNewPlayList;

	/** Button create a new dynamic play list */
	private JButton createDynamicPlayList;

	/**
	 * Instantiates a new search results dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public SearchResultsDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 800, 600, controlsBuilder);
	}

	@Override
	public void initialize() {
		setResizable(true);
		setTitle(I18nUtils.getString("SEARCH_RESULTS"));
		add(getContent(getLookAndFeel()));
	}

	/**
	 * Gets the content.
	 * 
	 * @param iLookAndFeel
	 * 
	 * @return the content
	 */
	private JPanel getContent(final ILookAndFeel iLookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());
		this.searchResultsTable = iLookAndFeel.getTable();
		// Disable autoresize, as we will control it
		this.searchResultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(getControlsBuilder()
				.createScrollPane(this.searchResultsTable), c);

		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 1;
		panel.add(getButtonsPanel(), c);

		return panel;
	}

	/**
	 * Gets the buttons panel.
	 * 
	 * @return the buttons panel
	 */
	private JPanel getButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new GridBagLayout());
		this.showElementInfo = new JButton(I18nUtils.getString("INFO"));
		this.addToCurrentPlayList = new JButton(
				I18nUtils.getString("ADD_TO_PLAYLIST"));
		this.addToNewPlayList = new JButton(
				I18nUtils.getString("ADD_TO_NEW_PLAYLIST"));
		this.createDynamicPlayList = new JButton(
				I18nUtils.getString("CREATE_DYNAMIC_PLAYLIST"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		buttonsPanel.add(this.showElementInfo, c);
		c.gridx = 1;
		buttonsPanel.add(this.addToCurrentPlayList, c);
		c.gridx = 2;
		buttonsPanel.add(this.addToNewPlayList, c);
		c.gridx = 3;
		buttonsPanel.add(this.createDynamicPlayList, c);
		return buttonsPanel;
	}

	/**
	 * Gets the search results table.
	 * 
	 * @return the searchResultsTable
	 */
	public JTable getSearchResultsTable() {
		return this.searchResultsTable;
	}

	/**
	 * Gets the show element info.
	 * 
	 * @return the showElementInfo
	 */
	public JButton getShowElementInfo() {
		return this.showElementInfo;
	}

	/**
	 * Gets the add to current play list.
	 * 
	 * @return the addToCurrentPlayList
	 */
	public JButton getAddToCurrentPlayList() {
		return this.addToCurrentPlayList;
	}

	/**
	 * Gets the add to new play list.
	 * 
	 * @return the addToNewPlayList
	 */
	public JButton getAddToNewPlayList() {
		return this.addToNewPlayList;
	}

	/**
	 * @return the createDynamicPlayList
	 */
	public JButton getCreateDynamicPlayList() {
		return this.createDynamicPlayList;
	}

}
