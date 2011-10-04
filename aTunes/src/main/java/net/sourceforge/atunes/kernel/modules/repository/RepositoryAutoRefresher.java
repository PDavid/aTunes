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

package net.sourceforge.atunes.kernel.modules.repository;

import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateTime;

class RepositoryAutoRefresher extends Thread {

    private RepositoryHandler handler;
    
    private IState state;

    /**
     * Instantiates a new repository auto refresher.
     * 
     * @param repositoryHandler
     *            the repository handler
     */
    public RepositoryAutoRefresher(RepositoryHandler repositoryHandler, IState state) {
        super();
        this.handler = repositoryHandler;
        this.state = state;
        setPriority(Thread.MIN_PRIORITY);
        if (state.getAutoRepositoryRefreshTime() != 0) {
            start();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(state.getAutoRepositoryRefreshTime() * 60000L);
                if (!handler.repositoryIsNull() && !handler.isLoaderWorking()) {
                    Logger.info(StringUtils.getString("Checking for repository changes... (", new DateTime().toString(), ')'));
                    int filesLoaded = handler.getAudioFilesList().size();
                    int newFilesCount = RepositoryLoader.countFilesInRepository(handler.getRepository());
                    if (filesLoaded != newFilesCount) {
                    	handler.refreshRepository();
                    }
                }
                // If it has been disabled exit
                if (state.getAutoRepositoryRefreshTime() == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            return;
        }
    }
}
