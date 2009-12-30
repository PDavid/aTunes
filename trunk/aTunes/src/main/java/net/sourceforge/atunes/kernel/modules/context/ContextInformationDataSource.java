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
package net.sourceforge.atunes.kernel.modules.context;

import java.util.Map;

import org.commonjukebox.plugins.PluginApi;

/**
 * This interface must be implemented by classes responsible of retrieve data
 * used by a context panel
 * 
 * @author alex
 * 
 */
@PluginApi
public interface ContextInformationDataSource {

    /**
     * This method returns a map of objects containing information retrieved
     * from data source given some parameters
     * 
     * @param parameters
     * @return Map of objects containing information
     */
    public Map<String, ?> getData(Map<String, ?> parameters);
}
