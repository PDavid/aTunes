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

package net.sourceforge.atunes.kernel.modules.radio;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class RadioBrowserDialog.
 */
public final class RadioBrowserDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = 8523236886848649698L;

	@SuppressWarnings("rawtypes")
	private JList list;

	private JTable table;

	private IIconFactory radioMediumIcon;

	/**
	 * Instantiates a new radio browser dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public RadioBrowserDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 800, 600, controlsBuilder);
		setResizable(false);
	}

	/**
	 * @param radioMediumIcon
	 */
	public void setRadioMediumIcon(final IIconFactory radioMediumIcon) {
		this.radioMediumIcon = radioMediumIcon;
	}

	@Override
	public void initialize() {
		setTitle(I18nUtils.getString("RADIO_BROWSER"));
		setContent();
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
	 * Sets the content.
	 */
	@SuppressWarnings("rawtypes")
	private void setContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		this.list = new JList();
		this.list.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		this.table = getControlsBuilder().createTable();
		this.table.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		JPanel topPanel = new JPanel(new BorderLayout(10, 0));
		JLabel radioIcon = new JLabel(
				this.radioMediumIcon.getIcon(getLookAndFeel()
						.getPaintForSpecialControls()));
		JLabel browserInstructions = new JLabel(
				I18nUtils.getString("RADIO_BROWSER_TEXT"));
		JButton closeButton = new JButton(I18nUtils.getString("CLOSE"));
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		topPanel.add(radioIcon, BorderLayout.WEST);
		topPanel.add(browserInstructions, BorderLayout.CENTER);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 20, 10, 20);
		c.gridwidth = 2;
		panel.add(topPanel, c);
		c.gridy = 1;
		c.weightx = 0.3;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 10, 10);
		panel.add(getControlsBuilder().createScrollPane(this.list), c);
		c.gridx = 1;
		c.weightx = 0.7;
		panel.add(getControlsBuilder().createScrollPane(this.table), c);
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(closeButton, c);
		add(panel);
	}

	/**
	 * @return list
	 */
	@SuppressWarnings("rawtypes")
	protected JList getList() {
		return this.list;
	}

	/**
	 * @return table
	 */
	public JTable getTable() {
		return this.table;
	}
}
