/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.navigator;

import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ViewMode;

class TreeGeneratorFactory {

	/**
	 * Returns generator for given mode
	 * @param viewMode
	 * @param state
	 * @return
	 */
	static TreeGenerator getTreeGenerator(ViewMode viewMode, IState state) {
		switch (viewMode) {
		case ARTIST: return new ArtistTreeGenerator();
		case ALBUM: return new AlbumTreeGenerator();
		case GENRE: return new GenreTreeGenerator();
		case FOLDER: return new FolderTreeGenerator();
		case YEAR: return new YearTreeGenerator();
		case ARTIST_ALBUM: return new ArtistTreeGenerator();
		default: return null;
		}
	}
		
	

}
