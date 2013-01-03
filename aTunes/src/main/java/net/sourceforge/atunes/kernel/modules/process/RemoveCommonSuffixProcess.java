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

package net.sourceforge.atunes.kernel.modules.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Process to remove common suffix from a list of files
 * 
 * @author alex
 * 
 */
public class RemoveCommonSuffixProcess extends AbstractChangeTagProcess {

	/** The files and titles. */
	private Map<ILocalAudioObject, String> filesAndTitles;

	@Override
	protected void retrieveInformationBeforeChangeTags() {
		if (this.filesAndTitles == null) {
			this.filesAndTitles = getTitlesForFiles(getFilesToChange());
		}
	}

	@Override
	protected void changeTag(final ILocalAudioObject file) {
		String newTitle = this.filesAndTitles.get(file);
		getTagHandler().setTitle(file, newTitle);
	}

	/**
	 * @param filesAndTitles
	 *            the filesAndTitles to set
	 */
	public void setFilesAndTitles(
			final Map<ILocalAudioObject, String> filesAndTitles) {
		this.filesAndTitles = filesAndTitles;
	}

	/**
	 * Returns a hash of files with its songs titles.
	 * 
	 * @param files
	 *            the files
	 * 
	 * @return the titles for files
	 */
	private Map<ILocalAudioObject, String> getTitlesForFiles(
			final Collection<ILocalAudioObject> files) {

		// First get the common suffix
		List<String> titles = new ArrayList<String>();
		for (ILocalAudioObject file : files) {
			titles.add(file.getTitleOrFileName());
		}
		String suffix = StringUtils.getCommonSuffix(titles
				.toArray(new String[titles.size()]));

		Map<ILocalAudioObject, String> result = new HashMap<ILocalAudioObject, String>();
		// For each file create the new title without suffix
		for (ILocalAudioObject f : files) {
			String title = org.apache.commons.lang.StringUtils.removeEnd(
					f.getTitleOrFileName(), suffix);
			if (title != null) {
				result.put(f, title);
			}
		}

		// Return files matched
		return result;
	}
}
