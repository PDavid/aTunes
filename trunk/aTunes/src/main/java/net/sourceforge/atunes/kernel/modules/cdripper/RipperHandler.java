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

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.NoCdListener;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.Encoder;
import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IRipperProgressDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class RipperHandler extends AbstractHandler {

	// Encoder options and file name patterns. Add here for more options
    public static final String[] FILENAMEPATTERN = { StringUtils.getString(CdRipper.TRACK_NUMBER, " - ", CdRipper.TITLE_PATTERN),
            StringUtils.getString(CdRipper.ARTIST_PATTERN, " - ", CdRipper.ALBUM_PATTERN, " - ", CdRipper.TRACK_NUMBER, " - ", CdRipper.TITLE_PATTERN),
            StringUtils.getString(CdRipper.ARTIST_PATTERN, " - ", CdRipper.TITLE_PATTERN) };

    private final class FillSongTitlesSwingWorker extends
			SwingWorker<IAlbumInfo, Void> {
		private final String artist;
		private final String album;

		private FillSongTitlesSwingWorker(String artist, String album) {
			this.artist = artist;
			this.album = album;
		}

		@Override
		protected IAlbumInfo doInBackground() throws Exception {
		    return Context.getBean(IWebServicesHandler.class).getAlbum(artist, album);
		}

		@Override
		protected void done() {
		    try {
		        if (get() != null) {
		            albumInfo = get();
		            List<String> trackNames = new ArrayList<String>();
		            for (TrackInfo trackInfo : get().getTracks()) {
		                trackNames.add(trackInfo.getTitle());
		            }
		            getRipCdDialogController().getComponentControlled().updateTrackNames(trackNames);
		        }
		    } catch (InterruptedException e) {
		        Logger.error(e);
		    } catch (ExecutionException e) {
		        Logger.error(e);
		    } finally {
		    	getRipCdDialogController().getComponentControlled().setCursor(Cursor.getDefaultCursor());
		    	getRipCdDialogController().getComponentControlled().getTitlesButton().setEnabled(true);
		    }
		}
	}

	private final class GetCdInfoAndStartRippingSwingWorker extends
			SwingWorker<CDInfo, Void> {
		private final RipCdDialog dialog;

		private GetCdInfoAndStartRippingSwingWorker(RipCdDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		protected CDInfo doInBackground() throws Exception {
		    if (!testTools()) {
		        return null;
		    }
		    ripper = new CdRipper(getOsManager());
		    ripper.setNoCdListener(new NoCdListener() {
		        @Override
		        public void noCd() {
		            Logger.error("No cd inserted");
		            interrupted = true;
		            SwingUtilities.invokeLater(new Runnable() {
		                @Override
		                public void run() {
		                    GuiHandler.getInstance().hideIndeterminateProgressDialog();
		                    Context.getBean(IErrorDialog.class).showErrorDialog(getFrame(), I18nUtils.getString("NO_CD_INSERTED"));
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
		        	getRipCdDialogController().showCdInfo(cdInfo, RepositoryHandler.getInstance().getPathForNewAudioFilesRipped());
		            if (!getRipCdDialogController().isCancelled()) {
		                String artist = getRipCdDialogController().getArtist();
		                String album = getRipCdDialogController().getAlbum();
		                int year = getRipCdDialogController().getYear();
		                String genre = getRipCdDialogController().getGenre();
		                String folder = getRipCdDialogController().getFolder();
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
		    	getRipCdDialogController().getComponentControlled().setVisible(false);
		        Logger.error(e);
		    } catch (ExecutionException e) {
		    	getRipCdDialogController().getComponentControlled().setVisible(false);
		        Logger.error(e);
		    }
		}
	}

	private static class ShowErrorDialogRunnable implements Runnable {
		
		private IFrame frame;
		
		public ShowErrorDialogRunnable(IFrame frame) {
			this.frame = frame;
		}
		
        @Override
        public void run() {
        	Context.getBean(IErrorDialog.class).showErrorDialog(frame, I18nUtils.getString("CDDA2WAV_NOT_FOUND"));
        }
    }

    private static final class TotalProgressListener implements ProgressListener {
        private final IRipperProgressDialog dialog;
        private final List<File> filesImported;

        private TotalProgressListener(IRipperProgressDialog dialog, List<File> filesImported) {
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

    private static final class EncoderProgressListener implements ProgressListener {
        private final IRipperProgressDialog dialog;

        private EncoderProgressListener(IRipperProgressDialog dialog) {
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

    private static final class DecoderProgressListener implements ProgressListener {
        private final IRipperProgressDialog dialog;

        private DecoderProgressListener(IRipperProgressDialog dialog) {
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

    private CdRipper ripper;
    private volatile boolean interrupted;
    private boolean folderCreated;
    private IAlbumInfo albumInfo;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Map of available encoders in the system: key is format name, value is
     * encoder
     */
    private Map<String, Encoder> availableEncoders;

	/**
	 * Controller
	 */
	private RipCdDialogController ripCdDialogController;

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
    }

    @Override
    public void applicationStateChanged(IState newState) {
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    }

    @Override
    protected void initHandler() {
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
        Logger.info("Process cancelled");
    }

    /**
     * Fill songs titles
     * 
     * @param artist
     *            the artist
     * @param album
     *            the album
     */
    void fillSongTitles(final String artist, final String album) {
        getRipCdDialogController().getComponentControlled().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getRipCdDialogController().getComponentControlled().getTitlesButton().setEnabled(false);
        new FillSongTitlesSwingWorker(artist, album).execute();
    }

    /**
     * Gets format name of the encoder which was used for ripping CD's. If
     * encoder is not available then get one of the available
     * 
     * @return Return the format name of the encoder used the previous time or
     *         default one if it's available
     */
    String getEncoder() {
        String encoderFormat = getState().getEncoder();
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
    String[] getEncoderQualities(String formatName) {
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
    String getEncoderDefaultQuality(String formatName) {
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
    String getEncoderQuality() {
        return getState().getEncoderQuality();
    }

    /**
     * Returns the filename pattern which is used.
     * 
     * @return The filename pattern
     */
    String getFileNamePattern() {
        return getState().getCdRipperFileNamePattern();
    }

    /**
     * Test for available encoders and returns a Map of the found encoders.
     * 
     * @return the available encoders
     */
    Map<String, Encoder> getAvailableEncoders() {
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
                        Logger.error(encoderClass + " is not a subtype of " + Encoder.class);
                    }
                } catch (ClassNotFoundException e) {
                    Logger.error(e);
                } catch (NoSuchMethodException e) {
                    Logger.error(e);
                } catch (IllegalAccessException e) {
                    Logger.error(e);
                } catch (InvocationTargetException e) {
                    Logger.error(e);
                } catch (InstantiationException e) {
                    Logger.error(e);
                }
            }

            Logger.info(StringUtils.getString("Available encoders: ", availableEncoders.keySet()));
        }
        return availableEncoders;
    }

    /**
     * Indicates if the user requested cd error correction
     * 
     * @return true if error correction for cd ripping should be used
     */
    boolean getCdErrorCorrection() {
        return getState().isUseCdErrorCorrection();
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
                Logger.error("Folder could not be created");
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

        final IRipperProgressDialog dialog = Context.getBean(IRipperProgressDialog.class);
        dialog.addCancelAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelProcess();
			}
		});
        
        // Get image from amazon if necessary
        if (albumInfo != null) {
            Image cover = Context.getBean(IWebServicesHandler.class).getAlbumImage(albumInfo);
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
            	dialog.hideDialog();
                notifyFinishImport(filesImported, folderFile);
                // Enable import cd option in menu
                Actions.getAction(RipCDAction.class).setEnabled(true);
            }
        }.execute();

        dialog.showDialog();
    }

    /**
     * Notify finish import.
     * 
     * @param filesImported
     *            the files imported
     * @param folder
     *            the folder
     */
    void notifyFinishImport(final List<File> filesImported, final File folder) {
        if (interrupted) { // If process is interrupted delete all imported files
            Runnable deleter = new Runnable() {
                @Override
                public void run() {
                    for (File f : filesImported) {
                        if (!f.delete()) {
                        	Logger.error(StringUtils.getString(f, " not deleted"));
                        }
                    }

                    // Wait two seconds to assure filesImported are deleted
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Logger.error(e);
                    }
                    if (folderCreated) {
                        if (!folder.delete()) {
                        	Logger.error(StringUtils.getString(folder, " not deleted"));
                        }
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
        String imageFileName = StringUtils.getString(path.getAbsolutePath(), getOsManager().getFileSeparator(), artist, "_", album, "_Cover.png");
        try {
            ImageUtils.writeImageToFile(image, imageFileName);
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    /**
     * Sets the format name of the encoder.
     * 
     * @param encoder
     *            the new format name of the encoder
     */
    void setEncoder(String encoder) {
        getState().setEncoder(encoder);
    }

    /**
     * Sets the encoder quality.
     * 
     * @param quality
     *            the new encoder quality
     */
    void setEncoderQuality(String quality) {
        getState().setEncoderQuality(quality);
    }

    /**
     * Sets the used filename pattern.
     * 
     * @param fileNamePattern
     *            The filename pattern used
     */
    void setFileNamePattern(String fileNamePattern) {
        getState().setCdRipperFileNamePattern(fileNamePattern);
    }

    /**
     * Sets CD correction
     * 
     * @param useCdErrorCorrection
     *            True if cd correction should be set
     */
    void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
        getState().setUseCdErrorCorrection(useCdErrorCorrection);
    }

    /**
     * Start cd ripper.
     */
    public void startCdRipper() {
        interrupted = false;
        final RipCdDialog dialog = getRipCdDialogController().getComponentControlled();
        GuiHandler.getInstance().showIndeterminateProgressDialog(I18nUtils.getString("RIP_CD"));

        SwingWorker<CDInfo, Void> getCdInfoAndStartRipping = new GetCdInfoAndStartRippingSwingWorker(dialog);
        getCdInfoAndStartRipping.execute();
    }

    /**
     * Test the presence of cdda2wav/icedax. Calls the test function from
     * Cdda2wav.java
     * 
     * @return Returns true if cdda2wav/icedax is present, false otherwise
     */
    boolean testTools() {
        if (!AbstractCdToWavConverter.testTool()) {
            Logger.error("Error testing \"cdda2wav\" or \"cdparanoia\". Check program is installed");
            SwingUtilities.invokeLater(new ShowErrorDialogRunnable(getFrame()));
            return false;
        }
        return true;
    }
    
    /**
     * Gets the rip cd dialog controller.
     * 
     * @return the rip cd dialog controller
     */
    RipCdDialogController getRipCdDialogController() {
        if (ripCdDialogController == null) {
            ripCdDialogController = new RipCdDialogController(new RipCdDialog(getFrame().getFrame()), getState(), getOsManager());
        }
        return ripCdDialogController;
    }

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

	/**
	 * Returns true if rip CDs is supported in current system
	 * @return
	 */
	public boolean isRipSupported() {
		return getOsManager().isRipSupported();
	}

}
