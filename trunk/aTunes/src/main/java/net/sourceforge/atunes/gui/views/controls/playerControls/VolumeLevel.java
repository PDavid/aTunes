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
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public final class VolumeLevel extends JLabel {

    private static final long serialVersionUID = 7166046387576859994L;

    private Timer timer;
    
    public VolumeLevel() {
        super();
        setHorizontalAlignment(SwingConstants.CENTER);
        setPreferredSize(new Dimension(50, 20));
        setFocusable(false);
    }
    
    @Override
    public void setText(String text) {
    	super.setText(text);
    	
    	// Keep text (volume level) visible for two seconds
        if (timer != null) {
        	timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setText("");
					}
				});
			}
		}, 2000);
    }    
}
