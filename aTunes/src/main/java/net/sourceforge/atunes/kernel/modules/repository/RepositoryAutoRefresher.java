/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

class RepositoryAutoRefresher extends Thread {

    private Logger logger;

    private RepositoryHandler handler;

    /**
     * Instantiates a new repository auto refresher.
     * 
     * @param repositoryHandler
     *            the repository handler
     */
    public RepositoryAutoRefresher(RepositoryHandler repositoryHandler) {
        super();
        this.handler = repositoryHandler;
        setPriority(Thread.MIN_PRIORITY);
        if (ApplicationState.getInstance().getAutoRepositoryRefreshTime() != 0) {
            start();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(ApplicationState.getInstance().getAutoRepositoryRefreshTime() * 60000L);
                if (!handler.repositoryIsNull() && !handler.isLoaderWorking()) {
                    getLogger()
                            .info(LogCategories.PROCESS, StringUtils.getString("Checking for repository changes... (", new SimpleDateFormat("HH:mm:ss").format(new Date()), ')'));
                    int filesLoaded = handler.getAudioFilesList().size();
                    int newFilesCount = RepositoryLoader.countFilesInRepository(handler.getRepository());
                    if (filesLoaded != newFilesCount) {
                        RepositoryHandler.getInstance().refreshRepository();
                    }
                }
                // If it has been disabled exit
                if (ApplicationState.getInstance().getAutoRepositoryRefreshTime() == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
