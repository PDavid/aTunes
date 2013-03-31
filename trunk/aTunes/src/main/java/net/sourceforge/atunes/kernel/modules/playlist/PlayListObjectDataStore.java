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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.IOException;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IObjectDataStore;
import net.sourceforge.atunes.utils.KryoSerializerService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Data store for playlist definition
 * 
 * @author alex
 * 
 */
public class PlayListObjectDataStore implements
		IObjectDataStore<IListOfPlayLists> {

	private KryoSerializerService kryoSerializerService;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param kryoSerializerService
	 */
	public void setKryoSerializerService(
			final KryoSerializerService kryoSerializerService) {
		this.kryoSerializerService = kryoSerializerService;
	}

	@Override
	public IListOfPlayLists read() {
		try {
			return (ListOfPlayLists) this.kryoSerializerService
					.readObjectFromFile(getFileName(), ListOfPlayLists.class);
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * @return file name to store play list definition
	 */
	private String getFileName() {
		return StringUtils.getString(this.osManager.getUserConfigFolder(),
				this.osManager.getFileSeparator(), Constants.PLAYLISTS_FILE);
	}

	@Override
	public void write(final IListOfPlayLists contents) {
		this.kryoSerializerService.writeObjectToFile(getFileName(), contents);
	}

	@Override
	public IListOfPlayLists read(final String id) {
		return read();
	}

	@Override
	public void write(final String id, final IListOfPlayLists object) {
		write(object);
	}
}
