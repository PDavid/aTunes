/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.images.DeviceImageIcon;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToRepositoryAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.FillDeviceWithRandomSongsAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public final class DeviceNavigationView extends AbstractNavigationView {

    /** The device tree. */
    private JTree deviceTree;

    /** The device tree menu. */
    private JPopupMenu deviceTreeMenu;

    /** The device table menu. */
    private JPopupMenu deviceTableMenu;

    public DeviceNavigationView(IState state) {
    	super(state);
	}
    
    @Override
    public ColorMutableImageIcon getIcon() {
        return new ColorMutableImageIcon() {
			
			@Override
			public ImageIcon getIcon(Paint paint) {
				return DeviceImageIcon.getIcon(paint);
			}
		};
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
    public JTree getTree() {
        if (deviceTree == null) {
            deviceTree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("DEVICE"))));
            deviceTree.setToggleClickCount(0);
            deviceTree.setCellRenderer(getTreeRenderer());
        }
        return deviceTree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (deviceTreeMenu == null) {
            deviceTreeMenu = new JPopupMenu();
            deviceTreeMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            deviceTreeMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(new EditTagMenu(false, this));
            deviceTreeMenu.add(Actions.getAction(EditTitlesAction.class));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(new JMenuItem(Actions.getAction(RemoveFromDiskAction.class)));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(getMenuItemForAction(CopyToRepositoryAction.class));
            deviceTreeMenu.add(new JMenuItem(Actions.getAction(FillDeviceWithRandomSongsAction.class)));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(Actions.getAction(SearchArtistAction.class));
            deviceTreeMenu.add(Actions.getAction(SearchArtistAtAction.class));
        }
        return deviceTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (deviceTableMenu == null) {
            deviceTableMenu = new JPopupMenu();
            deviceTableMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            deviceTableMenu.add(getMenuItemForAction(AddToPlayListAfterCurrentAudioObjectAction.class));
            deviceTableMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            deviceTableMenu.add(new JMenuItem(Actions.getAction(PlayNowAction.class)));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(new JMenuItem(Actions.getAction(OpenFolderFromNavigatorAction.class)));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(new EditTagMenu(false, this));
            deviceTableMenu.add(getMenuItemForAction(ExtractPictureAction.class));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(new JMenuItem(Actions.getAction(RemoveFromDiskAction.class)));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(getMenuItemForAction(CopyToRepositoryAction.class));
        }
        return deviceTableMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
    	return DeviceHandler.getInstance().getDataForView(viewMode);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing ", this.getClass().getName());

        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        // Get objects selected before refreshing tree
        List<TreeObject<? extends IAudioObject>> objectsSelected = getTreeObjectsSelected(getTree());
        // Get objects expanded before refreshing tree
        List<TreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(getTree(), root);

        root.removeAllChildren();

        // Build tree
        getTreeGeneratorFactory().getTreeGenerator(viewMode).buildTree(getState(), "DEVICE", this, (Map<String, Year>) getViewData(viewMode), treeFilter, root, treeModel, objectsSelected, objectsExpanded);

        
        // Expand nodes
        expandNodes(getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(getTree(), nodesToSelect);
    }

    @Override
    public List<ILocalAudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>();
        if (node.isRoot()) {
            if (treeFilter == null) {
                songs.addAll(DeviceHandler.getInstance().getAudioFilesList());
            } else {
                for (int i = 0; i < node.getChildCount(); i++) {
                    @SuppressWarnings("unchecked")
					TreeObject<ILocalAudioObject> obj = (TreeObject<ILocalAudioObject>) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
                    songs.addAll(obj.getAudioObjects());
                }
            }
        } else {
            @SuppressWarnings("unchecked")
			TreeObject<ILocalAudioObject> obj = (TreeObject<ILocalAudioObject>) node.getUserObject();
            songs = obj.getAudioObjects();
        }
        return songs;
    }

    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
        return true;
    }

    @Override
    public AbstractColumnSet getCustomColumnSet() {
        return null;
    }

    @Override
    public boolean isViewModeSupported() {
        return true;
    }
}
