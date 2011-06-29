/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.NoCdListener;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.Encoder;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.StringUtils;

class CdRipper {

    private final class EncodeFileRunnable implements Runnable {
		private final int trackNumber;
		private final List<String> artistNames;
		private final List<String> composerNames;
		private final boolean ripResultFinal;
		private final File resultFileTemp;
		private final File infFileTemp;
		private final List<String> titles;
		private final File wavFileTemp;

		private EncodeFileRunnable(int trackNumber, List<String> artistNames,
				List<String> composerNames, boolean ripResultFinal,
				File resultFileTemp, File infFileTemp, List<String> titles,
				File wavFileTemp) {
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
		    if (!interrupted && ripResultFinal && encoder != null) {
		        boolean encodeResult = encoder.encode(wavFileTemp, resultFileTemp,
		                (titles != null && titles.size() >= trackNumber ? titles.get(trackNumber - 1) : null), trackNumber,
		                artistNames.size() > trackNumber - 1 ? artistNames.get(trackNumber - 1) : Artist.getUnknownArtist(),
		                composerNames.size() > trackNumber - 1 ? composerNames.get(trackNumber - 1) : "");
		        if (encodeResult && encoderListener != null && !interrupted) {
		            SwingUtilities.invokeLater(new Runnable() {
		                @Override
		                public void run() {
		                    encoderListener.notifyFileFinished(resultFileTemp);
		                }
		            });
		        }

		        Logger.info("Deleting wav file...");
		        wavFileTemp.delete();
		        infFileTemp.delete();

		        if (interrupted && resultFileTemp != null) {
		            resultFileTemp.delete();
		        }
		    } else if (interrupted) {
		        wavFileTemp.delete();
		        infFileTemp.delete();
		    } else if (!ripResultFinal) {
		    	Logger.error(StringUtils.getString("Rip failed. Skipping track ", trackNumber, "..."));
		    }
		}
	}

	static final String ARTIST_PATTERN = "%A";
    static final String ALBUM_PATTERN = "%L";
    static final String TITLE_PATTERN = "%T";
    static final String TRACK_NUMBER = "%N";

    private AbstractCdToWavConverter cdToWavConverter;
    private Encoder encoder;
    private ProgressListener listener;
    private ProgressListener encoderListener;
    private boolean interrupted;
    /** ExecutorService for file encoding. */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String artist;
    private String album;
    private int year;
    private String genre;
    private String fileNamePattern;

    /**
     * Instantiates a new cd ripper.
     */
    CdRipper() {
        cdToWavConverter = AbstractCdToWavConverter.createNewConverterForOS();
    }

