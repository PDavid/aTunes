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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddPodcastFeedAction;
import net.sourceforge.atunes.kernel.actions.AddRadioAction;
import net.sourceforge.atunes.kernel.actions.CheckUpdatesAction;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.CustomAbstractAction;
import net.sourceforge.atunes.kernel.actions.CustomSearchAction;
import net.sourceforge.atunes.kernel.actions.DisconnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditPreferencesAction;
import net.sourceforge.atunes.kernel.actions.ExitAction;
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
import net.sourceforge.atunes.kernel.actions.ShowContextAction;
import net.sourceforge.atunes.kernel.actions.ShowCoverNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowEqualizerAction;
import net.sourceforge.atunes.kernel.actions.ShowLogAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigationTreeAction;
import net.sourceforge.atunes.kernel.actions.ShowRadioBrowserAction;
import net.sourceforge.atunes.kernel.actions.ShowStatsAction;
import net.sourceforge.atunes.kernel.actions.ShowStatusBarAction;
import net.sourceforge.atunes.kernel.actions.VolumeDownAction;
import net.sourceforge.atunes.kernel.actions.VolumeUpAction;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IMenuBar;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The application menu bar.
 */
public final class ApplicationMenuBar extends JMenuBar implements IMenuBar {

    private static final long serialVersionUID = 234977404080329591L;

    private JMenu file;
    private JMenu edit;
    private JMenu view;
    private JMenu playList;
    private JMenu tools;
    private JMenu device;
    private JMenu help;
    
    private IOSManager osManager;
        /**
     * Instantiates a new application menu bar.
     * @param osManager
     */
    public ApplicationMenuBar(IOSManager osManager) {
        super();
        this.osManager = osManager;
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
            if (!osManager.areMenuEntriesDelegated()) {
            	file.add(new JSeparator());
            	file.add(Actions.getAction(ExitAction.class));
            }
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
            if (!osManager.areMenuEntriesDelegated()) {
            	edit.add(Actions.getAction(EditPreferencesAction.class));
            }
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
            for (INavigationView navigationView : Context.getBean(INavigationHandler.class).getNavigationViews()) {
            	CustomAbstractAction action = navigationView.getActionToShowView(); 
        		// The first 9 views will have an accelerator key ALT + index
        		if (acceleratorIndex < 10) {
        			action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_0 + acceleratorIndex, ActionEvent.ALT_MASK));
        		}
    			acceleratorIndex++;
                view.add(action);
            }

            view.add(new JSeparator());
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowStatusBarAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowNavigationTreeAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(ShowContextAction.class)));
            view.add(new JCheckBoxMenuItem(Actions.getAction(OSDSettingAction.class)));
            view.add(new JSeparator());
            view.add(Actions.getAction(FullScreenAction.class));
        }
        return view;
    }

    /**
     * Creates "Playlist" menu
     * 
     * @return
     */
    private JMenu getPlayListMenu() {
        if (playList == null) {
            playList = new JMenu(I18nUtils.getString("PLAYLIST"));
            PlayListMenu.fillMenu(playList, Context.getBean(IFrame.class).getPlayListTable());
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
            if (!osManager.areMenuEntriesDelegated()) {
            	help.add(new JSeparator());
            	help.add(Actions.getAction(ShowAboutAction.class));
            }
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
        add(getPlayListMenu());
        add(getDeviceMenu());
        add(getToolsMenu());
        add(getCustomHelpMenu());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.menus.IMenuBar#addMenu(javax.swing.JMenu)
	 */
    @Override
	public void addMenu(JMenu newMenu) {
        remove(getComponentCount() - 1);
        add(newMenu);
        add(getCustomHelpMenu());
    }
}
