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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.StateChangeListeners;
import net.sourceforge.atunes.model.IConfirmationDialogFactory;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The listener interface for receiving editPreferencesDialog events.
 */
public final class EditPreferencesDialogListener implements ListSelectionListener, ActionListener {

    private EditPreferencesDialog editPreferencesDialog;
    private EditPreferencesDialogController editPreferencesDialogController;
    private StateChangeListeners listeners;

    /**
     * Instantiates a new edits the preferences dialog listener.
     * 
     * @param editPreferencesDialog
     * @param editPreferencesDialogController
     * @param listeners
     */
    public EditPreferencesDialogListener(EditPreferencesDialog editPreferencesDialog, 
    								     EditPreferencesDialogController editPreferencesDialogController,
    								     StateChangeListeners listeners) {
        this.editPreferencesDialog = editPreferencesDialog;
        this.editPreferencesDialogController = editPreferencesDialogController;
        this.listeners = listeners;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == editPreferencesDialog.getOk()) {
        	try {
            	editPreferencesDialogController.validatePreferences();
	        	boolean needRestart = editPreferencesDialogController.processPreferences();
	        	editPreferencesDialog.setVisible(false);
	        	listeners.notifyApplicationStateChanged();
        		// Let user decide if want to restart
	        	if (needRestart && Context.getBean(IConfirmationDialogFactory.class).getDialog().showDialog(I18nUtils.getString("APPLICATION_NEEDS_RESTART"))) {
	        		Context.getBean(IKernel.class).restart();
	        	}
			} catch (PreferencesValidationException e1) {
				Context.getBean(IErrorDialogFactory.class).getDialog().showErrorDialog(e1.getMessage(), editPreferencesDialog);
			}
        } else if (e.getSource() == editPreferencesDialog.getCancel()) {
            editPreferencesDialogController.resetImmediateChanges();
            editPreferencesDialog.setVisible(false);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == editPreferencesDialog.getList() && !e.getValueIsAdjusting()) {
            editPreferencesDialog.showPanel(editPreferencesDialog.getList().getSelectedIndex());
        }
    }

}
