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

package net.sourceforge.atunes.kernel;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.actions.EditTagAction.EditTagSources;
import net.sourceforge.atunes.kernel.controllers.editTagDialog.EditTagDialogController;
import net.sourceforge.atunes.kernel.controllers.editTitlesDialog.EditTitlesDialogController;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.controllers.playList.PlayListController;
import net.sourceforge.atunes.kernel.controllers.playerControls.PlayerControlsController;
import net.sourceforge.atunes.kernel.controllers.radioBrowser.RadioBrowserDialogController;
import net.sourceforge.atunes.kernel.controllers.ripcd.RipCdDialogController;
import net.sourceforge.atunes.kernel.controllers.stats.StatsDialogController;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * Static class to access controllers by calling
 * ControllerProxy.getInstance().get<Name of controller>()
 */
public final class ControllerProxy {

    /** Logger. */
    private Logger logger;

    /** Singleton instance of controller. */
    private static ControllerProxy instance;

    /** The navigation controller. */
    private NavigationController navigationController;

    /** The play list controller. */
    private PlayListController playListController;

    /** The player controls controller. */
    private PlayerControlsController playerControlsController;

    /** The edit tag dialog controller. */
    private Map<EditTagSources, EditTagDialogController> editTagDialogControllerMap;

    /** The stats dialog controller. */
    private StatsDialogController statsDialogController;

    /** The edit titles dialog controller. */
    private EditTitlesDialogController editTitlesDialogController;

    /** The rip cd dialog controller. */
    private RipCdDialogController ripCdDialogController;

    /** The radio browser controller. */
    private RadioBrowserDialogController radioBrowserController;

    /**
     * Instantiates a new controller proxy.
     */
    private ControllerProxy() {
    }

    /**
     * Getter for singleton instance.
     * 
     * @return the instance
     */
    public static ControllerProxy getInstance() {
        if (instance == null) {
            instance = new ControllerProxy();
        }
        return instance;
    }

    /**
     * Gets the edits the tag dialog controller.
     * 
     * @return the edits the tag dialog controller
     */
    public EditTagDialogController getEditTagDialogController(EditTagSources sourceOfEditTagDialog) {
        if (editTagDialogControllerMap == null) {
            editTagDialogControllerMap = new HashMap<EditTagSources, EditTagDialogController>();
        }

        if (!editTagDialogControllerMap.containsKey(sourceOfEditTagDialog)) {
            boolean arePrevNextButtonsShown = sourceOfEditTagDialog != EditTagSources.NAVIGATOR;
            editTagDialogControllerMap.put(sourceOfEditTagDialog, new EditTagDialogController(GuiHandler.getInstance().getEditTagDialog(arePrevNextButtonsShown)));
        }
        return editTagDialogControllerMap.get(sourceOfEditTagDialog);
    }

    /**
     * Gets the edits the titles dialog controller.
     * 
     * @return the edits the titles dialog controller
     */
    public EditTitlesDialogController getEditTitlesDialogController() {
        if (editTitlesDialogController == null) {
            EditTitlesDialog dialog = GuiHandler.getInstance().getEditTitlesDialog();
            editTitlesDialogController = new EditTitlesDialogController(dialog);
        }
        return editTitlesDialogController;
    }

    /**
     * Gets the navigation controller.
     * 
     * @return the navigation controller
     */
    public NavigationController getNavigationController() {
        if (navigationController == null) {
            NavigationTreePanel treePanel = GuiHandler.getInstance().getNavigationTreePanel();
            NavigationTablePanel tablePanel = GuiHandler.getInstance().getNavigationTablePanel();
            navigationController = new NavigationController(treePanel, tablePanel);
        }
        return navigationController;
    }

    /**
     * Gets the player controls controller.
     * 
     * @return the player controls controller
     */
    public PlayerControlsController getPlayerControlsController() {
        if (playerControlsController == null) {
            PlayerControlsPanel panel = null;
            panel = GuiHandler.getInstance().getPlayerControls();
            playerControlsController = new PlayerControlsController(panel);
        }
        return playerControlsController;
    }

    /**
     * Gets the play list controller.
     * 
     * @return the play list controller
     */
    public PlayListController getPlayListController() {
        if (playListController == null) {
            PlayListPanel panel = null;
            panel = GuiHandler.getInstance().getPlayListPanel();
            playListController = new PlayListController(panel);
        }
        return playListController;
    }

    /**
     * Gets the rip cd dialog controller.
     * 
     * @return the rip cd dialog controller
     */
    public RipCdDialogController getRipCdDialogController() {
        if (ripCdDialogController == null) {
            ripCdDialogController = new RipCdDialogController(GuiHandler.getInstance().getRipCdDialog());
        }
        return ripCdDialogController;
    }

    /**
     * Gets the stats dialog controller.
     * 
     * @return the stats dialog controller
     */
    public StatsDialogController getStatsDialogController() {
        if (statsDialogController == null) {
            statsDialogController = new StatsDialogController(GuiHandler.getInstance().getStatsDialog());
        }
        return statsDialogController;
    }

    /**
     * Gets the radio browser controller.
     * 
     * @return the radio browser controller
     */
    public RadioBrowserDialogController getRadioBrowserController() {
        if (radioBrowserController == null) {
            radioBrowserController = new RadioBrowserDialogController(GuiHandler.getInstance().getRadioBrowserDialog());
        }
        return radioBrowserController;
    }
}
