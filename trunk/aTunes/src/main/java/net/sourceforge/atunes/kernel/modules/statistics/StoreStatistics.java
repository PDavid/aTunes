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

package net.sourceforge.atunes.kernel.modules.statistics;

import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.ITaskService;

/**
 * Responsible of storing statistics
 * 
 * @author alex
 * 
 */
class StoreStatistics implements Runnable {

	private final ITaskService taskService;

	private final IStateService stateService;

	private final StatisticsHandler statisticsHandler;

	/**
	 * @param taskService
	 * @param stateService
	 * @param staticticsHandler
	 */
	public StoreStatistics(final ITaskService taskService,
			final IStateService stateService,
			final StatisticsHandler staticticsHandler) {
		this.taskService = taskService;
		this.stateService = stateService;
		this.statisticsHandler = staticticsHandler;
	}

	/**
	 * Stores statistics
	 * 
	 */
	void storeStatistics() {
		this.taskService.submitNow("Persist statistics", this);
	}

	@Override
	public void run() {
		this.stateService.persistStatisticsCache(this.statisticsHandler
				.getStatistics());
	}
}
