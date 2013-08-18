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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.ISearchRule;
import net.sourceforge.atunes.model.ITreeCellRendererCode;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * @author alex
 * 
 */
class RuleTreeCellRenderer implements
		ITreeCellRendererCode<JComponent, DefaultMutableTreeNode> {

	@Override
	public JComponent getComponent(final JComponent superComponent,
			final JTree tree, final DefaultMutableTreeNode value,
			final boolean isSelected, final boolean expanded,
			final boolean leaf, final int row, final boolean isHasFocus) {
		ILogicalSearchOperator logical = SearchRuleCreatorHelper
				.getLogicalNode(value);
		ISearchRule rule = SearchRuleCreatorHelper.getSearchRule(value);
		if (logical != null) {
			((JLabel) superComponent).setText(logical.getDescription());
		} else if (rule != null) {
			((JLabel) superComponent).setText(StringUtils.getString(rule
					.getField().getName(), " ", rule.getOperator()
					.getDescription(), " ", rule.getValue()));
		}
		return superComponent;
	}

}
