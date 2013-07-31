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
 * Search field for integer properties of objects of type O
 * 
 * @author alex
 * 
 * @param <O>
 */
public abstract class IntegerSearchField<O> extends
		GenericSearchField<O, Integer> {

	private ISearchBinaryOperator<Integer> integerEqualsSearchOperator;

	private ISearchBinaryOperator<Integer> integerGreaterThanSearchOperator;

	private ISearchBinaryOperator<Integer> integerLessThanSearchOperator;

	/**
	 * @param integerEqualsSearchOperator
	 */
	public void setIntegerEqualsSearchOperator(
			final ISearchBinaryOperator<Integer> integerEqualsSearchOperator) {
		this.integerEqualsSearchOperator = integerEqualsSearchOperator;
	}

	/**
	 * @param integerGreaterThanSearchOperator
	 */
	public void setIntegerGreaterThanSearchOperator(
			final ISearchBinaryOperator<Integer> integerGreaterThanSearchOperator) {
		this.integerGreaterThanSearchOperator = integerGreaterThanSearchOperator;
	}

	/**
	 * @param integerLessThanSearchOperator
	 */
	public void setIntegerLessThanSearchOperator(
			final ISearchBinaryOperator<Integer> integerLessThanSearchOperator) {
		this.integerLessThanSearchOperator = integerLessThanSearchOperator;
	}

	@Override
	public List<ISearchOperator> getOperators() {
		return CollectionUtils.fillCollectionWithElements(
				new ArrayList<ISearchOperator>(),
				this.integerEqualsSearchOperator,
				this.integerLessThanSearchOperator,
				this.integerGreaterThanSearchOperator);
	}

	@Override
	public Integer transform(final String originalValue) {
		return Integer.parseInt(originalValue);
	}
}
