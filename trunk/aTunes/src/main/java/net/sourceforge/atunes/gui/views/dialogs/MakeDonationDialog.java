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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class MakeDonationDialog.
 */
public final class MakeDonationDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = 4369595555397951445L;

	private IDesktop desktop;

	private String donationUrl;

	private boolean dontShowAgain;

	private boolean showOptionToNotShowAgain;

	private boolean userDonated;

	/**
	 * Instantiates a new repository selection info dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public MakeDonationDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 500, 350, controlsBuilder);
	}

	/**
	 * @param showOptionToNotShowAgain
	 */
	public void setShowOptionToNotShowAgain(
			final boolean showOptionToNotShowAgain) {
		this.showOptionToNotShowAgain = showOptionToNotShowAgain;
	}

	/**
	 * @param donationUrl
	 */
	public void setDonationUrl(final String donationUrl) {
		this.donationUrl = donationUrl;
	}

	/**
	 * @param desktop
	 */
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	@Override
	public void initialize() {
		setResizable(false);
		setTitle(I18nUtils.getString("MAKE_DONATION"));
	}

	@Override
	public void showDialog() {
		// Set content when showing dialog to set visible controls depending on
		// showOptionToNotShowAgain
		setContent();
		super.showDialog();
	}

	/**
	 * Sets the content.
	 */
	private void setContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel icon = new JLabel(Images.getImage(Images.APP_LOGO_90));
		JTextArea text = getControlsBuilder().createTextArea();
		text.setText(I18nUtils.getString("MAKE_DONATION_INFO"));
		text.setOpaque(false);
		text.setEditable(false);
		text.setWrapStyleWord(true);
		text.setLineWrap(true);
		text.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane scrollPane = getControlsBuilder().createScrollPane(text);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		JButton donateButton = new JButton(I18nUtils.getString("MAKE_DONATION"));
		donateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				MakeDonationDialog.this.desktop
						.openURL(MakeDonationDialog.this.donationUrl);
				MakeDonationDialog.this.userDonated = true;
				MakeDonationDialog.this.hideDialog();
			}
		});
		JButton notNowButton = new JButton(I18nUtils.getString("NOT_NOW"));
		notNowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				MakeDonationDialog.this.hideDialog();
			}
		});

		final JCheckBox dontShowAgain = new JCheckBox(
				I18nUtils.getString("DONT_SHOW_AGAIN"));
		dontShowAgain.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent event) {
				MakeDonationDialog.this.dontShowAgain = dontShowAgain
						.isSelected();
			}
		});

		JPanel buttons = new JPanel();
		buttons.add(donateButton);
		buttons.add(notNowButton);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(20, 20, 20, 20);
		panel.add(icon, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(20, 0, 20, 20);
		c.weighty = 1;
		panel.add(scrollPane, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = !this.showOptionToNotShowAgain ? new Insets(0, 0, 40, 0)
				: new Insets(0, 0, 0, 0);
		c.weighty = 0;
		c.insets = new Insets(10, 0, 10, 0);
		panel.add(buttons, c);
		c.gridy = 3;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(20, 0, 20, 0);
		panel.add(dontShowAgain, c);
		add(panel);

		dontShowAgain.setVisible(this.showOptionToNotShowAgain);
	}

	/**
	 * @return if user donated
	 */
	public boolean isUserDonated() {
		return this.userDonated;
	}

	/**
	 * @return the dontShowAgain
	 */
	public boolean isDontShowAgain() {
		return this.dontShowAgain;
	}
}
