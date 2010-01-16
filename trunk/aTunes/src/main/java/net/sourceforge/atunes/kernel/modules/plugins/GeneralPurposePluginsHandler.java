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
    	// Activate all plugins
    	if (plugins != null) {
    		for (GeneralPurposePlugin plugin : plugins) {
    			plugin.activate();
    		}
    	}
    }
    
    @Override
    public void applicationFinish() {
    	// Deactivate all plugins
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
		} catch (PluginSystemException e) {
			getLogger().error(LogCategories.PLUGINS, e);
		}
	}
	
	@Override
	public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
		// Call to deactivate all plugins and remove from plugins list
		for (Plugin instance : createdInstances) {
			((GeneralPurposePlugin)instance).deactivate();
			if (plugins != null) {
				plugins.remove(instance);
			}
		}
	}
}
