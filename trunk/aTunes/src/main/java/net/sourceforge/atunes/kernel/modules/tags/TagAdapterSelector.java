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

import java.util.List;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITagAdapter;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Selects tag adapter for a specific file type
 * 
 * @author alex
 * 
 */
public class TagAdapterSelector {

	private List<ITagAdapter> tagAdapters;

	/**
	 * @param tagAdapters
	 */
	public void setTagAdapters(List<ITagAdapter> tagAdapters) {
		this.tagAdapters = tagAdapters;
	}

	/**
	 * Selects adapter for given local audio object
	 * 
	 * @param audioObject
	 * @return
	 */
	ITagAdapter selectAdapter(final ILocalAudioObject audioObject) {
		for (ITagAdapter tagAdapter : tagAdapters) {
			if (tagAdapter.isFormatSupported(audioObject)) {
				return tagAdapter;
			}
		}
		throw new IllegalStateException(StringUtils.getString(
				"No tag adapter for audio object: ", audioObject.getUrl()));
	}
}
