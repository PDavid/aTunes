/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Enables or disables mute
 * 
 * @author fleax
 * 
 */
public class MuteAction extends Action {

    private static final long serialVersionUID = 306200192652324065L;

    MuteAction() {
        super(I18nUtils.getString("MUTE"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("MUTE"));
        putValue(SELECTED_KEY, ApplicationState.getInstance().isMuteEnabled());
        updateIcon();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // If this action is called from aTunes code (action is not fired in EDT) then switch the value of mute
        if (e == null) {
            putValue(SELECTED_KEY, !(Boolean) getValue(SELECTED_KEY));
        }
        ApplicationState.getInstance().setMuteEnabled((Boolean) getValue(SELECTED_KEY));
        PlayerHandler.getInstance().applyMuteState(ApplicationState.getInstance().isMuteEnabled());
        updateIcon();
    }

    /**
     * Updates icon of mute
     */
    public void updateIcon() {
        if (ApplicationState.getInstance().isMuteEnabled()) {
            putValue(SMALL_ICON, ImageLoader.getImage(ImageLoader.VOLUME_MUTE));
        } else {
            int volume = ApplicationState.getInstance().getVolume();
            if (volume > 80) {
                putValue(SMALL_ICON, ImageLoader.getImage(ImageLoader.VOlUME_MAX));
            } else if (volume > 40) {
                putValue(SMALL_ICON, ImageLoader.getImage(ImageLoader.VOLUME_MED));
            } else if (volume > 5) {
                putValue(SMALL_ICON, ImageLoader.getImage(ImageLoader.VOLUME_MIN));
            } else {
                putValue(SMALL_ICON, ImageLoader.getImage(ImageLoader.VOLUME_ZERO));
            }
        }
    }
}
