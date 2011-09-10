/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.plugins;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.PlaybackStateListener;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSets;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.AbstractNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.misc.Timer;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;
import org.commonjukebox.plugins.PluginSystemLogger;
import org.commonjukebox.plugins.PluginsFactory;
import org.commonjukebox.plugins.exceptions.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginConfiguration;
import org.commonjukebox.plugins.model.PluginFolder;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

public class PluginsHandler extends AbstractHandler implements PluginListener {

    /** Singleton instance */
    private static PluginsHandler instance;

    /**
     * Plugins factory
     */
    private PluginsFactory factory;

    private static Set<PluginType> pluginTypes;
    
    static {
        pluginTypes = new HashSet<PluginType>();
        pluginTypes.add(new PluginType(PlaybackStateListener.class.getName(), PlayerHandler.getInstance(), false));
        pluginTypes.add(new PluginType(AbstractColumn.class.getName(), ColumnSets.getInstance(), false));
        pluginTypes.add(new PluginType(AbstractNavigationView.class.getName(), NavigationHandler.getInstance(), false));
        pluginTypes.add(new PluginType(AbstractContextPanel.class.getName(), ContextHandler.getInstance(), false));
        pluginTypes.add(new PluginType(AbstractLookAndFeel.class.getName(), LookAndFeelSelector.getInstance(), false));
        pluginTypes.add(new PluginType(AbstractGeneralPurposePlugin.class.getName(), GeneralPurposePluginsHandler.getInstance(), false));
    }

    /**
     * Contains problems while last plugin load operation
     */
    private Map<PluginFolder, PluginSystemException> problemsLoadingPlugins;
    
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
            factory = new PluginsFactory();

            PluginSystemLogger.addHandler(new PluginsLoggerHandler());
            PluginSystemLogger.setLevel(Level.ALL);

            // User plugins folder
            factory.setPluginsRepository(getUserPluginsFolder());
            
