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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListObjectFilter;

/**
 * Selects local audio objects of a play list
 * 
 * @author alex
 * 
 */
public class PlayListLocalAudioObjectFilter implements
	IPlayListObjectFilter<ILocalAudioObject> {

    @Override
    public List<ILocalAudioObject> getObjects(final IPlayList playList) {
	List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
	for (int i = 0; i < playList.size(); i++) {
	    IAudioObject ao = playList.get(i);
	    if (ao instanceof ILocalAudioObject) {
		result.add((ILocalAudioObject) ao);
	    }
	}
	return result;
    }

}
