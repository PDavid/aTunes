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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.ILocaleBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

/**
 * The Kernel is the class responsible of create and interconnect all modules of
 * aTunes.
 */
public final class Kernel implements IKernel {

	/** Timer used to measure start time */
	private Timer timer;

	private IStateUI stateUI;

	private ILocaleBeanFactory localeBeanFactory;

	private IStateCore stateCore;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param localeBeanFactory
	 */
	public void setLocaleBeanFactory(final ILocaleBeanFactory localeBeanFactory) {
		this.localeBeanFactory = localeBeanFactory;
	}

	@Override
	public void start(final List<String> arguments) {
		Logger.debug("Starting Kernel");

		this.timer = new Timer();
		this.timer.start();

		new LanguageSelector().setLanguage(this.stateCore,
				this.localeBeanFactory);

		ITaskService taskService = this.beanFactory.getBean(ITaskService.class);
		// Retrieve state in background
		@SuppressWarnings("unchecked")
		List<AbstractStateRetrieveTask> tasks = this.beanFactory.getBean(
				"stateToRetrieve", ArrayList.class);
		executePreviousTasks(taskService, tasks);

		initializeUI();

		this.beanFactory.getBean(HandlerInitializer.class).initializeHandlers();

		createUI();

		// Set retrieved state
		executeAfterTasks(taskService, tasks);
	}

	private void executePreviousTasks(final ITaskService taskService,
			final Collection<AbstractStateRetrieveTask> tasks) {
		for (final AbstractStateRetrieveTask task : tasks) {
			taskService.submitNow(task.getClass().getName(), task);
		}
	}

	private void executeAfterTasks(final ITaskService taskService,
			final Collection<AbstractStateRetrieveTask> tasks) {
		for (final AbstractStateRetrieveTask task : tasks) {
			taskService.submitNow(task.getClass().getName(), task.setData());
		}
	}

	/**
	 * Initializes UI
	 */
	private void initializeUI() {
		Logger.debug("Initializing UI");
		try {
			// Call invokeAndWait to wait until splash screen is visible
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					Kernel.this.beanFactory.getBean(ILookAndFeelManager.class)
							.setLookAndFeel(
									Kernel.this.stateUI.getLookAndFeel(),
									Kernel.this.stateCore,
									Kernel.this.stateUI,
									Kernel.this.beanFactory
											.getBean(IOSManager.class));

					Kernel.this.beanFactory.getBean(HandlerInitializer.class)
							.setFrameForHandlers(
									Kernel.this.beanFactory
											.getBean(IFrame.class));
				}
			});
		} catch (InvocationTargetException e) {
			Logger.error(e);
			Logger.error(e.getCause());
		} catch (InterruptedException e) {
			Logger.error(e);
			Logger.error(e.getCause());
		}
	}

	/**
	 * Creates UI
	 */
	private void createUI() {
		Logger.debug("Creating UI");
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// Start component creation
					startCreation();

					callActionsAfterStart();
					Logger.info(StringUtils.getString("Application started (",
							StringUtils.toString(Kernel.this.timer.stop(), 3),
							" seconds)"));
					Kernel.this.timer = null;
				}
			});
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (InvocationTargetException e) {
			Logger.error(e);
		}
	}

	@Override
	public void finish() {
		this.beanFactory.getBean(Finisher.class).finish();
	}

	/**
	 * Call actions after start.
	 */
	void callActionsAfterStart() {
		this.beanFactory.getBean(ApplicationLifeCycleListeners.class)
				.applicationStarted();
		this.beanFactory.getBean(ApplicationLifeCycleListeners.class)
				.allHandlersInitialized();
		this.beanFactory.getBean(ITaskService.class).submitOnce(
				"Deferred handler initialization", 3, new Runnable() {
					@Override
					public void run() {
						Kernel.this.beanFactory.getBean(
								ApplicationLifeCycleListeners.class)
								.deferredInitialization();
					}
				});
		this.beanFactory.getBean(StartCounter.class).addOne();
		this.beanFactory.getBean(ITaskService.class).submitOnce(
				"Check start counter", 30, new Runnable() {
					@Override
					public void run() {
						Kernel.this.beanFactory.getBean(StartCounter.class)
								.checkCounter();
					}
				});
	}

	/**
	 * Creates all objects of aTunes: visual objects, controllers, and handlers.
	 */
	private void startCreation() {
		Logger.debug("Starting components");
		this.beanFactory.getBean(IUIHandler.class).startVisualization();
	}

	@Override
	public void restart() {
		this.beanFactory.getBean(Finisher.class).restart();
	}

	@Override
	public void terminateWithError(final Throwable e) {
		Logger.fatal(e);
		System.exit(1);
	}
}
