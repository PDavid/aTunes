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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * This action by default returns the current song info and with a parameter
 * exists prints out the info of the file specified.
 */
public class PrintSongInfoRemoteAction extends RemoteAction {

	private static final String SEPARATOR = "\n";

	private static final long serialVersionUID = 1742363002472924706L;

	private IRepositoryHandler repositoryHandler;

	private IPlayListHandler playListHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param handler
	 */
	public void setPlayListHandler(final IPlayListHandler handler) {
		this.playListHandler = handler;
	}

	/**
	 * @param handler
	 */
	public void setRepositoryHandler(final IRepositoryHandler handler) {
		this.repositoryHandler = handler;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		String errorResponse = null;
		IAudioObject object = null;
		if (CollectionUtils.isEmpty(parameters)) {
			object = this.playListHandler
					.getCurrentAudioObjectFromCurrentPlayList();
			if (object == null) {
				errorResponse = "Nothing is playing";
			}
		} else {
			String name = StringUtils.join(parameters, ' ');
			object = this.repositoryHandler.getFile(name);
			if (object == null) {
				errorResponse = "File not found";
			}
		}

		if (object != null) {
			return returnInformation(object);
		}

		return errorResponse;
	}

	/**
	 * @param object
	 */
	private String returnInformation(final IAudioObject object) {
		int mins = object.getDuration() / 60;
		int sec = object.getDuration() % 60;
		return net.sourceforge.atunes.utils.StringUtils.getString("Name: ",
				object.getTitleOrFileName(), SEPARATOR, "Artist: ",
				object.getArtist(this.unknownObjectChecker), SEPARATOR,
				"Album: ", object.getAlbum(this.unknownObjectChecker),
				SEPARATOR, "Year: ", object.getYear(this.unknownObjectChecker),
				SEPARATOR, "Duration: ", mins, ":", sec);
	}

	@Override
	protected String getHelpText() {
		return "Prints out current song information, or the song information of the specified filename";
	}

	@Override
	protected String getOptionalParameters() {
		return "[fileName]";
	}
}
