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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Cell renderer for ITrackInfo
 * 
 * @author alex
 * 
 */
public class TrackInfoTableCellRendererCode extends
		AbstractTableCellRendererCode<JLabel, ITrackInfo> {

	private Color unknownElementForegroundColor;

	private IControlsBuilder controlsBuilder;

	private IIconFactory favoriteIcon;

	private IPlayerHandler playerHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * @param favoriteIcon
	 */
	public void setFavoriteIcon(final IIconFactory favoriteIcon) {
		this.favoriteIcon = favoriteIcon;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param unknownElementForegroundColor
	 */
	public void setUnknownElementForegroundColor(
			final Color unknownElementForegroundColor) {
		this.unknownElementForegroundColor = unknownElementForegroundColor;
	}

	@Override
	public JLabel getComponent(final JLabel superComponent, final JTable t,
			final ITrackInfo value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		if (value != null) {
			if (column == 0) {
				superComponent.setText(null);
				if (value.isFavorite()) {
					superComponent.setIcon(this.favoriteIcon
							.getIcon(getLookAndFeel()
									.getPaintForColorMutableIcon(
											superComponent, isSelected)));
				} else {
					superComponent.setIcon(null);
				}
			} else if (column == 1) {
				superComponent.setText(value.getTitle());
				superComponent.setIcon(null);
			} else if (column == 2) {
				superComponent.setText(value.getAlbum());
				superComponent.setIcon(null);
			}
			this.controlsBuilder.applyComponentOrientation(superComponent);
			if (!value.isAvailable()) {
				superComponent
						.setForeground(this.unknownElementForegroundColor);
			}
		}
		IAudioObject audioObject = this.playerHandler.getAudioObject();
		if (audioObject != null
				&& audioObject.getArtist(this.unknownObjectChecker)
						.equalsIgnoreCase(value.getArtist())
				&& audioObject.getTitle().equalsIgnoreCase(value.getTitle())) {
			superComponent.setFont(getLookAndFeel()
					.getPlayListSelectedItemFont());
		}
		return superComponent;
	}
}