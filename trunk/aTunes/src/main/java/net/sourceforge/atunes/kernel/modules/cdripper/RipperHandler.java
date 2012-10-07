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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryAutoRefresher;
import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRipperHandler;
import net.sourceforge.atunes.model.IRipperProgressDialog;
import net.sourceforge.atunes.model.IStateRipper;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.FileUtils;
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

	private CdRipper ripper;
	private volatile boolean interrupted;
	private boolean folderCreated;
	private IAlbumInfo albumInfo;

	private ITaskService taskService;

	private IIndeterminateProgressDialog indeterminateProgressDialog;

	private IRepositoryHandler repositoryHandler;

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

	private IDialogFactory dialogFactory;

	private IApplicationArguments applicationArguments;

	private RepositoryAutoRefresher repositoryRefresher;

	private CdRipperFolderNameCreator cdRipperFolderNameCreator;

	/**
	 * @param cdRipperFolderNameCreator
	 */
	public void setCdRipperFolderNameCreator(final CdRipperFolderNameCreator cdRipperFolderNameCreator) {
		this.cdRipperFolderNameCreator = cdRipperFolderNameCreator;
	}

	/**
	 * @param repositoryRefresher
	 */
	public void setRepositoryRefresher(final RepositoryAutoRefresher repositoryRefresher) {
		this.repositoryRefresher = repositoryRefresher;
	}

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(final IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateRipper
	 */
	public void setStateRipper(final IStateRipper stateRipper) {
		this.stateRipper = stateRipper;
	}

	/**
	 * @param allEncoders
	 */
	public void setAllEncoders(final List<String> allEncoders) {
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
	void setInterrupted(final boolean interrupted) {
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
	void setRipper(final CdRipper ripper) {
		this.ripper = ripper;
	}

	CdRipper getRipper() {
		return ripper;
	}

	/**
	 * @param albumInfo
	 */
	void setAlbumInfo(final IAlbumInfo albumInfo) {
		this.albumInfo = albumInfo;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
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
	private void addFilesToRepositoryAndRefresh(final List<File> files, final File folder) {
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
		repositoryRefresher.start();
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
	public String[] getEncoderQualities(final String formatName) {
		if (getAvailableEncoders().containsKey(formatName)) {
			return getAvailableEncoders().get(formatName).getAvailableQualities();
		}
		return new String[0];
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#getEncoderDefaultQuality(java.lang.String)
	 */
	@Override
	public String getEncoderDefaultQuality(final String formatName) {
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

	@Override
	public void importSongs(final CDMetadata metadata, final String format, final String quality1, final boolean useParanoia) {
		final File folder = cdRipperFolderNameCreator.getFolder(metadata);
		if (folder == null) {
			Logger.error("Could not create folder to import cd");
			return;
		}

		// Disable repository refresh
		repositoryRefresher.stop();

		// Disable import cd option in menu
		getBean(RipCDAction.class).setEnabled(false);

		// Prepares commands for the encoder
		// Get encoder to be used
		Encoder encoder = getAvailableEncoders().get(format);
		encoder.setQuality(quality1);

		ripper.setEncoder(encoder);
		ripper.setCdMetadata(metadata);

		final IRipperProgressDialog dialog = getBean(IRipperProgressDialog.class);
		dialog.addCancelAction(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				cancelProcess();
			}
		});

		// Get image from amazon if necessary
		if (albumInfo != null) {
			ImageIcon cover = getBean(IWebServicesHandler.class).getAlbumImage(albumInfo);
			if (cover != null) {
				dialog.setCover(cover);
				savePicture(cover, folder, metadata.getAlbumArtist(), metadata.getAlbum());
			}
		}

		dialog.setArtistAndAlbum(metadata.getAlbumArtist(), metadata.getAlbum());

		dialog.setTotalProgressBarLimits(0, metadata.getTracks().size());
		dialog.setTotalProgressValue(0);

		final List<File> filesImported = new ArrayList<File>();

		ripper.setDecoderListener(new DecoderProgressListener(dialog));
		ripper.setEncoderListener(new EncoderProgressListener(dialog));
		ripper.setTotalProgressListener(new TotalProgressListener(dialog, filesImported));

		new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() {
				return ripper.ripTracks(folder, useParanoia);
			}

			@Override
			protected void done() {
				dialog.hideDialog();
				notifyFinishImport(filesImported, folder);
				// Enable import cd option in menu
				getBean(RipCDAction.class).setEnabled(true);
				repositoryRefresher.start();
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
	private void savePicture(final ImageIcon image, final File path, final String artist, final String album) {
		String imageFileName = StringUtils.getString(FileUtils.getPath(path), getOsManager().getFileSeparator(), artist, "_", album, "_Cover.png");
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

		GetCdInfoAndStartRippingSwingWorker cdInfoAndStartRippingSwingWorker = getBean(GetCdInfoAndStartRippingSwingWorker.class);
		cdInfoAndStartRippingSwingWorker.setDialog(dialog);
		cdInfoAndStartRippingSwingWorker.execute();
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
			ripCdDialogController = new RipCdDialogController(dialogFactory.newDialog(RipCdDialog.class), stateRipper, this);
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
}
