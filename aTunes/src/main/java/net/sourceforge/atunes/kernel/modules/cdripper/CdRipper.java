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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Responsible of rip cds
 * @author alex
 *
 */
public class CdRipper {

	private AbstractCdToWavConverter cdToWavConverter;
	private Encoder encoder;
	private ProgressListener listener;
	private boolean interrupted;
	/** ExecutorService for file encoding. */
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private CdRipperFileNameCreator cdRipperFileNameCreator;

	private CDMetadata cdMetadata;

	/**
	 * @param cdMetadata
	 */
	public void setCdMetadata(final CDMetadata cdMetadata) {
		this.cdMetadata = cdMetadata;
	}

	/**
	 * @param cdRipperFileNameCreator
	 */
	public void setCdRipperFileNameCreator(final CdRipperFileNameCreator cdRipperFileNameCreator) {
		this.cdRipperFileNameCreator = cdRipperFileNameCreator;
	}

	/**
	 * @param cdToWavConverter
	 */
	public void setCdToWavConverter(final AbstractCdToWavConverter cdToWavConverter) {
		this.cdToWavConverter = cdToWavConverter;
	}

	/**
	 * @return if process has been interrupted
	 */
	protected boolean isInterrupted() {
		return interrupted;
	}

	/**
	 * @return encoder used
	 */
	protected Encoder getEncoder() {
		return encoder;
	}

	/**
	 * Check folder.
	 * 
	 * @param folder
	 *            the folder
	 * 
	 * @return true, if successful
	 */
	private boolean checkFolder(final File folder) {
		return folder.exists() && folder.isDirectory();
	}

	/**
	 * Gets the cD info.
	 * 
	 * @return the cD info
	 */
	CDInfo getCDInfo() {
		return cdToWavConverter.retrieveDiscInformation();
	}

	/**
	 * Rip tracks.
	 * @param folder
	 * @param useParanoia
	 * @return true if successful
	 */
	boolean ripTracks(final File folder, final boolean useParanoia) {
		String extension = encoder != null ? encoder.getExtensionOfEncodedFiles() : "wav";
		Logger.info(StringUtils.getString("Running cd ripping of ", cdMetadata.getTracks().size(), " to ", extension, "..."));
		long t0 = System.currentTimeMillis();
		if (!checkFolder(folder)) {
			Logger.error(StringUtils.getString("Folder ", folder, " not found or not a directory"));
			return false;
		}

		if (listener != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listener.notifyProgress(0);
				}
			});
		}

		File resultFile = null;

		for (int i = 0; i < cdMetadata.getTracks().size(); i++) {
			resultFile = ripTrack(folder, useParanoia, extension, resultFile, i);
		}
		long t1 = System.currentTimeMillis();
		Logger.info(StringUtils.getString("Process finished in ", (t1 - t0) / 1000.0, " seconds"));
		return true;
	}

	/**
	 * @param folder
	 * @param useParanoia
	 * @param extension
	 * @param resultFile
	 * @param i
	 * @return
	 */
	private File ripTrack(final File folder, final boolean useParanoia, final String extension, final File resultFile, final int i) {

		File result = resultFile;

		if (!interrupted) {
			final int trackNumber = cdMetadata.getTracks().get(i);
			File wavFile = new File(StringUtils.getString(FileUtils.getPath(folder), "/track", trackNumber, ".wav"));

			if (encoder != null) {
				result = new File(StringUtils.getString(FileUtils.getPath(folder), '/', cdRipperFileNameCreator.getFileName(cdMetadata, trackNumber, extension)));
			}
			final File infFileTemp = new File(StringUtils.getString(FileUtils.getPath(folder), "/track", trackNumber, ".inf"));

			boolean ripResult = false;
			if (!interrupted) {
				if (useParanoia) {
					ripResult = cdToWavConverter.cdda2wav(trackNumber, wavFile, true);
				} else {
					ripResult = cdToWavConverter.cdda2wav(trackNumber, wavFile);
				}
			}

			/*
			 * Start encoding process, we use a thread so encoding and
			 * ripping can happen in parallel. This allows to import CD's
			 * faster.
			 */
			Runnable encodeFile = new EncodeFileRunnable(this, trackNumber, cdMetadata, ripResult, result, infFileTemp, wavFile, listener);

			executorService.execute(encodeFile);
			/*
			 * If it is the last track that is ripped, wait for encoder to
			 * finish. This allows the progress dialog to stay visible.
			 */
			if (i == cdMetadata.getTracks().size() - 1) {
				executorService.shutdown();
				try {
					executorService.awaitTermination(100, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					Logger.error(e);
				}
			}

			if (listener != null) {
				final int iHelp = i;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						listener.notifyProgress(iHelp + 1);
					}
				});
			}
		}
		return result;
	}

	/**
	 * Sets the decoder listener.
	 * 
	 * @param listener
	 *            the new decoder listener
	 */
	void setDecoderListener(final ProgressListener listener) {
		cdToWavConverter.setListener(listener);
	}

	/**
	 * Sets the encoder.
	 * 
	 * @param encoder
	 *            the new encoder
	 */
	void setEncoder(final Encoder encoder) {
		this.encoder = encoder;
	}

	/**
	 * Sets the encoder listener.
	 * 
	 * @param listener
	 *            the new encoder listener
	 */
	void setEncoderListener(final ProgressListener listener) {
		if (encoder != null) {
			encoder.setListener(listener);
		}
	}

	/**
	 * Sets the no cd listener.
	 * 
	 * @param listener
	 *            the new no cd listener
	 */
	void setNoCdListener(final NoCdListener listener) {
		cdToWavConverter.setNoCdListener(listener);
	}

	/**
	 * Sets the total progress listener.
	 * 
	 * @param listener
	 *            the new total progress listener
	 */
	void setTotalProgressListener(final ProgressListener listener) {
		this.listener = listener;
	}

	/**
	 * Stop.
	 */
	void stop() {
		interrupted = true;
		cdToWavConverter.stop();
		if (encoder != null) {
			encoder.stop();
		}
	}
}
