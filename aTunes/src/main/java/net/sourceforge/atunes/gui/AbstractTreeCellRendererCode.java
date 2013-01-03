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

package net.sourceforge.atunes.gui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;

import net.sourceforge.atunes.model.ITreeCellRendererCode;

/**
 * Tree cell renderer to implement
 * @author alex
 *
 * @param <T>
 * @param <U>
 */
public abstract class AbstractTreeCellRendererCode<T extends Component, U> implements ITreeCellRendererCode<T, U> {

	@Override
	public abstract JComponent getComponent(T superComponent, JTree tree, U value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus);

}
