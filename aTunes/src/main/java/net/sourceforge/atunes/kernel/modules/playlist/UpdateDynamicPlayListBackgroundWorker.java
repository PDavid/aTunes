package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorkerWithIndeterminateProgress;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Updates dynamic playlist in background
 * 
 * @author alex
 * 
 */
public class UpdateDynamicPlayListBackgroundWorker extends
		BackgroundWorkerWithIndeterminateProgress<Void, Void> {

	private DynamicPlayList playList;

	private PlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final PlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param playList
	 * @return
	 */
	public UpdateDynamicPlayListBackgroundWorker setPlayList(
			final DynamicPlayList playList) {
		this.playList = playList;
		return this;
	}

	@Override
	protected String getDialogTitle() {
		return I18nUtils.getString("PLEASE_WAIT");
	}

	@Override
	protected void doneAndDialogClosed(final Void result) {
		this.playListHandler.refreshPlayList();
		this.playListHandler.playListsChanged();
	}

	@Override
	protected Void doInBackground() {
		this.playListHandler.recalculateDynamicPlayList(this.playList);
		return null;
	}

	@Override
	protected void whileWorking(final List<Void> chunks) {
	}
}
