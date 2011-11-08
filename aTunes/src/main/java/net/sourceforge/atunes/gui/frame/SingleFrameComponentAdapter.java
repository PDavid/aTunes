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

package net.sourceforge.atunes.gui.frame;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Future;

import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;


final class SingleFrameComponentAdapter extends ComponentAdapter {
	
	private final AbstractSingleFrame abstractSingleFrame;
	
	private final ITaskService taskService;
	
	private final IState state;

	/**
	 * Previous tasks (if any)
	 */
	private Future<?> future;

	/**
	 * @param abstractSingleFrame
	 * @param taskService
	 * @param state
	 */
	SingleFrameComponentAdapter(AbstractSingleFrame abstractSingleFrame, ITaskService taskService, IState state) {
		this.abstractSingleFrame = abstractSingleFrame;
		this.taskService = taskService;
		this.state = state;
	}

	@Override
	public void componentResized(ComponentEvent event) {				
		saveState(event);
	}

	@Override
	public void componentMoved(ComponentEvent event) {
		saveState(event);
	}

	/**
	 * Called to save state when an event is detected in single frame
	 * @param event
	 */
	private void saveState(final ComponentEvent event) {
		final int width = abstractSingleFrame.getSize().width;
		final int height = abstractSingleFrame.getSize().height;
		final int x = event.getComponent().getX();
		final int y = event.getComponent().getY();
		
		if (abstractSingleFrame.isVisible() && width != 0 && height != 0) {
			// Task submitted after canceling previous ones to avoid executing task after each call to component listener
			if (future != null) {
				future.cancel(false);
			}
			
			future = taskService.submitOnce("Save Frame State", 1, new SaveFrameStateTask(abstractSingleFrame, state, width, height, x, y));
		}
	}
}