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

import net.sourceforge.atunes.Context;
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
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteAlbumAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteArtistAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteSongAction;
import net.sourceforge.atunes.kernel.actions.ShowPlayListItemInfoAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.utils.I18nUtils;

public final class PlayListMenuFiller {

    private PlayListMenuFiller() {
    }

    /**
     * Fills a pop up with play list menu items
     * 
     * @param menu
     */
    static void fillPopUpMenu(JPopupMenu menu, IPlayListTable table) {
        List<Object> objectsToAdd = getComponents(table);
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
     * @param table
     */
    static void fillMenu(JMenu menu, IPlayListTable table) {
        List<Object> objectsToAdd = getComponents(table);
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
    private static List<Object> getComponents(IPlayListTable table) {
        List<Object> objects = new ArrayList<Object>();
        
        objects.add(Context.getBean(ShowPlayListItemInfoAction.class));
        
        OpenFolderAction openFolderAction = Context.getBean(OpenFolderAction.class);
        openFolderAction.setAudioObjectsSource(table);
        objects.add(openFolderAction);
        
        objects.add(new JSeparator());
        objects.add(new EditTagMenu(true, table));
        objects.add(getFavoritesMenu());
        objects.add(new JSeparator());
        objects.add(Context.getBean(AutoScrollPlayListAction.class));
        objects.add(getMoveMenu());
        objects.add(Context.getBean(RemoveFromPlayListAction.class));
        objects.add(Context.getBean(RemoveDuplicatesFromPlayListAction.class));
        objects.add(Context.getBean(ClearPlayListAction.class));
        objects.add(new JSeparator());
        objects.add(Context.getBean(SavePlayListAction.class));
        objects.add(Context.getBean(LoadPlayListAction.class));
        objects.add(new JSeparator());
        objects.add(getSmartPlayListMenu());
        objects.add(new JSeparator());
        
        AbstractActionOverSelectedObjects<IAudioObject> createPlayListWithSelectedArtistsAction = Context.getBean(CreatePlayListWithSelectedArtistsAction.class);
        createPlayListWithSelectedArtistsAction.setAudioObjectsSource(table);
        objects.add(createPlayListWithSelectedArtistsAction);
        
        AbstractActionOverSelectedObjects<IAudioObject> createPlayListWithSelectedAlbumAction = Context.getBean(CreatePlayListWithSelectedAlbumsAction.class);
        createPlayListWithSelectedAlbumAction.setAudioObjectsSource(table);
        objects.add(createPlayListWithSelectedAlbumAction);
        
        AbstractActionOverSelectedObjects<IAudioObject> addAlbumWithSelectedArtistsAction = Context.getBean(AddAlbumWithSelectedArtistsAction.class);
        addAlbumWithSelectedArtistsAction.setAudioObjectsSource(table);
        objects.add(addAlbumWithSelectedArtistsAction);
        return objects;
    }
    
    /**
     * Returns menu for smart play list
     * @return
     */
    private static JMenu getSmartPlayListMenu() {
        JMenu smartPlayList = new JMenu(I18nUtils.getString("SMART_PLAYLIST"));
        smartPlayList.add((AbstractAction)Context.getBean("addRandomSongsAction10"));
        smartPlayList.add((AbstractAction)Context.getBean("addRandomSongsAction50"));
        smartPlayList.add((AbstractAction)Context.getBean("addRandomSongsAction100"));
        smartPlayList.add(new JSeparator());
        smartPlayList.add((AbstractAction)Context.getBean("addSongsMostPlayedAction10"));
        smartPlayList.add((AbstractAction)Context.getBean("addSongsMostPlayedAction50"));
        smartPlayList.add((AbstractAction)Context.getBean("addSongsMostPlayedAction100"));
        smartPlayList.add(new JSeparator());
        smartPlayList.add((AbstractAction)Context.getBean("addAlbumMostPlayedAction1"));
        smartPlayList.add((AbstractAction)Context.getBean("addAlbumMostPlayedAction5"));
        smartPlayList.add((AbstractAction)Context.getBean("addAlbumMostPlayedAction10"));
        smartPlayList.add(new JSeparator());
        smartPlayList.add((AbstractAction)Context.getBean("addArtistsMostPlayedAction1"));
        smartPlayList.add((AbstractAction)Context.getBean("addArtistsMostPlayedAction5"));
        smartPlayList.add((AbstractAction)Context.getBean("addArtistsMostPlayedAction10"));
        smartPlayList.add(new JSeparator());
        smartPlayList.add((AbstractAction)Context.getBean("addUnplayedSongsAction10"));
        smartPlayList.add((AbstractAction)Context.getBean("addUnplayedSongsAction50"));
        smartPlayList.add((AbstractAction)Context.getBean("addUnplayedSongsAction100"));
        return smartPlayList;
    }
    
    /**
     * Returns menu for favorites
     * @return
     */
    private static JMenu getFavoritesMenu() {
        JMenu favorites = new JMenu(I18nUtils.getString("FAVORITES"));
        favorites.add(Context.getBean(SetPlayListSelectionAsFavoriteSongAction.class));
        favorites.add(Context.getBean(SetPlayListSelectionAsFavoriteAlbumAction.class));
        favorites.add(Context.getBean(SetPlayListSelectionAsFavoriteArtistAction.class));
        return favorites;
    }
    
    /**
     * Returns move menu
     * @return
     */
    private static JMenu getMoveMenu() {
        JMenu move = new JMenu(I18nUtils.getString("MOVE"));
        move.add(Context.getBean(MoveAfterCurrentAudioObjectAction.class));
        move.add(new JSeparator());
        move.add(Context.getBean(MoveToTopAction.class));
        move.add(Context.getBean(MoveUpAction.class));
        move.add(Context.getBean(MoveDownAction.class));
        move.add(Context.getBean(MoveToBottomAction.class));
        move.add(new JSeparator());
        move.add(Context.getBean(ShufflePlayListAction.class));
        return move;
    }

    /**
     * Updates play list menu items
     * 
     * @param table
     */
	public static void updatePlayListMenuItems(IPlayListTable table) {
        updatePlayListPopupMenuItems(table.getMenu(), Context.getBean(IPlayListHandler.class).getSelectedAudioObjects());
    }

    /**
     * Updates all actions of play list popup
     * 
     * @param menu
     * @param selection
     */
    private static void updatePlayListPopupMenuItems(JPopupMenu menu, List<IAudioObject> selection) {
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
    private static void updatePlayListMenuItems(JMenu menu, List<IAudioObject> selection) {
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
