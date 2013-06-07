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

package net.sourceforge.atunes.kernel.modules.context.event;

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Youtube data source
 * 
 * @author alex
 * 
 */
public class EventsDataSource implements IContextInformationSource {

	private IWebServicesHandler webServicesHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	private List<IEvent> events;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	@Override
	public void getData(final IAudioObject audioObject) {
		this.events = webServicesHandler.getArtistEvents(audioObject
				.getArtist(unknownObjectChecker));
	}

	/**
	 * @return events found
	 */
	public List<IEvent> getEvents() {
		return events;
	}

	@Override
	public void cancel() {
	}
}
