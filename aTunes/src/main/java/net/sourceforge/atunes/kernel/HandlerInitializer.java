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

import java.util.List;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IHandlerBackgroundInitializationTask;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Initializes handlers
 * @author alex
 *
 */
public class HandlerInitializer {
	
	private List<AbstractHandler> handlers;
	
	private ITaskService taskService;
	
	/**
	 * @param taskService
	 */
	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}

    /**
     * initializes all defined handlers
     * @param state
     */
    void initializeHandlers() {
        for (AbstractHandler handler : handlers) {
        	IHandlerBackgroundInitializationTask task = handler.getInitializationTask();
        	if (task != null) {
                final Runnable initializationTask = task.getInitializationTask();
                if (initializationTask != null) {
                	final Runnable afterTask = task.getInitializationCompletedTask();
                	taskService.submitNow(StringUtils.getString(handler.getClass().getName(), ".InitializationTask"), new Runnable() {
                		@Override
                		public void run() {
                			initializationTask.run();
                			if (afterTask != null) {
                				GuiUtils.callInEventDispatchThread(afterTask);
                			}
                		}            		
                	});
                }
        	}
        }

        // Initialize handlers
        for (final AbstractHandler handler : handlers) {
            handler.initHandler();
        }
    }

	void setFrameForHandlers(IFrame frame) {
		for (AbstractHandler handler : handlers) {
			handler.setFrame(frame);
		}
	}
	
	/**
	 * @param handlers
	 */
	public void setHandlers(List<AbstractHandler> handlers) {
		this.handlers = handlers;
	}
}
