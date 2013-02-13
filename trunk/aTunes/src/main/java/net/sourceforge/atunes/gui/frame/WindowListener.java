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

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IWindowListener;
import net.sourceforge.atunes.utils.Logger;

/**
 * Listens to window events
 * 
 * @author alex
 * 
 */
public class WindowListener extends WindowAdapter {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public void windowStateChanged(final WindowEvent e) {
		if (e.getNewState() == Frame.ICONIFIED) {
			Logger.debug("Window Iconified");
			windowIconifiedEvent();
		} else if (e.getNewState() != Frame.ICONIFIED) {
			Logger.debug("Window Deiconified");
			windowDeiconifiedEvent();
		}
	}

	/**
	 * Called when window is deiconified
	 * 
	 * @param path
	 */
	private void windowDeiconifiedEvent() {
		for (IWindowListener l : this.beanFactory
				.getBeans(IWindowListener.class)) {
			l.windowDeiconified();
		}
	}

	/**
	 * Called when window is iconified
	 * 
	 * @param path
	 */
	private void windowIconifiedEvent() {
		for (IWindowListener l : this.beanFactory
				.getBeans(IWindowListener.class)) {
			l.windowIconified();
		}
	}
}
