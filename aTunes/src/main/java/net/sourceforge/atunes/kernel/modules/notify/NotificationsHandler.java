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

package net.sourceforge.atunes.kernel.modules.notify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.INotificationEngine;
import net.sourceforge.atunes.model.INotificationsHandler;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.Logger;

/**
 * Responsible of handling notifications
 * @author alex
 *
 */
public final class NotificationsHandler extends AbstractHandler implements INotificationsHandler {

	private Map<String, INotificationEngine> engines;

	private INotificationEngine defaultEngine;

	private IStateUI stateUI;

	private IStateCore stateCore;

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	private Map<String, INotificationEngine> getEngines() {
		if (engines == null) {
			Logger.debug("Initializing notification engines");
			engines = new HashMap<String, INotificationEngine>();
			// Add here any new notification engine
			addNotificationEngine(engines, getDefaultEngine());
			addNotificationEngine(engines, getBean("libnotifyNotificationEngine", INotificationEngine.class));
			addNotificationEngine(engines, getBean("growlNotificationEngine", INotificationEngine.class));
		}
		return engines;
	}

	/**
	 * Adds a new notification engine to map of notifications
	 * @param engine
	 */
	private void addNotificationEngine(final Map<String, INotificationEngine> engines, final INotificationEngine engine) {
		engines.put(engine.getName(), engine);
	}

	/**
	 * @return notification engine to use
	 */
	private INotificationEngine getNotificationEngine() {
		INotificationEngine engine = getEngines().get(stateCore.getNotificationEngine());
		if (engine == null) {
			engine = getDefaultEngine();
		}
		return engine;
	}

	@Override
	public void applicationFinish() {
		getNotificationEngine().disposeNotifications();
	}

	@Override
	public void applicationStateChanged() {
		getNotificationEngine().updateNotification(stateUI);
	}

	@Override
	public void showNotification(final IAudioObject audioObject) {
		// only show notification if not in full screen
		if (!getBean(IFullScreenHandler.class).isVisible()) {
			getNotificationEngine().showNotification(audioObject);
		}
	}

	@Override
	public void playbackStateChanged(final PlaybackState newState, final IAudioObject currentAudioObject) {
		if (stateUI.isShowOSD() && newState == PlaybackState.PLAYING) {
			// Playing
			showNotification(currentAudioObject);
		}
	}

	@Override
	public List<String> getNotificationEngines() {
		List<String> names = new ArrayList<String>(getEngines().keySet());
		Collections.sort(names, new Comparator<String>() {
			@Override
			public int compare(final String o1, final String o2) {
				if (o1.equals(getDefaultEngine().getName())) {
					return -1;
				} else if (o2.equals(getDefaultEngine().getName())) {
					return 1;
				}
				return o1.compareTo(o2);
			}
		});
		return names;
	}

	@Override
	public INotificationEngine getNotificationEngine(final String name) {
		return getEngines().get(name);
	}

	@Override
	public INotificationEngine getDefaultEngine() {
		if (defaultEngine == null) {
			defaultEngine = getBean("defaultNotificationEngine", INotificationEngine.class);
		}
		return defaultEngine;
	}
}
