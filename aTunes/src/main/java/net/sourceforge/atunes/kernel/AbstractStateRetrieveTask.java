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

package net.sourceforge.atunes.kernel;

import java.util.concurrent.CountDownLatch;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.utils.Logger;

/**
 * Abstract task to retrieve state
 * 
 * @author alex
 * 
 */
public abstract class AbstractStateRetrieveTask implements Runnable {

	private IStateService stateService;

	private IBeanFactory beanFactory;

	private final CountDownLatch latch = new CountDownLatch(1);

	/**
	 * @param stateService
	 */
	public final void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * @param beanFactory
	 */
	public final void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public final void run() {
		retrieveData(this.stateService, this.beanFactory);
		this.latch.countDown();
	}

	/**
	 * Runnable called to set data retrieved
	 * 
	 * @return
	 */
	public final Runnable setData() {
		return new Runnable() {
			@Override
			public void run() {
				taskFinished();
				setData(AbstractStateRetrieveTask.this.beanFactory);
			}
		};
	}

	/**
	 * Retrieves data
	 * 
	 * @param stateService
	 * @param beanFactory
	 */
	public abstract void retrieveData(IStateService stateService,
			IBeanFactory beanFactory);

	/**
	 * Loads retrieved data
	 * 
	 * @param beanFactory
	 * 
	 * @return runnable task
	 */
	public abstract void setData(IBeanFactory beanFactory);

	/**
	 * Waits until task finishes
	 */
	private final void taskFinished() {
		try {
			this.latch.await();
		} catch (InterruptedException e) {
			Logger.error(e);
		}
	}
}
