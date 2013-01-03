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

package net.sourceforge.atunes.kernel.modules.covernavigator;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.utils.Logger;

final class GetCoversProcessListener implements IProcessListener<Void> {

	private final CoverNavigatorController controller;

	/**
	 * @param controller
	 */
	public GetCoversProcessListener(final CoverNavigatorController controller) {
		this.controller = controller;
	}

	@Override
	public void processCanceled() {
		update();
	}

	@Override
	public void processFinished(final boolean ok, final Void result) {
		update();
	}

	/**
	 * Called to update covers
	 */
	private void update() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					GetCoversProcessListener.this.controller.updateCovers();
				}
			});
		} catch (InvocationTargetException e) {
			Logger.error(e);
		} catch (InterruptedException e) {
			Logger.error(e);
		}
	}
}