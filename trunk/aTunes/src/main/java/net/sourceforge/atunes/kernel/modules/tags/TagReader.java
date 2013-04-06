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

package net.sourceforge.atunes.kernel.modules.tags;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectReader;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagAdapter;

import org.joda.time.base.BaseDateTime;

/**
 * Reads and validates the tag of an audio file
 * 
 * @author fleax
 */
public final class TagReader implements ILocalAudioObjectReader {

	private TagAdapterSelector tagAdapterSelector;

	/**
	 * @param tagAdapterSelector
	 */
	public void setTagAdapterSelector(
			final TagAdapterSelector tagAdapterSelector) {
		this.tagAdapterSelector = tagAdapterSelector;
	}

	@Override
	public void readAudioObject(final ILocalAudioObject ao,
			final boolean readAudioProperties) {
		// Try to read all data from one adapter
		ITagAdapter adapter = this.tagAdapterSelector.selectAdapter(ao);
		boolean adapterSuitableForRatingRead = this.tagAdapterSelector
				.isAdapterSuitableForRatingRead(adapter);
		adapter.readData(ao, adapterSuitableForRatingRead, readAudioProperties);

		// If adapter does not support current rating read/write preference
		// choose another
		if (!adapterSuitableForRatingRead) {
			this.tagAdapterSelector.selectAdapterForRating(ao).readRating(ao);
		}

		// Validate date to ensure it will be read and written properly in
		// metadata
		validateTag(ao.getTag());
	}

	private void validateTag(final ITag tag) {
		validateDate(tag);
	}

	private void validateDate(final ITag tag) {
		if (tag != null && tag.getDate() != null) {
			BaseDateTime date = tag.getDate();
			int year = date.getYear();
			int month = date.getMonthOfYear();
			int day = date.getDayOfMonth();
			boolean valid = true;
			if (year < 1000 || year > 9999) {
				valid = false;
			}
			if (month < 1 || month > 12) {
				valid = false;
			}
			if (day < 1 || day > 31) {
				valid = false;
			}
			if (!valid) {
				tag.setDate(null);
			}
		}
	}

	@Override
	public ImageIcon getImage(final ILocalAudioObject ao, final int width,
			final int height) {
		return this.tagAdapterSelector.selectAdapter(ao).getImage(ao, width,
				height);
	}
}
