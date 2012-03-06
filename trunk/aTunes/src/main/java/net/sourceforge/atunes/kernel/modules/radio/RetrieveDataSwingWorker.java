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

package net.sourceforge.atunes.kernel.modules.radio;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.RadioBrowserTreeTableModel;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.utils.Logger;

final class RetrieveDataSwingWorker extends SwingWorker<List<IRadio>, Void> {
	
	private IRadioHandler radioHandler;
	
	private RadioBrowserDialog radioBrowserDialog;
	
	/**
	 * @param radioHandler
	 * @param radioBrowserDialog
	 */
	public RetrieveDataSwingWorker(IRadioHandler radioHandler, RadioBrowserDialog radioBrowserDialog) {
		this.radioHandler = radioHandler;
		this.radioBrowserDialog = radioBrowserDialog;
	}
	
	@Override
	protected List<IRadio> doInBackground() throws IOException {
	    return radioHandler.retrieveRadiosForBrowser();
	}

	@Override
	protected void done() {
	    try {
	        List<IRadio> radios = get();
	        radioBrowserDialog.getTreeTable().setTreeTableModel(new RadioBrowserTreeTableModel(radios));
	    } catch (InterruptedException e) {
	        Logger.error(e);
	    } catch (ExecutionException e) {
	        Logger.error(e);
	    }
	}
}