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

package net.sourceforge.atunes.kernel.modules.process;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * The Class SetAlbumNamesProcess.
 */
public class SetAlbumNamesProcess extends AbstractChangeTagProcess {

	private IUnknownObjectChecker unknownObjectChecker;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	protected void changeTag(final ILocalAudioObject file) {
		if (unknownObjectChecker.isUnknownAlbum(file
				.getAlbum(unknownObjectChecker))) {
			// Take name from folder
			String albumName = fileManager.getParentName(file);
			getTagHandler().setAlbum(file, albumName);
		}
	}
}
