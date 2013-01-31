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

import java.io.File;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.FileUtils;

/**
 * Several utilities to manage files of local audio objects
 * 
 * @author alex
 * 
 */
public class FileManager implements IFileManager {

	@Override
	public String getPath(final ILocalAudioObject ao) {
		if (ao != null) {
			return FileUtils.getPath(ao.getFile());
		}
		return null;
	}

	@Override
	public File getFile(final ILocalAudioObject ao) {
		if (ao != null) {
			return ao.getFile();
		}
		return null;
	}

	@Override
	public boolean fileExists(final ILocalAudioObject ao) {
		if (ao != null) {
			return ao.getFile().exists();
		}
		return false;
	}

	@Override
	public long getFileSize(final ILocalAudioObject ao) {
		if (ao != null) {
			return ao.getFile().length();
		}
		return 0;
	}

	@Override
	public String getFileName(final ILocalAudioObject ao) {
		if (ao != null) {
			return ao.getFile().getName();
		}
		return null;
	}

	@Override
	public String getParentFile(final ILocalAudioObject ao) {
		if (ao != null) {
			return FileUtils.getPath(ao.getFile().getParentFile());
		}
		return null;
	}
}
