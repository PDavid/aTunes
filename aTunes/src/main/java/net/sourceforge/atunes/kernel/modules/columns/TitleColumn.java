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

import net.sourceforge.atunes.model.IAudioObject;

public class TitleColumn extends AbstractColumn<String> {

 
    private static final long serialVersionUID = -4113331298039010230L;

    public TitleColumn() {
        super("TITLE");
        setVisible(true);
        setUsedForFilter(true);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        return ao1.getTitleOrFileName().compareTo(ao2.getTitleOrFileName());
    }

    @Override
    public String getValueFor(IAudioObject audioObject) {
        return audioObject.getTitleOrFileName();
    }

    @Override
    public String getValueForFilter(IAudioObject audioObject) {
        return audioObject.getTitle();
    }

}
