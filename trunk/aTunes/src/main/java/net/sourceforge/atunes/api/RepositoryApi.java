/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.api;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public final class RepositoryApi {

    private RepositoryApi() {

    }

    /**
     * Returns artist information for the given artist name or <code>null</code>
     * if there is no information about
     * 
     * @param artistName
     * @return
     */
    public static Artist getArtist(String artistName) {
        return RepositoryHandler.getInstance().getArtistStructure().get(artistName);
    }
}
