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
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * This action enables a telnet user, or remote control to get the current
 * volume and if a parameter is set, set the volume precisely to the given
 * percentage.
 *
 */
public class PrintVolumeRemoteAction extends RemoteAction {

	private static final long serialVersionUID = -9122835254109082353L;
	private IPlayerHandler playerHandler;
	private IStatePlayer statePlayer;

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		if (CollectionUtils.isEmpty(parameters)) {
			return Integer.toString(statePlayer.getVolume());
		} else if (parameters.size() == 1) {
			try {
				playerHandler.setVolume(Integer.parseInt(parameters.get(0)));
				return Integer.toString(statePlayer.getVolume());
			} catch (NumberFormatException ex) {
				return "Please input a number.";
			}
		} else {
			return "Bad number of arguments";
		}
	}

	@Override
	protected String getHelpText() {
		return "Outputs the current volume";
	}

	@Override
	protected String getOptionalParameters() {
		return null;
	}
}
