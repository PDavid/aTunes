/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.player;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

public class Volume {

    public static void setVolume(int volume) {
        if (volume < 0) {
            volume = 0;
        } else if (volume > 100) {
            volume = 100;
        }
        final int finalVolume = volume;
        ApplicationState.getInstance().setVolume(volume);
        PlayerHandler.getInstance().setVolume(volume);
        
        if (!EventQueue.isDispatchThread()) {
        	SwingUtilities.invokeLater(new Runnable() {
        		@Override
        		public void run() {
        	        ControllerProxy.getInstance().getPlayerControlsController().setVolume(finalVolume);
        	        GuiHandler.getInstance().getFullScreenWindow().setVolume(finalVolume);
        	        ((MuteAction) Actions.getAction(MuteAction.class)).updateIcon();
        		}
        	});
        } else {
        	ControllerProxy.getInstance().getPlayerControlsController().setVolume(finalVolume);
        	GuiHandler.getInstance().getFullScreenWindow().setVolume(finalVolume);
            ((MuteAction) Actions.getAction(MuteAction.class)).updateIcon();
        }

    }
}
