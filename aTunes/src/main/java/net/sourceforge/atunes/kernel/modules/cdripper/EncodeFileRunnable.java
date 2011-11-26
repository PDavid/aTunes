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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;
import java.util.List;

import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

final class EncodeFileRunnable implements Runnable {
	
	private final CdRipper ripper;
	private final int trackNumber;
	private final List<String> artistNames;
	private final List<String> composerNames;
	private final boolean ripResultFinal;
	private final File resultFileTemp;
	private final File infFileTemp;
	private final List<String> titles;
	private final File wavFileTemp;

	EncodeFileRunnable(CdRipper ripper, int trackNumber, List<String> artistNames,
			List<String> composerNames, boolean ripResultFinal,
			File resultFileTemp, File infFileTemp, List<String> titles,
			File wavFileTemp) {
		this.ripper = ripper;
		this.trackNumber = trackNumber;
		this.artistNames = artistNames;
		this.composerNames = composerNames;
		this.ripResultFinal = ripResultFinal;
		this.resultFileTemp = resultFileTemp;
		this.infFileTemp = infFileTemp;
		this.titles = titles;
		this.wavFileTemp = wavFileTemp;
	}

	@Override
	public void run() {
	    if (!ripper.isInterrupted() && ripResultFinal && ripper.getEncoder() != null) {
	        if (!callToEncode()) {
	        	Logger.error("Encoding unsuccessful");
	        }

	        Logger.info("Deleting wav file...");
	        deleteWavFileTemp();
	        deleteInfFileTemp();

	        if (ripper.isInterrupted() && resultFileTemp != null) {
	            deleteResultFileTemp();
	        }
	    } else if (ripper.isInterrupted()) {		    	
	        deleteWavFileTemp();
	        deleteInfFileTemp();
	    } else if (!ripResultFinal) {
	    	Logger.error(StringUtils.getString("Rip failed. Skipping track ", trackNumber, "..."));
	    }
	}

	/**
	 * @return
	 */
	private boolean callToEncode() {
		return ripper.getEncoder().encode(wavFileTemp, resultFileTemp,
		        (titles != null && titles.size() >= trackNumber ? titles.get(trackNumber - 1) : null), trackNumber,
		        artistNames.size() > trackNumber - 1 ? artistNames.get(trackNumber - 1) : Artist.getUnknownArtist(),
		        composerNames.size() > trackNumber - 1 ? composerNames.get(trackNumber - 1) : "");
	}

	/**
	 * 
	 */
	private void deleteResultFileTemp() {
		if (!resultFileTemp.delete()) {
			Logger.error(StringUtils.getString(resultFileTemp, " not deleted"));
		}
	}

	/**
	 * 
	 */
	private void deleteInfFileTemp() {
		if (!infFileTemp.delete()) {
			Logger.error(StringUtils.getString(infFileTemp, " not deleted"));
		}
	}

	/**
	 * 
	 */
	private void deleteWavFileTemp() {
		if (!wavFileTemp.delete()) {
			Logger.error(StringUtils.getString(wavFileTemp, " not deleted"));
		}
	}
}