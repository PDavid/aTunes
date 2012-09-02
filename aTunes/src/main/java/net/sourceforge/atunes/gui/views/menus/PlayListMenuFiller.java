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

package net.sourceforge.atunes.gui.views.menus;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AddAlbumWithSelectedArtistsAction;
import net.sourceforge.atunes.kernel.actions.AutoScrollPlayListAction;
import net.sourceforge.atunes.kernel.actions.ClearPlayListAction;
import net.sourceforge.atunes.kernel.actions.CreatePlayListWithSelectedAlbumsAction;
import net.sourceforge.atunes.kernel.actions.CreatePlayListWithSelectedArtistsAction;
import net.sourceforge.atunes.kernel.actions.LoadPlayListAction;
import net.sourceforge.atunes.kernel.actions.MoveAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.MoveDownAction;
import net.sourceforge.atunes.kernel.actions.MoveToBottomAction;
import net.sourceforge.atunes.kernel.actions.MoveToTopAction;
import net.sourceforge.atunes.kernel.actions.MoveUpAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderAction;
import net.sourceforge.atunes.kernel.actions.RemoveDuplicatesFromPlayListAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromPlayListAction;
import net.sourceforge.atunes.kernel.actions.SaveM3UPlayListAction;
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteAlbumAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteArtistAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteSongAction;
import net.sourceforge.atunes.kernel.actions.ShowPlayListItemInfoAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Creates play list menu
 * @author alex
 *
 */
public final class PlayListMenuFiller {

	private IPlayListTable playListTable;
	
	private IPlayListHandler playListHandler;

	private IBeanFactory beanFactory;
	
	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param playListTable
	 */
	public void setPlayListTable(IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}

    /**
     * Fills a pop up with play list menu items
     * 
     * @param menu
     */
    void fillPopUpMenu(JPopupMenu menu) {
        List<Object> objectsToAdd = getComponents(playListTable);
        for (Object o : objectsToAdd) {
            // Object can be an action or a swing component
            if (o instanceof Action) {
                menu.add((Action) o);
            } else {
                menu.add((Component) o);
            }
        }
    }

    /**
     * Fills a menu with play list menu items
     * 
     * @param menu
     */
    void fillMenu(JMenu menu) {
        List<Object> objectsToAdd = getComponents(playListTable);
        for (Object o : objectsToAdd) {
            // Object can be an action or a swing component
            if (o instanceof Action) {
                menu.add((Action) o);
            } else {
                menu.add((Component) o);
            }
        }
    }

    /**
     * returns components or actions to add to a menu or popup menu
     * 
     * @return
     */
    private List<Object> getComponents(IPlayListTable table) {
        List<Object> objects = new ArrayList<Object>();
        
        objects.add(beanFactory.getBean(ShowPlayListItemInfoAction.class));
        
        OpenFolderAction openFolderAction = beanFactory.getBean(OpenFolderAction.class);
        openFolderAction.setAudioObjectsSource(table);
        objects.add(openFolderAction);
        
        objects.add(new JSeparator());
        objects.add(new EditTagMenu(true, table));
        objects.add(getFavoritesMenu());
        objects.add(new JSeparator());
        objects.add(beanFactory.getBean(AutoScrollPlayListAction.class));
        objects.add(getMoveMenu());
        objects.add(beanFactory.getBean(RemoveFromPlayListAction.class));
        objects.add(beanFactory.getBean(RemoveDuplicatesFromPlayListAction.class));
        objects.add(beanFactory.getBean(ClearPlayListAction.class));
        objects.add(new JSeparator());
        objects.add(beanFactory.getBean(SavePlayListAction.class));
        objects.add(beanFactory.getBean(SaveM3UPlayListAction.class));
        objects.add(beanFactory.getBean(LoadPlayListAction.class));
        objects.add(new JSeparator());
        objects.add(getSmartPlayListMenu());
        objects.add(new JSeparator());
        
        AbstractActionOverSelectedObjects<IAudioObject> createPlayListWithSelectedArtistsAction = beanFactory.getBean(CreatePlayListWithSelectedArtistsAction.class);
        createPlayListWithSelectedArtistsAction.setAudioObjectsSource(table);
        objects.add(createPlayListWithSelectedArtistsAction);
        
        AbstractActionOverSelectedObjects<IAudioObject> createPlayListWithSelectedAlbumAction = beanFactory.getBean(CreatePlayListWithSelectedAlbumsAction.class);
        createPlayListWithSelectedAlbumAction.setAudioObjectsSource(table);
        objects.add(createPlayListWithSelectedAlbumAction);
        
        AbstractActionOverSelectedObjects<IAudioObject> addAlbumWithSelectedArtistsAction = beanFactory.getBean(AddAlbumWithSelectedArtistsAction.class);
        addAlbumWithSelectedArtistsAction.setAudioObjectsSource(table);
        objects.add(addAlbumWithSelectedArtistsAction);
        return objects;
    }
    
