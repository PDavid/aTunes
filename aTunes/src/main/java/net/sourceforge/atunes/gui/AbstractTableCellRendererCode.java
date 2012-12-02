/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.gui;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.ITableCellRendererCode;

/**
 * Table cell renderer
 * @author alex
 *
 * @param <T>
 * @param <U>
 */
public abstract class AbstractTableCellRendererCode<T extends JComponent, U> implements ITableCellRendererCode<T, U> {

	private ILookAndFeelManager lookAndFeelManager;

	private AbstractCommonColumnModel model;

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param model
	 */
	public void setModel(final AbstractCommonColumnModel model) {
		this.model = model;
	}

	/**
	 * @return
	 */
	protected final AbstractCommonColumnModel getModel() {
		return model;
	}

	/**
	 * Access lookAndFeel
	 * @return
	 */
	protected final ILookAndFeel getLookAndFeel() {
		return lookAndFeelManager.getCurrentLookAndFeel();
	}

	/**
	 * @param superComponent
	 * @param t
	 * @param value
	 * @param isSelected
	 * @param hasFocus
	 * @param row
	 * @param column
	 * @return
	 */
	@Override
	public abstract T getComponent(T superComponent, JTable t, U value, boolean isSelected, boolean hasFocus, int row, int column);
}
