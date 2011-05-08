/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JButton;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;

public final class NextButton extends JButton {

    private static final long serialVersionUID = -4939372038840047335L;

    private Dimension size;
    
    private Polygon nextShape;

    /**
     * Instantiates a new next button.
     * 
     * @param size
     */
    public NextButton(Dimension size) {
        super(Actions.getAction(PlayNextAudioObjectAction.class));
        // Force size
        this.size = size;
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        setText(null);

        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);
        
        nextShape = new Polygon();
        nextShape.addPoint(- this.size.width / 5, - this.size.height / 6);
        nextShape.addPoint(- this.size.width / 5, this.size.height / 6);
        nextShape.addPoint(0,  0);        
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	Graphics2D g2 = (Graphics2D) g;
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g2.setPaint(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintFor(this));
		g2.translate(this.size.getWidth() / 2 + this.size.width / 16, this.size.getHeight() / 2);
    	
   		g2.fill(nextShape);
   		g2.translate(this.size.width / 5, 0);
   		g2.fill(nextShape);
    	g2.dispose();
    }

}
