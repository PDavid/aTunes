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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IProcess;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * A Process represents a task to be done in background. While task is being
 * executed it updates a progress dialog. Also represents a common and easy way
 * to implement new background processes extending this class
 * 
 * @author alex
 * 
 * @param <T>
 */
public abstract class AbstractProcess<T> implements Runnable, IProcess<T> {

	/**
	 * List of listeners notified when Process ends or is canceled
	 */
	private volatile List<IProcessListener<T>> listeners;

	/**
	 * Flag indicating if process has been canceled
	 */
	private volatile boolean canceled = false;

	/**
	 * Size of this process. This can be for example the total number of files
	 * to copy, to delete, ...
	 */
	private volatile long processSize;

	/**
	 * The dialog used to show the progress of this process
	 */
	private IProgressDialog progressDialog;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * Adds a listener to this process
	 * 
	 * @param listener
	 */
	@Override
	public final void addProcessListener(IProcessListener<T> listener) {
		if (listeners == null) {
			listeners = new CopyOnWriteArrayList<IProcessListener<T>>();
		}
		listeners.add(listener);
	}

	/**
	 * Removes a listener of this process
	 * 
	 * @param listener
	 */
	@Override
	public final void removeProcessListener(IProcessListener<T> listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * Returns a message to be shown in progress dialog. Processes can override
	 * this method to show custom information
	 * 
	 * @return
	 */
	protected String getProgressDialogInformation() {
		return I18nUtils.getString("PLEASE_WAIT");
	}

	/**
	 * Used to cancel this process
	 */
	protected final void cancelProcess() {
		canceled = true;
	}

	/**
	 * Executes this process
	 */
	@Override
	public final void execute() {
		// Add a debug log entry
		Logger.debug(this.getClass().getName());

		// Get size of this process
		this.processSize = getProcessSize();

		// Create new thread to run process
		Thread t = new Thread(this);

		// Run...
		t.start();
	}

	/**
	 * Returns progress dialog used in this process
	 * 
	 * @return
	 */
	protected IProgressDialog getProgressDialog() {
		if (progressDialog == null) {
			progressDialog = dialogFactory.newDialog("progressDialog",
					IProgressDialog.class);
			progressDialog.setTitle(getProgressDialogTitle());
			progressDialog.setInfoText(getProgressDialogInformation());
			progressDialog.setCurrentProgress(0);
			progressDialog.setProgressBarValue(0);
			progressDialog.addCancelButtonActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// When cancel disable cancel button
					cancelProcess();
					progressDialog.disableCancelButton();
				}
			});
		}
		return progressDialog;
	}

	/**
	 * Shows progress dialog
	 */
	private final void showProgressDialog() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// Set process size in dialog
					getProgressDialog().setTotalProgress(processSize);

					// Show dialog
					getProgressDialog().showDialog();
				}
			});
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * Hide progress dialog
	 */
	private final void hideProgressDialog() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					getProgressDialog().hideDialog();
				}
			});
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * Sets the current progress of this process. This method should be called
	 * every time process wants to increase progress indicator
	 * 
	 * @param currentProgress
	 */
	protected final void setCurrentProgress(final long currentProgress) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					getProgressDialog().setCurrentProgress(currentProgress);
					getProgressDialog().setProgressBarValue(
							(int) ((currentProgress * 100.0) / processSize));
				}
			});
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * Returns a string for the title of progress dialog
	 * 
	 * @return
	 */
	protected abstract String getProgressDialogTitle();

	/**
	 * Code of the process
	 * 
	 * @return <code>true</code> if process is executed successfully,
	 *         <code>false</code>otherwise
	 */
	protected abstract boolean runProcess();

	/**
	 * Code to be executed after process cancellation
	 */
	protected abstract void runCancel();

	/**
	 * @return process result
	 */
	protected abstract T getProcessResult();

	/**
	 * Returns process size
	 * 
	 * @return
	 */
	protected abstract long getProcessSize();

	/**
	 * @return the canceled
	 */
	protected boolean isCanceled() {
		return canceled;
	}

	@Override
	public final void run() {
		// Show progress dialog
		showProgressDialog();

		// Run process code
		boolean ok = runProcess();

		// Process finished: hide progress dialog
		hideProgressDialog();

		// If process has been canceled then execute cancel code
		if (canceled) {
			runCancel();
		}

		// Notify all listeners
		if (listeners != null && !listeners.isEmpty()) {
			for (IProcessListener<T> listener : listeners) {
				if (canceled) {
					listener.processCanceled();
				} else {
					listener.processFinished(ok, getProcessResult());
				}
			}
		}
	}

	/**
	 * @return
	 */
	protected IDialogFactory getDialogFactory() {
		return dialogFactory;
	}
}
