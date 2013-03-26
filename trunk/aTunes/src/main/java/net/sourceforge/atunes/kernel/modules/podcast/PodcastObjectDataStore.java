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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IObjectDataStore;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.utils.KryoSerializerService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Data store for podcasts
 * 
 * @author alex
 * 
 */
public class PodcastObjectDataStore implements
		IObjectDataStore<List<IPodcastFeed>> {

	private KryoSerializerService kryoSerializerService;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param kryoSerializerService
	 */
	public void setKryoSerializerService(
			KryoSerializerService kryoSerializerService) {
		this.kryoSerializerService = kryoSerializerService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IPodcastFeed> read() {
		try {
			return (List<IPodcastFeed>) kryoSerializerService
					.readObjectFromFile(getFileName(), ArrayList.class);
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * @return file name to store play list definition
	 */
	private String getFileName() {
		return StringUtils.getString(osManager.getUserConfigFolder(),
				osManager.getFileSeparator(), Constants.PODCAST_FEED_CACHE);
	}

	@Override
	public void write(List<IPodcastFeed> contents) {
		kryoSerializerService.writeObjectToFile(getFileName(), contents);
	}

	@Override
	public List<IPodcastFeed> read(String id) {
		return read();
	}

	@Override
	public void write(String id, List<IPodcastFeed> object) {
		write(object);
	}
}
