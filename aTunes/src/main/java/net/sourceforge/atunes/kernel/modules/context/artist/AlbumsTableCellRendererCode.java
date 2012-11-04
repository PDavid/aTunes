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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanelRendererCode;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Renderer for albums of an artist in context panel
 * 
 * @author alex
 * 
 */
public class AlbumsTableCellRendererCode extends
	ContextTableRowPanelRendererCode<IAlbumInfo> {

    private IBeanFactory beanFactory;

    private ArtistAlbumListImagesDataSource source;

    /**
     * @param beanFactory
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

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
	    final JComponent superComponent, final IAlbumInfo value,
	    final boolean hasFocus) {
	return getPanelForTableRenderer(source.getCovers().get(value),
		StringUtils.getString("<html>", value.getTitle(), "</html>"),
		superComponent.getBackground(), superComponent.getForeground(),
		Constants.THUMB_IMAGE_WIDTH, Constants.THUMB_IMAGE_HEIGHT,
		hasFocus);
    }

    @Override
    public List<ContextTableAction<IAlbumInfo>> getActions() {
	ContextTableAction<IAlbumInfo> action = beanFactory
		.getBean(OpenAlbumUrlAction.class);
	action.setTable(getTable());
	return Collections.singletonList(action);
    }
}