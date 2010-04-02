/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddAlbumsMostPlayedAction;
import net.sourceforge.atunes.kernel.actions.AddArtistsMostPlayedAction;
import net.sourceforge.atunes.kernel.actions.AddRandomSongsAction;
import net.sourceforge.atunes.kernel.actions.AddSongsMostPlayedAction;
import net.sourceforge.atunes.kernel.actions.AddUnplayedSongsAction;
import net.sourceforge.atunes.kernel.actions.ClearPlayListAction;
import net.sourceforge.atunes.kernel.actions.CreatePlayListWithSelectedAlbumsAction;
import net.sourceforge.atunes.kernel.actions.CreatePlayListWithSelectedArtistsAction;
import net.sourceforge.atunes.kernel.actions.LoadPlayListAction;
import net.sourceforge.atunes.kernel.actions.MoveDownAction;
import net.sourceforge.atunes.kernel.actions.MoveToBottomAction;
import net.sourceforge.atunes.kernel.actions.MoveToTopAction;
import net.sourceforge.atunes.kernel.actions.MoveUpAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderAction;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromPlayListAction;
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteAlbumAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteArtistAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteSongAction;
import net.sourceforge.atunes.kernel.actions.ShowPlayListItemInfoAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public final class PlayListMenu {

    private PlayListMenu() {

    }

    /**
     * Fills a pop up with play list menu items
     * 
     * @param menu
     */
    public static void fillPopUpMenu(JPopupMenu menu, PlayListTable table) {
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
     */
    public static void fillMenu(JMenu menu, PlayListTable table) {
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
    private static List<Object> getComponents(PlayListTable table) {
        List<Object> objects = new ArrayList<Object>();
        objects.add(Actions.getAction(PlayAction.class));
        objects.add(Actions.getAction(ShowPlayListItemInfoAction.class));
        objects.add(new JSeparator());
        objects.add(Actions.getMenuItemForAction(OpenFolderAction.class, table));
        objects.add(new JSeparator());
        objects.add(new EditTagMenu(true, table));
        objects.add(new JSeparator());
        objects.add(Actions.getAction(SavePlayListAction.class));
        objects.add(Actions.getAction(LoadPlayListAction.class));
        objects.add(new JSeparator());
        objects.add(Actions.getAction(RemoveFromPlayListAction.class));
        objects.add(Actions.getAction(ClearPlayListAction.class));
        objects.add(new JSeparator());
        objects.add(Actions.getAction(MoveToTopAction.class));
        objects.add(Actions.getAction(MoveUpAction.class));
        objects.add(Actions.getAction(MoveDownAction.class));
        objects.add(Actions.getAction(MoveToBottomAction.class));
        objects.add(new JSeparator());
        objects.add(Actions.getAction(ShufflePlayListAction.class));
        objects.add(new JSeparator());
        JMenu smartPlayList = new JMenu(I18nUtils.getString("SMART_PLAYLIST"));
        smartPlayList.add(Actions.getAction(AddRandomSongsAction.class, AddRandomSongsAction.INSTANCE_10));
        smartPlayList.add(Actions.getAction(AddRandomSongsAction.class, AddRandomSongsAction.INSTANCE_50));
        smartPlayList.add(Actions.getAction(AddRandomSongsAction.class, AddRandomSongsAction.INSTANCE_100));
        smartPlayList.add(new JSeparator());
        smartPlayList.add(Actions.getAction(AddSongsMostPlayedAction.class, AddSongsMostPlayedAction.INSTANCE_10));
        smartPlayList.add(Actions.getAction(AddSongsMostPlayedAction.class, AddSongsMostPlayedAction.INSTANCE_50));
        smartPlayList.add(Actions.getAction(AddSongsMostPlayedAction.class, AddSongsMostPlayedAction.INSTANCE_100));
        smartPlayList.add(new JSeparator());
        smartPlayList.add(Actions.getAction(AddAlbumsMostPlayedAction.class, AddAlbumsMostPlayedAction.INSTANCE_1));
        smartPlayList.add(Actions.getAction(AddAlbumsMostPlayedAction.class, AddAlbumsMostPlayedAction.INSTANCE_5));
        smartPlayList.add(Actions.getAction(AddAlbumsMostPlayedAction.class, AddAlbumsMostPlayedAction.INSTANCE_10));
        smartPlayList.add(new JSeparator());
        smartPlayList.add(Actions.getAction(AddArtistsMostPlayedAction.class, AddArtistsMostPlayedAction.INSTANCE_1));
        smartPlayList.add(Actions.getAction(AddArtistsMostPlayedAction.class, AddArtistsMostPlayedAction.INSTANCE_5));
        smartPlayList.add(Actions.getAction(AddArtistsMostPlayedAction.class, AddArtistsMostPlayedAction.INSTANCE_10));
        smartPlayList.add(new JSeparator());
        smartPlayList.add(Actions.getAction(AddUnplayedSongsAction.class, AddUnplayedSongsAction.INSTANCE_10));
        smartPlayList.add(Actions.getAction(AddUnplayedSongsAction.class, AddUnplayedSongsAction.INSTANCE_50));
        smartPlayList.add(Actions.getAction(AddUnplayedSongsAction.class, AddUnplayedSongsAction.INSTANCE_100));
        objects.add(smartPlayList);
        objects.add(new JSeparator());
        objects.add(Actions.getAction(SetPlayListSelectionAsFavoriteSongAction.class));
        objects.add(Actions.getAction(SetPlayListSelectionAsFavoriteAlbumAction.class));
        objects.add(Actions.getAction(SetPlayListSelectionAsFavoriteArtistAction.class));
        objects.add(new JSeparator());
        objects.add(Actions.getMenuItemForAction(CreatePlayListWithSelectedArtistsAction.class, table));
        objects.add(Actions.getMenuItemForAction(CreatePlayListWithSelectedAlbumsAction.class, table));
        return objects;
    }

    /**
     * Updates play list menu items
     * 
     * @param table
     */
    public static void updatePlayListMenuItems(PlayListTable table) {
        updatePlayListPopupMenuItems(table.getMenu(), PlayListHandler.getInstance().getSelectedAudioObjects());
    }

    /**
     * Updates all actions of play list popup
     * 
     * @param menu
     * @param selection
     */
    private static void updatePlayListPopupMenuItems(JPopupMenu menu, List<AudioObject> selection) {
        for (Component c : menu.getComponents()) {
            Action action = null;
            if (c instanceof JMenuItem) {
                action = ((JMenuItem) c).getAction();
            }

            if (c instanceof JMenu) {
                updatePlayListMenuItems((JMenu) c, selection);
            }

            if (action != null && action instanceof net.sourceforge.atunes.kernel.actions.AbstractAction) {
                boolean enabled = ((net.sourceforge.atunes.kernel.actions.AbstractAction) action).isEnabledForPlayListSelection(selection);
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
    private static void updatePlayListMenuItems(JMenu menu, List<AudioObject> selection) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            // For some reason getItem can return null
            if (menuItem != null) {
                Action action = menuItem.getAction();

                if (menuItem instanceof JMenu) {
                    updatePlayListMenuItems((JMenu) menuItem, selection);
                }

                if (action != null && action instanceof net.sourceforge.atunes.kernel.actions.AbstractAction) {
                    boolean enabled = ((net.sourceforge.atunes.kernel.actions.AbstractAction) action).isEnabledForPlayListSelection(selection);
                    action.setEnabled(enabled);
                }
            }
        }
    }

}
