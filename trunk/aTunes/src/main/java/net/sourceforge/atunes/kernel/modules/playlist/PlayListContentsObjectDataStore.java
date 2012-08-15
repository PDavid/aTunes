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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IObjectDataStore;
import net.sourceforge.atunes.utils.KryoSerializerService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Data store for playlist contents
 * @author alex
 *
 */
public class PlayListContentsObjectDataStore implements IObjectDataStore<List<List<IAudioObject>>> {
	
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
	public void setKryoSerializerService(KryoSerializerService kryoSerializerService) {
		this.kryoSerializerService = kryoSerializerService;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<List<IAudioObject>> read() {
		try {
			return (List<List<IAudioObject>>) kryoSerializerService.readObjectFromFile(getFileName(), ArrayList.class);
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	@Override
	public void write(List<List<IAudioObject>> contents) {
		kryoSerializerService.writeObjectToFile(getFileName(), contents);
	}
	
	/**
	 * @return file name to store play list contents
	 */
	private String getFileName() {
		return StringUtils.getString(osManager.getUserConfigFolder(), osManager.getFileSeparator(), Constants.PLAYLISTS_CONTENTS_FILE);
	}

	@Override
	public List<List<IAudioObject>> read(String id) {
		return read();
	}
	
	@Override
	public void write(String id, List<List<IAudioObject>> object) {
		write(object);
	}
}
