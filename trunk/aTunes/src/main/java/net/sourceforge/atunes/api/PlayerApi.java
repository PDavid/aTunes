package net.sourceforge.atunes.api;

import net.sourceforge.atunes.kernel.modules.player.PlaybackState;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.AudioObject;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class PlayerApi {

	/**
	 * Stops current object being played
	 */
	public static void stop() {
		PlayerHandler.getInstance().stopCurrentAudioObject(true);
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
	
	/**
	 * Returns current audio object in active play list
	 * @return
	 */
	public static AudioObject getCurrentAudioObject() {
		return PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList();
	}
	
	/**
	 * Returns the current state of player: playing, paused...
	 * @return
	 */
	public static PlaybackState getCurrentPlaybackState() {
		return PlayerHandler.getInstance().getPlaybackState();
	}
	
	/**
	 * Returns current audio object time played in milliseconds
	 * @return
	 */
	public static long getCurrentAudioObjectPlayedTime() {
		return PlayerHandler.getInstance().getCurrentAudioObjectPlayedTime();
	}
	
	/**
	 * Returns length of current audio object in milliseconds 
	 * @return
	 */
	public static long getCurrentAudioObjectLength() {
		return PlayerHandler.getInstance().getCurrentAudioObjectLength();
	}
}
