package net.sourceforge.atunes.kernel.modules.audioobjetproperties;

import java.util.List;

import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListEventListener;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public class AudioObjectPropertiesHandler extends AbstractHandler implements PlayListEventListener {

	private static AudioObjectPropertiesHandler instance;
	
	private AudioObjectPropertiesController controller;
	
	private AudioObjectPropertiesHandler() {
	}
	
	/**
	 * Singleton instance
	 * @return
	 */
	public static AudioObjectPropertiesHandler getInstance() {
		if (instance == null) {
			instance = new AudioObjectPropertiesHandler();
		}
		return instance;
	}
	
    /**
     * Gets the  controller.
     * 
     * @return the controller
     */
    private AudioObjectPropertiesController getController() {
        if (controller == null) {
            AudioObjectPropertiesPanel panel = GuiHandler.getInstance().getPropertiesPanel();
            controller = new AudioObjectPropertiesController(panel);
        }
        return controller;
    }
	
    /**
     * Show song properties.
     * 
     * @param show
     *            the show
     */
    public void showSongProperties(boolean show) {
        ApplicationState.getInstance().setShowAudioObjectProperties(show);
        GuiHandler.getInstance().getFrame().showSongProperties(show);
        if (show) {
            if (PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList() != null) {
                getController().updateValues(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
            } else {
            	getController().onlyShowPropertiesPanel();
            }
        }
    }

	@Override
	public void applicationStarted(List<AudioObject> playList) {
        PlayListHandler.getInstance().addPlayListEventListener(this);
        showSongProperties(ApplicationState.getInstance().isShowAudioObjectProperties());
	}

	@Override
	public void applicationFinish() {
	}

	@Override
	public void applicationStateChanged(ApplicationState newState) {
	}

	@Override
	protected void initHandler() {
	}

	public void refreshPicture() {
		getController().refreshPicture();
	}

	public void refreshFavoriteIcons() {
		getController().refreshFavoriteIcons();
	}
	
    @Override
    public void clear() {
        // Next actions must be done ONLY if stopPlayerWhenPlayListClear is enabled
        if (ApplicationState.getInstance().isStopPlayerOnPlayListClear() && ApplicationState.getInstance().isShowAudioObjectProperties()) {
        	getController().clearPanel();
        }
    }
    
    @Override
    public void selectedAudioObjectChanged(AudioObject audioObject) {
        // Update file properties
        if (ApplicationState.getInstance().isShowAudioObjectProperties()) {
            getController().updateValues(audioObject);
        }
    }
}
