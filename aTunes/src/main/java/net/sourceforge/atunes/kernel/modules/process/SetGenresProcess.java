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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * The Class SetGenresProcess.
 */
public class SetGenresProcess extends AbstractChangeTagProcess {

	private Map<ILocalAudioObject, String> filesAndGenres;

	private IWebServicesHandler webServicesHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	@Override
	protected void retrieveInformationBeforeChangeTags() {
		super.retrieveInformationBeforeChangeTags();
		this.filesAndGenres = getGenresForFiles(getFilesToChange());
	}

	@Override
	protected void changeTag(final ILocalAudioObject file) throws IOException {
		String genre = this.filesAndGenres.get(file);
		// If file has already genre setted, avoid
		if (!file.getGenre(unknownObjectChecker).equals(genre)) {
			getTagHandler().setGenre(file, genre);
		}
	}

	/**
	 * Gets the genres for files.
	 * 
	 * @param files
	 *            the files
	 * 
	 * @return the genres for files
	 */
	private Map<ILocalAudioObject, String> getGenresForFiles(final Collection<ILocalAudioObject> files) {
		Map<ILocalAudioObject, String> result = new HashMap<ILocalAudioObject, String>();

		Map<String, String> tagCache = new HashMap<String, String>();

		for (ILocalAudioObject f : files) {
			if (!unknownObjectChecker.isUnknownArtist(f.getArtist(unknownObjectChecker))) {
				String tag = null;
				if (tagCache.containsKey(f.getArtist(unknownObjectChecker))) {
					tag = tagCache.get(f.getArtist(unknownObjectChecker));
				} else {
					tag = webServicesHandler.getArtistTopTag(f.getArtist(unknownObjectChecker));
					tagCache.put(f.getArtist(unknownObjectChecker), tag);
					// Wait one second to avoid IP banning
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// Nothing to do
					}
				}
				if (tag != null) {
					result.put(f, tag);
				}
			}
		}

		return result;
	}
}
