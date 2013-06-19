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
 * Search operator for "equals" operation of strings
 * 
 * @author alex
 * 
 */
public final class StringEqualsSearchOperator implements
		ISearchBinaryOperator<String> {

	@Override
	public String getDescription() {
		return I18nUtils.getString("IS_EQUALS_TO");
	}

	@Override
	public boolean evaluate(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		} else if (s1 != null && s2 != null) {
			return s1.equalsIgnoreCase(s2);
		}
		return false;
	}
}
