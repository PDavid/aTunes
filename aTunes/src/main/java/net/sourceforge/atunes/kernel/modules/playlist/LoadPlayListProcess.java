/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.process.Process;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

class LoadPlayListProcess extends Process {

    private List<String> filenamesToLoad;

    LoadPlayListProcess(List<String> filenamesToLoad) {
        this.filenamesToLoad = filenamesToLoad;
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
        final List<AudioObject> songsLoaded = new ArrayList<AudioObject>();
        for (int i = 0; i < filenamesToLoad.size() && !cancel; i++) {
            songsLoaded.add(PlayListIO.getAudioFileOrCreate(filenamesToLoad.get(i)));
            setCurrentProgress(i + 1);
        }
        // If canceled loaded files are added anyway
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (songsLoaded.size() >= 1) {
                    PlayListHandler.getInstance().addToPlayList(songsLoaded);
                }
            }
        });
        return true;
    }

}
