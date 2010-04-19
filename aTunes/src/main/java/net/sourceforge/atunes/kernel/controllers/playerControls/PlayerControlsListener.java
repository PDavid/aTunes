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

package net.sourceforge.atunes.kernel.controllers.playerControls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;

import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

/**
 * The listener interface for receiving playerControls events.
 */
public final class PlayerControlsListener extends MouseAdapter {

    private PlayerControlsPanel panel;

    /**
     * Instantiates a new player controls listener.
     * 
     * @param panel
     *            the panel
     * @param controller
     *            the controller
     */
    protected PlayerControlsListener(PlayerControlsPanel panel, PlayerControlsController controller) {
        this.panel = panel;
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.getSource().equals(panel.getProgressBar()) && panel.getProgressBar().isEnabled()) {
            // Progress bar width is greater than real slider width so calculate value assuming 5 pixels in both left and right of track 
            int value = (panel.getProgressBar().getMaximum() * (e.getX() - 5)) / (panel.getProgressBar().getWidth() - 10);
            // Force new value to avoid jump to next major tick
            panel.getProgressBar().setValue(value);
            // Calculate percent
            double perCent = (double) value / ((JSlider) e.getSource()).getMaximum();
            PlayerHandler.getInstance().seekCurrentAudioObject(perCent);
        }
    }
}
