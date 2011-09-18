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

package net.sourceforge.atunes.kernel.modules.columns;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

public class PathColumn extends AbstractColumn {

    private static final long serialVersionUID = 2053462205073873545L;

    public PathColumn() {
        super("LOCATION", String.class);
        setWidth(350);
        setVisible(false);
        setUsedForFilter(true);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        String p1 = "";
        String p2 = "";
        if (ao1 instanceof AudioFile) {
            p1 = ((AudioFile) ao1).getFile().getParentFile().getAbsolutePath();
        }
        if (ao2 instanceof AudioFile) {
            p2 = ((AudioFile) ao2).getFile().getParentFile().getAbsolutePath();
        }
        return p1.compareTo(p2);
    }

    @Override
    public Object getValueFor(IAudioObject audioObject) {
        if (audioObject instanceof IRadio) {
            return ((Radio) audioObject).getUrl();
        }
        if (audioObject instanceof IPodcastFeedEntry) {
            return ((PodcastFeedEntry) audioObject).getUrl();
        }
        return ((AudioFile) audioObject).getFile().getParentFile().getAbsolutePath();
    }
}
