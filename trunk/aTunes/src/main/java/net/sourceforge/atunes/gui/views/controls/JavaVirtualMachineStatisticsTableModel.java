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

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class AboutDialogTableModel.
 */
public class JavaVirtualMachineStatisticsTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1786557125033788184L;
    
    /*
     * Static attributes with immutable data to be shown in properties table
     */
    static final String VERSION = Constants.VERSION.toString();
    static final String JAVA_VERSION = System.getProperty("java.version");
    static final String OS_NAME = StringUtils.getString(System.getProperty("os.name"), " (", System.getProperty("os.arch"), ')');
    
    private String usedHeapUsage;
	private String garbageCollectionCount;
	private String maxHeapUsage;
	private String initHeapUsage;
	private String committedHeapUsage;
	private String usedNonHeapUsage;
	private String maxNonHeapUsage;
	private String initNonHeapUsage;
	private String committedNonHeapUsage;
	private String uptime;
	private String totalLoadedClassCount;
	private String loadedClassCount;
	private String unloadedClassCount;
	private String threadCount;

    /**
     * Instantiates a new about dialog table model.
     */
    public JavaVirtualMachineStatisticsTableModel() {
    	refreshData();
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
        return 17;
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
    	if (columnIndex == 0) {
    		switch (rowIndex) {
			case 0: return "Version";
			case 1: return "Java Runtime Enviroment";
			case 2: return "OS";
			case 3: return "Used Heap Space";
			case 4: return "Max Heap Space";
			case 5: return "Initial Heap Space";
			case 6: return "Committed Heap Space";
			case 7: return "Used Non Heap Space";
			case 8: return "Max Non Heap Space";
			case 9: return "Initial Non Heap Space";
			case 10: return "Committed Non Heap Space";
			case 11: return "Uptime";
			case 12: return "Total Loaded Classes Count";
			case 13: return "Loaded Classes Count";
			case 14: return "Unloaded Classes Count";
			case 15: return "Thread Count";
			case 16: return "Garbage Collection Count";
			default:
				break;
			}
    	} else {
    		switch (rowIndex) {
			case 0: return VERSION;
			case 1: return JAVA_VERSION;
			case 2: return OS_NAME;
			case 3: return usedHeapUsage;
			case 4: return maxHeapUsage;
			case 5: return initHeapUsage;
			case 6: return committedHeapUsage;
			case 7: return usedNonHeapUsage;
			case 8: return maxNonHeapUsage;
			case 9: return initNonHeapUsage;
			case 10: return committedNonHeapUsage;
			case 11: return uptime;
			case 12: return totalLoadedClassCount;
			case 13: return loadedClassCount;
			case 14: return unloadedClassCount;
			case 15: return threadCount;
			case 16: return garbageCollectionCount;
			default:
				break;
			}        		
    	}
    	return null;
    }
    
    /**
     * Updates data from virtual machine
     */
    public void refreshData() {
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        usedHeapUsage = StringUtils.fromByteToMegaOrGiga(heapUsage.getUsed());
        maxHeapUsage = StringUtils.fromByteToMegaOrGiga(heapUsage.getMax());
        initHeapUsage = StringUtils.fromByteToMegaOrGiga(heapUsage.getInit());
        committedHeapUsage = StringUtils.fromByteToMegaOrGiga(heapUsage.getCommitted());
        
        MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        usedNonHeapUsage = StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getUsed());
        maxNonHeapUsage = StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getMax());
        initNonHeapUsage = StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getInit());
        committedNonHeapUsage = StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getCommitted());
        
        uptime = StringUtils.fromSecondsToHoursAndDays(ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        totalLoadedClassCount = String.valueOf(classLoadingMXBean.getTotalLoadedClassCount());
        loadedClassCount = String.valueOf(classLoadingMXBean.getLoadedClassCount());
        unloadedClassCount = String.valueOf(classLoadingMXBean.getUnloadedClassCount());
        
        threadCount = String.valueOf(ManagementFactory.getThreadMXBean().getThreadCount());
        
        long collectionCount = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            collectionCount += Math.max(0, garbageCollectorMXBean.getCollectionCount());
        }
        garbageCollectionCount = String.valueOf(collectionCount);
        
        fireTableDataChanged();
    }
}