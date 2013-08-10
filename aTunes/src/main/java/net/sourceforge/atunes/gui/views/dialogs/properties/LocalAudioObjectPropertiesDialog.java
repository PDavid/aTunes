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
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFileManager;
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

	private IUnknownObjectChecker unknownObjectChecker;

	private IBeanFactory beanFactory;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
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
	 * @param controlsBuilder
	 */
	LocalAudioObjectPropertiesDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, controlsBuilder);
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
				this.fileManager.getFileName(file));
	}

	/**
	 * Adds the content.
	 * 
	 * @param lookAndFeel
	 */
	private void addContent(final ILookAndFeel lookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());

		this.pictureLabel = new JLabel();
		this.songLabel = new ProviderLabel(new SongProvider(),
				getControlsBuilder());
		this.songLabel.setFont(lookAndFeel.getPropertiesDialogBigFont());
		this.artistLabel = new ProviderLabel(new ArtistProvider(
				this.unknownObjectChecker), getControlsBuilder());
		this.artistLabel.setFont(lookAndFeel.getPropertiesDialogBigFont());
		this.albumArtistLabel = new ProviderLabel(new AlbumArtistProvider(
				this.unknownObjectChecker), getControlsBuilder());
		this.albumArtistLabel.setFont(lookAndFeel.getPropertiesDialogBigFont());
		this.albumLabel = new ProviderLabel(new AlbumProvider(
				this.unknownObjectChecker), getControlsBuilder());
		this.albumLabel.setFont(lookAndFeel.getPropertiesDialogBigFont());
		this.fileNameLabel = new ProviderLabel(new FileNameProvider(
				this.fileManager), getControlsBuilder());
		this.pathLabel = new ProviderLabel(new FilePathProvider(
				this.fileManager), getControlsBuilder());
		this.durationLabel = new JLabel();
		this.trackLabel = new JLabel();
		this.discNumberLabel = new JLabel();
		this.genreLabel = new JLabel();
		this.yearLabel = new JLabel();
		this.composerLabel = new JLabel();
		this.bitrateLabel = new JLabel();
		this.frequencyLabel = new JLabel();

		JButton editTagsButton = new JButton();
		editTagsButton.setText(I18nUtils.getString("EDIT_TAG"));
		editTagsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				EditTagDialogController ctl = LocalAudioObjectPropertiesDialog.this.beanFactory
						.getBean(EditTagDialogController.class);
				ctl.editFiles(java.util.Collections
						.singletonList(LocalAudioObjectPropertiesDialog.this.file));
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
		c.insets = new Insets(15, 20, 5, 0);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		panel.add(this.pictureLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(15, 20, 0, 20);
		panel.add(this.songLabel, c);

		c.gridy = 1;
		panel.add(this.artistLabel, c);

		c.gridy = 2;
		panel.add(this.albumArtistLabel, c);

		c.gridy = 3;
		panel.add(this.albumLabel, c);

		c.gridy = 4;
		panel.add(this.fileNameLabel, c);

		c.gridy = 5;
		panel.add(this.pathLabel, c);

		c.gridy = 6;
		panel.add(this.durationLabel, c);

		c.gridy = 7;
		panel.add(this.trackLabel, c);

		c.gridy = 8;
		panel.add(this.discNumberLabel, c);

		c.gridy = 9;
		panel.add(this.genreLabel, c);

		c.gridy = 10;
		panel.add(this.yearLabel, c);

		c.gridy = 11;
		panel.add(this.composerLabel, c);

		c.gridy = 12;
		panel.add(this.bitrateLabel, c);

		c.gridy = 13;
		panel.add(this.frequencyLabel, c);

		c.gridx = 0;
		c.gridy = 14;
		c.insets = new Insets(10, 5, 15, 10);
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		panel.add(editTagsButton, c);
	}

	/**
	 * Fill picture.
	 */
	private void fillPicture() {
		FillPictureBackgroundWorker worker = this.beanFactory
				.getBean(FillPictureBackgroundWorker.class);
		worker.setPictureLabel(this.pictureLabel);
		worker.setFile(this.file);
		worker.execute();
	}

	/**
	 * Sets the content.
	 */
	private void setContent() {
		fillPicture();
		this.songLabel.fillText(this.file);
		this.artistLabel.fillText(this.file);
		this.albumArtistLabel.fillText(this.file);
		this.albumLabel.fillText(this.file);
		this.fileNameLabel.fillText(this.file);
		this.pathLabel.fillText(this.file);

		this.durationLabel
				.setText(getHtmlFormatted(I18nUtils.getString("DURATION"),
						TimeUtils.secondsToHoursMinutesSeconds(this.file
								.getDuration())));
		this.trackLabel.setText(getHtmlFormatted(
				I18nUtils.getString("TRACK"),
				this.file.getTrackNumber() > 0 ? String.valueOf(this.file
						.getTrackNumber()) : "-"));
		this.discNumberLabel.setText(getHtmlFormatted(
				I18nUtils.getString("DISC_NUMBER"),
				this.file.getDiscNumber() > 0 ? String.valueOf(this.file
						.getDiscNumber()) : "-"));
		this.genreLabel.setText(getHtmlFormatted(
				I18nUtils.getString("GENRE"),
				StringUtils.isEmpty(this.file
						.getGenre(this.unknownObjectChecker)) ? "-" : this.file
						.getGenre(this.unknownObjectChecker)));
		this.yearLabel.setText(getHtmlFormatted(
				I18nUtils.getString("YEAR"),
				StringUtils.getNumberOrZero(this.file
						.getYear(this.unknownObjectChecker)) > 0 ? this.file
						.getYear(this.unknownObjectChecker) : "-"));
		this.composerLabel.setText(getHtmlFormatted(I18nUtils
				.getString("COMPOSER"), StringUtils.isEmpty(this.file
				.getComposer()) ? "-" : this.file.getComposer()));
		this.bitrateLabel.setText(getHtmlFormatted(I18nUtils
				.getString("BITRATE"), StringUtils.getString(
				Long.toString(this.file.getBitrate()), " Kbps")));
		this.frequencyLabel.setText(getHtmlFormatted(
				I18nUtils.getString("FREQUENCY"),
				StringUtils.getString(
						Integer.toString(this.file.getFrequency()), " Hz")));
	}
}
