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

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.AudioFileImageIcon;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedTreeObjects;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RefreshFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.RenameAudioFileInNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteAlbumFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteArtistFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteSongFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class RepositoryNavigationView extends AbstractNavigationView {

    private JTree tree;

    private JPopupMenu treePopupMenu;

    private JPopupMenu tablePopupMenu;
    
    private IRepositoryHandler repositoryHandler;

    @Override
    public IColorMutableImageIcon getIcon() {    	
        return new IColorMutableImageIcon() {
			
			@Override
			public ImageIcon getIcon(Color paint) {
				return AudioFileImageIcon.getSmallImageIcon(paint, getLookAndFeelManager().getCurrentLookAndFeel());
			}
		};
    }

    @Override
    public String getTitle() {
        return I18nUtils.getString("REPOSITORY");
    }

    @Override
    public String getTooltip() {
        return I18nUtils.getString("REPOSITORY_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (tree == null) {
            tree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("REPOSITORY"))));
            tree.setToggleClickCount(0);
            tree.setCellRenderer(getTreeRenderer());
            ToolTipManager.sharedInstance().registerComponent(tree);
        }
        return tree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (treePopupMenu == null) {
            treePopupMenu = new JPopupMenu();
            
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = Context.getBean("addToPlayListFromRepositoryNavigationView", AddToPlayListAction.class);
            addToPlayListAction.setAudioObjectsSource(this);
            treePopupMenu.add(addToPlayListAction);
            
            SetAsPlayListAction setAsPlayListAction = Context.getBean("setAsPlaylistFromRepositoryNavigationView", SetAsPlayListAction.class);
            setAsPlayListAction.setAudioObjectsSource(this);
            treePopupMenu.add(setAsPlayListAction);
            
            treePopupMenu.add(new JSeparator());
            
            OpenFolderFromNavigatorAction openFolderFromNavigatorAction = Context.getBean("openFolderFromRepositoryNavigationView", OpenFolderFromNavigatorAction.class);
            openFolderFromNavigatorAction.setAudioObjectsSource(this);
            treePopupMenu.add(openFolderFromNavigatorAction);
            
            AbstractActionOverSelectedTreeObjects<Folder> refreshFolderFromNavigatorAction = Context.getBean(RefreshFolderFromNavigatorAction.class);
            refreshFolderFromNavigatorAction.setTreeObjectsSource(this);
            treePopupMenu.add(refreshFolderFromNavigatorAction);
            
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(new EditTagMenu(false, this));
            treePopupMenu.add(Context.getBean(EditTitlesAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(Context.getBean(RemoveFromDiskAction.class));
            treePopupMenu.add(new JSeparator());
            
            AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = Context.getBean("copyToDeviceFromRepositoryNavigationView", CopyToDeviceAction.class);
            copyToDeviceAction.setAudioObjectsSource(this);
            treePopupMenu.add(copyToDeviceAction);
            
            treePopupMenu.add(new JSeparator());
            
            SetFavoriteAlbumFromNavigatorAction setFavoriteAlbumFromNavigatorAction = Context.getBean(SetFavoriteAlbumFromNavigatorAction.class);
            setFavoriteAlbumFromNavigatorAction.setAudioObjectsSource(this);
            treePopupMenu.add(setFavoriteAlbumFromNavigatorAction);
            
            SetFavoriteArtistFromNavigatorAction setFavoriteArtistFromNavigatorAction = Context.getBean(SetFavoriteArtistFromNavigatorAction.class);
            setFavoriteArtistFromNavigatorAction.setAudioObjectsSource(this);
            treePopupMenu.add(setFavoriteArtistFromNavigatorAction);
            
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(Context.getBean(SearchArtistAction.class));
            treePopupMenu.add(Context.getBean(SearchArtistAtAction.class));
        }
        return treePopupMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (tablePopupMenu == null) {
            tablePopupMenu = new JPopupMenu();
            
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = Context.getBean("addToPlayListFromRepositoryNavigationView", AddToPlayListAction.class);
            addToPlayListAction.setAudioObjectsSource(this);
            tablePopupMenu.add(addToPlayListAction);
            
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = Context.getBean("addToPlayListAfterCurrentAudioObjectFromRepositoryNavigationView", AddToPlayListAfterCurrentAudioObjectAction.class);
            addToPlayListAfterCurrentAudioObjectAction.setAudioObjectsSource(this);
            tablePopupMenu.add(addToPlayListAfterCurrentAudioObjectAction);
            
            SetAsPlayListAction setAsPlayListAction = Context.getBean("setAsPlaylistFromRepositoryNavigationView", SetAsPlayListAction.class);
            setAsPlayListAction.setAudioObjectsSource(this);
            tablePopupMenu.add(setAsPlayListAction);
            
            tablePopupMenu.add(Context.getBean(PlayNowAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(Context.getBean(ShowNavigatorTableItemInfoAction.class));
            tablePopupMenu.add(new JSeparator());
            
            OpenFolderFromNavigatorAction openFolderFromNavigatorAction = Context.getBean("openFolderFromRepositoryNavigationView", OpenFolderFromNavigatorAction.class);
            openFolderFromNavigatorAction.setAudioObjectsSource(this);
            tablePopupMenu.add(openFolderFromNavigatorAction);
            
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(new EditTagMenu(false, this));
            
            ExtractPictureAction extractPictureAction = Context.getBean("extractPictureFromRepositoryNavigationView", ExtractPictureAction.class);
            extractPictureAction.setAudioObjectsSource(this);
            tablePopupMenu.add(extractPictureAction);
            
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(Context.getBean(RemoveFromDiskAction.class));
            tablePopupMenu.add(Context.getBean(RenameAudioFileInNavigationTableAction.class));
            tablePopupMenu.add(new JSeparator());

            AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = Context.getBean("copyToDeviceFromRepositoryNavigationView", CopyToDeviceAction.class);
            copyToDeviceAction.setAudioObjectsSource(this);
            tablePopupMenu.add(copyToDeviceAction);
            
            tablePopupMenu.add(new JSeparator());
            
            SetFavoriteSongFromNavigatorAction setFavoriteSongFromNavigatorAction = Context.getBean(SetFavoriteSongFromNavigatorAction.class);
            setFavoriteSongFromNavigatorAction.setAudioObjectsSource(this);
            tablePopupMenu.add(setFavoriteSongFromNavigatorAction);
        }
        return tablePopupMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
    	return repositoryHandler.getDataForView(viewMode);
    }

    @Override
    public void selectAudioObject(ViewMode viewMode, IAudioObject audioObject) {
    	getTreeGeneratorFactory().getTreeGenerator(viewMode).selectAudioObject(getTree(), audioObject);
    }
    
    @Override
	public void selectArtist(ViewMode viewMode, String artist) {
    	getTreeGeneratorFactory().getTreeGenerator(viewMode).selectArtist(getTree(), artist);
	}
    
    @SuppressWarnings("unchecked")
    @Override
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing ", this.getClass().getName());

        // Get model and root
        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<ITreeObject<? extends IAudioObject>> objectsSelected = getTreeObjectsSelected(tree);
        // Get objects expanded before refreshing tree
        List<ITreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(tree, root);
        
        // Build treeN
        getTreeGeneratorFactory().getTreeGenerator(viewMode).buildTree("REPOSITORY", this, (Map<String, Year>) getViewData(viewMode), treeFilter, root, treeModel, objectsSelected, objectsExpanded);
        
        getTree().expandRow(0);
    }

    @Override
    public List<? extends IAudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
    	return new RepositoryAudioObjectsHelper().getAudioObjectForTreeNode(repositoryHandler.getAudioFilesList(), node, viewMode, treeFilter);
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
     * Gets the tool tip for repository.
     * 
     * @param repositoryHandler
     * @return the tool tip for repository
     */
    static String getToolTipForRepository(IRepositoryHandler repositoryHandler) {
        int songs = repositoryHandler.getAudioFilesList().size();
        return StringUtils.getString(I18nUtils.getString("REPOSITORY"), " (", songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG")), ")");
    }
    
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
}
