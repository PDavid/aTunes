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
package net.sourceforge.atunes.kernel.modules.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

public class GeneralPurposePluginsHandler extends Handler implements PluginListener {

    /** Singleton instance */
    private static GeneralPurposePluginsHandler instance;

    /**
     * List of plugins created
     */
    private List<GeneralPurposePlugin> plugins;

    /**
     * Getter of singleton instance
     * 
     * @return
     */
    public static GeneralPurposePluginsHandler getInstance() {
        if (instance == null) {
            instance = new GeneralPurposePluginsHandler();
        }
        return instance;
    }

    @Override
    public void applicationStarted() {
    }

    @Override
    public void applicationFinish() {
        // Deactivate all plugins yet active
        if (plugins != null) {
            for (GeneralPurposePlugin plugin : plugins) {
                plugin.deactivate();
            }
        }
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
    }

    @Override
    protected void initHandler() {
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            GeneralPurposePlugin instance = (GeneralPurposePlugin) plugin.getInstance();
            if (plugins == null) {
                plugins = new ArrayList<GeneralPurposePlugin>();
            }
            plugins.add(instance);
            // Activate plugin
            instance.activate();
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        // Call to deactivate all plugins and remove from plugins list
        for (Plugin instance : createdInstances) {
            ((GeneralPurposePlugin) instance).deactivate();
            if (plugins != null) {
                plugins.remove(instance);
            }
        }
    }
}
