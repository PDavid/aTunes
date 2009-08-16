/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.controllers.playerControls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;

import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.audio.CueTrack;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The listener interface for receiving playerControls events.
 */
public class PlayerControlsListener extends MouseAdapter {

    private PlayerControlsPanel panel;

    //private PlayerControlsController controller;

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
        //this.controller = controller;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource().equals(panel.getProgressBar()) && panel.getProgressBar().isEnabled()) {
            //int value = ((JSlider)e.getSource()).getValue();
            //double perCent = (double) value / ((JSlider)e.getSource()).getMaximum();
            //double perCentOfSong = value * 1000000 / duration;
            int widthClicked;
            if (GuiUtils.getComponentOrientation().isLeftToRight()) {
                widthClicked = e.getPoint().x;
            } else {
                widthClicked = ((JSlider) e.getSource()).getWidth() - e.getPoint().x;
            }
            int widthOfProgressBar = panel.getProgressBar().getSize().width;
            double perCent = (double) widthClicked / (double) widthOfProgressBar;

            AudioObject audioObject = PlayerHandler.getInstance().getAudioObject();
            if (audioObject == null) {
                return;
            }

            // If it is a cue track, then adjust the percent
            if (AudioFile.isCueFile(((AudioFile) audioObject).getFile())) {
                int startPosition = ((CueTrack) audioObject).getTrackStartPositionAsInt();
                float duration = ((CueTrack) audioObject).getDuration();
                long trackDuration = ((CueTrack) audioObject).getTotalDuration();
                perCent = (startPosition + perCent * duration) / trackDuration;
            }
            PlayerHandler.getInstance().seekCurrentAudioObject(perCent);
        }
    }
}
