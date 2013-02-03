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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectTransferProcess;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * A type of processes that move local audio objects
 * @author alex
 *
 */
/**
 * @author alex
 * 
 */
public abstract class AbstractLocalAudioObjectTransferProcess extends
		AbstractProcess<List<ILocalAudioObject>> implements
		ILocalAudioObjectTransferProcess {

	/**
	 * The files to be transferred.
	 */
	private Collection<ILocalAudioObject> filesToTransfer;

	/**
	 * List of files transferred. Used if process is canceled to delete these
	 * files
	 */
	private final List<ILocalAudioObject> filesTransferred;

	/**
	 * The dialog used to show the progress of this process
	 */
	private IProgressDialog progressDialog;

	/**
	 * User selection if an error occurs while transferring
	 */
	private String userSelectionWhenErrors = null;

	private IOSManager osManager;

	private IProgressDialog transferDialog;

	private String destination;

	private IStateRepository stateRepository;

	private Patterns patterns;

	private IFileManager fileManager;

	/**
	 * @return
	 */
	protected IFileManager getFileManager() {
		return this.fileManager;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param patterns
	 */
	public void setPatterns(final Patterns patterns) {
		this.patterns = patterns;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(final IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	/**
	 * @return
	 */
	protected IStateRepository getStateRepository() {
		return this.stateRepository;
	}

	/**
	 * @param collection
	 */
	public AbstractLocalAudioObjectTransferProcess() {
		this.filesTransferred = new ArrayList<ILocalAudioObject>();
	}

	/**
	 * Destination of transfer
	 * 
	 * @param destination
	 */
	@Override
	public void setDestination(final String destination) {
		this.destination = destination;
	}

	/**
	 * @return
	 */
	protected Collection<ILocalAudioObject> getFilesToTransfer() {
		return this.filesToTransfer;
	}

	/**
	 * @param transferDialog
	 */
	public void setTransferDialog(final IProgressDialog transferDialog) {
		this.transferDialog = transferDialog;
	}

	/**
	 * Sets the files to transfer by this process
	 * 
	 * @param filesToTransfer
	 */
	@Override
	public void setFilesToTransfer(final List<ILocalAudioObject> filesToTransfer) {
		this.filesToTransfer = filesToTransfer;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	protected long getProcessSize() {
		// Get size of files
		long totalBytes = 0;
		for (ILocalAudioObject file : this.filesToTransfer) {
			totalBytes = totalBytes + this.fileManager.getFileSize(file);
		}
		return totalBytes;
	}

	@Override
	protected IProgressDialog getProgressDialog() {
		if (this.progressDialog == null) {
			this.progressDialog = this.transferDialog;
			this.progressDialog.setTitle(getProgressDialogTitle());
			this.progressDialog.setInfoText(getProgressDialogInformation());
			this.progressDialog.setCurrentProgress(0);
			this.progressDialog.setProgressBarValue(0);
			this.progressDialog
					.addCancelButtonActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							cancelProcess();
							AbstractLocalAudioObjectTransferProcess.this.progressDialog
									.disableCancelButton();
						}
					});
		}
		return this.progressDialog;
	}

	@Override
	protected boolean runProcess() {
		boolean errors = false;
		File destinationFile = new File(getDestination());
		long bytesTransferred = 0;
		boolean ignoreAllErrors = false;
		Logger.info(StringUtils.getString("Transferring ",
				this.filesToTransfer.size(), " files to ", destinationFile));
		for (Iterator<ILocalAudioObject> it = this.filesToTransfer.iterator(); it
				.hasNext() && !isCanceled();) {
			ILocalAudioObject file = it.next();
			final List<Exception> thrownExceptions = new ArrayList<Exception>();
			ILocalAudioObject transferredFile = transferAudioFile(
					destinationFile, file, thrownExceptions);
			if (transferredFile != null) {
				this.filesTransferred.add(transferredFile);
			}
			if (!thrownExceptions.isEmpty()) {
				for (Exception e : thrownExceptions) {
					Logger.error(e);
				}
				if (!ignoreAllErrors) {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								AbstractLocalAudioObjectTransferProcess.this.userSelectionWhenErrors = (String) getDialogFactory()
										.newDialog(IMessageDialog.class)
										.showMessage(
												StringUtils.getString(I18nUtils
														.getString("ERROR"),
														": ", thrownExceptions
																.get(0)
																.getMessage()),
												I18nUtils.getString("ERROR"),
												JOptionPane.ERROR_MESSAGE,
												new String[] {
														I18nUtils
																.getString("IGNORE"),
														I18nUtils
																.getString("IGNORE_ALL"),
														I18nUtils
																.getString("CANCEL") });
							}
						});
					} catch (InterruptedException e1) {
						// Do nothing
					} catch (InvocationTargetException e1) {
						// Do nothing
					}
					if (I18nUtils.getString("IGNORE_ALL").equals(
							this.userSelectionWhenErrors)) {
						// Don't display more error messages
						ignoreAllErrors = true;
					} else if (I18nUtils.getString("CANCEL").equals(
							this.userSelectionWhenErrors)) {
						// Only in this case set errors to true to force refresh
						// in other case
						errors = true;

						// Don't continue
						break;
					}
				}
			}
			// Add size to bytes transferred
			bytesTransferred += this.fileManager.getFileSize(file);
			setCurrentProgress(bytesTransferred);
		}
		Logger.info("Transfer process done");
		return !errors;
	}

	@Override
	protected void runCancel() {
		// Remove all transferred files
		for (ILocalAudioObject f : this.filesTransferred) {
			this.fileManager.delete(f);
		}
	}

	/**
	 * Return destination of all files to be transferred
	 * 
	 * @return
	 */
	protected String getDestination() {
		return this.destination;
	}

	/**
	 * Transfers a file to a destination
	 * 
	 * @param destination
	 * @param ao
	 * @param list
	 *            to add exceptions thrown
	 * @return audio object copied or null if an exception happens
	 * @throws IOException
	 */
	protected ILocalAudioObject transferAudioFile(final File destination,
			final ILocalAudioObject ao, final List<Exception> thrownExceptions) {
		try {
			return this.fileManager.copyFile(ao,
					getDirectory(ao, destination, false), getName(ao, false));
		} catch (IOException e) {
			thrownExceptions.add(e);
			return null;
		}
	}

	/**
	 * @return the filesTransferred
	 */
	@Override
	public List<ILocalAudioObject> getFilesTransferred() {
		return this.filesTransferred;
	}

	/**
	 * Returns directory structure using import export folder pattern
	 * 
	 * @param song
	 * @param destination
	 * @param isMp3Device
	 * @param data
	 *            .osManager
	 * @return
	 */
	protected String getDirectory(final ILocalAudioObject song,
			final File destination, final boolean isMp3Device) {
		return getDirectory(song, destination, isMp3Device,
				getFolderPathPattern());
	}

	/**
	 * @return folder path pattern
	 */
	protected abstract String getFolderPathPattern();

	/**
	 * Prepares the directory structure in which the song will be written.
	 * 
	 * @param song
	 * @param destination
	 * @param isMp3Device
	 * @param pattern
	 * @return Returns the directory structure with full path where the file
	 *         will be written
	 */
	protected String getDirectory(final ILocalAudioObject song,
			final File destination, final boolean isMp3Device,
			final String pattern) {
		String songRelativePath = "";
		if (pattern != null) {
			songRelativePath = FileNameUtils.getValidFolderName(
					getNewFolderPath(pattern, song, this.osManager),
					isMp3Device, this.osManager);
		}
		return StringUtils.getString(
				net.sourceforge.atunes.utils.FileUtils.getPath(destination),
				this.osManager.getFileSeparator(), songRelativePath);
	}

	/**
	 * Returns a valid name to transfer the file using import export file name
	 * pattern
	 * 
	 * @param file
	 * @param isMp3Device
	 * @return
	 */
	protected String getName(final ILocalAudioObject file,
			final boolean isMp3Device) {
		return getName(file, isMp3Device, getFileNamePattern());
	}

	/**
	 * @return file name pattern to use
	 */
	protected abstract String getFileNamePattern();

	/**
	 * Returns a valid name to transfer the file given as argument. It applies
	 * pattern replace given as argument
	 * 
	 * @param file
	 * @param isMp3Device
	 * @param pattern
	 * @return
	 */
	protected String getName(final ILocalAudioObject file,
			final boolean isMp3Device, final String pattern) {
		String newName;
		if (pattern != null) {
			newName = getNewFileName(pattern, file, this.osManager);
		} else {
			newName = FileNameUtils.getValidFileName(this.fileManager
					.getFileName(file).replace("\\", "\\\\")
					.replace("$", "\\$"), isMp3Device, this.osManager);
		}
		return newName;
	}

	protected IOSManager getOsManager() {
		return this.osManager;
	}

	/**
	 * Prepares the filename in order to write it.
	 * 
	 * @param pattern
	 *            Filename pattern
	 * @param song
	 *            Song file to be written
	 * 
	 * @return Returns a (hopefully) valid filename
	 */
	protected String getNewFileName(final String pattern,
			final ILocalAudioObject song, final IOSManager osManager) {
		String result = this.patterns
				.applyPatternTransformations(pattern, song);
		// We need to place \\ before escape sequences otherwise the ripper
		// hangs. We can not do this later.
		result = result.replace("\\", "\\\\").replace("$", "\\$");
		result = StringUtils.getString(
				result,
				this.fileManager.getFileName(song).substring(
						this.fileManager.getFileName(song).lastIndexOf('.')));
		result = FileNameUtils.getValidFileName(result, osManager);
		return result;
	}

	/**
	 * Prepares the folder path in order to write it.
	 * 
	 * @param pattern
	 *            Folder path pattern
	 * @param song
	 *            Song file to be written
	 * 
	 * @return Returns a (hopefully) valid filename
	 */
	protected String getNewFolderPath(final String pattern,
			final ILocalAudioObject song, final IOSManager osManager) {
		String result = this.patterns
				.applyPatternTransformations(pattern, song);
		// We need to place \\ before escape sequences otherwise the ripper
		// hangs. We can not do this later.
		result = result.replace("\\", "\\\\").replace("$", "\\$");
		result = FileNameUtils.getValidFolderName(result, false, osManager);
		return result;
	}

	@Override
	protected List<ILocalAudioObject> getProcessResult() {
		return this.filesTransferred;
	}
}
