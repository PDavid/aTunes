/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.Cursor;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.gui.views.dialogs.RipperProgressDialog;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonAlbum;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonDisc;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonService;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.CdToWavConverter;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.NoCdListener;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.Encoder;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class RipperHandler extends Handler {

    private static class TotalProgressListener implements ProgressListener {
		private final RipperProgressDialog dialog;
		private final List<File> filesImported;

		private TotalProgressListener(RipperProgressDialog dialog,
				List<File> filesImported) {
			this.dialog = dialog;
			this.filesImported = filesImported;
		}

		@Override
		public void notifyFileFinished(File file) {
		    filesImported.add(file);
		}

		@Override
		public void notifyProgress(int value) {
		    dialog.setTotalProgressValue(value);
		    dialog.setDecodeProgressValue(0);
		    dialog.setDecodeProgressValue(StringUtils.getString(0, "%"));
		    dialog.setEncodeProgressValue(0);
		    dialog.setEncodeProgressValue(StringUtils.getString(0, "%"));
		}
	}

	private static class EncoderProgressListener implements ProgressListener {
		private final RipperProgressDialog dialog;

		private EncoderProgressListener(RipperProgressDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void notifyFileFinished(File f) {
		    // Nothing to do
		}

		@Override
		public void notifyProgress(int percent) {
		    dialog.setEncodeProgressValue(percent);
		    if (!(percent < 0)) {
		        dialog.setEncodeProgressValue(StringUtils.getString(percent, "%"));
		    }
		}
	}

	private static class DecoderProgressListener implements ProgressListener {
		private final RipperProgressDialog dialog;

		private DecoderProgressListener(RipperProgressDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void notifyFileFinished(File f) {
		    // Nothing to do
		}

		@Override
		public void notifyProgress(int percent) {
		    dialog.setDecodeProgressValue(percent);
		    if (percent > 0) {
		        dialog.setDecodeProgressValue(StringUtils.getString(percent, "%"));
		    } else {
		        dialog.setDecodeProgressValue("");
		    }
		}
	}

	private static RipperHandler instance = new RipperHandler();

    CdRipper ripper;
    volatile boolean interrupted;
    boolean folderCreated;
    String albumCoverURL;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Map of available encoders in the system: key is format name, value is
     * encoder
     */
    private Map<String, Encoder> availableEncoders;

    /**
     * List of encoder classes
     */
    private static List<String> allEncoders;

    static {
        // TODO: Add here every new encoder
        // The order of entries is used to determine default encoder. 
        // First entry is default encoder, if it's not available then second entry is default encoder...  
        allEncoders = new ArrayList<String>();
        allEncoders.add("net.sourceforge.atunes.kernel.modules.cdripper.encoders.OggEncoder");
        allEncoders.add("net.sourceforge.atunes.kernel.modules.cdripper.encoders.LameEncoder");
        allEncoders.add("net.sourceforge.atunes.kernel.modules.cdripper.encoders.Mp4Encoder");
        allEncoders.add("net.sourceforge.atunes.kernel.modules.cdripper.encoders.NeroAacEncoder");
        allEncoders.add("net.sourceforge.atunes.kernel.modules.cdripper.encoders.FlacEncoder");
        allEncoders.add("net.sourceforge.atunes.kernel.modules.cdripper.encoders.WavEncoder");
    }

    /**
     * Instantiates a new ripper handler.
     */
    private RipperHandler() {
        // Nothing to do
    }

    /**
     * Gets the single instance of RipperHandler.
     * 
     * @return single instance of RipperHandler
     */
    public static RipperHandler getInstance() {
        return instance;
    }

    @Override
    public void applicationFinish() {
        // TODO Auto-generated method stub

    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void applicationStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initHandler() {
        // TODO Auto-generated method stub

    }

    /**
     * Add files to existing repository if destiny folder is in repository. This
     * method is used after an import operation
     * 
     * @param files
     *            the files
     * @param folder
     *            the folder
     */
    private void addFilesToRepositoryAndRefresh(List<File> files, File folder) {
        if (RepositoryHandler.getInstance().isRepository(folder)) {
            RepositoryHandler.getInstance().addFilesAndRefresh(files);
        }
    }

    /**
     * Cancel process.
     */
    public void cancelProcess() {
        interrupted = true;
        ripper.stop();
        getLogger().info(LogCategories.RIPPER, "Process cancelled");
    }

    /**
     * Fill songs from amazon.
     * 
     * @param artist
     *            the artist
     * @param album
     *            the album
     */
    public void fillSongsFromAmazon(final String artist, final String album) {
        GuiHandler.getInstance().getRipCdDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        GuiHandler.getInstance().getRipCdDialog().getAmazonButton().setEnabled(false);
        new SwingWorker<AmazonAlbum, Void>() {
            @Override
            protected AmazonAlbum doInBackground() throws Exception {
                return AmazonService.getInstance().getAlbum(artist, album);
            }

            @Override
            protected void done() {
                try {
                    if (get() != null) {
                        albumCoverURL = get().getImageURL();
                        List<String> tracks = new ArrayList<String>();
                        for (AmazonDisc disc : get().getDiscs()) {
                            tracks.addAll(disc.getTracks());
                        }
                        GuiHandler.getInstance().getRipCdDialog().updateTrackNames(tracks);
                    }
                } catch (InterruptedException e) {
                    getLogger().internalError(e);
                } catch (ExecutionException e) {
                    getLogger().error(LogCategories.RIPPER, e);
                } finally {
                    GuiHandler.getInstance().getRipCdDialog().setCursor(Cursor.getDefaultCursor());
                    GuiHandler.getInstance().getRipCdDialog().getAmazonButton().setEnabled(true);
                }
            }
        }.execute();
    }

    /**
     * Gets format name of the encoder which was used for ripping CD's. If
     * encoder is not available then get one of the available
     * 
     * @return Return the format name of the encoder used the previous time or
     *         default one if it's available
     */
    public String getEncoder() {
        String encoderFormat = ApplicationState.getInstance().getEncoder();
        if (getAvailableEncoders().containsKey(encoderFormat)) {
            return encoderFormat;
        }
        // Not available, check what is the available encoder with higher priority (less index value)
        Encoder higherPriorityEncoder = null;
        for (Encoder availableEncoder : getAvailableEncoders().values()) {
            if (higherPriorityEncoder == null) {
                higherPriorityEncoder = availableEncoder;
            } else {
                int encoderIndex = allEncoders.indexOf(availableEncoder.getClass().getName());
                if (encoderIndex != -1 && encoderIndex < allEncoders.indexOf(higherPriorityEncoder.getClass().getName())) {
                    higherPriorityEncoder = availableEncoder;
                }
            }
        }
        if (higherPriorityEncoder != null) {
            return higherPriorityEncoder.getFormatName();
        }

        return null;
    }

    /**
     * Returns available qualities for given format name
     * 
     * @param formatName
     * @return
     */
    public String[] getEncoderQualities(String formatName) {
        if (getAvailableEncoders().containsKey(formatName)) {
            return getAvailableEncoders().get(formatName).getAvailableQualities();
        }
        return new String[0];
    }

    /**
     * Returns default quality for given format name
     * 
     * @param formatName
     * @return
     */
    public String getEncoderDefaultQuality(String formatName) {
        if (getAvailableEncoders().containsKey(formatName)) {
            return getAvailableEncoders().get(formatName).getDefaultQuality();
        }
        return "";
    }

    /**
     * Gets the encoder quality.
     * 
     * @return the encoder quality
     */
    public String getEncoderQuality() {
        return ApplicationState.getInstance().getEncoderQuality();
    }

    /**
     * Returns the filename pattern which is used.
     * 
     * @return The filename pattern
     */
    public String getFileNamePattern() {
        return ApplicationState.getInstance().getCdRipperFileNamePattern();
    }

    /**
     * Test for available encoders and returns a Map of the found encoders.
     * 
     * @return the available encoders
     */
    public Map<String, Encoder> getAvailableEncoders() {
        if (availableEncoders == null) {
            availableEncoders = new HashMap<String, Encoder>();

            // Test all encoders
            for (String encoderName : allEncoders) {
                try {
                    Class<?> encoderClass = Class.forName(encoderName);
                    if (Encoder.class.isAssignableFrom(encoderClass)) {
                        Method testToolMethod = encoderClass.getMethod("testTool");
                        Boolean encoderAvailable = (Boolean) testToolMethod.invoke(null);
                        if (encoderAvailable) {
                            Encoder instancedEncoder = (Encoder) encoderClass.newInstance();
                            availableEncoders.put(instancedEncoder.getFormatName(), instancedEncoder);
                        }
                    } else {
                        getLogger().error(LogCategories.RIPPER, encoderClass + " is not a subtype of " + Encoder.class);
                    }
                } catch (ClassNotFoundException e) {
                    getLogger().error(LogCategories.RIPPER, e);
                } catch (NoSuchMethodException e) {
                    getLogger().error(LogCategories.RIPPER, e);
                } catch (IllegalAccessException e) {
                    getLogger().error(LogCategories.RIPPER, e);
                } catch (InvocationTargetException e) {
                    getLogger().error(LogCategories.RIPPER, e);
                } catch (InstantiationException e) {
                    getLogger().error(LogCategories.RIPPER, e);
                }
            }

            getLogger().info(LogCategories.RIPPER, StringUtils.getString("Available encoders: ", availableEncoders.keySet()));
        }
        return availableEncoders;
    }

    /**
     * Indicates if the user requested cd error correction
     * 
     * @return true if error correction for cd ripping should be used
     */
    public boolean getCdErrorCorrection() {
        return ApplicationState.getInstance().isUseCdErrorCorrection();
    }

    /**
     * Controls the import process for ripping audio CD's.
     * 
     * @param folder
     *            The folder where the files should be saved
     * @param artist
     *            Artist name (whole CD)
     * @param album
     *            Album name
     * @param year
     *            Release year
     * @param genre
     *            Album genre
     * @param tracks
     *            List of the track numbers
     * @param trckNames
     *            List of the track names
     * @param format
     *            Format in which the files should converted
     * @param quality1
     *            Quality setting to be used
     * @param artistNames
     *            the artist names
     * @param composerNames
     *            the composer names
     */
    void importSongs(String folder, final String artist, final String album, final int year, final String genre, final List<Integer> tracks, final List<String> trckNames, final List<String> artistNames, final List<String> composerNames, final String format, final String quality1, final boolean useParanoia) {
        // Disable import cd option in menu
        Actions.getAction(RipCDAction.class).setEnabled(false);

        final File folderFile = new File(folder);
        if (!folderFile.exists()) {
            if (folderFile.mkdirs()) {
                folderCreated = true;
            } else {
                getLogger().error(LogCategories.RIPPER, "Folder could not be created");
                return;
            }
        }

        // Prepares commands for the encoder
        // Get encoder to be used
        Encoder encoder = getAvailableEncoders().get(format);
        encoder.setQuality(quality1);

        ripper.setEncoder(encoder);
        ripper.setArtist(artist);
        ripper.setAlbum(album);
        ripper.setYear(year);
        ripper.setGenre(genre);
        ripper.setFileNamePattern(getFileNamePattern());

        final RipperProgressDialog dialog = GuiHandler.getInstance().getRipperProgressDialog();

        // Get image from amazon if necessary
        if (albumCoverURL != null) {
            Image cover = AmazonService.getInstance().getImage(albumCoverURL);
            dialog.setCover(cover);
            savePicture(cover, folderFile, artist, album);
        }

        dialog.setArtistAndAlbum(artist, album);

        dialog.setTotalProgressBarLimits(0, tracks.size());
        dialog.setTotalProgressValue(0);

        final List<File> filesImported = new ArrayList<File>();

        ripper.setDecoderListener(new DecoderProgressListener(dialog));

        ripper.setEncoderListener(new EncoderProgressListener(dialog));

        ripper.setTotalProgressListener(new TotalProgressListener(dialog, filesImported));

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return ripper.ripTracks(tracks, trckNames, folderFile, artistNames, composerNames, useParanoia);
            }

            @Override
            protected void done() {
                notifyFinishImport(filesImported, folderFile);
                // Enable import cd option in menu
                Actions.getAction(RipCDAction.class).setEnabled(true);
            }
        }.execute();

        dialog.setVisible(true);
    }

    /**
     * Notify finish import.
     * 
     * @param filesImported
     *            the files imported
     * @param folder
     *            the folder
     */
    public void notifyFinishImport(final List<File> filesImported, final File folder) {
        GuiHandler.getInstance().getRipperProgressDialog().setVisible(false);
        if (interrupted) { // If process is interrupted delete all imported files
            Runnable deleter = new Runnable() {
                @Override
                public void run() {
                    for (File f : filesImported) {
                        f.delete();
                    }

                    // Wait two seconds to assure filesImported are deleted
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        getLogger().internalError(e);
                    }
                    if (folderCreated) {
                        folder.delete();
                    }
                }
            };
            executorService.submit(deleter);
        } else {
            addFilesToRepositoryAndRefresh(filesImported, folder);
        }
    }

    /**
     * Save picture.
     * 
     * @param image
     *            the image
     * @param path
     *            the path
     * @param artist
     *            the artist
     * @param album
     *            the album
     */
    private void savePicture(Image image, File path, String artist, String album) {
        String imageFileName = StringUtils.getString(path.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, artist, "_", album, "_Cover.png");
        try {
            ImageUtils.writeImageToFile(image, imageFileName);
        } catch (IOException e) {
            getLogger().internalError(e);
        }
    }

    /**
     * Sets the format name of the encoder.
     * 
     * @param encoder
     *            the new format name of the encoder
     */
    void setEncoder(String encoder) {
        ApplicationState.getInstance().setEncoder(encoder);
    }

    /**
     * Sets the encoder quality.
     * 
     * @param quality
     *            the new encoder quality
     */
    void setEncoderQuality(String quality) {
        ApplicationState.getInstance().setEncoderQuality(quality);
    }

    /**
     * Sets the used filename pattern.
     * 
     * @param fileNamePattern
     *            The filename pattern used
     */
    void setFileNamePattern(String fileNamePattern) {
        ApplicationState.getInstance().setCdRipperFileNamePattern(fileNamePattern);
    }

    /**
     * Sets CD correction
     * 
     * @param useCdErrorCorrection
     *            True if cd correction should be set
     */
    void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
        ApplicationState.getInstance().setUseCdErrorCorrection(useCdErrorCorrection);
    }

    /**
     * Start cd ripper.
     */
    public void startCdRipper() {
        interrupted = false;
        final RipCdDialog dialog = GuiHandler.getInstance().getRipCdDialog();
        GuiHandler.getInstance().showIndeterminateProgressDialog("");

        SwingWorker<CDInfo, Void> getCdInfoAndStartRipping = new SwingWorker<CDInfo, Void>() {
            @Override
            protected CDInfo doInBackground() throws Exception {
                if (!testTools()) {
                    return null;
                }
                ripper = new CdRipper();
                ripper.setNoCdListener(new NoCdListener() {
                    @Override
                    public void noCd() {
                        getLogger().error(LogCategories.RIPPER, "No cd inserted");
                        interrupted = true;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                GuiHandler.getInstance().hideIndeterminateProgressDialog();
                                GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("NO_CD_INSERTED"));
                            }
                        });
                    }
                });
                return ripper.getCDInfo();
            }

            @Override
            protected void done() {
                GuiHandler.getInstance().hideIndeterminateProgressDialog();
                CDInfo cdInfo;
                try {
                    cdInfo = get();
                    if (cdInfo != null) {
                        ControllerProxy.getInstance().getRipCdDialogController().showCdInfo(cdInfo, RepositoryHandler.getInstance().getPathForNewAudioFilesRipped());
                        if (!ControllerProxy.getInstance().getRipCdDialogController().isCancelled()) {
                            String artist = ControllerProxy.getInstance().getRipCdDialogController().getArtist();
                            String album = ControllerProxy.getInstance().getRipCdDialogController().getAlbum();
                            int year = ControllerProxy.getInstance().getRipCdDialogController().getYear();
                            String genre = ControllerProxy.getInstance().getRipCdDialogController().getGenre();
                            String folder = ControllerProxy.getInstance().getRipCdDialogController().getFolder();
                            List<Integer> tracks = dialog.getTracksSelected();
                            List<String> trckNames = dialog.getTrackNames();
                            List<String> artistNames = dialog.getArtistNames();
                            List<String> composerNames = dialog.getComposerNames();
                            setUseCdErrorCorrection(dialog.getUseCdErrorCorrection().isSelected());
                            setEncoder(dialog.getFormat().getSelectedItem().toString());
                            setEncoderQuality(dialog.getQuality());
                            setFileNamePattern(dialog.getFileNamePattern());
                            importSongs(folder, artist, album, year, genre, tracks, trckNames, artistNames, composerNames, dialog.getFormat().getSelectedItem().toString(), dialog
                                    .getQuality(), dialog.getUseCdErrorCorrection().isSelected());
                        } else {
                            setUseCdErrorCorrection(dialog.getUseCdErrorCorrection().isSelected());
                            setEncoder(dialog.getFormat().getSelectedItem().toString());
                            setEncoderQuality(dialog.getQuality());
                            setFileNamePattern(dialog.getFileNamePattern());
                        }
                    }
                } catch (InterruptedException e) {
                    GuiHandler.getInstance().getRipCdDialog().setVisible(false);
                    getLogger().internalError(e);
                } catch (ExecutionException e) {
                    GuiHandler.getInstance().getRipCdDialog().setVisible(false);
                    getLogger().internalError(e);
                }
            }
        };
        getCdInfoAndStartRipping.execute();
    }

    /**
     * Test the presence of cdda2wav/icedax. Calls the test function from
     * Cdda2wav.java
     * 
     * @return Returns true if cdda2wav/icedax is present, false otherwise
     */
    boolean testTools() {
        if (!CdToWavConverter.testTool()) {
            getLogger().error(LogCategories.RIPPER, "Error testing \"cdda2wav\" or \"cdparanoia\". Check program is installed");
            GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("CDDA2WAV_NOT_FOUND"));
            return false;
        }
        return true;
    }
}
