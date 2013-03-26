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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.IOException;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IObjectDataStore;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.InconsistentRepositoryException;
import net.sourceforge.atunes.utils.KryoSerializerService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Data store for repository
 * 
 * @author alex
 * 
 */
public class RepositoryObjectDataStore implements IObjectDataStore<IRepository> {

	private IOSManager osManager;

	private IStateRepository stateRepository;

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

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	@Override
	public IRepository read() {
		IRepository result = null;
		try {
			Object objectRead = kryoSerializerService.readObjectFromFile(
					getFileName(), Repository.class);
			if (objectRead != null) {
				result = (IRepository) objectRead;
				result.setStateRepository(stateRepository);

				// Check repository integrity
				result.validateRepository();
			}
		} catch (IOException e) {
			Logger.error(e);
		} catch (InconsistentRepositoryException e) {
			Logger.error(e);
		}
		return result;
	}

	/**
	 * @return file name to store repository
	 */
	private String getFileName() {
		String customRepositoryConfigFolder = osManager
				.getCustomRepositoryConfigFolder();
		if (customRepositoryConfigFolder == null) {
			customRepositoryConfigFolder = osManager.getUserConfigFolder();
		}
		return StringUtils.getString(customRepositoryConfigFolder,
				osManager.getFileSeparator(), Constants.CACHE_REPOSITORY_NAME);
	}

	@Override
	public void write(IRepository repository) {
		kryoSerializerService.writeObjectToFile(getFileName(), repository);
	}

	@Override
	public IRepository read(String id) {
		return read();
	}

	@Override
	public void write(String id, IRepository object) {
		write(object);
	}
}
