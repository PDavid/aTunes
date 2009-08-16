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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.substance.LeftConcaveButtonShaper;
import net.sourceforge.atunes.gui.substance.RightConcaveButtonShaper;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.utils.GuiUtils;

import org.jvnet.substance.SubstanceLookAndFeel;

/*
 * based on code from Xtreme Media Player
 */
/**
 * The Class NextButton.
 */
public class NextButton extends JButton {

    private static final long serialVersionUID = -4939372038840047335L;

    /**
     * Instantiates a new next button.
     * 
     * @param size
     */
    public NextButton(Dimension size) {
        super(ImageLoader.NEXT);
        // Force size
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        putClientProperty(SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, GuiUtils.getComponentOrientation().isLeftToRight() ? new LeftConcaveButtonShaper(
                PlayerControlsPanel.PLAY_BUTTON_SIZE.height) : new RightConcaveButtonShaper(PlayerControlsPanel.PLAY_BUTTON_SIZE.height));
        
        // Add behaviour
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerHandler.getInstance().playNextAudioObject(false);
            } 
        });
    }
}
