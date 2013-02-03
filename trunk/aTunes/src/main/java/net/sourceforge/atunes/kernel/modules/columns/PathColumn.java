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

package net.sourceforge.atunes.kernel.modules.columns;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

/**
 * Column to show path
 * 
 * @author alex
 * 
 */
public class PathColumn extends AbstractColumn<String> {

	private static final long serialVersionUID = 2053462205073873545L;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * Default constructor
	 */
	public PathColumn() {
		super("LOCATION");
		setWidth(350);
		setVisible(false);
		setUsedForFilter(true);
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		String p1 = "";
		String p2 = "";
		if (ao1 instanceof ILocalAudioObject) {
			p1 = this.fileManager.getFolderPath((ILocalAudioObject) ao1);
		}
		if (ao2 instanceof ILocalAudioObject) {
			p2 = this.fileManager.getFolderPath((ILocalAudioObject) ao2);
		}
		return compare(p1, p2);
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return -ascendingCompare(ao1, ao2);
	}

	@Override
	public String getValueFor(final IAudioObject audioObject, final int row) {
		if (audioObject instanceof IRadio) {
			return ((IRadio) audioObject).getUrl();
		}
		if (audioObject instanceof IPodcastFeedEntry) {
			return ((IPodcastFeedEntry) audioObject).getUrl();
		}
		if (audioObject instanceof ILocalAudioObject) {
			return this.fileManager
					.getFolderPath((ILocalAudioObject) audioObject);
		}
		return null;
	}
}
