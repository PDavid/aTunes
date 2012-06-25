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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRipperHandler;
import net.sourceforge.atunes.model.IRipperProgressDialog;
import net.sourceforge.atunes.model.IStateRipper;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.sanselan.ImageWriteException;

/**
 * Responsible of rip cds
 * @author alex
 *
 */
public final class RipperHandler extends AbstractHandler implements IRipperHandler {

    private static final String SEPARATOR = " - ";

	private static final String[] FILENAMEPATTERN = { StringUtils.getString(CdRipper.TRACK_NUMBER, SEPARATOR, CdRipper.TITLE_PATTERN),
            StringUtils.getString(CdRipper.ARTIST_PATTERN, SEPARATOR, CdRipper.ALBUM_PATTERN, SEPARATOR, CdRipper.TRACK_NUMBER, SEPARATOR, CdRipper.TITLE_PATTERN),
            StringUtils.getString(CdRipper.ARTIST_PATTERN, SEPARATOR, CdRipper.TITLE_PATTERN) };

    private CdRipper ripper;
    private volatile boolean interrupted;
    private boolean folderCreated;
    private IAlbumInfo albumInfo;
    
    private ITaskService taskService;
    
    private IIndeterminateProgressDialog indeterminateProgressDialog;
    
    private IRepositoryHandler repositoryHandler;
    
    private IWebServicesHandler webServicesHandler;
    
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
    private List<String> allEncoders;
    
    private IStateRipper stateRipper;
    
	private IUnknownObjectChecker unknownObjectChecker;
	
	private IDialogFactory dialogFactory;
	
