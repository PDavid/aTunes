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

import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.utils.KryoSerializerService;

/**
 * Writes dynamic play lists
 * 
 * @author alex
 * 
 */
public class DynamicPlayListWriter {

	private KryoSerializerService kryoSerializerService;

	/**
	 * @param kryoSerializerService
	 */
	public void setKryoSerializerService(
			final KryoSerializerService kryoSerializerService) {
		this.kryoSerializerService = kryoSerializerService;
	}

	/**
	 * Writes a dynamic play list to a file
	 * 
	 * @param playlist
	 * @param file
	 * @return
	 */
	boolean write(final IPlayList playlist, final File file) {
		if (playlist instanceof DynamicPlayList) {
			DynamicPlayListData data = new DynamicPlayListData();
			data.setQuery(((DynamicPlayList) playlist).getQuery());

			return this.kryoSerializerService.writeObjectToFile(
					file.getAbsolutePath(), data);
		} else {
			throw new IllegalArgumentException("Not a DynamicPlayList");
		}
	}
}
