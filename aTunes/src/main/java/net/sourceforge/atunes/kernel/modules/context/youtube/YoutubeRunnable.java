/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextListener;
import net.sourceforge.atunes.model.AudioObject;

/**
 * triggers the youtube search
 * 
 * @author Tobias Melcher
 * 
 */
public class YoutubeRunnable implements Runnable {

    /**
     * audio object from which perform search
     */
    private AudioObject audioObject;

    ContextListener listener;

    /**
     * Id of worker threads
     */
    long workerId;

    public YoutubeRunnable(ContextListener listener, AudioObject audioObject, long currentWorkerId) {
        this.audioObject = audioObject;
        this.workerId = currentWorkerId;
        this.listener = listener;
    }

    @Override
    public void run() {
        String searchString = ContextHandler.getInstance().getYoutubeSearchForAudioObject(audioObject);
        if (searchString.length() > 0) {
            final List<YoutubeResultEntry> result = ContextHandler.getInstance().searchInYoutube(searchString, 1);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyYoutubeSearchRetrieved(result, workerId);
                }
            });
        }
    }

}
