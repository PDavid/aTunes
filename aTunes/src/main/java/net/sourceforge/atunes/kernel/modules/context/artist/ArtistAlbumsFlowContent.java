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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.ScrollableFlowPanel;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Shows artist albums in flow
 * 
 * @author alex
 * 
 */
public class ArtistAlbumsFlowContent extends
		AbstractContextPanelContent<ArtistAlbumListImagesDataSource> {

	private ScrollableFlowPanel coversPanel;

	@Override
	public Component getComponent() {
		coversPanel = new ScrollableFlowPanel();
		coversPanel.setOpaque(false);
		coversPanel.setLayout(new FlowLayout());
		return coversPanel;
	}

	@Override
	public String getContentName() {
		return I18nUtils.getString("ALBUMS");
	}

	@Override
	public void updateContentFromDataSource(
			ArtistAlbumListImagesDataSource source) {
		IAlbumListInfo albumListInfo = source.getAlbumList();
		if (albumListInfo != null) {
			List<IAlbumInfo> albums = albumListInfo.getAlbums();
			for (IAlbumInfo album : albums) {
				coversPanel.add(getLabelForAlbum(source, album));
			}
		}
		coversPanel.revalidate();
		coversPanel.repaint();
		coversPanel.validate();
	}

	@Override
	public void clearContextPanelContent() {
		super.clearContextPanelContent();
		coversPanel.removeAll();
	};

	/**
	 * Gets the Label for album.
	 * 
	 * @param source
	 * @param album
	 * @return
	 */
	private JLabel getLabelForAlbum(ArtistAlbumListImagesDataSource source,
			final IAlbumInfo album) {
		Icon cover = source.getCovers().get(album);
		final JLabel coverLabel = new JLabel(cover);
		coverLabel.setToolTipText(album.getTitle());
		if (cover == null) {
			coverLabel.setPreferredSize(new Dimension(
					Constants.THUMB_IMAGE_WIDTH, Constants.THUMB_IMAGE_HEIGHT));
			coverLabel.setBorder(BorderFactory.createLineBorder(GuiUtils
					.getBorderColor()));
		} else {
			coverLabel.setBorder(null);
		}

		coverLabel.addMouseListener(new CoverMouseAdapter(album, coverLabel,
				getDesktop()));

		return coverLabel;
	}

}
