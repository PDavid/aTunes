/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.views.dialogs.editPreferences.EditPreferencesDialog;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * The listener interface for receiving editPreferencesDialog events.
 */
public class EditPreferencesDialogListener implements ListSelectionListener, ActionListener {

    /** The edit preferences dialog. */
    private EditPreferencesDialog editPreferencesDialog;

    /** The edit preferences dialog controller. */
    private EditPreferencesDialogController editPreferencesDialogController;

    /**
     * Instantiates a new edits the preferences dialog listener.
     * 
     * @param editPreferencesDialog
     *            the edit preferences dialog
     * @param editPreferencesDialogController
     *            the edit preferences dialog controller
     */
    public EditPreferencesDialogListener(EditPreferencesDialog editPreferencesDialog, EditPreferencesDialogController editPreferencesDialogController) {
        this.editPreferencesDialog = editPreferencesDialog;
        this.editPreferencesDialogController = editPreferencesDialogController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean needRestart = false;
        if (e.getSource() == editPreferencesDialog.getOk()) {
            if (editPreferencesDialogController.arePreferencesValid()) {
                needRestart = editPreferencesDialogController.processPreferences();
                editPreferencesDialog.setVisible(false);
                ApplicationStateHandler.getInstance().notifyApplicationStateChanged();
                if (needRestart) {
                    // Let user decide if want to restart
                    int result = VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("APPLICATION_NEEDS_RESTART"), LanguageTool.getString("CONFIRMATION"));
                    if (result == JOptionPane.OK_OPTION) {
                        Kernel.getInstance().restart();
                    }
                }
            }
        } else if (e.getSource() == editPreferencesDialog.getCancel()) {
            editPreferencesDialog.setVisible(false);
        } else if (e.getSource() == editPreferencesDialog.getContextPanel().getClearCache()) {
            ContextHandler.getInstance().clearCaches();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
     * .ListSelectionEvent)
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == editPreferencesDialog.getList()) {
            editPreferencesDialog.showPanel(editPreferencesDialog.getList().getSelectedIndex());
        }

    }

}
