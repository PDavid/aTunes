/*
 * aTunes 3.0.0
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

import net.sourceforge.atunes.kernel.StateChangeListeners;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The listener interface for receiving editPreferencesDialog events.
 */
public final class EditPreferencesDialogListener implements
	ListSelectionListener, ActionListener {

    private final EditPreferencesDialog editPreferencesDialog;
    private final EditPreferencesDialogController editPreferencesDialogController;
    private final IBeanFactory beanFactory;

    /**
     * Instantiates a new edits the preferences dialog listener.
     * 
     * @param editPreferencesDialog
     * @param editPreferencesDialogController
     * @param beanFactory
     */
    public EditPreferencesDialogListener(
	    final EditPreferencesDialog editPreferencesDialog,
	    final EditPreferencesDialogController editPreferencesDialogController,
	    final IBeanFactory beanFactory) {
	this.editPreferencesDialog = editPreferencesDialog;
	this.editPreferencesDialogController = editPreferencesDialogController;
	this.beanFactory = beanFactory;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	if (e.getSource() == editPreferencesDialog.getOk()) {
	    try {
		editPreferencesDialogController.validatePreferences();
		boolean needRestart = editPreferencesDialogController
			.processPreferences();
		editPreferencesDialog.setVisible(false);
		beanFactory.getBean(StateChangeListeners.class)
			.notifyApplicationStateChanged();
		// Let user decide if want to restart
		if (needRestart) {
		    IConfirmationDialog dialog = beanFactory.getBean(
			    IDialogFactory.class).newDialog(
			    IConfirmationDialog.class);
		    dialog.setMessage(I18nUtils
			    .getString("APPLICATION_NEEDS_RESTART"));
		    dialog.showDialog();
		    if (dialog.userAccepted()) {
			beanFactory.getBean(IKernel.class).restart();
		    }
		}
	    } catch (PreferencesValidationException e1) {
		beanFactory
			.getBean(IDialogFactory.class)
			.newDialog(IErrorDialog.class)
			.showErrorDialog(e1.getMessage(), editPreferencesDialog);
	    }
	} else if (e.getSource() == editPreferencesDialog.getCancel()) {
	    editPreferencesDialogController.resetImmediateChanges();
	    editPreferencesDialog.setVisible(false);
	}
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
	if (e.getSource() == editPreferencesDialog.getList()
		&& !e.getValueIsAdjusting()) {
	    editPreferencesDialog.showPanel(editPreferencesDialog.getList()
		    .getSelectedIndex());
	}
    }
}
