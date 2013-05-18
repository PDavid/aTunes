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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedTreeObjects;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
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

	private IColumnSet navigatorColumnSet;

	/**
	 * @param navigatorColumnSet
	 */
	public void setNavigatorColumnSet(final IColumnSet navigatorColumnSet) {
		this.navigatorColumnSet = navigatorColumnSet;
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
		return this.deviceIcon.getColorMutableIcon();
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
		if (this.deviceTree == null) {
			this.deviceTree = new NavigationTree(I18nUtils.getString("DEVICE"),
					getTreeRenderer());
		}
		return this.deviceTree;
	}

	@Override
	public JPopupMenu getTreePopupMenu() {
		if (this.deviceTreeMenu == null) {
			this.deviceTreeMenu = new JPopupMenu();
			AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = getBeanFactory()
					.getBean("addToPlayListFromDeviceNavigationView",
							AddToPlayListAction.class);
			addToPlayListAction.setAudioObjectsSource(this);
			this.deviceTreeMenu.add(addToPlayListAction);

			SetAsPlayListAction setAsPlayListAction = getBeanFactory().getBean(
					"setAsPlaylistFromDeviceNavigationView",
					SetAsPlayListAction.class);
			setAsPlayListAction.setAudioObjectsSource(this);
			this.deviceTreeMenu.add(setAsPlayListAction);

			this.deviceTreeMenu.add(new JSeparator());

			AbstractActionOverSelectedTreeObjects<IFolder> openFolder = getBeanFactory()
					.getBean("openFolderFromDeviceNavigationTree",
							OpenFolderFromNavigatorTreeAction.class);
			openFolder.setTreeObjectsSource(this);
			this.deviceTreeMenu.add(openFolder);

			this.deviceTreeMenu.add(new JSeparator());
			this.deviceTreeMenu.add(new EditTagMenu(false, this,
					getBeanFactory()));

			AbstractActionOverSelectedTreeObjects<IAlbum> editTitles = getBeanFactory()
					.getBean("editTitlesFromDeviceViewAction",
							EditTitlesAction.class);
			editTitles.setTreeObjectsSource(this);
			this.deviceTreeMenu.add(editTitles);
			this.deviceTreeMenu.add(new JSeparator());
			this.deviceTreeMenu.add(getBeanFactory().getBean(
					RemoveFromDiskAction.class));
			this.deviceTreeMenu.add(new JSeparator());

			CopyToRepositoryAction copyToRepositoryAction = getBeanFactory()
					.getBean(CopyToRepositoryAction.class);
			copyToRepositoryAction.setAudioObjectsSource(this);
			this.deviceTreeMenu.add(copyToRepositoryAction);

			this.deviceTreeMenu.add(getBeanFactory().getBean(
					FillDeviceWithRandomSongsAction.class));
			this.deviceTreeMenu.add(new JSeparator());
			this.deviceTreeMenu.add(getBeanFactory().getBean(
					SearchArtistAction.class));
			this.deviceTreeMenu.add(getBeanFactory().getBean(
					SearchArtistAtAction.class));
		}
		return this.deviceTreeMenu;
	}

	@Override
	public JPopupMenu getTablePopupMenu() {
		if (this.deviceTableMenu == null) {
			this.deviceTableMenu = new JPopupMenu();
			AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = getBeanFactory()
					.getBean("addToPlayListFromDeviceNavigationView",
							AddToPlayListAction.class);
			addToPlayListAction.setAudioObjectsSource(this);
			this.deviceTableMenu.add(addToPlayListAction);
			AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = getBeanFactory()
					.getBean(
							"addToPlayListAfterCurrentAudioObjectFromDeviceNavigationView",
							AddToPlayListAfterCurrentAudioObjectAction.class);
			addToPlayListAfterCurrentAudioObjectAction
					.setAudioObjectsSource(this);
			this.deviceTableMenu
					.add(addToPlayListAfterCurrentAudioObjectAction);

			SetAsPlayListAction setAsPlayListAction = getBeanFactory().getBean(
					"setAsPlaylistFromDeviceNavigationView",
					SetAsPlayListAction.class);
			setAsPlayListAction.setAudioObjectsSource(this);
			this.deviceTableMenu.add(setAsPlayListAction);

			this.deviceTableMenu.add(getBeanFactory().getBean(
					PlayNowAction.class));
			this.deviceTableMenu.add(new JSeparator());

			OpenFolderFromNavigatorTableAction openFolderFromNavigatorAction = getBeanFactory()
					.getBean("openFolderFromDeviceNavigationTable",
							OpenFolderFromNavigatorTableAction.class);
			openFolderFromNavigatorAction.setAudioObjectsSource(this);
			this.deviceTableMenu.add(openFolderFromNavigatorAction);

			this.deviceTableMenu.add(new JSeparator());
			this.deviceTableMenu.add(new EditTagMenu(false, this,
					getBeanFactory()));

			ExtractPictureAction extractPictureAction = getBeanFactory()
					.getBean("extractPictureFromDeviceNavigationView",
							ExtractPictureAction.class);
			extractPictureAction.setAudioObjectsSource(this);
			this.deviceTableMenu.add(extractPictureAction);

			this.deviceTableMenu.add(new JSeparator());
			this.deviceTableMenu.add(getBeanFactory().getBean(
					RemoveFromDiskAction.class));
			this.deviceTableMenu.add(new JSeparator());

			CopyToRepositoryAction copyToRepositoryAction = getBeanFactory()
					.getBean(CopyToRepositoryAction.class);
			copyToRepositoryAction.setAudioObjectsSource(this);
			this.deviceTableMenu.add(copyToRepositoryAction);
		}
		return this.deviceTableMenu;
	}

	@Override
	protected Map<String, ?> getViewData(final ViewMode viewMode) {
		return this.deviceHandler.getDataForView(viewMode);
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
	public List<IAudioObject> getAudioObjectForTreeNode(final ITreeNode node,
			final ViewMode viewMode, final String treeFilter,
			final String tableFilter) {
		return new RepositoryAudioObjectsHelper().getAudioObjectForTreeNode(
				this.deviceHandler.getAudioFilesList(), node, viewMode,
				treeFilter, tableFilter, this.navigatorColumnSet);
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

	@Override
	public boolean overlayNeedsToBeVisible() {
		return !this.deviceHandler.isDeviceConnected();
	}

	@Override
	public Action getOverlayAction() {
		return getBeanFactory().getBean(ConnectDeviceAction.class);
	}

	@Override
	public String getOverlayText() {
		return I18nUtils.getString("NO_DEVICE_INFORMATION");
	}
}
