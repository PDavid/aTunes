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
 * Remote action to call play
 * @author alex
 *
 */
public class PlayRemoteAction extends RemoteAction {

	private static final long serialVersionUID = -1122746023245126869L;

	private CustomAbstractAction playAction;

	/**
	 * @param playAction
	 */
	public void setPlayAction(final CustomAbstractAction playAction) {
		this.playAction = playAction;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		callAction(playAction);
		return OK;
	}

	@Override
	protected String getHelpText() {
		return "Starts or pauses playback";
	}

	@Override
	protected String getOptionalParameters() {
		return null;
	}
}
