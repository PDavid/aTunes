package net.sourceforge.atunes.api;

import javax.swing.JToggleButton;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public final class PlayerControlsApi {

    /**
     * Adds a secondary button in player controls
     * 
     * @param button
     */
    public static void addSecondaryControl(JToggleButton button) {
        GuiHandler.getInstance().getPlayerControls().addSecondaryControl(button);
    }
}
