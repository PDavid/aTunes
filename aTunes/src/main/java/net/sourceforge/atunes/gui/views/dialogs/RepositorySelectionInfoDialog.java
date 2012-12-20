/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class RepositorySelectionInfoDialog.
 */
public final class RepositorySelectionInfoDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = 4369595555397951445L;

	private boolean accepted;

	/**
	 * Instantiates a new repository selection info dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public RepositorySelectionInfoDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 400, 250, controlsBuilder);
	}

	@Override
	public void initialize() {
		setResizable(false);
		setTitle(I18nUtils.getString("REPOSITORY_SELECTION_INFO"));
		setContent();
	}

	/**
	 * Sets the content.
	 */
	private void setContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel icon = new JLabel(Images.getImage(Images.APP_LOGO_90));
		JTextArea text = getControlsBuilder().createTextArea();
		text.setText(I18nUtils.getString("REPOSITORY_SELECTION_INFO_TEXT"));
		text.setOpaque(false);
		text.setEditable(false);
		text.setWrapStyleWord(true);
		text.setLineWrap(true);
		text.setBorder(BorderFactory.createEmptyBorder());
		JButton button = new JButton(I18nUtils.getString("OK"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				RepositorySelectionInfoDialog.this.accepted = true;
				setVisible(false);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(icon, c);
		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(text, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(button, c);
		add(panel);
	}

	/**
	 * Returns true if user presses accept button
	 * 
	 * @return
	 */
	public boolean userAccepted() {
		return this.accepted;
	}
}
