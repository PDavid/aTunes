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

package net.sourceforge.atunes.gui;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.utils.Logger;

/**
 * Implementation of a IBackgroundWorker using Swing
 * @author alex
 *
 */
public class SwingBackgroundWorker implements IBackgroundWorker {

	private Runnable backgroundActions;
	
	private Runnable graphicalActionsWhenDone;

	@Override
	public void setBackgroundActions(Runnable backgroundActions) {
		this.backgroundActions = backgroundActions;
	}
	
	@Override
	public void setGraphicalActionsWhenDone(Runnable graphicalActionsWhenDone) {
		this.graphicalActionsWhenDone = graphicalActionsWhenDone;
	}
	
	@Override
	public void execute() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			
			protected Void doInBackground() {
				Logger.debug("Running background actions");
				backgroundActions.run();
				return null;
			}
			
			protected void done() {
				try {
					get();
				} catch (InterruptedException e) {
					Logger.error(e);
				} catch (ExecutionException e) {
					Logger.error(e);
				}
				graphicalActionsWhenDone.run();
				Logger.debug("Running finish actions completed");
			}
		};
		worker.execute();
	}
}
