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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.atunes.kernel.modules.tags.EditTagDialogController;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

/**
 * The properties dialog for audio files
 */
public final class LocalAudioObjectPropertiesDialog extends
		AudioObjectPropertiesDialog {

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

	private IAudioObjectImageLocator audioObjectImageLocator;

	private IUnknownObjectChecker unknownObjectChecker;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Instantiates a new audio file properties dialog.
	 * 
	 * @param frame
	 */
	LocalAudioObjectPropertiesDialog(final IFrame frame) {
		super(frame);
	}

	@Override
	public void setAudioObject(final IAudioObject audioObject) {
		if (audioObject instanceof ILocalAudioObject) {
			this.file = (ILocalAudioObject) audioObject;
			addContent(getLookAndFeel());
			setContent();
			this.pack();
		} else {
			throw new IllegalArgumentException("Not a ILocalAudioObject");
		}
	}

	/**
	 * @param audioObjectImageLocator
	 */
	public void setAudioObjectImageLocator(
			final IAudioObjectImageLocator audioObjectImageLocator) {
		this.audioObjectImageLocator = audioObjectImageLocator;
	}

	/**
	 * @param file
	 */
	public void setFile(final ILocalAudioObject file) {
		this.file = file;
		setTitle(getTitleText(file));
	}

	/**
	 * Gives a title for dialog.
	 * 
	 * @param file
	 *            the file
	 * 
	 * @return title for dialog
	 */
	private String getTitleText(final ILocalAudioObject file) {
		return StringUtils.getString(I18nUtils.getString("INFO_OF_FILE"), " ",
				file.getFile().getName());
	}

	/**
	 * Adds the content.
	 * 
	 * @param lookAndFeel
	 */
	private void addContent(final ILookAndFeel lookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());

		pictureLabel = new JLabel();
		songLabel = new ProviderLabel(new SongProvider());
		songLabel.setFont(lookAndFeel.getPropertiesDialogBigFont());
		artistLabel = new ProviderLabel(
				new ArtistProvider(unknownObjectChecker));
		artistLabel.setFont(lookAndFeel.getPropertiesDialogBigFont());
		albumArtistLabel = new ProviderLabel(new AlbumArtistProvider(
				unknownObjectChecker));
		albumArtistLabel.setFont(lookAndFeel.getPropertiesDialogBigFont());
		albumLabel = new ProviderLabel(new AlbumProvider(unknownObjectChecker));
		albumLabel.setFont(lookAndFeel.getPropertiesDialogBigFont());
		fileNameLabel = new ProviderLabel(new FileNameProvider());
		pathLabel = new ProviderLabel(new FilePathProvider());
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
			@Override
			public void actionPerformed(final ActionEvent evt) {
				EditTagDialogController ctl = beanFactory
						.getBean(EditTagDialogController.class);
				ctl.getComponentControlled().setPrevNextButtonsShown(false);
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
	private void setLayout(final JPanel panel, final JButton editTagsButton) {
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
		new FillPictureSwingWorker(audioObjectImageLocator, pictureLabel, file)
				.execute();
	}

	/**
	 * Sets the content.
	 */
	private void setContent() {
		fillPicture();
		songLabel.fillText(file);
		artistLabel.fillText(file);
		albumArtistLabel.fillText(file);
		albumLabel.fillText(file);
		fileNameLabel.fillText(file);
		pathLabel.fillText(file);

		durationLabel.setText(getHtmlFormatted(I18nUtils.getString("DURATION"),
				TimeUtils.secondsToHoursMinutesSeconds(file.getDuration())));
		trackLabel.setText(getHtmlFormatted(I18nUtils.getString("TRACK"), file
				.getTrackNumber() > 0 ? String.valueOf(file.getTrackNumber())
				: "-"));
		discNumberLabel.setText(getHtmlFormatted(I18nUtils
				.getString("DISC_NUMBER"),
				file.getDiscNumber() > 0 ? String.valueOf(file.getDiscNumber())
						: "-"));
		genreLabel.setText(getHtmlFormatted(I18nUtils.getString("GENRE"),
				StringUtils.isEmpty(file.getGenre(unknownObjectChecker)) ? "-"
						: file.getGenre(unknownObjectChecker)));
		yearLabel.setText(getHtmlFormatted(
				I18nUtils.getString("YEAR"),
				StringUtils.getNumberOrZero(file.getYear()) > 0 ? file
						.getYear() : "-"));
		composerLabel.setText(getHtmlFormatted(
				I18nUtils.getString("COMPOSER"),
				StringUtils.isEmpty(file.getComposer()) ? "-" : file
						.getComposer()));
		bitrateLabel
				.setText(getHtmlFormatted(I18nUtils.getString("BITRATE"),
						StringUtils.getString(Long.toString(file.getBitrate()),
								" Kbps")));
		frequencyLabel.setText(getHtmlFormatted(I18nUtils
				.getString("FREQUENCY"), StringUtils.getString(
				Integer.toString(file.getFrequency()), " Hz")));
	}
}
