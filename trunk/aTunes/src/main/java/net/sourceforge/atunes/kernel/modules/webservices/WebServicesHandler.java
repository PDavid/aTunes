package net.sourceforge.atunes.kernel.modules.webservices;

import java.util.List;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricsService;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.model.AudioObject;

public class WebServicesHandler extends AbstractHandler {

	private static WebServicesHandler instance;
	
	private WebServicesHandler() {		
	}
	
	@Override
	public void applicationStarted(List<AudioObject> playList) {
        LastFmService.getInstance().submitCacheToLastFm();
	}

	@Override
	public void applicationFinish() {
        LastFmService.getInstance().finishService();
        LyricsService.getInstance().finishService();
	}

	@Override
	public void applicationStateChanged(ApplicationState newState) {
        LastFmService.getInstance().updateService();
        LyricsService.getInstance().updateService();
        YoutubeService.getInstance().updateService();
	}

	@Override
	protected void initHandler() {
        LastFmService.getInstance().updateService();
        LyricsService.getInstance().updateService();
	}

	/**
	 * Returns instance
	 * @return
	 */
	public static AbstractHandler getInstance() {
		if (instance == null) {
			instance = new WebServicesHandler();
		}
		return instance;
	}

}
