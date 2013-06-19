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

package net.sourceforge.atunes.kernel.modules.search;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ISearchUnaryOperator;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Operator to check if element is favorite
 * 
 * @author alex
 * 
 */
public class AlbumFavoriteSearchOperator implements
		ISearchUnaryOperator<IAlbum> {

	private IFavoritesHandler favoritesHandler;

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}

	@Override
	public String getDescription() {
		return I18nUtils.getString("IS_IN_FAVORITES");
	}

	@Override
	public boolean evaluate(IAlbum album) {
		return this.favoritesHandler.isAlbumFavorite(album);
	}
}
