/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.atunes.gui.images.RadioImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The properties dialog for radios.
 */
final class RadioPropertiesDialog extends AudioObjectPropertiesDialog {

    private static final long serialVersionUID = -73744354419152730L;
    private JLabel pictureLabel;
    private JLabel titleLabel;
    private JLabel urlLabel;
    private JLabel labelLabel;
    private JLabel bitrateLabel;
    private JLabel frequencyLabel;
    private Radio radio;

    /**
     * Instantiates a new radio properties dialog.
     * 
     * @param radio
     * @param frame
     */
    RadioPropertiesDialog(Radio radio, IFrame frame) {
        super(getTitleText(radio), frame);
        this.radio = radio;
        setAudioObject(radio);
        addContent();

        setContent();

        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Gives a title for dialog.
     * 
     * @param radio
     *            the radio
     * 
     * @return title for dialog
     */
    private static String getTitleText(IRadio radio) {
        return StringUtils.getString(I18nUtils.getString("INFO_OF_RADIO"), " ", radio.getName());
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        JPanel panel = new JPanel(new GridBagLayout());

        pictureLabel = new JLabel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.insets = new Insets(40, 10, 5, 10);
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.VERTICAL;
        panel.add(pictureLabel, c);

        titleLabel = new JLabel();
        titleLabel.setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPropertiesDialogBigFont());
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleLabel, c);

        urlLabel = new JLabel();
        urlLabel.setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPropertiesDialogBigFont());
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 10, 5, 10);
        panel.add(urlLabel, c);

        labelLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 2;
        panel.add(labelLabel, c);

        bitrateLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 3;
        panel.add(bitrateLabel, c);

        frequencyLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        panel.add(frequencyLabel, c);

        add(panel);
    }

    /**
     * Fill picture.
     */
    private void fillPicture() {
        ImageIcon picture = RadioImageIcon.getIcon();
        pictureLabel.setPreferredSize(new Dimension(picture.getIconWidth(), picture.getIconHeight()));
        pictureLabel.setIcon(picture);
        pictureLabel.setVisible(true);
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        fillPicture();
        titleLabel.setText(getHtmlFormatted(I18nUtils.getString("NAME"), StringUtils.isEmpty(radio.getName()) ? "-" : radio.getName()));
        urlLabel.setText(getHtmlFormatted(I18nUtils.getString("URL"), radio.getUrl()));
        labelLabel.setText(getHtmlFormatted(I18nUtils.getString("LABEL"), StringUtils.isEmpty(radio.getLabel()) ? "-" : radio.getLabel()));
        bitrateLabel.setText(getHtmlFormatted(I18nUtils.getString("BITRATE"), radio.getBitrate() > 0 ? StringUtils.getString(String.valueOf(radio.getBitrate()), " kbps") : "-"));
        frequencyLabel.setText(getHtmlFormatted(I18nUtils.getString("FREQUENCY"), radio.getFrequency() > 0 ? StringUtils.getString(String.valueOf(radio.getFrequency()), " Hz")
                : "-"));
    }
}
