/*
 * aTunes 3.0.0
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

import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.ITaskService;


final class SingleFrameComponentAdapter extends ComponentAdapter {
	
	private final AbstractSingleFrame abstractSingleFrame;
	
	private final ITaskService taskService;
	
	private final IStateUI stateUI;

	/**
	 * Previous size tasks (if any)
	 */
	private Future<?> sizeFuture;

	/**
	 * Previous position tasks (if any)
	 */
	private Future<?> positionFuture;

	/**
	 * @param abstractSingleFrame
	 * @param taskService
	 * @param stateUI
	 */
	SingleFrameComponentAdapter(AbstractSingleFrame abstractSingleFrame, ITaskService taskService, IStateUI stateUI) {
		this.abstractSingleFrame = abstractSingleFrame;
		this.taskService = taskService;
		this.stateUI = stateUI;
	}

	@Override
	public void componentResized(ComponentEvent event) {				
		saveSize();
	}

	@Override
	public void componentMoved(ComponentEvent event) {
		savePosition(event);
	}

	/**
	 * Called to save size when an event is detected in single frame
	 */
	private void saveSize() {
		int width = abstractSingleFrame.getSize().width;
		int height = abstractSingleFrame.getSize().height;
		
		if (abstractSingleFrame.isVisible() && width != 0 && height != 0) {
			// Task submitted after canceling previous ones to avoid executing task after each call to component listener
			if (sizeFuture != null) {
				sizeFuture.cancel(false);
			}
			
			sizeFuture = taskService.submitOnce("Save Frame Size", 1, new SaveFrameSizeTask(abstractSingleFrame, stateUI, width, height));
		}
	}

	/**
	 * Called to save position when an event is detected in single frame
	 * @param event
	 */
	private void savePosition(final ComponentEvent event) {
		final int x = event.getComponent().getX();
		final int y = event.getComponent().getY();
		
		if (abstractSingleFrame.isVisible()) {
			// Task submitted after canceling previous ones to avoid executing task after each call to component listener
			if (positionFuture != null) {
				positionFuture.cancel(false);
			}
			
			positionFuture = taskService.submitOnce("Save Frame Position", 1, new SaveFramePositionTask(stateUI, x, y));
		}
	}
}