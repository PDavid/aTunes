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

import java.util.Collection;

import net.sourceforge.atunes.model.IState;
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

	private IState state;
	
	private Collection<IWindowListener> listeners;

	/**
	 * @param frame
	 * @param taskService
	 * @param state
	 * @param listeners
	 */
	FrameListenersDecorator(AbstractSingleFrame frame,
			ITaskService taskService, IState state,
			Collection<IWindowListener> listeners) {
		super();
		this.frame = frame;
		this.taskService = taskService;
		this.state = state;
		this.listeners = listeners;
	}

	void decorate() {
		// Set window state listener
		net.sourceforge.atunes.gui.frame.WindowListener listener = new net.sourceforge.atunes.gui.frame.WindowListener(listeners);
		frame.addWindowStateListener(listener);
		frame.addWindowFocusListener(listener);
		frame.addComponentListener(new SingleFrameComponentAdapter(frame, taskService, state));
	}
}


