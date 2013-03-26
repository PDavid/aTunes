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
import javax.swing.JList;

import net.sourceforge.atunes.model.IListCellRendererCode;

/**
 * Custom renderer code for lists
 * 
 * @author alex
 * 
 * @param <T>
 * @param <U>
 */
public abstract class AbstractListCellRendererCode<T extends Component, U>
		implements IListCellRendererCode<T, U> {

	@Override
	public abstract JComponent getComponent(T superComponent, JList list,
			U value, int index, boolean isSelected, boolean cellHasFocus);

}
