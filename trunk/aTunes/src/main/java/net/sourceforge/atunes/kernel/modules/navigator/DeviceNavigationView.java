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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedTreeObjects;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToRepositoryAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.FillDeviceWithRandomSongsAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorTableAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorTreeAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Navigation view for device
 * 
 * @author alex
 * 
 */
public final class DeviceNavigationView extends AbstractNavigationView {

    /** The device tree. */
    private NavigationTree deviceTree;

    /** The device tree menu. */
    private JPopupMenu deviceTreeMenu;

    /** The device table menu. */
    private JPopupMenu deviceTableMenu;

    private IDeviceHandler deviceHandler;

    private IIconFactory deviceIcon;

    private IBeanFactory beanFactory;

    /**
     * @param beanFactory
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

    /**
     * @param deviceIcon
     */
    public void setDeviceIcon(final IIconFactory deviceIcon) {
	this.deviceIcon = deviceIcon;
    }

    /**
     * @param deviceHandler
     */
    public void setDeviceHandler(final IDeviceHandler deviceHandler) {
	this.deviceHandler = deviceHandler;
    }

    @Override
    public IColorMutableImageIcon getIcon() {
	return deviceIcon.getColorMutableIcon();
    }

    @Override
    public String getTitle() {
	return I18nUtils.getString("DEVICE");
    }

    @Override
    public String getTooltip() {
	return I18nUtils.getString("DEVICE_TAB_TOOLTIP");
    }

    @Override
    public NavigationTree getTree() {
	if (deviceTree == null) {
	    deviceTree = new NavigationTree(I18nUtils.getString("DEVICE"),
		    getTreeRenderer());
	}
	return deviceTree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
	if (deviceTreeMenu == null) {
	    deviceTreeMenu = new JPopupMenu();
	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = beanFactory
		    .getBean("addToPlayListFromDeviceNavigationView",
			    AddToPlayListAction.class);
	    addToPlayListAction.setAudioObjectsSource(this);
	    deviceTreeMenu.add(addToPlayListAction);

	    SetAsPlayListAction setAsPlayListAction = beanFactory.getBean(
		    "setAsPlaylistFromDeviceNavigationView",
		    SetAsPlayListAction.class);
	    setAsPlayListAction.setAudioObjectsSource(this);
	    deviceTreeMenu.add(setAsPlayListAction);

	    deviceTreeMenu.add(new JSeparator());

	    AbstractActionOverSelectedTreeObjects<IFolder> openFolder = beanFactory
		    .getBean("openFolderFromDeviceNavigationTree",
			    OpenFolderFromNavigatorTreeAction.class);
	    openFolder.setTreeObjectsSource(this);
	    deviceTreeMenu.add(openFolder);

	    deviceTreeMenu.add(new JSeparator());
	    deviceTreeMenu.add(new EditTagMenu(false, this));

	    AbstractActionOverSelectedTreeObjects<IAlbum> editTitles = beanFactory
		    .getBean("editTitlesFromDeviceViewAction",
			    EditTitlesAction.class);
	    editTitles.setTreeObjectsSource(this);
	    deviceTreeMenu.add(editTitles);
	    deviceTreeMenu.add(new JSeparator());
	    deviceTreeMenu.add(beanFactory.getBean(RemoveFromDiskAction.class));
	    deviceTreeMenu.add(new JSeparator());

	    CopyToRepositoryAction copyToRepositoryAction = beanFactory
		    .getBean(CopyToRepositoryAction.class);
	    copyToRepositoryAction.setAudioObjectsSource(this);
	    deviceTreeMenu.add(copyToRepositoryAction);

	    deviceTreeMenu.add(beanFactory
		    .getBean(FillDeviceWithRandomSongsAction.class));
	    deviceTreeMenu.add(new JSeparator());
	    deviceTreeMenu.add(beanFactory.getBean(SearchArtistAction.class));
	    deviceTreeMenu.add(beanFactory.getBean(SearchArtistAtAction.class));
	}
	return deviceTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
	if (deviceTableMenu == null) {
	    deviceTableMenu = new JPopupMenu();
	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = beanFactory
		    .getBean("addToPlayListFromDeviceNavigationView",
			    AddToPlayListAction.class);
	    addToPlayListAction.setAudioObjectsSource(this);
	    deviceTableMenu.add(addToPlayListAction);
	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = beanFactory
		    .getBean(
			    "addToPlayListAfterCurrentAudioObjectFromDeviceNavigationView",
			    AddToPlayListAfterCurrentAudioObjectAction.class);
	    addToPlayListAfterCurrentAudioObjectAction
		    .setAudioObjectsSource(this);
	    deviceTableMenu.add(addToPlayListAfterCurrentAudioObjectAction);

	    SetAsPlayListAction setAsPlayListAction = beanFactory.getBean(
		    "setAsPlaylistFromDeviceNavigationView",
		    SetAsPlayListAction.class);
	    setAsPlayListAction.setAudioObjectsSource(this);
	    deviceTableMenu.add(setAsPlayListAction);

	    deviceTableMenu.add(beanFactory.getBean(PlayNowAction.class));
	    deviceTableMenu.add(new JSeparator());

	    OpenFolderFromNavigatorTableAction openFolderFromNavigatorAction = beanFactory
		    .getBean("openFolderFromDeviceNavigationTable",
			    OpenFolderFromNavigatorTableAction.class);
	    openFolderFromNavigatorAction.setAudioObjectsSource(this);
	    deviceTableMenu.add(openFolderFromNavigatorAction);

	    deviceTableMenu.add(new JSeparator());
	    deviceTableMenu.add(new EditTagMenu(false, this));

	    ExtractPictureAction extractPictureAction = beanFactory.getBean(
		    "extractPictureFromDeviceNavigationView",
		    ExtractPictureAction.class);
	    extractPictureAction.setAudioObjectsSource(this);
	    deviceTableMenu.add(extractPictureAction);

	    deviceTableMenu.add(new JSeparator());
	    deviceTableMenu
		    .add(beanFactory.getBean(RemoveFromDiskAction.class));
	    deviceTableMenu.add(new JSeparator());

	    CopyToRepositoryAction copyToRepositoryAction = beanFactory
		    .getBean(CopyToRepositoryAction.class);
	    copyToRepositoryAction.setAudioObjectsSource(this);
	    deviceTableMenu.add(copyToRepositoryAction);
	}
	return deviceTableMenu;
    }

