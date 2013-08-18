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

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.ISearchRule;

class SearchRuleCreatorHelper {

	static boolean isLogicalNode(final DefaultMutableTreeNode value) {
		return value != null
				&& value.getUserObject() instanceof ILogicalSearchOperator;
	}

	static boolean isSearchRule(final DefaultMutableTreeNode value) {
		return value != null && value.getUserObject() instanceof ISearchRule;
	}

	static ILogicalSearchOperator getLogicalNode(
			final DefaultMutableTreeNode value) {
		return isLogicalNode(value) ? (ILogicalSearchOperator) value
				.getUserObject() : null;
	}

	static ISearchRule getSearchRule(final DefaultMutableTreeNode value) {
		return isSearchRule(value) ? (ISearchRule) value.getUserObject() : null;
	}
}
