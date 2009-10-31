package net.sourceforge.atunes.api;

import javax.swing.JMenu;

import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class MenuBarApi {

	/**
	 * Adds a new menu just before "Help" 
	 * @param newMenu
	 */
	public static final void addMenu(JMenu newMenu) {
		VisualHandler.getInstance().getMenuBar().addMenu(newMenu);
	}
}
