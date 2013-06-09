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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class IndeterminateProgressDialog.
 * 
 * @author fleax
 */
public final class IndeterminateProgressDialog extends AbstractCustomDialog
		implements IIndeterminateProgressDialog {

	private static final long serialVersionUID = -3071934230042256578L;

	/**
	 * Instantiates a new indeterminate progress dialog.
	 * 
	 * @param parent
	 * @param controlsBuilder
	 */
	public IndeterminateProgressDialog(final IFrame parent,
			final IControlsBuilder controlsBuilder) {
		super(parent, 450, 160, controlsBuilder);
		add(getContent());
		setResizable(false);
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel pictureLabel = new JLabel(Images.getImage(Images.APP_LOGO_90));
		JLabel label = new JLabel(StringUtils.getString(
				I18nUtils.getString("PLEASE_WAIT"), "..."));
		Font f = label.getFont().deriveFont(Font.PLAIN);
		label.setFont(f);
		JProgressBar progressBar = getControlsBuilder()
				.createIndeterminateProgressBar();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(0, 20, 0, 0);
		panel.add(pictureLabel, c);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 0, 20);
		c.anchor = GridBagConstraints.SOUTH;
		panel.add(label, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 5, 20);
		c.anchor = GridBagConstraints.NORTH;
		panel.add(progressBar, c);
		return panel;
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}
}
