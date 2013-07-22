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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateRepository implements IStateRepository {

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public boolean isKeyAlwaysCaseSensitiveInRepositoryStructure() {
		return this.preferenceHelper.getPreference(
				Preferences.CASE_SENSITIVE_REPOSITORY_STRUCTURE_KEYS,
				Boolean.class, false);
	}

	@Override
	public void setKeyAlwaysCaseSensitiveInRepositoryStructure(
			final boolean caseSensitiveRepositoryStructureKeys) {
		this.preferenceHelper.setPreference(
				Preferences.CASE_SENSITIVE_REPOSITORY_STRUCTURE_KEYS,
				caseSensitiveRepositoryStructureKeys);
	}

	@Override
	public int getAutoRepositoryRefreshTime() {
		// Disabled by default
		return this.preferenceHelper.getPreference(
				Preferences.AUTO_REPOSITORY_REFRESH_TIME, Integer.class, 0);
	}

	@Override
	public void setAutoRepositoryRefreshTime(final int autoRepositoryRefreshTime) {
		this.preferenceHelper.setPreference(
				Preferences.AUTO_REPOSITORY_REFRESH_TIME,
				autoRepositoryRefreshTime);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getLastRepositoryFolders() {
		return this.preferenceHelper.getPreference(
				Preferences.LAST_REPOSITORY_FOLDERS, List.class, null);
	}

	@Override
	public void setLastRepositoryFolders(
			final List<String> lastRepositoryFolders) {
		this.preferenceHelper.setPreference(
				Preferences.LAST_REPOSITORY_FOLDERS, lastRepositoryFolders);
	}

	@Override
	public String getImportFileNamePattern() {
		return this.preferenceHelper.getPreference(
				Preferences.IMPORT_FILENAME_PATTERN, String.class, null);
	}

	@Override
	public void setImportFileNamePattern(final String importFileNamePattern) {
		this.preferenceHelper.setPreference(
				Preferences.IMPORT_FILENAME_PATTERN, importFileNamePattern);
	}

	@Override
	public String getImportFolderPathPattern() {
		return this.preferenceHelper.getPreference(
				Preferences.IMPORT_FOLDER_PATH_PATTERN, String.class, null);
	}

	@Override
	public void setImportFolderPathPattern(final String importFolderPathPattern) {
		this.preferenceHelper
				.setPreference(Preferences.IMPORT_FOLDER_PATH_PATTERN,
						importFolderPathPattern);
	}

	@Override
	public String getExportFileNamePattern() {
		return this.preferenceHelper.getPreference(
				Preferences.EXPORT_FILENAME_PATTERN, String.class, null);
	}

	@Override
	public void setExportFileNamePattern(final String exportFileNamePattern) {
		this.preferenceHelper.setPreference(
				Preferences.EXPORT_FILENAME_PATTERN, exportFileNamePattern);
	}

	@Override
	public String getExportFolderPathPattern() {
		return this.preferenceHelper.getPreference(
				Preferences.EXPORT_FOLDER_PATH_PATTERN, String.class, null);
	}

	@Override
	public void setExportFolderPathPattern(final String exportFolderPathPattern) {
		this.preferenceHelper
				.setPreference(Preferences.EXPORT_FOLDER_PATH_PATTERN,
						exportFolderPathPattern);
	}

	@Override
	public boolean isReviewTagsBeforeImport() {
		return this.preferenceHelper.getPreference(
				Preferences.REVIEW_TAGS_BEFORE_IMPORT, Boolean.class, true);
	}

	@Override
	public void setReviewTagsBeforeImport(final boolean reviewTagsBeforeImport) {
		this.preferenceHelper.setPreference(
				Preferences.REVIEW_TAGS_BEFORE_IMPORT, reviewTagsBeforeImport);
	}

	@Override
	public boolean isApplyChangesToSourceFilesBeforeImport() {
		return this.preferenceHelper.getPreference(
				Preferences.APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT,
				Boolean.class, false);
	}

	@Override
	public void setApplyChangesToSourceFilesBeforeImport(
			final boolean applyChangesToSourceFilesBeforeImport) {
		this.preferenceHelper.setPreference(
				Preferences.APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT,
				applyChangesToSourceFilesBeforeImport);
	}

	@Override
	public boolean isSetTrackNumbersWhenImporting() {
		return this.preferenceHelper.getPreference(
				Preferences.SET_TRACK_NUMBERS_WHEN_IMPORTING, Boolean.class,
				true);
	}

	@Override
	public void setSetTrackNumbersWhenImporting(
			final boolean setTrackNumbersWhenImporting) {
		this.preferenceHelper.setPreference(
				Preferences.SET_TRACK_NUMBERS_WHEN_IMPORTING,
				setTrackNumbersWhenImporting);
	}

	@Override
	public boolean isSetTitlesWhenImporting() {
		return this.preferenceHelper.getPreference(
				Preferences.SET_TITLES_WHEN_IMPORTING, Boolean.class, true);
	}

	@Override
	public void setSetTitlesWhenImporting(final boolean setTitlesWhenImporting) {
		this.preferenceHelper.setPreference(
				Preferences.SET_TITLES_WHEN_IMPORTING, setTitlesWhenImporting);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getRecognitionPatterns() {
		return this.preferenceHelper.getPreference(
				Preferences.RECOGNITION_PATTERNS, List.class, null);
	}

	@Override
	public void setRecognitionPatterns(final List<String> recognitionPatterns) {
		this.preferenceHelper.setPreference(Preferences.RECOGNITION_PATTERNS,
				recognitionPatterns);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getMassiveRecognitionPatterns() {
		return this.preferenceHelper.getPreference(
				Preferences.MASSIVE_RECOGNITION_PATTERNS, List.class, null);
	}

	@Override
	public void setMassiveRecognitionPatterns(
			final List<String> massiveRecognitionPatterns) {
		this.preferenceHelper.setPreference(
				Preferences.MASSIVE_RECOGNITION_PATTERNS,
				massiveRecognitionPatterns);
	}

	@Override
	public String getCommandBeforeAccessRepository() {
		return this.preferenceHelper.getPreference(
				Preferences.COMMAND_BEFORE_ACCESS_REPOSITORY, String.class,
				null);
	}

	@Override
	public void setCommandBeforeAccessRepository(
			final String commandBeforeAccessRepository) {
		this.preferenceHelper.setPreference(
				Preferences.COMMAND_BEFORE_ACCESS_REPOSITORY,
				commandBeforeAccessRepository);
	}

	@Override
	public String getCommandAfterAccessRepository() {
		return this.preferenceHelper
				.getPreference(Preferences.COMMAND_AFTER_ACCESS_REPOSITORY,
						String.class, null);
	}

	@Override
	public void setCommandAfterAccessRepository(
			final String commandAfterAccessRepository) {
		this.preferenceHelper.setPreference(
				Preferences.COMMAND_AFTER_ACCESS_REPOSITORY,
				commandAfterAccessRepository);
	}

	@Override
	public boolean isStoreRatingInFile() {
		return this.preferenceHelper.getPreference(
				Preferences.STORE_RATING_IN_FILE, Boolean.class, false);
	}

	@Override
	public void setStoreRatingInFile(final boolean storeRatingInFile) {
		this.preferenceHelper.setPreference(Preferences.STORE_RATING_IN_FILE,
				storeRatingInFile);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
