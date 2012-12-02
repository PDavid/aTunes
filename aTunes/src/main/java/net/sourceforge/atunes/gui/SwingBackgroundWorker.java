/*
 * aTunes 3.0.0
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

import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IBackgroundWorker;

/**
 * Implementation of a IBackgroundWorker using Swing
 * @author alex
 * @param <T>
 */
public class SwingBackgroundWorker<T> implements IBackgroundWorker<T> {

	private Callable<T> backgroundActions;

	private Runnable graphicalActionsBeforeStart;

	private IActionsWithBackgroundResult<T> graphicalActionsWhenDone;

	private BackgroundSwingWorker<T> backgroundSwingWorker;

	@Override
	public void setBackgroundActions(final Callable<T> backgroundActions) {
		this.backgroundActions = backgroundActions;
	}

	@Override
	public void setActionsBeforeBackgroundStarts(final Runnable afterStartActions) {
		this.graphicalActionsBeforeStart = afterStartActions;
	}

	@Override
	public void setActionsWhenDone(final IActionsWithBackgroundResult<T> graphicalActionsWhenDone) {
		this.graphicalActionsWhenDone = graphicalActionsWhenDone;
	}

	@Override
	public void execute() {
		backgroundSwingWorker = new BackgroundSwingWorker<T>(graphicalActionsBeforeStart, backgroundActions, graphicalActionsWhenDone);
		backgroundSwingWorker.execute();
	}

	@Override
	public boolean isDone() {
		return backgroundSwingWorker != null ? backgroundSwingWorker.isDone() : false;
	}
}
