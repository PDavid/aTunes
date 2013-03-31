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

import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.ITaskService;

/**
 * Executed to save repository cache
 * 
 * @author alex
 * 
 */
public class PersistRepositoryTask implements Runnable {

	private IRepository repository;

	private IStateService stateService;

	private ITaskService taskService;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param stateService
	 */
	public void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * Persists repository
	 * 
	 * @param repository
	 */
	public void persist(final IRepository repository) {
		this.repository = repository;
		this.taskService.submitNow("Persist Repository Cache", this);
	}

	@Override
	public void run() {
		this.stateService.persistRepositoryCache(this.repository);
	}
}
