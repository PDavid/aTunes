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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The properties dialog for radios.
 */
public final class RadioPropertiesDialog extends AudioObjectPropertiesDialog {

	private static final long serialVersionUID = -73744354419152730L;
	private JLabel pictureLabel;
	private JLabel titleLabel;
	private JLabel urlLabel;
	private JLabel labelLabel;
	private JLabel bitrateLabel;
	private JLabel frequencyLabel;
	private IRadio radio;

	private IIconFactory radioMediumIcon;

	/**
	 * @param radioMediumIcon
	 */
	public void setRadioMediumIcon(final IIconFactory radioMediumIcon) {
		this.radioMediumIcon = radioMediumIcon;
	}

	/**
	 * Instantiates a new radio properties dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	RadioPropertiesDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, controlsBuilder);
	}

	@Override
	public void setAudioObject(final IAudioObject audioObject) {
		if (audioObject instanceof IRadio) {
			this.radio = (IRadio) audioObject;
			setTitle(getTitleText(this.radio));
			addContent(getLookAndFeel());
			setContent();
			getControlsBuilder().applyComponentOrientation(this);
		} else {
			throw new IllegalArgumentException("Not a IRadio");
		}
	}

	@Override
	public void initialize() {
	}

	/**
	 * Gives a title for dialog.
	 * 
	 * @param radio
	 *            the radio
	 * 
	 * @return title for dialog
	 */
	private String getTitleText(final IRadio radio) {
		return StringUtils.getString(I18nUtils.getString("INFO_OF_RADIO"), " ",
				radio.getName());
	}

	/**
	 * Adds the content.
	 * 
	 * @param iLookAndFeel
	 */
	private void addContent(final ILookAndFeel iLookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());

		this.pictureLabel = new JLabel();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(40, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(this.pictureLabel, c);

		this.titleLabel = new JLabel();
		this.titleLabel.setFont(iLookAndFeel.getPropertiesDialogBigFont());
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(this.titleLabel, c);

		this.urlLabel = new JLabel();
		this.urlLabel.setFont(iLookAndFeel.getPropertiesDialogBigFont());
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(5, 10, 5, 10);
		panel.add(this.urlLabel, c);

		this.labelLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 2;
		panel.add(this.labelLabel, c);

		this.bitrateLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 3;
		panel.add(this.bitrateLabel, c);

		this.frequencyLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 4;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		panel.add(this.frequencyLabel, c);

		add(panel);
	}

	/**
	 * Fill picture.
	 */
	private void fillPicture() {
		ImageIcon picture = this.radioMediumIcon.getIcon(getLookAndFeel()
				.getPaintForSpecialControls());
		this.pictureLabel.setPreferredSize(new Dimension(
				picture.getIconWidth(), picture.getIconHeight()));
		this.pictureLabel.setIcon(picture);
		this.pictureLabel.setVisible(true);
	}

	/**
	 * Sets the content.
	 */
	private void setContent() {
		fillPicture();
		this.titleLabel.setText(getHtmlFormatted(
				I18nUtils.getString("NAME"),
				StringUtils.isEmpty(this.radio.getName()) ? "-" : this.radio
						.getName()));
		this.urlLabel.setText(getHtmlFormatted(I18nUtils.getString("URL"),
				this.radio.getUrl()));
		this.labelLabel.setText(getHtmlFormatted(
				I18nUtils.getString("LABEL"),
				StringUtils.isEmpty(this.radio.getLabel()) ? "-" : this.radio
						.getLabel()));
		this.bitrateLabel
				.setText(getHtmlFormatted(
						I18nUtils.getString("BITRATE"),
						this.radio.getBitrate() > 0 ? StringUtils.getString(
								String.valueOf(this.radio.getBitrate()),
								" kbps") : "-"));
		this.frequencyLabel
				.setText(getHtmlFormatted(
						I18nUtils.getString("FREQUENCY"),
						this.radio.getFrequency() > 0 ? StringUtils.getString(
								String.valueOf(this.radio.getFrequency()),
								" Hz") : "-"));
	}
}
