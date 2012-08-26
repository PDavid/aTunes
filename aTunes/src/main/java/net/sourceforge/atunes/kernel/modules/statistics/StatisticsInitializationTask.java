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

package net.sourceforge.atunes.kernel.modules.statistics;

import net.sourceforge.atunes.model.IHandlerBackgroundInitializationTask;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatistics;

/**
 * Reads statistics
 * @author alex
 *
 */
public final class StatisticsInitializationTask implements IHandlerBackgroundInitializationTask {
	
	private StatisticsHandler statisticsHandler;
	
	private IStateHandler stateHandler;
	
	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(StatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}
	
	/**
	 * @param stateHandler
	 */
	public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
	
	@Override
	public Runnable getInitializationTask() {
		return new Runnable() {
			@Override
			public void run() {
			    IStatistics statistics = stateHandler.retrieveStatisticsCache();
			    if (statistics != null) {
			    	statisticsHandler.setStatistics(statistics);
			    }
			}
		};
	}
	
	@Override
	public Runnable getInitializationCompletedTask() {
		return null;
	}
}