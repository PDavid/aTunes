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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.springframework.context.ApplicationContext;

/**
 * The interface for all frames
 */
public interface IFrame {

	/**
	 * Creates the frame.
	 * 
	 * @param frameState
	 */
	void create(IFrameState frameState);

	/**
	 * Gets the app menu bar.
	 * 
	 * @return the app menu bar
	 */
	IMenuBar getAppMenuBar();

	/**
	 * Gets the extended state.
	 * 
	 * @return the extended state
	 */
	int getExtendedState();

	/**
	 * Gets the frame.
	 * 
	 * @return the frame
	 */
	JFrame getFrame();

	/**
	 * Returns the state of the frame.
	 * 
	 * @return the state of the frame
	 */
	IFrameState getFrameState();

	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	Point getLocation();

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	Dimension getSize();

	/**
	 * Gets the progress bar
	 * 
	 * @return
	 */
	JProgressBar getProgressBar();

	/**
	 * Checks if is visible.
	 * 
	 * @return true, if is visible
	 */
	boolean isVisible();

	/**
	 * Sets the text of the center status bar.
	 * 
	 * @param text
	 *            the text
	 * @param toolTip
	 */
	void setCenterStatusBarText(String text, String toolTip);

	/**
	 * Sets the default close operation.
	 * 
	 * @param op
	 */
	void setDefaultCloseOperation(int op);

	/**
	 * Sets the extended state.
	 * 
	 * @param state
	 */
	void setExtendedState(int state);

	/**
	 * Sets the location.
	 * 
	 * @param location
	 */
	void setLocation(Point location);

	/**
	 * Sets the location relative to.
	 * 
	 * @param c
	 */
	void setLocationRelativeTo(Component c);

	/**
	 * Sets the text of the right status bar.
	 * 
	 * @param text
	 *            the text
	 * @param toolTip
	 */
	void setRightStatusBarText(String text, String toolTip);

	/**
	 * Sets the status bar image label text.
	 * 
	 * @param icon
	 * @param text
	 */
	void setStatusBarDeviceLabelText(String text);

	/**
	 * Sets the title.
	 * 
	 * @param title
	 */
	void setTitle(String title);

	/**
	 * Sets the visible.
	 * 
	 * @param visible
	 */
	void setVisible(boolean visible);

	/**
	 * Show audio scrobbler panel.
	 * 
	 * @param show
	 */
	void showContextPanel(boolean show);

	/**
	 * Show navigator
	 * 
	 * @param show
	 */
	void showNavigator(boolean show);

	/**
	 * Show navigation table.
	 * 
	 * @param show
	 */
	void showNavigationTable(boolean show);

	/**
	 * Show navigation tree.
	 * 
	 * @param show
	 */
	void showNavigationTree(boolean show);

	/**
	 * Show navigation table filter
	 * 
	 * @param show
	 */
	void showNavigationTableFilter(boolean show);

	/**
	 * Show status bar.
	 * 
	 * @param show
	 *            the show
	 */
	void showStatusBar(boolean show);

	/**
	 * Show device info.
	 * 
	 * @param show
	 */
	void showDeviceInfo(boolean show);

	/**
	 * Show new podcast feed entries info.
	 * 
	 * @param show
	 */
	void showNewPodcastFeedEntriesInfo(boolean show);

	/**
	 * Show new version info.
	 * 
	 * @param show
	 * @param version
	 */
	void showNewVersionInfo(boolean show, ApplicationVersion version);

	/**
	 * Actions to perform when application is started and frame is visible
	 * 
	 * @param frameState
	 */
	void applicationStarted(IFrameState frameState);

	/**
	 * Returns split pane default relative positions
	 * 
	 * @return
	 */
	Map<String, Double> getDefaultSplitPaneRelativePositions();

	/**
	 * Shows progress bar with given text and determinate or indeterminate
	 * 
	 * @param indeterminate
	 * @param text
	 */
	void showProgressBar(boolean indeterminate, String text);

	/**
	 * Hides progress bar
	 */
	void hideProgressBar();

	/**
	 * @param stateUI
	 */
	void setStateUI(IStateUI stateUI);

	/**
	 * @param stateContext
	 */
	void setStateContext(IStateContext stateContext);

	/**
	 * @param controlsBuilder
	 */
	void setControlsBuilder(IControlsBuilder controlsBuilder);

	/**
	 * @param context
	 */
	void setApplicationContext(ApplicationContext context);
}
