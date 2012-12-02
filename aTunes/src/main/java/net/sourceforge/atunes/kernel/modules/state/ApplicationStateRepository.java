/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.List;

import net.sourceforge.atunes.model.IStateRepository;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateRepository implements IStateRepository {

	/**
     * Component responsible of store state
     */
    private IStateStore stateStore;
    
    /**
     * Sets state store
     * @param store
     */
    public void setStateStore(IStateStore store) {
		this.stateStore = store;
	}

    @Override
	public boolean isKeyAlwaysCaseSensitiveInRepositoryStructure(){
        return (Boolean) this.stateStore.retrievePreference(Preferences.CASE_SENSITIVE_REPOSITORY_STRUCTURE_KEYS, false);
    }

    @Override
	public void setKeyAlwaysCaseSensitiveInRepositoryStructure(boolean caseSensitiveRepositoryStructureKeys){
        this.stateStore.storePreference(Preferences.CASE_SENSITIVE_REPOSITORY_STRUCTURE_KEYS, caseSensitiveRepositoryStructureKeys);
    }
    
    @Override
	public int getAutoRepositoryRefreshTime() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.AUTO_REPOSITORY_REFRESH_TIME, 60);
    }

    @Override
	public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime) {
    	this.stateStore.storePreference(Preferences.AUTO_REPOSITORY_REFRESH_TIME, autoRepositoryRefreshTime);
    }
    

    @Override
	@SuppressWarnings("unchecked")
	public List<String> getLastRepositoryFolders() {
    	return (List<String>) this.stateStore.retrievePreference(Preferences.LAST_REPOSITORY_FOLDERS, null);
    }

    @Override
	public void setLastRepositoryFolders(List<String> lastRepositoryFolders) {
    	this.stateStore.storePreference(Preferences.LAST_REPOSITORY_FOLDERS, lastRepositoryFolders);
    }

    @Override
	public String getImportFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.IMPORT_FILENAME_PATTERN, null);
    }

    @Override
	public void setImportFileNamePattern(String importFileNamePattern) {
    	this.stateStore.storePreference(Preferences.IMPORT_FILENAME_PATTERN, importFileNamePattern);
    }

    @Override
	public String getImportFolderPathPattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.IMPORT_FOLDER_PATH_PATTERN, null);
    }

    @Override
	public void setImportFolderPathPattern(String importFolderPathPattern) {
    	this.stateStore.storePreference(Preferences.IMPORT_FOLDER_PATH_PATTERN, importFolderPathPattern);
    }

    @Override
	public String getExportFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.EXPORT_FILENAME_PATTERN, null);
    }

    @Override
	public void setExportFileNamePattern(String exportFileNamePattern) {
    	this.stateStore.storePreference(Preferences.EXPORT_FILENAME_PATTERN, exportFileNamePattern);
    }

    @Override
	public String getExportFolderPathPattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.EXPORT_FOLDER_PATH_PATTERN, null);
    }

    @Override
	public void setExportFolderPathPattern(String exportFolderPathPattern) {
    	this.stateStore.storePreference(Preferences.EXPORT_FOLDER_PATH_PATTERN, exportFolderPathPattern);
    }

    @Override
	public boolean isReviewTagsBeforeImport() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.REVIEW_TAGS_BEFORE_IMPORT, true);
    }

    @Override
	public void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport) {
    	this.stateStore.storePreference(Preferences.REVIEW_TAGS_BEFORE_IMPORT, reviewTagsBeforeImport);
    }
    
    @Override
	public boolean isApplyChangesToSourceFilesBeforeImport() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT, false);
    }

    @Override
	public void setApplyChangesToSourceFilesBeforeImport(boolean applyChangesToSourceFilesBeforeImport) {
    	this.stateStore.storePreference(Preferences.APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT, applyChangesToSourceFilesBeforeImport);
    }
    
    @Override
	public boolean isSetTrackNumbersWhenImporting() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SET_TRACK_NUMBERS_WHEN_IMPORTING, true);
    }

    @Override
	public void setSetTrackNumbersWhenImporting(boolean setTrackNumbersWhenImporting) {
    	this.stateStore.storePreference(Preferences.SET_TRACK_NUMBERS_WHEN_IMPORTING, setTrackNumbersWhenImporting);
    }
    
    @Override
	public boolean isSetTitlesWhenImporting() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SET_TITLES_WHEN_IMPORTING, true);
    }

    @Override
	public void setSetTitlesWhenImporting(boolean setTitlesWhenImporting) {
    	this.stateStore.storePreference(Preferences.SET_TITLES_WHEN_IMPORTING, setTitlesWhenImporting);
    }

    @Override
	@SuppressWarnings("unchecked")
	public List<String> getRecognitionPatterns() {
    	return (List<String>) this.stateStore.retrievePreference(Preferences.RECOGNITION_PATTERNS, null);
    }

    @Override
	public void setRecognitionPatterns(List<String> recognitionPatterns) {
    	this.stateStore.storePreference(Preferences.RECOGNITION_PATTERNS, recognitionPatterns);
    }
    
    @Override
	@SuppressWarnings("unchecked")
	public List<String> getMassiveRecognitionPatterns() {
    	return (List<String>) this.stateStore.retrievePreference(Preferences.MASSIVE_RECOGNITION_PATTERNS, null);
    }

    @Override
	public void setMassiveRecognitionPatterns(List<String> massiveRecognitionPatterns) {
    	this.stateStore.storePreference(Preferences.MASSIVE_RECOGNITION_PATTERNS, massiveRecognitionPatterns);
    }

    @Override
	public String getCommandBeforeAccessRepository() {
    	return (String) this.stateStore.retrievePreference(Preferences.COMMAND_BEFORE_ACCESS_REPOSITORY, null);
    }

    @Override
	public void setCommandBeforeAccessRepository(String commandBeforeAccessRepository) {
    	this.stateStore.storePreference(Preferences.COMMAND_BEFORE_ACCESS_REPOSITORY, commandBeforeAccessRepository);
    }
    
    @Override
	public String getCommandAfterAccessRepository() {
    	return (String) this.stateStore.retrievePreference(Preferences.COMMAND_AFTER_ACCESS_REPOSITORY, null);
    }

    @Override
	public void setCommandAfterAccessRepository(String commandAfterAccessRepository) {
    	this.stateStore.storePreference(Preferences.COMMAND_AFTER_ACCESS_REPOSITORY, commandAfterAccessRepository);
    }
}
