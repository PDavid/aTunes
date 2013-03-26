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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationTableFactory;
import net.sourceforge.atunes.kernel.modules.context.ITracksTableListener;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Basic information about an album
 * 
 * @author alex
 * 
 */
public class AlbumBasicInfoContent extends
		AbstractContextPanelContent<AlbumInfoDataSource> {

	private static final long serialVersionUID = -5538266144953409867L;

	private JLabel albumCoverLabel;

	private UrlLabel albumLabel;

	private UrlLabel artistLabel;

	private UrlLabel yearLabel;

	private JTable tracksTable;

	private ContextInformationTableFactory contextInformationTableFactory;

	private ITracksTableListener contextTableURLOpener;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public String getContentName() {
		return I18nUtils.getString("INFO");
	}

	/**
	 * @param contextTableURLOpener
	 */
	public void setContextTableURLOpener(
			final ITracksTableListener contextTableURLOpener) {
		this.contextTableURLOpener = contextTableURLOpener;
	}

	/**
	 * @param contextInformationTableFactory
	 */
	public void setContextInformationTableFactory(
			final ContextInformationTableFactory contextInformationTableFactory) {
		this.contextInformationTableFactory = contextInformationTableFactory;
	}

	@Override
	public void updateContentFromDataSource(final AlbumInfoDataSource source) {
		IAudioObject audioObject = source.getAudioObject();
		IAlbumInfo album = source.getAlbumInfo();
		updateArtist(audioObject, album);
		updateAlbum(album);
		updateYear(album);
		updateAlbumCover(source);
		((ContextTracksTableModel) this.tracksTable.getModel()).setAlbum(album);
	}

	/**
	 * @param source
	 */
	private void updateAlbumCover(final AlbumInfoDataSource source) {
		ImageIcon image = source.getImage();
		ImageIcon imageIcon = null;
		if (image != null) {
			imageIcon = ImageUtils.resize(image,
					Constants.ALBUM_IMAGE_SIZE.getSize(),
					Constants.ALBUM_IMAGE_SIZE.getSize());
		} else {
			imageIcon = Images.getImage(Images.APP_LOGO_150);
		}
		this.albumCoverLabel.setIcon(imageIcon);
	}

	/**
	 * @param album
	 */
	private void updateYear(final IAlbumInfo album) {
		// TODO: wikipedia is opened in English
		this.yearLabel.setText(
				album != null ? album.getYear() : "",
				album != null && album.getYear() != null ? StringUtils
						.getString("http://en.wikipedia.org/wiki/",
								album.getYear()) : null);
	}

	/**
	 * @param album
	 */
	private void updateAlbum(final IAlbumInfo album) {
		this.albumLabel.setText(
				album != null ? album.getTitle() : I18nUtils
						.getString("UNKNOWN_ALBUM"),
				album != null ? album.getUrl() : null);
		this.albumLabel.setEnabled(album != null && album.getUrl() != null);
	}

	/**
	 * @param audioObject
	 * @param album
	 */
	private void updateArtist(final IAudioObject audioObject,
			final IAlbumInfo album) {
		this.artistLabel.setText(album != null ? album.getArtist()
				: audioObject.getArtist(this.unknownObjectChecker),
				album != null ? album.getArtistUrl() : null);
		this.artistLabel.setEnabled(album != null
				&& album.getArtistUrl() != null);
	}

	@Override
	public void clearContextPanelContent() {
		super.clearContextPanelContent();
		this.albumCoverLabel.setIcon(null);
		this.albumCoverLabel.setBorder(null);
		this.albumLabel.setText(null);
		this.artistLabel.setText(null);
		this.yearLabel.setText(null);
		((ContextTracksTableModel) this.tracksTable.getModel()).setAlbum(null);
	}

	@Override
	public Component getComponent() {
		// Create components
		this.albumCoverLabel = new JLabel();
		this.albumLabel = (UrlLabel) getControlsBuilder().getUrlLabel();
		this.albumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.albumLabel.setFont(getLookAndFeelManager().getCurrentLookAndFeel()
				.getContextInformationBigFont());
		this.artistLabel = (UrlLabel) getControlsBuilder().getUrlLabel();
		this.artistLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.yearLabel = (UrlLabel) getControlsBuilder().getUrlLabel();
		this.yearLabel.setHorizontalAlignment(SwingConstants.CENTER);

		this.tracksTable = this.contextInformationTableFactory
				.getNewTracksTable(this.contextTableURLOpener);
		this.tracksTable.setModel(new ContextTracksTableModel());

		// Add components
		return arrangeComponents();
	}

	/**
	 * @return
	 */
	private Component arrangeComponents() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 0, 0, 0);
		panel.add(this.albumCoverLabel, c);
		c.gridy = 1;
		c.insets = new Insets(5, 5, 0, 5);
		panel.add(this.albumLabel, c);
		c.gridy = 2;
		panel.add(this.artistLabel, c);
		c.gridy = 3;
		panel.add(this.yearLabel, c);
		c.gridy = 4;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(this.tracksTable, c);
		return panel;
	}
}
