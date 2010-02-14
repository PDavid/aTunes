package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Component;

public abstract class TreeCellDecorator {
	
	/**
	 * Decorates a tree cell component in some way given the user object
	 * @param component
	 * @param userObject
	 * @return
	 */
	public abstract Component decorateTreeCellComponent(Component component, Object userObject);
	

}
