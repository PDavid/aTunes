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

package net.sourceforge.atunes.model;

import java.util.Collections;
import java.util.Map;

public enum ViewMode {

    ARTIST, ALBUM, GENRE, FOLDER, YEAR;
    
    /**
     * Returns data from repository for this view
     * @param repository
     * @return
     */
    public Map<String, ?> getDataForView(Repository repository) {
		if (repository != null) {
			return getStructure(repository);
		} else {
			return Collections.emptyMap();
		}
    }

	/**
	 * @param repository
	 */
	private Map<String, ?> getStructure(Repository repository) {
		switch (this) {
		case YEAR: return repository.getYearStructure();
		case GENRE: return repository.getGenreStructure();
		case FOLDER : return repository.getFolderStructure();
		case ALBUM : return repository.getAlbumStructure();
		case ARTIST : return repository.getArtistStructure();
		default: return null;
		}
	}
}