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

package net.sourceforge.atunes.gui;


import javax.swing.table.TableColumn;

import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.ITableCellRendererCode;

/**
 * The Class PlayListColumnModel.
 */
public final class PlayListColumnModel extends AbstractCommonColumnModel {

	private static final long serialVersionUID = -2211160302611944001L;

	private IPlayListHandler playListHandler;

	private IColumnSet playListColumnSet;

	private IPlayListTable playListTable;

	/**
	 * @param playListTable
	 */
	public void setPlayListTable(final IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}

	/**
	 * @param playListColumnSet
	 */
	public void setPlayListColumnSet(final IColumnSet playListColumnSet) {
		this.playListColumnSet = playListColumnSet;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Initialization needed
	 */
	public void initialize() {
		setTable(playListTable.getSwingComponent());
		setColumnSet(playListColumnSet);
		enableColumnChange(true);
	}

	@Override
	protected void reapplyFilter() {
		playListHandler.reapplyFilter();
	}

	@Override
	public void addColumn(final TableColumn aColumn) {
		super.addColumn(aColumn);
		updateColumnSettings(aColumn);

		// No header renderer is added to play list since user can change order of table manually by adding, removing or moving rows
		// so keep ordering has no sense
	}

	@Override
	public ITableCellRendererCode<?, ?> getRendererCodeFor(final Class<?> clazz) {
		AbstractTableCellRendererCode<?, ?> renderer = null;
		if (clazz.equals(Integer.class)) {
			renderer = getBeanFactory().getBean(PlayListIntegerTableCellRendererCode.class);
		} else if (clazz.equals(String.class)) {
			renderer = getBeanFactory().getBean(PlayListStringTableCellRendererCode.class);
		} else if (clazz.equals(TextAndIcon.class)) {
			renderer = getBeanFactory().getBean(PlayListTextAndIconTableCellRendererCode.class);
		} else {
			return super.getRendererCodeFor(clazz);
		}
		renderer.setModel(this);
		return renderer;
	}
}
