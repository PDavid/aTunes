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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.ScrollableFlowPanel;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * A dialog to show covers
 * 
 * @author alex
 * 
 */
public final class CoverNavigatorDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = -1744765531225480303L;

	private JList list;
	private JPanel coversPanel;
	private JButton coversButton;

	/**
	 * Instantiates a new cover navigator frame.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public CoverNavigatorDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 800, 550, controlsBuilder);
	}

	@Override
	public void initialize() {
		setTitle(I18nUtils.getString("COVER_NAVIGATOR"));
		setContent(getLookAndFeel());
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	/**
	 * Sets the list of artists to show
	 * 
	 * @param artists
	 */
	public void setArtists(final List<IArtist> artists) {
		this.list.setListData(artists.toArray());
	}

	/**
	 * Gets the covers button.
	 * 
	 * @return the coversButton
	 */
	public JButton getCoversButton() {
		return this.coversButton;
	}

	/**
	 * Gets the covers panel.
	 * 
	 * @return the coversPanel
	 */
	public JPanel getCoversPanel() {
		return this.coversPanel;
	}

	/**
	 * Gets the list.
	 * 
	 * @return the list
	 */
	public JList getList() {
		return this.list;
	}

	/**
	 * Sets the content.
	 * 
	 * @param iLookAndFeel
	 */
	private void setContent(final ILookAndFeel iLookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());

		this.coversPanel = new ScrollableFlowPanel();
		this.coversPanel.setOpaque(false);
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEADING);
		this.coversPanel.setLayout(flowLayout);

		this.list = iLookAndFeel.getList();
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScrollPane = getControlsBuilder().createScrollPane(
				this.list);
		listScrollPane.setMinimumSize(new Dimension(200, 0));

		JScrollPane coversScrollPane = getControlsBuilder().createScrollPane(
				this.coversPanel);
		coversScrollPane.setBorder(BorderFactory.createLineBorder(GuiUtils
				.getBorderColor()));
		coversScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		coversScrollPane.getVerticalScrollBar().setUnitIncrement(20);

		this.coversButton = new JButton(I18nUtils.getString("GET_COVERS"));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.weighty = 1;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(listScrollPane, c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.8;
		c.fill = GridBagConstraints.BOTH;
		panel.add(coversScrollPane, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(this.coversButton, c);

		add(panel);
	}

}
