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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanelRendererCode;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Renderer for similar artists in context panel
 * 
 * @author alex
 * 
 */
public class SimilarArtistTableCellRendererCode extends
	ContextTableRowPanelRendererCode<IArtistInfo> {

    private IBeanFactory beanFactory;

    /**
     * @param beanFactory
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

    @Override
    public String getCacheKeyControl(final IArtistInfo a) {
	return a.getSimilarTo();
    }

    @Override
    public ContextTableRowPanel<IArtistInfo> createPanel(
	    final JComponent superComponent, final IArtistInfo value,
	    final boolean hasFocus) {
	return getPanelForTableRenderer(value.getImage(),
		StringUtils.getString(
			"<html><br>",
			value.getName(),
			"<br>",
			value.getMatch(),
			"%<br>",
			value.isAvailable() ? I18nUtils
				.getString("AVAILABLE_IN_REPOSITORY") : "",
			"</html>"), superComponent.getBackground(),
		superComponent.getForeground(), Constants.THUMB_IMAGE_WIDTH,
		Constants.THUMB_IMAGE_HEIGHT, hasFocus);
    }

    @Override
    public List<ContextTableAction<IArtistInfo>> getActions() {
	List<ContextTableAction<IArtistInfo>> actions = new ArrayList<ContextTableAction<IArtistInfo>>();
	ContextTableAction<IArtistInfo> readMore = beanFactory
		.getBean(ReadMoreContextTableAction.class);
	readMore.setTable(getTable());
	ContextTableAction<IArtistInfo> addAlbum = beanFactory
		.getBean(AddAlbumArtistToPlayListContextTableAction.class);
	addAlbum.setTable(getTable());
	actions.add(readMore);
	actions.add(addAlbum);
	return actions;
    }
}
