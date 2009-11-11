package net.sourceforge.atunes.api;

import javax.swing.JMenu;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public final class MenuBarApi {

    /**
     * Adds a new menu just before "Help"
     * 
     * @param newMenu
     */
    public static void addMenu(JMenu newMenu) {
        GuiHandler.getInstance().getMenuBar().addMenu(newMenu);
    }
}
