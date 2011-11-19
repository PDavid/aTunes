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

package net.sourceforge.atunes.kernel;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.Logger;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class HandlerInitializer implements ApplicationContextAware {
	
	private static final class InitHandlerRunnable implements Runnable {
		
		private final AbstractHandler handler;

		private InitHandlerRunnable(AbstractHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			handler.initHandler();
		}
	}

	private Collection<AbstractHandler> handlers;

    /**
     * Creates and registers all defined handlers
     * @param state
     */
    void registerAndInitializeHandlers(IState state) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        // Register handlers
        for (AbstractHandler handler : handlers) {
            Runnable task = handler.getPreviousInitializationTask();
            if (task != null) {
                executorService.submit(task);
            }
        }

        // Initialize handlers
        for (final AbstractHandler handler : handlers) {
            executorService.submit(new InitHandlerRunnable(handler));
        }

        executorService.shutdown();
        
        try {
			executorService.awaitTermination(100, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.error(e);
		}
    }

	void setFrameForHandlers(IFrame frame) {
		for (AbstractHandler handler : handlers) {
			handler.setFrame(frame);
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.handlers = applicationContext.getBeansOfType(AbstractHandler.class).values();
	}
}
