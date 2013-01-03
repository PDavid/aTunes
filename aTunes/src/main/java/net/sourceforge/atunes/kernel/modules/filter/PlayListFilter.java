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

package net.sourceforge.atunes.kernel.modules.filter;

import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Filter for play list
 * @author alex
 *
 */
public final class PlayListFilter implements IFilter {

	private static final String PLAYLIST = "PLAYLIST";

	private PlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final PlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	public String getName() {
		return PLAYLIST;
	}

	@Override
	public String getDescription() {
		return I18nUtils.getString(PLAYLIST);
	}

	@Override
	public void applyFilter(final String filter) {
		this.playListHandler.setFilter(filter);
	}
}