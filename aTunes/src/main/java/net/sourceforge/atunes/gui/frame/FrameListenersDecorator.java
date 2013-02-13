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

import net.sourceforge.atunes.model.IBeanFactory;

/**
 * Decorates a frame by adding all listeners needed to keep size, location, play
 * list scrolled etc.
 * 
 * @author alex
 * 
 */
public class FrameListenersDecorator {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	void decorate(final AbstractSingleFrame frame) {
		// Set window state listener
		net.sourceforge.atunes.gui.frame.WindowListener listener = this.beanFactory
				.getBean(WindowListener.class);
		frame.addWindowStateListener(listener);
		frame.addWindowFocusListener(listener);
		frame.addComponentListener(this.beanFactory
				.getBean(SingleFrameComponentAdapter.class));
	}
}
