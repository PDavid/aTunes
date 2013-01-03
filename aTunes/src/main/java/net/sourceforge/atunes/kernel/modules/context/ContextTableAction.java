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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sourceforge.atunes.model.IDesktop;

/**
 * Action over an element of a context table
 * 
 * @author alex
 * 
 * @param <T>
 */
public abstract class ContextTableAction<T> extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2310966691761526236L;

	private ContextTable table;

	private IDesktop desktop;

	/**
	 * @param name
	 */
	public ContextTableAction(final String name) {
		super(name);
	}

	/**
	 * @param desktop
	 */
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	/**
	 * @param table
	 */
	public void setTable(final ContextTable table) {
		this.table = table;
	}

	@Override
	public final void actionPerformed(final ActionEvent e) {
		int row = this.table.getSelectedRow();
		if (row != -1) {
			T object = getSelectedObject(row);
			execute(object);
		}
	}

	/**
	 * @return context table
	 */
	public ContextTable getTable() {
		return this.table;
	}

	/**
	 * @return desktop
	 */
	protected IDesktop getDesktop() {
		return this.desktop;
	}

	/**
	 * Executes the action over the given object
	 * 
	 * @param object
	 */
	protected abstract void execute(T object);

	/**
	 * Returns selected object
	 * 
	 * @param row
	 * @return
	 */
	protected abstract T getSelectedObject(int row);

	/**
	 * Returns if this action is available for given object
	 * 
	 * @param object
	 * @return
	 */
	protected abstract boolean isEnabledForObject(Object object);

}
