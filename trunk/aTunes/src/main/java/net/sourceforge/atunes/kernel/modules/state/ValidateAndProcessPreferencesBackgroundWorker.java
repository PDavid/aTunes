/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorkerWithIndeterminateProgress;
import net.sourceforge.atunes.kernel.StateChangeListeners;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

public class ValidateAndProcessPreferencesBackgroundWorker
		extends
		BackgroundWorkerWithIndeterminateProgress<PreferencesValidationResult, Void> {

	private List<AbstractPreferencesPanel> panels;

	private EditPreferencesDialog preferencesDialog;

	/**
	 * Validates panels and process preferences if valid
	 * 
	 * @param preferencesDialog
	 * @param panels
	 */
	void validateAndProcessPreferences(EditPreferencesDialog preferencesDialog,
			List<AbstractPreferencesPanel> panels) {
		this.preferencesDialog = preferencesDialog;
		this.panels = panels;
		execute();
	}

	@Override
	protected String getDialogTitle() {
		return I18nUtils.getString("VALIDATING_PREFERENCES");
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected PreferencesValidationResult doInBackground() {
		PreferencesValidationResult result = new PreferencesValidationResult();
		try {
			for (AbstractPreferencesPanel p : panels) {
				p.validatePanel();
			}
			// No exception, preferences are valid
			// Start processing them
			result.setNeedsRestart(processPreferences());
		} catch (PreferencesValidationException e) {
			result.setError(e);
		}
		return result;
	}

	@Override
	protected void doneAndDialogClosed(PreferencesValidationResult result) {
		if (!result.hasError()) {
			preferencesDialog.setVisible(false);
			getBeanFactory().getBean(StateChangeListeners.class)
					.notifyApplicationStateChanged();
			// Let user decide if want to restart
			if (result.isNeedsRestart()) {
				IConfirmationDialog dialog = getDialogFactory().newDialog(
						IConfirmationDialog.class);
				dialog.setMessage(I18nUtils
						.getString("APPLICATION_NEEDS_RESTART"));
				dialog.showDialog();
				if (dialog.userAccepted()) {
					getBeanFactory().getBean(IKernel.class).restart();
				}
			}
		} else {
			getDialogFactory().newDialog(IErrorDialog.class).showErrorDialog(
					result.getError().getMessage(), preferencesDialog);
		}
	}

	/**
	 * Process preferences.
	 * 
	 * @return true if application needs to be restarted to apply some changes
	 */
	private boolean processPreferences() {
		boolean needRestart = false;
		// Apply preferences from panels
		for (AbstractPreferencesPanel p : panels) {
			if (p.isDirty()) {
				Logger.debug("Panel ", p.getTitle(), " is dirty");
				// WARNING: There was a bug when call to applyPreferences was
				// made as second operand of OR due to shortcut
				// So call method and after do OR (method call as first operand
				// is also valid)
				// See bug
				// https://sourceforge.net/tracker/?func=detail&aid=2999531&group_id=161929&atid=821812
				// for more information
				boolean panelNeedRestart = p.applyPreferences();
				needRestart = needRestart || panelNeedRestart;
			} else {
				Logger.debug("Panel ", p.getTitle(), " is clean");
			}
		}
		return needRestart;
	}
}
