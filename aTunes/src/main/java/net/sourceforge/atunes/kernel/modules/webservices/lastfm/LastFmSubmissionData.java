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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLSerializerService;

import org.apache.commons.io.FileUtils;

/**
 * Manages pending submission data
 * 
 * @author alex
 * 
 */
public class LastFmSubmissionData {

	private File submissionCacheDir;

	private IOSManager osManager;

	private XMLSerializerService xmlSerializerService;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param xmlSerializerService
	 */
	public void setXmlSerializerService(
			final XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}

	private synchronized File getSubmissionDataDir() throws IOException {
		if (this.submissionCacheDir == null) {
			this.submissionCacheDir = new File(StringUtils.getString(
					this.osManager.getUserConfigFolder(),
					this.osManager.getFileSeparator(), Constants.CACHE_DIR,
					this.osManager.getFileSeparator(),
					Constants.LAST_FM_CACHE_DIR,
					this.osManager.getFileSeparator(),
					Constants.LAST_FM_SUBMISSION_CACHE_DIR));
		}
		if (!this.submissionCacheDir.exists()) {
			FileUtils.forceMkdir(this.submissionCacheDir);
		}
		return this.submissionCacheDir;
	}

	private String getFileNameForSubmissionCache() throws IOException {
		return StringUtils.getString(net.sourceforge.atunes.utils.FileUtils
				.getPath(getSubmissionDataDir()), this.osManager
				.getFileSeparator(), "submissionDataCache.xml");
	}

	/**
	 * Adds submission (scrobbling) data
	 * 
	 * @param submissionData
	 */
	public synchronized void addSubmissionData(
			final SubmissionData submissionData) {
		List<SubmissionData> submissionDataList = getSubmissionData();
		submissionDataList.add(submissionData);
		Collections.sort(submissionDataList, new SubmissionDataComparator());
		try {
			String path = getFileNameForSubmissionCache();
			if (path != null) {
				this.xmlSerializerService.writeObjectToFile(submissionDataList,
						path);
				Logger.debug("Stored submission data: ", submissionData);
			}
		} catch (IOException e) {
			Logger.error(e);
		}

	}

	/**
	 * Returns submission data
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<SubmissionData> getSubmissionData() {
		List<SubmissionData> data = null;
		try {
			String path = getFileNameForSubmissionCache();
			if (path != null && new File(path).exists()) {
				data = (List<SubmissionData>) this.xmlSerializerService
						.readObjectFromFile(path);
			}
		} catch (IOException e) {
			Logger.error(e);
		}
		if (data == null) {
			data = new ArrayList<SubmissionData>();
		}
		return data;
	}

	/**
	 * Clears all submission data
	 */
	public synchronized void removeSubmissionData() {
		try {
			String path = getFileNameForSubmissionCache();
			if (path != null && new File(path).exists()) {
				this.xmlSerializerService.writeObjectToFile(
						new ArrayList<SubmissionData>(), path);
			}
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	private static class SubmissionDataComparator implements
			Comparator<SubmissionData>, Serializable {
		private static final long serialVersionUID = -4769284375865961129L;

		@Override
		public int compare(final SubmissionData o1, final SubmissionData o2) {
			return Integer.valueOf(o1.getStartTime()).compareTo(
					o2.getStartTime());
		}
	}

}
