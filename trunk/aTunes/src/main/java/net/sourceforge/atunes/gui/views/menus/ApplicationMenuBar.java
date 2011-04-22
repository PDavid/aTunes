/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import net.sourceforge.atunes.kernel.actions.AbstractAction;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddPodcastFeedAction;
import net.sourceforge.atunes.kernel.actions.AddRadioAction;
import net.sourceforge.atunes.kernel.actions.CheckUpdatesAction;
import net.sourceforge.atunes.kernel.actions.CollapseTreesAction;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.CustomSearchAction;
import net.sourceforge.atunes.kernel.actions.DisconnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditPreferencesAction;
import net.sourceforge.atunes.kernel.actions.ExitAction;
import net.sourceforge.atunes.kernel.actions.ExpandTreesAction;
import net.sourceforge.atunes.kernel.actions.ExportAction;
import net.sourceforge.atunes.kernel.actions.FullScreenAction;
import net.sourceforge.atunes.kernel.actions.GoToWebSiteAction;
import net.sourceforge.atunes.kernel.actions.GoToWikiAction;
import net.sourceforge.atunes.kernel.actions.ImportLovedTracksFromLastFMAction;
import net.sourceforge.atunes.kernel.actions.ImportToRepositoryAction;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.actions.OSDSettingAction;
import net.sourceforge.atunes.kernel.actions.RefreshDeviceAction;
import net.sourceforge.atunes.kernel.actions.RefreshRepositoryAction;
import net.sourceforge.atunes.kernel.actions.RepairAlbumNamesAction;
import net.sourceforge.atunes.kernel.actions.RepairGenresAction;
import net.sourceforge.atunes.kernel.actions.RepairTrackNumbersAction;
import net.sourceforge.atunes.kernel.actions.ReportBugOrFeatureRequestAction;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.actions.SelectRepositoryAction;
import net.sourceforge.atunes.kernel.actions.ShowAboutAction;
import net.sourceforge.atunes.kernel.actions.ShowAlbumsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowArtistsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowAudioObjectPropertiesAction;
import net.sourceforge.atunes.kernel.actions.ShowContextAction;
import net.sourceforge.atunes.kernel.actions.ShowCoverNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowEqualizerAction;
import net.sourceforge.atunes.kernel.actions.ShowFoldersInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowGenresInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowLogAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigationTreeAction;
import net.sourceforge.atunes.kernel.actions.ShowRadioBrowserAction;
import net.sourceforge.atunes.kernel.actions.ShowStatsAction;
import net.sourceforge.atunes.kernel.actions.ShowStatusBarAction;
import net.sourceforge.atunes.kernel.actions.ShowToolbarAction;
import net.sourceforge.atunes.kernel.actions.ShowYearsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.VolumeDownAction;
import net.sourceforge.atunes.kernel.actions.VolumeUpAction;
import net.sourceforge.atunes.kernel.modules.navigator.AbstractNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The application menu bar.
 */
public final class ApplicationMenuBar extends JMenuBar {

    private static final long serialVersionUID = 234977404080329591L;

    private JMenu file;
    private JMenu edit;
    private JMenu view;
    private JMenu navigator;
    private JMenu playList;
    private JMenu tools;
    private JMenu device;
    private JMenu help;

    /**
     * Instantiates a new application menu bar.
     */
    public ApplicationMenuBar() {
        super();
        addMenus();
    }

    /**
     * Creates "File" menu
     * 
     * @return
     */
    private JMenu getFileMenu() {
        if (file == null) {
            file = new JMenu(I18nUtils.getString("FILE"));
            file.add(Actions.getAction(SelectRepositoryAction.class));
            file.add(Actions.getAction(RefreshRepositoryAction.class));
            file.add(new JSeparator());
            file.add(Actions.getAction(ImportToRepositoryAction.class));
            file.add(Actions.getAction(ExportAction.class));
            file.add(new JSeparator());
            file.add(Actions.getAction(ExitAction.class));
        }
        return file;
    }

    /**
     * Creates "Edit" menu
     * 
     * @return
     */
    private JMenu getEditMenu() {
        if (edit == null) {
            edit = new JMenu(I18nUtils.getString("EDIT"));
            JMenu player = new JMenu(I18nUtils.getString("VOLUME"));
            JMenuItem showEqual = new JMenuItem(Actions.getAction(ShowEqualizerAction.class));
            player.add(showEqual);
            if (!PlayerHandler.getInstance().supportsCapability(PlayerEngineCapability.EQUALIZER)) {
                showEqual.setEnabled(false);
            }
            player.add(Actions.getAction(VolumeUpAction.class));
            player.add(Actions.getAction(VolumeDownAction.class));
            player.add(new JCheckBoxMenuItem(Actions.getAction(MuteAction.class)));
            JMenu repair = new JMenu(I18nUtils.getString("REPAIR"));
            repair.add(Actions.getAction(RepairTrackNumbersAction.class));
            repair.add(Actions.getAction(RepairGenresAction.class));
            repair.add(Actions.getAction(RepairAlbumNamesAction.class));
            edit.add(player);
            edit.add(Actions.getAction(EditPreferencesAction.class));
            edit.add(new JSeparator());
            edit.add(repair);
        }
        return edit;
    }

