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

import net.sourceforge.atunes.model.IRepositoryLoader;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;

/**
 * A default implementation of IRepositoryLoaderListener that does nothing
 * @author alex
 *
 */
class VoidRepositoryLoaderListener implements IRepositoryLoaderListener {

	@Override
	public void notifyCurrentPath(String path) {
		// Nothing to do
	}

	@Override
	public void notifyCurrentAlbum(String artist, String album) {
		// Nothing to do
	}

	@Override
	public void notifyFileLoaded() {
		// Nothing to do
	}

	@Override
	public void notifyFilesInRepository(int files) {
		// Nothing to do
	}

	@Override
	public void notifyFinishRead(IRepositoryLoader loader) {
		// Nothing to do
	}

	@Override
	public void notifyFinishRefresh(IRepositoryLoader loader) {
		// Nothing to do
	}

	@Override
	public void notifyRemainingTime(long time) {
		// Nothing to do
	}

	@Override
	public void notifyReadProgress() {
		// Nothing to do
	}
}
