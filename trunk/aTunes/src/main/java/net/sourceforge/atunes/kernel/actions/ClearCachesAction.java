/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package net.sourceforge.atunes.kernel.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricsService;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Action to clear caches from last.fm and lyrics
 * 
 * @author alex
 * 
 */
public class ClearCachesAction extends Action {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5131926704037915711L;

    public ClearCachesAction() {
        super(I18nUtils.getString("CLEAR_CACHE"));
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
                GuiHandler.getInstance().getEditPreferencesDialog().setCursor(Cursor.getDefaultCursor());
                setEnabled(true);
            }
        };
        setEnabled(false);
        GuiHandler.getInstance().getEditPreferencesDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        clearCaches.execute();
    }

}
