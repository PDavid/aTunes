/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

/**
 * The Class PodcastFeedEntryPropertiesDialog.
 */
class PodcastFeedEntryPropertiesDialog extends PropertiesDialog {

    private static final long serialVersionUID = -2472573171771586037L;

    /** The picture label. */
    private JLabel pictureLabel;

    /** The title label. */
    private JLabel titleLabel;

    /** The artist label. */
    private JLabel artistLabel;

    /** The url label. */
    private JLabel urlLabel;

    /** The duration label. */
    private JLabel durationLabel;

    /** The date label. */
    private JLabel dateLabel;

    /** The podcast feed label. */
    private JLabel podcastFeedLabel;

    /** The downloaded label. */
    private JLabel downloadedLabel;

    /** The description label. */
    private JLabel descriptionLabel;

    /** The description scroll pane. */
    private JScrollPane descriptionScrollPane;

    /** The description text area. */
    private JTextArea descriptionTextArea;

    /** The entry. */
    private PodcastFeedEntry entry;

    /**
     * Instantiates a new podcast feed entry properties dialog.
     * 
     * @param entry
     *            the entry
     */
    PodcastFeedEntryPropertiesDialog(PodcastFeedEntry entry, Component owner) {
        super(getTitleText(entry), owner);
        this.entry = entry;
        addContent();

        setContent();

        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Gives a title for dialog.
     * 
     * @param entry
     *            the entry
     * 
     * @return title for dialog
     */
    private static String getTitleText(PodcastFeedEntry entry) {
        return StringUtils.getString(LanguageTool.getString("INFO_OF_PODCAST_FEED"), " ", entry.getTitle());
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
        c.gridheight = 3;
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.VERTICAL;
        panel.add(pictureLabel, c);

        titleLabel = new JLabel();
        titleLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleLabel, c);

        artistLabel = new JLabel();
        artistLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
        c.gridx = 1;
        c.gridy = 1;
        panel.add(artistLabel, c);

        urlLabel = new JLabel();
        urlLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
        c.gridx = 1;
        c.gridy = 2;
        panel.add(urlLabel, c);

        durationLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 3;
        panel.add(durationLabel, c);

        dateLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 4;
        panel.add(dateLabel, c);

        podcastFeedLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 5;
        panel.add(podcastFeedLabel, c);

        downloadedLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 6;
        panel.add(downloadedLabel, c);

        descriptionLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 7;
        c.insets = new Insets(5, 10, 0, 10);
        panel.add(descriptionLabel, c);

        descriptionScrollPane = new JScrollPane();
        descriptionScrollPane.setMinimumSize(new Dimension(400, 100));
        c.gridx = 1;
        c.gridy = 8;
        c.insets = new Insets(0, 10, 5, 10);
        panel.add(descriptionScrollPane, c);

        descriptionTextArea = new JTextArea();
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setOpaque(false);
        descriptionTextArea.setBorder(BorderFactory.createEmptyBorder());
        descriptionScrollPane.setViewportView(descriptionTextArea);

        add(panel);

    }

    /**
     * Fill picture.
     */
    private void fillPicture() {
        ImageIcon picture = ImageLoader.getImage(ImageLoader.RSS);
        pictureLabel.setPreferredSize(new Dimension(picture.getIconWidth(), picture.getIconHeight()));
        pictureLabel.setIcon(picture);
        pictureLabel.setVisible(true);
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        fillPicture();
        titleLabel.setText(getHtmlFormatted(LanguageTool.getString("NAME"), StringUtils.isEmpty(entry.getTitle()) ? "-" : entry.getTitle()));
        artistLabel.setText(getHtmlFormatted(LanguageTool.getString("ARTIST"), StringUtils.isEmpty(entry.getArtist()) ? "-" : entry.getArtist()));
        urlLabel.setText(getHtmlFormatted(LanguageTool.getString("URL"), entry.getUrl()));
        if (entry.getDuration() > 0) {
            durationLabel.setText(getHtmlFormatted(LanguageTool.getString("DURATION"), TimeUtils.seconds2String(entry.getDuration())));
        } else {
            durationLabel.setText(getHtmlFormatted(LanguageTool.getString("DURATION"), "-"));
        }
        if (entry.getDate() != null) {
            dateLabel.setText(getHtmlFormatted(LanguageTool.getString("DATE"), StringUtils.getString(DateFormat.getDateInstance(DateFormat.LONG,
                    ApplicationState.getInstance().getLocale().getLocale()).format(entry.getDate()), ", ", DateFormat.getTimeInstance().format(entry.getDate()))));
        } else {
            dateLabel.setText(getHtmlFormatted(LanguageTool.getString("DATE"), "-"));
        }
        podcastFeedLabel.setText(getHtmlFormatted(LanguageTool.getString("PODCAST_FEED"), entry.getPodcastFeed().getName()));
        downloadedLabel.setText(getHtmlFormatted(LanguageTool.getString("DOWNLOADED"), entry.isDownloaded() ? LanguageTool.getString("YES") : LanguageTool.getString("NO")));
        descriptionLabel.setText(getHtmlFormatted(LanguageTool.getString("DESCRIPTION"), ""));
        descriptionTextArea.setText(entry.getDescription());
        descriptionTextArea.setCaretPosition(0);
    }

}
