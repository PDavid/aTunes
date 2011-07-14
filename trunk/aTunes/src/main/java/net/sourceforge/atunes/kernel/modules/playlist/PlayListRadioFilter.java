/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.AudioObject;

public class PlayListRadioFilter implements PlayListObjectFilter<Radio> {
	
	@Override
	public List<Radio> getObjects(PlayList playList) {
        List<Radio> result = new ArrayList<Radio>();
        for (AudioObject ao : playList.getAudioObjects()) {
            if (ao instanceof Radio) {
                result.add((Radio)ao);
            }
        }
        return result;
	}

}
