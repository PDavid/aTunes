package net.sourceforge.atunes.kernel.modules.plugins;

import org.commonjukebox.plugins.PluginListener;

public class PluginType {
	
	/**
	 * The class type of a set of plugins
	 */
	private String classType;
	
	/**
	 * Listener of plugins
	 */
	private PluginListener listener;
	
    /**
     * Indicates if application must be restarted after a change in configuration or an activation or deactivation of a plugin of this type
     */
	private boolean  applicationNeedsRestart;

	/**
	 * @param classType
	 * @param listener
	 * @param applicationNeedsRestart
	 */
	protected PluginType(String classType, PluginListener listener, boolean applicationNeedsRestart) {
		this.classType = classType;
		this.listener = listener;
		this.applicationNeedsRestart = applicationNeedsRestart;
	}

	/**
	 * @return the classType
	 */
	protected String getClassType() {
		return classType;
	}

	/**
	 * @return the listener
	 */
	protected PluginListener getListener() {
		return listener;
	}

	/**
	 * @return the applicationNeedsRestart
	 */
	protected boolean isApplicationNeedsRestart() {
		return applicationNeedsRestart;
	}
	
	

}
