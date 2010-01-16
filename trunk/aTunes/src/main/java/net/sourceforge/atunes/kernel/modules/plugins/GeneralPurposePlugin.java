package net.sourceforge.atunes.kernel.modules.plugins;

import org.commonjukebox.plugins.PluginApi;

/**
 * Abstract class for general purpose plugin (those that don't need to implement a special interface or extend a class) 
 * @author fleax
 *
 */
@PluginApi
public abstract class GeneralPurposePlugin {

	
	/**
	 * Called when plugin becomes active
	 */
	public abstract void activate();
	
	/**
	 * Called when plugin becomes inactive
	 */
	public abstract void deactivate();
}
