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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.atunes.kernel.modules.process.SetTrackNumberProcess;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Calls process to set track numbers automatically
 * 
 * @author fleax
 * 
 */
public class AutoSetTracksAction extends
	AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = 1378739676496602001L;

    private static final Pattern NUMBER_SEPARATOR_PATTERN = Pattern
	    .compile("[^0-9]+");

    private IProcessFactory processFactory;

    private IWebServicesHandler webServicesHandler;

    /**
     * @param webServicesHandler
     */
    public void setWebServicesHandler(
	    final IWebServicesHandler webServicesHandler) {
	this.webServicesHandler = webServicesHandler;
    }

    /**
     * @param processFactory
     */
    public void setProcessFactory(final IProcessFactory processFactory) {
	this.processFactory = processFactory;
    }

    /**
     * Default constructor
     */
    public AutoSetTracksAction() {
	super(I18nUtils.getString("AUTO_SET_TRACK_NUMBER"));
    }

    @Override
    protected void executeAction(final List<ILocalAudioObject> objects) {
	/*
	 * Given an array of files, returns a map containing each file and its
	 * track number based on information found on file name.
	 */
	Map<ILocalAudioObject, Integer> filesToSet = new HashMap<ILocalAudioObject, Integer>();
	for (ILocalAudioObject ao : objects) {
	    int trackNumber = getTrackNumber(ao);

	    if (trackNumber != 0) {
		filesToSet.put(ao, trackNumber);
	    }
	}
	if (!filesToSet.isEmpty()) {
	    // Call process
	    SetTrackNumberProcess process = (SetTrackNumberProcess) processFactory
		    .getProcessByName("setTrackNumberProcess");
	    process.setFilesAndTracks(filesToSet);
	    process.execute();
	}
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	return !selection.isEmpty();
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

	// If trackNumber could not be retrieved from file name, try to get from
	// last.fm
	// To get this, titles must match
	if (trackNumber == 0) {
	    trackNumber = webServicesHandler.getTrackNumber(audioFile);
	}
	return trackNumber;
    }
}
