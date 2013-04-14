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

import javax.swing.JPanel;

/**
 * This class represents a little graphic component used in a context panel to
 * show information about an audio object Information shown is retrieved from a
 * context data source
 * 
 * @author alex
 * 
 * @param <T>
 */
public interface IContextPanelContent<T extends IContextInformationSource> {

	/**
	 * Updates the context panel content with information of the given audio
	 * object
	 * 
	 * @param audioObject
	 * @param finishCallback
	 */
	public void updateContextPanelContent(IAudioObject audioObject,
			IBackgroundWorkerCallback<Void> finishCallback);

	/**
	 * Removes content from the context panel content This method must clear all
	 * previous information retrieved for previous audio object
	 */
	public void clearContextPanelContent();

	/**
	 * By default contents don't need special scroll
	 * 
	 * @return
	 */
	public boolean isScrollNeeded();

	/**
	 * Given an audio object updates its content
	 * 
	 * @param source
	 */
	public void updateContentFromDataSource(T source);

	/**
	 * Returns the content name to be shown in context panel
	 * 
	 * @return
	 */
	public String getContentName();

	/**
	 * Method to return a Swing component with panel content
	 * 
	 * @return
	 */
	public Component getComponent();

	/**
	 * Returns a list of components to be shown in a popup button If this method
	 * returns <code>null</code> or empty list button will not be visible
	 * (default behaviour)
	 * 
	 * @return
	 */
	public List<Component> getOptions();

	/**
	 * @param parentPanel
	 *            the parentTaskPane to set
	 */
	public void setParentPanel(JPanel parentPanel);

	/**
	 * @param dataSource
	 */
	public void setDataSource(IContextInformationSource dataSource);

	/**
	 * @param iLookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager iLookAndFeelManager);

	/**
	 * @return
	 */
	public JPanel getParentPanel();
}