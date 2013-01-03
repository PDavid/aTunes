/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.model;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

/**
 * A graphical component
 * 
 * @author alex
 * @param <T>
 */
public interface IComponent<T extends JComponent> {

    /**
     * Returns the underlying Swing component
     * 
     * @return
     */
    public T getSwingComponent();

    /**
     * Sets maximum size
     * 
     * @param size
     */
    public void setMaximumSize(Dimension size);

    /**
     * Sets minimum size
     * 
     * @param size
     */
    public void setMinimumSize(Dimension size);

    /**
     * Sets preferred size
     * 
     * @param size
     */
    public void setPreferredSize(Dimension size);

    /**
     * Sets visibility
     * 
     * @param visible
     */
    public void setVisible(boolean visible);

    /**
     * Returns if visible
     * 
     * @return
     */
    public boolean isVisible();

    /**
     * Enables or disables component
     * 
     * @param b
     */
    public void setEnabled(boolean b);

    /**
     * Returns if enabled
     * 
     * @return
     */
    public boolean isEnabled();

    /**
     * @param seekListener
     */
    public void addMouseListener(MouseListener seekListener);

    /**
     * @param listener
     */
    public void addMouseMotionListener(MouseMotionListener listener);

    /**
     * @param keyListener
     */
    public void addKeyListener(KeyListener keyListener);

    /**
     * @param b
     */
    public void setOpaque(boolean b);

    /**
     * @return
     */
    public Dimension getPreferredSize();

    /**
     * @return location on screen
     */
    Point getLocationOnScreen();

}
