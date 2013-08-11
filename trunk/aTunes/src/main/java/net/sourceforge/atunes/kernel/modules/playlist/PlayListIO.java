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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * The Class PlayListIO.
 */
public final class PlayListIO implements IPlayListIOService {

	/** The Constant M3U_FILE_EXTENSION. */
	public static final String PLAYLIST_M3U_FILE_EXTENSION = "m3u";

	/**
	 * Extension for play lists generated with aTunes
	 */
	public static final String PLAYLIST_FILE_EXTENSION = "atu";

	/**
	 * Extension for dynamic play lists generated with aTunes
	 */
	public static final String DYNAMIC_PLAYLIST_FILE_EXTENSION = "datu";

	private static final String HTTP_PREFIX = "http://";

	private IRepositoryHandler repositoryHandler;

	private IRadioHandler radioHandler;

	private ILocalAudioObjectFactory localAudioObjectFactory;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param radioHandler
	 */
	public void setRadioHandler(final IRadioHandler radioHandler) {
		this.radioHandler = radioHandler;
	}

	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(
			final ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param fileNames
	 * @return
	 */
	@Override
	public List<IAudioObject> getAudioObjectsFromFileNamesList(
			final List<String> fileNames) {
		List<IAudioObject> result = new ArrayList<IAudioObject>();
		for (String fileName : fileNames) {
			result.add(getAudioObjectOrCreate(fileName));
		}
		return result;
	}

	/**
	 * Returns an AudioObject given a resource name or instantiates it if does
	 * not exist. A resource can be a file or URL at this moment
	 * 
	 * @param repositoryHandler
	 * @param resourceName
	 * @param radioHandler
	 * @param localAudioObjectFactory
	 * @return
	 */
	@Override
	public IAudioObject getAudioObjectOrCreate(final String resourceName) {
		IAudioObject ao = null;

		// It's an online radio
		if (resourceName.startsWith(HTTP_PREFIX)) {
			ao = this.radioHandler.getRadioIfLoaded(resourceName);
			if (ao == null) {
				// If radio is not previously loaded in application then create
				// a new Radio object with resource as name and url and leave
				// label empty
				ao = this.radioHandler.createRadio(resourceName, resourceName,
						null);
			}
			return ao;
		}

		// It's not an online radio, then it must be an AudioFile
		ao = this.repositoryHandler.getFileIfLoaded(resourceName);
		if (ao == null) {
			// If LocalAudioObject is not previously loaded in application then
			// create a new AudioFile
			ao = this.localAudioObjectFactory.getLocalAudioObject(new File(
					resourceName));
		}
		return ao;
	}

	/**
	 * Returns a list of files contained in a play list file.
	 * 
	 * @param file
	 * @return
	 */
	@Override
	public List<IAudioObject> getFilesFromList(final File file) {
		List<String> list = read(file);
		return getAudioObjectsFromFileNamesList(list);
	}

	/**
	 * FileFilter to be used when loading and saving a play list file.
	 * 
	 * @return the playlist file filter
	 */
	@Override
	public final FilenameFilter getPlaylistFileFilter() {
		return new PlayListFileFilter();
	}

	@Override
	public FilenameFilter getDynamicPlaylistFileFilter() {
		return new DynamicPlayListFileFilter();
	}

	/**
	 * Checks if is valid play list.
	 * 
	 * @param playListFile
	 *            the play list file
	 * 
	 * @return true, if is valid play list
	 */
	@Override
	public boolean isValidPlayList(final String playListFile) {
		File f = new File(playListFile);
		return (playListFile.endsWith(PLAYLIST_M3U_FILE_EXTENSION)
				|| playListFile.endsWith(PLAYLIST_FILE_EXTENSION) || playListFile
					.endsWith(DYNAMIC_PLAYLIST_FILE_EXTENSION)) && f.exists();
	}

	@Override
	public boolean isDynamicPlayList(final File file) {
		return file != null
				&& file.exists()
				&& file.getAbsolutePath().endsWith(
						DYNAMIC_PLAYLIST_FILE_EXTENSION);
	}

	/**
	 * This function reads the filenames from the playlist file
	 * 
	 * @return Returns an List of files of the playlist as String.
	 */
	@Override
	public List<String> read(final File file) {
		if (FilenameUtils.getExtension(FileUtils.getPath(file))
				.equalsIgnoreCase(PLAYLIST_FILE_EXTENSION)) {
			return this.beanFactory.getBean(PlayListReader.class).read(file);
		}
		return this.beanFactory.getBean(M3UPlayListReader.class).read(file);
	}

	@Override
	public void readDynamicPlayList(final File file) {
		this.beanFactory.getBean(DynamicPlayListReader.class).read(file);
	}

	/**
	 * Writes a play list to a file.
	 * 
	 * @param playlist
	 * @param file
	 * @return
	 */
	@Override
	public boolean writeM3U(final IPlayList playlist, final File file) {
		return this.beanFactory.getBean(M3UPlayListWriter.class).writeM3U(
				playlist, file);
	}

	@Override
	public File checkPlayListFileName(final File file) {
		return checkPlayListExtension(file, PLAYLIST_FILE_EXTENSION);
	}

	@Override
	public File checkM3UPlayListFileName(final File file) {
		return checkPlayListExtension(file, PLAYLIST_M3U_FILE_EXTENSION);
	}

	@Override
	public File checkDynamicPlayListFileName(final File file) {
		return checkPlayListExtension(file, DYNAMIC_PLAYLIST_FILE_EXTENSION);
	}

	private File checkPlayListExtension(final File file, final String extension) {
		if (!file.getName().toUpperCase()
				.endsWith("." + extension.toUpperCase())) {
			return new File(StringUtils.getString(FileUtils.getPath(file), ".",
					extension));
		}
		return file;
	}

	@Override
	public FilenameFilter getPlaylistM3UFileFilter() {
		return new PlayListM3UFileFilter();
	}

	@Override
	public boolean write(final IPlayList playlist, final File file) {
		if (playlist.isDynamic()) {
			return this.beanFactory.getBean(DynamicPlayListWriter.class).write(
					playlist, file);
		}
		return this.beanFactory.getBean(PlayListWriter.class).write(playlist,
				file);
	}

	@Override
	public boolean write(final List<IAudioObject> audioObjects, final File file) {
		return this.beanFactory.getBean(PlayListWriter.class).write(
				audioObjects, file);
	}

	@Override
	public boolean writeM3U(final List<IAudioObject> audioObjects,
			final File file) {
		return this.beanFactory.getBean(M3UPlayListWriter.class).writeM3U(
				audioObjects, file);
	}

	@Override
	public FilenameFilter getAllAcceptedPlaylistsFileFilter() {
		return new AllAcceptedPlaylistsFileFilter();
	}
}