	private IApplicationArguments applicationArguments;
	
	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}
	
	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
	
	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}
    
    /**
     * @param stateRipper
     */
    public void setStateRipper(IStateRipper stateRipper) {
		this.stateRipper = stateRipper;
	}

    /**
     * @param allEncoders
     */
    public void setAllEncoders(List<String> allEncoders) {
		this.allEncoders = allEncoders;
	}
  
    /**
     * @return
     */
    IRepositoryHandler getRepositoryHandler() {
		return repositoryHandler;
	}
    
    /**
     * @param interrupted
     */
    void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}
    
    /**
     * @return
     */
    IIndeterminateProgressDialog getIndeterminateProgressDialog() {
		return indeterminateProgressDialog;
	}
    
    /**
     * @param ripper
     */
    void setRipper(CdRipper ripper) {
		this.ripper = ripper;
	}
    
    CdRipper getRipper() {
		return ripper;
	}

    /**
     * @param albumInfo
     */
    void setAlbumInfo(IAlbumInfo albumInfo) {
		this.albumInfo = albumInfo;
	}
    
    /**
     * @param taskService
     */
    public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
    
    /**
     * @param webServicesHandler
     */
    public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
    
    @Override
    public void applicationStarted() {
    	this.repositoryHandler = getBean(IRepositoryHandler.class);
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
        if (repositoryHandler.isRepository(folder)) {
            repositoryHandler.addFilesAndRefresh(files);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#cancelProcess()
	 */
    @Override
	public void cancelProcess() {
        interrupted = true;
        ripper.stop();
        Logger.info("Process cancelled");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#fillSongTitles(java.lang.String, java.lang.String)
	 */
    @Override
	public void fillSongTitles(final String artist, final String album) {
        getRipCdDialogController().getComponentControlled().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getRipCdDialogController().getComponentControlled().getTitlesButton().setEnabled(false);
        new FillSongTitlesSwingWorker(this, artist, album, getBean(IWebServicesHandler.class)).execute();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#getEncoderName()
	 */
    @Override
	public String getEncoderName() {
        String encoderFormat = stateRipper.getEncoder();
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#getEncoderQualities(java.lang.String)
	 */
    @Override
	public String[] getEncoderQualities(String formatName) {
        if (getAvailableEncoders().containsKey(formatName)) {
            return getAvailableEncoders().get(formatName).getAvailableQualities();
        }
        return new String[0];
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#getEncoderDefaultQuality(java.lang.String)
	 */
    @Override
	public String getEncoderDefaultQuality(String formatName) {
        if (getAvailableEncoders().containsKey(formatName)) {
            return getAvailableEncoders().get(formatName).getDefaultQuality();
        }
        return "";
    }

    /**
     * Test for available encoders and returns a Map of the found encoders.
     * 
     * @return the available encoders
     */
    private Map<String, Encoder> getAvailableEncoders() {
        if (availableEncoders == null) {
            availableEncoders = new HashMap<String, Encoder>();

            // Test all encoders
            for (String encoderName : allEncoders) {
                try {
                    Class<?> encoderClass = Class.forName(encoderName);
                    if (Encoder.class.isAssignableFrom(encoderClass)) {
                        @SuppressWarnings("unchecked")
						Encoder instancedEncoder = getBean((Class<Encoder>)encoderClass);
                        if (instancedEncoder.testEncoder()) {
                            availableEncoders.put(instancedEncoder.getFormatName(), instancedEncoder);
                        }
                    } else {
                        Logger.error(encoderClass.getName(), " is not a subtype of ", Encoder.class.getName());
                    }
                } catch (ClassNotFoundException e) {
                    Logger.error(e);
                }
            }
            
            // Add simulator
            if (applicationArguments.isSimulateCD()) {
            	Encoder fakeEncoder = getBean(FakeEncoder.class);
            	availableEncoders.put(fakeEncoder.getFormatName(), fakeEncoder);
            }

            Logger.info("Available encoders: ", availableEncoders.keySet());
        }
        return availableEncoders;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#getAvailableEncodersNames()
	 */
    @Override
	public Set<String> getAvailableEncodersNames() {
    	return getAvailableEncoders().keySet();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#importSongs(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String, java.lang.String, boolean)
	 */
    @Override
	public void importSongs(String folder, final String artist, final String album, final int year, final String genre, final List<Integer> tracks, final List<String> trckNames, final List<String> artistNames, final List<String> composerNames, final String format, final String quality1, final boolean useParanoia) {
        // Disable import cd option in menu
        getBean(RipCDAction.class).setEnabled(false);

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
        ripper.setFileNamePattern(stateRipper.getCdRipperFileNamePattern());

        final IRipperProgressDialog dialog = getBean(IRipperProgressDialog.class);
        dialog.addCancelAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelProcess();
			}
		});
        
        // Get image from amazon if necessary
        if (albumInfo != null) {
            ImageIcon cover = getBean(IWebServicesHandler.class).getAlbumImage(albumInfo);
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
            protected Boolean doInBackground() {
                return ripper.ripTracks(tracks, trckNames, folderFile, artistNames, composerNames, useParanoia);
            }

            @Override
            protected void done() {
            	dialog.hideDialog();
                notifyFinishImport(filesImported, folderFile);
                // Enable import cd option in menu
                getBean(RipCDAction.class).setEnabled(true);
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
    private void notifyFinishImport(final List<File> filesImported, final File folder) {
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
                    if (folderCreated && !folder.delete()) {
                        Logger.error(StringUtils.getString(folder, " not deleted"));
                    }
                }
            };
            taskService.submitNow("Delete files after import", deleter);
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
    private void savePicture(ImageIcon image, File path, String artist, String album) {
        String imageFileName = StringUtils.getString(path.getAbsolutePath(), getOsManager().getFileSeparator(), artist, "_", album, "_Cover.png");
        try {
            ImageUtils.writeImageToFile(image.getImage(), imageFileName);
        } catch (IOException e) {
            Logger.error(e);
        } catch (ImageWriteException e) {
            Logger.error(e);
		}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#startCdRipper()
	 */
    @Override
	public void startCdRipper() {
        interrupted = false;
        final RipCdDialog dialog = getRipCdDialogController().getComponentControlled();
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
                indeterminateProgressDialog = dialogFactory.newDialog(IIndeterminateProgressDialog.class);
                indeterminateProgressDialog.setTitle(I18nUtils.getString("RIP_CD"));
                indeterminateProgressDialog.showDialog();
        	}
        });

        SwingWorker<CDInfo, Void> getCdInfoAndStartRipping = new GetCdInfoAndStartRippingSwingWorker(getOsManager(), stateRipper, this, dialog, webServicesHandler, unknownObjectChecker, applicationArguments);
        getCdInfoAndStartRipping.execute();
    }

    /**
     * Test the presence of cdda2wav/icedax. Calls the test function from
     * Cdda2wav
     * 
     * @return Returns true if cdda2wav/icedax is present, false otherwise
     */
    boolean testTools() {
        if (!CdToWavConverterTest.testTools(applicationArguments, getOsManager())) {
            Logger.error("Error testing \"cdda2wav\" or \"cdparanoia\". Check program is installed");
            SwingUtilities.invokeLater(new ShowErrorDialogRunnable());
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
            ripCdDialogController = new RipCdDialogController(dialogFactory.newDialog(RipCdDialog.class), stateRipper, getOsManager(), repositoryHandler, this);
        }
        return ripCdDialogController;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#isRipSupported()
	 */
	@Override
	public boolean isRipSupported() {
		return getOsManager().isRipSupported();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#getFilenamePatterns()
	 */
	@Override
	public String[] getFilenamePatterns() {
		return Arrays.copyOf(FILENAMEPATTERN, FILENAMEPATTERN.length);
	}
}
