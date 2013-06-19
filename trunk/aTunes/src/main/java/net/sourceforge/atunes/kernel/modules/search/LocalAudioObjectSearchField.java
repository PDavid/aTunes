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

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchOperator;
import net.sourceforge.atunes.model.ISearchUnaryOperator;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Artist Search field
 * 
 * @author alex
 * 
 */
public class LocalAudioObjectSearchField extends
		GenericSearchField<ILocalAudioObject, ILocalAudioObject> {

	private IRepositoryHandler repositoryHandler;

	private ISearchUnaryOperator<IArtist> localAudioObjectFavoriteSearchOperator;

	/**
	 * @param localAudioObjectFavoriteSearchOperator
	 */
	public void setLocalAudioObjectFavoriteSearchOperator(
			ISearchUnaryOperator<IArtist> localAudioObjectFavoriteSearchOperator) {
		this.localAudioObjectFavoriteSearchOperator = localAudioObjectFavoriteSearchOperator;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public List<ISearchOperator> getOperators() {
		return CollectionUtils.fillCollectionWithElements(
				new ArrayList<ISearchOperator>(),
				localAudioObjectFavoriteSearchOperator);
	}

	@Override
	public String getName() {
		return I18nUtils.getString("SONG");
	}

	@Override
	public List<IAudioObject> getAudioObjects(ILocalAudioObject ao) {
		return CollectionUtils.fillCollectionWithElements(
				new ArrayList<IAudioObject>(), ao);
	}

	@Override
	public List<ILocalAudioObject> getObjectsForEvaluation() {
		return new ArrayList<ILocalAudioObject>(
				this.repositoryHandler.getAudioFilesList());
	}

	@Override
	public ILocalAudioObject getValueForEvaluation(ILocalAudioObject object) {
		return object;
	}

	@Override
	public ILocalAudioObject transform(String originalValue) {
		return null;
	}
}
