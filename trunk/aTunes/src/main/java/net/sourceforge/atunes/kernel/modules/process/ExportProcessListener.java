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

package net.sourceforge.atunes.kernel.modules.process;

import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.utils.I18nUtils;

class ExportProcessListener implements
		IProcessListener<List<ILocalAudioObject>> {

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	public void processCanceled() {
		// Nothing to do
	}

	@Override
	public void processFinished(final boolean ok,
			final List<ILocalAudioObject> result) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!ok) {
					ExportProcessListener.this.dialogFactory.newDialog(
							IErrorDialog.class).showErrorDialog(
							I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
				}
			}
		});
	}
}