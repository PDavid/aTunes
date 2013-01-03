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

package net.sourceforge.atunes.kernel.modules.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectDuplicateFinder;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Default comparator used to sort audio objects
 * 
 * @author fleax
 * 
 */
public class AudioObjectDuplicateFinder implements IAudioObjectDuplicateFinder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3725777266546165273L;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public List<IAudioObject> findDuplicates(final List<? extends IAudioObject> audioObjects) {
		if (CollectionUtils.isEmpty(audioObjects)) {
			return Collections.emptyList();
		}

		List<IAudioObject> duplicated = new ArrayList<IAudioObject>();
		Set<Integer> hashSet = new HashSet<Integer>();
		for (IAudioObject ao : audioObjects) {
			Integer hash = getHashForAudioObject(ao);
			if (hash != null) {
				if (hashSet.contains(hash)) {
					duplicated.add(ao);
				} else {
					hashSet.add(hash);
				}
			}
		}

		return duplicated;
	}

	private Integer getHashForAudioObject(final IAudioObject ao) {
		if (ao instanceof ILocalAudioObject) {
			return getHashForLocalAudioObject((ILocalAudioObject)ao);
		} else if (ao instanceof IRadio) {
			return getHashForRadio((IRadio)ao);
		} else if (ao instanceof IPodcastFeedEntry) {
			return getHashForPodcastFeedEntry((IPodcastFeedEntry)ao);
		} else {
			throw new IllegalArgumentException("Type not supported");
		}
	}

	private Integer getHashForPodcastFeedEntry(final IPodcastFeedEntry ao) {
		if (StringUtils.isEmpty(ao.getTitle()) || StringUtils.isEmpty(ao.getUrl())) {
			return null;
		}
		return "PODCAST".hashCode() + ao.getTitle().toLowerCase().hashCode() * ao.getUrl().toLowerCase().hashCode();
	}

	private Integer getHashForRadio(final IRadio ao) {
		if (StringUtils.isEmpty(ao.getUrl())) {
			return null;
		}
		return "RADIO".hashCode() + ao.getUrl().toLowerCase().hashCode();
	}

	private Integer getHashForLocalAudioObject(final ILocalAudioObject ao) {
		if (StringUtils.isEmpty(ao.getTitle()) || StringUtils.isEmpty(ao.getArtist(unknownObjectChecker)) || unknownObjectChecker.isUnknownArtist(ao.getArtist(unknownObjectChecker))) {
			return null;
		}
		return "LOCAL".hashCode() + ao.getTitle().toLowerCase().hashCode() * ao.getArtist(unknownObjectChecker).toLowerCase().hashCode();
	}
}
