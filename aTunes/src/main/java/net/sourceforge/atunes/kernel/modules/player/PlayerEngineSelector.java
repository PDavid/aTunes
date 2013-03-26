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

package net.sourceforge.atunes.kernel.modules.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.Logger;

import org.apache.commons.lang.ArrayUtils;

class PlayerEngineSelector {

	/**
	 * Names of all engines
	 */
	private String[] engineNames;

	private List<AbstractPlayerEngine> engines;

	private IOSManager osManager;

	private IStatePlayer statePlayer;

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param engines
	 */
	public void setEngines(List<AbstractPlayerEngine> engines) {
		this.engines = engines;
	}

	/**
	 * Selects player engine
	 * 
	 * @return player engine or null
	 */
	IPlayerEngine selectPlayerEngine() {
		List<AbstractPlayerEngine> availableEngines = getAvailableEngines();
		if (availableEngines.isEmpty()) {
			return null;
		}

		engineNames = getEngineNames(availableEngines);
		Logger.info("List of availables engines : ",
				ArrayUtils.toString(engineNames));

		// Get engine of application state (default or selected by user)
		String selectedEngine = statePlayer.getPlayerEngine();

		// If selected engine is not available then try default engine or
		// another one
		if (!ArrayUtils.contains(engineNames, selectedEngine)) {
			Logger.info(selectedEngine, " is not availaible");
			selectedEngine = selectDefaultEngine(availableEngines);
		}

		for (AbstractPlayerEngine engine : availableEngines) {
			if (engine.getEngineName().equals(selectedEngine)) {
				return engine;
			}
		}

		return null;
	}

	/**
	 * @param availableEngines
	 * @return
	 */
	private String selectDefaultEngine(
			List<AbstractPlayerEngine> availableEngines) {
		String selectedEngine;
		if (ArrayUtils.contains(engineNames, Constants.DEFAULT_ENGINE)) {
			selectedEngine = Constants.DEFAULT_ENGINE;
		} else {
			// If default engine is not available, then get the first engine of
			// map returned
			selectedEngine = availableEngines.iterator().next().getEngineName();
		}
		// Update application state with this engine
		statePlayer.setPlayerEngine(selectedEngine);
		return selectedEngine;
	}

	/**
	 * @param availableEngines
	 * @return
	 */
	private String[] getEngineNames(List<AbstractPlayerEngine> availableEngines) {
		String[] availableEngineNames = new String[availableEngines.size()];
		for (int i = 0; i < availableEngines.size(); i++) {
			availableEngineNames[i] = availableEngines.get(i).getEngineName();
		}
		return availableEngineNames;
	}

	/**
	 * @return
	 */
	private List<AbstractPlayerEngine> getAvailableEngines() {
		// Remove unsupported engines
		// To do that create a clone of list to be able to remove from
		List<AbstractPlayerEngine> availableEngines = new ArrayList<AbstractPlayerEngine>(
				engines);
		Iterator<AbstractPlayerEngine> it = availableEngines.iterator();
		while (it.hasNext()) {
			AbstractPlayerEngine engine = it.next();
			// Engines must be supported for given OS and available
			if (!osManager.isPlayerEngineSupported(engine)
					|| !engine.isEngineAvailable()) {
				it.remove();
			}
		}
		return availableEngines;
	}
}
