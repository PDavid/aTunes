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

package net.sourceforge.atunes.gui.views.controls;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.utils.JavaCommittedHeapSpaceStatistic;
import net.sourceforge.atunes.utils.JavaCommittedNonHeapSpaceStatistic;
import net.sourceforge.atunes.utils.JavaGarbageCollectionCountStatistic;
import net.sourceforge.atunes.utils.JavaInitialHeapSpaceStatistic;
import net.sourceforge.atunes.utils.JavaInitialNonHeapSpaceStatistic;
import net.sourceforge.atunes.utils.JavaLoadedClassesCountStatistic;
import net.sourceforge.atunes.utils.JavaMaxHeapSpaceStatistic;
import net.sourceforge.atunes.utils.JavaMaxNonHeapSpaceStatistic;
import net.sourceforge.atunes.utils.JavaOsNameStatistic;
import net.sourceforge.atunes.utils.JavaRuntimeStatistic;
import net.sourceforge.atunes.utils.JavaThreadCountStatistic;
import net.sourceforge.atunes.utils.JavaTotalLoadedClassesCountStatistic;
import net.sourceforge.atunes.utils.JavaUnloadedClassesCountStatistic;
import net.sourceforge.atunes.utils.JavaUptimeStatistic;
import net.sourceforge.atunes.utils.JavaUsedHeapSpaceStatistic;
import net.sourceforge.atunes.utils.JavaUsedNonHeapSpaceStatistic;
import net.sourceforge.atunes.utils.JavaVirtualMachineStatistic;


/**
 * The Class AboutDialogTableModel.
 */
public class JavaVirtualMachineStatisticsTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1786557125033788184L;
    
	private List<JavaVirtualMachineStatistic> statistics;

    /**
     * Instantiates a new about dialog table model.
     */
    public JavaVirtualMachineStatisticsTableModel() {
    	setStatisticsModel();
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
    
    private void setStatisticsModel() {
    	this.statistics = new ArrayList<JavaVirtualMachineStatistic>();
    	this.statistics.add(new JavaRuntimeStatistic());
    	this.statistics.add(new JavaOsNameStatistic());
    	this.statistics.add(new JavaUsedHeapSpaceStatistic());
    	this.statistics.add(new JavaMaxHeapSpaceStatistic());
    	this.statistics.add(new JavaInitialHeapSpaceStatistic());
    	this.statistics.add(new JavaCommittedHeapSpaceStatistic());
    	this.statistics.add(new JavaUsedNonHeapSpaceStatistic());
    	this.statistics.add(new JavaMaxNonHeapSpaceStatistic());
    	this.statistics.add(new JavaInitialNonHeapSpaceStatistic());
    	this.statistics.add(new JavaCommittedNonHeapSpaceStatistic());
    	this.statistics.add(new JavaUptimeStatistic());
    	this.statistics.add(new JavaTotalLoadedClassesCountStatistic());
    	this.statistics.add(new JavaLoadedClassesCountStatistic());
    	this.statistics.add(new JavaUnloadedClassesCountStatistic());
    	this.statistics.add(new JavaThreadCountStatistic());
    	this.statistics.add(new JavaGarbageCollectionCountStatistic());
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