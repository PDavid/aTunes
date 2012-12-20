/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Navigation view which shows repository objects
 * 
 * @author alex
 * 
 */
public class RepositoryNavigationView extends AbstractNavigationView {

    private static final String REPOSITORY = "REPOSITORY";

    private NavigationTree tree;

    private IRepositoryHandler repositoryHandler;

    private IIconFactory audioFileSmallIcon;

    private JPopupMenu repositoryNavigationViewTreePopupMenu;

    private JPopupMenu repositoryNavigationViewTablePopupMenu;

    /**
     * @param repositoryNavigationViewTablePopupMenu
     */
    public void setRepositoryNavigationViewTablePopupMenu(
	    final JPopupMenu repositoryNavigationViewTablePopupMenu) {
	this.repositoryNavigationViewTablePopupMenu = repositoryNavigationViewTablePopupMenu;
    }

    /**
     * @param repositoryNavigationViewTreePopupMenu
     */
    public void setRepositoryNavigationViewTreePopupMenu(
	    final JPopupMenu repositoryNavigationViewTreePopupMenu) {
	this.repositoryNavigationViewTreePopupMenu = repositoryNavigationViewTreePopupMenu;
    }

    /**
     * @param audioFileSmallIcon
     */
    public void setAudioFileSmallIcon(final IIconFactory audioFileSmallIcon) {
	this.audioFileSmallIcon = audioFileSmallIcon;
    }

    @Override
    public IColorMutableImageIcon getIcon() {
	return audioFileSmallIcon.getColorMutableIcon();
    }

    @Override
    public String getTitle() {
	return I18nUtils.getString(REPOSITORY);
    }

    @Override
    public String getTooltip() {
	return I18nUtils.getString("REPOSITORY_TAB_TOOLTIP");
    }

    @Override
    public NavigationTree getTree() {
	if (tree == null) {
	    tree = new NavigationTree(I18nUtils.getString(REPOSITORY),
		    getTreeRenderer());
	    ToolTipManager.sharedInstance().registerComponent(tree);
	}
	return tree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
	return repositoryNavigationViewTreePopupMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
	return repositoryNavigationViewTablePopupMenu;
    }

    @Override
    protected Map<String, ?> getViewData(final ViewMode viewMode) {
	return repositoryHandler.getDataForView(viewMode);
    }

    @Override
    public void selectAudioObject(final ViewMode viewMode,
	    final IAudioObject audioObject) {
	getTreeGeneratorFactory().getTreeGenerator(viewMode).selectAudioObject(
		getTree(), audioObject);
    }

    @Override
    public void selectArtist(final ViewMode viewMode, final String artist) {
	getTreeGeneratorFactory().getTreeGenerator(viewMode).selectArtist(
		getTree(), artist);
    }

    @Override
    protected void refreshTree(final ViewMode viewMode, final String treeFilter) {
	// Get objects selected before refreshing tree
	List<ITreeObject<? extends IAudioObject>> objectsSelected = getSelectedTreeObjects();
	// Get objects expanded before refreshing tree
	List<ITreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(tree);

	// Build treeN
	getTreeGeneratorFactory().getTreeGenerator(viewMode).buildTree(
		getTree(), REPOSITORY, this, getViewData(viewMode), treeFilter,
		objectsSelected, objectsExpanded);

	getTree().expandRow(0);
    }

    @Override
    public List<? extends IAudioObject> getAudioObjectForTreeNode(
	    final ITreeNode node, final ViewMode viewMode,
	    final String treeFilter) {
	return new RepositoryAudioObjectsHelper().getAudioObjectForTreeNode(
		repositoryHandler.getAudioFilesList(), node, viewMode,
		treeFilter);
    }

    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
	return true;
    }

    @Override
    public IColumnSet getCustomColumnSet() {
	// Return null since use default navigator column set
	return null;
    }

    @Override
    public boolean isViewModeSupported() {
	return true;
    }

    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
	this.repositoryHandler = repositoryHandler;
    }
}
