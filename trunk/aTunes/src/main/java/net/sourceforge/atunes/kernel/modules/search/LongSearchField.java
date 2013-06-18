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

import java.util.List;

import net.sourceforge.atunes.model.ISearchOperator;
import net.sourceforge.atunes.utils.CollectionUtils;

public abstract class LongSearchField<O> extends GenericSearchField<O, Long> {

	private ISearchOperator<Long> longEqualsSearchOperator;

	private ISearchOperator<Long> longGreaterThanSearchOperator;

	private ISearchOperator<Long> longLessThanSearchOperator;

	/**
	 * @param longEqualsSearchOperator
	 */
	public void setLongEqualsSearchOperator(
			ISearchOperator<Long> longEqualsSearchOperator) {
		this.longEqualsSearchOperator = longEqualsSearchOperator;
	}

	/**
	 * @param longGreaterThanSearchOperator
	 */
	public void setLongGreaterThanSearchOperator(
			ISearchOperator<Long> longGreaterThanSearchOperator) {
		this.longGreaterThanSearchOperator = longGreaterThanSearchOperator;
	}

	/**
	 * @param longLessThanSearchOperator
	 */
	public void setLongLessThanSearchOperator(
			ISearchOperator<Long> longLessThanSearchOperator) {
		this.longLessThanSearchOperator = longLessThanSearchOperator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ISearchOperator<Long>> getOperators() {
		return CollectionUtils.listWith(longEqualsSearchOperator,
				longLessThanSearchOperator, longGreaterThanSearchOperator);
	}

	@Override
	public Long transform(String originalValue) {
		return Long.parseLong(originalValue);
	}
}
