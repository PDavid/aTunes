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

import net.sourceforge.atunes.model.ISearchBinaryOperator;
import net.sourceforge.atunes.model.ISearchOperator;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * Search field for long properties for objects of type O
 * 
 * @author alex
 * 
 * @param <O>
 */
public abstract class LongSearchField<O> extends GenericSearchField<O, Long> {

	private ISearchBinaryOperator<Long> longEqualsSearchOperator;

	private ISearchBinaryOperator<Long> longGreaterThanSearchOperator;

	private ISearchBinaryOperator<Long> longLessThanSearchOperator;

	/**
	 * @param longEqualsSearchOperator
	 */
	public void setLongEqualsSearchOperator(
			final ISearchBinaryOperator<Long> longEqualsSearchOperator) {
		this.longEqualsSearchOperator = longEqualsSearchOperator;
	}

	/**
	 * @param longGreaterThanSearchOperator
	 */
	public void setLongGreaterThanSearchOperator(
			final ISearchBinaryOperator<Long> longGreaterThanSearchOperator) {
		this.longGreaterThanSearchOperator = longGreaterThanSearchOperator;
	}

	/**
	 * @param longLessThanSearchOperator
	 */
	public void setLongLessThanSearchOperator(
			final ISearchBinaryOperator<Long> longLessThanSearchOperator) {
		this.longLessThanSearchOperator = longLessThanSearchOperator;
	}

	@Override
	public List<ISearchOperator> getOperators() {
		return CollectionUtils.fillCollectionWithElements(
				new ArrayList<ISearchOperator>(),
				this.longEqualsSearchOperator, this.longLessThanSearchOperator,
				this.longGreaterThanSearchOperator);
	}

	@Override
	public Long transform(final String originalValue) {
		return Long.parseLong(originalValue);
	}
}
