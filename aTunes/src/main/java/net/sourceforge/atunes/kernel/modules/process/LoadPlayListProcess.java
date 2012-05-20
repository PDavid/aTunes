/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.process;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds to play list a list of local audio objects given their files
 * @author alex
 *
 */
public class LoadPlayListProcess extends AbstractProcess {

    private List<String> filenamesToLoad;
    
    private String playListName;
    
    private IPlayListHandler playListHandler;
    
    private IPlayListIOService playListIOService;
    
    /**
     * @param playListIOService
     */
    public void setPlayListIOService(IPlayListIOService playListIOService) {
		this.playListIOService = playListIOService;
	}
    
    /**
     * @param filenamesToLoad
     */
    public void setFilenamesToLoad(List<String> filenamesToLoad) {
		this.filenamesToLoad = filenamesToLoad;
	}
    
    /**
     * @param playListName
     */
    public void setPlayListName(String playListName) {
		this.playListName = playListName;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

    @Override
    protected long getProcessSize() {
        return this.filenamesToLoad.size();
    }

    @Override
    protected String getProgressDialogTitle() {
        return I18nUtils.getString("LOADING");
    }

    @Override
    protected void runCancel() {
        // Nothing to do
    }

    @Override
    protected boolean runProcess() {
        final List<IAudioObject> songsLoaded = new ArrayList<IAudioObject>();
        for (int i = 0; i < filenamesToLoad.size() && !isCanceled(); i++) {
            songsLoaded.add(playListIOService.getAudioObjectOrCreate(filenamesToLoad.get(i)));
            setCurrentProgress(i + 1);
        }
        // If canceled loaded files are added anyway
        SwingUtilities.invokeLater(new AddToPlayListRunnable(songsLoaded, playListHandler, playListName));
        return true;
    }
}
