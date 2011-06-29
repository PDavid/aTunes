/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.model.RadioBrowserTreeTableModel;
import net.sourceforge.atunes.gui.views.dialogs.RadioBrowserDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.misc.log.Logger;

final class RadioBrowserDialogController extends AbstractSimpleController<RadioBrowserDialog> {

    private final class RetrieveDataSwingWorker extends
			SwingWorker<List<Radio>, Void> {
		@Override
		protected List<Radio> doInBackground() throws Exception {
		    return RadioHandler.getInstance().retrieveRadiosForBrowser();
		}

		@Override
		protected void done() {
		    try {
		        List<Radio> radios = get();
		        getComponentControlled().getTreeTable().setTreeTableModel(new RadioBrowserTreeTableModel(radios));
		    } catch (InterruptedException e) {
		        Logger.error(e);
		    } catch (ExecutionException e) {
		        Logger.error(e);
		    } finally {
		        //getFrameControlled().setCursor(Cursor.getDefaultCursor());
		    }
		}
	}

	/**
     * Instantiates a new radio browser dialog controller.
     * 
     * @param frameControlled
     *            the frame controlled
     */
    RadioBrowserDialogController(RadioBrowserDialog frameControlled) {
        super(frameControlled);
        addBindings();
        addStateBindings();
    }

    /**
     * Show radio browser.
     */
    void showRadioBrowser() {
        retrieveData();
        getComponentControlled().setVisible(true);
    }

    /**
     * Retrieve data.
     */
    void retrieveData() {
        //getFrameControlled().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new RetrieveDataSwingWorker().execute();
    }

    @Override
    protected void addBindings() {
        RadioBrowserDialogListener listener = new RadioBrowserDialogListener(getComponentControlled());
        getComponentControlled().getTreeTable().addMouseListener(listener);
    }

    @Override
    protected void addStateBindings() {
        //getFrameControlled().getTreeTable().setRowSorter(new TableRowSorter<RadioBrowserTreeTableModel>(model));
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

}
