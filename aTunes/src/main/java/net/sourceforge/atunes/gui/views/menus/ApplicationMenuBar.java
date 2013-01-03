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

package net.sourceforge.atunes.gui.views.menus;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import net.sourceforge.atunes.model.IMenuBar;

/**
 * The application menu bar.
 */
/**
 * @author alex
 *
 */
public final class ApplicationMenuBar extends JMenuBar implements IMenuBar {

    private static final long serialVersionUID = 234977404080329591L;

    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu viewMenu;
    private JMenu playerMenu;
    private PlayListMenu playListMenu;
    private JMenu toolsMenu;
    private JMenu deviceMenu;
    private JMenu helpMenu;
    
    /**
     * @param playerMenu
     */
    public void setPlayerMenu(JMenu playerMenu) {
		this.playerMenu = playerMenu;
	}
    
    /**
     * @param fileMenu
     */
    public void setFileMenu(JMenu fileMenu) {
		this.fileMenu = fileMenu;
	}
    
    /**
     * @param editMenu
     */
    public void setEditMenu(JMenu editMenu) {
		this.editMenu = editMenu;
	}
    
    /**
     * @param viewMenu
     */
    public void setViewMenu(JMenu viewMenu) {
		this.viewMenu = viewMenu;
	}
    
    /**
     * @param toolsMenu
     */
    public void setToolsMenu(JMenu toolsMenu) {
		this.toolsMenu = toolsMenu;
	}
    
    /**
     * @param deviceMenu
     */
    public void setDeviceMenu(JMenu deviceMenu) {
		this.deviceMenu = deviceMenu;
	}
    
    /* (non-Javadoc)
     * @see javax.swing.JMenuBar#setHelpMenu(javax.swing.JMenu)
     */
    public void setHelpMenu(JMenu helpMenu) {
		this.helpMenu = helpMenu;
	}
    
    /**
     * @param playListMenu
     */
    public void setPlayListMenu(PlayListMenu playListMenu) {
		this.playListMenu = playListMenu;
	}
    
    /**
     * Adds the menus.
     */
    @Override
    public void initialize() {
        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(playerMenu);
        playListMenu.initialize();
        add(playListMenu);
        add(deviceMenu);
        add(toolsMenu);
        add(helpMenu);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.menus.IMenuBar#addMenu(javax.swing.JMenu)
	 */
    @Override
	public void addMenu(JMenu newMenu) {
        remove(getComponentCount() - 1);
        add(newMenu);
        add(helpMenu);
    }
    
    @Override
    public JMenuBar getSwingComponent() {
    	return this;
    }
}
