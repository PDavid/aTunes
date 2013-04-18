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

package net.sourceforge.atunes.kernel.modules.webservices;

import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.kernel.actions.RemoveLovedSongInLastFmAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Worker to remove song from last.fm
 * 
 * @author alex
 * 
 */
public class RemoveLovedSongBackgroundWorker extends
		BackgroundWorker<Void, Void> {

	private IWebServicesHandler webServicesHandler;

	private IAudioObject audioObject;

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param audioObject
	 */
	public void remove(IAudioObject audioObject) {
		this.audioObject = audioObject;
		execute();
	}

	@Override
	protected void before() {
		getBeanFactory().getBean(RemoveLovedSongInLastFmAction.class)
				.setEnabled(false);
	}

	@Override
	protected Void doInBackground() {
		this.webServicesHandler.removeLovedSong(audioObject);
		return null;
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected void done(Void result) {
		getBeanFactory().getBean(RemoveLovedSongInLastFmAction.class)
				.setEnabled(true);
	}
}
