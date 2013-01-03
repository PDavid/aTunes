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

package net.sourceforge.atunes.kernel;

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IHandler;
import net.sourceforge.atunes.model.IHandlerBackgroundInitializationTask;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.PlaybackState;

/**
 * An abstract handler
 * @author alex
 *
 */
public abstract class AbstractHandler implements IHandler {

	private IFrame frame;

	private IOSManager osManager;

	private IHandlerBackgroundInitializationTask initializationTask;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return
	 */
	protected final IBeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param initializationTask
	 */
	public void setInitializationTask(final IHandlerBackgroundInitializationTask initializationTask) {
		this.initializationTask = initializationTask;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @return os manager
	 */
	protected IOSManager getOsManager() {
		return osManager;
	}

	/**
	 * @return the frame
	 */
	protected IFrame getFrame() {
		return frame;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	@Override
	public final IHandlerBackgroundInitializationTask getInitializationTask() {
		return initializationTask;
	}

	@Override
	public void deferredInitialization() {}

	@Override
	public void allHandlersInitialized() {}

	@Override
	public void favoritesChanged() {}

	@Override
	public void deviceConnected(final String location) {}

	@Override
	public void deviceReady(final String location) {}

	@Override
	public void deviceDisconnected(final String location) {}

	@Override
	public void playbackStateChanged(final PlaybackState newState, final IAudioObject currentAudioObject) {}

	@Override
	public void audioObjectsAdded(final List<IPlayListAudioObject> audioObjectsAdded) {}

	@Override
	public void audioObjectsRemoved(final List<IPlayListAudioObject> audioObjectsRemoved) {}

	@Override
	public void applicationFinish() {}

	@Override
	public void applicationStateChanged() {}

	@Override
	public void applicationStarted() {}

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(final IAudioObject audioObject) {}

	/**
	 * Initializes handler
	 */
	 protected void initHandler() {}

	@Override
	public void windowIconified() {}

	@Override
	public void windowDeiconified() {}

	/**
	 * Delegate method to get beans
	 * @param <T>
	 * @param beanType
	 * @return
	 */
	protected <T> T getBean(final Class<T> beanType) {
		return beanFactory.getBean(beanType);
	}

	/**
	 * Returns bean with given name and type
	 * @param <T>
	 * @param name
	 * @param clazz
	 * @return
	 */
	protected <T> T getBean(final String name, final Class<T> clazz) {
		return beanFactory.getBean(name, clazz);
	}
}
