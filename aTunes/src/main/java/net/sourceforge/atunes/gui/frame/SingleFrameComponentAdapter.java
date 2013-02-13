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

package net.sourceforge.atunes.gui.frame;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Future;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ITaskService;

/**
 * Saves size and position of frame
 * 
 * @author alex
 * 
 */
public final class SingleFrameComponentAdapter extends ComponentAdapter {

	private IFrame frame;

	private ITaskService taskService;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * Previous size tasks (if any)
	 */
	private Future<?> sizeFuture;

	/**
	 * Previous position tasks (if any)
	 */
	private Future<?> positionFuture;

	@Override
	public void componentResized(final ComponentEvent event) {
		saveSize();
	}

	@Override
	public void componentMoved(final ComponentEvent event) {
		savePosition(event);
	}

	/**
	 * Called to save size when an event is detected in single frame
	 */
	private void saveSize() {
		int width = this.frame.getSize().width;
		int height = this.frame.getSize().height;

		if (this.frame.isVisible() && width != 0 && height != 0) {
			// Task submitted after canceling previous ones to avoid executing
			// task after each call to component listener
			if (this.sizeFuture != null) {
				this.sizeFuture.cancel(false);
			}

			SaveFrameSizeTask task = this.beanFactory
					.getBean(SaveFrameSizeTask.class);
			task.setWidth(width);
			task.setHeight(height);
			this.sizeFuture = this.taskService.submitOnce("Save Frame Size", 1,
					task);
		}
	}

	/**
	 * Called to save position when an event is detected in single frame
	 * 
	 * @param event
	 */
	private void savePosition(final ComponentEvent event) {
		final int x = event.getComponent().getX();
		final int y = event.getComponent().getY();

		if (this.frame.isVisible()) {
			// Task submitted after canceling previous ones to avoid executing
			// task after each call to component listener
			if (this.positionFuture != null) {
				this.positionFuture.cancel(false);
			}

			SaveFramePositionTask task = this.beanFactory
					.getBean(SaveFramePositionTask.class);
			task.setX(x);
			task.setY(y);
			this.positionFuture = this.taskService.submitOnce(
					"Save Frame Position", 1, task);
		}
	}
}