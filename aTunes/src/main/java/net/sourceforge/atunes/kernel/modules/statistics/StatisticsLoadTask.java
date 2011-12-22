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

import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatistics;

final class StatisticsLoadTask implements Runnable {
	
	private final StatisticsHandler statisticsHandler;
	
	private final IStateHandler stateHandler;
	
	/**
	 * @param statisticsHandler
	 * @param stateHandler
	 */
	StatisticsLoadTask(StatisticsHandler statisticsHandler, IStateHandler stateHandler) {
		this.statisticsHandler = statisticsHandler;
		this.stateHandler = stateHandler;
	}

	@Override
	public void run() {
	    IStatistics statistics = stateHandler.retrieveStatisticsCache();
	    if (statistics == null) {
	    	statistics = new Statistics();
	    }
	    this.statisticsHandler.setStatistics(statistics);
	}
}