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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.kernel.modules.player.Volume;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.GuiUtils;

public final class VolumeSlider extends JSlider {

    private static final long serialVersionUID = -7802263658163323018L;

    /**
     * Slider to control volume
     * @param state
     * @param playerHandler
     */
    public VolumeSlider(final IState state, final IPlayerHandler playerHandler) {
        super();
        setOpaque(false);
        setMinimum(0);
        setMaximum(100);
        setValue(50);
        setFocusable(false);
        setPreferredSize(new Dimension(GuiUtils.getComponentWidthForResolution(0.1f), 20));

        // Add behaviour
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                if (notches < 0) {
                    setValue(getValue() + 5);
                } else {
                    setValue(getValue() - 5);
                }

                Volume.setVolume(getValue(), state, playerHandler);
            }
        });

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
				Volume.setVolume(getValue(), state, playerHandler);
            }
        });
    }
}
