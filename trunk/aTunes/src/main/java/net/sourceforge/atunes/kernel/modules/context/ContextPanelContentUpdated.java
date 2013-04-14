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

package net.sourceforge.atunes.kernel.modules.context;

import net.sourceforge.atunes.model.IBackgroundWorkerCallback;

/**
 * Class to control when a content finishes its update
 * 
 * @author alex
 * 
 */
public class ContextPanelContentUpdated implements
		IBackgroundWorkerCallback<Void> {

	private final int totalContents;

	private int contentsUpdated;

	private final Runnable allContentsUpdatedCallback;

	/**
	 * @param totalContents
	 * @param allContentsUpdatedCallback
	 */
	public ContextPanelContentUpdated(final int totalContents,
			final Runnable allContentsUpdatedCallback) {
		this.totalContents = totalContents;
		this.allContentsUpdatedCallback = allContentsUpdatedCallback;
	}

	@Override
	public void workerFinished(final Void result) {
		this.contentsUpdated++;
		if (this.contentsUpdated == this.totalContents) {
			// all contents updated -> callback
			this.allContentsUpdatedCallback.run();
		}
	}

}
