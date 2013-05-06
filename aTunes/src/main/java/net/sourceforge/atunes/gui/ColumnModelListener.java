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

import java.util.concurrent.Future;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ITaskService;

class ColumnModelListener implements TableColumnModelListener {

	private Future<?> future;

	private AbstractCommonColumnModel model;

	private IBeanFactory beanFactory;

	/**
	 * @param abstractCommonColumnModel
	 * @param beanFactory
	 */
	public ColumnModelListener(
			AbstractCommonColumnModel abstractCommonColumnModel,
			IBeanFactory beanFactory) {
		this.model = abstractCommonColumnModel;
		this.beanFactory = beanFactory;
	}

	private void saveColumnSet() {
		if (this.future != null) {
			this.future.cancel(false);
		}
		this.future = this.beanFactory.getBean(ITaskService.class).submitOnce(
				"Save Column Model", 1, new Runnable() {

					@Override
					public void run() {
						// One second after last column width change save
						// column set
						// This is to avoid saving column set after each
						// column change event
						ColumnModelListener.this.model.getColumnSet()
								.saveColumnSet();
					}
				});
	}

	@Override
	public void columnAdded(final TableColumnModelEvent e) {
		saveColumnSet();
	}

	@Override
	public void columnMarginChanged(final ChangeEvent e) {
		this.model.updateColumnWidth();
		saveColumnSet();
	}

	@Override
	public void columnMoved(final TableColumnModelEvent e) {
		if (this.model.getColumnBeingMoved() == -1) {
			this.model.setColumnBeingMoved(e.getFromIndex());
		}
		this.model.setColumnMovedTo(e.getToIndex());
		saveColumnSet();
	}

	@Override
	public void columnRemoved(final TableColumnModelEvent e) {
		saveColumnSet();
	}

	@Override
	public void columnSelectionChanged(final ListSelectionEvent e) {
		// Nothing to do
	}
}