/*
 * aTunes 2.2.0-SNAPSHOT
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.kernel.modules.tags.EditTagDialogController;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The properties dialog for audio files
 */
final class LocalAudioObjectPropertiesDialog extends AudioObjectPropertiesDialog {

    private final class FillPictureSwingWorker extends SwingWorker<ImageIcon, Void> {
        @Override
        protected ImageIcon doInBackground() {
            return audioObjectImageLocator.getImage(file, Constants.DIALOG_IMAGE_SIZE);
        }

        @Override
        protected void done() {
            ImageIcon cover;
            try {
                cover = get();
                pictureLabel.setIcon(cover);
                pictureLabel.setVisible(cover != null);
            } catch (InterruptedException e) {
            	Logger.error(e);
            } catch (ExecutionException e) {
            	Logger.error(e);
            }
        }
    }

    private static final long serialVersionUID = 7504320983331038543L;

    private JLabel pictureLabel;
    private ProviderLabel fileNameLabel;
    private ProviderLabel pathLabel;
    private ProviderLabel songLabel;
    private ProviderLabel artistLabel;
    private ProviderLabel albumArtistLabel;
    private JLabel composerLabel;
    private ProviderLabel albumLabel;
    private JLabel durationLabel;
    private JLabel trackLabel;
    private JLabel discNumberLabel;
    private JLabel yearLabel;
    private JLabel genreLabel;
    private JLabel bitrateLabel;
    private JLabel frequencyLabel;
    private ILocalAudioObject file;

    private IFrame frame;
    
    private IOSManager osManager;
    
    private IPlayListHandler playListHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private IAudioObjectImageLocator audioObjectImageLocator;
    
    private ILocalAudioObjectValidator localAudioObjectValidator;
    
    private IProcessFactory processFactory;
    
    /**
     * Instantiates a new audio file properties dialog.
     * @param file
     * @param frame
     * @param osManager
     * @param playListHandler
     * @param lookAndFeelManager
     * @param repositoryHandler
     * @param audioObjectImageLocator
     * @param localAudioObjectValidator
     * @param processFactory
     */
    LocalAudioObjectPropertiesDialog(ILocalAudioObject file, IFrame frame, IOSManager osManager, IPlayListHandler playListHandler, ILookAndFeelManager lookAndFeelManager, IRepositoryHandler repositoryHandler, IAudioObjectImageLocator audioObjectImageLocator, ILocalAudioObjectValidator localAudioObjectValidator, IProcessFactory processFactory) {
        super(getTitleText(file), frame, lookAndFeelManager);
        this.file = file;
        this.frame = frame;
        this.osManager = osManager;
        this.playListHandler = playListHandler;
        this.repositoryHandler = repositoryHandler;
        this.audioObjectImageLocator = audioObjectImageLocator;
        this.localAudioObjectValidator = localAudioObjectValidator;
        this.processFactory = processFactory;
        setAudioObject(file);
        addContent(lookAndFeelManager);

        setContent();
        this.pack();
    }

    /**
     * Gives a title for dialog.
     * 
     * @param file
     *            the file
     * 
     * @return title for dialog
     */
    private static String getTitleText(ILocalAudioObject file) {
        return StringUtils.getString(I18nUtils.getString("INFO_OF_FILE"), " ", file.getFile().getName());
    }

