package net.sourceforge.atunes.kernel.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricsService;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * Action to clear caches from last.fm and lyrics
 * @author alex
 *
 */
public class ClearCachesAction extends Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5131926704037915711L;

	public ClearCachesAction() {
		super(LanguageTool.getString("CLEAR_CACHE"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingWorker<Boolean, Void> clearCaches = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				boolean exception;
				exception = LastFmService.getInstance().clearCache();
				exception = LyricsService.getInstance().clearCache() || exception;
				return exception;
			}

			@Override
			protected void done() {
				VisualHandler.getInstance().getEditPreferencesDialog().setCursor(Cursor.getDefaultCursor());
				setEnabled(true);
			}
		};
		setEnabled(false);
		VisualHandler.getInstance().getEditPreferencesDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		clearCaches.execute();
	}

}
