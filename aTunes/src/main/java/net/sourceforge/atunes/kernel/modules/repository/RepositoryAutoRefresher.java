/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateTime;

/**
 * Calls refresh automatically
 * @author alex
 *
 */
public class RepositoryAutoRefresher implements Runnable {

	private ITaskService taskService;
	
	private RepositoryHandler repositoryHandler;
	
	private ScheduledFuture<?> task;

	private IStateRepository stateRepository;
	
	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}
	
	/**
	 * @param taskService
	 */
	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(RepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * Starts auto refresh
	 */
    public void start() {
    	stop();
    	if (stateRepository.getAutoRepositoryRefreshTime() > 0) {
    		Logger.info("Repository will refresh automatically every ", stateRepository.getAutoRepositoryRefreshTime(), " minutes");
    		task = taskService.submitPeriodically("RepositoryAutoRefresher", 30, stateRepository.getAutoRepositoryRefreshTime() * 60L, this);
    	} else {
    		Logger.info("Repository will not refresh automatically");
    	}
    }
    
    /**
     * Stops auto refresh
     */
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
