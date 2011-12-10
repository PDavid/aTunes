/*
 * aTunes 2.2.0-SNAPSHOT
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

import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.StringUtils;

public class SizeColumn extends AbstractColumn<String> {

    
    private static final long serialVersionUID = 6971729868799630776L;

    public SizeColumn() {
        super("SIZE");
        setWidth(100);
        setVisible(false);
        setAlignment(SwingConstants.CENTER);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        long l1 = 0;
        long l2 = 0;
        if (ao1 instanceof ILocalAudioObject) {
            l1 = ((ILocalAudioObject) ao1).getFile().length();
        }
        if (ao2 instanceof ILocalAudioObject) {
            l2 = ((ILocalAudioObject) ao2).getFile().length();
        }
        return Long.valueOf(l1).compareTo(l2);
    }

    @Override
    public String getValueFor(IAudioObject audioObject) {
        if (audioObject instanceof IRadio) {
            return "";
        }
        if (audioObject instanceof IPodcastFeedEntry) {
            return "";
        }
        return StringUtils.fromByteToMegaOrGiga(((ILocalAudioObject) audioObject).getFile().length());
    }

}
