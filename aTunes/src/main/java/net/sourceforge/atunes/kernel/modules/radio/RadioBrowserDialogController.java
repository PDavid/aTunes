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

package net.sourceforge.atunes.kernel.modules.radio;



import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IRadioHandler;

final class RadioBrowserDialogController extends AbstractSimpleController<RadioBrowserDialog> {

    private IRadioHandler radioHandler;
    
	/**
     * Instantiates a new radio browser dialog controller.
     * 
     * @param frameControlled
     * @param radioHandler
     */
    RadioBrowserDialogController(RadioBrowserDialog frameControlled, IRadioHandler radioHandler) {
        super(frameControlled);
        this.radioHandler = radioHandler;
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
        new RetrieveDataSwingWorker(radioHandler, getComponentControlled()).execute();
    }

    @Override
	public void addBindings() {
        RadioBrowserDialogListener listener = new RadioBrowserDialogListener(getComponentControlled(), radioHandler);
        getComponentControlled().getTreeTable().addMouseListener(listener);
    }
}
