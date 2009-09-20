/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.menus.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;

/**
 * The Interface Frame.
 */
public interface Frame {

    /**
     * Creates the.
     */
    public void create();

    /**
     * Gets the app menu bar.
     * 
     * @return the app menu bar
     */
    public ApplicationMenuBar getAppMenuBar();

    /**
     * Gets the context information panel.
     * 
     * @return the context information panel
     */
    public ContextPanel getContextPanel();

    /**
     * Gets the extended state.
     * 
     * @return the extended state
     */
    public int getExtendedState();

    /**
     * Gets the frame.
     * 
     * @return the frame
     */
    public JFrame getFrame();

    /**
     * Gets the location.
     * 
     * @return the location
     */
    public Point getLocation();

    /**
     * Gets the navigation panel.
     * 
     * @return the navigation panel
     */
    public NavigationPanel getNavigationPanel();

    /**
     * Gets the player controls.
     * 
     * @return the player controls
     */
    public PlayerControlsPanel getPlayerControls();

    /**
     * Gets the play list panel.
     * 
     * @return the play list panel
     */
    public PlayListPanel getPlayListPanel();

    /**
     * Gets the play list table.
     * 
     * @return the play list table
     */
    public PlayListTable getPlayListTable();

    /**
     * Gets the properties panel.
     * 
     * @return the properties panel
     */
    public AudioObjectPropertiesPanel getPropertiesPanel();

    /**
     * Gets the size.
     * 
     * @return the size
     */
    public Dimension getSize();

    /**
     * Gets the tool bar.
     * 
     * @return the tool bar
     */
    public ToolBar getToolBar();
    
    /**
     * Gets the progress bar
     * @return
     */
    public JProgressBar getProgressBar();

    /**
     * Checks if is visible.
     * 
     * @return true, if is visible
     */
    public boolean isVisible();

    /**
     * Sets the center status bar.
     * 
     * @param text
     *            the text
     * @param toolTip
     *            the tool tip
     */
    public void setCenterStatusBar(String text, String toolTip);

    /**
     * Sets the default close operation.
     * 
     * @param op
     *            the new default close operation
     */
    public void setDefaultCloseOperation(int op);

    /**
     * Sets the extended state.
     * 
     * @param state
     *            the new extended state
     */
    public void setExtendedState(int state);

    /**
     * Sets the left status bar text.
     * 
     * @param text
     *            the text
     * @param toolTip
     *            the tool tip
     */
    public void setLeftStatusBarText(String text, String toolTip);

    /**
     * Sets the location.
     * 
     * @param location
     *            the new location
     */
    public void setLocation(Point location);

    /**
     * Sets the location relative to.
     * 
     * @param c
     *            the new location relative to
     */
    public void setLocationRelativeTo(Component c);

    /**
     * Sets the right status bar.
     * 
     * @param text
     *            the text
     * @param toolTip
     *            the tool tip
     */
    public void setRightStatusBar(String text, String toolTip);

    /**
     * Sets the status bar image label text.
     * 
     * @param icon
     *            the icon
     * @param text
     *            the text
     */
    public void setStatusBarDeviceLabelText(String text);

    /**
     * Sets the title.
     * 
     * @param title
     *            the new title
     */
    public void setTitle(String title);

    /**
     * Sets the visible.
     * 
     * @param visible
     *            the new visible
     */
    public void setVisible(boolean visible);

    /**
     * Show audio scrobbler panel.
     * 
     * @param show
     *            the show
     * @param changeSize
     *            the change size
     */
    public void showContextPanel(boolean show, boolean changeSize);

    /**
     * Show navigation panel.
     * 
     * @param show
     *            the show
     * @param changeSize
     *            the change size
     */
    public void showNavigationPanel(boolean show, boolean changeSize);

    /**
     * Show navigation table.
     * 
     * @param show
     *            the show
     */
    public void showNavigationTable(boolean show);

    /**
     * Show song properties.
     * 
     * @param show
     *            the show
     */
    public void showSongProperties(boolean show);

    /**
     * Show status bar.
     * 
     * @param show
     *            the show
     */
    public void showStatusBar(boolean show);

    /**
     * Show tool bar.
     * 
     * @param show
     */
    public void showToolBar(boolean show);

    /**
     * Show device info.
     * 
     * @param show
     *            the visible
     */
    public void showDeviceInfo(boolean show);

    /**
     * Show new podcast feed entries info.
     * 
     * @param show
     */
    public void showNewPodcastFeedEntriesInfo(boolean show);

    /**
     * Show new version info.
     * 
     * @param show
     * @param version
     */
    public void showNewVersionInfo(boolean show, ApplicationVersion version);

}
