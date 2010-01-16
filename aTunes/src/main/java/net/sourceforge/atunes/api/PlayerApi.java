package net.sourceforge.atunes.api;

import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class PlayerApi {

	/**
	 * Stops current object being played
	 */
	public static void stop() {
//		if (!EventQueue.isDispatchThread()) {
//			SwingUtilities.invokeLater(new Runnable() {
//				@Override
//				public void run() {
					PlayerHandler.getInstance().stopCurrentAudioObject(true);
//				}
//			});
//		}
		
	}
	
	/**
	 * Starts playing previous object
	 */
	public static void previous() {
		PlayerHandler.getInstance().playPreviousAudioObject();
	}
	
	/**
	 * Starts playing next object
	 */
	public static void next() {
		PlayerHandler.getInstance().playNextAudioObject();
	}
	
	/**
	 * Plays or pauses current object
	 */
	public static void play() {
		PlayerHandler.getInstance().playCurrentAudioObject(true);
	}
}
