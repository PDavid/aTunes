/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

public class NormalizeModeAction extends AbstractAction {

    private static final long serialVersionUID = 6993968558006979367L;

    private Timer timer;

    public NormalizeModeAction() {
        super(I18nUtils.getString("NORMALIZE"), Images.getImage(Images.NORMALIZATION));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("NORMALIZE"));
        putValue(SELECTED_KEY, ApplicationState.getInstance().isUseNormalisation());

        timer = new Timer(1000, new ActionListener() {
            boolean showWarning;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (showWarning) {
                    putValue(SMALL_ICON, Images.getImage(Images.NORMALIZATION));
                } else {
                    putValue(SMALL_ICON, Images.getImage(Images.WARNING));
                }
                showWarning = !showWarning;
            }
        });
        if (ApplicationState.getInstance().isUseNormalisation()) {
            timer.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isNormalized = !ApplicationState.getInstance().isUseNormalisation();
        ApplicationState.getInstance().setUseNormalisation(isNormalized);
        // SystemTrayHandler.getInstance().setNormalize(isNormalized);
        PlayerHandler.getInstance().applyNormalization();
        if (timer.isRunning()) {
            timer.stop();
            putValue(SMALL_ICON, Images.getImage(Images.NORMALIZATION));
        } else {
            timer.start();
        }
    }

}
