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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IRipperProgressDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * A dialog to show progress of ripping process
 * 
 * @author alex
 * 
 */
public final class RipperProgressDialog extends AbstractCustomDialog implements
		IRipperProgressDialog {

	private static final long serialVersionUID = -3891515847607545757L;

	private JLabel cover;
	private JProgressBar totalProgressBar;
	private JLabel totalProgressValueLabel;
	private JProgressBar decodeProgressBar;
	private JLabel decodeProgressValueLabel;
	private JProgressBar encodeProgressBar;
	private JLabel encodeProgressValueLabel;
	private JButton cancelButton;

	/**
	 * Instantiates a new ripper progress dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public RipperProgressDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 420, 200, controlsBuilder);
	}

	@Override
	public void addCancelAction(final ActionListener action) {
		this.cancelButton.addActionListener(action);
	}

	@Override
	public void setArtistAndAlbum(final String artist, final String album) {
		setTitle(StringUtils.getString(I18nUtils.getString("RIPPING_CD"), " ",
				artist, " - ", album));
	}

	/**
	 * Sets the content.
	 */
	@Override
	public void initialize() {
		setTitle(I18nUtils.getString("RIPPING_CD"));
		setResizable(false);

		JPanel panel = new JPanel(new GridBagLayout());

		this.cover = new JLabel(Images.getImage(Images.APP_LOGO_90));

		JLabel totalProgressLabel = new JLabel(
				I18nUtils.getString("TOTAL_PROGRESS"));
		this.totalProgressBar = new JProgressBar();
		this.totalProgressBar.setPreferredSize(new Dimension(10, 12));
		this.totalProgressValueLabel = new JLabel();
		JLabel decodeProgressLabel = new JLabel(I18nUtils.getString("DECODING"));
		this.decodeProgressBar = new JProgressBar();
		this.decodeProgressBar.setPreferredSize(new Dimension(10, 12));
		this.decodeProgressValueLabel = new JLabel();
		JLabel encodeProgressLabel = new JLabel(I18nUtils.getString("ENCODING"));
		this.encodeProgressBar = new JProgressBar();
		this.encodeProgressBar.setPreferredSize(new Dimension(10, 12));
		encodeProgressLabel.setBorder(BorderFactory.createEmptyBorder());
		this.encodeProgressValueLabel = new JLabel();
		this.cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		this.cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});

		setDecodeProgressBarLimits(0, 100);
		setEncodeProgressBarLimits(0, 100);

		arrangePanel(panel, totalProgressLabel, decodeProgressLabel,
				encodeProgressLabel);

		add(panel);
	}

	/**
	 * @param panel
	 * @param totalProgressLabel
	 * @param decodeProgressLabel
	 * @param encodeProgressLabel
	 */
	private void arrangePanel(final JPanel panel,
			final JLabel totalProgressLabel, final JLabel decodeProgressLabel,
			final JLabel encodeProgressLabel) {
		GridBagConstraints c = new GridBagConstraints();

		addCover(panel, c);

		addTotalProgress(panel, totalProgressLabel, c);

		addDecodeProgress(panel, decodeProgressLabel, c);

		addEncodeProgress(panel, encodeProgressLabel, c);

		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(10, 0, 5, 0);
		panel.add(this.cancelButton, c);
	}

	/**
	 * @param panel
	 * @param encodeProgressLabel
	 * @param c
	 */
	private void addEncodeProgress(final JPanel panel,
			final JLabel encodeProgressLabel, final GridBagConstraints c) {
		c.gridy = 4;
		c.gridwidth = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(3, 0, 0, 20);
		panel.add(encodeProgressLabel, c);
		c.gridx = 2;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(this.encodeProgressValueLabel, c);
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 20);
		panel.add(this.encodeProgressBar, c);
	}

	/**
	 * @param panel
	 * @param decodeProgressLabel
	 * @param c
	 */
	private void addDecodeProgress(final JPanel panel,
			final JLabel decodeProgressLabel, final GridBagConstraints c) {
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(3, 0, 0, 20);
		panel.add(decodeProgressLabel, c);
		c.gridx = 2;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(this.decodeProgressValueLabel, c);
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 20);
		panel.add(this.decodeProgressBar, c);
	}

	/**
	 * @param panel
	 * @param totalProgressLabel
	 * @param c
	 */
	private void addTotalProgress(final JPanel panel,
			final JLabel totalProgressLabel, final GridBagConstraints c) {
		c.gridx = 1;
		c.weightx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(20, 0, 0, 20);
		panel.add(totalProgressLabel, c);
		c.gridx = 2;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(this.totalProgressValueLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 20);
		panel.add(this.totalProgressBar, c);
	}

	/**
	 * @param panel
	 * @param c
	 */
	private void addCover(final JPanel panel, final GridBagConstraints c) {
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 6;
		c.insets = new Insets(10, 20, 0, 20);
		panel.add(this.cover, c);
	}

	@Override
	public void setCover(final ImageIcon img) {
		this.cover.setIcon(ImageUtils.resize(img, 90, 90));
	}

	/**
	 * Sets the decode progress bar limits.
	 * 
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 */
	private void setDecodeProgressBarLimits(final int min, final int max) {
		setLimits(this.decodeProgressBar, min, max);
	}

	@Override
	public void setDecodeProgressValue(final int value) {
		if (value < 0) {
			this.decodeProgressBar.setIndeterminate(true);
		} else {
			this.decodeProgressBar.setIndeterminate(false);
			this.decodeProgressBar.setValue(value);
		}

	}

	@Override
	public void setDecodeProgressValue(final String value) {
		this.decodeProgressValueLabel.setText(value);
	}

	/**
	 * Sets the encode progress bar limits.
	 * 
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 */
	private void setEncodeProgressBarLimits(final int min, final int max) {
		setLimits(this.encodeProgressBar, min, max);
	}

	@Override
	public void setEncodeProgressValue(final int value) {
		if (value < 0) {
			this.encodeProgressBar.setIndeterminate(true);
		} else {
			this.encodeProgressBar.setIndeterminate(false);
			this.encodeProgressBar.setValue(value);
		}
	}

	@Override
	public void setEncodeProgressValue(final String value) {
		this.encodeProgressValueLabel.setText(value);
	}

	/**
	 * Sets the limits.
	 * 
	 * @param progressBar
	 *            the progress bar
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 */
	private void setLimits(final JProgressBar progressBar, final int min,
			final int max) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
	}

	@Override
	public void setTotalProgressBarLimits(final int min, final int max) {
		setLimits(this.totalProgressBar, min, max);
	}

	@Override
	public void setTotalProgressValue(final int value) {
		this.totalProgressBar.setValue(value);
		this.totalProgressValueLabel.setText(StringUtils.getString(value,
				" / ", this.totalProgressBar.getMaximum()));
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	@Override
	public void hideDialog() {
		setVisible(false);
		dispose();
	}
}
