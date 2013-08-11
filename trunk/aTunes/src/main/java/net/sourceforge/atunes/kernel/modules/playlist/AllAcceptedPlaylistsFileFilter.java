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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.File;
import java.io.FilenameFilter;

import net.sourceforge.atunes.utils.I18nUtils;

/**
 * A file filter for all supported play lists files
 * 
 * @author alex
 * 
 */
final class AllAcceptedPlaylistsFileFilter implements FilenameFilter {

	private final PlayListM3UFileFilter m3uFilter;
	private final PlayListFileFilter filter;
	private final DynamicPlayListFileFilter dynamicFilter;

	/**
	 * Default constructor
	 */
	public AllAcceptedPlaylistsFileFilter() {
		this.m3uFilter = new PlayListM3UFileFilter();
		this.filter = new PlayListFileFilter();
		this.dynamicFilter = new DynamicPlayListFileFilter();
	}

	@Override
	public boolean accept(final File dir, final String name) {
		return this.m3uFilter.accept(dir, name)
				|| this.filter.accept(dir, name)
				|| this.dynamicFilter.accept(dir, name);
	}

	@Override
	public String toString() {
		return I18nUtils.getString("PLAYLIST");
	}
}