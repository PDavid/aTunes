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

package net.sourceforge.atunes.kernel.modules.radio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.RadioBrowserTreeTableModel;
import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.utils.Logger;

/**
 * Retrieves radios to show in browser
 * 
 * @author alex
 * 
 */
public final class RetrieveRadioBrowserDataBackgroundWorker extends
		BackgroundWorker<List<IRadio>> {

	private IRadioHandler radioHandler;

	private RadioBrowserDialog radioBrowserDialog;

	/**
	 * @param radioHandler
	 */
	public void setRadioHandler(final IRadioHandler radioHandler) {
		this.radioHandler = radioHandler;
	}

	/**
	 * @param dialog
	 */
	void retrieve(final RadioBrowserDialog dialog) {
		this.radioBrowserDialog = dialog;
		execute();
	}

	@Override
	protected void before() {
	}

	@Override
	protected List<IRadio> doInBackground() {
		try {
			return this.radioHandler.retrieveRadiosForBrowser();
		} catch (IOException e) {
			Logger.error(e);
		}
		return new ArrayList<IRadio>();
	}

	@Override
	protected void done(final List<IRadio> result) {
		this.radioBrowserDialog.getTreeTable().setTreeTableModel(
				new RadioBrowserTreeTableModel(result));
		this.radioBrowserDialog.setVisible(true);
	}
}