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

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.utils.KryoSerializerService;
import net.sourceforge.atunes.utils.Logger;

/**
 * Reads play lists
 * 
 * @author alex
 * 
 */
public class DynamicPlayListReader {

	private KryoSerializerService kryoSerializerService;

	private IPlayListHandler playListHandler;

	private ISearchHandler searchHandler;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param searchHandler
	 */
	public void setSearchHandler(final ISearchHandler searchHandler) {
		this.searchHandler = searchHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param kryoSerializerService
	 */
	public void setKryoSerializerService(
			final KryoSerializerService kryoSerializerService) {
		this.kryoSerializerService = kryoSerializerService;
	}

	void read(final File file) {
		try {
			DynamicPlayListData data = (DynamicPlayListData) this.kryoSerializerService
					.readObjectFromFile(file.getAbsolutePath(),
							DynamicPlayListData.class);
			if (data != null) {
				ISearchNode query = data.getQuery().createSearchQuery(
						this.beanFactory);
				// Then execute
				Collection<IAudioObject> result = this.searchHandler
						.search(query);
				// Create play list
				this.playListHandler.newDynamicPlayList(query, result);
			}
		} catch (IOException e) {
			Logger.error(e);
		}
	}
}