    @Override
    protected Map<String, ?> getViewData(final ViewMode viewMode) {
	return deviceHandler.getDataForView(viewMode);
    }

    @Override
    protected void refreshTree(final ViewMode viewMode, final String treeFilter) {
	// Nodes to be selected after refresh
	List<ITreeNode> nodesToSelect = new ArrayList<ITreeNode>();
	// Nodes to be expanded after refresh
	List<ITreeNode> nodesToExpand = new ArrayList<ITreeNode>();

	// Get objects selected before refreshing tree
	List<ITreeObject<? extends IAudioObject>> objectsSelected = getSelectedTreeObjects();
	// Get objects expanded before refreshing tree
	List<ITreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(getTree());

	getTree().setRoot(I18nUtils.getString("DEVICE"));

	// Build tree
	getTreeGeneratorFactory().getTreeGenerator(viewMode).buildTree(
		getTree(), "DEVICE", this, getViewData(viewMode), treeFilter,
		objectsSelected, objectsExpanded);

	// Expand nodes
	getTree().expandNodes(nodesToExpand);

	// Once tree has been refreshed, select previously selected nodes
	getTree().selectNodes(nodesToSelect);
    }

    @Override
    public List<? extends IAudioObject> getAudioObjectForTreeNode(
	    final ITreeNode node, final ViewMode viewMode,
	    final String treeFilter) {
	return new RepositoryAudioObjectsHelper().getAudioObjectForTreeNode(
		deviceHandler.getAudioFilesList(), node, viewMode, treeFilter);
    }

    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
	return true;
    }

    @Override
    public IColumnSet getCustomColumnSet() {
	return null;
    }

    @Override
    public boolean isViewModeSupported() {
	return true;
    }
}
