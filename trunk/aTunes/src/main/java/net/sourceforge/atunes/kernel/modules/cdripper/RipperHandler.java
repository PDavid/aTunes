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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryAutoRefresher;
import net.sourceforge.atunes.kernel.modules.tags.Genres;
import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
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
 * 
 * @author alex
 * 
 */
public final class RipperHandler extends AbstractHandler implements
		IRipperHandler {

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

	private Genres genresHelper;

	private ILocalAudioObjectFactory localAudioObjectFactory;

	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(
			final ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}

	/**
	 * @param genresHelper
	 */
	public void setGenresHelper(final Genres genresHelper) {
		this.genresHelper = genresHelper;
	}

	/**
	 * @param repositoryRefresher
	 */
	public void setRepositoryRefresher(
			final RepositoryAutoRefresher repositoryRefresher) {
		this.repositoryRefresher = repositoryRefresher;
	}

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			final IApplicationArguments applicationArguments) {
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
		return this.repositoryHandler;
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
		return this.indeterminateProgressDialog;
	}

	/**
	 * @param ripper
	 */
	void setRipper(final CdRipper ripper) {
		this.ripper = ripper;
	}

	CdRipper getRipper() {
		return this.ripper;
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
	private void addFilesToRepositoryAndRefresh(
			final List<ILocalAudioObject> files, final File folder) {
		if (this.repositoryHandler.isRepository(folder)) {
			this.repositoryHandler.addAudioObjectsAndRefresh(files);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#cancelProcess
	 * ()
	 */
	@Override
	public void cancelProcess() {
		this.interrupted = true;
		this.ripper.stop();
		Logger.info("Process cancelled");
		this.repositoryRefresher.start();
	}

	@Override
	public void fillSongTitles(final String artist, final String album) {
		getBean(FillSongTitlesBackgroundWorker.class).retrieveTitles(artist,
				album);
	}

	@Override
	public String getEncoderName() {
		String encoderFormat = this.stateRipper.getEncoder();
		if (getAvailableEncoders().containsKey(encoderFormat)) {
			return encoderFormat;
		}
		// Not available, check what is the available encoder with higher
		// priority (less index value)
		Encoder higherPriorityEncoder = null;
		for (Encoder availableEncoder : getAvailableEncoders().values()) {
			if (higherPriorityEncoder == null) {
				higherPriorityEncoder = availableEncoder;
			} else {
				int encoderIndex = this.allEncoders.indexOf(availableEncoder
						.getClass().getName());
				if (encoderIndex != -1
						&& encoderIndex < this.allEncoders
								.indexOf(higherPriorityEncoder.getClass()
										.getName())) {
					higherPriorityEncoder = availableEncoder;
				}
			}
		}
		if (higherPriorityEncoder != null) {
			return higherPriorityEncoder.getFormatName();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#
	 * getEncoderQualities(java.lang.String)
	 */
	@Override
	public String[] getEncoderQualities(final String formatName) {
		if (getAvailableEncoders().containsKey(formatName)) {
			return getAvailableEncoders().get(formatName)
					.getAvailableQualities();
		}
		return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#
	 * getEncoderDefaultQuality(java.lang.String)
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
		if (this.availableEncoders == null) {
			this.availableEncoders = new HashMap<String, Encoder>();

			// Test all encoders
			for (String encoderName : this.allEncoders) {
				try {
					Class<?> encoderClass = Class.forName(encoderName);
					if (Encoder.class.isAssignableFrom(encoderClass)) {
						@SuppressWarnings("unchecked")
						Encoder instancedEncoder = getBean((Class<Encoder>) encoderClass);
						if (instancedEncoder.testEncoder()) {
							this.availableEncoders.put(
									instancedEncoder.getFormatName(),
									instancedEncoder);
						}
					} else {
						Logger.error(encoderClass.getName(),
								" is not a subtype of ",
								Encoder.class.getName());
					}
				} catch (ClassNotFoundException e) {
					Logger.error(e);
				}
			}

			// Add simulator
			if (this.applicationArguments.isSimulateCD()) {
				Encoder fakeEncoder = getBean(FakeEncoder.class);
				this.availableEncoders.put(fakeEncoder.getFormatName(),
						fakeEncoder);
			}

			Logger.info("Available encoders: ", this.availableEncoders.keySet());
		}
		return this.availableEncoders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#
	 * getAvailableEncodersNames()
	 */
	@Override
	public Set<String> getAvailableEncodersNames() {
		return getAvailableEncoders().keySet();
	}

	@Override
	public void importSongs(final CDMetadata metadata, final String format,
			final String quality1, final boolean useParanoia) {
		final File folder = getBean(CdRipperFolderNameCreator.class).getFolder(
				metadata);
		if (folder == null) {
			Logger.error("Could not create folder to import cd");
			return;
		}

		// Disable repository refresh
		this.repositoryRefresher.stop();

		// Disable import cd option in menu
		getBean(RipCDAction.class).setEnabled(false);

		// Prepares commands for the encoder
		// Get encoder to be used
		Encoder encoder = getAvailableEncoders().get(format);
		encoder.setQuality(quality1);

		this.ripper.setEncoder(encoder);
		this.ripper.setCdMetadata(metadata);

		final IRipperProgressDialog dialog = getBean(IRipperProgressDialog.class);
		dialog.addCancelAction(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				cancelProcess();
			}
		});

		// Get image from amazon if necessary
		if (this.albumInfo != null) {
			ImageIcon cover = getBean(IWebServicesHandler.class).getAlbumImage(
					this.albumInfo);
			if (cover != null) {
				dialog.setCover(cover);
				savePicture(cover, folder, metadata.getAlbumArtist(),
						metadata.getAlbum());
			}
		}

		dialog.setArtistAndAlbum(metadata.getAlbumArtist(), metadata.getAlbum());

		dialog.setTotalProgressBarLimits(0, metadata.getTracks().size());
		dialog.setTotalProgressValue(0);

		final List<File> filesImported = new ArrayList<File>();

		this.ripper.setDecoderListener(new DecoderProgressListener(dialog));
		this.ripper.setEncoderListener(new EncoderProgressListener(dialog));
		this.ripper.setTotalProgressListener(new TotalProgressListener(dialog,
				filesImported));

		getBean(RipTracksBackgroundWorker.class).ripTracks(ripper, dialog,
				folder, useParanoia, filesImported);
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
		if (this.interrupted) { // If process is interrupted delete all imported
			// files
			Runnable deleter = new Runnable() {
				@Override
				public void run() {
					for (File f : filesImported) {
						if (!f.delete()) {
							Logger.error(StringUtils.getString(f,
									" not deleted"));
						}
					}

					// Wait two seconds to assure filesImported are deleted
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						Logger.error(e);
					}
					if (RipperHandler.this.folderCreated && !folder.delete()) {
						Logger.error(StringUtils.getString(folder,
								" not deleted"));
					}
				}
			};
			this.taskService.submitNow("Delete files after import", deleter);
		} else {
			List<ILocalAudioObject> audioObjects = new ArrayList<ILocalAudioObject>();
			for (File f : filesImported) {
				audioObjects.add(this.localAudioObjectFactory
						.getLocalAudioObject(f));
			}

			addFilesToRepositoryAndRefresh(audioObjects, folder);
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
	private void savePicture(final ImageIcon image, final File path,
			final String artist, final String album) {
		String imageFileName = StringUtils.getString(FileUtils.getPath(path),
				getOsManager().getFileSeparator(), artist, "_", album,
				"_Cover.png");
		try {
			ImageUtils.writeImageToFile(image.getImage(), imageFileName);
		} catch (IOException e) {
			Logger.error(e);
		} catch (ImageWriteException e) {
			Logger.error(e);
		}
	}

	@Override
	public void startCdRipper() {
		this.interrupted = false;
		final RipCdDialog dialog = getRipCdDialogController()
				.getComponentControlled();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				RipperHandler.this.indeterminateProgressDialog = RipperHandler.this.dialogFactory
						.newDialog(IIndeterminateProgressDialog.class);
				RipperHandler.this.indeterminateProgressDialog
						.setTitle(I18nUtils.getString("RIP_CD"));
				RipperHandler.this.indeterminateProgressDialog.showDialog();
			}
		});

		GetCdInfoAndStartRippingBackgroundWorker worker = getBean(GetCdInfoAndStartRippingBackgroundWorker.class);
		worker.setDialog(dialog);
		worker.execute();
	}

	/**
	 * Test the presence of cdda2wav/icedax. Calls the test function from
	 * Cdda2wav
	 * 
	 * @return Returns true if cdda2wav/icedax is present, false otherwise
	 */
	boolean testTools() {
		if (!CdToWavConverterTest.testTools(this.applicationArguments,
				getOsManager())) {
			Logger.error("Error testing \"cdda2wav\" or \"cdparanoia\". Check program is installed");
			GuiUtils.callInEventDispatchThread(new Runnable() {
				@Override
				public void run() {
					RipperHandler.this.dialogFactory.newDialog(
							IErrorDialog.class).showErrorDialog(
							I18nUtils.getString("CDDA2WAV_NOT_FOUND"));
				}

			});
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
		if (this.ripCdDialogController == null) {
			this.ripCdDialogController = new RipCdDialogController(
					this.dialogFactory.newDialog(RipCdDialog.class),
					this.stateRipper, this, this.genresHelper);
		}
		return this.ripCdDialogController;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.atunes.kernel.modules.cdripper.IRipperHandler#isRipSupported
	 * ()
	 */
	@Override
	public boolean isRipSupported() {
		return getOsManager().isRipSupported();
	}
}
