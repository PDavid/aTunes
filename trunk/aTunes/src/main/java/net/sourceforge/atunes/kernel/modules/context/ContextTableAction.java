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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sourceforge.atunes.model.IDesktop;

/**
 * Action over an element of a context table
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
	
	public ContextTableAction(String name, ContextTable table, IDesktop desktop) {
		super(name);
		this.table = table;
		this.desktop = desktop;
		
	}
	
	@Override
	public final void actionPerformed(ActionEvent e) {
		int row = this.table.getSelectedRow();
		if (row != -1) {
			T object = getSelectedObject(row);
			execute(object);
		}
	}

	protected IDesktop getDesktop() {
		return desktop;
	}
	
	/**
	 * Executes the action over the given object
	 * @param object
	 */
	protected abstract void execute(T object);

	/**
	 * Returns selected object
	 * @param row
	 * @return
	 */
	protected abstract T getSelectedObject(int row);
	
	/**
	 * Returns if this action is available for given object
	 * @param object
	 * @return
	 */
	protected abstract boolean isEnabledForObject(T object);
	

}
