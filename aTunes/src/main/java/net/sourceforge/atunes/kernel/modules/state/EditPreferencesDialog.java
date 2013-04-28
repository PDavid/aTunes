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

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * General characteristics of the preference dialog
 */
public final class EditPreferencesDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = -4759149194433605946L;

	private JButton ok;
	private JButton cancel;
	private JPanel options;
	private JList list;
	private List<AbstractPreferencesPanel> panels;

	/**
	 * Instantiates a new edits the preferences dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public EditPreferencesDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 900, 700, controlsBuilder);
	}

	@Override
	public void initialize() {
		setResizable(true);
		setTitle(I18nUtils.getString("PREFERENCES"));
		setMinimumSize(new Dimension(900, 700));
		add(getContent(getLookAndFeel()));
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

	/**
	 * Gets the cancel.
	 * 
	 * @return the cancel
	 */
	public JButton getCancel() {
		return this.cancel;
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JPanel getContent(final ILookAndFeel lookAndFeel) {
		JPanel container = new JPanel(new GridBagLayout());
		container.setOpaque(false);
		this.list = new JList();
		this.list.setCellRenderer(lookAndFeel
				.getListCellRenderer(new PreferencesListCellRendererCode()));
		JScrollPane scrollPane = getControlsBuilder().createScrollPane(
				this.list);
		scrollPane.setMinimumSize(new Dimension(200, 0));
		this.options = new JPanel();
		this.ok = new JButton(I18nUtils.getString("OK"));
		this.cancel = new JButton(I18nUtils.getString("CANCEL"));
		JPanel auxPanel = new JPanel();
		auxPanel.add(this.ok);
		auxPanel.add(this.cancel);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 0.3;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 0, 5);
		container.add(scrollPane, c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.7;
		c.insets = new Insets(5, 5, 0, 5);
		container.add(this.options, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 0, 10, 10);
		container.add(auxPanel, c);

		return container;
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
	 * Gets the ok.
	 * 
	 * @return the ok
	 */
	public JButton getOk() {
		return this.ok;
	}

	/**
	 * Sets the list model.
	 * 
	 * @param listModel
	 *            the new list model
	 */
	public void setListModel(final ListModel listModel) {
		this.list.setModel(listModel);
	}

	/**
	 * Sets the panels.
	 * 
	 * @param panels
	 *            the new panels
	 */
	public void setPanels(final List<AbstractPreferencesPanel> panels) {
		this.panels = panels;
		this.options.setLayout(new CardLayout());
		for (int i = 0; i < panels.size(); i++) {
			this.options.add(Integer.toString(i), panels.get(i));
			panels.get(i).setDialog(this);
		}
		getControlsBuilder().applyComponentOrientation(this);
	}

	/**
	 * Show panel.
	 * 
	 * @param index
	 *            the index
	 */
	public void showPanel(final int index) {
		((CardLayout) this.options.getLayout()).show(this.options,
				Integer.toString(index));
		// Mark panel as dirty
		this.panels.get(index).setDirty(true);
	}

	/**
	 * Marks panels as not dirty
	 */
	public void resetPanels() {
		for (AbstractPreferencesPanel panel : this.panels) {
			panel.setDirty(false);
		}
	}
}
