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
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CloseAction;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * A progress dialog
 * 
 * @author alex
 * 
 */
public class ProgressDialog extends AbstractCustomDialog implements
		IProgressDialog {

	private static final long serialVersionUID = 5792663290880992661L;

	/** The progress bar. */
	private JProgressBar progressBar;

	/** The image label. */
	private JLabel imagelabel;

	/** The info label. */
	private JLabel infoLabel;

	/** The received label. */
	private JLabel currentLabel;

	/** The total label. */
	private JLabel totalLabel;

	/** The cancel button. */
	private JButton cancelButton;

	/**
	 * Instantiates a new transfer progress dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public ProgressDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 450, 150, false, CloseAction.DISPOSE, controlsBuilder);
	}

	@Override
	public void initialize() {
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
		this.progressBar = new JProgressBar();
		this.progressBar.setBorder(BorderFactory.createEmptyBorder());
		this.progressBar.setStringPainted(true);
		this.imagelabel = new JLabel(Images.getImage(Images.APP_LOGO_90));
		this.infoLabel = new JLabel();
		this.currentLabel = new JLabel();
		JLabel separatorLabel = new JLabel(" / ");
		this.totalLabel = new JLabel();
		this.cancelButton = new JButton(I18nUtils.getString("CANCEL"));

		arrangePanel(panel, separatorLabel);

		return panel;
	}

	/**
	 * @param panel
	 * @param separatorLabel
	 */
	private void arrangePanel(final JPanel panel, final JLabel separatorLabel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 5;
		c.insets = new Insets(0, 20, 0, 0);
		panel.add(this.imagelabel, c);
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 0, 20);
		c.anchor = GridBagConstraints.WEST;
		panel.add(this.infoLabel, c);
		c.gridx = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 0, 0, 3);
		c.anchor = GridBagConstraints.EAST;
		panel.add(this.currentLabel, c);
		c.gridx = 3;
		c.insets = new Insets(5, 0, 0, 0);
		panel.add(separatorLabel, c);
		c.gridx = 4;
		c.insets = new Insets(5, 0, 0, 20);
		panel.add(this.totalLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 5, 20);
		c.anchor = GridBagConstraints.CENTER;
		panel.add(this.progressBar, c);
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(this.cancelButton, c);
	}

	@Override
	public void setInfoText(final String s) {
		this.infoLabel.setText(s);
	}

	@Override
	public void setProgressBarValue(final int value) {
		this.progressBar.setValue(value);
	}

	@Override
	public void setCurrentProgress(final long value) {
		this.currentLabel.setText(Long.toString(value));
	}

	@Override
	public void setTotalProgress(final long value) {
		this.totalLabel.setText(Long.toString(value));
	}

	@Override
	public void addCancelButtonActionListener(final ActionListener a) {
		this.cancelButton.addActionListener(a);
	}

	@Override
	public void disableCancelButton() {
		this.cancelButton.setEnabled(false);
	}

	@Override
	public void setIcon(final ImageIcon icon) {
		this.imagelabel.setIcon(icon);
	}

	/**
	 * @return the currentLabel
	 */
	protected JLabel getCurrentLabel() {
		return this.currentLabel;
	}

	/**
	 * @return the totalLabel
	 */
	protected JLabel getTotalLabel() {
		return this.totalLabel;
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}
}
