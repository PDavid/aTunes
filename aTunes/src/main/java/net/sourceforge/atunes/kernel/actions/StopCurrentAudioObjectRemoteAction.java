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

/**
 * Remote action to stop current audio object
 * @author alex
 *
 */
public class StopCurrentAudioObjectRemoteAction extends RemoteAction {

	private static final long serialVersionUID = -1177020643937370678L;

	private CustomAbstractAction stopAction;

	/**
	 * @param stopAction
	 */
	public void setStopAction(final CustomAbstractAction stopAction) {
		this.stopAction = stopAction;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		callAction(stopAction);
		return OK;
	}

	@Override
	protected String getHelpText() {
		return "Stops playback";
	}

	@Override
	protected String getOptionalParameters() {
		return null;
	}
}
