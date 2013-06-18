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
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Year Search field
 * 
 * @author alex
 * 
 */
public class YearSearchField extends IntegerSearchField<IYear> {

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public String getName() {
		return I18nUtils.getString("YEAR");
	}

	@Override
	public List<IAudioObject> getAudioObjects(IYear year) {
		return new ArrayList<IAudioObject>(year.getAudioObjects());
	}

	@Override
	public Collection<IYear> getObjectsForEvaluation() {
		// Get only known years
		List<IYear> years = new ArrayList<IYear>();
		for (IYear year : repositoryHandler.getYears()) {
			if (StringUtils.isNumber(year.getName(null))) {
				years.add(year);
			}
		}
		return years;
	}

	@Override
	public Integer getValueForEvaluation(IYear year) {
		return Integer.parseInt(year.getName(null));
	}
}
