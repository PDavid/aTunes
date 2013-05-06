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

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.ITrackInfo;

/**
 * Creates a table to show context information
 * 
 * @author alex
 * 
 */
public class ContextInformationTableFactory {

	private ILookAndFeelManager lookAndFeelManager;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Returns a new table to show tracks information from context
	 * 
	 * @param listener
	 * @return
	 */
	public JTable getNewTracksTable(final ITracksTableListener listener) {
		final JTable tracksTable = this.lookAndFeelManager
				.getCurrentLookAndFeel().getTable();
		tracksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tracksTable
				.setDefaultRenderer(
						ITrackInfo.class,
						this.lookAndFeelManager
								.getCurrentLookAndFeel()
								.getTableCellRenderer(
										this.beanFactory
												.getBean(TrackInfoTableCellRendererCode.class)));

		tracksTable.getTableHeader().setReorderingAllowed(true);
		tracksTable.getTableHeader().setResizingAllowed(false);
		tracksTable.setColumnModel(new TracksDefaultTableColumnModel());

		tracksTable.getSelectionModel().addListSelectionListener(
				new TracksTableListSelectionListener(listener, tracksTable));

		return tracksTable;
	}
}
