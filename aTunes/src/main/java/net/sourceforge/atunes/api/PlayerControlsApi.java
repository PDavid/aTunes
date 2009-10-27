package net.sourceforge.atunes.api;

import javax.swing.JToggleButton;

import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class PlayerControlsApi {

	/**
	 * Adds a secondary button in player controls
	 * @param button
	 */
	public static void addSecondaryControl(JToggleButton button) {
		VisualHandler.getInstance().getPlayerControls().addSecondaryControl(button);
	}
}
