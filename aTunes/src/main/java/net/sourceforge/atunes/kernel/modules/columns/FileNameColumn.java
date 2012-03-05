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
import net.sourceforge.atunes.model.ILocalAudioObject;

public class FileNameColumn extends AbstractColumn<String> {

    
    private static final long serialVersionUID = -6243616734204965925L;

    public FileNameColumn() {
        super("FILE");
        setWidth(250);
        setVisible(false);
        setUsedForFilter(true);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
    	if (ao1 instanceof ILocalAudioObject) {
    		return ((ILocalAudioObject) ao1).getFile().getName().compareTo(((ILocalAudioObject) ao2).getFile().getName());
    	}
    	return 0;
    }

    @Override
    public String getValueFor(IAudioObject audioObject) {
    	if (audioObject instanceof ILocalAudioObject) { 
    		return ((ILocalAudioObject) audioObject).getFile().getName();
    	}
    	return null;
    }

}
