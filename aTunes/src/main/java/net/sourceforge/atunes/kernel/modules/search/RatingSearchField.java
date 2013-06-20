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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Rating Search field
 * 
 * @author alex
 * 
 */
public class RatingSearchField extends IntegerSearchField<IAudioObject> {

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public String getName() {
		return I18nUtils.getString("SCORE");
	}

	@Override
	public List<IAudioObject> getAudioObjects(IAudioObject ao) {
		return Collections.singletonList(ao);
	}

	@Override
	public Collection<IAudioObject> getObjectsForEvaluation() {
		return new ArrayList<IAudioObject>(
				repositoryHandler.getAudioFilesList());
	}

	@Override
	public Integer getValueForEvaluation(IAudioObject ao) {
		return ao.getStars();
	}
}
