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

import net.sourceforge.atunes.kernel.AbstractStateRetrieveTask;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.IStatistics;

/**
 * Reads statistics
 * 
 * @author alex
 * 
 */
public final class StatisticsInitializationTask extends
		AbstractStateRetrieveTask {

	private IStatistics statistics;

	@Override
	public void retrieveData(final IStateService stateService,
			final IBeanFactory beanFactory) {
		this.statistics = stateService.retrieveStatisticsCache();
	}

	@Override
	public void setData(final IBeanFactory beanFactory) {
		if (this.statistics != null) {
			beanFactory.getBean(StatisticsHandler.class).setStatistics(
					this.statistics);
		}
	}
}