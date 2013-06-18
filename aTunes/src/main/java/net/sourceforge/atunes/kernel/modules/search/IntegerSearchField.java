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

public abstract class IntegerSearchField<O> extends
		GenericSearchField<O, Integer> {

	private ISearchOperator<Integer> integerEqualsSearchOperator;

	private ISearchOperator<Integer> integerGreaterThanSearchOperator;

	private ISearchOperator<Integer> integerLessThanSearchOperator;

	/**
	 * @param integerEqualsSearchOperator
	 */
	public void setIntegerEqualsSearchOperator(
			ISearchOperator<Integer> integerEqualsSearchOperator) {
		this.integerEqualsSearchOperator = integerEqualsSearchOperator;
	}

	/**
	 * @param integerGreaterThanSearchOperator
	 */
	public void setIntegerGreaterThanSearchOperator(
			ISearchOperator<Integer> integerGreaterThanSearchOperator) {
		this.integerGreaterThanSearchOperator = integerGreaterThanSearchOperator;
	}

	/**
	 * @param integerLessThanSearchOperator
	 */
	public void setIntegerLessThanSearchOperator(
			ISearchOperator<Integer> integerLessThanSearchOperator) {
		this.integerLessThanSearchOperator = integerLessThanSearchOperator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ISearchOperator<Integer>> getOperators() {
		return CollectionUtils
				.listWith(integerEqualsSearchOperator,
						integerLessThanSearchOperator,
						integerGreaterThanSearchOperator);
	}

	@Override
	public Integer transform(String originalValue) {
		return Integer.parseInt(originalValue);
	}
}
