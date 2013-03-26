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

package net.sourceforge.atunes.gui.views.controls;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IJavaVirtualMachineStatistic;

/**
 * The Class AboutDialogTableModel.
 */
public class JavaVirtualMachineStatisticsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1786557125033788184L;

	private List<IJavaVirtualMachineStatistic> statistics;

	/**
	 * Instantiates a new about dialog table model.
	 * 
	 * @param beanFactory
	 */
	public JavaVirtualMachineStatisticsTableModel(IBeanFactory beanFactory) {
		setStatisticsModel(beanFactory);
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		return column == 0 ? "Property" : "Value";
	}

	@Override
	public int getRowCount() {
		return statistics.size();
	}

	private void setStatisticsModel(IBeanFactory beanFactory) {
		this.statistics = new ArrayList<IJavaVirtualMachineStatistic>();
		this.statistics.addAll(beanFactory
				.getBeans(IJavaVirtualMachineStatistic.class));
	}

	@Override
	public String getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return this.statistics.get(rowIndex).getDescription();
		} else {
			return this.statistics.get(rowIndex).getValue();
		}
	}
}