/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.model.AudioObject;

public class PlayingColumn extends Column {

    /**
     * 
     */
    private static final long serialVersionUID = -5604736587749167043L;

    public PlayingColumn() {
        super("PLAYING", Integer.class);
        setResizable(false);
        setWidth(16);
        setVisible(true);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return 0;
    }
    
    @Override
    public boolean isSortable() {
    	return false;
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        return 1;
    }

    @Override
    public String getHeaderText() {
        return "";
    }
    
    @Override
    public boolean isPlayListExclusive() {
    	return true;
    }

}
