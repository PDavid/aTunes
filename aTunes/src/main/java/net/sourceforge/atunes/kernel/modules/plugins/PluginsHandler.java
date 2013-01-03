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

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSetsPluginListener;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.kernel.modules.navigator.AbstractNavigationView;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IExceptionDialog;
import net.sourceforge.atunes.model.IGeneralPurposePluginsHandler;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlaybackStateListener;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

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

/**
 * Responsible of loading and unloading plugins
 * 
 * @author alex
 */
public class PluginsHandler extends AbstractHandler implements PluginListener,
	IPluginsHandler {

    /**
     * Plugins factory
     */
    private PluginsFactory factory;

    private Set<PluginType> pluginTypes;

    private IStateCore stateCore;

    private IDialogFactory dialogFactory;

    private ColumnSetsPluginListener columnSetsPluginListener;

    /**
     * @param columnSetsPluginListener
     */
    public void setColumnSetsPluginListener(
	    final ColumnSetsPluginListener columnSetsPluginListener) {
	this.columnSetsPluginListener = columnSetsPluginListener;
    }

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }

    /**
     * @param stateCore
     */
    public void setStateCore(final IStateCore stateCore) {
	this.stateCore = stateCore;
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
    protected void initHandler() {
	if (stateCore.isPluginsEnabled()) {
	    initPlugins();
	} else {
	    Logger.info("Plugins are disabled");
	}
    }

    @Override
    public void applicationStarted() {
	if (!stateCore.isPluginsEnabled()) {
	    return;
	}

	addPluginListeners();

	Timer t = new Timer();
	t.start();

	Map<PluginFolder, PluginSystemException> problemsLoadingPlugins = null;
	try {
	    // PLUGINS MUST BE STARTED WHEN APPLICATION IS STARTED, OTHERWISE
	    // THEY CAN TRY TO ACCESS TO COMPONENTS NOT CREATED OR INITIALIZED
	    // YET
	    problemsLoadingPlugins = factory.start(getPluginClassNames(),
		    false, "net.sourceforge.atunes");
	} catch (PluginSystemException e) {
	    Logger.error(e);
	}

	Logger.info(StringUtils.getString("Found ",
		factory.getPlugins().size(), " plugins (", t.stop(),
		" seconds)"));
	Logger.info(StringUtils.getString("Problems loading ",
		problemsLoadingPlugins != null ? problemsLoadingPlugins.size()
			: 0, " plugins"));

	if (problemsLoadingPlugins != null) {
	    for (Map.Entry<PluginFolder, PluginSystemException> pluginFolder : problemsLoadingPlugins
		    .entrySet()) {
		// Show a message with detailed information about the error
		dialogFactory.newDialog(IExceptionDialog.class)
			.showExceptionDialog(
				I18nUtils.getString("PLUGIN_LOAD_ERROR"),
				pluginFolder.getValue());

		// Ask user to remove plugin folder
		IConfirmationDialog dialog = dialogFactory
			.newDialog(IConfirmationDialog.class);
		dialog.setMessage(I18nUtils
			.getString("PLUGIN_LOAD_ERROR_REMOVE_CONFIRMATION"));
		dialog.showDialog();
		if (dialog.userAccepted()) {
		    try {
			FileUtils.deleteDirectory(pluginFolder.getKey());
		    } catch (IOException e) {
			dialogFactory
				.newDialog(IExceptionDialog.class)
				.showExceptionDialog(
					I18nUtils
						.getString("PLUGIN_UNINSTALLATION_ERROR"),
					e);
		    }
		}
	    }
	}
    }

    private Set<PluginType> getPluginTypes() {
	if (pluginTypes == null) {
	    pluginTypes = new HashSet<PluginType>();
	    pluginTypes.add(new PluginType(IPlaybackStateListener.class
		    .getName(), (PluginListener) getBean(IPlayerHandler.class),
		    false));
	    pluginTypes.add(new PluginType(AbstractColumn.class.getName(),
		    columnSetsPluginListener, false));
	    pluginTypes.add(new PluginType(AbstractNavigationView.class
		    .getName(),
		    (PluginListener) getBean(INavigationHandler.class), false));
	    pluginTypes.add(new PluginType(
		    AbstractContextPanel.class.getName(),
		    (PluginListener) getBean(IContextHandler.class), false));
	    pluginTypes
		    .add(new PluginType(
			    AbstractLookAndFeel.class.getName(),
			    (PluginListener) getBean(ILookAndFeelManager.class),
			    false));
	    pluginTypes.add(new PluginType(AbstractGeneralPurposePlugin.class
		    .getName(), getBean(IGeneralPurposePluginsHandler.class),
		    false));
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
	    factory.addPluginListener(pluginType.getClassType(),
		    pluginType.getListener());
	}
    }

    @Override
    public List<PluginInfo> getAvailablePlugins() {
	if (stateCore.isPluginsEnabled()) {
	    return this.factory.getPlugins();
	}
	return null;
    }

    /**
     * Return path to plugins folder, which is inside user config folder.
     * 
     * @return the plugins folder
     */
    private String getUserPluginsFolder() {
	String userConfigFolder = getOsManager().getUserConfigFolder();
	String pluginsFolder = StringUtils.getString(userConfigFolder,
		getOsManager().getFileSeparator(), Constants.PLUGINS_DIR);
	File pluginFile = new File(pluginsFolder);
	if (!pluginFile.exists() && !pluginFile.mkdir()) {
	    return userConfigFolder;
	}
	return pluginsFolder;
    }

    /**
     * Returns path to temporal plugins folder
     * 
     * @return path to temporal plugins folder
     * @throws IOException
     */
    private String getTemporalPluginsFolder() throws IOException {
	String temporalPluginsFolder = StringUtils.getString(getOsManager()
		.getTempFolder(), getOsManager().getFileSeparator(),
		Constants.PLUGINS_DIR);
	File temporalPluginsFile = new File(temporalPluginsFolder);
	if (!temporalPluginsFile.exists() && !temporalPluginsFile.mkdirs()) {
	    throw new IOException(StringUtils.getString(
		    "Can't create temporal plugins folder: ",
		    temporalPluginsFolder));
	}
	return temporalPluginsFolder;
    }

    @Override
    public Map<PluginFolder, PluginSystemException> installPlugin(
	    final File zipFile) throws PluginSystemException {
	try {
	    return factory.installPlugin(zipFile);
	} catch (PluginSystemException e) {
	    Logger.error(e);
	    throw e;
	}
    }

    @Override
    public Map<PluginFolder, PluginSystemException> uninstallPlugin(
	    final PluginInfo plugin) throws IOException, PluginSystemException {
	// Only remove plugins if are contained in a separate folder under user
	// plugins folder
	File pluginLocation = plugin.getPluginFolder();
	if (pluginLocation.getParent().equals(
		net.sourceforge.atunes.utils.FileUtils.getPath(new File(
			getUserPluginsFolder())))) {
	    try {
		return factory.uninstallPlugin(plugin);
	    } catch (PluginSystemException e) {
		Logger.error(e);
		throw e;
	    }
	}
	return new HashMap<PluginFolder, PluginSystemException>();
    }

    @Override
    public void setPluginActive(final PluginInfo plugin, final boolean active) {
	try {
	    if (active) {
		activatePlugin(plugin);
	    } else {
		deactivatePlugin(plugin);
	    }
	} catch (PluginSystemException e) {
	    Logger.error(e);
	}
    }

    @Override
    public void pluginActivated(final PluginInfo plugin) {
	Logger.info(StringUtils.getString("Plugin activated: ",
		plugin.getName(), " (", plugin.getClassName(), ")"));
    }

    @Override
    public void pluginDeactivated(final PluginInfo plugin,
	    final Collection<Plugin> createdInstances) {
	Logger.info(StringUtils.getString("Plugin deactivated: ",
		plugin.getName(), " (", plugin.getClassName(), ")"));
    }

    @Override
    public boolean pluginNeedsRestart(final PluginInfo plugin) {
	for (PluginType pluginType : getPluginTypes()) {
	    if (plugin.getPluginType().equals(pluginType.getClassType())) {
		return pluginType.isApplicationNeedsRestart();
	    }
	}
	return false;
    }

    @Override
    public Plugin getNewInstance(final PluginInfo pluginInfo)
	    throws PluginSystemException {
	return factory.getNewInstance(pluginInfo);
    }

    @Override
    public void activatePlugin(final PluginInfo plugin)
	    throws PluginSystemException {
	factory.activatePlugin(plugin);
    }

    @Override
    public void deactivatePlugin(final PluginInfo plugin)
	    throws PluginSystemException {
	factory.deactivatePlugin(plugin);
    }

    @Override
    public void validateConfiguration(final PluginInfo plugin,
	    final PluginConfiguration configuration)
	    throws InvalidPluginConfigurationException {
	factory.validateConfiguration(plugin, configuration);
    }

    @Override
    public void setConfiguration(final PluginInfo plugin,
	    final PluginConfiguration configuration)
	    throws PluginSystemException {
	factory.setConfiguration(plugin, configuration);
    }

    @Override
    public PluginConfiguration getConfiguration(final PluginInfo plugin)
	    throws PluginSystemException {
	return factory.getConfiguration(plugin);
    }
}
