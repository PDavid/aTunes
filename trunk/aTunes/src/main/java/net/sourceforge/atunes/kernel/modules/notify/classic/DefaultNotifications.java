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

package net.sourceforge.atunes.kernel.modules.notify.classic;

import javax.swing.JDialog;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.dialogs.OSDDialog;
import net.sourceforge.atunes.kernel.modules.notify.CommonNotificationEngine;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class DefaultNotifications extends CommonNotificationEngine {

	/**
	 * OSD controller
	 */
	private OSDDialogController osdDialogController;
	
	/**
	 * OSD Dialog
	 */
	private OSDDialog osdDialog;
	
    /**
     * Gets the oSD dialog controller.
     * 
     * @return the oSD dialog controller
     */
    private OSDDialogController getOSDDialogController() {
        if (osdDialogController == null) {
            JDialog.setDefaultLookAndFeelDecorated(false);
            osdDialog = new OSDDialog(ApplicationState.getInstance().getOsdWidth());
            JDialog.setDefaultLookAndFeelDecorated(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isDialogUndecorated());
            osdDialogController = new OSDDialogController(osdDialog);
        }
        return osdDialogController;
    }

    @Override
    public void updateNotification(ApplicationState newState) {
    	osdDialog.setWidth(newState.getOsdWidth());
    }
	
    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public void showNotification(AudioObject audioObject) {
    	getOSDDialogController().showOSD(audioObject);
    }
    
    @Override
    public void disposeNotifications() {
    }
    
    @Override
    public boolean testEngineAvailable() {
    	return true; // Always available
    }
    
    @Override
    public String getDescription() {
    	return I18nUtils.getString("NOTIFICATION_ENGINE_DEFAULT_DESCRIPTION");
    }
    
    @Override
    public String getUrl() {
    	return null;
    }
}
