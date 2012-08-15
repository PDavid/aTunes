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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.IOException;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IObjectDataStore;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.KryoSerializerService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Data store for playlist definition
 * @author alex
 *
 */
public class PlayListObjectDataStore implements IObjectDataStore<IListOfPlayLists> {
	
	private KryoSerializerService kryoSerializerService;
	
	private IOSManager osManager;
	
	private IStatePlayer statePlayer;

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * @param kryoSerializerService
	 */
	public void setKryoSerializerService(KryoSerializerService kryoSerializerService) {
		this.kryoSerializerService = kryoSerializerService;
	}
	
	@Override
	public IListOfPlayLists read() {
		try {
			ListOfPlayLists listOfPlayLists = (ListOfPlayLists) kryoSerializerService.readObjectFromFile(getFileName(), ListOfPlayLists.class);
			if (listOfPlayLists != null) {
				listOfPlayLists.setStatePlayer(statePlayer);
			}
			return listOfPlayLists;
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * @return file name to store play list definition
	 */
	private String getFileName() {
		return StringUtils.getString(osManager.getUserConfigFolder(), osManager.getFileSeparator(), Constants.PLAYLISTS_FILE);
	}

	@Override
	public void write(IListOfPlayLists contents) {
		kryoSerializerService.writeObjectToFile(getFileName(), contents);
	}
	
	@Override
	public IListOfPlayLists read(String id) {
		return read();
	}
	
	@Override
	public void write(String id, IListOfPlayLists object) {
		write(object);
	}
}
