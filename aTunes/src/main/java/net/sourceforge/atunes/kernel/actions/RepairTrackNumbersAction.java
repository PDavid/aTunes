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

package net.sourceforge.atunes.kernel.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import net.sourceforge.atunes.kernel.modules.process.SetTrackNumberProcess;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Sets automatically track number of songs
 * @author alex
 *
 */
public class RepairTrackNumbersAction extends CustomAbstractAction {

	private static final class FilesWithEmptyTracksFilter implements Predicate<ILocalAudioObject> {
		@Override
		public boolean apply(@Nonnull final ILocalAudioObject ao) {
			return ao.getTrackNumber() == 0;
		}
	}


	private static final long serialVersionUID = 4117130815173907225L;

	private IProcessFactory processFactory;

	private IRepositoryHandler repositoryHandler;

	private IWebServicesHandler webServicesHandler;

	private IDialogFactory dialogFactory;

	private static final Pattern NUMBER_SEPARATOR_PATTERN = Pattern.compile("[^0-9]+");

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * Constructor
	 */
	public RepairTrackNumbersAction() {
		super(I18nUtils.getString("REPAIR_TRACK_NUMBERS"));
	}

	@Override
	protected void executeAction() {
		// Show confirmation dialog
		IConfirmationDialog dialog = dialogFactory.newDialog(IConfirmationDialog.class);
		dialog.setMessage(I18nUtils.getString("REPAIR_TRACK_NUMBERS_MESSAGE"));
		if (dialog.userAccepted()) {
			// Call track number edit
			/*
			 * Given an array of files, returns a map containing each file and its
			 * track number based on information found on file name.
			 */
			Map<ILocalAudioObject, Integer> filesToSet = new HashMap<ILocalAudioObject, Integer>();
			for (ILocalAudioObject ao : getFilesWithEmptyTracks(repositoryHandler.getAudioFilesList())) {
				int trackNumber = getTrackNumber(ao);

				if (trackNumber != 0) {
					filesToSet.put(ao, trackNumber);
				}
			}
			if (!filesToSet.isEmpty()) {
				// Call process
				SetTrackNumberProcess process = (SetTrackNumberProcess) processFactory.getProcessByName("setTrackNumberProcess");
				process.setFilesAndTracks(filesToSet);
				process.execute();
			}
		}
	}

	/**
	 * Returns track number for a given audio file
	 * 
	 * @param audioFile
	 * @return
	 */
	private int getTrackNumber(final ILocalAudioObject audioFile) {
		// Try to get a number from file name
		String fileName = audioFile.getNameWithoutExtension();
		String[] aux = NUMBER_SEPARATOR_PATTERN.split(fileName);
		int trackNumber = 0;
		int i = 0;
		while (trackNumber == 0 && i < aux.length) {
			String token = aux[i];
			try {
				trackNumber = Integer.parseInt(token);
				// If trackNumber >= 1000 maybe it's not a track number (year?)
				if (trackNumber >= 1000) {
					trackNumber = 0;
				}
			} catch (NumberFormatException e) {
				// Ok, it's not a valid number, skip it
			}
			i++;
		}

		// If trackNumber could not be retrieved from file name, try to get from last.fm
		// To get this, titles must match
		if (trackNumber == 0) {
			trackNumber = webServicesHandler.getTrackNumber(audioFile);
		}

		return trackNumber;
	}


	/**
	 * Returns files without track number
	 * @param audioFiles
	 * @return
	 */
	private Collection<ILocalAudioObject> getFilesWithEmptyTracks(final Collection<ILocalAudioObject> audioFiles) {
		return Collections2.filter(audioFiles, new FilesWithEmptyTracksFilter());
	}
}
