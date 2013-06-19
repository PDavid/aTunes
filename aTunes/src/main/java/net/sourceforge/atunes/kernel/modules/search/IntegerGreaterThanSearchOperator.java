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

import net.sourceforge.atunes.model.ISearchBinaryOperator;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Search operator for "greater than" operation for integers
 * 
 * @author alex
 * 
 */
public class IntegerGreaterThanSearchOperator implements
		ISearchBinaryOperator<Integer> {

	@Override
	public String getDescription() {
		return I18nUtils.getString("IS_GREATER_THAN");
	}

	@Override
	public boolean evaluate(Integer o1, Integer o2) {
		if (o1 == null || o2 == null) {
			return false;
		}
		return o1.intValue() > o2.intValue();
	}
}
