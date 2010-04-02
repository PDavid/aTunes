/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

public class BitrateColumn extends AbstractColumn {

    
    private static final long serialVersionUID = 7541146903350085592L;

    public BitrateColumn() {
        super("BITRATE", String.class);
        setWidth(100);
        setVisible(false);
        setAlignment(SwingConstants.CENTER);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return Long.valueOf(ao1.getBitrate()).compareTo(Long.valueOf(ao2.getBitrate()));
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        // Return bitrate
        if (audioObject.getBitrate() > 0) {
            return StringUtils.getString(Long.toString(audioObject.getBitrate()), " Kbps");
        }
        return "";
    }

}
