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

import net.sourceforge.atunes.kernel.modules.pattern.PatternToFileTranslator;
import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateTime;

/**
 * Creates folders for CD rip
 * @author alex
 *
 */
public class CdRipperFolderNameCreator {

	private IRepositoryHandler repositoryHandler;

	private IStateRepository stateRepository;

	private IUnknownObjectChecker unknownObjectChecker;

	private IOSManager osManager;

	private PatternToFileTranslator patternToFileTranslator;

	/**
	 * @param patternToFileTranslator
	 */
	public void setPatternToFileTranslator(final PatternToFileTranslator patternToFileTranslator) {
		this.patternToFileTranslator = patternToFileTranslator;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(final IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * Returns a folder to store cd tracks
	 * @param metadata
	 * @return
	 */
	File getFolder(final CDMetadata metadata) {
		String folderNamePattern = stateRepository.getImportFolderPathPattern();
		String folder = null;
		if (StringUtils.isEmpty(folderNamePattern)) {
			folder = StringUtils.getString(unknownObjectChecker.getUnknownAlbum(), " - ", DateUtils.toPathString(new DateTime()));
		} else {
			folder = patternToFileTranslator.translateFromPatternToFolderName(metadata, folderNamePattern);
		}

		// Build absolute path with repository path
		File folderFile = new File(StringUtils.getString(repositoryHandler.getRepositoryPath(), osManager.getFileSeparator(), folder));
		if (!folderFile.exists() && !folderFile.mkdirs()) {
			Logger.error("Folder ", FileUtils.getPath(folderFile), " could not be created");
			return null;
		}
		return folderFile;
	}
}
