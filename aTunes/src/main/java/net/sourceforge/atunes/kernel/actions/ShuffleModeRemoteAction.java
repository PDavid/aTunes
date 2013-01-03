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
 * Remote action to enable shuffle
 * @author alex
 *
 */
public class ShuffleModeRemoteAction extends RemoteAction {

	private static final String ON = "on";

	private static final String OFF = "off";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1135197116966774313L;

	@Override
	public String runCommand(final List<String> parameters) {
		if(parameters.size() == 1) {
			String parameter = parameters.get(0);
			if(parameter.equalsIgnoreCase(OFF)) {
				callToggleAction(ShuffleModeAction.class, false);
			} else if(parameter.equalsIgnoreCase(ON)) {
				callToggleAction(ShuffleModeAction.class, true);
			}
			return OK;
		} else {
			return "Bad number of arguments, plase input either a ON of OFF.";
		}
	}

	@Override
	protected String getHelpText() {
		return "Toggles shuffle playlist on/off";
	}

	@Override
	protected String getOptionalParameters() {
		return "[on|off]";
	}
}
