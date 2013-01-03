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

import javax.swing.JComponent;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanelRendererCode;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Renderer for albums of an artist in context panel
 * 
 * @author alex
 * 
 */
public class AlbumsTableCellRendererCode extends
		ContextTableRowPanelRendererCode<IAlbumInfo> {

	private ArtistAlbumListImagesDataSource source;

	/**
	 * @param source
	 */
	public void setSource(final ArtistAlbumListImagesDataSource source) {
		this.source = source;
	}

	@Override
	public String getCacheKeyControl(final IAlbumInfo a) {
		return a.getArtist();
	}

	@Override
	public ContextTableRowPanel<IAlbumInfo> createPanel(
			final JComponent superComponent, final IAlbumInfo value) {
		return getPanelForTableRenderer(this.source.getCovers().get(value),
				StringUtils.getString("<html>", value.getTitle(), "</html>"),
				Constants.THUMB_IMAGE_WIDTH);
	}
}