    /**
     * Returns menu for smart play list
     * @return
     */
    private JMenu getSmartPlayListMenu() {
        JMenu smartPlayList = new JMenu(I18nUtils.getString("SMART_PLAYLIST"));
        smartPlayList.add(beanFactory.getBean("addRandomSongsAction10", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addRandomSongsAction50", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addRandomSongsAction100", AbstractAction.class));
        smartPlayList.add(new JSeparator());
        smartPlayList.add(beanFactory.getBean("addSongsMostPlayedAction10", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addSongsMostPlayedAction50", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addSongsMostPlayedAction100", AbstractAction.class));
        smartPlayList.add(new JSeparator());
        smartPlayList.add(beanFactory.getBean("addAlbumMostPlayedAction1", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addAlbumMostPlayedAction5", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addAlbumMostPlayedAction10", AbstractAction.class));
        smartPlayList.add(new JSeparator());
        smartPlayList.add(beanFactory.getBean("addArtistsMostPlayedAction1", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addArtistsMostPlayedAction5", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addArtistsMostPlayedAction10", AbstractAction.class));
        smartPlayList.add(new JSeparator());
        smartPlayList.add(beanFactory.getBean("addUnplayedSongsAction10", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addUnplayedSongsAction50", AbstractAction.class));
        smartPlayList.add(beanFactory.getBean("addUnplayedSongsAction100", AbstractAction.class));
        return smartPlayList;
    }
    
    /**
     * Returns menu for favorites
     * @return
     */
    private JMenu getFavoritesMenu() {
        JMenu favorites = new JMenu(I18nUtils.getString("FAVORITES"));
        favorites.add(beanFactory.getBean(SetPlayListSelectionAsFavoriteSongAction.class));
        favorites.add(beanFactory.getBean(SetPlayListSelectionAsFavoriteAlbumAction.class));
        favorites.add(beanFactory.getBean(SetPlayListSelectionAsFavoriteArtistAction.class));
        return favorites;
    }
    
    /**
     * Returns move menu
     * @return
     */
    private JMenu getMoveMenu() {
        JMenu move = new JMenu(I18nUtils.getString("MOVE"));
        move.add(beanFactory.getBean(MoveAfterCurrentAudioObjectAction.class));
        move.add(new JSeparator());
        move.add(beanFactory.getBean(MoveToTopAction.class));
        move.add(beanFactory.getBean(MoveUpAction.class));
        move.add(beanFactory.getBean(MoveDownAction.class));
        move.add(beanFactory.getBean(MoveToBottomAction.class));
        move.add(new JSeparator());
        move.add(beanFactory.getBean(ShufflePlayListAction.class));
        return move;
    }

    /**
     * Updates play list menu items
     * 
     * @param table
     */
	public void updatePlayListMenuItems() {
        updatePlayListPopupMenuItems(playListTable.getMenu(), playListHandler.getSelectedAudioObjects());
    }

    /**
     * Updates all actions of play list popup
     * 
     * @param menu
     * @param selection
     */
    private void updatePlayListPopupMenuItems(JPopupMenu menu, List<IAudioObject> selection) {
        for (Component c : menu.getComponents()) {
            Action action = null;
            if (c instanceof JMenuItem) {
                action = ((JMenuItem) c).getAction();
            }

            if (c instanceof JMenu) {
                updatePlayListMenuItems((JMenu) c, selection);
            }

            if (action instanceof net.sourceforge.atunes.kernel.actions.CustomAbstractAction) {
                boolean enabled = ((net.sourceforge.atunes.kernel.actions.CustomAbstractAction) action).isEnabledForPlayListSelection(selection);
                action.setEnabled(enabled);
            }
        }
    }

    /**
     * Updates all actions of play list popup menu
     * 
     * @param menu
     * @param selection
     */
    private void updatePlayListMenuItems(JMenu menu, List<IAudioObject> selection) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            // For some reason getItem can return null
            if (menuItem != null) {
                Action action = menuItem.getAction();

                if (menuItem instanceof JMenu) {
                    updatePlayListMenuItems((JMenu) menuItem, selection);
                }

                if (action instanceof net.sourceforge.atunes.kernel.actions.CustomAbstractAction) {
                    boolean enabled = ((net.sourceforge.atunes.kernel.actions.CustomAbstractAction) action).isEnabledForPlayListSelection(selection);
                    action.setEnabled(enabled);
                }
            }
        }
    }
}
