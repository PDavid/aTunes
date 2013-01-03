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

package net.sourceforge.atunes.gui.views.decorators;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.AbstractTreeCellRendererCode;

/**
 * Renderer for each node of a navigation tree
 * 
 * @author alex
 * 
 */
public final class NavigationViewTreeCellRendererCode extends
	AbstractTreeCellRendererCode<JComponent, DefaultMutableTreeNode> {

    private final List<AbstractTreeCellDecorator<?, ?>> decorators;

    /**
     * @param list
     */
    public NavigationViewTreeCellRendererCode(
	    final List<AbstractTreeCellDecorator<?, ?>> list) {
	this.decorators = list;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public JComponent getComponent(final JComponent superComponent,
	    final JTree tree, final DefaultMutableTreeNode value,
	    final boolean isSelected, final boolean expanded,
	    final boolean leaf, final int row, final boolean isHasFocus) {
	for (AbstractTreeCellDecorator decorator : decorators) {
	    if (decorator.getComponentClass().isAssignableFrom(
		    superComponent.getClass())
		    && decorator.getValueClass().isAssignableFrom(
			    value.getUserObject().getClass())) {
		decorator.decorateTreeCellComponent(superComponent,
			value.getUserObject(), isSelected);
	    }
	}
	return superComponent;
    }
}