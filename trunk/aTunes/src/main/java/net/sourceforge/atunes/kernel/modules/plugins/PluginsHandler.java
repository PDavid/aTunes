/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumns;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextPanel;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationView;
import net.sourceforge.atunes.kernel.modules.player.PlaybackStateListener;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.Timer;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.ZipUtils;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;
import org.commonjukebox.plugins.PluginSystemLogger;
import org.commonjukebox.plugins.PluginsFactory;

public class PluginsHandler extends Handler implements PluginListener {

    /** Singleton instance */
    private static PluginsHandler instance;

    /**
     * Plugins factory
     */
    private PluginsFactory factory;

    private static Set<PluginType> pluginTypes;

    /**
     * Getter of singleton instance
     * 
     * @return
     */
    public static PluginsHandler getInstance() {
        if (instance == null) {
            instance = new PluginsHandler();
        }
        return instance;
    }

    /**
     * Initializes all plugins found in plugins dir
     */
    private void initPlugins() {
        try {
        	Timer t = new Timer();
        	t.start();
            factory = new PluginsFactory();

            PluginSystemLogger.addHandler(new java.util.logging.Handler() {
				
				@Override
				public void publish(LogRecord record) {
					if (record.getLevel().equals(Level.SEVERE)) {
						getLogger().error(LogCategories.PLUGINS, record.getMessage());
					} else {
						getLogger().debug(LogCategories.PLUGINS, record.getMessage());
					}
				}
				
				@Override
				public void flush() {
				}
				
				@Override
				public void close() throws SecurityException {
				}
			});
            PluginSystemLogger.setLevel(Kernel.DEBUG ? Level.FINE : Level.OFF);
            
            // User plugins folder
            factory.addPluginsFolder(getUserPluginsFolder());

            addPluginListeners();
            int plugins = factory.start(getPluginClassNames(), true, "net.sourceforge.atunes");
            getLogger().info(LogCategories.PLUGINS, StringUtils.getString("Found ", plugins, " plugins (", t.stop(), " seconds)"));            
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
            if (e.getCause() != null) {
            	getLogger().error(LogCategories.PLUGINS, e.getCause());
            }
        }
    }
    
    @Override
    public void applicationFinish() {
    	// TODO Auto-generated method stub
    	
    }
    
    @Override
    public void applicationStateChanged(ApplicationState newState) {
    	// TODO Auto-generated method stub
    	
    }
    
    @Override
    protected void initHandler() {
    	initPlugins();
    }
    
    @Override
    public void applicationStarted() {
    	// TODO Auto-generated method stub
    	
    }


    /**
     * A set of all plugin types accepted TODO: Add a new plugin type here
     * 
     * @return
     */
    private Set<PluginType> getPluginTypes() {
        if (pluginTypes == null) {
            pluginTypes = new HashSet<PluginType>();
            pluginTypes.add(new PluginType(PlaybackStateListener.class.getName(), PlayerHandler.getInstance(), false));
            pluginTypes.add(new PluginType(Column.class.getName(), PlayListColumns.getInstance(), false));
            pluginTypes.add(new PluginType(NavigationView.class.getName(), NavigationHandler.getInstance(), false));
            pluginTypes.add(new PluginType(ContextPanel.class.getName(), ContextHandler.getInstance(), false));
        }
        return pluginTypes;
    }

    /**
     * Return class names of all plugin types
     * 
     * @return
     */
    private Set<String> getPluginClassNames() {
        Set<String> result = new HashSet<String>();
        for (PluginType pluginType : getPluginTypes()) {
            result.add(pluginType.getClassType());
        }
        return result;
    }

    /**
     * Registers plugin listeners of every plugin type This class is registered
     * for all plugin types
     */
    private void addPluginListeners() {
        for (PluginType pluginType : getPluginTypes()) {
            factory.addPluginListener(pluginType.getClassType(), this);
            factory.addPluginListener(pluginType.getClassType(), pluginType.getListener());
        }
    }

    /**
     * Return list of available plugins
     * 
     * @return
     */
    public List<PluginInfo> getAvailablePlugins() {
    	return this.factory.getPlugins();
    }

    /**
     * Return path to plugins folder, which is inside user config folder.
     * 
     * @return the plugins folder
     */
    private static String getUserPluginsFolder() {
        String userConfigFolder = SystemProperties.getUserConfigFolder(Kernel.DEBUG);
        String pluginsFolder = StringUtils.getString(userConfigFolder, SystemProperties.FILE_SEPARATOR, Constants.PLUGINS_DIR);
        File pluginFile = new File(pluginsFolder);
        if (!pluginFile.exists()) {
            if (!pluginFile.mkdir()) {
                return userConfigFolder;
            }
        }
        return pluginsFolder;
    }

    /**
     * Unzips a zip file in user plugins directory and updates plugins
     * 
     * @param zipFile
     * @throws PluginSystemException
     *             , IOException
     */
    public void installPlugin(File zipFile) throws IOException, PluginSystemException {
        try {
            ZipUtils.unzipArchive(zipFile, new File(getUserPluginsFolder()));
            factory.refresh();
        } catch (IOException e) {
            getLogger().error(LogCategories.PLUGINS, e);
            throw e;
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
            if (e.getCause() != null) {
            	getLogger().error(LogCategories.PLUGINS, e.getCause());
            }
            throw e;
        }
    }

    /**
     * Removes a plugin from user plugins folder and updates plugins
     * 
     * @param plugin
     * @throws IOException
     * @throws PluginSystemException
     */
    public void uninstallPlugin(PluginInfo plugin) throws IOException, PluginSystemException {
        // Only remove plugins if are contained in a separate folder under user plugins folder
        File pluginLocation = new File(plugin.getPluginLocation());
        if (pluginLocation.getParent().equals(new File(getUserPluginsFolder()).getAbsolutePath())) {
            try {
                factory.uninstallPlugin(plugin);
                factory.refresh();
            } catch (PluginSystemException e) {
                getLogger().error(LogCategories.PLUGINS, e);
                if (e.getCause() != null) {
                	getLogger().error(LogCategories.PLUGINS, e.getCause());
                }
                throw e;
            }
        }
    }

    /**
     * Activates or deactivates given plugin
     * 
     * @param plugin
     * @param active
     */
    public void setPluginActive(PluginInfo plugin, boolean active) {
        try {
            if (active) {
                plugin.activate();
            } else {
                plugin.deactivate();
            }
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
            if (e.getCause() != null) {
            	getLogger().error(LogCategories.PLUGINS, e.getCause());
            }
        }
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        getLogger().info(LogCategories.PLUGINS, StringUtils.getString("Plugin activated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        getLogger().info(LogCategories.PLUGINS, StringUtils.getString("Plugin deactivated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
    }

    /**
     * Returns <code>true</code> if the application must be restarted after
     * changing or activating / deactivating the given plugin
     * 
     * @param plugin
     * @return
     */
    public boolean pluginNeedsRestart(PluginInfo plugin) {
        for (PluginType pluginType : getPluginTypes()) {
            if (plugin.getPluginType().equals(pluginType.getClassType())) {
                return pluginType.isApplicationNeedsRestart();
            }
        }
        return false;
    }

}
