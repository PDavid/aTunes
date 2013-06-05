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

package net.sourceforge.atunes.kernel;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;

/**
 * Abstract class to help creation of background workers using indeterminate
 * progress
 * 
 * @author alex
 * @param <T>
 * @param <I>
 */
public abstract class BackgroundWorkerWithIndeterminateProgress<T, I> extends
		BackgroundWorker<T, I> {

	private IDialogFactory dialogFactory;

	private IIndeterminateProgressDialog dialog;

	/**
	 * @return dialog factory
	 */
	protected IDialogFactory getDialogFactory() {
		return dialogFactory;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
		this.dialog = dialogFactory
				.newDialog(IIndeterminateProgressDialog.class);
		this.dialog.setTitle(getDialogTitle());
	}

	@Override
	protected final void before() {
		this.dialog.showDialog();
	}

	@Override
	protected final void done(T result) {
		this.dialog.hideDialog();
		doneAndDialogClosed(result);
	}

	/**
	 * @return dialog title to use
	 */
	protected abstract String getDialogTitle();

	/**
	 * Called when dialog closed
	 * 
	 * @param result
	 */
	protected abstract void doneAndDialogClosed(T result);
}
