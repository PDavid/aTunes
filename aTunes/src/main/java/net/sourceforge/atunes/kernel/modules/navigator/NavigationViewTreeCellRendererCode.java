/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.AbstractTreeCellRendererCode;

final class NavigationViewTreeCellRendererCode extends AbstractTreeCellRendererCode {
	
	private List<AbstractTreeCellDecorator<?, ?>> decorators;
	
	NavigationViewTreeCellRendererCode(List<AbstractTreeCellDecorator<?, ?>> list) {
		this.decorators = list;
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JComponent getComponent(JComponent superComponent, JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus) {
		Object valueObject = ((DefaultMutableTreeNode) value).getUserObject();
	    for (AbstractTreeCellDecorator decorator : decorators) {
	    	if (decorator.getComponentClass().isAssignableFrom(superComponent.getClass()) && decorator.getValueClass().isAssignableFrom(valueObject.getClass())) {
	    		decorator.decorateTreeCellComponent(superComponent, valueObject, isSelected);
	    	}
	    }
	    return superComponent;
	}
}