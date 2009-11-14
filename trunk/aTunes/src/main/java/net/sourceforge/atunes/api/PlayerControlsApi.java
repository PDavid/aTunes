package net.sourceforge.atunes.api;

import javax.swing.Action;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public final class PlayerControlsApi {

    /**
     * Adds a secondary toggle button in player controls
     * 
     * @param button
     */
    public static void addSecondaryControl(Action action) {
        GuiHandler.getInstance().getPlayerControls().addSecondaryControl(action);
    }
}
