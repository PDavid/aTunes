/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The properties dialog for audio files
 */
final class AudioFilePropertiesDialog extends PropertiesDialog {

    private static final long serialVersionUID = 7504320983331038543L;

    static final Logger logger = new Logger();

    JLabel pictureLabel;
    private JLabel fileNameLabel;
    private JLabel pathLabel;
    private JLabel songLabel;
    private JLabel artistLabel;
    private JLabel albumArtistLabel;
    private JLabel composerLabel;
    private JLabel albumLabel;
    private JLabel durationLabel;
    private JLabel trackLabel;
    private JLabel discNumberLabel;
    private JLabel yearLabel;
    private JLabel genreLabel;
    private JLabel bitrateLabel;
    private JLabel frequencyLabel;
    AudioFile file;

    /**
     * Instantiates a new audio file properties dialog.
     * 
     * @param file
     *            the file
     */
    AudioFilePropertiesDialog(AudioFile file, JFrame owner) {
        super(getTitleText(file), owner);
        this.file = file;
        setAudioObject(file);
        addContent();

        setContent();

        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Gives a title for dialog.
     * 
     * @param file
     *            the file
     * 
     * @return title for dialog
     */
    private static String getTitleText(AudioFile file) {
        return StringUtils.getString(I18nUtils.getString("INFO_OF_FILE"), " ", file.getFile().getName());
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        JPanel panel = new JPanel(new GridBagLayout());

        pictureLabel = new JLabel();
        pictureLabel.setPreferredSize(new Dimension(Constants.DIALOG_IMAGE_SIZE.getSize(), Constants.DIALOG_IMAGE_SIZE.getSize()));
        pictureLabel.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 4;
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        panel.add(pictureLabel, c);

        songLabel = new JLabel();
        songLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(songLabel, c);

        artistLabel = new JLabel();
        artistLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
        c.gridx = 1;
        c.gridy = 1;
        panel.add(artistLabel, c);

        albumArtistLabel = new JLabel();
        albumArtistLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
        c.gridx = 1;
        c.gridy = 2;
        panel.add(albumArtistLabel, c);

        albumLabel = new JLabel();
        albumLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
        c.gridx = 1;
        c.gridy = 3;
        panel.add(albumLabel, c);

        fileNameLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 4;
        panel.add(fileNameLabel, c);

        pathLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 5;
        panel.add(pathLabel, c);

        durationLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 6;
        panel.add(durationLabel, c);

        trackLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 7;
        panel.add(trackLabel, c);

        discNumberLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 8;
        panel.add(discNumberLabel, c);

        genreLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 9;
        panel.add(genreLabel, c);

        yearLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 10;
        panel.add(yearLabel, c);

        composerLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 11;
        panel.add(composerLabel, c);

        bitrateLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 12;
        panel.add(bitrateLabel, c);

        frequencyLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 13;
        panel.add(frequencyLabel, c);

        add(panel);
    }

    /**
     * Fill picture.
     */
    private void fillPicture() {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                return file.getImage(Constants.DIALOG_IMAGE_SIZE);
            }

            @Override
            protected void done() {
                ImageIcon cover;
                try {
                    cover = get();
                    if (cover != null) {
                        pictureLabel.setIcon(cover);
                    } else {
                        pictureLabel.setIcon(Images.getImage(Images.NO_COVER_AUDIOFILE_PROPERTIES));
                    }
                    pictureLabel.setVisible(true);
                } catch (InterruptedException e) {
                    logger.error(LogCategories.IMAGE, e);
                } catch (ExecutionException e) {
                    logger.error(LogCategories.IMAGE, e);
                }
            }
        }.execute();

    }

    /**
     * Sets the content.
     */
    private void setContent() {
        fillPicture();
        songLabel.setText(getHtmlFormatted(I18nUtils.getString("SONG"), StringUtils.isEmpty(file.getTitle()) ? "-" : file.getTitle()));
        artistLabel.setText(getHtmlFormatted(I18nUtils.getString("ARTIST"), StringUtils.isEmpty(file.getArtist()) ? "-" : file.getArtist()));
        albumArtistLabel.setText(getHtmlFormatted(I18nUtils.getString("ALBUM_ARTIST"), StringUtils.isEmpty(file.getAlbumArtist()) ? "-" : file.getAlbumArtist()));
        albumLabel.setText(getHtmlFormatted(I18nUtils.getString("ALBUM"), StringUtils.isEmpty(file.getAlbum()) ? "-" : file.getAlbum()));
        fileNameLabel.setText(getHtmlFormatted(I18nUtils.getString("FILE"), file.getFile().getName()));
        pathLabel.setText(getHtmlFormatted(I18nUtils.getString("LOCATION"), file.getFile().getParent()));
        durationLabel.setText(getHtmlFormatted(I18nUtils.getString("DURATION"), StringUtils.seconds2String(file.getDuration())));
        trackLabel.setText(getHtmlFormatted(I18nUtils.getString("TRACK"), file.getTrackNumber() > 0 ? String.valueOf(file.getTrackNumber()) : "-"));
        discNumberLabel.setText(getHtmlFormatted(I18nUtils.getString("DISC_NUMBER"), file.getDiscNumber() > 0 ? String.valueOf(file.getDiscNumber()) : "-"));
        genreLabel.setText(getHtmlFormatted(I18nUtils.getString("GENRE"), StringUtils.isEmpty(file.getGenre()) ? "-" : file.getGenre()));
        yearLabel.setText(getHtmlFormatted(I18nUtils.getString("YEAR"), StringUtils.getNumberOrZero(file.getYear()) > 0 ? file.getYear() : "-"));
        composerLabel.setText(getHtmlFormatted(I18nUtils.getString("COMPOSER"), StringUtils.isEmpty(file.getComposer()) ? "-" : file.getComposer()));
        bitrateLabel.setText(getHtmlFormatted(I18nUtils.getString("BITRATE"), StringUtils.getString(Long.toString(file.getBitrate()), " Kbps")));
        frequencyLabel.setText(getHtmlFormatted(I18nUtils.getString("FREQUENCY"), StringUtils.getString(Integer.toString(file.getFrequency()), " Hz")));
    }

}
