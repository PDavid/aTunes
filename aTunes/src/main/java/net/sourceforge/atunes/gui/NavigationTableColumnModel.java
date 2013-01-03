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

import javax.swing.JComponent;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITableCellRendererCode;

/**
 * Column model for navigation table
 * 
 * @author alex
 * 
 */
public final class NavigationTableColumnModel extends AbstractCommonColumnModel {

	private static final long serialVersionUID = 1071222881574684439L;

	private INavigationHandler navigationHandler;

	private ITable navigationTable;

	/**
	 * @param navigationTable
	 */
	public void setNavigationTable(final ITable navigationTable) {
		this.navigationTable = navigationTable;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * Initialization needed
	 */
	public void initialize() {
		setTable(this.navigationTable.getSwingComponent());
		enableColumnChange(true);
	}

	@Override
	public void addColumn(final TableColumn aColumn) {
		updateColumnSettings(aColumn);
		super.addColumn(aColumn);
	}

	@Override
	protected void reapplyFilter() {
		this.navigationHandler.updateViewTable();
	}

	@Override
	public ITableCellRendererCode<?, ?> getRendererCodeFor(final Class<?> clazz) {
		@SuppressWarnings("unchecked")
		ITableCellRendererCode<JComponent, Object> renderer = (ITableCellRendererCode<JComponent, Object>) super
				.getRendererCodeFor(clazz);

		NavigationTableCellRendererCode navigationRenderer = getBeanFactory()
				.getBean(NavigationTableCellRendererCode.class);
		navigationRenderer.setRenderer(renderer);
		return navigationRenderer;
	}
}
