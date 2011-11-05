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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.utils.I18nUtils;

public class FillDeviceWithRandomSongsAction extends CustomAbstractAction {

    private static final long serialVersionUID = -201250351035880261L;

    FillDeviceWithRandomSongsAction() {
        super(I18nUtils.getString("FILL_DEVICE_WITH_RANDOM_SONGS"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("FILL_DEVICE_WITH_RANDOM_SONGS"));
    }

    private String freeMemory;
    
    private IInputDialog inputDialog;
    
    private IDeviceHandler deviceHandler; 
    
    private IErrorDialogFactory errorDialogFactory;
    
    private IFrame frame;
    
    @Override
    protected void executeAction() {
        // Ask how much memory should be left free
    	inputDialog.setTitle(I18nUtils.getString("MEMORY_TO_LEAVE_FREE"));
    	inputDialog.showDialog(freeMemory);
        freeMemory = inputDialog.getResult();
        try {
        	deviceHandler.fillWithRandomSongs(Long.parseLong(freeMemory.trim()));
        } catch (NumberFormatException e) {
            // User did not enter numerical value. Show error dialog
        	errorDialogFactory.getDialog().showErrorDialog(frame, I18nUtils.getString("ERROR_NO_NUMERICAL_VALUE"));
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return deviceHandler.isDeviceConnected();
    }

	/**
	 * @param freeMemory the freeMemory to set
	 */
	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	/**
	 * @param inputDialog the inputDialog to set
	 */
	public void setInputDialog(IInputDialog inputDialog) {
		this.inputDialog = inputDialog;
	}

	/**
	 * @param deviceHandler the deviceHandler to set
	 */
	public void setDeviceHandler(IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	/**
	 * @param errorDialogerrorDialogFactory the errorDialogFactory to set
	 */
	public void setErrorDialogFactory(IErrorDialogFactory errorDialogFactory) {
		this.errorDialogFactory = errorDialogFactory;
	}

	/**
	 * @param frame the frame to set
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
}
