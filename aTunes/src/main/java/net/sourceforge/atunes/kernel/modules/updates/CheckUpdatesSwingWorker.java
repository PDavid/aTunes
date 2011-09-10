package net.sourceforge.atunes.kernel.modules.updates;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.utils.I18nUtils;

final class CheckUpdatesSwingWorker extends
		SwingWorker<ApplicationVersion, Void> {
	/**
	 * 
	 */
	private final IUpdateHandler updateHandler;
	private final boolean showNoNewVersion;
	private final boolean alwaysInDialog;

	CheckUpdatesSwingWorker(IUpdateHandler updateHandler, boolean showNoNewVersion, boolean alwaysInDialog) {
		this.updateHandler = updateHandler;
		this.showNoNewVersion = showNoNewVersion;
		this.alwaysInDialog = alwaysInDialog;
	}

	@Override
	protected ApplicationVersion doInBackground() throws Exception {
	    return this.updateHandler.getLastVersion();
	}

	@Override
	protected void done() {
	    try {
	        ApplicationVersion version = get();
	        if (version != null && version.compareTo(Constants.VERSION) == 1) {
	            GuiHandler.getInstance().showNewVersionInfo(version, alwaysInDialog);
	        } else if (showNoNewVersion) {
	            GuiHandler.getInstance().showMessage(I18nUtils.getString("NOT_NEW_VERSION"));
	        }
	    } catch (InterruptedException e) {
	        Logger.error(e);
	    } catch (ExecutionException e) {
	        Logger.error(e);
	    }
	}
}