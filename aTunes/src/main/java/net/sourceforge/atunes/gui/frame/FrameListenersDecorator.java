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

import java.util.Collection;

import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWindowListener;


/**
 * Decorates a frame by adding all listeners needed to keep size, location, play list scrolled etc.
 * @author alex
 *
 */
class FrameListenersDecorator {

	private AbstractSingleFrame frame;

	private ITaskService taskService;

	private IStateUI stateUI;
	
	private Collection<IWindowListener> listeners;

	/**
	 * @param frame
	 * @param taskService
	 * @param stateUI
	 * @param listeners
	 */
	FrameListenersDecorator(AbstractSingleFrame frame,
			ITaskService taskService, IStateUI stateUI,
			Collection<IWindowListener> listeners) {
		super();
		this.frame = frame;
		this.taskService = taskService;
		this.stateUI = stateUI;
		this.listeners = listeners;
	}

	void decorate() {
		// Set window state listener
		net.sourceforge.atunes.gui.frame.WindowListener listener = new net.sourceforge.atunes.gui.frame.WindowListener(listeners);
		frame.addWindowStateListener(listener);
		frame.addWindowFocusListener(listener);
		frame.addComponentListener(new SingleFrameComponentAdapter(frame, taskService, stateUI));
	}
}


