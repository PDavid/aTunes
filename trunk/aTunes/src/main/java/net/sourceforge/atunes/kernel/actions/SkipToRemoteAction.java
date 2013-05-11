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

import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action enables a user to seek from telnet in percentages.
 * 
 */
public class SkipToRemoteAction extends RemoteAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6798387500260668197L;

	private IPlayerHandler playerHandler;

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		if (parameters.size() >= 1) {
			try {
				int perc = Integer.parseInt(parameters.get(0));
				if (perc < 0 || perc > 100) {
					throw new NumberFormatException();
				}
				this.playerHandler.seekCurrentAudioObject(perc);
				return OK;
			} catch (NumberFormatException ex) {
				return StringUtils.getString("Bad number format: ",
						parameters.get(0));
			}
		}
		return "Missing parameter";
	}

	@Override
	protected String getHelpText() {
		return "Skips the playback to the percentage chosen. 0-100";
	}

	@Override
	protected String getOptionalParameters() {
		return "[number]";
	}
}
