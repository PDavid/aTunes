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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.PlayAction;

public final class PlayPauseButton extends JButton {

    private static final long serialVersionUID = 4348041346542204394L;

    private boolean playing;
    
    private Dimension size;
    
    private Polygon playShape;
    
    private Rectangle pauseShape1;
    private Rectangle pauseShape2;
    
    /**
     * Instantiates a new play pause button.
     * 
     * @param width
     * @param height
     */
    public PlayPauseButton(Dimension size) {
        super(Actions.getAction(PlayAction.class));
        // Force size of button
        this.size = size;
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        setText(null);

        playShape = new Polygon();
        playShape.addPoint(- this.size.width / 5, - this.size.height / 4);
        playShape.addPoint(- this.size.width / 5, this.size.height / 4);
        playShape.addPoint(this.size.width / 6,  0);        
        
        pauseShape1 = new Rectangle(- this.size.width / 10 - this.size.width / 6, - this.size.height / 5, this.size.width / 7, (int) (this.size.height / (5f/2f)));
        pauseShape2 = new Rectangle(- this.size.width / 20, - this.size.height / 5, this.size.width / 7, (int) (this.size.height / (5f/2f)));
        
        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	Graphics2D g2 = (Graphics2D) g;
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g2.setPaint(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintFor(this));
		g2.translate(this.size.getWidth() * 4/7, this.size.getHeight() / 2);
    	
    	if (!playing) {
    		g2.fill(playShape);
    	} else {
    		g2.fill(pauseShape1);
    		g2.fill(pauseShape2);
    	}    	
    	
    	g2.dispose();
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
    	repaint();
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
