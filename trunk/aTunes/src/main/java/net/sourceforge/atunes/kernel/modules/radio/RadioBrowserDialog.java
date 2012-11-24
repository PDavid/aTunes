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

package net.sourceforge.atunes.kernel.modules.radio;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.JXTreeTable;

/**
 * The Class RadioBrowserDialog.
 */
public final class RadioBrowserDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = 8523236886848649698L;

	/** The table. */
	private JXTreeTable treeTable;

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
	 * Sets the content.
	 * 
	 * @param iLookAndFeel
	 */
	private void setContent(final ILookAndFeel iLookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());
		this.treeTable = new JXTreeTable();
		this.treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JPanel topPanel = new JPanel(new BorderLayout(10, 0));
		JLabel radioIcon = new JLabel(
				this.radioMediumIcon.getIcon(getLookAndFeel()
						.getPaintForSpecialControls()));
		JLabel browserInstructions = new JLabel(
				I18nUtils.getString("RADIO_BROWSER_INSTRUCTIONS"));
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
		panel.add(topPanel, c);
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(iLookAndFeel.getScrollPane(this.treeTable), c);
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(closeButton, c);
		add(panel);
	}

	/**
	 * Gets the table.
	 * 
	 * @return the table
	 */
	public JXTreeTable getTreeTable() {
		return this.treeTable;
	}

}
