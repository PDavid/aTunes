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
import java.util.List;

import javax.swing.Action;


/**
 * This class represents a context panel shown in a context tab. Context panel
 * shows information related to the current audio object active in the
 * application
 * 
 * @author alex
 *
 */
public interface IContextPanel {

	/**
	 * Name of the context panel. This is an internal ID
	 * 
	 * @return The name of the context panel
	 */
	public String getContextPanelName();

	/**
	 * Title of the context panel as it will appear in context tab
	 * 
	 * @param audioObject
	 *            The current audio object in context panels or
	 *            <code>null</code> if no current audio object is selected
	 * 
	 * @return The title of the context panel
	 */
	public String getContextPanelTitle(IAudioObject audioObject);

	/**
	 * Icon of the context panel. This icon is used in context tab
	 * 
	 * @param audioObject
	 *            The current audio object in context panels or
	 *            <code>null</code> if no current audio object is selected
	 * @return The icon of the context panel
	 */
	public IColorMutableImageIcon getContextPanelIcon(IAudioObject audioObject);

	/**
	 * Indicates if panel must be visible for the given audio object
	 * 
	 * @param audioObject
	 *            The current audio object in context panels or
	 *            <code>null</code> if no current audio object is selected
	 * @return
	 */
	public boolean isPanelVisibleForAudioObject(IAudioObject audioObject);

	/**
	 * Updates the context panel with information related to the given audio
	 * object This method must be called every time the current audio object of
	 * the application changes and the panel is visible (the context tab showing
	 * this panel is selected)
	 * @param audioObject
	 * @param forceUpdate
	 */
	public void updateContextPanel(final IAudioObject audioObject, boolean forceUpdate);

	/**
	 * Removes all content from this context panel This method must be called
	 * when the user selected a different context tab, so if user returns to the
	 * tab showing this panel method updateContextPanel must be called again
	 */
	public void clearContextPanel();
	
	/**
	 * Returns if with the change of audio objects, panel needs to update its contents
	 * @param previousAudioObject
	 * @param newAudioObject
	 * @return
	 */
	public boolean panelNeedsToBeUpdated(IAudioObject previousAudioObject, IAudioObject newAudioObject);

	/**
	 * Returns a graphical component with all contents of the context panel
	 * 
	 * @param lookAndFeel
	 * @return
	 */
	public Component getUIComponent(ILookAndFeel lookAndFeel);

	/**
	 * Returns title to be used in tab for the current audio object
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * Returns icon to be used in tab for the current audio object
	 * 
	 * @return
	 */
	public IColorMutableImageIcon getIcon();

	/**
	 * Returns <code>true</code> if tab is enabled (can be used) for the current
	 * audio object
	 * 
	 * @return
	 */
	public boolean isEnabled();

	/**
	 * Returns <code>true</code> if tab is visible for the current audio object
	 * @return
	 */
	public boolean isVisible();

	/**
	 * Return components to show in options
	 * @return
	 */
	public List<Component> getOptions();

	/**
	 * Action when context panel selected
	 * @return
	 */
	public Action getAction();
	
	/**
	 * List of contents shown in the context panel. Contents are shown in order
	 * in context tab
	 * @param contents
	 */
	public void setContents(List<IContextPanelContent<?>> contents);
}