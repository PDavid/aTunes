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

package net.sourceforge.atunes.kernel.modules.ui;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IFrameFactory;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.utils.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Creates frame
 * 
 * @author alex
 * 
 */
public class FrameFactory implements IFrameFactory, ApplicationContextAware {

	private IStateUI stateUI;

	private String defaultFrameClass;

	private ApplicationContext context;

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	@Override
	public void setDefaultFrameClass(final String defaultFrameClass) {
		this.defaultFrameClass = defaultFrameClass;
	}

	@Override
	public IFrame create() {
		IFrame frame = null;
		Class<? extends IFrame> clazz = stateUI.getFrameClass();
		if (clazz != null) {
			try {
				frame = clazz.newInstance();
			} catch (InstantiationException e) {
				Logger.error(e);
			} catch (IllegalAccessException e) {
				Logger.error(e);
			}
		}

		if (frame == null) {
			frame = constructDefaultFrame();
			if (frame != null) {
				stateUI.setFrameClass(frame.getClass());
			}
		}

		if (frame == null) {
			throw new IllegalArgumentException("Could not create main frame");
		}

		frame.setApplicationContext(context);

		return frame;
	}

	/**
	 * Creates default frame
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private IFrame constructDefaultFrame() {
		IFrame frame = null;
		try {
			frame = (IFrame) Class.forName(defaultFrameClass).newInstance();
		} catch (InstantiationException e) {
			Logger.error(e);
		} catch (IllegalAccessException e) {
			Logger.error(e);
		} catch (ClassNotFoundException e) {
			Logger.error(e);
		}
		return frame;
	}

}