            // Temporal plugins folder
            factory.setTemporalPluginRepository(getTemporalPluginsFolder());

        } catch (PluginSystemException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(e);
		}
    }

    @Override
    public void applicationFinish() {
    }

    @Override
    public void applicationStateChanged(IState newState) {
    }

    @Override
    protected void initHandler() {
    	if (Kernel.isEnablePlugins()) {
    		initPlugins();
    	} else {
    		Logger.info("Plugins are disabled");
    	}
    	
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    	if (!Kernel.isEnablePlugins()) {
    		return;
    	}
    	
        addPluginListeners();

        Timer t = new Timer();
        t.start();

        try {
        	// PLUGINS MUST BE STARTED WHEN APPLICATION IS STARTED, OTHERWISE THEY CAN TRY TO ACCESS TO COMPONENTS NOT CREATED OR INITIALIZED YET
			problemsLoadingPlugins = factory.start(getPluginClassNames(), false, "net.sourceforge.atunes");
		} catch (PluginSystemException e) {
			Logger.error(e);
		}

        Logger.info(StringUtils.getString("Found ", factory.getPlugins().size(), " plugins (", t.stop(), " seconds)"));
        Logger.info(StringUtils.getString("Problems loading ", problemsLoadingPlugins != null ? problemsLoadingPlugins.size() : 0, " plugins"));

    	if (problemsLoadingPlugins != null) {
    		for (PluginFolder pluginFolder : problemsLoadingPlugins.keySet()) {
    			// Show a message with detailed information about the error
    			GuiHandler.getInstance().showExceptionDialog(pluginFolder.getName(), I18nUtils.getString("PLUGIN_LOAD_ERROR"), problemsLoadingPlugins.get(pluginFolder));
    			
    			// Ask user to remove plugin folder
    			if (GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("PLUGIN_LOAD_ERROR_REMOVE_CONFIRMATION")) == JOptionPane.YES_OPTION) {
    				try {
						FileUtils.deleteDirectory(pluginFolder);
					} catch (IOException e) {
						GuiHandler.getInstance().showExceptionDialog(pluginFolder.getName(), I18nUtils.getString("PLUGIN_UNINSTALLATION_ERROR"), e);
					}
    			}
    		}
    	}
    }

    /**
     * Return class names of all plugin types
     * 
     * @return
     */
    private Set<String> getPluginClassNames() {
        Set<String> result = new HashSet<String>();
        for (PluginType pluginType : pluginTypes) {
            result.add(pluginType.getClassType());
        }
        return result;
    }

    /**
     * Registers plugin listeners of every plugin type This class is registered
     * for all plugin types
     */
    private void addPluginListeners() {
        for (PluginType pluginType : pluginTypes) {
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
        String userConfigFolder = OsManager.getUserConfigFolder(Kernel.isDebug());
        String pluginsFolder = StringUtils.getString(userConfigFolder, OsManager.getFileSeparator(), Constants.PLUGINS_DIR);
        File pluginFile = new File(pluginsFolder);
        if (!pluginFile.exists() && !pluginFile.mkdir()) {
            return userConfigFolder;
        }
        return pluginsFolder;
    }
    
    /**
     * Returns path to temporal plugins folder
     * @return path to temporal plugins folder
     * @throws IOException
     */
    private static String getTemporalPluginsFolder() throws IOException {
    	String temporalPluginsFolder = StringUtils.getString(OsManager.getTempFolder(), OsManager.getFileSeparator(), Constants.PLUGINS_DIR);
    	File temporalPluginsFile = new File(temporalPluginsFolder);
    	if (!temporalPluginsFile.exists() && !temporalPluginsFile.mkdirs()) {
    		throw new IOException(StringUtils.getString("Can't create temporal plugins folder: ", temporalPluginsFolder));
    	}
    	return temporalPluginsFolder;
    }

    /**
     * Unzips a zip file in user plugins directory and updates plugins
     * 
     * @param zipFile
     * @throws PluginSystemException
     *             , IOException
     */
    public Map<PluginFolder, PluginSystemException> installPlugin(File zipFile) throws PluginSystemException {
        try {
        	return factory.installPlugin(zipFile);
        } catch (PluginSystemException e) {
            Logger.error(e);
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
    public Map<PluginFolder, PluginSystemException> uninstallPlugin(PluginInfo plugin) throws IOException, PluginSystemException {
        // Only remove plugins if are contained in a separate folder under user plugins folder
        File pluginLocation = plugin.getPluginFolder();
        if (pluginLocation.getParent().equals(new File(getUserPluginsFolder()).getAbsolutePath())) {
            try {
                return factory.uninstallPlugin(plugin);
            } catch (PluginSystemException e) {
                Logger.error(e);
                throw e;
            }
        }
        return new HashMap<PluginFolder, PluginSystemException>();
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
                PluginsHandler.getInstance().activatePlugin(plugin);
            } else {
            	PluginsHandler.getInstance().deactivatePlugin(plugin);
            }
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        Logger.info(StringUtils.getString("Plugin activated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        Logger.info(StringUtils.getString("Plugin deactivated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
    }

    /**
     * Returns <code>true</code> if the application must be restarted after
     * changing or activating / deactivating the given plugin
     * 
     * @param plugin
     * @return
     */
    public boolean pluginNeedsRestart(PluginInfo plugin) {
        for (PluginType pluginType : pluginTypes) {
            if (plugin.getPluginType().equals(pluginType.getClassType())) {
                return pluginType.isApplicationNeedsRestart();
            }
        }
        return false;
    }

    /**
     * Returns new instance of plugin
     * @param pluginInfo
     * @return
     * @throws PluginSystemException
     */
    public Plugin getNewInstance(PluginInfo pluginInfo) throws PluginSystemException {
    	return factory.getNewInstance(pluginInfo);
    }
    
    /**
     * Activates plugin
     * @param plugin
     * @throws PluginSystemException
     */
    public void activatePlugin(PluginInfo plugin) throws PluginSystemException {
    	factory.activatePlugin(plugin);
    }
    
    /**
     * Deactivates plugin
     * @param plugin
     * @throws PluginSystemException 
     */
    public void deactivatePlugin(PluginInfo plugin) throws PluginSystemException {
    	factory.deactivatePlugin(plugin);
    }

    /**
     * Validates plugin configuration
     * @param plugin
     * @param configuration
     * @throws InvalidPluginConfigurationException
     */
    public void validateConfiguration(PluginInfo plugin, PluginConfiguration configuration) throws InvalidPluginConfigurationException {
    	factory.validateConfiguration(plugin, configuration);
    }
    
    /**
     * Sets plugin configuration
     * @param plugin
     * @param configuration
     * @throws PluginSystemException
     */
    public void setConfiguration(PluginInfo plugin, PluginConfiguration configuration) throws PluginSystemException {
    	factory.setConfiguration(plugin, configuration);
    }
    
    /**
     * Returns plugin configuration
     * @param plugin
     * @return plugin configuration
     * @throws PluginSystemException
     */
    public PluginConfiguration getConfiguration(PluginInfo plugin) throws PluginSystemException {
    	return factory.getConfiguration(plugin);
    }
    
    private static class PluginsLoggerHandler extends java.util.logging.Handler {

        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().equals(Level.SEVERE)) {
                Logger.error(record.getMessage());
            } else {
                Logger.info(record.getMessage());
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

    }

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

}
