/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.columns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

/**
 * Class to handle column sets and plugins
 * 
 * @author fleax
 * 
 */
public class ColumnSets implements PluginListener {

    /**
     * Column sets
     */
    private static List<ColumnSet> columnSets;

    /**
     * Singleton instance
     */
    private static ColumnSets instance;
    
    private Logger logger;

    public static ColumnSets getInstance() {
        if (instance == null) {
            instance = new ColumnSets();
        }
        return instance;
    }

    /**
     * Column sets
     * 
     * @return
     */
    private static List<ColumnSet> getColumnSets() {
        if (columnSets == null) {
            columnSets = new ArrayList<ColumnSet>();
        }
        return columnSets;
    }

    /**
     * Register a new column set
     * 
     * @param columnSet
     */
    protected static void registerColumnSet(ColumnSet columnSet) {
        getColumnSets().add(columnSet);
    }

    /**
     * Store columns settings
     */
    public static void storeColumnSettings() {
        for (ColumnSet columnSet : getColumnSets()) {
            columnSet.storeCurrentColumnSettings();
        }
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            for (ColumnSet columnSet : getColumnSets()) {
                columnSet.addNewColumn((Column) plugin.getInstance());
            }
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.COLUMNS, e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        // Take class of column (just the first)
        Class<?> columnClass = null;
        for (Plugin instancedColumn : createdInstances) {
            columnClass = instancedColumn.getClass();
            break;
        }

        for (ColumnSet columnSet : getColumnSets()) {
            columnSet.removeColumn(columnClass);
        }
    }
    
    /**
     * Getter for logger
     * @return
     */
    private Logger getLogger() {
    	if (logger == null) {
    		logger = new Logger();
    	}
    	return logger;
    }
}
