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

import java.util.List;
import java.util.concurrent.CancellationException;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IContextPanelContent;
import net.sourceforge.atunes.utils.Logger;

/**
 * This class implements a special worker used to retrieve data from a
 * ContextInformationDataSource and show it in a ContextPanelContent
 * 
 * @author alex
 * 
 */
public class ContextInformationBackgroundWorker extends
		BackgroundWorker<Void, Void> {

	/**
	 * The context panel content where information must be shown after
	 * retrieving data
	 */
	private IContextPanelContent<IContextInformationSource> content;

	/**
	 * The context information data source used to retrieve information
	 */
	private IContextInformationSource dataSource;

	/**
	 * audio object
	 */
	private IAudioObject audioObject;

	/**
	 * @param content
	 */
	public void setContent(
			final IContextPanelContent<IContextInformationSource> content) {
		this.content = content;
	}

	/**
	 * @param dataSource
	 */
	public void setDataSource(final IContextInformationSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @param audioObject
	 */
	public void setAudioObject(final IAudioObject audioObject) {
		this.audioObject = audioObject;
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Void doInBackground() {
		this.dataSource.getData(this.audioObject);
		return null;
	}

	@Override
	protected void done(final Void result) {
		try {
			this.content.updateContentFromDataSource(this.dataSource);
			if (!isCancelled()) {
				// Enable task pane so user can expand or collapse
				this.content.getParentPanel().setEnabled(true);
				// After update data expand content
				this.content.getParentPanel().setVisible(true);
			}
		} catch (CancellationException e) {
			// thrown when cancelled
			Logger.error(e);
		}
	}

	/**
	 * Cancels data retrieve
	 */
	void cancel() {
		cancel(true);
		this.dataSource.cancel();
	}
}
