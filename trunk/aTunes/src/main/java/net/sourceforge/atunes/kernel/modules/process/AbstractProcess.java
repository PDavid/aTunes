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

package net.sourceforge.atunes.kernel.modules.process;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.dialogs.ProgressDialog;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * A Process represents a task to be done in background. While task is being
 * executed it updates a progress dialog. Also represents a common and easy way
 * to implement new background processes extending this class
 * 
 * Note: it is not implemented using a SwingWorker since with that progress
 * dialog is not updated synchronized
 * 
 * @author fleax
 * 
 */
public abstract class AbstractProcess {

    private final class ProcessRunnable implements Runnable {
		@Override
		public void run() {
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
		        for (ProcessListener listener : listeners) {
		            if (canceled) {
		                listener.processCanceled();
		            } else {
		                listener.processFinished(ok);
		            }
		        }
		    }
		}
	}

	/**
     * Logger shared by all processes
     */
    private Logger logger;

    /**
     * List of listeners notified when Process ends or is canceled
     */
    private volatile List<ProcessListener> listeners;

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
    private ProgressDialog progressDialog;

    /**
     * The Swing component owner of this process. Needed to set owner of
     * progress dialog. Can be null
     */
    private Component owner;

    /**
     * Adds a listener to this process
     * 
     * @param listener
     */
    public final void addProcessListener(ProcessListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<ProcessListener>();
        }
        listeners.add(listener);
    }

    /**
     * Removes a listener of this process
     * 
     * @param listener
     */
    public final void removeProcessListener(ProcessListener listener) {
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
    public final void execute() {
        // Add a debug log entry
        addDebugLog(this.getClass().getName());

        // Get size of this process
        this.processSize = getProcessSize();

        // Add window listener to progress dialog to run cancel
        getProgressDialog().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelProcess();
            }
        });

        // Create new thread to run process
        Thread t = new Thread(new ProcessRunnable());

        // Run...
        t.start();
    }

    /**
     * Adds a information log
     * 
     * @param o
     */
    protected final void addInfoLog(Object o) {
        getLogger().info(LogCategories.PROCESS, o);
    }

    /**
     * Adds a debug log
     * 
     * @param o
     */
    protected final void addDebugLog(Object... o) {
        getLogger().debug(LogCategories.PROCESS, o);
    }

    /**
     * Adds an error log
     * 
     * @param o
     */
    protected final void addErrorLog(Object o) {
        getLogger().error(LogCategories.PROCESS, o);
    }

    /**
     * Returns progress dialog used in this process
     * 
     * @return
     */
    protected ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = GuiHandler.getInstance().getNewProgressDialog(getProgressDialogTitle(), owner);
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
    final void showProgressDialog() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    // Set process size in dialog
                    getProgressDialog().setTotalProgress(processSize);

                    // Show dialog
                    getProgressDialog().setVisible(true);
                }
            });
        } catch (Exception e) {
            getLogger().error(LogCategories.PROCESS, e);
        }
    }

    /**
     * Hide progress dialog
     */
    final void hideProgressDialog() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    getProgressDialog().setVisible(false);
                }
            });
        } catch (Exception e) {
            getLogger().error(LogCategories.PROCESS, e);
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
                    getProgressDialog().setProgressBarValue((int) ((currentProgress * 100.0) / processSize));
                }
            });
        } catch (Exception e) {
            getLogger().error(LogCategories.PROCESS, e);
        }
    }

    /**
     * Component owner of this process
     * 
     * @param owner
     *            the owner to set
     */
    protected void setOwner(Component owner) {
        this.owner = owner;
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
     * @return <code>true</code> if process is executed succesfully,
     *         <code>false</code>otherwise
     */
    protected abstract boolean runProcess();

    /**
     * Code to be executed after process cancelation
     */
    protected abstract void runCancel();

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

    /**
     * @return the owner
     */
    protected Component getOwner() {
        return owner;
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
