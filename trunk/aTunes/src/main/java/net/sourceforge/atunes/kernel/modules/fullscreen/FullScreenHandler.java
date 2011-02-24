package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import net.sourceforge.atunes.gui.views.dialogs.fullScreen.FullScreenWindow;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public class FullScreenHandler extends AbstractHandler {

	private static FullScreenHandler instance;
	
	private FullScreenController controller;
	
	/**
	 * Returns singleton instance of this handler
	 * @return
	 */
	public static FullScreenHandler getInstance() {
		if (instance == null) {
			instance = new FullScreenHandler();
		}
		return instance;
	}
	
	@Override
	public void applicationStarted(List<AudioObject> playList) {
	}

	@Override
	public void applicationFinish() {
	}

	@Override
	public void applicationStateChanged(ApplicationState newState) {
	}

    @Override
    public void playListCleared() {
        // Next actions must be done ONLY if stopPlayerWhenPlayListClear is enabled
        if (ApplicationState.getInstance().isStopPlayerOnPlayListClear()) {

            // Remove audio object information from full screen mode
            getFullScreenController().setAudioObjects(null);
        }
    }
    
    @Override
    public void selectedAudioObjectChanged(AudioObject audioObject) {
        List<AudioObject> objects = new ArrayList<AudioObject>(5);
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getPreviousAudioObject(2));
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getPreviousAudioObject(1));
        objects.add(audioObject);
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getNextAudioObject(1));
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getNextAudioObject(2));

        getFullScreenController().setAudioObjects(objects);
    }

	@Override
	protected void initHandler() {
	}

	/**
	 * Returns full screen controller
	 * @return
	 */
	private FullScreenController getFullScreenController() {
		if (controller == null) {
			JDialog.setDefaultLookAndFeelDecorated(false);
			FullScreenWindow window = new FullScreenWindow(GuiHandler.getInstance().getFrame().getFrame());
	        JDialog.setDefaultLookAndFeelDecorated(true);
	        controller = new FullScreenController(window);
		}
		return controller;
	}
	
	/**
	 * Shows or hides full screen
	 */
	public void toggleFullScreenVisibility() {
		getFullScreenController().toggleVisibility();
	}

	/**
	 * Sets playing
	 * @param playing
	 */
	public void setPlaying(boolean playing) {
		getFullScreenController().setPlaying(playing);
	}

	/**
	 * Returns true if full screen is visible
	 * @return
	 */
	public boolean isVisible() {
		return getFullScreenController().isVisible();
	}

	/**
	 * Sets audio object length
	 * @param currentLength
	 */
	public void setAudioObjectLength(long currentLength) {
		getFullScreenController().setAudioObjectLenght(currentLength);
	}

	/**
	 * Set audio object played time
	 * @param actualPlayedTime
	 * @param currentAudioObjectLength
	 */
	public void setCurrentAudioObjectPlayedTime(long actualPlayedTime, long currentAudioObjectLength) {
		getFullScreenController().setCurrentAudioObjectPlayedTime(actualPlayedTime, currentAudioObjectLength);
	}

	/**
	 * Sets volume
	 * @param finalVolume
	 */
	public void setVolume(int finalVolume) {
		getFullScreenController().setVolume(finalVolume);
	}

}
