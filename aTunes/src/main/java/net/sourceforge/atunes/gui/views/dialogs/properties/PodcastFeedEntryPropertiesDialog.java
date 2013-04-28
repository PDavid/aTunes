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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDateFormatter;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

import org.joda.time.base.BaseDateTime;

/**
 * The properties dialog for podcast feed entries.
 */
public final class PodcastFeedEntryPropertiesDialog extends
		AudioObjectPropertiesDialog {

	private static final long serialVersionUID = -2472573171771586037L;

	private JLabel pictureLabel;
	private JLabel titleLabel;
	private JLabel artistLabel;
	private JLabel urlLabel;
	private JLabel durationLabel;
	private JLabel dateLabel;
	private JLabel podcastFeedLabel;
	private JLabel downloadedLabel;
	private JLabel descriptionLabel;
	private JTextArea descriptionTextArea;
	private IPodcastFeedEntry entry;

	private IUnknownObjectChecker unknownObjectChecker;

	private IIconFactory rssMediumIcon;

	private IDateFormatter dateFormatter;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param dateFormatter
	 */
	public void setDateFormatter(final IDateFormatter dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	/**
	 * @param rssMediumIcon
	 */
	public void setRssMediumIcon(final IIconFactory rssMediumIcon) {
		this.rssMediumIcon = rssMediumIcon;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Instantiates a new podcast feed entry properties dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	PodcastFeedEntryPropertiesDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, controlsBuilder);
	}

	/**
	 * @param entry
	 */
	public void setEntry(final IPodcastFeedEntry entry) {
		this.entry = entry;
		setTitle(getTitleText(entry));
	}

	@Override
	public void initialize() {
	}

	@Override
	public void setAudioObject(final IAudioObject audioObject) {
		if (audioObject instanceof IPodcastFeedEntry) {
			this.entry = (IPodcastFeedEntry) audioObject;
			addContent(getLookAndFeel());
			setContent();
			getControlsBuilder().applyComponentOrientation(this);
		} else {
			throw new IllegalArgumentException("Not a IPodcastFeedEntry");
		}
	}

	/**
	 * Gives a title for dialog.
	 * 
	 * @param entry
	 *            the entry
	 * 
	 * @return title for dialog
	 */
	private static String getTitleText(final IPodcastFeedEntry entry) {
		return StringUtils.getString(
				I18nUtils.getString("INFO_OF_PODCAST_FEED"), " ",
				entry.getTitle());
	}

	/**
	 * Adds the content.
	 * 
	 * @param iLookAndFeel
	 */
	private void addContent(final ILookAndFeel iLookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());

		this.pictureLabel = new JLabel();
		this.titleLabel = new JLabel();
		this.titleLabel.setFont(iLookAndFeel.getPropertiesDialogBigFont());
		this.artistLabel = new JLabel();
		this.artistLabel.setFont(iLookAndFeel.getPropertiesDialogBigFont());
		this.urlLabel = new JLabel();
		this.urlLabel.setFont(iLookAndFeel.getPropertiesDialogBigFont());
		this.durationLabel = new JLabel();
		this.dateLabel = new JLabel();
		this.podcastFeedLabel = new JLabel();
		this.downloadedLabel = new JLabel();
		this.descriptionLabel = new JLabel();
		this.descriptionTextArea = getControlsBuilder().createTextArea();
		this.descriptionTextArea.setEditable(false);
		this.descriptionTextArea.setLineWrap(true);
		this.descriptionTextArea.setWrapStyleWord(true);
		this.descriptionTextArea.setOpaque(false);
		this.descriptionTextArea.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane descriptionScrollPane = this.controlsBuilder
				.createScrollPane(this.descriptionTextArea);
		descriptionScrollPane.setMinimumSize(new Dimension(400, 100));

		arrangePanel(panel, descriptionScrollPane);

		add(panel);
	}

	/**
	 * @param panel
	 * @param descriptionScrollPane
	 */
	private void arrangePanel(final JPanel panel,
			final JScrollPane descriptionScrollPane) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.insets = new Insets(20, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(this.pictureLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(this.titleLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		panel.add(this.artistLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		panel.add(this.urlLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		panel.add(this.durationLabel, c);

		c.gridx = 1;
		c.gridy = 4;
		panel.add(this.dateLabel, c);

		c.gridx = 1;
		c.gridy = 5;
		panel.add(this.podcastFeedLabel, c);

		c.gridx = 1;
		c.gridy = 6;
		panel.add(this.downloadedLabel, c);

		c.gridx = 1;
		c.gridy = 7;
		c.insets = new Insets(5, 10, 0, 10);
		panel.add(this.descriptionLabel, c);

		c.gridx = 1;
		c.gridy = 8;
		c.insets = new Insets(0, 10, 20, 10);
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(descriptionScrollPane, c);
	}

	/**
	 * Fill picture.
	 */
	private void fillPicture() {
		ImageIcon picture = this.rssMediumIcon.getIcon(getLookAndFeel()
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
				StringUtils.isEmpty(this.entry.getTitle()) ? "-" : this.entry
						.getTitle()));
		this.artistLabel.setText(getHtmlFormatted(
				I18nUtils.getString("ARTIST"), StringUtils.isEmpty(this.entry
						.getArtist(this.unknownObjectChecker)) ? "-"
						: this.entry.getArtist(this.unknownObjectChecker)));
		this.urlLabel.setText(getHtmlFormatted(I18nUtils.getString("URL"),
				this.entry.getUrl()));
		if (this.entry.getDuration() > 0) {
			this.durationLabel.setText(getHtmlFormatted(I18nUtils
					.getString("DURATION"), TimeUtils
					.secondsToHoursMinutesSeconds(this.entry.getDuration())));
		} else {
			this.durationLabel.setText(getHtmlFormatted(
					I18nUtils.getString("DURATION"), "-"));
		}
		if (this.entry.getDate() != null) {
			setDate(this.entry.getDate());
		} else {
			this.dateLabel.setText(getHtmlFormatted(
					I18nUtils.getString("DATE"), "-"));
		}
		this.podcastFeedLabel.setText(getHtmlFormatted(I18nUtils
				.getString("PODCAST_FEED"), this.entry.getPodcastFeed()
				.getName()));
		this.downloadedLabel.setText(getHtmlFormatted(I18nUtils
				.getString("DOWNLOADED"),
				this.entry.isDownloaded() ? I18nUtils.getString("YES")
						: I18nUtils.getString("NO")));
		this.descriptionLabel.setText(getHtmlFormatted(
				I18nUtils.getString("DESCRIPTION"), ""));
		this.descriptionTextArea.setText(this.entry.getDescription());
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				PodcastFeedEntryPropertiesDialog.this.descriptionTextArea
						.setCaretPosition(0);
			}
		});
	}

	/**
	 * @param date
	 */
	private void setDate(final BaseDateTime date) {
		this.dateLabel.setText(getHtmlFormatted(I18nUtils.getString("DATE"),
				this.dateFormatter.toString(date)));
	}
}