    /**
     * Creates "View" menu
     * 
     * @return
     */
    private JMenu getViewMenu() {
        if (view == null) {
            view = new JMenu(I18nUtils.getString("VIEW"));

            // Add dinamically actions to show each navigation view loaded
            int acceleratorIndex = 1;
            for (AbstractNavigationView navigationView : NavigationHandler.getInstance().getNavigationViews()) {
            	AbstractAction action = navigationView.getActionToShowView(); 
        		// The first 9 views will have an accelerator key ALT + index
        		if (acceleratorIndex < 10) {
        			action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_0 + acceleratorIndex, ActionEvent.ALT_MASK));
        		}
    			acceleratorIndex++;
                view.add(action);
            }

            view.add(new JSeparator());
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowToolbarAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowStatusBarAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowNavigationTreeAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowNavigationTableAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowAudioObjectPropertiesAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowContextAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(OSDSettingAction.class)));
            view.add(new JSeparator());
            view.add(Actions.getAction(FullScreenAction.class));
        }
        return view;
    }

    /**
     * Creates "Navigator" menu
     * 
     * @return
     */
    private JMenu getNavigatorMenu() {
        if (navigator == null) {
            navigator = new JMenu(I18nUtils.getString("NAVIGATOR"));
            JRadioButtonMenuItem showArtist = new JRadioButtonMenuItem(Actions.getAction(ShowArtistsInNavigatorAction.class));
            JRadioButtonMenuItem showAlbum = new JRadioButtonMenuItem(Actions.getAction(ShowAlbumsInNavigatorAction.class));
            JRadioButtonMenuItem showGenre = new JRadioButtonMenuItem(Actions.getAction(ShowGenresInNavigatorAction.class));
            JRadioButtonMenuItem showYear = new JRadioButtonMenuItem(Actions.getAction(ShowYearsInNavigatorAction.class));
            JRadioButtonMenuItem showFolder = new JRadioButtonMenuItem(Actions.getAction(ShowFoldersInNavigatorAction.class));
            ButtonGroup group = new ButtonGroup();
            group.add(showArtist);
            group.add(showAlbum);
            group.add(showGenre);
            group.add(showYear);
            group.add(showFolder);
            navigator.add(showArtist);
            navigator.add(showAlbum);
            navigator.add(showGenre);
            navigator.add(showYear);
            navigator.add(showFolder);
            navigator.add(new JSeparator());
            navigator.add(Actions.getAction(ExpandTreesAction.class));
            navigator.add(Actions.getAction(CollapseTreesAction.class));
        }
        return navigator;
    }

    /**
     * Creates "Playlist" menu
     * 
     * @return
     */
    private JMenu getPlayListMenu() {
        if (playList == null) {
            playList = new JMenu(I18nUtils.getString("PLAYLIST"));
            PlayListMenu.fillMenu(playList, PlayListHandler.getInstance().getPlayListTable());
        }
        return playList;
    }

    /**
     * Creates "Tools" menu
     * 
     * @return
     */
    private JMenu getToolsMenu() {
        if (tools == null) {
            tools = new JMenu(I18nUtils.getString("TOOLS"));
            tools.add(Actions.getAction(RipCDAction.class));
            tools.add(new JSeparator());
            tools.add(Actions.getAction(ShowStatsAction.class));
            tools.add(Actions.getAction(ShowCoverNavigatorAction.class));
            tools.add(new JSeparator());
            tools.add(Actions.getAction(AddRadioAction.class));
            tools.add(Actions.getAction(ShowRadioBrowserAction.class));
            tools.add(Actions.getAction(AddPodcastFeedAction.class));
            tools.add(new JSeparator());
            tools.add(Actions.getAction(CustomSearchAction.class));
            tools.add(new JSeparator());
            tools.add(Actions.getAction(ImportLovedTracksFromLastFMAction.class));
        }
        return tools;
    }

    /**
     * Creates "Device" menu
     * 
     * @return
     */
    private JMenu getDeviceMenu() {
        if (device == null) {
            device = new JMenu(I18nUtils.getString("DEVICE"));
            device.add(Actions.getAction(ConnectDeviceAction.class));
            device.add(Actions.getAction(RefreshDeviceAction.class));
            device.add(Actions.getAction(DisconnectDeviceAction.class));
        }
        return device;
    }

    /**
     * Creates "Help" menu
     * 
     * @return
     */
    private JMenu getCustomHelpMenu() {
        if (help == null) {
            help = new JMenu(I18nUtils.getString("HELP"));
            help.add(Actions.getAction(GoToWebSiteAction.class));
            help.add(Actions.getAction(GoToWikiAction.class));
            help.add(Actions.getAction(ReportBugOrFeatureRequestAction.class));
            help.add(new JSeparator());
            help.add(Actions.getAction(ShowLogAction.class));
            help.add(Actions.getAction(CheckUpdatesAction.class));
            help.add(new JSeparator());
            help.add(Actions.getAction(ShowAboutAction.class));
        }
        return help;
    }

    /**
     * Adds the menus.
     */
    private void addMenus() {
        add(getFileMenu());
        add(getEditMenu());
        add(getViewMenu());
        add(getNavigatorMenu());
        add(getPlayListMenu());
        add(getDeviceMenu());
        add(getToolsMenu());
        add(getCustomHelpMenu());
    }

    /**
     * Adds a menu before "Help" menu
     * 
     * @param newMenu
     */
    public void addMenu(JMenu newMenu) {
        remove(getComponentCount() - 1);
        add(newMenu);
        add(getCustomHelpMenu());
    }
}
