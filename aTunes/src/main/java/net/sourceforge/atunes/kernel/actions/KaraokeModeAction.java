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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action enables or disables karaoke mode
 * 
 * @author fleax
 * 
 */
public class KaraokeModeAction extends AbstractAction {

    private static final long serialVersionUID = 1614471688413649087L;

    private Timer timer;

    KaraokeModeAction() {
        super(I18nUtils.getString("KARAOKE"), Images.getImage(Images.KARAOKE));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("KARAOKE"));
        putValue(SELECTED_KEY, ApplicationState.getInstance().isKaraoke());

        timer = new Timer(1000, new ActionListener() {
            private boolean showWarning;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (showWarning) {
                    putValue(SMALL_ICON, Images.getImage(Images.KARAOKE));
                } else {
                    putValue(SMALL_ICON, Images.getImage(Images.WARNING));
                }
                showWarning = !showWarning;
            }
        });
        if (ApplicationState.getInstance().isKaraoke()) {
            timer.start();
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ApplicationState.getInstance().setKaraoke((Boolean) getValue(SELECTED_KEY));
        if (timer.isRunning()) {
            timer.stop();
            putValue(SMALL_ICON, Images.getImage(Images.KARAOKE));
        } else {
            timer.start();
        }
    }

}
