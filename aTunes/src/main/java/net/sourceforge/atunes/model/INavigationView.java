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

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

/**
 * A view of navigator to show a content (repository, favorites, radios, etc)
 * 
 * @author alex
 * 
 */
public interface INavigationView extends IAudioObjectsSource,
		ITreeObjectsSource {

	/**
	 * @return the title of this view
	 */
	public String getTitle();

	/**
	 * @return the icon of this view
	 */
	public IColorMutableImageIcon getIcon();

	/**
	 * @return the tooltip of this view in tabbed pane
	 */
	public String getTooltip();

	/**
	 * @return the JTree that contains this view
	 */
	public INavigationTree getTree();

	/**
	 * @return the tree popup menu of this view
	 */
	public JPopupMenu getTreePopupMenu();

	/**
	 * @return factory of tree generators for this view
	 */
	public ITreeGeneratorFactory getTreeGeneratorFactory();

	/**
	 * @return the table popup menu of this view
	 */
	public JPopupMenu getTablePopupMenu();

	/**
	 * Returns scroll pane of tree
	 * 
	 * @return
	 */
	public JScrollPane getTreeScrollPane();

	/**
	 * @return panel to be placed over tree and shown in some situations
	 */
	public JPanel getOverlayPanel();

	/**
	 * Refreshes view
	 * 
	 * @param viewMode
	 * @param treeFilter
	 */
	public void refreshView(ViewMode viewMode, String treeFilter);

	/**
	 * Returns a list of audio object associated to a tree node
	 * 
	 * @param node
	 * @param viewMode
	 * @param treeFilter
	 * @param tableFilter
	 * @return
	 */
	public List<IAudioObject> getAudioObjectForTreeNode(ITreeNode node,
			ViewMode viewMode, String treeFilter, String tableFilter);

	/**
	 * Returns <code>true</code> if the view supports organize information in
	 * different view modes
	 * 
	 * @return
	 */
	public boolean isViewModeSupported();

	/**
	 * Returns <code>true</code> if the view uses default navigator columns or
	 * <code>false</code> if defines its own column set
	 * 
	 * @return
	 */
	public boolean isUseDefaultNavigatorColumnSet();

	/**
	 * If <code>isUseDefaultNavigatorColumns</code> returns <code>false</code>
	 * then this method must return a column set with columns
	 * 
	 * @return
	 */
	public IColumnSet getCustomColumnSet();

	/**
	 * Enables or disables tree popup menu items of this view
	 * 
	 * @param e
	 */
	public void updateTreePopupMenuWithTreeSelection(MouseEvent e);

	/**
	 * Enables or disables table popup menu items of this view
	 * 
	 * @param table
	 * @param e
	 */
	public void updateTablePopupMenuWithTableSelection(ITable table,
			MouseEvent e);

	/**
	 * Return current view mode
	 * 
	 * @return
	 */
	public ViewMode getCurrentViewMode();

	/**
	 * Returns an action to show this view
	 * 
	 * @return
	 */
	public Action getActionToShowView();

	/**
	 * Requests view to select given audio object
	 * 
	 * @param currentViewMode
	 * @param audioObject
	 */
	public void selectAudioObject(ViewMode currentViewMode,
			IAudioObject audioObject);

	/**
	 * Requests view to select given artist
	 * 
	 * @param currentViewMode
	 * @param artist
	 */
	public void selectArtist(ViewMode currentViewMode, String artist);

	/**
	 * @return true if this view needs to show overlay information
	 */
	public boolean overlayNeedsToBeVisible();

	/**
	 * @return text to show in overlay
	 */
	public String getOverlayText();

	/**
	 * @return action of overlay
	 */
	public Action getOverlayAction();
}