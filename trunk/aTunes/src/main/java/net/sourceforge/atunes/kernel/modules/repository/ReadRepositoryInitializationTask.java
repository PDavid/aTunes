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

import net.sourceforge.atunes.kernel.AbstractStateRetrieveTask;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IStateService;

/**
 * Loads repository
 * 
 * @author alex
 * 
 */
public final class ReadRepositoryInitializationTask extends
		AbstractStateRetrieveTask {

	private IRepository repository;

	@Override
	public void retrieveData(final IStateService stateService,
			final IBeanFactory beanFactory) {
		// This is the first access to repository, so execute the
		// command defined by user
		new LoadRepositoryCommandExecutor().execute(beanFactory.getBean(
				IStateRepository.class).getCommandBeforeAccessRepository());
		this.repository = stateService.retrieveRepositoryCache();
	}

	@Override
	public void setData(final IBeanFactory beanFactory) {
		beanFactory.getBean(RepositoryFromCacheProcessor.class).setRepository(
				this.repository);
		beanFactory.getBean(RepositoryAutoRefresher.class).start();
	}
}