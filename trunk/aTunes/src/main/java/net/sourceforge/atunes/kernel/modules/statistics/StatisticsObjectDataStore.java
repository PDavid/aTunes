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

package net.sourceforge.atunes.kernel.modules.statistics;

import java.io.IOException;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IObjectDataStore;
import net.sourceforge.atunes.model.IStatistics;
import net.sourceforge.atunes.utils.KryoSerializerService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Data store for statistics
 * 
 * @author alex
 * 
 */
public class StatisticsObjectDataStore implements IObjectDataStore<IStatistics> {

	private IOSManager osManager;

	private KryoSerializerService kryoSerializerService;

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

	@Override
	public IStatistics read() {
		IStatistics result = null;
		try {
			result = (IStatistics) kryoSerializerService.readObjectFromFile(
					getFileName(), Statistics.class);
		} catch (IOException e) {
			Logger.error(e);
		}
		return result;
	}

	/**
	 * @return file name to store favorites
	 */
	private String getFileName() {
		return StringUtils.getString(osManager.getUserConfigFolder(),
				osManager.getFileSeparator(), Constants.CACHE_STATISTICS_NAME);
	}

	@Override
	public void write(IStatistics statistics) {
		kryoSerializerService.writeObjectToFile(getFileName(), statistics);
	}

	@Override
	public IStatistics read(String id) {
		return read();
	}

	@Override
	public void write(String id, IStatistics object) {
		write(object);
	}
}
