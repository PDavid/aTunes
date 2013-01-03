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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import javax.swing.JDialog;

import net.sourceforge.atunes.model.ILookAndFeelManager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Creates full screen window
 * 
 * @author alex
 * 
 */
public class FullScreenWindowFactory implements ApplicationContextAware {

    private ILookAndFeelManager lookAndFeelManager;

    private ApplicationContext context;

    @Override
    public void setApplicationContext(
	    final ApplicationContext applicationContext) {
	this.context = applicationContext;
    }

    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(
	    final ILookAndFeelManager lookAndFeelManager) {
	this.lookAndFeelManager = lookAndFeelManager;
    }

    /**
     * Creates a new full screen window
     * 
     * @return
     */
    public FullScreenWindow getFullScreenWindow() {
	JDialog.setDefaultLookAndFeelDecorated(false);
	FullScreenWindow window = context.getBean(FullScreenWindow.class);
	JDialog.setDefaultLookAndFeelDecorated(lookAndFeelManager
		.getCurrentLookAndFeel().isDialogUndecorated());
	return window;
    }
}
