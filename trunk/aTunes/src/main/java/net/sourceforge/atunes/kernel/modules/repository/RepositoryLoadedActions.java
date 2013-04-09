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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRepository;

/**
 * Actions when repository is loaded
 * 
 * @author alex
 * 
 */
public class RepositoryLoadedActions {

	private RepositoryHandler repositoryHandler;

	private INavigationHandler navigationHandler;

	private ShowRepositoryDataHelper showRepositoryDataHelper;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param showRepositoryDataHelper
	 */
	public void setShowRepositoryDataHelper(
			ShowRepositoryDataHelper showRepositoryDataHelper) {
		this.showRepositoryDataHelper = showRepositoryDataHelper;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(RepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * Notify finish repository read.
	 */
	public void repositoryReadCompleted(final IRepository repository) {
		this.repositoryHandler.setRepository(repository);
		this.repositoryHandler.notifyRepositoryRead();
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				beanFactory.getBean(IFrame.class).hideProgressBar();
				beanFactory.getBean(RepositoryActionsHelper.class)
						.enableActionsDependingOnRepository(repository);
				showRepositoryDataHelper.showRepositoryAudioFileNumber(
						repository.getFiles().size(),
						repository.getTotalSizeInBytes(),
						repository.getTotalDurationInSeconds());
				navigationHandler.repositoryReloaded();
			}
		});
	}
}
