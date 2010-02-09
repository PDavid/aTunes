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
package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

/*
 * based on code from Xtreme Media Player
 */
public final class PlayPauseButton extends JButton {

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
        super(Images.getImage(Images.PLAY));
        // Force size of button
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);

        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);

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
    public void setPlaying(final boolean playing) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setPlayingState(playing);
                }
            });
        } else {
            setPlayingState(playing);
        }
    }

    private void setPlayingState(boolean playing) {
        if (playing) {
            setIcon(Images.getImage(Images.PAUSE));
        } else {
            setIcon(Images.getImage(Images.PLAY));
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