    /**
     * Check folder.
     * 
     * @param folder
     *            the folder
     * 
     * @return true, if successful
     */
    private boolean checkFolder(File folder) {
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
     * This prepares the filename for the encoder.
     * 
     * @param titles
     *            the titles
     * @param trackNumber
     *            the track number
     * @param extension
     *            the extension
     * 
     * @return the file name
     */
    private String getFileName(List<String> titles, int trackNumber, String extension) {
        DecimalFormat df = new DecimalFormat("00");
        if (fileNamePattern == null) {
            return StringUtils.getString("track", trackNumber, '.', extension);
        }
        String result = StringUtils.getString(fileNamePattern, '.', extension);
        result = result.replaceAll(ARTIST_PATTERN, artist);
        result = result.replaceAll(ALBUM_PATTERN, album);
        result = result.replaceAll(TRACK_NUMBER, df.format(trackNumber));
        if (titles.size() > trackNumber - 1) {
            // We need to place \\ before escape sequences otherwise the ripper hangs. We can not do this later.
            result = result.replaceAll(TITLE_PATTERN, titles.get(trackNumber - 1).replace("\\", "\\\\").replace("$", "\\$"));
        } else {
            result = result.replaceAll(TITLE_PATTERN, StringUtils.getString("track", trackNumber));
        }
        // Replace known illegal characters. 
        result = FileNameUtils.getValidFileName(result);
        return result;
    }

    /**
     * Rip tracks.
     * 
     * @param tracks
     *            the tracks
     * @param titles
     *            the titles
     * @param folder
     *            the folder
     * @param artistNames
     *            the artist names
     * @param composerNames
     *            the composer names
     * @param useParanoia
     *            whether to use the paranoia mode of cdda2wav to correct CD
     *            errors (slow!)
     * @return true, if successful
     */

    boolean ripTracks(List<Integer> tracks, final List<String> titles, File folder, final List<String> artistNames, final List<String> composerNames, boolean useParanoia) {
        String extension = encoder != null ? encoder.getExtensionOfEncodedFiles() : "wav";
        Logger.info(StringUtils.getString("Running cd ripping of ", tracks.size(), " to ", extension, "..."));
        long t0 = System.currentTimeMillis();
        if (!checkFolder(folder)) {
        	Logger.error(StringUtils.getString("Folder ", folder, " not found or not a directory"));
            return false;
        }

        if (listener != null && encoderListener != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyProgress(0);
                    encoderListener.notifyProgress(0);
                }
            });
        }

        File wavFile = null;
        File resultFile = null;

        for (int i = 0; i < tracks.size(); i++) {
            if (!interrupted) {
                final int trackNumber = tracks.get(i);
                wavFile = new File(StringUtils.getString(folder.getAbsolutePath(), "/track", trackNumber, ".wav"));
                final boolean ripResultFinal;

                if (encoder != null) {
                    resultFile = new File(StringUtils.getString(folder.getAbsolutePath(), '/', getFileName(titles, trackNumber, extension)));
                }
                final File resultFileTemp = resultFile;
                final File wavFileTemp = wavFile;
                final File infFileTemp = new File(StringUtils.getString(folder.getAbsolutePath(), "/track", trackNumber, ".inf"));

                boolean ripResult = false;
                if (!interrupted) {
                    if (useParanoia) {
                        ripResult = cdToWavConverter.cdda2wav(trackNumber, wavFile, true);
                    } else {
                        ripResult = cdToWavConverter.cdda2wav(trackNumber, wavFile);
                    }
                }
                ripResultFinal = ripResult;

                /*
                 * Start encoding process, we use a thread so encoding and
                 * ripping can happen in parallel. This allows to import CD's
                 * faster.
                 */
                Runnable encodeFile = new EncodeFileRunnable(trackNumber, artistNames, composerNames,
						ripResultFinal, resultFileTemp, infFileTemp, titles,
						wavFileTemp);

                executorService.execute(encodeFile);
                /*
                 * If it is the last track that is ripped, wait for encoder to
                 * finish. This allows the progress dialog to stay visible.
                 */
                if (i == tracks.size() - 1) {
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
        }
        long t1 = System.currentTimeMillis();
        Logger.info(StringUtils.getString("Process finished in ", (t1 - t0) / 1000.0, " seconds"));
        return true;
    }

    /**
     * Sets the album.
     * 
     * @param album
     *            the new album
     */
    void setAlbum(String album) {
        if (album == null || album.equals("")) {
            this.album = Album.getUnknownAlbum();
        } else {
            this.album = album;
        }
        if (encoder != null) {
            encoder.setAlbum(this.album);
        }
    }

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the new artist
     */
    void setArtist(String artist) {
        if (artist == null || artist.equals("")) {
            this.artist = Artist.getUnknownArtist();
        } else {
            this.artist = artist;
        }
        if (encoder != null) {
            encoder.setArtist(this.artist);
        }
    }

    /**
     * Sets the decoder listener.
     * 
     * @param listener
     *            the new decoder listener
     */
    void setDecoderListener(ProgressListener listener) {
        cdToWavConverter.setListener(listener);
    }

    /**
     * Sets the encoder.
     * 
     * @param encoder
     *            the new encoder
     */
    void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Sets the encoder listener.
     * 
     * @param listener
     *            the new encoder listener
     */
    void setEncoderListener(ProgressListener listener) {
        if (encoder != null) {
            encoder.setListener(listener);
        }
    }

    /**
     * Sets the file name pattern.
     * 
     * @param fileNamePattern
     *            the new file name pattern
     */
    void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    /**
     * Sets the genre.
     * 
     * @param genre
     *            the new genre
     */
    void setGenre(String genre) {
        this.genre = genre;
        if (encoder != null) {
            encoder.setGenre(this.genre);
        }
    }

    /**
     * Sets the no cd listener.
     * 
     * @param listener
     *            the new no cd listener
     */
    void setNoCdListener(NoCdListener listener) {
        cdToWavConverter.setNoCdListener(listener);
    }

    /**
     * Sets the total progress listener.
     * 
     * @param listener
     *            the new total progress listener
     */
    void setTotalProgressListener(ProgressListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the year.
     * 
     * @param year
     *            the new year
     */
    void setYear(int year) {
        this.year = year;
        if (encoder != null) {
            encoder.setYear(this.year);
        }
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
