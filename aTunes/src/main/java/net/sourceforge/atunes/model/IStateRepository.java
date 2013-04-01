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

package net.sourceforge.atunes.model;

import java.util.List;

/**
 * @author alex
 * 
 */
public interface IStateRepository extends IState {

	/**
	 * used in RepositoryFiller to build repository structure keys case
	 * sensitive or not
	 * 
	 * @return true if case structures genre and artist handled sensitive,
	 *         default = false for convenience
	 */
	boolean isKeyAlwaysCaseSensitiveInRepositoryStructure();

	/**
	 * enable case sensitive tree structure of artist and genre or merge keys
	 * case insensitive
	 * 
	 * @param caseSensitiveRepositoryStructureKeys
	 */
	void setKeyAlwaysCaseSensitiveInRepositoryStructure(
			boolean caseSensitiveRepositoryStructureKeys);

	/**
	 * Refresh repository automatically
	 * 
	 * @return
	 */
	int getAutoRepositoryRefreshTime();

	/**
	 * Refresh repository automatically
	 * 
	 * @param autoRepositoryRefreshTime
	 */
	void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime);

	/**
	 * last repository folders
	 * 
	 * @return
	 */
	List<String> getLastRepositoryFolders();

	/**
	 * last repository folders
	 * 
	 * @param lastRepositoryFolders
	 */
	void setLastRepositoryFolders(List<String> lastRepositoryFolders);

	/**
	 * File name pattern in import
	 * 
	 * @return
	 */
	String getImportFileNamePattern();

	/**
	 * File name pattern in export
	 * 
	 * @return
	 */
	String getExportFileNamePattern();

	/**
	 * File name pattern in import
	 * 
	 * @param importFileNamePattern
	 */
	void setImportFileNamePattern(String importFileNamePattern);

	/**
	 * File name pattern in export
	 * 
	 * @param exportFileNamePattern
	 */
	void setExportFileNamePattern(String exportFileNamePattern);

	/**
	 * Folder path pattern in import
	 * 
	 * @return
	 */
	String getImportFolderPathPattern();

	/**
	 * Folder path pattern in export
	 * 
	 * @return
	 */
	String getExportFolderPathPattern();

	/**
	 * Folder path pattern in import
	 * 
	 * @param importFolderPathPattern
	 */
	void setImportFolderPathPattern(String importFolderPathPattern);

	/**
	 * Folder path pattern in export
	 * 
	 * @param exportFolderPathPattern
	 */
	void setExportFolderPathPattern(String exportFolderPathPattern);

	/**
	 * Review tags before start import
	 * 
	 * @return
	 */
	boolean isReviewTagsBeforeImport();

	/**
	 * Review tags before start import
	 * 
	 * @param reviewTagsBeforeImport
	 */
	void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport);

	/**
	 * Change tags to original files before import
	 * 
	 * @return
	 */
	boolean isApplyChangesToSourceFilesBeforeImport();

	/**
	 * Change tags to original files before import
	 * 
	 * @param applyChangesToSourceFilesBeforeImport
	 */
	void setApplyChangesToSourceFilesBeforeImport(
			boolean applyChangesToSourceFilesBeforeImport);

	/**
	 * Set track numbers when importing
	 * 
	 * @return
	 */
	boolean isSetTrackNumbersWhenImporting();

	/**
	 * Set track numbers when importing
	 * 
	 * @param setTrackNumbersWhenImporting
	 */
	void setSetTrackNumbersWhenImporting(boolean setTrackNumbersWhenImporting);

	/**
	 * Set titles when importing
	 * 
	 * @return
	 */
	boolean isSetTitlesWhenImporting();

	/**
	 * Set titles when importing
	 * 
	 * @param setTitlesWhenImporting
	 */
	void setSetTitlesWhenImporting(boolean setTitlesWhenImporting);

	/**
	 * List of patterns
	 * 
	 * @return
	 */
	List<String> getRecognitionPatterns();

	/**
	 * List of patterns
	 * 
	 * @param recognitionPatterns
	 */
	void setRecognitionPatterns(List<String> recognitionPatterns);

	/**
	 * List of massive patterns
	 * 
	 * @return
	 */
	List<String> getMassiveRecognitionPatterns();

	/**
	 * List of massive patterns
	 * 
	 * @param massiveRecognitionPatterns
	 */
	void setMassiveRecognitionPatterns(List<String> massiveRecognitionPatterns);

	/**
	 * Command to execute before access repository
	 * 
	 * @return
	 */
	String getCommandBeforeAccessRepository();

	/**
	 * Command to execute before access repository
	 * 
	 * @param commandBeforeAccessRepository
	 */
	void setCommandBeforeAccessRepository(String commandBeforeAccessRepository);

	/**
	 * Command to execute after finish access to repository
	 * 
	 * @return
	 */
	String getCommandAfterAccessRepository();

	/**
	 * Command to execute after finish access to repository
	 * 
	 * @param commandAfterAccessRepository
	 */
	void setCommandAfterAccessRepository(String commandAfterAccessRepository);

	/**
	 * @return if ratings must be stored in file (tag) or not
	 */
	boolean isStoreRatingInFile();

	/**
	 * if ratings must be stored in file (tag) or not
	 * 
	 * @param storeRatingInFile
	 */
	void setStoreRatingInFile(boolean storeRatingInFile);

}
