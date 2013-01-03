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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import javax.swing.JComponent;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanelRendererCode;
import net.sourceforge.atunes.model.IVideoEntry;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Renderer for Youtube results in context panel
 * 
 * @author alex
 * 
 */
public class YoutubeResultsTableCellRendererCode extends
		ContextTableRowPanelRendererCode<IVideoEntry> {

	@Override
	public ContextTableRowPanel<IVideoEntry> createPanel(
			final JComponent superComponent, final IVideoEntry value) {
		return getPanelForTableRenderer(
				value.getImage(),
				StringUtils.getString("<html>", value.getName(), "<br>(",
						value.getDuration(), ")</html>"),
				Constants.THUMB_IMAGE_WIDTH);
	}

	@Override
	public String getCacheKeyControl(final IVideoEntry v) {
		return v.getArtist();
	}
}
