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

import java.util.concurrent.ScheduledFuture;

import net.sourceforge.atunes.kernel.TaskService;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateTime;

/**
 * Calls refresh automatically
 * @author alex
 *
 */
public class RepositoryAutoRefresher implements Runnable {

	private TaskService taskService;
	
	private IState state;
	
	private RepositoryHandler repositoryHandler;
	
	private ScheduledFuture<?> task;
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * @param taskService
	 */
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(RepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
    public void start() {
    	stop();
    	if (state.getAutoRepositoryRefreshTime() > 0) {
    		Logger.info("Repository will refresh automatically every ", state.getAutoRepositoryRefreshTime(), " minutes");
    		task = taskService.submitPeriodically("RepositoryAutoRefresher", 30, state.getAutoRepositoryRefreshTime() * 60L, this);
    	} else {
    		Logger.info("Repository will not refresh automatically");
    	}
    }
    
    public void stop() {
    	if (task != null) {
    		Logger.info("Cancelling previous pending task for automatically refresh repository");
    		task.cancel(true);
    	}
    }

    @Override
    public void run() {
        if (!repositoryHandler.isLoaderWorking()) {
            Logger.info(StringUtils.getString("Automatically refreshing repository... (", new DateTime().toString(), ')'));
            repositoryHandler.refreshRepository();
        }
    }
}
