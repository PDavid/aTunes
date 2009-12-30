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
package net.sourceforge.atunes.kernel.controllers.editPreferencesDialog;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.DefaultListModel;

import net.sourceforge.atunes.gui.views.dialogs.editPreferences.ContextPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.DevicePanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.EditPreferencesDialog;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.GeneralPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.ImportExportPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.InternetPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.LastFmPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.NavigatorPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.OSDPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PlayListPrefPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PlayerPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PluginsPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PodcastFeedPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PreferencesPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.RadioPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.RepositoryPanel;
import net.sourceforge.atunes.kernel.controllers.model.SimpleController;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;

public final class EditPreferencesDialogController extends SimpleController<EditPreferencesDialog> {

    /** The panels of the edit preferences dialog */
    PreferencesPanel[] panels = new PreferencesPanel[] { new GeneralPanel(), new RepositoryPanel(), new PlayerPanel(), new NavigatorPanel(), new PlayListPrefPanel(),
            new OSDPanel(), new ContextPanel(), new InternetPanel(), new LastFmPanel(), new DevicePanel(), new RadioPanel(), new PodcastFeedPanel(), new ImportExportPanel(),
            new PluginsPanel() };

    /**
     * Instantiates a new edits the preferences dialog controller.
     */
    public EditPreferencesDialogController() {
        super(GuiHandler.getInstance().getEditPreferencesDialog());
        getComponentControlled().setPanels(panels);
        buildList();
        addBindings();
    }

    @Override
    protected void addBindings() {
        EditPreferencesDialogListener listener = new EditPreferencesDialogListener(getComponentControlled(), this);
        getComponentControlled().getList().addListSelectionListener(listener);
        getComponentControlled().getCancel().addActionListener(listener);
        getComponentControlled().getOk().addActionListener(listener);
        getComponentControlled().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Call dialogVisibilityChanged
                for (PreferencesPanel panel : panels) {
                    panel.dialogVisibilityChanged(true);
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // Call dialogVisibilityChanged
                for (PreferencesPanel panel : panels) {
                    panel.dialogVisibilityChanged(false);
                }
            }
        });
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Builds the list.
     */
    private void buildList() {
        DefaultListModel listModel = new DefaultListModel();

        for (PreferencesPanel p : panels) {
            listModel.addElement(p);
        }

        getComponentControlled().setListModel(listModel);
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Checks if preferences of all panels are valid
     * 
     * @return
     */
    boolean arePreferencesValid() {
        for (PreferencesPanel p : panels) {
            if (!p.validatePanel()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Process preferences.
     * 
     * @return true if application needs to be restarted to apply some changes
     */
    boolean processPreferences() {
        boolean needRestart = false;
        // Apply preferences from panels
        for (PreferencesPanel p : panels) {
            needRestart = needRestart || p.applyPreferences(ApplicationState.getInstance());
        }
        return needRestart;
    }

    /**
     * reset immediate changes of panels
     */
    void resetImmediateChanges() {
        for (PreferencesPanel p : panels) {
            p.resetImmediateChanges(ApplicationState.getInstance());
        }
    }

    /**
     * Start.
     */
    public void start() {
        getLogger().debug(LogCategories.CONTROLLER);

        // Update panels
        for (PreferencesPanel panel : panels) {
            panel.updatePanel(ApplicationState.getInstance());
        }

        // Call dialogVisibilityChanged
        for (PreferencesPanel panel : panels) {
            panel.dialogVisibilityChanged(true);
        }

        getComponentControlled().getList().setSelectedIndex(0);
        getComponentControlled().setVisible(true);
    }
}
