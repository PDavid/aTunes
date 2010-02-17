/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.Fonts;

/**
 * The audio object properties panel.
 */
public final class AudioObjectPropertiesPanel extends JPanel {

    private static final long serialVersionUID = 6097305595858691246L;

    private JPanel mainPanel;
    private JLabel pictureLabel;
    private JLabel urlLabel;
    private JLabel titleLabel;
    private JLabel trackLabel;
    private JLabel discNumberLabel;
    private JLabel yearLabel;
    private JLabel genreLabel;
    private JLabel bitrateLabel;
    private JLabel frequencyLabel;

    /**
     * Instantiates a new audio object properties panel.
     */
    public AudioObjectPropertiesPanel() {
        super(new BorderLayout());
        addContent();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(600, 300);
        frame.add(new AudioObjectPropertiesPanel());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        getMainPanel().setVisible(false);
        getPictureLabel().setVisible(false);
        add(getMainPanel(), BorderLayout.CENTER);
    }

    public JLabel getBitrateLabel() {
        return bitrateLabel;
    }

    public JLabel getUrlLabel() {
        return urlLabel;
    }

    public JLabel getFrequencyLabel() {
        return frequencyLabel;
    }

    public JLabel getGenreLabel() {
        return genreLabel;
    }

    public JLabel getPictureLabel() {
        return pictureLabel;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new GridBagLayout());
            pictureLabel = new JLabel();
            pictureLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridheight = 7;
            c.insets = new Insets(5, 10, 5, 5);
            mainPanel.add(pictureLabel, c);
            c.gridx = 1;
            c.gridy = 0;
            c.gridwidth = 3;
            c.gridheight = 1;
            c.weightx = 1;
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(5, 10, 0, 10);
            titleLabel = new JLabel();
            titleLabel.setFont(Fonts.getSmallFont());
            titleLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            mainPanel.add(titleLabel, c);
            c.gridy = 1;
            c.insets = new Insets(0, 10, 0, 10);
            urlLabel = new JLabel();
            urlLabel.setFont(Fonts.getSmallFont());
            mainPanel.add(urlLabel, c);
            c.gridy = 2;
            c.gridwidth = 1;
            c.insets = new Insets(0, 10, 0, 5);
            trackLabel = new JLabel();
            trackLabel.setFont(Fonts.getSmallFont());
            mainPanel.add(trackLabel, c);
            c.gridy = 3;
            discNumberLabel = new JLabel();
            discNumberLabel.setFont(Fonts.getSmallFont());
            mainPanel.add(discNumberLabel, c);
            c.gridy = 4;
            yearLabel = new JLabel();
            yearLabel.setFont(Fonts.getSmallFont());
            c.insets = new Insets(0, 10, 5, 10);
            mainPanel.add(yearLabel, c);

            c.gridx = 2;
            c.gridy = 2;
            genreLabel = new JLabel();
            genreLabel.setFont(Fonts.getSmallFont());
            c.insets = new Insets(0, 5, 0, 5);
            mainPanel.add(genreLabel, c);

            c.gridy = 3;
            bitrateLabel = new JLabel();
            bitrateLabel.setFont(Fonts.getSmallFont());
            mainPanel.add(bitrateLabel, c);
            frequencyLabel = new JLabel();
            frequencyLabel.setFont(Fonts.getSmallFont());
            c.gridy = 4;
            mainPanel.add(frequencyLabel, c);
        }
        return mainPanel;
    }

    public JLabel getTrackLabel() {
        return trackLabel;
    }

    public JLabel getYearLabel() {
        return yearLabel;
    }

    public JLabel getDiscNumberLabel() {
        return discNumberLabel;
    }

}