    /**
     * Adds the content.
     * @param iLookAndFeelManager
     */
    private void addContent(final ILookAndFeelManager iLookAndFeelManager) {
        JPanel panel = new JPanel(new GridBagLayout());

        pictureLabel = new JLabel();
        songLabel = new ProviderLabel(songProvider);
        songLabel.setFont(iLookAndFeelManager.getCurrentLookAndFeel().getPropertiesDialogBigFont());
        artistLabel = new ProviderLabel(artistProvider);
        artistLabel.setFont(iLookAndFeelManager.getCurrentLookAndFeel().getPropertiesDialogBigFont());
        albumArtistLabel = new ProviderLabel(albumArtistProvider);
        albumArtistLabel.setFont(iLookAndFeelManager.getCurrentLookAndFeel().getPropertiesDialogBigFont());
        albumLabel = new ProviderLabel(albumProvider);
        albumLabel.setFont(iLookAndFeelManager.getCurrentLookAndFeel().getPropertiesDialogBigFont());
        fileNameLabel = new ProviderLabel(fileNameProvider);
        pathLabel = new ProviderLabel(filePathProvider);
        durationLabel = new JLabel();
        trackLabel = new JLabel();
        discNumberLabel = new JLabel();
        genreLabel = new JLabel();
        yearLabel = new JLabel();
        composerLabel = new JLabel();
        bitrateLabel = new JLabel();
        frequencyLabel = new JLabel();

        JButton editTagsButton = new JButton();
        editTagsButton.setText(I18nUtils.getString("EDIT_TAG"));
        editTagsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                EditTagDialogController ctl = new EditTagDialogController(new EditTagDialog(frame, false, iLookAndFeelManager), osManager, playListHandler, repositoryHandler, localAudioObjectValidator, processFactory);
                ctl.editFiles(java.util.Collections.singletonList(file));
            }
        });

        setLayout(panel, editTagsButton);

        add(panel);
    }

	/**
	 * @param panel
	 * @param editTagsButton
	 */
	private void setLayout(JPanel panel, JButton editTagsButton) {
		GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 4;
        c.insets = new Insets(10, 10, 5, 10);
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        panel.add(pictureLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(songLabel, c);

        c.gridy = 1;
        panel.add(artistLabel, c);

        c.gridy = 2;
        panel.add(albumArtistLabel, c);

        c.gridy = 3;
        panel.add(albumLabel, c);

        c.gridy = 4;
        panel.add(fileNameLabel, c);

        c.gridy = 5;
        panel.add(pathLabel, c);

        c.gridy = 6;
        panel.add(durationLabel, c);

        c.gridy = 7;
        panel.add(trackLabel, c);

        c.gridy = 8;
        panel.add(discNumberLabel, c);

        c.gridy = 9;
        panel.add(genreLabel, c);

        c.gridy = 10;
        panel.add(yearLabel, c);

        c.gridy = 11;
        panel.add(composerLabel, c);

        c.gridy = 12;
        panel.add(bitrateLabel, c);

        c.gridy = 13;
        panel.add(frequencyLabel, c);

        c.gridx = 0;
        c.gridy = 14;
        c.insets = new Insets(10, 5, 15, 10);
        panel.add(editTagsButton, c);
	}

    /**
     * Fill picture.
     */
    private void fillPicture() {
        new FillPictureSwingWorker().execute();

    }

    /**
     * Sets the content.
     */
    private void setContent() {
        fillPicture();
        songLabel.fillText();
        artistLabel.fillText();
        albumArtistLabel.fillText();
        albumLabel.fillText();
        fileNameLabel.fillText();
        pathLabel.fillText();

        durationLabel.setText(getHtmlFormatted(I18nUtils.getString("DURATION"), StringUtils.seconds2String(file.getDuration())));
        trackLabel.setText(getHtmlFormatted(I18nUtils.getString("TRACK"), file.getTrackNumber() > 0 ? String.valueOf(file.getTrackNumber()) : "-"));
        discNumberLabel.setText(getHtmlFormatted(I18nUtils.getString("DISC_NUMBER"), file.getDiscNumber() > 0 ? String.valueOf(file.getDiscNumber()) : "-"));
        genreLabel.setText(getHtmlFormatted(I18nUtils.getString("GENRE"), StringUtils.isEmpty(file.getGenre()) ? "-" : file.getGenre()));
        yearLabel.setText(getHtmlFormatted(I18nUtils.getString("YEAR"), StringUtils.getNumberOrZero(file.getYear()) > 0 ? file.getYear() : "-"));
        composerLabel.setText(getHtmlFormatted(I18nUtils.getString("COMPOSER"), StringUtils.isEmpty(file.getComposer()) ? "-" : file.getComposer()));
        bitrateLabel.setText(getHtmlFormatted(I18nUtils.getString("BITRATE"), StringUtils.getString(Long.toString(file.getBitrate()), " Kbps")));
        frequencyLabel.setText(getHtmlFormatted(I18nUtils.getString("FREQUENCY"), StringUtils.getString(Integer.toString(file.getFrequency()), " Hz")));
    }

    private interface ValueProvider {
    	String getLabel();
    	String getValue();
        String getClearValue();
    }

    private abstract class AbstractFieldProvider implements ValueProvider {

        public abstract String getI18Name();

        public final String getValue() {
            String v = getClearValue();
            return StringUtils.isEmpty(v) ? "-" : v;
        }

        public final String getLabel() {
            return getHtmlFormatted(I18nUtils.getString(getI18Name()));
        }

    }

    private static class ProviderLabel extends JPanel {

        private static final long serialVersionUID = -2928151775717411054L;

        private final JLabel label;
        
        private final JTextField value;
        
        private final ValueProvider provider;

        
        public ProviderLabel(ValueProvider provider) {
        	super(new BorderLayout(10, 0)); 
            if (provider == null) {
                throw new IllegalArgumentException("provider pointer should not be null");
            }
            this.provider = provider;
        	label = new JLabel();
        	value = new CustomTextField();
        	value.setEditable(false);
        	add(label, BorderLayout.WEST);
        	add(value, BorderLayout.CENTER);
        }

        public void fillText() {
            label.setText(provider.getLabel());
            value.setText(provider.getValue());
        }
    }

    private class SongProvider extends AbstractFieldProvider {

        public String getI18Name() {
            return "SONG"; // noi18n
        }

        public String getClearValue() {
            return file.getTitle();
        }

    }

    private SongProvider songProvider = new SongProvider();

    private class ArtistProvider extends AbstractFieldProvider {

        public String getI18Name() {
            return "ARTIST";
        }

        public String getClearValue() {
            return file.getArtist();
        }
    }

    private ArtistProvider artistProvider = new ArtistProvider();

    private class AlbumArtistProvider extends AbstractFieldProvider {

        public String getI18Name() {
            return "ALBUM_ARTIST";
        }

        public String getClearValue() {
            return file.getAlbumArtist();
        }
    }

    private AlbumArtistProvider albumArtistProvider = new AlbumArtistProvider();

    private class AlbumProvider extends AbstractFieldProvider {

        public String getI18Name() {
            return "ALBUM";
        }

        public String getClearValue() {
            return file.getAlbum();
        }
    }

    private AlbumProvider albumProvider = new AlbumProvider();

    private class FileNameProvider extends AbstractFieldProvider {

        public String getI18Name() {
            return "FILE";
        }

        public String getClearValue() {
            return file.getFile().getName();
        }
    }

    private FileNameProvider fileNameProvider = new FileNameProvider();

    private class FilePathProvider extends AbstractFieldProvider {

        public String getI18Name() {
            return "LOCATION";
        }

        public String getClearValue() {
            return file.getFile().getAbsolutePath();
        }
    }

    private FilePathProvider filePathProvider = new FilePathProvider();
}
