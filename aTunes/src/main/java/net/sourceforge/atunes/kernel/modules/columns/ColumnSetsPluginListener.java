/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.columns;

import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

/**
 * Class to handle column sets and plugins
 * 
 * @author fleax
 */
public class ColumnSetsPluginListener implements PluginListener {

    /**
     * Column sets
     */
    private List<AbstractColumnSet> columnSets;

    private IPluginsHandler pluginsHandler;

    /**
     * @param pluginsHandler
     */
    public void setPluginsHandler(final IPluginsHandler pluginsHandler) {
	this.pluginsHandler = pluginsHandler;
    }

    /**
     * @param columnSets
     */
    public void setColumnSets(final List<AbstractColumnSet> columnSets) {
	this.columnSets = columnSets;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void pluginActivated(final PluginInfo plugin) {
	try {
	    for (AbstractColumnSet columnSet : columnSets) {
		columnSet.addNewColumn((AbstractColumn) pluginsHandler
			.getNewInstance(plugin));
	    }
	} catch (PluginSystemException e) {
	    Logger.error(e);
	}
    }

    @Override
    public void pluginDeactivated(final PluginInfo plugin,
	    final Collection<Plugin> createdInstances) {
	// Take class of column (just the first)
	Class<?> columnClass = null;
	for (Plugin instancedColumn : createdInstances) {
	    columnClass = instancedColumn.getClass();
	    break;
	}

	for (IColumnSet columnSet : columnSets) {
	    columnSet.removeColumn(columnClass);
	}
    }
}
