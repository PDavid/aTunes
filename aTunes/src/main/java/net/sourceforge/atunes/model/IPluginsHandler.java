/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.commonjukebox.plugins.exceptions.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginConfiguration;
import org.commonjukebox.plugins.model.PluginFolder;
import org.commonjukebox.plugins.model.PluginInfo;

/**
 * Manages plugins
 * 
 * @author alex
 * 
 */
public interface IPluginsHandler extends IHandler {

    /**
     * Return list of available plugins
     * 
     * @return
     */
    public List<PluginInfo> getAvailablePlugins();

    /**
     * Unzips a zip file in user plugins directory and updates plugins
     * 
     * @param zipFile
     * @return
     * @throws PluginSystemException
     */
    public Map<PluginFolder, PluginSystemException> installPlugin(File zipFile)
	    throws PluginSystemException;

    /**
     * Removes a plugin from user plugins folder and updates plugins
     * 
     * @param plugin
     * @return
     * @throws IOException
     * @throws PluginSystemException
     */
    public Map<PluginFolder, PluginSystemException> uninstallPlugin(
	    PluginInfo plugin) throws IOException, PluginSystemException;

    /**
     * Activates or deactivates given plugin
     * 
     * @param plugin
     * @param active
     */
    public void setPluginActive(PluginInfo plugin, boolean active);

    /**
     * Returns <code>true</code> if the application must be restarted after
     * changing or activating / deactivating the given plugin
     * 
     * @param plugin
     * @return
     */
    public boolean pluginNeedsRestart(PluginInfo plugin);

    /**
     * Returns new instance of plugin
     * 
     * @param pluginInfo
     * @return
     * @throws PluginSystemException
     */
    public Plugin getNewInstance(PluginInfo pluginInfo)
	    throws PluginSystemException;

    /**
     * Activates plugin
     * 
     * @param plugin
     * @throws PluginSystemException
     */
    public void activatePlugin(PluginInfo plugin) throws PluginSystemException;

    /**
     * Deactivates plugin
     * 
     * @param plugin
     * @throws PluginSystemException
     */
    public void deactivatePlugin(PluginInfo plugin)
	    throws PluginSystemException;

    /**
     * Validates plugin configuration
     * 
     * @param plugin
     * @param configuration
     * @throws InvalidPluginConfigurationException
     */
    public void validateConfiguration(PluginInfo plugin,
	    PluginConfiguration configuration)
	    throws InvalidPluginConfigurationException;

    /**
     * Sets plugin configuration
     * 
     * @param plugin
     * @param configuration
     * @throws PluginSystemException
     */
    public void setConfiguration(PluginInfo plugin,
	    PluginConfiguration configuration) throws PluginSystemException;

    /**
     * Returns plugin configuration
     * 
     * @param plugin
     * @return plugin configuration
     * @throws PluginSystemException
     */
    public PluginConfiguration getConfiguration(PluginInfo plugin)
	    throws PluginSystemException;

}