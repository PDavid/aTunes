/*
 * aTunes 2.0.0-SNAPSHOT
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
import net.sourceforge.atunes.gui.substance.CircleButtonShaper;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

import org.jvnet.substance.SubstanceLookAndFeel;

/*
 * based on code from Xtreme Media Player
 */
/**
 * The Class PlayPauseButton.
 */
public class PlayPauseButton extends JButton {

    private static final long serialVersionUID = 4348041346542204394L;

    /** The playing. */
    private boolean playing;

    /**
     * Instantiates a new play pause button.
     * 
     * @param width
     * @param height
     */
    public PlayPauseButton(Dimension size) {
        super(ImageLoader.getImage(ImageLoader.PLAY));
        // Force size of button
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        putClientProperty(SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new CircleButtonShaper());

        // Add behaviour
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerHandler.getInstance().playCurrentAudioObject(true);
            }
        });
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    public void setPlaying(boolean playing) {
        if (playing) {
            setIcon(ImageLoader.getImage(ImageLoader.PAUSE));
        } else {
            setIcon(ImageLoader.getImage(ImageLoader.PLAY));
        }
        this.playing = playing;
    }

    /**
     * Checks if is playing.
     * 
     * @return true, if is playing
     */
    public boolean isPlaying() {
        return playing;
    }

}
