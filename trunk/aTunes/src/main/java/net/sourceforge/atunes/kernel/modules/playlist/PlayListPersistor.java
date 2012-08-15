/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.concurrent.Future;

import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.ITaskService;

/**
 * Responsible of call to persist methods
 * @author alex
 *
 */
public class PlayListPersistor {
	
	private ITaskService taskService;
	
    private Future<?> persistPlayListFuture;
    
	private IStateHandler stateHandler;
	
	/**
	 * @param taskService
	 */
	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
	
	/**
	 * @param stateHandler
	 */
	public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
	
    /**
     * Called when play lists needs to be persisted
     */
    void persistPlayLists(final IListOfPlayLists listOfPlayLists) {
		// Wait 5 seconds and persist play list 
		if (persistPlayListFuture != null) {
			persistPlayListFuture.cancel(false);
		}

		persistPlayListFuture = taskService.submitOnce("Persist PlayList", 2, new Runnable() {
			@Override
			public void run() {
				// Store play list definition
				stateHandler.persistPlayLists(listOfPlayLists);
			}
		});
    }
}
