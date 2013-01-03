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
 * Remote action to show OSD
 * @author alex
 *
 */
public class ShowOSDRemoteAction extends RemoteAction {

	private static final long serialVersionUID = 646318992035897920L;

	private CustomAbstractAction showOSDAction;

	/**
	 * @param showOSDAction
	 */
	public void setShowOSDAction(final CustomAbstractAction showOSDAction) {
		this.showOSDAction = showOSDAction;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		callAction(showOSDAction);
		return OK;
	}

	@Override
	protected String getHelpText() {
		return "Pops out a window with the current name of the song";
	}

	@Override
	protected String getOptionalParameters() {
		return null;
	}
}
