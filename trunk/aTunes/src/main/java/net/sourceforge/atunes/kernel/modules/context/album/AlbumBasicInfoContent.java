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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * Basic information about an album
 * 
 * @author alex
 * 
 */
public class AlbumBasicInfoContent extends AbstractContextPanelContent<AlbumInfoDataSource> {

	private static final long serialVersionUID = -5538266144953409867L;

	private JLabel albumCoverLabel;
	private UrlLabel albumLabel;
	private UrlLabel artistLabel;
	private UrlLabel yearLabel;

	@Override
	public String getContentName() {
		return I18nUtils.getString("INFO");
	}

	@Override
	public void updateContentFromDataSource(AlbumInfoDataSource source) {
		IAudioObject audioObject = source.getAudioObject();
		IAlbumInfo album = source.getAlbumInfo();
		artistLabel.setText(album != null ? album.getArtist() : audioObject.getArtist(), album != null ? album.getArtistUrl() : null);
		artistLabel.setEnabled(album != null && album.getArtistUrl() != null);
		albumLabel.setText(album != null ? album.getTitle() : I18nUtils.getString("UNKNOWN_ALBUM"), album != null ? album.getUrl() : null);
		albumLabel.setEnabled(album != null && album.getUrl() != null);
		// TODO: wikipedia is opened in English
		yearLabel.setText(album != null ? album.getYear() : "", album != null && album.getYear() != null ? StringUtils.getString("http://en.wikipedia.org/wiki/", album
				.getYear()) : null);

		ImageIcon image = source.getImage();
		ImageIcon imageIcon = null;
		if (image != null) {
			imageIcon = ImageUtils.resize(image, Constants.ALBUM_IMAGE_SIZE.getSize(), Constants.ALBUM_IMAGE_SIZE.getSize());
			albumCoverLabel.setBorder(Context.getBean(DropShadowBorder.class));
		} else {
			imageIcon = Images.getImage(Images.APP_LOGO_150);
		}
		albumCoverLabel.setIcon(imageIcon);
	}

	@Override
	public void clearContextPanelContent() {
		super.clearContextPanelContent();
		albumCoverLabel.setIcon(null);
		albumCoverLabel.setBorder(null);
		albumLabel.setText(null);
		artistLabel.setText(null);
		yearLabel.setText(null);
	}

	@Override
	public Component getComponent() {
		// Create components
		albumCoverLabel = new JLabel();
		albumLabel = new UrlLabel(getDesktop());
		albumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		albumLabel.setFont(getLookAndFeelManager().getCurrentLookAndFeel().getContextInformationBigFont());
		artistLabel = new UrlLabel(getDesktop());
		artistLabel.setHorizontalAlignment(SwingConstants.CENTER);
		yearLabel = new UrlLabel(getDesktop());
		yearLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Add components
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 0, 0, 0);
		panel.add(albumCoverLabel, c);
		c.gridy = 1;
		c.insets = new Insets(5, 5, 0, 5);
		panel.add(albumLabel, c);
		c.gridy = 2;
		panel.add(artistLabel, c);
		c.gridy = 3;
		panel.add(yearLabel, c);

		return panel;
	}

}
