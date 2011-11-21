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
import java.awt.EventQueue;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.PauseImageIcon;
import net.sourceforge.atunes.gui.images.PlayImageIcon;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

public final class PlayPauseButton extends JButton implements ILookAndFeelChangeListener {

    private static final long serialVersionUID = 4348041346542204394L;

    private boolean playing;
    
    private Dimension size;

	private ILookAndFeel lookAndFeel;
	
    /**
     * Instantiates a new play pause button.
     * 
     * @param size
     * @param lookAndFeelManager
     */
    public PlayPauseButton(Dimension size, ILookAndFeelManager lookAndFeelManager) {
        super(Context.getBean("playAction", Action.class));
        // Force size of button
        this.size = size;
        this.lookAndFeel = lookAndFeelManager.getCurrentLookAndFeel();
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        setText(null);

        setIcon();
        
        lookAndFeel.putClientProperties(this);
        lookAndFeelManager.addLookAndFeelChangeListener(this);
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
        this.playing = playing;
    	setIcon();
    }

    /**
     * Checks if is playing.
     * 
     * @return true, if is playing
     */
    public boolean isPlaying() {
        return playing;
    }
    
    @Override
    public void lookAndFeelChanged() {
    	setIcon();
    }
    
    private void setIcon() {
    	if (playing) {
    		PauseImageIcon icon = Context.getBean("pauseIcon", PauseImageIcon.class);
    		icon.setSize(size);
    		setIcon(icon.getIcon(lookAndFeel.getPaintForSpecialControls()));
    	} else {
    		PlayImageIcon icon = Context.getBean("playIcon", PlayImageIcon.class);
    		icon.setSize(size);
    		setIcon(icon.getIcon(lookAndFeel.getPaintForSpecialControls()));
    	}
    }

}
