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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchOperator;
import net.sourceforge.atunes.model.ISearchUnaryOperator;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Album Search field
 * 
 * @author alex
 * 
 */
public class AlbumSearchField extends StringSearchField<IAlbum> {

	private IRepositoryHandler repositoryHandler;

	private ISearchUnaryOperator<IAlbum> albumFavoriteSearchOperator;

	/**
	 * @param albumFavoriteSearchOperator
	 */
	public void setAlbumFavoriteSearchOperator(
			ISearchUnaryOperator<IAlbum> albumFavoriteSearchOperator) {
		this.albumFavoriteSearchOperator = albumFavoriteSearchOperator;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public List<ISearchOperator> getOperators() {
		List<ISearchOperator> operators = super.getOperators();
		operators.add(albumFavoriteSearchOperator);
		return operators;
	}

	@Override
	public String getName() {
		return I18nUtils.getString("ALBUM");
	}

	@Override
	public List<IAudioObject> getAudioObjects(IAlbum album) {
		return new ArrayList<IAudioObject>(album.getAudioObjects());
	}

	@Override
	public List<IAlbum> getObjectsForEvaluation() {
		return this.repositoryHandler.getAlbums();
	}

	@Override
	public String getValueForEvaluation(IAlbum album) {
		return album.getName();
	}
}
