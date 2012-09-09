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

package net.sourceforge.atunes.model;

import java.util.List;

/**
 * @author alex
 *
 */
public interface IStateRepository {

	/**
	 * used in RepositoryFiller to build repository structure keys case sensitive or not
	 * @return true if case structures genre and artist handled sensitive, default = false for convenience
	 */
	public boolean isKeyAlwaysCaseSensitiveInRepositoryStructure();

	/**
	 * enable case sensitive tree structure of artist and genre or merge keys case insensitive
	 * @param caseSensitiveRepositoryStructureKeys
	 */
	public void setKeyAlwaysCaseSensitiveInRepositoryStructure(boolean caseSensitiveRepositoryStructureKeys);
	
	/**
	 * Refresh repository automatically
	 * @return
	 */
	public int getAutoRepositoryRefreshTime();

	/**
	 * Refresh repository automatically
	 * @param autoRepositoryRefreshTime
	 */
	public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime);
	
	/**
	 * last repository folders
	 * @return
	 */
	public List<String> getLastRepositoryFolders();

	/**
	 * last repository folders
	 * @param lastRepositoryFolders
	 */
	public void setLastRepositoryFolders(List<String> lastRepositoryFolders);

	/**
	 * File name pattern in import
	 * @return
	 */
	public String getImportFileNamePattern();

	/**
	 * File name pattern in export
	 * @return
	 */
	public String getExportFileNamePattern();

	/**
	 * File name pattern in import
	 * @param importFileNamePattern
	 */
	public void setImportFileNamePattern(String importFileNamePattern);

	/**
	 * File name pattern in export
	 * @param exportFileNamePattern
	 */
	public void setExportFileNamePattern(String exportFileNamePattern);

	/**
	 * Folder path pattern in import
	 * @return
	 */
	public String getImportFolderPathPattern();

	/**
	 * Folder path pattern in export
	 * @return
	 */
	public String getExportFolderPathPattern();

	/**
	 * Folder path pattern in import
	 * @param importFolderPathPattern
	 */
	public void setImportFolderPathPattern(String importFolderPathPattern);

	/**
	 * Folder path pattern in export
	 * @param exportFolderPathPattern
	 */
	public void setExportFolderPathPattern(String exportFolderPathPattern);

	/**
	 * Review tags before start import
	 * @return
	 */
	public boolean isReviewTagsBeforeImport();

	/**
	 * Review tags before start import
	 * @param reviewTagsBeforeImport
	 */
	public void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport);

	/**
	 * Change tags to original files before import
	 * @return
	 */
	public boolean isApplyChangesToSourceFilesBeforeImport();

	/**
	 * Change tags to original files before import
	 * @param applyChangesToSourceFilesBeforeImport
	 */
	public void setApplyChangesToSourceFilesBeforeImport(boolean applyChangesToSourceFilesBeforeImport);

	/**
	 * Set track numbers when importing
	 * @return
	 */
	public boolean isSetTrackNumbersWhenImporting();

	/**
	 * Set track numbers when importing
	 * @param setTrackNumbersWhenImporting
	 */
	public void setSetTrackNumbersWhenImporting(boolean setTrackNumbersWhenImporting);

	/**
	 * Set titles when importing
	 * @return
	 */
	public boolean isSetTitlesWhenImporting();

	/**
	 * Set titles when importing
	 * @param setTitlesWhenImporting
	 */
	public void setSetTitlesWhenImporting(boolean setTitlesWhenImporting);

	/**
	 * List of patterns
	 * @return
	 */
	public List<String> getRecognitionPatterns();

	/**
	 * List of patterns
	 * @param recognitionPatterns
	 */
	public void setRecognitionPatterns(List<String> recognitionPatterns);

	/**
	 * List of massive patterns
	 * @return
	 */
	public List<String> getMassiveRecognitionPatterns();

	/**
	 * List of massive patterns
	 * @param massiveRecognitionPatterns
	 */
	public void setMassiveRecognitionPatterns(List<String> massiveRecognitionPatterns);

	/**
	 * Command to execute before access repository
	 * @return
	 */
	public String getCommandBeforeAccessRepository();

	/**
	 * Command to execute before access repository
	 * @param commandBeforeAccessRepository
	 */
	public void setCommandBeforeAccessRepository(String commandBeforeAccessRepository);

	/**
	 * Command to execute after finish access to repository
	 * @return
	 */
	public String getCommandAfterAccessRepository();

	/**
	 * Command to execute after finish access to repository
	 * @param commandAfterAccessRepository
	 */
	public void setCommandAfterAccessRepository(String commandAfterAccessRepository);
}
