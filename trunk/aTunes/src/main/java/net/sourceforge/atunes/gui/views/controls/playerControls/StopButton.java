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

import javax.swing.JButton;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.StopImageIcon;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * The Class StopButton.
 */
public final class StopButton extends JButton implements ILookAndFeelChangeListener{

    private static final long serialVersionUID = 6007885049773560874L;

    private Dimension size;
    
    private ILookAndFeel lookAndFeel;
    
    /**
     * Instantiates a new stop button.
     * 
     * @param size
     * @param lookAndFeelManager
     */
    public StopButton(Dimension size, ILookAndFeelManager lookAndFeelManager) {
        super(Context.getBean(StopCurrentAudioObjectAction.class));
        // Force size
        this.size = size;
        this.lookAndFeel = lookAndFeelManager.getCurrentLookAndFeel();
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        setText(null);
        setIcon(StopImageIcon.getIcon(size, lookAndFeel));
        lookAndFeel.putClientProperties(this);
        lookAndFeelManager.addLookAndFeelChangeListener(this);
    }
    
    @Override
    public void lookAndFeelChanged() {
        setIcon(StopImageIcon.getIcon(size, lookAndFeel));
    }
}
