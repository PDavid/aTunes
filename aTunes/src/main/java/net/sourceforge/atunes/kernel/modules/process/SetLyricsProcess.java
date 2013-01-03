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

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * The Class SetLyricsProcess.
 */
public class SetLyricsProcess extends AbstractChangeTagProcess {

	private IWebServicesHandler webServicesHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	@Override
	protected void changeTag(final ILocalAudioObject file) throws IOException {
		// Check if no lyrics is present and we have enough info for a query
		if (file.getLyrics().isEmpty() && !file.getArtist(unknownObjectChecker).isEmpty() && !file.getTitle().isEmpty()) {
			ILyrics lyrics = webServicesHandler.getLyrics(file.getArtist(unknownObjectChecker), file.getTitle());
			String lyricsString = lyrics != null ? lyrics.getLyrics().trim() : "";
			if (!lyricsString.isEmpty()) {
				getTagHandler().setLyrics(file, lyricsString);
			}
		}
	}
}
