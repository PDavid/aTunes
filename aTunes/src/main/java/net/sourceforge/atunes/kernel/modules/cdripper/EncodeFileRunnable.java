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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;

import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

final class EncodeFileRunnable implements Runnable {
	
	private final CdRipper ripper;
	private final int trackNumber;
	private final boolean ripResultFinal;
	private final File resultFileTemp;
	private final File infFileTemp;
	private final File wavFileTemp;
	private final CDMetadata metadata;
	private final ProgressListener listener;

	/**
	 * @param ripper
	 * @param trackNumber
	 * @param metadata
	 * @param ripResultFinal
	 * @param resultFileTemp
	 * @param infFileTemp
	 * @param wavFileTemp
	 * @param listener
	 */
	EncodeFileRunnable(CdRipper ripper, int trackNumber, CDMetadata metadata, boolean ripResultFinal,
			File resultFileTemp, File infFileTemp,
			File wavFileTemp, ProgressListener listener) {
		this.ripper = ripper;
		this.trackNumber = trackNumber;
		this.metadata = metadata;
		this.ripResultFinal = ripResultFinal;
		this.resultFileTemp = resultFileTemp;
		this.infFileTemp = infFileTemp;
		this.wavFileTemp = wavFileTemp;
		this.listener = listener;
	}

	@Override
	public void run() {
	    if (!ripper.isInterrupted() && ripResultFinal && ripper.getEncoder() != null) {
	        if (!callToEncode()) {
	        	Logger.error("Encoding unsuccessful");
	        } else {
	        	if (listener != null) {
	        		listener.notifyFileFinished(resultFileTemp);
	        	}
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
		boolean encodeOK = ripper.getEncoder().encode(wavFileTemp, resultFileTemp);
		if (encodeOK) {
			boolean tagOK = ripper.getEncoder().setTag(resultFileTemp, trackNumber, metadata);
			if (!tagOK) {
				return false;
			}
		}
        return encodeOK;
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
		if (infFileTemp.exists() && !infFileTemp.delete()) {
			Logger.error(StringUtils.getString(infFileTemp, " not deleted"));
		}
	}

	/**
	 * 
	 */
	private void deleteWavFileTemp() {
		if (wavFileTemp.exists() && !wavFileTemp.delete()) {
			Logger.error(StringUtils.getString(wavFileTemp, " not deleted"));
		}
	}
}